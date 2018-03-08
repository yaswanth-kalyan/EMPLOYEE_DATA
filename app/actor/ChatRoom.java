
package actor;



import java.nio.channels.ClosedChannelException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import models.AppUser;
import models.chat.ChatGroup;
import models.chat.ChatGroupAppUserInfo;
import models.chat.FileComment;
import models.chat.FileLike;
import models.chat.GitNotification;
import models.chat.Message;
import models.chat.MessageAttachment;
import models.chat.MessageContentType;
import models.chat.Notification;
import models.chat.Role;
import models.chat.UploadFileInfo;
import play.Logger;
import play.libs.Akka;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.libs.Json;
import play.mvc.WebSocket;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import actor.OnlineActor.Cron;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.pattern.Patterns;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import controllers.Application;
import controllers.ChatController;

/**
 * A chat room is an Actor.
 */

public class ChatRoom extends UntypedActor 
{

	
	
	// Default room.
	public static ActorRef defaultRoom = Akka.system().actorOf(Props.create(ChatRoom.class));

	// Create a Robot, just for fun.
	/* static {
        new Robot(defaultRoom);
    }*/

	/**
	 * Join the default room.
	 */
	static {
		Akka.system().scheduler().schedule(
				Duration.create(0, TimeUnit.MILLISECONDS), //Initial delay 0 milliseconds
				Duration.create(10,TimeUnit.SECONDS),     //Frequency seconds
				defaultRoom, 
				new Cron(),
				Akka.system().dispatcher(),
				null
				);
		TimeZone.setDefault(TimeZone.getTimeZone("GMT+5:30"));
		
	}
	
	
	public static void join(final Long userId, final WebSocket.In<JsonNode> in, final WebSocket.Out<JsonNode> out) throws Exception{
		
		//Logger.debug("chatroom join() method websocket created  "); 
		// Send the Join message to the room
		final String result = (String)Await.result(Patterns.ask(defaultRoom,new Join(userId, out),1000),Duration.create(1,TimeUnit.SECONDS));
		if("OK".equals(result)) {
			// For each event received on the socket,
			in.onMessage(new Callback<JsonNode>() {
			
				@Override
				public void invoke(final JsonNode event) {
					//Logger.debug("invoke method");
					// Send a Talk message to the room.
					//Logger.debug("------------------------invoke() description--------------"+event.get("description").asText());
					//Logger.debug("------------------------invoke() toUserId--------------"+event.get("toUserId").asText());
					//Logger.debug("------------------------invoke() role--------------"+event.get("role").asText());
					//Logger.debug("------------------------invoke() messageContentType--------------"+event.get("messageContentType").asText());
					//Logger.debug("------------------------invoke() uploadFileId--------------"+event.get("uploadFileId").asText());
					//Logger.debug("------------------------invoke() comments--------------"+event.get("comments").asText());
					//Logger.debug("------------------------invoke() messagePersistentStatus--------------"+event.get("messagePersistentStatus").asText());
					//Logger.debug("------------------------invoke() randomId--------------"+event.get("randomId").asText());
					//Logger.debug(new Talk(userId, event.get("description").asText(),Long.parseLong(event.get("toUserId").asText()),event.get("role").asText(),event.get("messageContentType").asText(),event.get("uploadFileId").asText(),event.get("comments").asText(),event.get("messagePersistentStatus").asText(),event.get("randomId").asText()));
					defaultRoom.tell(new Talk(userId, event.get("description").asText(),Long.parseLong(event.get("toUserId").asText()),event.get("role").asText(),event.get("messageContentType").asText(),event.get("uploadFileId").asText(),event.get("comments").asText(),event.get("messagePersistentStatus").asText(),event.get("randomId").asText(),event.get("gitNotificationId").asText()), null);
				
						

				} 
			});

			// When the socket is closed.
			in.onClose(new Callback0() {
				@Override
				public void invoke() {
					// Send a Quit message to the room.
					defaultRoom.tell(new Quit(userId), null);
				}
			});
			

		}
		else 
		{
			// Cannot connect, create a Json error.
			final ObjectNode error = Json.newObject();
			error.put("error", result);

			// Send the error to the socket.
			out.write(error);

		}

	}

	// Members of this room.
   public static Map<Long, WebSocket.Out<JsonNode>> members = new HashMap<Long, WebSocket.Out<JsonNode>>();
   public static Set<Long> onlineAppusers = new HashSet<Long>();

