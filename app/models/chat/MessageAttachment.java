package models.chat;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import models.BaseEntity;

@Entity
public class MessageAttachment extends BaseEntity {
	
   @Id
   public Long id;
    
   @OneToOne
   public Message message;
   
   @OneToOne
   public Attachment attachment;
   
   
   public static Finder<Long,MessageAttachment> find = new Finder<Long,MessageAttachment>(MessageAttachment.class);

   
}
