package models;

import java.io.Serializable;
import java.util.ArrayList;
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

import com.avaje.ebean.Model;

@Entity
public class TaskList extends Model implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3191373244886699654L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	@Column(unique = true)
	public String taskListName;
	
	@Column(columnDefinition = "TEXT")
	public String description;
	
	@ManyToOne
	public AppUser createdBy;
	
	public Date creationDate;
	
	@OneToMany(cascade = CascadeType.ALL)
	public List<Task> tasks = new ArrayList<Task>();
	
	@ManyToMany
	@JoinTable(name = "taskList_appUsers")
	public List<AppUser> appUsers = new ArrayList<AppUser>();
	
	public static final Model.Finder<Long, TaskList> find=new Model.Finder<Long, TaskList>(TaskList.class);
	
	public AppUser getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(AppUser createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTaskListName() {
		return taskListName;
	}

	public void setTaskListName(String taskListName) {
		this.taskListName = taskListName;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	public List<AppUser> getAppUsers() {
		return appUsers;
	}

	public void setAppUsers(List<AppUser> appUsers) {
		this.appUsers = appUsers;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	

}
