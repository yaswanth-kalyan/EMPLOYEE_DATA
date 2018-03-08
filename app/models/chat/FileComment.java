
package models.chat;

import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Version;

import models.AppUser;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.CreatedTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
public class FileComment extends Model {
	
	  	@Id
	    @JsonIgnore
	    public Long id;
	    
	    @Column(columnDefinition = "TEXT")
	    public String comment;
	    
	    @ManyToOne
	    @JsonIgnore
	    public UploadFileInfo uploadFileInfo;
	    
	    
	    @ManyToOne
	    public AppUser commentBy;
	    
	    @OneToOne(cascade=CascadeType.REMOVE)
	    @JsonIgnore
	    public Message message;
	    
	    
	    @CreatedTimestamp
		public Timestamp createdOn;

		@Version
		@JsonIgnore
		public Timestamp lastUpdate;
	    
		
		public static Finder<Long,FileComment> find = new Finder<Long,FileComment>(FileComment.class);
}