	@Override
	public void onReceive(final Object message) throws Exception {
		if(message instanceof Join) {
			// Received a Join message
			final Join join = (Join)message;

			// Check if this username is free.
			if(members.containsKey(join.userId)) {
				getSender().tell("OK",getSelf());
			} else {
			
				members.put(join.userId, join.channel);
				notifyStatus("join", join.userId, "he join the room");
				//members.put(join.userId, join.channel);
				getSender().tell("OK",getSelf());
			}

		} else if(message instanceof Talk)  {
			// Received a Talk message
			final Talk talk = (Talk)message;
			String messagePersistentStatus=talk.messagePersistentStatus;
			if(messagePersistentStatus.equalsIgnoreCase("saving")){
				saveMessage(talk);
			}else if(messagePersistentStatus.equalsIgnoreCase("edit")){
				editMessage(talk);
			}else if(messagePersistentStatus.equalsIgnoreCase("delete")){
				deleteMessage(talk);
			}else if(messagePersistentStatus.equalsIgnoreCase("typing")){
				if(talk.role !=null && "USER".equalsIgnoreCase(talk.role.toString())){
					typingMessagenotifyTo(talk.toUserId,talk.userId,talk.role,talk.description);
					}
					else if("GROUP".equalsIgnoreCase(talk.role.toString())){
						typingMessagenotifyAll(talk.toUserId,talk.userId,talk.role,talk.description);
					}
			}else if(messagePersistentStatus.equalsIgnoreCase("like") || messagePersistentStatus.equalsIgnoreCase("unLike")){
				notifyLikeStatus(talk);
			}
		} else if(message instanceof Quit)  {
			// Received a Quit message
			final Quit quit = (Quit)message;
			try{
				notifyStatus("quit", quit.userId, "has left the room");
			}catch (Exception e) {
				// TODO: handle exception
			}
			members.remove(quit.userId);
		} else if(message instanceof Cron)  {
			runCron();
		} else if(message.equals("generalMessages"))  {
			//Logger.debug("generalMessages"+message);
			ChatController.postListOfLeave1EmployeesInGeneral();
			ChatController.postBirthdayWishInGeneral();
			
		}  else {
			unhandled(message);
		}
	}




	public String getJsonMessage(Message mess){
		Message message=ChatController.convertJsonMessage(mess,true);
		return Json.toJson(message).toString();
		
	}
	private void deleteMessage(Talk talk) {
		 Message message1=Message.find.where().eq("randomId",talk.randomId).findUnique();
		// Logger.debug("delete-----------"+message1);
		if(message1 != null && message1.role !=null && "USER".equalsIgnoreCase(message1.role.toString())){
			notifyTo(message1,getJsonMessage(message1),"delete");
		}
		else if(message1!=null && "GROUP".equalsIgnoreCase(message1.role.toString())){
			notifyAll(message1,getJsonMessage(message1),"delete");
		}
	
		if(message1 != null && message1.messageContentType==MessageContentType.FILE || 	message1.messageContentType==MessageContentType.IMAGE || message1.messageContentType == MessageContentType.SNIPPET){
			 UploadFileInfo uploadFileInfo=UploadFileInfo.find.where().eq("message",message1).findList().get(0);
			
			 List<FileComment> FileCommentList=uploadFileInfo.commentList;
			 uploadFileInfo.message=message1;
			 List<Message> MessageList=new ArrayList<Message>();
			 for (FileComment fileComment : FileCommentList) {
				 MessageList.add(fileComment.message);
			}
			if(!MessageList.isEmpty()){
				// Logger.info("upload file deleted with comments id: "+uploadFileInfo.id);
				 Ebean.delete(MessageList);
			}
			Ebean.delete(uploadFileInfo.likeList);
			Ebean.delete(uploadFileInfo);
			 // Ebean.delete(FileCommentList);
			 
			
		}else{
			if(message1.isAttachment){
				List<MessageAttachment> messageAttachmentList =MessageAttachment.find.where().eq("message",message1).findList();
			//	Logger.debug("messageAttachmentList id deleted....."+messageAttachmentList);
				for (MessageAttachment messageAttachment : messageAttachmentList) {
					messageAttachment.delete();
				}
			}
			message1.delete();
		}
	}

	private void editMessage(Talk talk) {
		Message message1=Message.find.where().eq("randomId",talk.randomId).findUnique();
		
		if(message1.role !=null && "USER".equalsIgnoreCase(message1.role.toString())){
		 notifyTo(message1,"","edit");
		}
		else if("GROUP".equalsIgnoreCase(message1.role.toString())){
			notifyAll(message1,"","edit");
		}	
		
	}

