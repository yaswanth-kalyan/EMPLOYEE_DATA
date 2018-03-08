package models;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;

@Entity
public class TaskComment extends Model implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8L;
	@Id
	public Long id;
	@ManyToOne
	public AppUser appUser;
	@Column(columnDefinition="TEXT")
	public String comment;
	public Date commentDate;
	@ManyToOne
	public Task task;
	@ManyToOne
	public TaskStatus taskStatus;
	
	public Task getTask() {
		return task;
	}


	public void setTask(Task task) {
		this.task = task;
	}


	public TaskStatus getTaskStatus() {
		return taskStatus;
	}


	public void setTaskStatus(TaskStatus taskStatus) {
		this.taskStatus = taskStatus;
	}


	public Date getCommentDate() {
		return commentDate;
	}


	public void setCommentDate(Date commentDate) {
		this.commentDate = commentDate;
	}



	public Long getId() {
		return id;
	}



	public void setId(Long id) {
		this.id = id;
	}



	public AppUser getAppUser() {
		return appUser;
	}



	public void setAppUser(AppUser appUser) {
		this.appUser = appUser;
	}



	public String getComment() {
		return comment;
	}



	public void setComment(String comment) {
		this.comment = comment;
	}



	public static final Finder<Long, TaskComment> find=new Model.Finder<Long, TaskComment>(TaskComment.class);
	



}
