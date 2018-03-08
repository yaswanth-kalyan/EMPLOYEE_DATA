package models.leave;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;

import models.AppUser;
import models.BaseEntity;

@Entity
public class Leaves extends BaseEntity{
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;

	public Float addedLeaves = (float) 0;
	
	public Float usedLeaves = (float) 0;
	
	public Float remainingLeaves = (float) 0;
	
	public Date year;

	@ManyToOne
	public LeaveType leaveType;
	
	public LeaveStatus leaveStatus;
	
	@ManyToOne
	public AppUser appUser;
	
	
	
	@ManyToOne
	public AppliedLeaves appliedLeaves;
	
	public static Model.Finder<Long, Leaves> find = new Model.Finder<Long, Leaves>(Leaves.class);

	public static Float totalAddedLeaves(AppUser appUser){
		Float totalAddedLeaves = 0f;
		List<Leaves> leavesList = new ArrayList<Leaves>();
		Date currentDate = new Date();
		@SuppressWarnings("deprecation")
		String lastyear = new SimpleDateFormat("yyyy").format(currentDate);
		try {
			Date startdate = new SimpleDateFormat("yyyy").parse(lastyear);
			leavesList.addAll(Leaves.find.where().eq("appUser", appUser).eq("year", startdate).findList());
			//Logger.debug("addeList >>>>"+leavesList);
			for(Leaves leaves : leavesList){
				totalAddedLeaves += leaves.addedLeaves;
			}
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return totalAddedLeaves;
		
	}
	public static Float totalRemainingLeaves(AppUser appUser){
		Float totalAddedLeaves = 0f;
		List<Leaves> leavesList = new ArrayList<Leaves>();
		Date currentDate = new Date();
		@SuppressWarnings("deprecation")
		String lastyear = new SimpleDateFormat("yyyy").format(currentDate);
		try {
			Date startdate = new SimpleDateFormat("yyyy").parse(lastyear);
			leavesList.addAll(Leaves.find.where().eq("appUser", appUser).eq("year", startdate).findList());
			for(Leaves leaves : leavesList){
				totalAddedLeaves += leaves.remainingLeaves;
			}
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return totalAddedLeaves;
		
	}
}
