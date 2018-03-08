
package models.chat;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import models.AppUser;
import models.BaseEntity;

@Entity
//@JsonInclude(Include.NON_NULL)
public class Message extends BaseEntity {
	
    @Id
    public Long id;

    @JsonIgnore
    public String title;

    @Column(columnDefinition = "TEXT")
    public String description;

    @ManyToOne
    @JoinColumn(nullable=false)
    public AppUser messageBy;
    
    @ManyToOne
    @JsonIgnore
    public AppUser messageTo ;
    
    public Boolean isViewd=false;
    
    public MessageContentType messageContentType;
    
    public Role role;
    
    @Column(columnDefinition = "TEXT")
    @JsonIgnore
    public String comments;
   
    public Boolean isAttachment=false;
    
	public String randomId;
   
    @ManyToOne
    @JsonIgnore
	public ChatGroup chatGroup;
    
	@OneToMany(cascade=CascadeType.ALL)
	@JsonIgnore
    public List<Notification> notificationList=new ArrayList<Notification>();
    
    	
    @Transient
    public  boolean   isUserNameChange;
    
    @Transient
    public  List<Attachment> attachmentList=new ArrayList<Attachment>();

	public boolean isUserNameChange() {
		return isUserNameChange;
	}

	public void setUserNameChange(boolean isUserNameChange) {
		this.isUserNameChange = isUserNameChange;
	}
	
	@Transient
	public UploadFileInfo uploadFile;
	

	
	@Transient
	public GitNotification  gitNotification;
	

	@Transient
	public GitIssue  gitIssue;

	public static Finder<Long,Message> find = new Finder<Long,Message>(Message.class);
	public static Finder<String,Message> find1 = new Finder<String,Message>(Message.class);

	
	
	

	
	
	
	
	
    
   
}
