package controllers;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

import models.AppUser;
import models.Task;
import models.UserProjectStatus;
import models.chat.Attachment;
import models.chat.AttachmentType;
import models.chat.ChatAppUserLastSeenTabInfo;
import models.chat.ChatAppUserSettings;
import models.chat.ChatGroup;
import models.chat.ChatGroupAppUserInfo;
import models.chat.FileComment;
import models.chat.FileLike;
import models.chat.GitCommitComment;
import models.chat.GitIssue;
import models.chat.GitNotification;
import models.chat.GitNotificationType;
import models.chat.Message;
import models.chat.MessageAttachment;
import models.chat.MessageContentType;
import models.chat.Notification;
import models.chat.Role;
import models.chat.UploadFileInfo;
import models.leave.AppliedLeaves;
import models.leave.DateWiseAppliedLeaves;
import models.leave.LeaveStatus;

import org.apache.commons.io.IOUtils;

import play.Logger;
//import org.apache.commons.io.IOUtils;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.WebSocket;
import sun.misc.BASE64Decoder;
import action.BasicAuth;
import actor.ChatRoom;
import bean.GitNotificationBean;
import bean.GroupBean;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.SqlUpdate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ChatController extends Controller {
	public static final int MAX_ROWS = 20;

	public Form<GroupBean> groupForm = Form.form(GroupBean.class);

	@BasicAuth
	public Result getChat(String channelName) {
	//	Logger.info("getChat()  :: ");
		boolean isMatching=true;
		Map<String, String> channelMap=null;
		AppUser appUser = Application.getLoggedInUser();
		channelName = channelName !=null && !channelName.isEmpty() ? channelName.trim() : channelName;
		channelMap = getChannelName(channelName);
		String channel = channelMap.get("channelName");
		channel = channel.trim();
		isMatching=Boolean.parseBoolean(channelMap.get("isMatching"));
		
		if (appUser != null && isMatching) {
			return ok(views.html.chat.connect.render(appUser, null,channel));
		} else if(appUser != null && !isMatching) {
			return redirect(routes.ChatController.getChat(channel));
		}else{
			return ok("appuser is not found");
		}

	}
	@BasicAuth
	public Result getChatTab(){
		String channelName = getLastSeenChannelName();
		return redirect(routes.ChatController.getChat(channelName));
	}
	
	public synchronized Map<String,String> getChannelName(String channelName) {
		boolean isMatching=false;
		channelName = channelName !=null && !channelName.isEmpty() ? channelName.trim() : channelName;
		final AppUser loggedInUser = Application.getLoggedInUser();
		Map<String,String> channelStatusMap=new HashMap<String,String>();
		if(channelName != null && !channelName.isEmpty() && !channelName.equalsIgnoreCase("@"+loggedInUser.userName) && !channelName.equalsIgnoreCase("@github") ){
			
			if(channelName.startsWith("@") ){
				channelName=channelName.replace("@","").trim();
				List<AppUser> AppUserList = AppUser.find.where().eq("status", UserProjectStatus.Active).findList();
				for (AppUser appuser : AppUserList) {
					if(appuser.userName.equalsIgnoreCase(channelName)){
						//Logger.debug("user matched channelName updated.."+appuser.userName);
						updateLastSeenTabId(String.valueOf(appuser.id),"USER");
						isMatching=true;
						channelName="@"+appuser.userName.trim();
						break;
					}
				}
			}else{
				//Logger.debug("else block channel name is...................................:"+channelName);
				List<ChatGroup> groupList = ChatGroup.find.where().eq("isDisabled",false).findList();
				for (ChatGroup chatGroup : groupList) {
					if(chatGroup !=null && chatGroup.name.equalsIgnoreCase(channelName)){
						updateLastSeenTabId(String.valueOf(chatGroup.id),"GROUP");
						isMatching=true;
						channelName=chatGroup.name.trim();
					//	Logger.info("channelName"+channelName);
						break;
					}
				}
			}
			
			if(isMatching == false){
				//it means channelName not matching
				channelName=getLastSeenChannelName();
			}
		}else{
			//it means channelName is null
			channelName=getLastSeenChannelName();
		}
		channelStatusMap.put("channelName",channelName);
		channelStatusMap.put("isMatching",String.valueOf(isMatching));
		return channelStatusMap;
		
	}


	public static synchronized String  getLastSeenChannelName() {
		String channelName ="";
		final AppUser loggedInUser = Application.getLoggedInUser();
		ChatAppUserLastSeenTabInfo chatAppUser = ChatAppUserLastSeenTabInfo.find.where().eq("loggedInUser", loggedInUser).findUnique();

		if (chatAppUser != null && chatAppUser.lastSeenTab != null && chatAppUser.lastSeenTabRole != null && chatAppUser.lastSeenTabRole == Role.USER ) {
			AppUser appUser=AppUser.find.byId(chatAppUser.lastSeenTab);
			channelName = "@"+appUser.userName.trim();
		} else if (chatAppUser != null && chatAppUser.lastSeenTab != null && chatAppUser.lastSeenTabRole != null && chatAppUser.lastSeenTabRole == Role.GROUP) {
			ChatGroup group=ChatGroup.find.byId(chatAppUser.lastSeenTab);
			channelName=group.name.trim();
		} else {
			channelName = "General";
		}
		return channelName;
	}
	
	
	
	
	public Result chatRoomJs(final Long appUser) {
		// it returns loggedInUser and toUser
		// Logger.debug("in chat room js ------------------------------------");
		final AppUser loggedInUser = Application.getLoggedInUser();
		// Logger.debug("in chat room js
		// ------------------------------------"+loggedInUser);
		return ok(views.js.chat.render(loggedInUser));
	}

	public WebSocket<JsonNode> chatRoom(final Long loginAppUserId) {
		//Logger.debug("---entring chatRoom method -" + loginAppUserId);
		// Logger.debug(" client ID from Bean "+messageBean.clientId);
		// Long toAppUserId=Long.parseLong(messageBean.clientId);
		return new WebSocket<JsonNode>() {
			// Called when the Websocket Handshake is done.
			@Override
			public void onReady(final WebSocket.In<JsonNode> in, final WebSocket.Out<JsonNode> out) {

				// Join the chat room.
				try {
					// Logger.debug("---join method calling
					// -"+loginAppUserId+"==========");
					ChatRoom.join(loginAppUserId, in, out);

				} catch (final Exception ex) {
					ex.printStackTrace();
				}
			}
		};
	}

	public Result isLogin() {
		Map<String, String> jsonMap = new HashMap<String, String>();
		final AppUser loggedInUser = Application.getLoggedInUser();
		if (loggedInUser != null) {
			jsonMap.put("isLogin", "true");
		} else {
			jsonMap.put("isLogin", "false");
		}

		return ok(Json.toJson(jsonMap));
	}
	public  synchronized Result  getLastSeenChannelMessages() {
		final ObjectNode channelsJsonMap = Json.newObject();
		String channelName = getLastSeenChannelName();
		 JsonNode toChannelJsonMap =null;
		if(channelName.contains("@")){
			channelName=channelName.replace("@","").trim();
			List<AppUser> appUserList = AppUser.find.where().eq("userName",channelName).eq("status", UserProjectStatus.Active).findList();
			 AppUser appUser =null;
			
			if(appUserList !=null && !appUserList.isEmpty() && appUserList.size() >=1){
				appUser= appUserList.get(0);
				toChannelJsonMap = getJsonToUserMessages(appUser.id, -1);
			}
		    channelsJsonMap.set(appUser.userName, toChannelJsonMap);
		}else{
			ChatGroup chatGroup =null;
			List<ChatGroup> chatGroupList= ChatGroup.find.where().eq("name",channelName).findList();
			if(chatGroupList !=null && !chatGroupList.isEmpty() && chatGroupList.size() >=1){
				chatGroup= chatGroupList.get(0);
				toChannelJsonMap = getJsonGroupMessages(chatGroup.id, -1);
				channelsJsonMap.set(chatGroup.name,toChannelJsonMap);
			}
		}
		return ok(channelsJsonMap);
	}

	@BasicAuth
	public synchronized Result messagesPage(final Long appUserToId, int firstRow) {
		JsonNode toAppUserJsonMap = getJsonToUserMessages(appUserToId, firstRow);
		return ok(toAppUserJsonMap);
	}

	@BasicAuth
	public synchronized JsonNode getJsonToUserMessages(final Long appUserToId, int firstRow) {
		// Logger.debug("messagesPage----------------------->"+appUserToId);
		final AppUser loggedInUser = Application.getLoggedInUser();

		/*
		 * select * from message where message_by_id=1 and message_to_id=6 or
		 * message_by_id=6 and message_to_id=1
		 */
		// Message.find.where().eq(Expr.or(Expr.and(Expr.eq("messageTo.id",loggedInUser.id),Expr.eq("messageBy.id",
		// appUserToId)),Expr.and(Expr.and(Expr.eq("messageTo.id",appUserToId),Expr.eq("messageBy.id",loggedInUser.id)))));

		final ObjectNode JsonDateWiseMessageMap = Json.newObject();
		final ExpressionList<Message> messageExpList = Message.find.where().or(Expr.eq("messageTo.id", loggedInUser.id),
				Expr.eq("messageBy.id", loggedInUser.id));
		if (appUserToId != null) {
			messageExpList.or(Expr.eq("messageTo.id", appUserToId), Expr.eq("messageBy.id", appUserToId))
					.order("createdOn");
		}
		Query<Message> messageQuery = messageExpList.query();
		int totalMessageCount = messageQuery.findRowCount();

		boolean isLastRow = false;
		List<Message> messageList = new ArrayList<Message>();
		if (totalMessageCount > MAX_ROWS) {
			int startIndex = 0;
			if (firstRow == -1) {
				startIndex = totalMessageCount - MAX_ROWS;
				firstRow = startIndex;
				if (totalMessageCount <= MAX_ROWS) {
					isLastRow = true;
				}
				messageList = messageQuery.setFirstRow(startIndex).setMaxRows(MAX_ROWS).findList();
			} else {
				if (firstRow <= MAX_ROWS) {
					messageList = messageQuery.setFirstRow(0).setMaxRows(firstRow).findList();
					firstRow = 0;
					isLastRow = true;
				} else {
					startIndex = firstRow - MAX_ROWS;
					firstRow = firstRow - MAX_ROWS;
					isLastRow = false;
					messageList = messageQuery.setFirstRow(startIndex).setMaxRows(MAX_ROWS).findList();
				}
			}
		} else if (totalMessageCount <= MAX_ROWS) {
			messageList = messageQuery.setFirstRow(0).setMaxRows(totalMessageCount).findList();
			isLastRow = true;
			firstRow = MAX_ROWS - totalMessageCount;
		}

		JsonDateWiseMessageMap.put("isLastRow", isLastRow);
		JsonDateWiseMessageMap.put("firstRow", firstRow);

		String lastUnReadRandomId = null;
		final List<Message> unReadaMessageList = Message.find.where().eq("messageBy.id", appUserToId).eq("messageTo.id", loggedInUser.id).eq("isViewd", false).order("createdOn").findList();

		if (unReadaMessageList != null && unReadaMessageList.size() >= 1) {
			lastUnReadRandomId = unReadaMessageList.get(0).randomId;
		}
		JsonDateWiseMessageMap.set("messagePage", getJsonDateWiseMessageMap(messageList));
		JsonDateWiseMessageMap.put("lastUnReadRandomId", lastUnReadRandomId != null ? lastUnReadRandomId : "");
		//Logger.debug("lastUnReadaMessageId" + lastUnReadRandomId);

		return JsonDateWiseMessageMap;
	}

	public static  Message convertJsonMessage(Message message,Boolean isReceiver) {
		GitNotification gitNotification = null;
		UploadFileInfo uploadFileInfo = null;
		MessageContentType mc=message.messageContentType;
		message.description = message.description.replaceAll("\n", "<br>");
		
		if (mc== MessageContentType.IMAGE || mc == MessageContentType.FILE || mc==MessageContentType.SNIPPET) {
			List<UploadFileInfo>  uploadFileInfoList =UploadFileInfo.find.where().eq("message",message).findList();
			uploadFileInfo = uploadFileInfoList !=null && uploadFileInfoList.size() >= 1 ? uploadFileInfoList.get(0) : null   ;
			if(mc==MessageContentType.SNIPPET){
				uploadFileInfo=getSnippetFirstTime(uploadFileInfo);
			}else {
				if(!isReceiver){
					uploadFileInfo=getLikesAndCommentInfo(uploadFileInfo);
				}else{
					uploadFileInfo=getLikesAndCommentInfoToReceiver(uploadFileInfo);
				}
			}
			message.uploadFile=uploadFileInfo != null ? uploadFileInfo : null;
		}else if(mc==MessageContentType.COMMENT){
			FileComment  fileComment=FileComment.find.where().eq("message",message).findUnique();
			uploadFileInfo = fileComment.uploadFileInfo;
			String channelName =getFileBYUserName(uploadFileInfo);
			if(!isReceiver){
				uploadFileInfo.snippetMap.put("byUserName",channelName);
			}else{
				uploadFileInfo=getLikesAndCommentInfoToReceiver(uploadFileInfo);
				uploadFileInfo.snippetMap.put("byUserName",channelName);
			}
			if(uploadFileInfo.commentList !=null && !uploadFileInfo.commentList.isEmpty() && uploadFileInfo.commentList.size() >= 1){
				Query<FileComment> fileCommentQuery = FileComment.find.where().eq("uploadFileInfo",uploadFileInfo).order("createdOn desc").setMaxRows(1);
				FileComment lastComment =  fileCommentQuery != null ? fileCommentQuery.findUnique():null;
				uploadFileInfo.commentList.clear();
				uploadFileInfo.commentList.add(lastComment);
				
			}
			
			message.uploadFile= uploadFileInfo != null ? uploadFileInfo : null;
		}else if (mc == MessageContentType.GITNOTIFICATION) {
			gitNotification = GitNotification.find.where().eq("message.id", message.id).findUnique();
			if (gitNotification != null) {
				if (gitNotification.gitNotificationType == GitNotificationType.PUSH
						|| gitNotification.gitNotificationType == GitNotificationType.CREATEBRANCH
						|| gitNotification.gitNotificationType == GitNotificationType.DELETEBRANCH) {
					message.gitNotification = gitNotification;
				} else if (gitNotification.gitNotificationType == GitNotificationType.ISSUES) {
					GitIssue gitIssue = GitIssue.find.where().eq("gitNotificationId", gitNotification).findUnique();
					List<UploadFileInfo> uploadFileInfoList = UploadFileInfo.find.where().eq("gitIssue", gitIssue).findList();
					gitNotification.gitIssue = gitIssue;
					message.gitNotification = gitNotification;
					if (uploadFileInfoList != null) {
						gitIssue.uploadFileInfoList = uploadFileInfoList;
					}
				} else if (gitNotification.gitNotificationType == GitNotificationType.COMMITCOMMENT) {
					GitCommitComment gitComment = GitCommitComment.find.where().eq("gitNotificationId", gitNotification)
							.findUnique();
					gitComment.commitId = gitComment.commitId.substring(Math.max(0, gitComment.commitId.length() - 7));
					gitNotification.gitCommitComment = gitComment;
					message.gitNotification = gitNotification;
				}

			}
		}
		if (message.isAttachment) {
			List<MessageAttachment> messageAttachmentList = MessageAttachment.find.where().eq("message", message).findList();
			for (MessageAttachment messageAttachment : messageAttachmentList) {
				message.attachmentList.add(messageAttachment.attachment);
			}
		}
		
		return message;
	}

	
	public static String getFileBYUserName(UploadFileInfo uploadFileInfo){
		Message message = uploadFileInfo.message;
		String byUserName = "";
		if (message != null) {
				AppUser appUser = message.messageBy;
				//Logger.debug("appUser userName......."+appUser.getUserName().trim());
				byUserName = appUser != null && appUser.getUserName() != null  ? appUser.getUserName().trim() : "";
			}
		
			return byUserName;
		}
	private static UploadFileInfo getLikesAndCommentInfoToReceiver(UploadFileInfo uploadFileInfo) {
		Map<Object,Object>  map= uploadFileInfo.snippetMap;
		map.put("likesCount",String.valueOf(uploadFileInfo.likeList.size()));
		map.put("commentsCount",String.valueOf(uploadFileInfo.commentList.size()));
		uploadFileInfo.snippetMap = map;
		return uploadFileInfo;
	}
	private static UploadFileInfo getLikesAndCommentInfo(UploadFileInfo uploadFileInfo) {
		Map<Object,Object>  map= new HashMap<>();
		if(uploadFileInfo != null){
		map.put("likesCount",String.valueOf(uploadFileInfo.likeList.size()));
		map.put("isLiked",isLiked(uploadFileInfo));
		map.put("commentsCount",String.valueOf(uploadFileInfo.commentList.size()));
		FileLike fileLike = getLastLiked(uploadFileInfo);
		map.put("lastLiked",fileLike);
		uploadFileInfo.snippetMap = map;
		}
		return uploadFileInfo;
	}
	
	/*private static String getFileName(Message message) {
		List<UploadFileInfo> uploadFileInfoList=UploadFileInfo.find.where().eq("message",message).findList();
		return (uploadFileInfoList !=null && uploadFileInfoList.size()>=1 ) ? uploadFileInfoList.get(0).uploadFileName : "";
	}*/

	public static UploadFileInfo getSnippetFirstTime(UploadFileInfo uploadFileInfo) {
		StringBuilder result=new StringBuilder();
		if(uploadFileInfo.uploadImage !=null && uploadFileInfo.uploadImage.length > 0 ){
			String snippet=new String(uploadFileInfo.uploadImage);
		    String[] lines =snippet.split(System.getProperty("line.separator"));
		    uploadFileInfo.snippetMap.put("noOfLines",String.valueOf(lines.length));
		    if(lines.length<=5){
		    	for (String line: lines) {
		    		 result.append(line);
				}
		    }else{
		    	result.append(lines[0]).append(lines[1]).append(lines[2]).append(lines[3]).append(lines[5]);
		    }	
		}
	    uploadFileInfo.snippetMap.put("snippetCode",result.toString());
		return uploadFileInfo;
	}
	
	

	public GitNotification getjsonGitIssueDetails(GitNotification gitNotification) {
	//	GitIssue gitIssue = GitIssue.find.where().eq("gitNotificationId", gitNotification).findUnique();

		//List<UploadFileInfo> uploadFiles = UploadFileInfo.find.where().eq("gitIssue", gitIssue).findList();
		return gitNotification;

	}

	public static String convertTimestampToSpecificFormat(Timestamp timestamp) {
		String date = new SimpleDateFormat("hh:mm a").format(timestamp);
		// Logger.debug(date+"");
		return date;
	}

	@BasicAuth
	public Result updateLastSeenTabId(String lastSeenTabId, String role) {

		//Logger.debug("updateLastSeenTabId():chatController------------------------------------------------------------------------------------"+ lastSeenTabId + "role :" + role);
		final AppUser loggedInUser = Application.getLoggedInUser();
		ChatAppUserLastSeenTabInfo chatAppUser = ChatAppUserLastSeenTabInfo.find.where()
				.eq("loggedInUser", loggedInUser).findUnique();
		// chatAppUser notnull means already chatAppUser lastSeenId existed
		if (chatAppUser != null) {
			if (lastSeenTabId != null && role != null && !(lastSeenTabId.equalsIgnoreCase("null"))
					&& !(role.equalsIgnoreCase("null"))) {
				chatAppUser.lastSeenTab = Long.parseLong(lastSeenTabId);
				if (Role.USER.toString().equalsIgnoreCase(role)) {
					chatAppUser.lastSeenTabRole = Role.USER;
				} else {
					chatAppUser.lastSeenTabRole = Role.GROUP;
				}
			} else {
				// Logger.debug("updateLastSeenTabId else method");
				chatAppUser.lastSeenTab = null;
				chatAppUser.lastSeenTabRole = null;

			}
			chatAppUser.update();
		} else {
			ChatAppUserLastSeenTabInfo newChatAppUser = new ChatAppUserLastSeenTabInfo();
			newChatAppUser.loggedInUser = loggedInUser;
			newChatAppUser.lastSeenTab = Long.parseLong(lastSeenTabId);
			if (role.equalsIgnoreCase("user")) {
				newChatAppUser.lastSeenTabRole = Role.USER;
			} else if (role.equalsIgnoreCase("group")) {
				newChatAppUser.lastSeenTabRole = Role.GROUP;
			}

			newChatAppUser.save();
		}
		return ok();

	}

	@BasicAuth
	public Result getLastSeenTabIdAtFirstTime() {
		final AppUser loggedInUser = Application.getLoggedInUser();
		// Logger.debug("getLastSeenTabIdAtFirstTime-----------------------------------------------"+loggedInUser.id);
		ChatAppUserLastSeenTabInfo chatAppUser = ChatAppUserLastSeenTabInfo.find.where()
				.eq("loggedInUser", loggedInUser).findUnique();
		Map<Object, Object> jsonMap = new HashMap<Object, Object>();
		if (chatAppUser != null) {
			jsonMap.put("lastSeenTab", chatAppUser.lastSeenTab);
			jsonMap.put("role", chatAppUser.lastSeenTabRole.toString());
		} else {
			jsonMap.put("lastSeenTab", "firstTime");
			jsonMap.put("role", "firstTime");
		}
		return ok(Json.toJson(jsonMap));

	}

	
	/*@BasicAuth
	public Result getLastSeenTabId() {
		// Logger.debug("getLastSeenTabId():ChatController");
		Map<Object, Object> jsonMap = new HashMap<Object, Object>();
		final AppUser loggedInUser = Application.getLoggedInUser();
		ChatAppUserLastSeenTabInfo chatAppUser = ChatAppUserLastSeenTabInfo.find.where()
				.eq("loggedInUser", loggedInUser).findUnique();
		// Logger.debug("getLastSeenTabId"+chatAppUser);
		// Logger.debug("getLastSeenTabId"+chatAppUser);

		// messages
		if (chatAppUser != null && chatAppUser.lastSeenTab != null && chatAppUser.lastSeenTabRole != null
				&& "USER".equalsIgnoreCase(chatAppUser.lastSeenTabRole.toString())) {
			List<Message> unReadaMessageList = null;
			Long appUserToId = chatAppUser.lastSeenTab;
			// Logger.debug("appUserToId :"+appUserToId+"loggedInUser
			// :"+loggedInUser);
			final ExpressionList<Message> messageExpList = Message.find.where()
					.or(Expr.eq("messageTo.id", loggedInUser.id), Expr.eq("messageBy.id", loggedInUser.id));
			if (appUserToId != null) {
				messageExpList.or(Expr.eq("messageTo.id", appUserToId), Expr.eq("messageBy.id", appUserToId))
						.order("createdOn");
				unReadaMessageList = Message.find.where().eq("messageBy.id", appUserToId)
						.eq("messageTo.id", loggedInUser.id).eq("isViewd", false).order("createdOn").findList();
			}

			Query<Message> messageQuery = messageExpList.query();
			List<Message> messageList = new ArrayList<Message>();
			Integer totalMessageCount = messageQuery.findRowCount();

			for (Message message : messageList) {
				message.isViewd = true;
				message.update();
			}

			if (totalMessageCount > MAX_ROWS) {
				int startIndex = totalMessageCount - MAX_ROWS;
				jsonMap.put("firstRow", startIndex);
				jsonMap.put("isLastRow", false);
				// messageList=messageQuery.setFirstRow(startIndex).setMaxRows(MAX_ROWS).findList();
			} else if (totalMessageCount <= MAX_ROWS) {
				messageList = messageQuery.setFirstRow(0).setMaxRows(totalMessageCount).findList();
				jsonMap.put("isLastRow", true);
				jsonMap.put("firstRow", MAX_ROWS - totalMessageCount);
			}

			JsonNode JsonDateWiseMessageMap = getJsonDateWiseMessageMap(messageList);

			String lastUnReadRandomId = null;
			if (unReadaMessageList != null && unReadaMessageList.size() >= 1) {
				lastUnReadRandomId = unReadaMessageList.get(0).randomId;
			}
			jsonMap.put("lastSeenTab", chatAppUser.lastSeenTab);
			jsonMap.put("role", chatAppUser.lastSeenTabRole.toString());
			// jsonMap.put("messagePage",JsonDateWiseMessageMap);
		} else if (chatAppUser != null && chatAppUser.lastSeenTabRole != null
				&& "GROUP".equalsIgnoreCase(chatAppUser.lastSeenTabRole.toString())) {
			List<Message> unReadaGroupMessageList = new ArrayList<Message>();
			Query<Message> messageQuery = Message.find.where()
					.eq("chatGroup", ChatGroup.find.byId(chatAppUser.lastSeenTab)).eq("role", Role.GROUP)
					.order("createdOn");

			List<Message> messageList = new ArrayList<Message>();
			Integer totalMessageCount = messageQuery.findRowCount();

			if (totalMessageCount > MAX_ROWS) {
				int startIndex = totalMessageCount - MAX_ROWS;
				jsonMap.put("firstRow", startIndex);
				jsonMap.put("isLastRow", false);
				// messageList=messageQuery.setFirstRow(startIndex).setMaxRows(MAX_ROWS).findList();
			} else if (totalMessageCount <= MAX_ROWS) {
				messageList = messageQuery.setFirstRow(0).setMaxRows(totalMessageCount).findList();
				jsonMap.put("isLastRow", true);
				jsonMap.put("firstRow", MAX_ROWS - totalMessageCount);
			}

			List<Notification> notificationList = Notification.find.where().eq("messageTo", loggedInUser)
					.eq("toChatGroup", ChatGroup.find.byId(chatAppUser.lastSeenTab)).eq("role", Role.GROUP)
					.eq("isViewed", false).order("createdOn").findList();
			for (Notification notification : notificationList) {
				notification.isViewed = true;
				notification.update();
				unReadaGroupMessageList.add(notification.message);
			}
			JsonNode JsonDateWiseMessageMap = getJsonDateWiseMessageMap(messageList);

			String lastUnReadRandomId = null;
			if (unReadaGroupMessageList != null && unReadaGroupMessageList.size() >= 1) {
				// lastUnReadRandomId =unReadaGroupMessageList.get(0).randomId;
			}
			jsonMap.put("lastSeenTab", chatAppUser.lastSeenTab);
			jsonMap.put("role", chatAppUser.lastSeenTabRole.toString());
			// jsonMap.put("messagePage",Json.toJson(JsonDateWiseMessageMap));

		} else {
			jsonMap.put("lastSeenTab", "firstTime");
			jsonMap.put("role", "firstTime");
		}
		return ok(Json.toJson(jsonMap));

	}*/
	@BasicAuth
	public Result getLastSeenTabId() {
		// Logger.debug("getLastSeenTabId():ChatController");
		Map<Object, Object> jsonMap = new HashMap<Object, Object>();
		final AppUser loggedInUser = Application.getLoggedInUser();
		final ObjectNode channelsJsonMap = Json.newObject();
		ChatAppUserLastSeenTabInfo chatAppUser = ChatAppUserLastSeenTabInfo.find.where().eq("loggedInUser", loggedInUser).findUnique();
		
		if (chatAppUser != null && chatAppUser.lastSeenTab != null && chatAppUser.lastSeenTabRole != null && "USER".equalsIgnoreCase(chatAppUser.lastSeenTabRole.toString())) {
			AppUser appUser = AppUser.find.byId(chatAppUser.lastSeenTab);
			if(appUser !=null){
				JsonNode toappUserJsonMap = getJsonToUserMessages(appUser.id, -1);
				channelsJsonMap.set(appUser.userName, toappUserJsonMap);
			}
			jsonMap.put("channelData",channelsJsonMap);
			jsonMap.put("lastSeenTab", chatAppUser.lastSeenTab);
			jsonMap.put("role", chatAppUser.lastSeenTabRole.toString());
		} else if (chatAppUser != null && chatAppUser.lastSeenTabRole != null && "GROUP".equalsIgnoreCase(chatAppUser.lastSeenTabRole.toString())) {
			ChatGroup chatGroup=ChatGroup.find.byId(chatAppUser.lastSeenTab);
			if(chatGroup !=null){
					JsonNode groupJsonMap = getJsonGroupMessages(chatGroup.id, -1);
					channelsJsonMap.set(chatGroup.name, groupJsonMap);
			 }
			jsonMap.put("lastSeenTab", chatAppUser.lastSeenTab);
			jsonMap.put("role", chatAppUser.lastSeenTabRole.toString());
			jsonMap.put("channelData",channelsJsonMap);

		} else {
			JsonNode groupJsonMap = getJsonGroupMessages(getGeneralGroup().id, -1);
			channelsJsonMap.set(getGeneralGroup().name, groupJsonMap);
			jsonMap.put("lastSeenTab", getGeneralGroup().id);
			jsonMap.put("role","GROUP");
		}
		return ok(Json.toJson(jsonMap));

	}

	@BasicAuth
	public Result createGroup() {
		return ok(views.html.chat.createGroup.render());
	}

	public synchronized static Integer messageCount(Long toUserId) {
		AppUser toAppuser = AppUser.find.byId(toUserId);
		final AppUser loggedInUser = Application.getLoggedInUser();
		Integer messageCount = Message.find.where().eq("messageTo", loggedInUser).eq("messageBy", toAppuser)
				.eq("isViewd", false).findList().size();
		// Logger.debug(messageCount+"messageCount at loading time");
		return messageCount;
	}

	public static String convertMessageDateToSecficFormate(Date mapDate) {
		String dayWiseDate = null;
		Date date = getDateWithoutTime(mapDate);
		Date toadyDate = getDateWithoutTime(new Date());

		Date messageYesterdayDate = getDateWithoutTime(mapDate);
		Date yesterdayDate = getDateWithoutTime(getYesterdayDate(new Date()));

		// Logger.debug(messageYesterdayDate+"-----------------------------------------------"+yesterdayDate);

		if (date.compareTo(toadyDate) == 0) {
			dayWiseDate = "Today";
		} else if (messageYesterdayDate.compareTo(yesterdayDate) == 0) {
			dayWiseDate = "Yesterday";
		} else {
			dayWiseDate = new SimpleDateFormat("dd-MMM-yyyy").format(date);
		}

		return dayWiseDate;
	}

	public static Date getDateWithoutTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static Date getYesterdayDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, -1);
		return cal.getTime();
	}

	@BasicAuth
	public Result changeMessageViewStatus(String messageId) {
		// Logger.debug(Long.parseLong(messageId)+"ChatController
		// changeMessageViewStatus()");
		Message message = Message.find.byId(Long.parseLong(messageId));
		message.isViewd = true;
		message.update();
		return ok();
	}

	@BasicAuth
	public Result getMessageNotificationCount(Long toUserId) {
		List<Integer> jsonMessageNotificationList = new ArrayList<Integer>();
		AppUser toAppuser = AppUser.find.byId(toUserId);
		final AppUser loggedInUser = Application.getLoggedInUser();
		Integer messageCount = Message.find.where().eq("messageTo", loggedInUser).eq("messageBy", toAppuser)
				.eq("isViewd", false).findList().size();
		jsonMessageNotificationList.add(messageCount);
		return ok(Json.toJson(jsonMessageNotificationList));

	}

	@BasicAuth
	public Result changeMessageNotification(String toAppsuerId) {
		// Logger.debug("changeMessageNotification in
		// chatcontroller"+toAppsuerId);
		List<Long> jsonUnreadMessageList = new ArrayList<Long>();
		final AppUser loggedInUser = Application.getLoggedInUser();
		final List<Message> messageList = Message.find.where().eq("messageBy.id", Long.parseLong(toAppsuerId))
				.eq("messageTo.id", loggedInUser.id).eq("isViewd", false).findList();
		// Logger.debug("Message List : -------------->"+messageList.size());
		if (messageList != null) {
			for (Message message : messageList) {
				// jsonUnreadMessageList.add(message.id);
				message.isViewd = true;
				message.update();
			}
		}
		return ok(Json.toJson(jsonUnreadMessageList));
	}

	@BasicAuth
	public synchronized Result findOnlineAppuserList() {
		// Logger.debug("findOnlineAppuserList():ChatController");
		Set<Long> onlineIdsSet = new HashSet<Long>();
		Set<Long> offlineIdsSet = new HashSet<Long>();
		Set<Long> websocketOnlineUsers = ChatRoom.members.keySet();
		final AppUser loggedInUser = Application.getLoggedInUser();
		for (Long long1 : websocketOnlineUsers) {
			onlineIdsSet.add(long1);
		}
		// onlineIdsSet.add(loggedInUser.id);
		// Logger.debug(onlineIdsSet+"onlineIdsList");

		Map<String, Object> onlineAppuserIdMap = new HashMap<String, Object>();
		List<AppUser> AppUserList = AppUser.find.where().eq("status", UserProjectStatus.Active).findList();
		for (AppUser appuser : AppUserList) {
			offlineIdsSet.add(appuser.id);
		}
		offlineIdsSet.removeAll(onlineIdsSet);

		onlineAppuserIdMap.put("onlineIdsList", onlineIdsSet);
		onlineAppuserIdMap.put("offlineIdsList", offlineIdsSet);
		// Logger.debug(onlineAppuserIdMap+"onlineAppuserIdMap");

		return ok(Json.toJson(onlineAppuserIdMap));

	}

	@BasicAuth
	public Result saveChatGroup() {
		AppUser appUser = Application.getLoggedInUser();

		Form<GroupBean> filledgroupForm = groupForm.bindFromRequest();
		//System.out.println("grooup name "+filledgroupForm.toString());
		GroupBean groupBean = filledgroupForm.get();
		//System.out.println("grooup bean  "+groupBean.toString());
		models.chat.ChatGroup group = groupBean.toEntity();
		//group.save();
		Map<Object, Object> jsonMap = new HashMap<Object, Object>();
		jsonMap.put("status", true);
		if (groupBean.members != null && groupBean.members.size() > 0) {
			groupBean.sendGroupNotification(group);
		}

		ChatAppUserLastSeenTabInfo chatAppUser = ChatAppUserLastSeenTabInfo.find.where().eq("loggedInUser", appUser)
				.findUnique();
		if (chatAppUser != null) {
			chatAppUser.lastSeenTab = group.id;
			chatAppUser.lastSeenTabRole = Role.GROUP;
			chatAppUser.update();
		} else {
			ChatAppUserLastSeenTabInfo newChatAppUser = new ChatAppUserLastSeenTabInfo();
			newChatAppUser.loggedInUser = appUser;
			newChatAppUser.lastSeenTab = group.id;

			newChatAppUser.lastSeenTabRole = Role.GROUP;
			newChatAppUser.save();
		}
		if (appUser != null) {
		// return redirect(routes.ChatController.getChat("general"));
			return ok(Json.toJson(jsonMap));
		} else {
			return ok("appuser is not exixsted.....");
		}
	}

	public static Integer getGroupMessageNotificationCountAt(Long groupId) {
		AppUser appUser = Application.getLoggedInUser();
		Integer notificationCount = Notification.find.where().eq("messageTo", appUser)
				.eq("toChatGroup", ChatGroup.find.byId(groupId)).eq("isViewed", false).findList().size();
		return notificationCount;

	}

	@BasicAuth
	public Result isValidGroupMember(String groupId) {
		// Logger.debug("isValidGroupMember():chatController"+groupId);
		ChatGroup group = null;
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		List<Long> appUserIdList = new ArrayList<Long>();
		final AppUser loggedInUser = Application.getLoggedInUser();

		if (groupId != null && groupId != "undefined") {
			group = ChatGroup.find.byId(Long.parseLong(groupId));
			jsonMap.put("groupName", group.name);
			if (group != null && group.name.equalsIgnoreCase("general")) {
				jsonMap.put("isGeneralGroup", true);
			}
		}

		List<AppUser> appUserList = new ArrayList<AppUser>();
		List<ChatGroupAppUserInfo> groupMembers = ChatGroupAppUserInfo.find.where().eq("chatGroup", group).findList();
		for (ChatGroupAppUserInfo chatGroupAppUserInfo : groupMembers) {
			appUserList.add(chatGroupAppUserInfo.appUser);
		}
		for (AppUser appUser : appUserList) {
			if (loggedInUser.id != appUser.id) {
				appUserIdList.add(appUser.id);
			}

		}
		jsonMap.put("appUserIdList", appUserIdList);
		jsonMap.put("appUserId", loggedInUser.id);
		for (AppUser appUser : appUserList) {
			if (loggedInUser.id == appUser.id
					|| String.valueOf(appUser.id).equalsIgnoreCase(String.valueOf(loggedInUser.id))) {
				jsonMap.put("isMember", "true");
				break;
			} else {
				jsonMap.put("isMember", "false");
			}
		}
		/*
		 * String randomId="919099541150009300";; Message firstRow
		 * =Message.find.where().eq("randomId",randomId).findUnique();
		 */
		return ok(Json.toJson(jsonMap));

	}

	@BasicAuth
	public synchronized Result getGroupMessages(Long groupId, int firstRow) {
		JsonNode groupJsonMap = getJsonGroupMessages(groupId, firstRow);
		return ok(groupJsonMap);
	}

	public synchronized JsonNode getJsonGroupMessages(Long groupId, int firstRow) {
		final AppUser loggedInUser = Application.getLoggedInUser();
		final ObjectNode JsonDateWiseMessageMap = Json.newObject();
		Query<Message> messageQuery = Message.find.where().eq("chatGroup.id", groupId).order("createdOn");

		List<Message> messageList = new ArrayList<Message>();
		Integer totalMessageCount = messageQuery.findRowCount();

		boolean isLastRow = false;
		if (totalMessageCount > MAX_ROWS) {
			int startIndex = 0;
			if (firstRow == -1) {
				startIndex = totalMessageCount - MAX_ROWS;
				firstRow = startIndex;
				if (totalMessageCount <= MAX_ROWS) {
					isLastRow = true;
				}
				messageList = messageQuery.setFirstRow(startIndex).setMaxRows(MAX_ROWS).findList();
			} else {
				if (firstRow <= MAX_ROWS) {
					messageList = messageQuery.setFirstRow(0).setMaxRows(firstRow).findList();
					firstRow = 0;
					isLastRow = true;
				} else {
					startIndex = firstRow - MAX_ROWS;
					firstRow = firstRow - MAX_ROWS;
					isLastRow = false;
					messageList = messageQuery.setFirstRow(startIndex).setMaxRows(MAX_ROWS).findList();
				}
			}
		} else if (totalMessageCount <= MAX_ROWS) {
			messageList = messageQuery.setFirstRow(0).setMaxRows(totalMessageCount).findList();
			isLastRow = true;
			firstRow = MAX_ROWS - totalMessageCount;
		}

		JsonDateWiseMessageMap.put("isLastRow", isLastRow);
		JsonDateWiseMessageMap.put("firstRow", firstRow);

		// Logger.debug("raw sql"+messageQuery.getGeneratedSql());
		// Logger.debug(messageList.size()+"messageList---- size----after---");

		List<Message> unreadGroupMessageList = new ArrayList<Message>();
		List<Notification> notificationList = Notification.find.where().eq("messageTo", loggedInUser)
				.eq("toChatGroup.id", groupId).eq("isViewed", false).findList();
		if (notificationList != null && notificationList.size() >= 1) {
			for (Notification notification : notificationList) {
				Message message = notification.message;
				unreadGroupMessageList.add(message);
			}
		}
		JsonDateWiseMessageMap.set("messagePage", getJsonDateWiseMessageMap(messageList));
		String lastUnReadRandomId = null;
		if (unreadGroupMessageList != null && unreadGroupMessageList.size() >= 1) {
			lastUnReadRandomId = unreadGroupMessageList.get(0).randomId;
		}
		JsonDateWiseMessageMap.put("lastUnReadRandomId", lastUnReadRandomId != null ? lastUnReadRandomId : "");
		// Logger.debug(Json.toJson(JsonDateWiseMessageMap)+"JsonDateWiseMessageMap-----------");
		return JsonDateWiseMessageMap;

	}

	private JsonNode getJsonDateWiseMessageMap(List<Message> messageList) {
		// Logger.debug("messageList"+messageList);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		LinkedHashMap<String, Object> JsonDateWiseMessageMap = new LinkedHashMap<String, Object>();
		ArrayList<Object> jsonMessageList = new ArrayList<Object>();
		String changeName = null;
		String messageDate = null;

		for (Message message : messageList) {
			MessageContentType mc = message.messageContentType;
			Boolean isDatChanged = false;
			messageDate = dateFormat.format(message.createdOn).toUpperCase();
			if (!JsonDateWiseMessageMap.containsKey(messageDate)) {
				isDatChanged = true;
				jsonMessageList = new ArrayList<Object>();
			}
			if (mc == MessageContentType.TEXT || mc == MessageContentType.FILE || mc == MessageContentType.IMAGE
					|| mc==MessageContentType.SNIPPET || mc == MessageContentType.CREATEGROUP
					|| mc == MessageContentType.RENAMEGROUP || mc == MessageContentType.DELETEGROUP
					|| mc == MessageContentType.LEFTGROUP || mc == MessageContentType.ADDTOGROUP) {
				if (changeName == null || (changeName != null && (!changeName.equals(AppUser.find.byId(message.messageBy.id).FullName)) || isDatChanged)) {
					message.setUserNameChange(true);
				}
			}
			jsonMessageList.add(convertJsonMessage(message,false));
			changeName = AppUser.find.byId(message.messageBy.id).FullName;

			JsonDateWiseMessageMap.put(messageDate, jsonMessageList);
		}
		//Logger.debug("JsonDateWiseMessageMap----------" + JsonDateWiseMessageMap);
		return Json.toJson(JsonDateWiseMessageMap);
	}

	@BasicAuth
	public Result changeGroupNotificationStatus(Long groupId) {
		AppUser loggedInUser = Application.getLoggedInUser();
		List<Notification> notificationList = Notification.find.where().eq("messageTo", loggedInUser)
				.eq("toChatGroup", ChatGroup.find.byId(groupId)).eq("isViewed", false).findList();
		for (Notification notification : notificationList) {
			notification.isViewed = true;
			notification.update();
		}
		return ok();
	}

	@BasicAuth
	public Result changeActiveAppUserGroupNotification(Long groupId) {
//		Logger.debug(" group id is activer======================================= "+groupId);
		AppUser loggedInUser = Application.getLoggedInUser();
		/*List<Notification> notificationList = Notification.find.where().eq("messageTo", loggedInUser)
				.eq("toChatGroup", ChatGroup.find.byId(groupId)).findList();
		for (Notification notification : notificationList) {
			notification.isViewed = true;
			notification.update();
		}*/
		
		
		String queryStr = "UPDATE notification SET is_viewed='t' WHERE message_to_id= :appid AND to_chat_group_id= :gid AND is_viewed is false;";
		
		SqlUpdate update = Ebean.createSqlUpdate(queryStr);
				update.setParameter("appid", loggedInUser.id);
				update.setParameter("gid", groupId);

		int modifiedCount = Ebean.execute(update);
//		Logger.debug("updated rows ::" + modifiedCount);
		
		return ok();
	}
	
	@BasicAuth
	public Result getGroupMessageNotificationCount(Long groupId) {
		Integer notificationCount = null;
		AppUser loggedInUser = Application.getLoggedInUser();
		List<Notification> notificationList = Notification.find.where().eq("messageTo", loggedInUser)
				.eq("toChatGroup", ChatGroup.find.byId(groupId)).eq("isViewed", false).findList();

		if (notificationList != null && notificationList.size() >= 0) {
			notificationCount = notificationList.size();
		}
		return ok(Json.toJson(notificationCount));

	}

	public static Boolean isGroupMember(ChatGroup chatGroup) {
		Boolean isMember = false;
		List<AppUser> appUserList = new ArrayList<AppUser>();
		List<ChatGroupAppUserInfo> groupMembers = ChatGroupAppUserInfo.find.where().eq("chatGroup", chatGroup)
				.findList();
		AppUser loggedInUser = Application.getLoggedInUser();

		for (ChatGroupAppUserInfo chatGroupAppUserInfo : groupMembers) {
			appUserList.add(chatGroupAppUserInfo.appUser);
		}
		for (AppUser appUser : appUserList) {
			if (loggedInUser.id == appUser.id
					|| String.valueOf(appUser.id).equalsIgnoreCase(String.valueOf(loggedInUser.id))) {
				isMember = true;
				break;
			} else {
				isMember = false;
			}
		}
		return isMember;

	}

	@BasicAuth
	public static Integer getNotificationsByAllUsers() {
		// Logger.debug("getNotificationsByAllUsers()");
		final AppUser loggedInUser = Application.getLoggedInUser();
		Integer messageCount = Message.find.where().eq("messageTo", loggedInUser).eq("isViewd", false).findList()
				.size();
		Integer notificationCount = Notification.find.where().eq("messageTo", loggedInUser).eq("isViewed", false)
				.findList().size();
		Integer total = messageCount + notificationCount;
	//	Logger.debug("getNotificationsByAllUsers()" + total);
		return total;
	}

	public Result getGlobalNotifications(String toUserId) {
		// Logger.debug("chatcontroller:getGlobalNotifications"+toUserId+"global
		// message count message");
		Integer messageCount = Message.find.where().eq("messageTo", AppUser.find.byId(Long.parseLong(toUserId)))
				.eq("isViewd", false).findList().size();
		// Logger.debug("chatcontroller:getGlobalNotifications"+toUserId+"global
		// message count message"+messageCount);
		return ok(Json.toJson(messageCount));
	}

	public Result getGroupGlobalNotifications(String groupId) {
		boolean isValidMember = false;
		final AppUser loggedInUser = Application.getLoggedInUser();
		List<Long> groupAppUserIdList = new ArrayList<Long>();
		Map<String, Object> JsonGroupAppUserIdMap = new HashMap<String, Object>();
		ChatGroup chatGroup = ChatGroup.find.byId(Long.parseLong(groupId));
		for (AppUser appUser : chatGroup.getAppuserList()) {
			groupAppUserIdList.add(appUser.id);
			if (appUser.id == loggedInUser.id) {
				isValidMember = true;
			}

		}
		Integer notificationCount = Notification.find.where().eq("messageTo", loggedInUser)
				.eq("toChatGroup", ChatGroup.find.byId(Long.parseLong(groupId))).eq("isViewed", false).findList()
				.size();
		// Logger.debug("chatcontroller:getGroupGlobalNotifications"+groupId+"global
		// message count message"+notificationCount);

		JsonGroupAppUserIdMap.put("groupAppUserIdList", groupAppUserIdList);
		JsonGroupAppUserIdMap.put("count", notificationCount);
		JsonGroupAppUserIdMap.put("isValidMember", isValidMember);

		return ok(Json.toJson(JsonGroupAppUserIdMap));

	}

	@BasicAuth
	public Result getAppsuerProfileImage(Long id) throws IOException {
		RawSql rawSql = RawSqlBuilder.parse("select id,thumbnail from app_user where id ="+id).create();
		Query<AppUser> query = Ebean.find(AppUser.class);
		query.setRawSql(rawSql);
	    AppUser appUser = query.findUnique();
		ByteArrayInputStream input = null;
		try {
			if (appUser != null && appUser.getThumbnail() != null) {
				input = new ByteArrayInputStream(appUser.getThumbnail());
				return ok(input).as("image/jpg");
			} 
			else {
			//	File file = new File("conf/images/thrymr.png");
				//input = new ByteArrayInputStream(ChatController.scale(Files.toByteArray(file)));
				return  Results.ok("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ok(input).as("image/jpg");
	}

	public static byte[] scale(byte[] fileData) {
		ByteArrayInputStream in = new ByteArrayInputStream(fileData);
		try {
			BufferedImage img = ImageIO.read(in);
			int scaledWidth = 35;
			int scaledHeight = 35;

			Image scaledImage = img.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
			BufferedImage imageBuff = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);
			imageBuff.getGraphics().drawImage(scaledImage, 0, 0, new Color(0, 0, 0), null);

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			ImageIO.write(imageBuff, "jpg", buffer);

			return buffer.toByteArray();

		} catch (IOException e) {
			throw new RuntimeException("IOException in scale");
		}
	}

	@BasicAuth
	public Result isExistGroup() {
		DynamicForm requestForm = Form.form().bindFromRequest();
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		String groupName = requestForm.get("name");
		String id = requestForm.get("id");

		try {
			if (id != null && !id.isEmpty()) {
				map.put("valid", !(ChatGroup.find.where().ieq("name", groupName.trim()).ne("id", Long.parseLong(id))
						.findRowCount() > 0));
			} else {
				map.put("valid", !((ChatGroup.find.where().ieq("name", groupName.trim()).findList().size()) > 0));
			}
		} catch (Exception e) {
			map.put("valid", false);
		}
		return ok(Json.toJson(map));
	}

	public Result leaveChannel(Long groupId) {
		HashMap<Object, Object> status = new HashMap<Object, Object>();
		ChatGroup chatGroup = ChatGroup.find.byId(groupId);
		AppUser loggedInUser = Application.getLoggedInUser();
		List<ChatGroupAppUserInfo> chatGroupAppUsers = ChatGroupAppUserInfo.find.where().eq("chatGroup", chatGroup)
				.eq("appUser", loggedInUser).findList();
		for (ChatGroupAppUserInfo chatGroupUser : chatGroupAppUsers) {
			chatGroupUser.delete();
			String randomId = new SimpleDateFormat("DDMMYYYYhhmmssSSSSSSS").format(new Date());
			ChatRoom.tellRoom(loggedInUser.id, "left the group", chatGroup.id, "GROUP",
					MessageContentType.LEFTGROUP.toString(), "", "", "saving", randomId, "");
		}
		List<ChatGroup> groups = ChatGroup.find.all();
		status.put("status", true);
		if (groups != null) {
			status.put("defaultgroup", groups.get(0).id);
			status.put("groupId", chatGroup.id);
			status.put("groupName", chatGroup.name);
			status.put("defaultGroupName", groups.get(0).name);
		}
		return ok(Json.toJson(status));
	}

	public List<AppUser> lastChatAppuserList() {
		List<AppUser> finalAppUserList = new ArrayList<AppUser>();
		final AppUser loggedInUser = Application.getLoggedInUser();
		Message.find.where().eq("messageTo.id", loggedInUser).eq("role", Role.USER).order("createdOn desc")
				.setMaxRows(10);
		return finalAppUserList;
	}

	public Result isGeneralGroup(Long groupId) {
		List<ChatGroup> groups = ChatGroup.find.all();
		HashMap<Object, Object> status = new HashMap<Object, Object>();
		if (groups != null) {
			if (groups.get(0).id == ChatGroup.find.byId(groupId).id) {
				status.put("status", true);
			} else {
				status.put("status", false);
			}
		}

		return ok(Json.toJson(status));

	}

	public Result getInviteOthersToChannel(Long groupId) {
		ChatGroup group = ChatGroup.find.byId(groupId);
		List<AppUser> appUserList = AppUser.find.where().ne("userName","bb8bot").findList();
		List<ChatGroupAppUserInfo> chatgroupUsers = ChatGroupAppUserInfo.find.where().eq("chatGroup", group).findList();
		List<AppUser> groupAppUser = new ArrayList<AppUser>();
		for (ChatGroupAppUserInfo chatGroupUser : chatgroupUsers) {
			groupAppUser.add(chatGroupUser.appUser);
		}
		List<AppUser> otherUsers = new ArrayList<AppUser>();
		for (AppUser appUser : appUserList) {
			if (!(Application.getLoggedInUser().id == appUser.id)) {
				if (!groupAppUser.contains(appUser)) {
					otherUsers.add(appUser);
				}
			}
		}
		return ok(views.html.chat.inviteOthersToChannel.render(group, otherUsers));

	}

	public static Map<AppUser, Integer> getUserNotifications() {
		Map<AppUser, Integer> jsonNotificationMap = new HashMap<AppUser, Integer>();
		final AppUser loggedInUser = Application.getLoggedInUser();
		for (AppUser toAppUser : AppUser.find.where().eq("status", UserProjectStatus.Active).findList()) {
			if (toAppUser.id != loggedInUser.id && toAppUser.status.equals(UserProjectStatus.Active)) {
				Integer messageCount = Message.find.where().eq("messageTo", loggedInUser).eq("messageBy", toAppUser).eq("isViewd", false).findList().size();
				jsonNotificationMap.put(toAppUser, messageCount);
			}
		}

		List list = new LinkedList(jsonNotificationMap.entrySet());
		// Defined Custom Comparator here
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return -((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
			}
		});

		// Here I am copying the sorted list in HashMap
		// using LinkedHashMap to preserve the insertion order
		HashMap sortedHashMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedHashMap.put(entry.getKey(), entry.getValue());
		}

		return sortedHashMap;
	}

	public Result getGroupMembers(Long groupId) {
		ChatGroup chatGroup = ChatGroup.find.byId(groupId);
		List<AppUser> groupUsers = chatGroup.getAppuserList();
		List<AppUser> groupMembers = new ArrayList<AppUser>();
		for (AppUser appUser : groupUsers) {
			AppUser appUser1 = AppUser.find.byId(appUser.id);
			groupMembers.add(appUser1);
		}
		//Logger.debug("" + groupMembers.toString());
		return ok(views.html.chat.viewGroupMembers.render(groupMembers));
	}

	@BasicAuth
	public Result getUploadImage(Long uplaodId) {
		UploadFileInfo uploadFileInfo = UploadFileInfo.find.byId(uplaodId);
		
		if (uploadFileInfo != null && uploadFileInfo.uploadImage != null) {
			//response().setCookie(uploadFileInfo.uploadFileName,uploadFileInfo.uploadFileName);
			return ok(uploadFileInfo.uploadImage).as("image/png");
		} else {
			return badRequest();
		}

	}

	@BasicAuth
	public Result downloadUploadImage(Long uplaodId) {
		UploadFileInfo uploadFileInfo = UploadFileInfo.find.byId(uplaodId);
		String uploadFileContentType = uploadFileInfo.uploadFileContentType;
		String uploadFileName = uploadFileInfo.uploadFileName;
		byte[] uploadFile = uploadFileInfo.uploadImage;
		response().setContentType("APPLICATION/OCTET-STREAM");
		response().setHeader("Content-Disposition", "attachment; filename=\"" + uploadFileName + "\"");
		return ok(uploadFile).as(uploadFileContentType);
	}

	// Accept only 1000000KB of data.
	@BasicAuth
	//@BodyParser.Of(value = BodyParser.MultipartFormData.class, maxLength = 500000000 * 1024)
	public Result saveUploadImage() {
		//response().setHeader("Connection","keep-alive");
		//response().setHeader("Transfer-Encoding","chunked");
		
		UploadFileInfo uploadFile = new UploadFileInfo();
		boolean isSuccess = true;
		try {
			byte[] uploadImage = null;
			String fileName = null;
			String contentType = null;
			// processing image
			play.mvc.Http.MultipartFormData body = request().body().asMultipartFormData();
			play.mvc.Http.MultipartFormData.FilePart picture = body.getFile("image");
			java.io.File file = null;
			if (picture != null) {
				fileName = picture.getFilename();
				contentType = picture.getContentType();
				file = picture.getFile();
			//	play.Logger.debug("file-name-----------------------------------> " + fileName);
			//	play.Logger.debug("file------------------------------------> " + file);
				//Logger.debug("contentType" + contentType);
			}
			InputStream is = new FileInputStream(file);
			uploadImage = IOUtils.toByteArray(is);

			uploadFile.uploadImage = uploadImage;
			uploadFile.appUser = Application.getLoggedInUser();
			uploadFile.uploadFileContentType = contentType;
			uploadFile.uploadFileName = fileName;
			uploadFile.fileSize = String.valueOf(uploadImage.length);
			uploadFile.save();
			
			uploadFile=getLikesAndCommentInfo(uploadFile);
			//Logger.debug("uploadFile.f****....."+uploadFile);
		} catch (Exception e) {
			isSuccess = false;
		}
		/*if(isSuccess){
			throw  new NullPointerException("test");
		}*/
		uploadFile.uploadImage = null;
		
		return ok(Json.toJson(uploadFile));

	}

	@BasicAuth
	public Result editMessage(String randomId) {
		//Logger.debug("editMessage" + "chatcontroller*************");

		String newDescription = null;
		// Map<String, String[]> data = request().body().asFormUrlEncoded();
		DynamicForm requestData = Form.form().bindFromRequest();
		newDescription = requestData.get("msgTextArea");

		Map<String, String> jsonMap = new HashMap<String, String>();
		Message message = Message.find.where().eq("randomId", randomId).findUnique();
		if (message != null) {
			message.description = newDescription;
			message.update();
		}
		jsonMap.put("messageId", String.valueOf(message.id));
		jsonMap.put("description", message.description.replaceAll("\n", "<br>"));
		jsonMap.put("messageContentType",
				message.messageContentType != null ? message.messageContentType.toString() : "");
		jsonMap.put("randomId", message.randomId);
		if (message.role != null && message.role == Role.USER) {
			jsonMap.put("role", Role.USER.toString());
			jsonMap.put("toUserId", String.valueOf(message.messageTo));
		} else if (message.role != null && message.role == Role.GROUP) {
			jsonMap.put("role", Role.GROUP.toString());
			jsonMap.put("toUserId", String.valueOf(message.chatGroup.id));
		}

		return ok(Json.toJson(jsonMap));

	}

	@BasicAuth
	public Result deleteMessage(Long messageId) {
		//Logger.debug("delete" + "chatcontroller*************");
		Map<String, String> jsonMap = new HashMap<String, String>();
		Message message = Message.find.byId(messageId);
		jsonMap.put("messageId", String.valueOf(message.id));
		message.delete();
		return ok(Json.toJson(jsonMap));
	}

	@BasicAuth
	public Result getMessageDetails(String randomId) {
		System.out.println(randomId + "randomId");
		Message message = Message.find.where().eq("randomId", randomId).findUnique();
		Map<String, String> jsonMap = new HashMap<String, String>();
		jsonMap.put("description", message.description);
		jsonMap.put("messageContentType", message.messageContentType.toString());
		jsonMap.put("appUserName", AppUser.find.byId(message.messageBy.id).FullName);

		return ok(Json.toJson(jsonMap));

	}

	@BasicAuth
	public Result getAllAppuserList() {
		List<AppUser> appUserList = AppUser.find.where().eq("status", UserProjectStatus.Active).findList();
		// Map<Object,Object> jsonMap=new HashMap<Object,Object>();
		List<Object> jsonList = new ArrayList<Object>();
		for (AppUser appUser : appUserList) {
			Map<String, String> userMap = new HashMap<String, String>();
			userMap.put("username", appUser.userName);
			userMap.put("fullname", appUser.getFullName());
			jsonList.add(userMap);
		}
		return ok(Json.toJson(jsonList));

	}

	public Result getAppuserProfile(String userName) {
		Map<Object, Object> jsonMap = new HashMap<Object, Object>();

		AppUser appuser = AppUser.find.where().ieq("userName",userName).findUnique();
		if(appuser!=null){
			jsonMap.put("id", appuser.id != null ? appuser.id :"");
			jsonMap.put("fullName",  appuser.FullName != null ? appuser.FullName :"");
			jsonMap.put("userName",  appuser.userName != null ? appuser.userName :"");
			jsonMap.put("jobTitle",  appuser.jobTitle != null ? appuser.jobTitle :"");
			jsonMap.put("email", 	 appuser.email != null ? appuser.email :"");
			jsonMap.put("mobileNo",  appuser.mobileNo != 0l ? appuser.mobileNo :"");
			jsonMap.put("gitId",     appuser.gitId != null ? appuser.gitId :"");
			jsonMap.put("employeeId",appuser.employeeId != null ? appuser.employeeId :"");
			if (appuser.getImage() != null) {
				jsonMap.put("imageUrl", "/secure-chat/get-appuser-image/" + appuser.id);
			} else {
				jsonMap.put("imageUrl", "/assets/images/thrymr.png");
			}

		}
		
		return ok(Json.toJson(jsonMap));

	}

	public Result getWebHookJson(Long groupId) {
		ChatGroup chatGroup = ChatGroup.find.byId(groupId);
		JsonNode json = request().body().asJson();
		// Logger.debug("json size >?>>>>>>>>>>>>>>>>>>>>>>>"+json.size());
		if (chatGroup != null) {
			GitNotificationBean sendNotification = new GitNotificationBean();
			if (json.has("commits")) {
				if (json.findPath("created").asText().toString().trim().equalsIgnoreCase("true")
						|| json.findPath("deleted").asText().toString().trim().equalsIgnoreCase("true")) {
					// Logger.debug(">>>>>>>>>>>>>> branch created or deleted
					// >>>>>>>>>>>.");
					sendNotification.notifyWhenGitBranchModified(json, chatGroup);
				} else {
					sendNotification.notifyWhenPush(json, chatGroup);
				}
			}
			if (json.has("issue")) {

				sendNotification.notifyWhenIssueTriggered(json, chatGroup);
				// Logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.
				// "+json.findValue("action"));
			}
			if (json.has("action") && !json.has("issue") && json.has("comment")) {
				sendNotification.notifyWhenCommentOnCommmit(json, chatGroup);
			}
		}
		return ok("");
	}

	public Result getGitIntegrationDetails(Long groupId) {
		Map<Object, Object> jsonMap = new HashMap<Object, Object>();
		List<Message> gitNotifications = Message.find.where().eq("chatGroup", ChatGroup.find.byId(groupId))
				.eq("messageContentType", MessageContentType.GITNOTIFICATION.toString()).findList();
		//Logger.debug("git meessages " + gitNotifications.toString());
		if (gitNotifications.size() > 0) {
			jsonMap.put("configure", true);
		} else {
			jsonMap.put("configure", false);
		}
		return ok(Json.toJson(jsonMap));
	}

	public Result gitConfigureUrl(Long groupId) {

		Map<Object, Object> jsonMap = new HashMap<Object, Object>();
		String url = "http://" + request().host() + "/secure-chat/get-webhook-response/" + groupId;
		jsonMap.put("url", url);

		return ok(Json.toJson(jsonMap));
	}

	public Result getAppuserJsonDetails(Long appsuerId) {
		final ObjectNode appUserJsonMap = Json.newObject();
		AppUser appUser = AppUser.find.byId(appsuerId);
		appUserJsonMap.set("appUser",Json.toJson(appUser));
		
		AppUser loggedInUser = Application.getLoggedInUser();
		ChatAppUserSettings appUserSettings =ChatAppUserSettings.find.where().eq("loggedInUser",loggedInUser).findUnique();
		appUserJsonMap.set("appUserSettings",Json.toJson(appUserSettings));
		appUserJsonMap.set("createdChatGroupsIds",Json.toJson(getAppUserCreatedChatGroups(loggedInUser.id)));
		return ok(appUserJsonMap);
	}

	public Result isMessageSaved(String randomId) {
		Map<Object, Object> jsonMap = new HashMap<Object, Object>();
		Message message = Message.find.where().eq("randomId", randomId).findUnique();
		if (message != null) {
			jsonMap.put("status", true);
			jsonMap.put("randomId", message.randomId);
		} else {
			jsonMap.put("status", false);
			jsonMap.put("randomId", randomId);
		}
		return ok(Json.toJson(jsonMap));
	}

	public Result getChatNew() {
		AppUser loggedInUser = Application.getLoggedInUser();
		return ok(views.html.chat.chatNew.render(loggedInUser));
	}

	public Result getGitNotificationType(Long msgId) {
		Map<Object, Object> jsonMap = new HashMap<Object, Object>();
		Message message = Message.find.byId(msgId);
		GitNotification notification = GitNotification.find.where().eq("message", message).findUnique();
		if (notification != null) {
			jsonMap.put("status", true);
			jsonMap.put("type", notification.gitNotificationType);
			jsonMap.put("notifId", notification.id);
		} else {
			jsonMap.put("status", false);
			jsonMap.put("type", "PUSH");
		}
		return ok(Json.toJson(jsonMap));
	}

	public Result getGitIssueType(Long notificationId) {
		Map<Object, Object> jsonMap = new HashMap<Object, Object>();
		GitNotification notification = GitNotification.find.byId(notificationId);
		GitIssue gitIssue = GitIssue.find.where().eq("gitNotificationId", notification).findUnique();
		if (gitIssue != null) {
			jsonMap.put("issueType", gitIssue.gitIssueType);
			jsonMap.put("id", gitIssue.id);
		}
		return ok(Json.toJson(jsonMap));
	}

	public Result getGitIssueDetails(Long notificationId) {
		Map<Object, Object> jsonMap = new HashMap<Object, Object>();
		GitNotification notification = GitNotification.find.byId(notificationId);
		GitIssue gitIssue = GitIssue.find.where().eq("gitNotificationId", notification).findUnique();
		List<UploadFileInfo> uploadFiles = UploadFileInfo.find.where().eq("gitIssue", gitIssue).findList();
		ArrayList<Object> files = new ArrayList<Object>();
		if (notification != null) {
			jsonMap.put("repository", notification.repository);
			jsonMap.put("repositoryUrl", notification.repositoryUrl);
			jsonMap.put("newBranch", notification.repositoryBranch);
			jsonMap.put("branchedFrom", notification.branchedFrom);
			jsonMap.put("pusherEmail", notification.pusherEmail);
			jsonMap.put("pushedUser", notification.committedBy);
		}
		if (gitIssue != null) {
			jsonMap.put("issueRaisedBy", gitIssue.IssueRaisedBy);
			jsonMap.put("issueRaisedByUrl", gitIssue.IssueRaisedByUrl);
			jsonMap.put("issueNumber", gitIssue.issueNumber);
			jsonMap.put("comment", gitIssue.comment);
			jsonMap.put("title", gitIssue.title);
			jsonMap.put("issueUrl", gitIssue.issueUrl);
			jsonMap.put("commentBy", gitIssue.commentedBy);
			jsonMap.put("assignedTo", gitIssue.assignedTo);
			jsonMap.put("assigneeUrl", gitIssue.assigneeUrl);
			jsonMap.put("fullName", gitIssue.fullName);
			if (uploadFiles.size() > 0) {
				for (UploadFileInfo file : uploadFiles) {
					Map<String, Object> fileIdList = new HashMap<String, Object>();
					fileIdList.put("id", file.id);
					fileIdList.put("fileName", file.uploadFileName);
					fileIdList.put("fileUrl", file.fileUrl);
					files.add(fileIdList);
				}
				jsonMap.put("issueImages", files);
			}
		}
		// Logger.info(""+jsonMap.toString());
		return ok(Json.toJson(jsonMap));
	}

	public Result getGitIssueImage(Long uploadFileId) {
		UploadFileInfo uploadFileInfo = UploadFileInfo.find.byId(uploadFileId);
		ByteArrayInputStream input = new ByteArrayInputStream(uploadFileInfo.uploadImage);
		if (uploadFileInfo != null && uploadFileInfo.uploadImage != null) {
			return ok(uploadFileInfo.uploadImage).as("image/png");
		} else {
			return badRequest();
		}

	}

	public Result getGitCommitCommentDetails(Long notificationId) {
		Map<Object, Object> jsonMap = new HashMap<Object, Object>();
		GitNotification notification = GitNotification.find.byId(notificationId);
		GitCommitComment gitComment = GitCommitComment.find.where().eq("gitNotificationId", notification).findUnique();
		if (gitComment != null) {
			jsonMap.put("fullName", gitComment.full_name);
			jsonMap.put("commitId", gitComment.commitId.substring(Math.max(0, gitComment.commitId.length() - 7)));
			jsonMap.put("commitUrl", gitComment.commitUrl);
			jsonMap.put("commentBy", gitComment.commentBy);
			jsonMap.put("comment", gitComment.comment);
		}
		return ok(Json.toJson(jsonMap));
	}

	public Result getAllChannelsJsonData() throws InterruptedException {
		final ObjectNode channelsJsonMap = Json.newObject();
		for (ChatGroup chatGroup : ChatGroup.find.all()) {
			if (isGroupMember(chatGroup)) {
				JsonNode groupJsonMap = getJsonGroupMessages(chatGroup.id, -1);
				//Thread.sleep(1000);
				channelsJsonMap.set(chatGroup.name, groupJsonMap);
			}
		}
		AppUser loggedInUser = Application.getLoggedInUser();
		for (AppUser toAppUser : AppUser.find.where().ne("id", loggedInUser.id).ne("email", "github@thrymr.net").ne("email","bb8bot@thrymr.net")
				.findList()) {
			JsonNode toappUserJsonMap = getJsonToUserMessages(toAppUser.id, -1);
			channelsJsonMap.set(toAppUser.userName, toappUserJsonMap);
		}
		return ok(channelsJsonMap);
	}

	public Result getNewChannelJsonData(String channelId) {
		final ObjectNode channelsJsonMap = Json.newObject();
		ChatGroup chatGroup = ChatGroup.find.byId(Long.parseLong(channelId));
		JsonNode groupJsonMap = getJsonGroupMessages(chatGroup.id, -1);
		channelsJsonMap.set(chatGroup.name, groupJsonMap);
		return ok(channelsJsonMap);
	}

	@BasicAuth
	public Result isValidGroupMember1(String groupId) {
		// Logger.debug("isValidGroupMember():chatController"+groupId);
		ChatGroup chatGroup = null;
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		//List<Long> appUserIdList = new ArrayList<Long>();
		final AppUser loggedInUser = Application.getLoggedInUser();

		if (groupId != null && groupId != "undefined") {
			chatGroup = ChatGroup.find.byId(Long.parseLong(groupId));
			jsonMap.put("groupName", chatGroup.name);
		}
		for (AppUser appUser : chatGroup.getAppuserList()) {
			if (appUser.id == loggedInUser.id) {
				jsonMap.put("isMember", true);
				jsonMap.put("id", loggedInUser.id);
				jsonMap.put("userName", loggedInUser.userName);
				jsonMap.put("role", loggedInUser.role);
				break;
			} else {
				jsonMap.put("isMember", false);
			}
		}
		return ok(Json.toJson(jsonMap));

	}

	public synchronized Result getHtmlfromUrl(String url) {
		StringBuffer html = new StringBuffer();
		try {

			// url=URLEncoder.encode(url, "UTF-8");
			URL obj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
			conn.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
			/*
			 * conn.setReadTimeout(5000);
			 * conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
			 * conn.setRequestMethod("POST");
			 */
			/*
			 * conn.setDoOutput(true); conn.setDoInput(true);
			 * conn.setRequestProperty("Content-Length","19000000");
			 * conn.setRequestProperty("Content-Type",
			 * "application/x-www-form-urlencoded"); //URLencode.. ;
			 */

			//Logger.debug("Request URL ... " + url);

			boolean redirect = false;

			// normally, 3xx is redirect
			int status = conn.getResponseCode();
		//	Logger.debug("status code ......." + conn.getResponseCode());
		//	Logger.debug("header fields code ......." + conn.getHeaderFields());

			if (status != HttpURLConnection.HTTP_OK) {
				if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM
						|| status == HttpURLConnection.HTTP_SEE_OTHER)
					redirect = true;
			}

			//Logger.debug("Response Code ... " + status);

			if (redirect) {
				// get redirect url from "location" header field
				String newUrl = conn.getHeaderField("Location");

				//Logger.debug("newUrl : " + newUrl);
				// get the cookie if need, for login
				//String cookies = conn.getHeaderField("Set-Cookie");

				// open the new connnection again
				conn = (HttpURLConnection) new URL(newUrl).openConnection();

			//	Logger.debug("Redirect to URL : " + newUrl);

			}
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			html = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {

				if (inputLine.contains("<body")) {
					break;
				} else {
					html.append(inputLine);

				}

			}
			// in.close();

			// Logger.debug("URL Content... \n" + html.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ok(views.html.preview.render(html.toString()));
	}

	public synchronized Result saveUrlPreview() {
		DynamicForm requestForm = Form.form().bindFromRequest();
		String randomId = requestForm.get("randomId");
		String base64Image = requestForm.get("image");
		String urlPreviewTitle = requestForm.get("title");
		String urlPreviewDescription = requestForm.get("description");
		String url = requestForm.get("previewUrl").trim();
		String imageUrl = requestForm.get("imageUrl");
		Attachment attachment = null;

		List<Attachment> attachmentList = Attachment.find.where().ieq("url", url).findList();

		if (attachmentList != null && attachmentList.size() >= 1) {
			attachment = attachmentList.get(0);
		} else {
			attachment = new Attachment();
			attachment.attachmentImage = convertBase64ToBinary(base64Image);
			attachment.description = urlPreviewDescription;
			attachment.attachmentType = AttachmentType.URL;
			attachment.title = urlPreviewTitle;
			attachment.url = url;
			attachment.imageUrl = imageUrl;
			attachment.appUser = Application.getLoggedInUser();
			attachment.save();
		}
		Message message = Message.find.where().eq("randomId", randomId).findUnique();
		message.isAttachment = true;
		message.update();

		MessageAttachment messageAttachment = new MessageAttachment();
		messageAttachment.message = message;
		messageAttachment.attachment = attachment;
		messageAttachment.save();

		//Logger.debug("saveUrlPreview() chatController" + attachment);
		return ok("hello");
	}

	private byte[] convertBase64ToBinary(String base64Image) {
		byte[] imageBytes = null;
		// remove base64Image base64 format // data:image/png;base64
		byte[] imageByteDataType = DatatypeConverter.parseBase64Binary(base64Image);
		try {
			imageBytes = new BASE64Decoder().decodeBuffer(base64Image);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return imageBytes;
	}

	public Result deleteChatGroup(Long groupId) {
		GroupBean groupBean = new GroupBean();
		ChatGroup group = ChatGroup.find.byId(groupId);
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		List<AppUser> appUsers = null;
		if (group != null) {
			AppUser groupAdmin = group.createdBy;
			appUsers = group.getAppuserList();
			group.messageList = null;
			List<ChatGroupAppUserInfo> chatAppUserList = ChatGroupAppUserInfo.find.where().eq("chatGroup", group)
					.findList();
			if (chatAppUserList.size() > 0) {
				for (ChatGroupAppUserInfo chatGroupuser : chatAppUserList) {
					chatGroupuser.delete();
				}
			}
			List<Message> messages = Message.find.where().eq("chatGroup", group).findList();
			if (messages.size() > 0) {
				for (Message message : messages) {
					List<UploadFileInfo> uploadedFiles = UploadFileInfo.find.where().eq("message", message).findList();
					for(UploadFileInfo file : uploadedFiles){
						file.message=null;
						file.delete();
					}
					message.delete();
				}
			}
			List<Notification> notifications = Notification.find.where().eq("toChatGroup", group).findList();
			if (notifications.size() > 0) {
				for (Notification notification : notifications) {
					notification.delete();
				}
			}
			List<Task> tasks = Task.find.where().eq("chatGroup", group).findList();
			if (tasks.size() > 0) {
				for (Task task : tasks) {
					task.delete();
				}
			}
			group.delete();
			List<ChatGroup> groups = ChatGroup.find.all();
			if (groups != null) {
				jsonMap.put("defaultgroup", groups.get(0).id);
				jsonMap.put("groupId", group.id);
				jsonMap.put("groupName", group.name);
				jsonMap.put("defaultGroupName", groups.get(0).name);
			}
			jsonMap.put("status", true);
			groupBean.sendDeleteNotifcation(appUsers, group.name, groupAdmin);
		}
		return ok(Json.toJson(jsonMap));
	}

	// Accept only 1000000KB of data.
	@BasicAuth
	//@BodyParser.Of(value = BodyParser.MultipartFormData.class, maxLength = 500000000 * 1024)
	public Result saveCodeSnippet() {
		Map<Object,Object> jsonMap =new HashMap<Object,Object>();
		
		DynamicForm requestForm = Form.form().bindFromRequest();
		Boolean isSucces = true;
		String snippetName = requestForm.get("snippetName");
		String uploadFileContentType = requestForm.get("fileType");
		String code = requestForm.get("code");
		
		//Logger.debug("snippetName"+snippetName);
		//Logger.debug("uploadFileContentType"+uploadFileContentType);
		//Logger.debug("code"+code);
		
		UploadFileInfo uploadSnippet =new UploadFileInfo();
		try {
			uploadSnippet.uploadFileName=snippetName;
			uploadSnippet.uploadFileContentType = uploadFileContentType;
			uploadSnippet.uploadImage=code.getBytes();
			uploadSnippet.appUser = Application.getLoggedInUser();
			uploadSnippet.save();
			uploadSnippet=getSnippetFirstTime(uploadSnippet);
		} catch (Exception e) {
			//Logger.error("exception in saving code snippet.");
			isSucces = false;
		}
		
		if(isSucces){
			//Logger.debug("upload File sucessfully stored"+uploadSnippet.id);
			jsonMap.put("uploadFile", uploadSnippet);
		}
		
		return ok(Json.toJson(jsonMap));
	}
	
	public Result getSnippetAllLines(String randomId){
		Message message = Message.find.where().eq("randomId",randomId).findUnique();
		Map<String, String> jsonMap = new HashMap<String, String>();
		UploadFileInfo uploadFileInfo=null;
		if(message!=null){
			 uploadFileInfo = UploadFileInfo.find.where().eq("message.id",message.id).findUnique();
		}
		jsonMap.put("allLines",uploadFileInfo.uploadImage != null ? new String(uploadFileInfo.uploadImage) : "");
		return ok(Json.toJson(jsonMap));
	}
	
	public Result renameChatGroup(){
		  String previousGroupName = "";
		  GroupBean groupBean = new GroupBean();
		  DynamicForm requestForm = Form.form().bindFromRequest();
		  Map<String, Object> jsonMap = new HashMap<String, Object>();
		  String groupName = requestForm.get("name").trim();
	      String id = requestForm.get("id");
		  ChatGroup chatGroup = ChatGroup.find.byId(Long.parseLong(id));
		  if(groupName!=null && chatGroup!=null ){
			  previousGroupName = chatGroup.name;
			  chatGroup.name = groupName;
			  chatGroup.update();
			  jsonMap.put("status", true);
		  }else{
			  jsonMap.put("status", false);
		  }
		  groupBean.sendRenameGroupNotification(chatGroup,previousGroupName);
		  return ok(Json.toJson(jsonMap)); 
	  }
	  public List<Long> getAppUserCreatedChatGroups(Long appUserId){
		  List<Long> appUserList = new ArrayList<Long>();
		  List<ChatGroup> chatGroups = ChatGroup.find.where().eq("createdBy", AppUser.find.byId(appUserId)).findList();
		  if(chatGroups.size()>0){
			  for(ChatGroup chatGroup:chatGroups){
				  appUserList.add(chatGroup.id);
			  }
		  }
		  return appUserList;
	  }
	
	  public Result getFileCommentsList(String FileId){
		  UploadFileInfo uploadFileInfo = UploadFileInfo.find.byId(Long.parseLong(FileId));
		 List<FileComment> commentList = FileComment.find.where().eq("uploadFileInfo",uploadFileInfo).order("createdOn").findList();
		 uploadFileInfo.commentList = commentList;
		  return ok(Json.toJson(uploadFileInfo));
	  }

	public Result getFileDetails(String FileId) throws JsonProcessingException {
		Map<String,Object> map =new HashMap<String,Object>();
		//final AppUser loggedInUser = Application.getLoggedInUser();
		Message message = null;
	
		UploadFileInfo uploadFileInfo = UploadFileInfo.find.byId(Long.parseLong(FileId));
		if(uploadFileInfo != null ){
			message = uploadFileInfo.message;		
		}
		if (message != null) {
			if (message.messageContentType == MessageContentType.SNIPPET) {
				uploadFileInfo = getSnippetFirstTime(uploadFileInfo);
			}
			message.uploadFile = getChannalDetailsMap(uploadFileInfo);
			FileLike fileLike = getLastLiked(uploadFileInfo);
			
			map.put("message",Json.toJson(message));
			map.put("likesCount",message.uploadFile.likeList.size());
			map.put("commentsCount",uploadFileInfo.commentList.size());
			map.put("lastLiked",Json.toJson(fileLike != null ? fileLike : ""));
			map.put("isLiked",isLiked(message.uploadFile));
			return ok(Json.toJson(map));
		}
		return ok(Json.toJson(""));
	}
	public static FileLike getLastLiked(UploadFileInfo uploadFileInfo){
		final AppUser loggedInUser = Application.getLoggedInUser();
		FileLike fileLike =null;
		try {
		int likesCount = uploadFileInfo.likeList.size();
		if(uploadFileInfo.likeList != null && !uploadFileInfo.likeList.isEmpty() && likesCount >= 1){
			Query<FileLike> FileLikeQuery = FileLike.find.where().eq("uploadFileInfo",uploadFileInfo).order("createdOn").setMaxRows(2);
			fileLike = FileLikeQuery.findList().get(0);
			if(loggedInUser !=null  &&  fileLike.likeBy.id == loggedInUser.id && likesCount >=2 ){
					FileLikeQuery.order("createdOn").setMaxRows(2);	
					fileLike = FileLikeQuery.findList().get(1);
			}
		}
		} catch (Exception e) {
			//Logger.debug("exception in getLastLiked");
		}
		return fileLike;
	}
	
	private static Boolean isLiked(UploadFileInfo uploadFileInfo) {
		boolean isLiked = false;
		try {
		final AppUser loggedInUser = Application.getLoggedInUser();
			 List<FileLike> fileLikeList=FileLike.find.where().eq("likeBy",loggedInUser).eq("uploadFileInfo",uploadFileInfo).findList();
			 if(fileLikeList !=null && !fileLikeList.isEmpty()){
				 isLiked =true;
		 }
		} catch (Exception e) {
			//Logger.debug("exception in isLiked");
		}
			 return isLiked;
	}
	public static UploadFileInfo getChannalDetailsMap(UploadFileInfo uploadFileInfo) {
		//StringBuilder result=new StringBuilder();
		Integer fileSize= uploadFileInfo.fileSize !=null  ?  Integer.parseInt(uploadFileInfo.fileSize) : 0;
		List<FileComment> commentList = FileComment.find.where().eq("uploadFileInfo",uploadFileInfo).order("createdOn").findList();
		uploadFileInfo.commentList = commentList;
		uploadFileInfo.snippetMap.put("groupName",getChannalName(uploadFileInfo));
		uploadFileInfo.snippetMap.put("fileSize",fileSize.toString());
		return uploadFileInfo;
	}
	public static String getChannalName(UploadFileInfo uploadFileInfo){
		Message message = uploadFileInfo.message;
		String channelName = "";
		
		if (message != null) {
			if (message.role == Role.GROUP) {
				ChatGroup chatGroup = message.chatGroup;
				channelName = chatGroup != null ? chatGroup.name.trim() : "";
			} else if (message.role == Role.USER) {
				AppUser appUser = message.messageTo;
				//Logger.debug("appUser userName......."+appUser.getUserName().trim());
				channelName = appUser != null && appUser.getUserName() != null  ? appUser.getUserName().trim() : "";
			}
		}
		return channelName;
	}
	
	public Result getFileTODetails(String FileId) {
		Message message = null;
		UploadFileInfo uploadFileInfo = UploadFileInfo.find.byId(Long.parseLong(FileId));
		message = uploadFileInfo.message;
		Map<String,Object> map =new HashMap<String, Object>();
		if (message != null) {
			if (message.role == Role.GROUP) {
				map.put("messageTo",String.valueOf(message.chatGroup.id));
			} else if (message.role == Role.USER) {
				map.put("messageTo",String.valueOf(message.messageTo.id));
			}
			map.put("channelName",getChannalName(uploadFileInfo));
			map.put("byUserName",getFileBYUserName(uploadFileInfo));
			map.put("message",Json.toJson(message));
			map.put("commentsCount",uploadFileInfo.commentList.size());
		}
	
		return ok(Json.toJson(map));
	}
	
	public String getFileMessageRole(UploadFileInfo uploadFileInfo){
		Message message = uploadFileInfo.message;
		String role =null;
		if (message != null) {
			role =  message.role.toString();
		}
		return role;
	}
	public Long	getFileMessageTo(UploadFileInfo uploadFileInfo,Long likeBy){
		Message message = uploadFileInfo.message;
		Long messageTo = null;
		if (message != null) {
			if (message.role == Role.GROUP) {
				messageTo = message.chatGroup.id;
			} else if (message.role == Role.USER) {
				if(message.messageBy.id == likeBy){
					messageTo = message.messageTo.id;
				}else{
					messageTo = message.messageBy.id;
				}
			}
		}
		return messageTo;
	}
	
	  public Result updateFileLike(){
		 JsonNode jsonNode = request().body().asJson();
		 String likeById = jsonNode.findPath("likeBy").asText().toString();
		 String fileId =   jsonNode.findPath("fileId").asText().toString();
		 String likeStatus =   jsonNode.findPath("likeStatus").asText().toString();
		 final AppUser loggedInUser = Application.getLoggedInUser();
		 Map<String,Object> map =new HashMap<String,Object>();
		//Logger.info(fileId);
		// Logger.info(likeById);
		// Logger.info(likeStatus);
		 UploadFileInfo uploadFileInfo=UploadFileInfo.find.byId(Long.parseLong(fileId)); 
		 AppUser likeBy=AppUser.find.byId(Long.parseLong(likeById));
		 if(likeStatus !=null && likeStatus.equalsIgnoreCase("like")){
			 List<FileLike> fileLikeList=FileLike.find.where().eq("likeBy",loggedInUser).eq("uploadFileInfo",uploadFileInfo).findList();
			 if(fileLikeList == null || fileLikeList.isEmpty()  ){
				  FileLike fileLike= new FileLike();
				  fileLike.likeBy=loggedInUser;
				  fileLike.uploadFileInfo=uploadFileInfo;
				  fileLike.save();
				//Logger.debug("file Commented...."+fileId);
				 ChatRoom.tellRoom(loggedInUser.id,"like",getFileMessageTo(uploadFileInfo,loggedInUser.id), getFileMessageRole(uploadFileInfo),"TEXT",String.valueOf(uploadFileInfo.id), "", "like","", "");
			 }
			 
		 } if(likeStatus !=null && likeStatus.equalsIgnoreCase("unLike")){
			 FileLike fileLike = null;
			 List<FileLike> fileLikeList=FileLike.find.where().eq("likeBy",loggedInUser).eq("uploadFileInfo",uploadFileInfo).findList();
			 if(fileLikeList != null && fileLikeList.size() >= 1 ){
				  fileLike = fileLikeList.get(0);
				  fileLike.delete();
				 // Logger.debug("file unCommnted...."+fileId);
			 }
			
			ChatRoom.tellRoom(loggedInUser.id,"unLike",getFileMessageTo(uploadFileInfo,loggedInUser.id),getFileMessageRole(uploadFileInfo),"TEXT",String.valueOf(uploadFileInfo.id), "", "unLike","","");
		 }
			FileLike fileLike = getLastLiked(uploadFileInfo);
			map.put("likesCount",uploadFileInfo.likeList.size());
			map.put("fileId",uploadFileInfo.id);
			map.put("isLiked",isLiked(uploadFileInfo));
			map.put("lastLiked",Json.toJson(fileLike!=null ? fileLike :""));
			map.put("eventBy",loggedInUser.id);
		  return ok(Json.toJson(map));
	  }
	  
	  public Result getAllIamgeLikes(){
			 JsonNode jsonNode = request().body().asJson();
			 String fileId =   jsonNode.findPath("fileId").asText().toString();
			 List<String> list =new ArrayList<String>();
			 UploadFileInfo uploadFileInfo=UploadFileInfo.find.byId(Long.parseLong(fileId)); 
			 uploadFileInfo.likeList.stream().filter(fileLike -> fileLike.likeBy != null && fileLike.likeBy.getUserName() !=null).forEach(fileLike -> list.add(fileLike.likeBy.getUserName()));
			// Logger.debug(list+"");
			  return ok(Json.toJson(list));
		  }
	public static void postListOfLeave1EmployeesInGeneral() {
		List<DateWiseAppliedLeaves> DateWiseAppliedLeavesList=DateWiseAppliedLeaves.find.where().eq("leaveDate",getTodayDateWithOutTime()).findList();
		List<AppliedLeaves> appliedLeavesList = AppliedLeaves.find.where().in("dateLeaves",DateWiseAppliedLeavesList).in("leaveStatus",LeaveStatus.APPROVED,LeaveStatus.PENDING_APPROVAL).findList();
		
		List<String> userList = new ArrayList<String>();
		final StringBuilder message =new StringBuilder();
		appliedLeavesList.stream().filter(appliedLeaves -> appliedLeaves.appUser != null && appliedLeaves.appUser.getStatus().equals(UserProjectStatus.Active)).forEach(appliedLeaves -> userList.add(appliedLeaves.appUser.getUserName()));
		userList.stream().sorted().forEachOrdered(userName -> message.append("@"+userName+" , "));
		if(message.length() > 2){
			 message.replace(message.length()-2 ,message.length(),"") ;
			 String randomId = new SimpleDateFormat("DDMMYYYYhhmmssSSSSSSS").format(new Date());
			 ChatRoom.tellRoom(getAdminId(),message.toString(),getGeneralGroup().id,Role.GROUP.toString(),MessageContentType.LEAVESTATUS.toString(),"","","saving",randomId,"");
		}
	}
	public static Date getTodayDateWithOutTime(){
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date today= getDateWithoutTime(new Date());
		String stringDate = df.format(today);
		Date todayDate = null;
		try {
			todayDate = new SimpleDateFormat("yyyy-MM-dd").parse(stringDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return todayDate;
	}
	
	public static void postBirthdayWishInGeneral() {
		Calendar cal = Calendar.getInstance();
		String todayDate =new SimpleDateFormat("MM-dd").format(cal.getTime());
		
		System.out.println(todayDate);
		RawSql rawSql = RawSqlBuilder.parse("select id,user_name from app_user where status = 'Active' and to_char(dob, 'MM-dd') like '%" + todayDate+ "%'").create();
		Query<AppUser> query = Ebean.find(AppUser.class);
		query.setRawSql(rawSql);
		List<AppUser> appUserList =query.findList();
		System.out.println(query.getGeneratedSql());
		
		List<String> userList = new ArrayList<String>();
		
		final StringBuilder message =new StringBuilder();
		appUserList.stream().filter(appUser -> appUser != null && appUser.getUserName() !=null).forEach(appUser -> userList.add(appUser.getUserName()));
		userList.stream().sorted().forEachOrdered(userName -> message.append("@"+userName+" , "));
		if(message.length() > 2){
			 message.replace(message.length()-2 ,message.length(),"") ;
		String randomId = new SimpleDateFormat("DDMMYYYYhhmmssSSSSSSS").format(new Date());
		ChatRoom.tellRoom(getAdminId(),message.toString(), getGeneralGroup().id,Role.GROUP.toString(),MessageContentType.BIRTHDAY.toString(),"","", "saving",randomId,"");
		
		}
	}
	
	public static Long getAdminId(){
		AppUser appsuer = AppUser.find.where().ieq("email", "bb8bot@thrymr.net").findUnique();
		return appsuer.id;
	}
	public static ChatGroup getGeneralGroup(){
		ChatGroup chatGroup = ChatGroup.find.where().ieq("name", "general").findUnique();
		return chatGroup;
	}
	
	
	public Result saveChatAppuserSettings(){
		 JsonNode jsonNode = request().body().asJson();
		 String leftPanelColor = jsonNode.findPath("leftPanelColor").asText().toString();
		// Logger.debug("leftPanelColor"+leftPanelColor);
		 Boolean isEnableDesktopNotfication =   jsonNode.findPath("isEnableDesktopNotfication").asBoolean();
		ChatAppUserSettings chatAppUserSettings =null;
		AppUser loggedInUser = Application.getLoggedInUser();
		//String bb8LandingMessageDescription =null;
		chatAppUserSettings =ChatAppUserSettings.find.where().eq("loggedInUser",loggedInUser).findUnique();
		if(chatAppUserSettings != null){
			 chatAppUserSettings.isEnableDesktopNotfication‎=isEnableDesktopNotfication;
			 chatAppUserSettings.leftPanelColor=leftPanelColor;
			 chatAppUserSettings.update();
		}else{
			chatAppUserSettings=new ChatAppUserSettings();
			chatAppUserSettings.loggedInUser = loggedInUser;
			chatAppUserSettings.isEnableDesktopNotfication‎=isEnableDesktopNotfication;
			chatAppUserSettings.leftPanelColor=leftPanelColor;
			chatAppUserSettings.save();
		}
		
		/*if(bb8LandingMessageDescription !=null && !bb8LandingMessageDescription.isEmpty()){
			//List<BB8LandingMessage> bb8MessageList= BB8LandingMessage.find.where().eq("addedBy","loggedInUser").findList();
			
		}*/
		
		return ok(Json.toJson(chatAppUserSettings));
		
		
	}
		
	
}
