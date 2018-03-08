package models;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;
@Entity
public class Attendance extends Model{
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	public Date date;
	public AttendenceStatus status;
	public Date inTime = null;
	public Date outTime = null;
	public String spendTime;
    public Date esslIntime;
    public Date esslOuttime;
    public String esslSpendtime;
    public String timeInOffice;
    public String esslBreakTime;
    
    @ManyToOne
    public AppUser appUser;
    
    
	public String getEsslBreakTime() {
		return esslBreakTime;
	}
	public void setEsslBreakTime(String esslBreakTime) {
		this.esslBreakTime = esslBreakTime;
	}
	public String getTimeInOffice() {
		return timeInOffice;
	}
	public void setTimeInOffice(String timeInOffice) {
		this.timeInOffice = timeInOffice;
	}
	public Date getEsslIntime() {
		return esslIntime;
	}
	public void setEsslIntime(Date esslIntime) {
		this.esslIntime = esslIntime;
	}
	public Date getEsslOuttime() {
		return esslOuttime;
	}
	public void setEsslOuttime(Date esslOuttime) {
		this.esslOuttime = esslOuttime;
	}
	public String getEsslSpendtime() {
		return esslSpendtime;
	}
	public void setEsslSpendtime(String esslSpendtime) {
		this.esslSpendtime = esslSpendtime;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public AttendenceStatus getStatus() {
		return status;
	}
	public void setStatus(AttendenceStatus status) {
		this.status = status;
	}
	public Date getInTime() {
		return inTime;
	}
	public void setInTime(Date inTime) {
		this.inTime = inTime;
	}
	public Date getOutTime() {
		return outTime;
	}
	public void setOutTime(Date outTime) {
		this.outTime = outTime;
	}
	public String getSpendTime() {
		return spendTime;
	}
	public void setSpendTime(String spendTime) {
		this.spendTime = spendTime;
	}
	public AppUser getAppUser() {
		return appUser;
	}
	public void setAppUser(AppUser appUser) {
		this.appUser = appUser;
	}

	public static Finder<Long, Attendance> find=new Model.Finder<Long, Attendance>(Attendance.class);
	

}