	private void saveMessage(Talk talk) {
		UploadFileInfo uploadFileInfo=null;
		String comment=null;
		
		String messageContentType=talk.messageContentType;
		Message message1 = new Message();
		message1.description = talk.description;
		message1.description = message1.description.trim();
		message1.messageBy = AppUser.find.byId(talk.userId);
		message1.randomId = talk.randomId;
	//	message1.randomId = UUID.randomUUID().toString();
		message1.messageContentType=MessageContentType.valueOf(messageContentType);
		if(talk.role.equalsIgnoreCase(String.valueOf(Role.USER))){
			message1.messageTo = AppUser.find.byId(talk.toUserId);
			message1.role = Role.USER;
		 }else if(talk.role.equalsIgnoreCase(String.valueOf(Role.GROUP))){
    		 message1.chatGroup = ChatGroup.find.byId(talk.toUserId);
    		// Logger.debug("chatGroup-------------------------------------------"+ ChatGroup.find.byId(talk.toUserId));
			 message1.role = Role.GROUP;
		}
		
		//before we must write Line()
		Date beforeDate=getDateBeforeMessageSaved(talk.userId,talk.toUserId,talk.role);
		//play.Logger.debug("message before saved.... :"+message1);
		message1.save();
		
		uploadFileInfo=(talk.uploadFileId!=null && !talk.uploadFileId.equalsIgnoreCase(""))?UploadFileInfo.find.byId(Long.parseLong(talk.uploadFileId)):null;
		comment = (talk.comments!=null && !talk.comments.equalsIgnoreCase(""))?talk.comments:null;
		
		if(message1.messageContentType==MessageContentType.COMMENT){
			if(comment != null && comment.trim() !=null  && !comment.trim().isEmpty() ){
				saveComment(comment,message1,uploadFileInfo);
			}
		}else if(message1.messageContentType==MessageContentType.FILE || message1.messageContentType==MessageContentType.SNIPPET || message1.messageContentType==MessageContentType.IMAGE ){
			//Logger.debug("talk.uploadFileId............"+uploadFileInfo);
				uploadFileInfo.message=message1;
				uploadFileInfo.update();
				if(comment != null && comment.trim() !=null && !comment.trim().isEmpty() ){
					saveComment(comment,message1,uploadFileInfo);
				}
		}
		
		if(messageContentType.equalsIgnoreCase(String.valueOf(MessageContentType.GITNOTIFICATION)) && talk.gitNotificationId!=null && !talk.gitNotificationId.equalsIgnoreCase("")&&!talk.gitNotificationId.isEmpty()){
			GitNotification gitNotification = GitNotification.find.byId(Long.parseLong(talk.gitNotificationId));
			gitNotification.message = message1;
			gitNotification.update();
		}
	//	play.Logger.debug("message sucessfully stored  message id :"+message1);
		String messagePage=getResultPage(beforeDate,message1,talk.userId);
		
		if(message1.role !=null && "USER".equalsIgnoreCase(message1.role.toString())){
		 notifyTo(message1,messagePage,"saving");
		}
		else if("GROUP".equalsIgnoreCase(message1.role.toString())){
			saveGroupNotification(message1);
			notifyAll(message1,messagePage,"saving");
		}
	}

	private void saveComment(String comment, Message message1, UploadFileInfo uploadFileInfo) {
		  FileComment fileComment=new FileComment();
		  fileComment.comment=comment;
		  fileComment.uploadFileInfo=uploadFileInfo;
		  fileComment.message=message1;
		  fileComment.commentBy=message1.messageBy;
		  fileComment.save();
	}

	private void notifyStatus(String status, Long userId, String message) throws ClosedChannelException {
		//Logger.debug("appuser"+userId+"-----"+ status +message);
		
		for(WebSocket.Out<JsonNode> channel: members.values()) {
        	final ObjectNode event = Json.newObject();
        	//event.put("kind", "status");
        	event.put("onlineStatus", status);
			event.put("userId",userId);
			 ArrayNode m = event.putArray("members");
	            for(Long u: members.keySet()) {
	                m.add(u);
	            }
			channel.write(event);
        }//for
		
	/*	
		for(WebSocket.Out<JsonNode> channel: members.values()) {
        	final ObjectNode event = Json.newObject();
			//event.put("kind", "message");
			//event.put("direction","messageTo");
			event.put("onlineStatus", status);
			event.put("userId",userId);
            
            ArrayNode m = event.putArray("members");
            for(Long u: members.keySet()) {
                m.add(u);
            }
			try{
				channel.write(event);
			}catch (Exception e) {
				// TODO: handle exception
			}
        }//for
*/		
	}

