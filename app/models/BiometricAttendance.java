package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.avaje.ebean.Model;

@Entity
public class BiometricAttendance extends Model{
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	public Long esslId;
	public Date date;
	public int  statusCode;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getEsslId() {
		return esslId;
	}
	public void setEsslId(Long esslId) {
		this.esslId = esslId;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	
	public static Finder<Long, BiometricAttendance> find=new Model.Finder<Long, BiometricAttendance>(BiometricAttendance.class);
	
	@Override
	public String toString() {
		return "BiometricAttendance [id=" + id + ", esslId=" + esslId
				+ ", date=" + date + ", statusCode=" + statusCode + "]";
	}


}
