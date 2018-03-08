package models.chat;


import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import models.AppUser;
import models.BaseEntity;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class ChatAppUserSettings extends BaseEntity {
	
	@Id
	@JsonIgnore
	public Long id;
	
	@OneToOne(cascade= CascadeType.ALL)
	@JoinColumn(nullable=false,unique=true)
	public AppUser loggedInUser;
	
	@Column(nullable=false)
	public Boolean isEnableDesktopNotfication‎;
	
	@Column(nullable=false)
	public String leftPanelColor;
	
	
	
	
	public static Model.Finder<Long, ChatAppUserSettings> find = new Finder<Long,ChatAppUserSettings>(ChatAppUserSettings.class);
	
	
}


