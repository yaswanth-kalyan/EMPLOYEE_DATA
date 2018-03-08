package models.leave;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import models.BaseEntity;

import com.avaje.ebean.Model;

@Entity
public class Holidays extends BaseEntity{
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;
	
	public Date year;
	
	public String holidayFor;
	
	public Date holidayDate;
	
	public Boolean Compensatory= false;
	
	public Date correspondingWorkingDay;
	
	public static Model.Finder<Long, Holidays> find = new Model.Finder<Long, Holidays>(Holidays.class);
	

}
