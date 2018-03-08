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
public class Tomorrows extends Model implements Comparable<Tomorrows>{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public int id;
	@Column(columnDefinition="TEXT")
	public String tomorrow;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTomorrow() {
		return tomorrow;
	}
	public void setTomorrow(String tomorrow) {
		this.tomorrow = tomorrow;
	}
	@Override
	public String toString() {
		return ""+tomorrow;
	}
	public int compareTo(Tomorrows o) {
		// TODO Auto-generated method stub
		return (int)(this.id-o.id);
	}
	
	public static List<Tomorrows> getList(List<Tomorrows> tomorrows) {
		Collections.sort(tomorrows);
		return tomorrows;
	}
	

}
