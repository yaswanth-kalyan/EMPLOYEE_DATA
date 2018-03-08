package bean;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import models.AppUser;
import models.chat.ChatGroup;
import models.chat.GitCommit;
import models.chat.GitCommitComment;
import models.chat.GitIssue;
import models.chat.GitIssueType;
import models.chat.GitNotification;
import models.chat.GitNotificationType;
import models.chat.UploadFileInfo;
import play.Logger;
import actor.ChatRoom;

import com.fasterxml.jackson.databind.JsonNode;

public class GitNotificationBean {
	
	public void notifyWhenPush(JsonNode json,ChatGroup chatGroup){
		
		JsonNode commitsList = json.findValue("commits");
		JsonNode ref = json.findPath("ref");
		JsonNode repository = json.findPath("repository");
		//Logger.info("repository "+repository.findPath("name").asText());
		//Logger.info(" branch name "+ref.asText());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssX");
		GitNotification gitNotification = new GitNotification();
		gitNotification.notificationTitle="BB8";
		gitNotification.repositoryBranch=ref.asText().replace("refs/heads/", "");
		gitNotification.repository = json.findPath("repository").findValue("name").asText();
		gitNotification.repositoryUrl = json.findPath("repository").findValue("html_url").asText();
		gitNotification.originJson = json.toString();
		Iterator<JsonNode> i= commitsList.elements();
		while(i.hasNext()){
			GitCommit commit = new GitCommit();
			JsonNode jsonCommit = i.next();
			commit.commitId = jsonCommit.findValue("id").asText();
			commit.message=jsonCommit.findValue("message").asText();
			commit.commitUrl =  jsonCommit.findValue("url").asText();
			String commitTime = jsonCommit.findValue("timestamp").asText();
			commit.committerName=jsonCommit.findValue("committer").findValue("name").asText();
			commit.committerEmail =  jsonCommit.findValue("committer").findValue("email").asText();
			commit.userName =  json.findValue("sender").findValue("login").asText();
			try {
				Logger.info("String date  "+commitTime.replace("T", " ").replace("+", " "));
				commit.committedAt = sdf.parse(commitTime.replace("T", " "));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			gitNotification.commitsList.add(commit);
		}
		gitNotification.noOfCommits = gitNotification.commitsList.size();
		gitNotification.committedBy = json.findValue("pusher").findValue("name").asText();
		gitNotification.pusherEmail = json.findValue("pusher").findValue("email").asText();
		gitNotification.gitNotificationType=GitNotificationType.PUSH;
		gitNotification.save();
		List<AppUser> appUserList = AppUser.find.where().ieq("gitId", "bb8@github").findList();
		String message = gitNotification.gitMessage();
		String randomId = new SimpleDateFormat("DDMMYYYYhhmmssSSSSSSS").format(new Date());
		ChatRoom.tellRoom(appUserList.get(0).id, message, chatGroup.id, "GROUP", "GITNOTIFICATION", "", "", "saving",randomId,gitNotification.id.toString());
		
	 	
	}
	public void notifyWhenIssueTriggered(JsonNode json,ChatGroup chatGroup){
		AppUser appUser = AppUser.find.where().eq("gitId", "bb8@github").findUnique();
		String comment = "";
		String body = "";
		String action = json.findValue("action").textValue();
		if(action.equalsIgnoreCase("opened")||action.equalsIgnoreCase("created")||action.equalsIgnoreCase("closed")||action.equalsIgnoreCase("reopened")||action.equalsIgnoreCase("assigned")){
			GitNotification gitNotification = new GitNotification();
			gitNotification.repository = json.findValue("repository").findValue("name").asText();
			gitNotification.repositoryUrl = json.findPath("repository").findValue("html_url").asText();
			gitNotification.gitNotificationType = GitNotificationType.ISSUES;
			gitNotification.originJson = json.toString();
			gitNotification.save();
			
			GitIssue gitIssue = new GitIssue();
			gitIssue.fullName = json.findValue("repository").findValue("full_name").asText();
			gitIssue.IssueRaisedBy= json.findValue("sender").findValue("login").asText();
			gitIssue.IssueRaisedByUrl = json.findValue("sender").findValue("html_url").asText();
			gitIssue.issueNumber = json.findValue("issue").findValue("number").asText();
			gitIssue.title = json.findValue("issue").findValue("title").asText();
			gitIssue.issueUrl = json.findValue("issue").findValue("html_url").asText();
			if(action.equalsIgnoreCase("opened")){
				body = json.findValue("issue").findValue("body").asText();
				gitIssue.gitIssueType = GitIssueType.OPEN;
				gitIssue.commentedBy = gitIssue.IssueRaisedBy;
			}else if(action.equalsIgnoreCase("created")){
				 body = json.findValue("comment").findValue("body").asText();
				 gitIssue.gitIssueType = GitIssueType.COMMENT;
				 gitIssue.commentedBy = json.findValue("comment").findValue("user").findValue("login").asText();
			}else if(action.equalsIgnoreCase("closed")){
				 //body = json.findValue("comment").findValue("body").asText();
				 gitIssue.gitIssueType = GitIssueType.CLOSE;
				 gitIssue.commentedBy = json.findValue("sender").findValue("login").asText();
			}else if(action.equalsIgnoreCase("reopened")){
				 gitIssue.gitIssueType = GitIssueType.REOPEN;
				 gitIssue.commentedBy = json.findValue("sender").findValue("login").asText();
			}else if(action.equalsIgnoreCase("assigned")){
				 gitIssue.gitIssueType = GitIssueType.ASSIGNED;
				 gitIssue.commentedBy = gitIssue.IssueRaisedBy;
				 gitIssue.assignedTo = json.findValue("assignee").findValue("login").asText();
				 gitIssue.assigneeUrl = json.findValue("assignee").findValue("html_url").asText();
			}
			gitIssue.save();
			if(!body.trim().isEmpty() && !body.equalsIgnoreCase("")){
			HashMap<Object,ArrayList<Object>> issueProperties = this.convertIssueBody(body);
			Logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>    converted issue body --size "+issueProperties.toString());
			if(issueProperties.size()>0){
				comment = issueProperties.get("comment").get(0).toString();
				for(int i=0;i<issueProperties.get("imgName").size();i++){
					Logger.info(" issuepropetirs loop >>>>>>");
					UploadFileInfo fileInfo = new UploadFileInfo();
					fileInfo.appUser=appUser;
					fileInfo.uploadFileName = issueProperties.get("imgName").get(i).toString();
					Logger.info(" file name "+issueProperties.get("imgName").get(i).toString());
					fileInfo.fileUrl = issueProperties.get("url").get(i).toString().trim();
					Logger.info(" file url "+issueProperties.get("url").get(i).toString());
					String url = issueProperties.get("url").get(i).toString().trim();
					fileInfo.uploadFileContentType = url.substring(url.lastIndexOf(".") + 1);
					Logger.info("before saving gatbyteArrayfrom url ");
					fileInfo.uploadImage = this.getbyteArrayFromUrl(url);
					fileInfo.gitIssue = gitIssue;
					fileInfo.save();
					//gitIssue.commentFileList.add(fileInfo);
	
				}
			}else if(action.equalsIgnoreCase("opened")){
				comment = json.findValue("issue").findValue("body").asText();
			}else if(action.equalsIgnoreCase("created")){
				comment = json.findValue("comment").findValue("body").asText();
			}
			if(comment.contains("http")){
				int index = comment.indexOf("http",0);
				String sub = comment.substring(index);
				String s = sub.substring(0, sub.indexOf(" "));
				comment = comment.replace(s, "<a>"+s+"</a>");
			}
			if(comment.contains("\r\n\r\n")){
				comment = comment.replace("\r\n\r\n", "<br>");
			}
			if(comment.contains("\r\n")){
				comment = comment.replace("\r\n", "<br>");
			}
			if(comment.contains("<br><br>")){
				comment = comment.replace("<br><br>", "<br>");
			}
			}
			gitIssue.comment = comment;
			gitIssue.gitNotificationId = gitNotification;
			gitIssue.update();
			Logger.info(">>>>>>>>>>>>>>>>>> gitIssue saved");
			String randomId = new SimpleDateFormat("DDMMYYYYhhmmssSSSSSSS").format(new Date());
			ChatRoom.tellRoom(appUser.id, comment, chatGroup.id, "GROUP", "GITNOTIFICATION", "", "", "saving",randomId,gitNotification.id.toString());
		}
		
	}
	public void notifyWhenGitBranchModified(JsonNode json,ChatGroup chatGroup){
		AppUser appUser = AppUser.find.where().eq("gitId", "bb8@github").findUnique();
		GitNotification gitNotification = new GitNotification();
		gitNotification.repository = json.findPath("repository").findValue("name").asText();
		gitNotification.repositoryBranch = json.findPath("ref").asText().replace("refs/heads/", "");
		gitNotification.repositoryUrl = json.findPath("repository").findValue("html_url").asText();
		gitNotification.originJson = json.toString();
		String created = json.findPath("created").asText();
		String deleted = json.findPath("deleted").asText();
		if(created.trim().equalsIgnoreCase("true")){
			gitNotification.gitNotificationType = GitNotificationType.CREATEBRANCH;
			gitNotification.branchedFrom = json.findPath("repository").findValue("master_branch").asText();
		}
		if(deleted.trim().equalsIgnoreCase("true")){
			gitNotification.gitNotificationType = GitNotificationType.DELETEBRANCH;
		}
		gitNotification.committedBy = json.findPath("pusher").findValue("name").asText();
		gitNotification.pusherEmail = json.findPath("pusher").findValue("email").asText();
		gitNotification.save();
		//Logger.info(">>>>>>>>>>>>>> before tellRoom() >>>>>>>>>");
		String randomId = new SimpleDateFormat("DDMMYYYYhhmmssSSSSSSS").format(new Date());
		ChatRoom.tellRoom(appUser.id, "", chatGroup.id, "GROUP", "GITNOTIFICATION", "", "","saving",randomId,gitNotification.id.toString());
		
		//Logger.info(">>>>>>>>>>>>>> after default.tellRoom called>>>>>>>> ");
	}
	
	
	
	public HashMap<Object,ArrayList<Object>> convertIssueBody(String issueString){
		String str = issueString;
		String findStr = "![";
		String findStr2 = "[";
		String findurl="(http";
		HashMap<Object,ArrayList<Object>> issueDetails = new HashMap<Object,ArrayList<Object>>();
		if(issueString.contains(findStr2) || issueString.contains(findStr) && issueString.contains("]")){
		int lastIndex = 0;
		int lastIndex1 = 0;
		int count = 0;
		int count1=0;
		ArrayList<Object> imageName = new ArrayList<Object>();
		ArrayList<Object> imageUrldetails = new ArrayList<Object>();
		ArrayList<Object> comment = new ArrayList<Object>();
		while(lastIndex1 != -1){
			lastIndex1 = str.indexOf(findurl,lastIndex1);
		    if(lastIndex1 != -1){
		    	String url = str.substring(lastIndex1, str.indexOf(")", lastIndex1)+1);
		    	String imageUrl = url.replace("(", "").replace(")", "");
		    	imageUrldetails.add(imageUrl);
		    	String str1 = str.replace(url, "");
		    	str=str1;
		    	count1++;
		    	lastIndex1 += findurl.length();
		    }
		}
		while(lastIndex != -1){
			if(issueString.contains(findStr)){
	    lastIndex = str.indexOf(findStr,lastIndex);
			}else if(issueString.contains(findStr2)){
				lastIndex = str.indexOf(findStr2,lastIndex);
			}
	    if(lastIndex != -1){
	    	String imagename = str.substring(lastIndex, str.indexOf("]", lastIndex)+1);
	    	String imageName1=null;
	    	if(issueString.contains(findStr)){
	    		imageName1 = imagename.replace(findStr, "").replace("]", "");
	    	}else if(issueString.contains(findStr2)){
	    		 imageName1 = imagename.replace(findStr2, "").replace("]", "");
	    	}
	    	imageName.add(imageName1);
	    	String str1 = str.replace(imagename, "");
	    	str = str1;
	        count ++;
	        if(issueString.contains(findStr)){
	        lastIndex += findStr.length();
	        }else if(issueString.contains(findStr2)){
	        	 lastIndex += findStr2.length();
	        }
	    }
			
	}
		comment.add(str);
		issueDetails.put("imgName", imageName);
		issueDetails.put("url",imageUrldetails);
		issueDetails.put("comment",comment);
	}
		
		return issueDetails;
	}
	public byte[] getbyteArrayFromUrl(String fileUrl){
		InputStream is = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			Logger.info(" in getbyteArrayFromUrl url --> "+fileUrl.trim());
		URL url = new URL(fileUrl.trim());
		  is = url.openStream ();
		  byte[] byteChunk = new byte[1024]; // Or whatever size you want to read in at a time.
		  int n;

		  while ( (n = is.read(byteChunk)) > 0 ) {
		    baos.write(byteChunk, 0, n);
		  }
		}
		catch (IOException e) {
		  System.err.printf ("Failed while reading bytes from %s: %s",  e.getMessage());
		  e.printStackTrace ();
		  // Perform any other exception handling that's appropriate.
		}
		finally {
			try{
		  if (is != null) { is.close(); }
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		//Logger.info("returnnig byte array >>> "+baos.toByteArray());
		return baos.toByteArray();
	}
	public void notifyWhenCommentOnCommmit(JsonNode json,ChatGroup chatGroup){
		AppUser appUser = AppUser.find.where().eq("gitId", "bb8@github").findUnique();
		GitNotification gitNotification = new GitNotification();
		gitNotification.repository = json.findPath("repository").findValue("name").asText();
		gitNotification.repositoryBranch = json.findPath("ref").asText().replace("refs/heads/", "");
		gitNotification.repositoryUrl = json.findPath("repository").findValue("html_url").asText();
		gitNotification.originJson = json.toString();
		gitNotification.save();
		if(json.has("comment")){
			gitNotification.gitNotificationType = GitNotificationType.COMMITCOMMENT;
			GitCommitComment comment = new GitCommitComment();
			comment.full_name = json.findValue("repository").findValue("full_name").asText();
			comment.commitId = json.findValue("comment").findValue("commit_id").asText();
			comment.commentBy = json.findValue("sender").findValue("login").asText();
			comment.commitUrl = json.findValue("comment").findValue("html_url").asText();
			comment.comment = json.findValue("comment").findValue("body").asText();
			comment.gitNotificationId = gitNotification;
			gitNotification.update();
			comment.save();
		}
		String randomId = new SimpleDateFormat("DDMMYYYYhhmmssSSSSSSS").format(new Date());
		ChatRoom.tellRoom(appUser.id, "", chatGroup.id, "GROUP", "GITNOTIFICATION", "", "","saving",randomId,gitNotification.id.toString());
	}
	public void convertCommentBody(String comment){
		
	}
}
