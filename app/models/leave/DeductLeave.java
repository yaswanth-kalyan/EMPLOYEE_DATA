package models.leave;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;

import models.BaseEntity;

@Entity
public class DeductLeave extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	@ManyToOne
	public LeaveType leaveType;
	
	public Float deductLeaves = 0.0f;
	
	public static Model.Finder<Long, DeductLeave> find = new Model.Finder<Long, DeductLeave>(DeductLeave.class);
}
