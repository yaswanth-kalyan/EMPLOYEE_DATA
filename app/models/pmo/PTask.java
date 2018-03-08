package models.pmo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import models.BaseEntity;

import com.avaje.ebean.Model;

@Entity
public class PTask extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

	/**
	 * name of the task
	 */
	public String name;

	/**
	 * description of the task
	 */
	@Column(columnDefinition = "text")
	public String description;

	/**
	 * estimate about the project in hours
	 */
	@Column(columnDefinition="Decimal(10,2)")
	public Double estimatedTime = new Double(0.00d);

	/**
	 * Actual time taken for completing the task
	 */
	@Column(columnDefinition="Decimal(10,2)")
	public Double actualTime = new Double(0.00d);

	@Temporal(TemporalType.TIMESTAMP)
	public Date plannedStartDate;

	@Temporal(TemporalType.TIMESTAMP)	
	public Date plannedEndDate;

	@Temporal(TemporalType.TIMESTAMP)
	public Date actualStartDate;

	@Temporal(TemporalType.TIMESTAMP)
	public Date actualEndDate;

	@ManyToOne
	public UserStory userStory;

	public static Model.Finder<Long, PTask> find = new Model.Finder<Long, PTask>(PTask.class);

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getEstimatedTime() {
		return estimatedTime;
	}

	public void setEstimatedTime(Double estimatedTime) {
		this.estimatedTime = estimatedTime;
	}

	public Double getActualTime() {
		return actualTime;
	}

	public void setActualTime(Double actualTime) {
		this.actualTime = actualTime;
	}

	public Date getPlannedStartDate() {
		return plannedStartDate;
	}

	public void setPlannedStartDate(Date plannedStartDate) {
		this.plannedStartDate = plannedStartDate;
	}

	public Date getPlannedEndDate() {
		return plannedEndDate;
	}

	public void setPlannedEndDate(Date plannedEndDate) {
		this.plannedEndDate = plannedEndDate;
	}

	public Date getActualStartDate() {
		return actualStartDate;
	}

	public void setActualStartDate(Date actualStartDate) {
		this.actualStartDate = actualStartDate;
	}

	public Date getActualEndDate() {
		return actualEndDate;
	}

	public void setActualEndDate(Date actualEndDate) {
		this.actualEndDate = actualEndDate;
	}

	public UserStory getUserStory() {
		return userStory;
	}

	public void setUserStory(UserStory userStory) {
		this.userStory = userStory;
	}

	@Override
	public String toString() {
		return "PTask [id=" + id + ", name=" + name + ", description="
				+ description + ", estimatedTime=" + estimatedTime
				+ ", actualTime=" + actualTime + ", plannedStartDate="
				+ plannedStartDate + ", plannedEndDate=" + plannedEndDate
				+ ", actualStartDate=" + actualStartDate + ", actualEndDate="
				+ actualEndDate + ", userStory=" + userStory + "]";
	}
	
	
}