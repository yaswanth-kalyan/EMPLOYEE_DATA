package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
public class Role extends Model{
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long id;
	@Column(unique=true)
	public String role;
	
	@ManyToMany
	@JoinTable(name="AppUsers_Role")
	@JsonIgnore
	public List<AppUser> appUser = new ArrayList<AppUser>();
	
	
	public List<AppUser> getAppUsers() {
		return appUser;
	}

	public void setAppUsers(List<AppUser> appUser) {
		this.appUser = appUser;
	}


	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public String getRole() {
		return role;
	}


	public void setRole(String role) {
		this.role = role;
	}


	public static Finder<Long, Role> find = new Model.Finder<Long,Role>(Role.class);
}
