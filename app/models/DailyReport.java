package models;

import java.text.SimpleDateFormat;
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

@Entity
public class DailyReport extends Model{


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public int id;
	@Column(unique=true)
	@ManyToOne
	public AppUser appUser;
	
	@Column
	@OneToMany(cascade=CascadeType.ALL)
	public List<UsersDailyReport> usersDailyReport;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public AppUser getAppUser() {
		return appUser;
	}
	public void setAppUser(AppUser appUser) {
		this.appUser = appUser;
	}
	public List<UsersDailyReport> getUsersDailyReport() {
		return usersDailyReport;
	}
	public void setUsersDailyReport(List<UsersDailyReport> usersDailyReport) {
		this.usersDailyReport = usersDailyReport;
	}

	public static Finder<Long, DailyReport> find = new Model.Finder<Long,DailyReport>(DailyReport.class);
	
	public static Boolean checkDate(Date date) {
		Boolean flag = false;
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String edate = sdf.format(date);
		String tdate = sdf.format(new Date());
		if(edate.equals(tdate)) {
			return true;
		} else {
			return false;
		}
	}
	
	public static String getTodayDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String date = sdf.format(new Date());
		return date;
	}
	public static String getThisYear() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		String date = sdf.format(new Date());
		return date;
	}
	
}
