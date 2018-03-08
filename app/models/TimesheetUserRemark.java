package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;

@Entity
public class TimesheetUserRemark extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	@ManyToOne
	public AppUser appUser;
	
	@Column(columnDefinition="TEXT")
	public String remark;
	public Date date;
	
	public static Finder<Long, TimesheetUserRemark> find = new Model.Finder<Long,TimesheetUserRemark>(TimesheetUserRemark.class);

	@Override
	public String toString() {
		return "TimesheetUserRemark [id=" + id + ", appUser=" + appUser
				+ ", remark=" + remark + ", date=" + date + "]";
	}
}
