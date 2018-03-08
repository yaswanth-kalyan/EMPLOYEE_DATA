package models.chat;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import models.AppUser;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.CreatedTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class FileLike extends Model {

  	@Id
    @JsonIgnore
    public Long id;
    
   
    
    @ManyToOne
    public AppUser likeBy;
    
    //@OneToOne(cascade=CascadeType.REMOVE)
    //@JsonIgnore
    //public Message message;
    
    
    @ManyToOne
    @JsonIgnore
    public UploadFileInfo uploadFileInfo;
    
    
    @CreatedTimestamp
	public Timestamp createdOn;
    
    
    
	
	public static Finder<Long,FileLike> find = new Finder<Long,FileLike>(FileLike.class);

}