	private void typingMessagenotifyAll(Long toUserId, Long loginUserId,String role, String description) {
		for(WebSocket.Out<JsonNode> channel: members.values()) {
        	//Logger.debug("each channel group message"+channel);
        	final ObjectNode event = Json.newObject();
		
			event.put("fromUserId",loginUserId);
			if(description!=null && !(description.toString().equalsIgnoreCase(""))){
				event.put("typingUserName",AppUser.find.byId(loginUserId).FullName);
			}else{
				event.put("typingUserName","");
			}
			
			event.put("groupId",toUserId);
			event.put("role",role);
			event.put("messageContentType","TEXT");
			event.put("messagePersistentStatus","typing");
			event.put("description",description);
            
           /* ArrayNode m = event.putArray("members");
            for(Long u: members.keySet()) {
                m.add(u);
            }*/
            channel.write(event);
        }//for
		
	}

	private void typingMessagenotifyTo(Long toUserId, Long loginUserId,String role,String description ) {
		final WebSocket.Out<JsonNode> channel = members.get(toUserId);
		// message.messageTo.id=null it means to user not available(isviewed=flase)
		if(channel!=null){
			//message.messageTo.id!=null means touser is available (must check active clientID)
			final ObjectNode event = Json.newObject();
			event.put("kind", "message");
			event.put("direction", "messageTo");
			event.put("fromUserId",loginUserId);
			event.put("toUserId",toUserId);
			if(description!=null && !(description.toString().equalsIgnoreCase(""))){
				event.put("typingUserName",AppUser.find.byId(loginUserId).FullName);
			}else{
				event.put("typingUserName","");
			}
			
			
			event.put("role",role);
			event.put("messageContentType","TEXT");
			event.put("messagePersistentStatus","typing");
			event.put("description",description);
			channel.write(event);
		}
		
		
	}

	public void runCron(){
		for(final WebSocket.Out<JsonNode> channel: members.values()) {
			final ObjectNode event = Json.newObject();
			event.put("kind", "cron");
			event.put("message", "Cron to keep chat connection alive");
			channel.write(event);
		}
	}
	public void notifyTo(final Message message,String messagePage,String messagePersistentStatus){
		final WebSocket.Out<JsonNode> channel = members.get(message.messageTo.id);
		// message.messageTo.id=null it means to user not available(isviewed=flase)
		if(channel!=null){
			//message.messageTo.id!=null means touser is available (must check active clientID)
			final ObjectNode event = Json.newObject();
			event.put("messageId",message.id);
			event.put("kind", "message");
			event.put("direction", "messageTo");
			event.put("fromUserId",String.valueOf(message.messageBy.id));
			event.put("toUserId",String.valueOf(message.messageTo.id));
			event.put("userName",AppUser.find.byId(message.messageBy.id).userName);
			event.put("role",String.valueOf(message.role.toString()));
			event.put("message", (messagePage!=null && !messagePage.equalsIgnoreCase(""))?messagePage:"");
			event.put("messageContentType",(message.messageContentType!=null && !message.messageContentType.toString().equalsIgnoreCase(""))? message.messageContentType.toString():"");
			event.put("messagePersistentStatus",messagePersistentStatus!=null?messagePersistentStatus : "");
			event.put("messageBody",message.description!=null?message.description :"");
			event.put("randomId",message.randomId);
			event.put("channelName",message.messageBy.userName);
			channel.write(event);
		}
		//play.Logger.debug("myid:"+message.messageBy.id);
		final WebSocket.Out<JsonNode> mychannel = members.get(message.messageBy.id);
		if(mychannel!=null){
			//Logger.debug("message by....----------------------------------------"+messagePage);
			final ObjectNode event = Json.newObject();
			event.put("messageId",message.id);
			event.put("kind", "message");
			event.put("direction", "messageBy");
			event.put("fromUserId",String.valueOf(message.messageBy.id)); //this i think not required
			event.put("toUserId",String.valueOf(message.messageTo.id));
			event.put("userName",AppUser.find.byId(message.messageBy.id).userName);
			event.put("role",String.valueOf(message.role.toString()));
			event.put("message", (messagePage!=null && !messagePage.equalsIgnoreCase(""))?messagePage:"");
			event.put("messageContentType",(message.messageContentType!=null && !message.messageContentType.toString().equalsIgnoreCase(""))? message.messageContentType.toString():"");
			event.put("messagePersistentStatus",messagePersistentStatus!=null?messagePersistentStatus :"");
			event.put("messageBody",message.description!=null?message.description :"");
			event.put("randomId",message.randomId);
			event.put("channelName",message.messageBy.userName);
			mychannel.write(event);
		}
	}
	private JsonNode getMessageCommetnsList(Message message) {
		final ArrayList<String> commentList=new ArrayList<String>();
		final ObjectNode JsonCommentList = Json.newObject();
		List<UploadFileInfo> uploadFileInfoList = UploadFileInfo.find.where().eq("message",message).findList();
		if(uploadFileInfoList!=null && !uploadFileInfoList.isEmpty()){
			UploadFileInfo	uploadFileInfo =	uploadFileInfoList.get(0);
			uploadFileInfo.commentList.forEach(comment -> commentList.add(comment.message.randomId));
			//Logger.info("deleted commentList"+commentList);
			
		}
		return Json.toJson(JsonCommentList);
	}

