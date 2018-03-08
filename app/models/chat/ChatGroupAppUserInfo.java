package models.chat;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import models.AppUser;
import models.BaseEntity;

import com.avaje.ebean.Model;

@Entity
public class ChatGroupAppUserInfo extends BaseEntity {
	
	@Id
	public Long id;
	
	@ManyToOne
	public ChatGroup chatGroup;
	
	@ManyToOne
	public AppUser appUser;
	
	public static Model.Finder<Long, ChatGroupAppUserInfo> find = new Finder<Long, ChatGroupAppUserInfo>(ChatGroupAppUserInfo.class);
	
	
	

}
