package bean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.AppUser;
import models.chat.ChatGroup;
import models.chat.ChatGroupAppUserInfo;
import models.chat.GroupType;
import models.chat.MessageContentType;
import play.Logger;
import actor.ChatRoom;
import controllers.Application;

public class GroupBean implements Serializable {
	private static final long serialVersionUID = 1L;

	public Long id;

	public String name;
	
	public String groupType;

	public String description;

	public Long appUserId;

	public List<String> members;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getMembers() {
		return members;
	}

	public void setMembers(List<String> members) {
		this.members = members;
	}

	// toEntity()
	public ChatGroup toEntity() {
		AppUser loggedInAppUser = Application.getLoggedInUser();
		ChatGroup group = null;
		boolean isSaved =false;
		if (this.id != null) {
			group = ChatGroup.find.byId(id);
			isSaved = true;
		} else {
			group = new ChatGroup();
			group.createdBy = Application.getLoggedInUser();
			//group.save();
			isSaved = false;
		}

		if (this.name != null) {
			group.name = this.name;
		}
		if(this.groupType!=null && !this.groupType.trim().isEmpty()){
			if(this.groupType.equalsIgnoreCase("on")){
				group.groupType = GroupType.PRIVATE;
			}
			if(this.groupType.equalsIgnoreCase("off")){
				group.groupType = GroupType.PUBLIC;
			}
		}
		if (this.description != null) {
			group.description = this.description;
		}
		
		if(isSaved == false){
			group.save();
		}

		if (this.members != null) {
			for (String appUserId : members) {
				AppUser appUser = AppUser.find.byId(Long.parseLong(appUserId));
				ChatGroupAppUserInfo groupMember = new ChatGroupAppUserInfo();
				groupMember.appUser = appUser;
				groupMember.chatGroup = group;
				groupMember.save();
				//group.update();
			}
			if(!(getAppuserIdList(group).contains(loggedInAppUser.id))){
			ChatGroupAppUserInfo groupMember = new ChatGroupAppUserInfo();
			groupMember.appUser = loggedInAppUser;
			groupMember.chatGroup = group;
			groupMember.save();
			//group.update();
			}
		}
		//group.update();
		return group;

	}
	public List<Long> getAppuserIdList(ChatGroup group){
		List<ChatGroupAppUserInfo> groupMembers=ChatGroupAppUserInfo.find.where().eq("chatGroup",group).findList();
		 List<Long> appUserList = new ArrayList<Long>();
		 if(groupMembers.size()>0){
			for (ChatGroupAppUserInfo chatGroupAppUserInfo : groupMembers) {
				    ChatGroupAppUserInfo chatGroupAppUser = ChatGroupAppUserInfo.find.byId(chatGroupAppUserInfo.id);
				    AppUser appUser = AppUser.find.byId(chatGroupAppUserInfo.appUser.id);
				    appUserList.add(appUser.id);
			}
		 }
			return appUserList;
		
	}
	
	/*
	 * public List<ValidationError> validate() {
	 * Logger.info(" in group validation()"); List<ValidationError> errors = new
	 * ArrayList<ValidationError>(); ArrayList<String> groups = new
	 * ArrayList<String>(); if(this.id !=null){
	 * ChatGroup.find.where().eq("isDisabled"
	 * ,false).orderBy("id").findList().stream().filter(Groups -> Groups.id !=
	 * this.id).forEach(Groups -> groups.add(Groups.name.toUpperCase())); }else{
	 * Groups
	 * .find.where().eq("isDisabled",false).orderBy("id").findList().stream
	 * ().forEach(Groups -> groups.add(Groups.name.toUpperCase()));
	 * if(groups.contains(this.name.toUpperCase().trim())){ errors.add(new
	 * ValidationError("name" ,"GroupName "+this.name+" already exists")); } }
	 * return errors.isEmpty() ? null : errors; }
	 */

	public void sendGroupNotification(ChatGroup group) {
		AppUser loggerInUser = Application.getLoggedInUser();
		String contentType ;
		int index =1 ;
		String groupCreatedMessage = "";
		if(this.id!=null){
			contentType = MessageContentType.ADDTOGROUP.toString();
		}else{
			contentType = MessageContentType.CREATEGROUP.toString();
		}
			if(this.members.size()>0){
				try {
			 for(String newUser:this.members){
						Thread.sleep(0,0001);
					
				 AppUser appUser = AppUser.find.byId(Long.parseLong(newUser));
				 String randomId = new SimpleDateFormat("DDMMYYYYhhmmssSSSSSSS").format(new Date());
				 ChatRoom.tellRoom(loggerInUser.id, "you have been added to #"+group.name, appUser.id, "USER", contentType, "", "", "saving",randomId,"");

			 if(index == this.members.size()){
				 groupCreatedMessage = groupCreatedMessage+"@"+appUser.userName;
			 }
			 else if(index<this.members.size()){
				 groupCreatedMessage = groupCreatedMessage+"@"+appUser.userName+"&nbsp;";
				 index++;
			 }else{
				 groupCreatedMessage = groupCreatedMessage+"@"+appUser.userName;
			 	}
			 }
			 Thread.sleep(0,0001);
			 String randomId = new SimpleDateFormat("DDMMYYYYhhmmssSSSSSSS").format(new Date());
			ChatRoom.tellRoom(loggerInUser.id, "added&nbsp;"+groupCreatedMessage,group.id,"GROUP",contentType, "", "", "saving",randomId,"");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	public void sendDeleteNotifcation(List<AppUser> appUsers,String groupName,AppUser admin) {
		AppUser adminAppUser = AppUser.find.byId(admin.id); 
	String contentType ;
	if(appUsers.size()>0){
		contentType = MessageContentType.DELETEGROUP.toString();
		try{
		for(AppUser appUser : appUsers){
			Thread.sleep(0,0001);
			 String randomId = new SimpleDateFormat("DDMMYYYYhhmmssSSSSSSS").format(new Date());
			 ChatRoom.tellRoom(adminAppUser.id, "#"+groupName+" has been deleted by &nbsp;@"+adminAppUser.userName, appUser.id, "USER", contentType, "", "", "saving",randomId,"");
		}
		}catch(Exception e){
			e.printStackTrace();
		}
	  }
	}
	public void sendRenameGroupNotification(ChatGroup chatGroup,String previousName){
		AppUser loggerInUser = Application.getLoggedInUser();
		List<AppUser> appUsers = chatGroup.getAppuserList();
		try{
			String contentType = MessageContentType.RENAMEGROUP.toString();
		if(appUsers.size()>0){
			
			
			for(AppUser appUser : appUsers){
				Thread.sleep(0,0001);
				 String randomId = new SimpleDateFormat("DDMMYYYYhhmmssSSSSSSS").format(new Date());
				 Logger.info("sending message from "+loggerInUser.id+" to "+appUser.id);
				 ChatRoom.tellRoom(loggerInUser.id, "#"+previousName+" renamed as &nbsp;"+chatGroup.name, appUser.id, "USER", contentType, "", "", "saving",randomId,"");
			}
			
		  }
		 Thread.sleep(0,0001);
		 String randomId = new SimpleDateFormat("DDMMYYYYhhmmssSSSSSSS").format(new Date());
		 Logger.info("sending message from "+loggerInUser.id+" to group"+chatGroup.id);
		ChatRoom.tellRoom(loggerInUser.id, "#"+previousName+" renamed as "+chatGroup.name,chatGroup.id,"GROUP",contentType, "", "", "saving",randomId,"");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "GroupBean [id=" + id + ", name=" + name + ", description=" + description + ", members=" + members + "]";
	}
	
	
	
}
