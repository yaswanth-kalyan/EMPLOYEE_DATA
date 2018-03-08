package models.chat;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import models.AppUser;
import models.BaseEntity;

@Entity
public class Notification extends BaseEntity {
	@Id
	public Long id;
	
	@ManyToOne
	@JoinColumn(nullable=false)
    public AppUser messageBy;
	
	
    @ManyToOne
    public AppUser messageTo ;
    
    @ManyToOne
	public ChatGroup toChatGroup;
    
	public Long count;
	
	public Boolean isViewed=false;
	
	public Role role;
	
	@ManyToOne
	@JoinColumn(nullable=false)
	public Message message;
	
	
	
	
	@Override
	public String toString() {
		return "Notification [id=" + id + ", message=" + message + "]";
	}




	public static Finder<Long,Notification> find = new Finder<Long,Notification>(Notification.class);
	

}
