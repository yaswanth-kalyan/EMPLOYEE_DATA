package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import models.pmo.RoadMap;

import com.avaje.ebean.Model;

@Entity
public class Projects extends Model implements Comparable<Projects>{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long id;
	@Column(unique=true)
	public String projectName;
	public String description;
	@Temporal(TemporalType.DATE)
	public Date startedDate;
	@Temporal(TemporalType.DATE)
	public Date endedDate;
	public UserProjectStatus status;
	public String client;
	public String projectLeader;
	@ManyToOne
	public AppUser projectManager;

	
	@OneToMany(cascade=CascadeType.ALL)
	private List<RoadMap> roadMaps = new ArrayList<>();
	
	
	@ManyToMany()
	@JoinTable(name="AppUsers_Projects")
	public List<AppUser> appUser;
	
	@ManyToMany
	@JoinTable(name="Project_Client")
	public List<Contact> clientContect;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getStartedDate() {
		return startedDate;
	}
	public void setStartedDate(Date startedDate) {
		this.startedDate = startedDate;
	}
	public Date getEndedDate() {
		return endedDate;
	}
	public void setEndedDate(Date endedDate) {
		this.endedDate = endedDate;
	}
	public String getClient() {
		return client;
	}
	public void setClient(String client) {
		this.client = client;
	}
	public String getProjectLeader() {
		return projectLeader;
	}
	public void setProjectLeader(String projectLeader) {
		this.projectLeader = projectLeader;
	}
	public List<AppUser> getAppUsers() {
		return appUser;
	}
	public void setAppUsers(List<AppUser> appUser) {
		this.appUser = appUser;
	}
	public List<Contact> getClientContect() {
		return clientContect;
	}
	public void setClientContect(List<Contact> clientContect) {
		this.clientContect = clientContect;
	}
	public UserProjectStatus getStatus() {
		return status;
	}

	public void setStatus(UserProjectStatus status) {
		this.status = status;
	}
	public AppUser getProjectManager() {
		return projectManager;
	}
	public void setProjectManager(AppUser projectManager) {
		this.projectManager = projectManager;
	}
	public List<AppUser> getAppUser() {
		return appUser;
	}
	public void setAppUser(List<AppUser> appUser) {
		this.appUser = appUser;
	}

	public static Finder<Long, Projects> find = new Model.Finder<Long, Projects>(Projects.class);
	
	public static String getProjectName(String pid) {
		long id = Long.parseLong(pid);
		Projects pname = Projects.find.byId(id);
		return pname.getProjectName();
	}

	public int compareTo(Projects o) {
		// TODO Auto-generated method stub
		return (int)(this.id-o.id);
	}
	
	public static List<Projects> getLists(List<Projects> projects) {
		Collections.sort(projects);
		return projects;
	}
	public List<RoadMap> getRoadMaps() {
		return roadMaps;
	}
	public void setRoadMaps(List<RoadMap> roadMaps) {
		this.roadMaps = roadMaps;
	}
}
