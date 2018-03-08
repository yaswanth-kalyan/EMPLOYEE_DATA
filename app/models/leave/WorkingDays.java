package models.leave;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import models.BaseEntity;

import com.avaje.ebean.Model;

@Entity
public class WorkingDays extends BaseEntity{
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;
	
	public Days day;
	
	public DaysSection daysSection;
	
	public static Model.Finder<Long, WorkingDays> find = new Model.Finder<Long, WorkingDays>(WorkingDays.class);
	

}
