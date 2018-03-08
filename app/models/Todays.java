package models;

import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.avaje.ebean.Model;

@Entity
public class Todays extends Model implements Comparable<Todays>{
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public int id;
	@Column(columnDefinition="TEXT")
	public String today;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getToday() {
		return today;
	}
	public void setToday(String today) {
		this.today = today;
	}
	@Override
	public String toString() {
		return ""+today;
	}
	public int compareTo(Todays o) {
		// TODO Auto-generated method stub
		return (int)(this.id-o.id);
	}
	
	public static List<Todays> getList(List<Todays> today) {
		Collections.sort(today);
		return today;
	}
	
}
