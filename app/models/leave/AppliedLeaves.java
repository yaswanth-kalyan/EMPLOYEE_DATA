package models.leave;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.avaje.ebean.Model;

import models.AppUser;
import models.BaseEntity;
import play.Logger;

@Entity
public class AppliedLeaves extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

	public LeaveStatus leaveStatus;

	@ManyToOne
	public LeaveType leaveType;

	public Date startDate;

	public Date endDate;

	public Float totalLeaves;

	@Column(columnDefinition = "TEXT")
	public String reason;

	@OneToMany(cascade = CascadeType.ALL)
	public List<DateWiseAppliedLeaves> dateLeaves;
	
	@OneToMany(cascade = CascadeType.ALL)
	public List<DeductLeave> deductLeaves;

	@ManyToOne
	public AppUser appUser;
	@ManyToOne
	public AppUser approvedBy;
	
	public Date year;
	
	public String rejectedReason;

	public static Model.Finder<Long, AppliedLeaves> find = new Model.Finder<Long, AppliedLeaves>(AppliedLeaves.class);

	public Date getStartDate() {
		return startDate;
	}

	public static Float takenLeaves(AppUser appUser, LeaveType leaveType) {
		Float totalTaken = 0f;
		Date currentDate = new Date();
		@SuppressWarnings("deprecation")
		String lastyear = new SimpleDateFormat("yyyy").format(currentDate);
		try {
			Date startdate = new SimpleDateFormat("dd-MM-yyyy").parse("01-01-" + lastyear);
			Date endate = new SimpleDateFormat("dd-MM-yyyy").parse("31-12-" + lastyear);
			Calendar cal = Calendar.getInstance();
			Calendar cal1 = Calendar.getInstance();
			cal.setTime(startdate);
			cal.add(Calendar.DATE, -1);
			cal1.setTime(endate);
			cal1.add(Calendar.DATE, 1);
			List<AppliedLeaves> apList = AppliedLeaves.find.where().ge("startDate", cal.getTime())
					.le("endDate", cal1.getTime()).eq("appUser", appUser).eq("leaveType", leaveType)
					.eq("leaveStatus", LeaveStatus.TAKEN).findList();
			for (AppliedLeaves appliedLeaves : apList) {
				totalTaken += appliedLeaves.totalLeaves;
			}
			return totalTaken;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return totalTaken;

	}

	public static Float approvedLeaves(AppUser appUser, LeaveType leaveType) {
		Float totalTaken = 0f;
		Date currentDate = new Date();
		@SuppressWarnings("deprecation")
		String lastyear = new SimpleDateFormat("yyyy").format(currentDate);
		try {
			Date startdate = new SimpleDateFormat("dd-MM-yyyy").parse("01-01-" + lastyear);
			Date endate = new SimpleDateFormat("dd-MM-yyyy").parse("31-12-" + lastyear);
			Calendar cal = Calendar.getInstance();
			Calendar cal1 = Calendar.getInstance();
			cal.setTime(startdate);
			cal.add(Calendar.DATE, -1);
			cal1.setTime(endate);
			cal1.add(Calendar.DATE, 1);
			List<AppliedLeaves> apList = AppliedLeaves.find.where().gt("startDate", cal.getTime())
					.lt("endDate", cal1.getTime()).eq("appUser", appUser).eq("leaveType", leaveType)
					.eq("leaveStatus", LeaveStatus.APPROVED).findList();
			for (AppliedLeaves appliedLeaves : apList) {
				totalTaken += appliedLeaves.totalLeaves;
			}
			return totalTaken;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return totalTaken;

	}

	public static Float appliedLeaves(AppUser appUser, LeaveType leaveType) {
		Float totalTaken = 0f;
		Date currentDate = new Date();
		@SuppressWarnings("deprecation")
		String lastyear = new SimpleDateFormat("yyyy").format(currentDate);
		try {
			Date startdate = new SimpleDateFormat("dd-MM-yyyy").parse("01-01-" + lastyear);
			Date endate = new SimpleDateFormat("dd-MM-yyyy").parse("31-12-" + lastyear);
			Calendar cal = Calendar.getInstance();
			Calendar cal1 = Calendar.getInstance();
			cal.setTime(startdate);
			cal.add(Calendar.DATE, -1);
			cal1.setTime(endate);
			cal1.add(Calendar.DATE, 1);
			List<AppliedLeaves> apList = AppliedLeaves.find.where().ge("startDate", cal.getTime())
					.le("endDate", cal1.getTime()).eq("appUser", appUser).eq("leaveType", leaveType)
					.eq("leaveStatus", LeaveStatus.PENDING_APPROVAL).findList();
			for (AppliedLeaves appliedLeaves : apList) {
				totalTaken += appliedLeaves.totalLeaves;
			}
			return totalTaken;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return totalTaken;

	}

	public static List<AppliedLeaves> takenLeavesList(AppUser appUser, LeaveType leaveType) {
		List<AppliedLeaves> apList = new ArrayList<AppliedLeaves>();
		Date currentDate = new Date();
		@SuppressWarnings("deprecation")
		String lastyear = new SimpleDateFormat("yyyy").format(currentDate);
		try {
			Date startdate = new SimpleDateFormat("dd-MM-yyyy").parse("01-01-" + lastyear);
			Date endate = new SimpleDateFormat("dd-MM-yyyy").parse("31-12-" + lastyear);
			Calendar cal = Calendar.getInstance();
			Calendar cal1 = Calendar.getInstance();
			cal.setTime(startdate);
			cal.add(Calendar.DATE, -1);
			cal1.setTime(endate);
			cal1.add(Calendar.DATE, 1);
			apList = AppliedLeaves.find.where().ge("startDate", cal.getTime()).le("endDate", cal1.getTime())
					.eq("appUser", appUser).eq("leaveType", leaveType).eq("leaveStatus", LeaveStatus.TAKEN).findList();

			return apList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return apList;

	}

	public static List<AppliedLeaves> cacelledLeavesList(AppUser appUser, LeaveType leaveType) {
		List<AppliedLeaves> apList = new ArrayList<AppliedLeaves>();
		Date currentDate = new Date();
		@SuppressWarnings("deprecation")
		String lastyear = new SimpleDateFormat("yyyy").format(currentDate);
		try {
			Date startdate = new SimpleDateFormat("dd-MM-yyyy").parse("01-01-" + lastyear);
			Date endate = new SimpleDateFormat("dd-MM-yyyy").parse("31-12-" + lastyear);
			Calendar cal = Calendar.getInstance();
			Calendar cal1 = Calendar.getInstance();
			cal.setTime(startdate);
			cal.add(Calendar.DATE, -1);
			cal1.setTime(endate);
			cal1.add(Calendar.DATE, 1);
			apList = AppliedLeaves.find.where().gt("startDate", cal.getTime()).lt("endDate", cal1.getTime())
					.eq("appUser", appUser).eq("leaveType", leaveType).eq("leaveStatus", LeaveStatus.CANCELLED)
					.findList();

			return apList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return apList;

	}

	public static List<AppliedLeaves> rejectedLeavesList(AppUser appUser, LeaveType leaveType) {
		List<AppliedLeaves> apList = new ArrayList<AppliedLeaves>();
		Date currentDate = new Date();
		@SuppressWarnings("deprecation")
		String lastyear = new SimpleDateFormat("yyyy").format(currentDate);
		try {
			Date startdate = new SimpleDateFormat("dd-MM-yyyy").parse("01-01-" + lastyear);
			Date endate = new SimpleDateFormat("dd-MM-yyyy").parse("31-12-" + lastyear);
			Calendar cal = Calendar.getInstance();
			Calendar cal1 = Calendar.getInstance();
			cal.setTime(startdate);
			cal.add(Calendar.DATE, -1);
			cal1.setTime(endate);
			cal1.add(Calendar.DATE, 1);
			apList = AppliedLeaves.find.where().gt("startDate", cal.getTime()).lt("endDate", cal1.getTime())
					.eq("appUser", appUser).eq("leaveType", leaveType).eq("leaveStatus", LeaveStatus.REJECTED)
					.findList();

			return apList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return apList;

	}

	public static List<AppliedLeaves> approvedLeavesList(AppUser appUser, LeaveType leaveType) {
		List<AppliedLeaves> apList = new ArrayList<AppliedLeaves>();
		Date currentDate = new Date();
		@SuppressWarnings("deprecation")
		String lastyear = new SimpleDateFormat("yyyy").format(currentDate);
		try {
			Date startdate = new SimpleDateFormat("dd-MM-yyyy").parse("01-01-" + lastyear);
			Date endate = new SimpleDateFormat("dd-MM-yyyy").parse("31-12-" + lastyear);
			Calendar cal = Calendar.getInstance();
			Calendar cal1 = Calendar.getInstance();
			cal.setTime(startdate);
			cal.add(Calendar.DATE, -1);
			cal1.setTime(endate);
			cal1.add(Calendar.DATE, 1);
			apList = AppliedLeaves.find.where().gt("startDate", cal.getTime()).lt("endDate", cal1.getTime())
					.eq("appUser", appUser).eq("leaveType", leaveType).eq("leaveStatus", LeaveStatus.APPROVED)
					.findList();

			return apList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return apList;

	}

	public static List<AppliedLeaves> appliedLeavesList(AppUser appUser, LeaveType leaveType) {
		List<AppliedLeaves> apList = new ArrayList<AppliedLeaves>();
		Date currentDate = new Date();
		@SuppressWarnings("deprecation")
		String lastyear = new SimpleDateFormat("yyyy").format(currentDate);
		try {
			Date startdate = new SimpleDateFormat("dd-MM-yyyy").parse("01-01-" + lastyear);
			Date endate = new SimpleDateFormat("dd-MM-yyyy").parse("31-12-" + lastyear);
			Calendar cal = Calendar.getInstance();
			Calendar cal1 = Calendar.getInstance();
			cal.setTime(startdate);
			cal.add(Calendar.DATE, -1);
			cal1.setTime(endate);
			cal1.add(Calendar.DATE, 1);
			apList = AppliedLeaves.find.where().ge("startDate", cal.getTime()).le("endDate", cal1.getTime())
					.eq("appUser", appUser).eq("leaveType", leaveType).eq("leaveStatus", LeaveStatus.PENDING_APPROVAL)
					.findList();
			return apList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return apList;

	}

	public static Float totalLeaves(AppUser appUser, LeaveStatus leaveStatus) {

		Float totalTaken = 0f;
		Date currentDate = new Date();
		@SuppressWarnings("deprecation")
		String lastyear = new SimpleDateFormat("yyyy").format(currentDate);
		try {
			Date startdate = new SimpleDateFormat("dd-MM-yyyy").parse("01-01-" + lastyear);
			Date endate = new SimpleDateFormat("dd-MM-yyyy").parse("31-12-" + lastyear);
			Calendar cal = Calendar.getInstance();
			Calendar cal1 = Calendar.getInstance();
			cal.setTime(startdate);
			cal.add(Calendar.DATE, -1);
			cal1.setTime(endate);
			cal1.add(Calendar.DATE, 1);
			List<AppliedLeaves> apList = AppliedLeaves.find.where().gt("startDate", cal.getTime()).lt("endDate", cal1.getTime()).eq("appUser", appUser).eq("leaveStatus", leaveStatus).findList();
			for (AppliedLeaves appliedLeaves : apList) {
				totalTaken += appliedLeaves.totalLeaves;
			}
			return totalTaken;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return totalTaken;
	}

	public static Boolean StatusLeave(Long Id) throws ParseException {
		SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy");
		Boolean flag = true;
		AppliedLeaves appliedLeaves = AppliedLeaves.find.byId(Id);
		String ssDate = sf.format(appliedLeaves.getStartDate());
		Date sDate = sf.parse(ssDate);
		String tDate = sf.format(new Date());
		Date todayDate = sf.parse(tDate);
		Logger.debug("sdate" + sDate + "tdate" + todayDate);
		if (sDate.before(todayDate) && !sDate.equals(todayDate)) {
			flag = false;
		}
		return flag;

	}

	@Override
	public String toString() {
		return "AppliedLeaves [id=" + id + ", leaveStatus=" + leaveStatus + ", leaveType=" + leaveType + ", startDate="
				+ startDate + ", endDate=" + endDate + ", totalLeaves=" + totalLeaves + ", reason=" + reason
				+ ", dateLeaves=" + dateLeaves + ", deductLeaves=" + deductLeaves + ", appUser=" + appUser
				+ ", approvedBy=" + approvedBy + ", year=" + year + "]";
	}
	
	
}