	public void notifyAll(final Message message,String messagePage,String messagePersistentStatus){
		// Send a Json event to all members
	        for(WebSocket.Out<JsonNode> channel: members.values()) {
	        	//Logger.debug("each channel group message"+channel);
	        	final ObjectNode event = Json.newObject();
				event.put("messageId",message.id);
				event.put("kind", "message");
				event.put("direction", "messageTo");
				event.put("fromUserId",String.valueOf(message.messageBy.id));
				event.put("groupId",String.valueOf(message.chatGroup.id));
				event.put("groupName",ChatGroup.find.byId((message.chatGroup.id)).name);
			    //event.put("toUserId",String.valueOf(message.messageTo.id));
				//event.put("user",AppUser.find.byId(message.messageTo.id).FullName);
				event.put("role",String.valueOf(message.role.toString()));
				event.put("message", messagePage!=null && !messagePage.equalsIgnoreCase("")?messagePage:null);
				event.put("messageContentType",message.messageContentType.toString());
				event.put("messagePersistentStatus",messagePersistentStatus!=null?messagePersistentStatus :"");
				event.put("messageBody",message.description!=null?message.description :"");
				event.put("randomId",message.randomId);
	            ArrayNode m = event.putArray("members");
	            for(Long u: members.keySet()) {
	                m.add(u);
	            }
	            channel.write(event);
	        }//for
	}


	private void notifyLikeStatus(Talk talk) {
		//Logger.debug(" notifyLikeStatus()");
		UploadFileInfo uploadFileInfo = getuploadFileInfo(talk.uploadFileId);
		if(uploadFileInfo != null) {
			FileLike fileLike = getLastLiked(uploadFileInfo, AppUser.find.byId(talk.userId));
			//Logger.debug(" notifyLikeStatus() uploadFileInfo" + uploadFileInfo.id);
			// Logger.debug(" notifyLikeStatus ()
			// fileLike......................"+fileLike.id);

			final ObjectNode event = Json.newObject();
			event.put("likeStatus", talk.messagePersistentStatus);
			event.put("likesCount", uploadFileInfo.likeList.size());
			event.put("fileId", uploadFileInfo.id);
			event.put("eventBy", talk.userId);
			event.put("isLiked", talk.messagePersistentStatus.equalsIgnoreCase("like") ? true : false);
			event.set("lastLiked", Json.toJson(fileLike != null ? fileLike : ""));

			if (talk.role.equalsIgnoreCase("GROUP")) {
				// Send a Json event to all members
				for (WebSocket.Out<JsonNode> channel : members.values()) {
					ArrayNode m = event.putArray("members");
					for (Long u : members.keySet()) {
						m.add(u);
					}
					channel.write(event);
				}
			} else if (talk.role.equalsIgnoreCase("USER")) {
				final WebSocket.Out<JsonNode> channel = members.get(talk.toUserId);
				if (channel != null) {
					channel.write(event);
				}
			}
		}
	}

	
	private FileLike getLastLiked(UploadFileInfo uploadFileInfo,AppUser loggedInUser) {
//		/Logger.info("loggedInUser"+loggedInUser.id);
		FileLike fileLike =null;
		int likesCount = uploadFileInfo.likeList.size();
		//Logger.info("getLastLiked()"+likesCount);
		if(uploadFileInfo.likeList != null && !uploadFileInfo.likeList.isEmpty() && likesCount == 1){
			Query<FileLike> FileLikeQuery = FileLike.find.where().eq("uploadFileInfo",uploadFileInfo).order("createdOn desc").setMaxRows(1);
			fileLike = FileLikeQuery.findList().get(0);
			//Logger.info("if....."+likesCount);
		}else if(uploadFileInfo.likeList != null && !uploadFileInfo.likeList.isEmpty() && likesCount >= 2){
			Query<FileLike> FileLikeQuery = FileLike.find.where().eq("uploadFileInfo",uploadFileInfo).order("createdOn desc").setMaxRows(2);
			fileLike = FileLikeQuery.findList().get(0);
			//Logger.info(fileLike.id+"fileLike.id");
			//Logger.info(fileLike.likeBy.id+"fileLike.likeBy.id");
			//Logger.info(loggedInUser.id+"loggedInUser.id");
			
			if(fileLike.likeBy.id == loggedInUser.id){
				fileLike = FileLikeQuery.findList().get(0);
				//Logger.info("else......"+likesCount);
			}
		}
		return fileLike;
	}




