
package models.chat;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import models.AppUser;
import models.BaseEntity;
import play.Logger;

import com.avaje.ebean.Model;

@Entity
public class ChatGroup extends BaseEntity {

	@Id
	public Long id;
	
	public String description;
	
	public String name;
	
	public GroupType groupType; 
	
	@ManyToOne
	@NotNull
	public AppUser createdBy;
	
	public Boolean isDisabled = false;
	
	@OneToMany(mappedBy="chatGroup")
	public List<Message> messageList = new ArrayList<Message>();
	


	public static Model.Finder<Long, ChatGroup> find = new Finder<Long, ChatGroup>(ChatGroup.class);
	
	public List<AppUser> getAppuserList(){
		List<ChatGroupAppUserInfo> groupMembers=ChatGroupAppUserInfo.find.where().eq("chatGroup",this).findList();
		 List<AppUser> appUserList = new ArrayList<AppUser>();
			for (ChatGroupAppUserInfo chatGroupAppUserInfo : groupMembers) {
				appUserList.add(chatGroupAppUserInfo.appUser);
			}
			//Logger.info(">>>>>>>>>>>>>>> group users "+appUserList.toString());
			return appUserList;
		
	}


	@Override
	public String toString() {
		return "ChatGroup [id=" + id + ", description=" + description
				+ ", name=" + name + ", groupType=" + groupType
				+ ", createdBy=" + createdBy + ", isDisabled=" + isDisabled
				+ "]";
	}
	
}
