package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import com.avaje.ebean.Model;

@Entity
public class Contact extends Model{
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	public String name;
	public String MoibileNo;
	public String email;
	
	@ManyToMany
	@JoinTable(name="Project_Client")
	public List<Projects> projects;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMoibileNo() {
		return MoibileNo;
	}
	public void setMoibileNo(String moibileNo) {
		MoibileNo = moibileNo;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public List<Projects> getProjects() {
		return projects;
	}
	public void setProjects(List<Projects> projects) {
		this.projects = projects;
	}

	public static Finder<Long, Contact> find = new Model.Finder<>(Long.class,Contact.class);
}