	private  UploadFileInfo getuploadFileInfo(String uploadFileId){
		 UploadFileInfo uploadFileInfo = UploadFileInfo.find.byId(Long.parseLong(uploadFileId));
		return uploadFileInfo;
		 
	}

    public void saveGroupNotification(Message message){
    	ChatGroup chatGroup=ChatGroup.find.byId(message.chatGroup.id);
    	List<AppUser> GroupAppUserList = new ArrayList<AppUser>();
		List<ChatGroupAppUserInfo> groupMembers=ChatGroupAppUserInfo.find.where().eq("chatGroup",chatGroup).findList();
		for (ChatGroupAppUserInfo chatGroupAppUserInfo : groupMembers) {
			GroupAppUserList.add(chatGroupAppUserInfo.appUser);
		}
    	for(AppUser appUser : GroupAppUserList){
    		if(!(appUser.id == message.messageBy.id)){
    		Notification notification = new Notification();
    		notification.messageBy = message.messageBy;
    		notification.messageTo = appUser;
    	    notification.toChatGroup = 	chatGroup;
    		notification.isViewed = false;
    		notification.role = Role.GROUP;
    		notification.message=message;
    		notification.save();
    	}
    	}
    }

	// -- Messages
	public static class Join {
		final Long userId;
		final WebSocket.Out<JsonNode> channel;
		public Join(final Long username1, final WebSocket.Out<JsonNode> channel1) {
			userId = username1;
			channel = channel1;
		}

	}
	public static class Talk {

		final Long userId;
		final String description;
		final Long toUserId;
		final String role;
		final String messageContentType;
		final String uploadFileId; 
		final String comments;
		final String messagePersistentStatus;
		final String randomId;
		final String gitNotificationId;
		
		

		public Talk(final Long username1, final String description1,final Long username2,final String role1,String messageContentType1,String  uploadFile1,String comments1,String messagePersistentStatus1,String randomId1,String gitNotificationId1) {
			userId = username1;
			description = description1;
			toUserId = username2;
			role = role1;
			messageContentType=messageContentType1;
			uploadFileId=uploadFile1;
			comments=comments1;
			messagePersistentStatus=messagePersistentStatus1;
			randomId=randomId1;
			gitNotificationId = gitNotificationId1;
		}

	}
	
	


	public static class Quit {
		final Long userId;
		public Quit(final Long username1) {
			userId = username1;
		}

	}
	
