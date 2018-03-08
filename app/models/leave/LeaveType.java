package models.leave;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import models.BaseEntity;

import com.avaje.ebean.Model;

@Entity
public class LeaveType extends BaseEntity{

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;
	
	public String leaveType;
	
	public Boolean carryForward = false;
	
	
	
	public static Model.Finder<Long, LeaveType> find = new Model.Finder<Long, LeaveType>(LeaveType.class);
	
	
	
}
