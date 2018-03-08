package models.leave;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;

import models.AppUser;
import models.BaseEntity;

@Entity
public class DateWiseAppliedLeaves extends BaseEntity{

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;
	
	public Date leaveDate;
	
	public DurationEnum duEnum;
	
	public AppliedLeaveType appliedLeaveType = AppliedLeaveType.Planned;
	
	@ManyToOne
	public AppUser applyUser;
	
	public static Finder<Long, DateWiseAppliedLeaves> find = new Model.Finder<Long, DateWiseAppliedLeaves>(DateWiseAppliedLeaves.class);
}