	private String getResultPage(Date beforeDate,Message lastMessage,Long loginAppUser) {
		String returnHtmlPage="";
		Date todayDateWithoutTime=getDateWithoutTime(new Date()) ;
		//Logger.debug(beforeDate+"----**************************8----"+todayDateWithoutTime);
		// compare previousConversationDate and today date .
		if(beforeDate.before(todayDateWithoutTime)){
			//two dates are different means create map with message showMessagePage 
			final ArrayList<Message> messageSet = new ArrayList<Message>();
			LinkedHashMap<Date, List<Message>> dateWiseMessageMap = new LinkedHashMap<Date, List<Message>>();
			messageSet.add(lastMessage);
			try {
				dateWiseMessageMap.put(new SimpleDateFormat("yyyy-MM-dd").parse(lastMessage.createdOn.toString()),messageSet);
				//Logger.debug(dateWiseMessageMap+"getDateWiseMessageMap:ChatRoom");
				
				if(lastMessage.role.equals(Role.GROUP)){
					Query<Message> beforeLastDataBaseMessageQuery=null;
					beforeLastDataBaseMessageQuery=Message.find.where().eq("role",Role.GROUP).order("createdOn desc").setMaxRows(2);
					if(beforeLastDataBaseMessageQuery!=null && beforeLastDataBaseMessageQuery.findList() !=null &&  beforeLastDataBaseMessageQuery.findList().size()>=2 ){
						Message beforeLastDataBaseMessage = beforeLastDataBaseMessageQuery!=null ? beforeLastDataBaseMessageQuery.findList().get(1):null;
						String changeName=AppUser.find.byId(beforeLastDataBaseMessage.messageBy.id).FullName;
						if(!changeName.equals(AppUser.find.byId(lastMessage.messageBy.id).FullName)){
							lastMessage.setUserNameChange(true);
						}
					}else{
						lastMessage.setUserNameChange(true);
					}
				}else if(lastMessage.role.equals(Role.USER)){
					final ExpressionList<Message> userQueryList = Message.find.where().or(Expr.eq("messageTo.id",lastMessage.messageBy.id),Expr.eq("messageBy.id",lastMessage.messageBy.id));
					userQueryList.or(Expr.eq("messageTo.id",lastMessage.messageTo.id ),Expr.eq("messageBy.id",lastMessage.messageTo.id)).eq("role",Role.USER).order("createdOn desc").setMaxRows(2);
				  	        
					if(userQueryList!=null && userQueryList.findList() !=null &&  userQueryList.findList().size()>=2 ){
						Message beforeLastDataBaseMessage = userQueryList!=null ? userQueryList.findList().get(1):null;
						String changeName=AppUser.find.byId(beforeLastDataBaseMessage.messageBy.id).FullName;
						if(!changeName.equals(AppUser.find.byId(lastMessage.messageBy.id).FullName)){
						//	("names or changed..");
							lastMessage.setUserNameChange(true);
						}
					}else{
						lastMessage.setUserNameChange(true);
					}
				}
				//Map<Date, List<Object>> showMessageMap=convertJsonshowMessage(dateWiseMessageMap);
				//returnHtmlPage=Json.toJson(showMessageMap).toString();
				Message message=ChatController.convertJsonMessage(lastMessage,true);
				returnHtmlPage=Json.toJson(message).toString();
				//Logger.debug("returnHtmlPage================================================="+returnHtmlPage);
				//returnHtmlPage = views.html.chat.showMessage.render(dateWiseMessageMap,1l,AppUser.find.byId(loginAppUser)).toString();
			} catch (ParseException e) {
				//Logger.debug("date not parse :getDateWiseMessageMap:ChatRoom");
				e.printStackTrace();
			}
		}else{
			if(lastMessage.role.equals(Role.GROUP)){
				Query<Message> beforeLastDataBaseMessageQuery=null;
				beforeLastDataBaseMessageQuery=Message.find.where().eq("role",Role.GROUP).order("createdOn desc").setMaxRows(2);
				if(beforeLastDataBaseMessageQuery!=null && beforeLastDataBaseMessageQuery.findList() !=null &&  beforeLastDataBaseMessageQuery.findList().size()>=2 ){
					Message beforeLastDataBaseMessage = beforeLastDataBaseMessageQuery!=null ? beforeLastDataBaseMessageQuery.findList().get(1):null;
					String changeName=AppUser.find.byId(beforeLastDataBaseMessage.messageBy.id).FullName;
					if(!changeName.equals(AppUser.find.byId(lastMessage.messageBy.id).FullName)){
						lastMessage.setUserNameChange(true);
					}
				}else{
					lastMessage.setUserNameChange(true);
				}
			}else if(lastMessage.role.equals(Role.USER)){
				final ExpressionList<Message> userQueryList = Message.find.where().or(Expr.eq("messageTo.id",lastMessage.messageBy.id),Expr.eq("messageBy.id",lastMessage.messageBy.id));
				userQueryList.or(Expr.eq("messageTo.id",lastMessage.messageTo.id ),Expr.eq("messageBy.id",lastMessage.messageTo.id)).eq("role",Role.USER).order("createdOn desc").setMaxRows(2);
			  	        
				if(userQueryList!=null && userQueryList.findList() !=null &&  userQueryList.findList().size()>=2 ){
					Message beforeLastDataBaseMessage = userQueryList!=null ? userQueryList.findList().get(1):null;
					String changeName=AppUser.find.byId(beforeLastDataBaseMessage.messageBy.id).FullName;
					//Logger.debug("before userName"+AppUser.find.byId(beforeLastDataBaseMessage.messageBy.id).FullName+"-----------"+AppUser.find.byId(lastMessage.messageBy.id).FullName);
					if(!changeName.equals(AppUser.find.byId(lastMessage.messageBy.id).FullName)){
						//Logger.debug("names or changed..");
						lastMessage.setUserNameChange(true);
					}
				}else{
					//Logger.debug("else");
					lastMessage.setUserNameChange(true);
				}
				
			}
			
		
			Message message=ChatController.convertJsonMessage(lastMessage,true);
			returnHtmlPage=Json.toJson(message).toString();
		}
		
		return returnHtmlPage;
		
	}
	
	
	 public ChatGroup getGroupJsonDetails(ChatGroup chatGroup){
		 ChatGroup chatGroup1=new ChatGroup();
		 chatGroup1.id=chatGroup.id;
		 chatGroup1.name=chatGroup.name;
		 chatGroup1.description=chatGroup.description;
		 return chatGroup1;
	 }
	 
	
	private Date getDateBeforeMessageSaved(Long userId, Long toUserId,String role){
		
		Date previousConversationDateWithoutTime=null;
		if(role != null && "USER".equalsIgnoreCase(role.toString())){
		  Query<Message> fromMessageQuery = Message.find.where().eq("messageBy.id", userId).eq("messageTo.id", toUserId).eq("role",Role.USER).order("createdOn desc").setMaxRows(1);
		  Query<Message> toMessageQuery = Message.find.where().eq("messageBy.id", toUserId).eq("messageTo.id", userId).eq("role",Role.USER).order("createdOn desc").setMaxRows(1);
		
		  
		  Message fromMessage = fromMessageQuery!=null ? fromMessageQuery.findUnique():null;
		  Message toMessage =  toMessageQuery!=null ? toMessageQuery.findUnique():null;
		//  Logger.debug("fromMessage------------->"+fromMessage);
		//  Logger.debug("toMessage--------------->"+toMessage);
		  if(fromMessage!=null || toMessage!=null ) {//means no conversation message between two usres
			Date previousConversationDateWithTime = null;
				Date fromDate = null;
				Date toDate = null;
				if (fromMessage != null) {
					// Logger.debug("fromMessage createdOn"+fromMessage.createdOn);
					toDate = fromMessage.createdOn;
				}
				if (toMessage != null) {
					// Logger.debug("fromMessage createdOn"+toMessage.createdOn);
					fromDate = toMessage.createdOn;
				}
				
				if (fromDate != null && toDate!=null ) {
					if(fromDate.before(toDate)){
						previousConversationDateWithTime = toDate;
					}else{
						previousConversationDateWithTime = fromDate;
					}
				}else{
					if (fromDate != null ) {
						previousConversationDateWithTime = fromDate;
					}
					if (toDate != null ) {
						previousConversationDateWithTime = toDate;
					}
				}

				

			//	Logger.debug("previousConversationDateWithTime------------------------------->"+ previousConversationDateWithTime);
				previousConversationDateWithoutTime = getDateWithoutTime(previousConversationDateWithTime);
			  return previousConversationDateWithoutTime;
		  }else{
			return previousConversationDateWithoutTime=getDateWithoutTime(getYesterdayDate(new Date()));  
		  }
		
		}else 	if(role != null && "GROUP".equalsIgnoreCase(role.toString())){
			 Query<Message> messageQuery = Message.find.where().eq("chatGroup.id",toUserId).eq("role",Role.GROUP).order("createdOn desc").setMaxRows(1);
			 Message message = messageQuery.findUnique();
			// Logger.debug("GROUP messge"+message);;
			 if(message!=null){ //means no conversation message between two Users
			  Date previousConversationDateWithTime = message.createdOn;
			//  Logger.debug(previousConversationDateWithTime+"before convert without time");
			  previousConversationDateWithoutTime =getDateWithoutTime(previousConversationDateWithTime) ;
			//  Logger.debug(previousConversationDateWithoutTime+"previousConversationDateWithoutTime in Group");
			 }else{
				 return previousConversationDateWithoutTime=getDateWithoutTime(getYesterdayDate(new Date()));  
			  }
		}
		return previousConversationDateWithoutTime;
	
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
	public synchronized static void tellRoom(Long username1,String text1,Long username2, String role1, String messageContentType1, String uploadFile1, String comments1, String messagePersistentStatus1,final String randomId,String gitNotificationId){
		Talk talk = new Talk(username1, text1, username2, role1, messageContentType1, uploadFile1, comments1, messagePersistentStatus1,randomId,gitNotificationId);
		defaultRoom.tell(talk, null);
	}
	

}

class chatAppUser  extends play.mvc.Controller {
	public static models.AppUser appUser=Application.getLoggedInUser();
}
