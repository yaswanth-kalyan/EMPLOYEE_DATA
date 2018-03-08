package models;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;

@Entity
public class Timesheet extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	@ManyToOne
	public AppUser appUser;
	
	@ManyToOne
	public Projects project;
	
	public float hours;
	public Date date;
	
	
	public static Finder<Long, Timesheet> find = new Model.Finder<Long,Timesheet>(Timesheet.class);

	@Override
	public String toString() {
		return "Timesheet [id=" + id + ", appUser=" + appUser.FullName + ", project="
				+ project.projectName + ", hours=" + hours + ", date=" + date + "]";
	}
	
	public static String getTodayDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		/*Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);   
		String date = sdf.format(cal.getTime());*/
		String date = sdf.format(new Date());
		return date;
	}
}
