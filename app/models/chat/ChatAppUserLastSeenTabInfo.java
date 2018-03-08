package models.chat;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import models.AppUser;
import models.BaseEntity;

import com.avaje.ebean.Model;

@Entity
public class ChatAppUserLastSeenTabInfo extends BaseEntity {
	
	@Id
	public Long id;
	
	@OneToOne(cascade= CascadeType.ALL)
	@JoinColumn(nullable=false,unique=true)
	public AppUser loggedInUser;
	
	@Column(nullable=false)  //this column referAppUserid orGroupId
	public Long lastSeenTab;
	
	@Column(nullable=false)
	public Role lastSeenTabRole;
	
	
	@Column  //this column refer previousAppUserid or previousGroupId  
	public Long previousLastSeenTab;
	
	@Column
	public Role previousLastSeenTabRole;
	
	public Date LastSeenDate;
	
	public static Model.Finder<Long, ChatAppUserLastSeenTabInfo> find = new Finder<Long,ChatAppUserLastSeenTabInfo>(ChatAppUserLastSeenTabInfo.class);
	
	@Override
	public String toString() {
		return "ChatAppUserLastSeenTabInfo [id=" + id + ", loggedInUser="
				+ loggedInUser + ", lastSeenTab=" + lastSeenTab
				+ ", lastSeenTabRole=" + lastSeenTabRole
				+ ", previousLastSeenTab=" + previousLastSeenTab
				+ ", previousLastSeenTabRole=" + previousLastSeenTabRole
				+ ", LastSeenDate=" + LastSeenDate + "]";
	}
	
	

	
	

	
}

