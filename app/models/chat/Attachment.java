package models.chat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import models.AppUser;
import models.BaseEntity;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Attachment extends BaseEntity{

		@Id
		@JsonIgnore
	    public Long id;
	    
	    @Lob
	    public byte[] attachmentImage;
	    
	    @Column(columnDefinition = "TEXT")
	    public String description;
	    
	    @Column(columnDefinition = "TEXT")
	    public String title;
	    
	    @Column(columnDefinition = "TEXT")
	    public String url;
	    
	    public AttachmentType attachmentType;;
	    
	    @ManyToOne
	    @JsonIgnore
	    public AppUser appUser;
	    
	    @Column(columnDefinition = "TEXT")
	    public String imageUrl;
	    

		public static Model.Finder<Long,Attachment> find = new Model.Finder<Long,Attachment>(Attachment.class);

}
