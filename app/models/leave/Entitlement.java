package models.leave;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import models.AppUser;
import models.BaseEntity;

import com.avaje.ebean.Model;


@Entity
public class Entitlement extends BaseEntity{

	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;
	
	@ManyToMany
	@JoinTable(name="entitlement_app_user")
	public List<AppUser> appUserList;
	
	@ManyToOne
	public LeaveType leaveType;
	
	public String workedDate;
	
	public Date leavePeriod;
	
	public Float noOfDays;
	
	public static Model.Finder<Long, Entitlement> find = new Model.Finder<Long, Entitlement>(Entitlement.class);
	
	
	public static List<Entitlement> getEntitlementList(){
		return Entitlement.find.where().order("id desc").findList();
	}
}
