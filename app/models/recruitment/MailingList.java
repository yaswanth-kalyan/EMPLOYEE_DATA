package models.recruitment;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;

import models.AppUser;
import models.BaseEntity;

@Entity
public class MailingList extends BaseEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;
	
	@ManyToOne
	public AppUser appUser;
	
	public static Model.Finder<Long,MailingList> find=new Model.Finder<Long,MailingList>(MailingList.class);
}
