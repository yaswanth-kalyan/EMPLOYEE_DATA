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

import models.chat.ChatGroup;

import com.avaje.ebean.Model;

@Entity
public class Task extends Model implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	@Column(columnDefinition="TEXT", nullable=false)
	public String title;
	
	@Column(columnDefinition="TEXT")
	public String description;
	
	@ManyToOne
	public AppUser assignTo;
	
	@ManyToOne
	public AppUser createdBy;
	
	@ManyToMany
	@JoinTable(name="appUser_Task_AddUser")
	public List<AppUser> assignFrom=new ArrayList<>();
	
	@ManyToMany
	@JoinTable(name="appUser_Task_AllUser")
	public List<AppUser> appUsers=new ArrayList<>();
	
	@ManyToOne
	public Projects project;
	
	@ManyToOne
	public ChatGroup chatGroup;
	
	public Date creationDate;
	
	@ManyToOne
	public TaskStatus status;
	
	@OneToMany(cascade=CascadeType.ALL)
	public List<TaskComment> comments=new ArrayList<TaskComment>();
	
	@ManyToOne
	public TaskList taskList;
	
	public boolean taskMark;
	
	public static final Finder<Long, Task> find=new Model.Finder<Long, Task>(Task.class);

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public AppUser getAssignTo() {
		return assignTo;
	}

	public void setAssignTo(AppUser assignTo) {
		this.assignTo = assignTo;
	}

	public AppUser getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(AppUser createdBy) {
		this.createdBy = createdBy;
	}

	public List<AppUser> getAssignFrom() {
		return assignFrom;
	}

	public void setAssignFrom(List<AppUser> assignFrom) {
		this.assignFrom = assignFrom;
	}

	public List<AppUser> getAppUsers() {
		return appUsers;
	}

	public void setAppUsers(List<AppUser> appUsers) {
		this.appUsers = appUsers;
	}

	public Projects getProject() {
		return project;
	}

	public void setProject(Projects project) {
		this.project = project;
	}

	public ChatGroup getChatGroup() {
		return chatGroup;
	}

	public void setChatGroup(ChatGroup chatGroup) {
		this.chatGroup = chatGroup;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public TaskStatus getStatus() {
		return status;
	}

	public void setStatus(TaskStatus status) {
		this.status = status;
	}

	public List<TaskComment> getComments() {
		return comments;
	}

	public void setComments(List<TaskComment> comments) {
		this.comments = comments;
	}

	public TaskList getTaskList() {
		return taskList;
	}

	public void setTaskList(TaskList taskList) {
		this.taskList = taskList;
	}

	public boolean isTaskMark() {
		return taskMark;
	}

	public void setTaskMark(boolean taskMark) {
		this.taskMark = taskMark;
	}
    	
}
