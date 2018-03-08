package models.pmo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import models.BaseEntity;

import com.avaje.ebean.Model;
@Entity
public class Sprint extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	public String name;
	
	@OneToMany(cascade = CascadeType.ALL)
	public List<UserStory> userStories = new  ArrayList<UserStory>();
	
	public Date startDate;
	
	public Date endDate;
	
	public static Model.Finder<Long, Sprint> find = new Model.Finder<Long, Sprint>(Sprint.class);

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

	public List<UserStory> getUserStories() {
		return userStories;
	}

	public void setUserStories(List<UserStory> userStories) {
		this.userStories = userStories;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public String toString() {
		return "Sprint [id=" + id + ", name=" + name + ", userStories="
				+ userStories + ", startDate=" + startDate + ", endDate="
				+ endDate + "]";
	}
	
	
	
}
