package models;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.avaje.ebean.Model;

@Entity
public class UsersDailyReport extends Model {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long id;
	@Column
	public Date date;
	@Column
	@OneToMany(cascade=CascadeType.ALL)
	public List<Todays> today;
	@Column
	@OneToMany(cascade=CascadeType.ALL)
	public List<Tomorrows> tomorrow;
	@Column
	@OneToMany(cascade=CascadeType.ALL)
	public List<Problems> problem;
	@Column
	public int rate;
	public Boolean isDone;

	public int getRate() {
		return rate;
	}
	public void setRate(int rate) {
		this.rate = rate;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public List<Todays> getToday() {
		return today;
	}
	public void setToday(List<Todays> today) {
		this.today = today;
	}
	public List<Tomorrows> getTomorrow() {
		return tomorrow;
	}
	public void setTomorrow(List<Tomorrows> tomorrow) {
		this.tomorrow = tomorrow;
	}
	public List<Problems> getProblem() {
		return problem;
	}
	public void setProblem(List<Problems> problem) {
		this.problem = problem;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Boolean getIsDone() {
		return isDone;
	}
	public void setIsDone(Boolean isDone) {
		this.isDone = isDone;
	}


	public static Finder<Long, UsersDailyReport> find = new Model.Finder<Long, UsersDailyReport>(UsersDailyReport.class);

}
