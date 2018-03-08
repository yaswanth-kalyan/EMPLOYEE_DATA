package models.chat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import models.AppUser;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
public class BB8LandingMessage{

		@Id
		@JsonIgnore
	    public Long id;
	    
	    @Column(columnDefinition = "TEXT")
	    public String description;
	    
	    @ManyToOne
	    public AppUser addedBy;
	    

		public static Model.Finder<Long,BB8LandingMessage> find = new Model.Finder<Long,BB8LandingMessage>(BB8LandingMessage.class);

}
