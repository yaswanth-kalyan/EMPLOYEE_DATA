package models.pmo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.avaje.ebean.Model;

import models.BaseEntity;
import models.Projects;

@Entity
public class RoadMap extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

	@NotNull
	@NotBlank
	public String title;

	@Column(columnDefinition = "text")
	private String description;

	@ManyToOne
	@NotNull
	private Projects project;

	@OneToMany(cascade = CascadeType.ALL)
	public List<Epic> epics = new ArrayList<Epic>();

	@OneToMany(cascade = CascadeType.ALL)
	public List<UserStory> userStories = new ArrayList<UserStory>();

	public static Model.Finder<Long, RoadMap> find = new Model.Finder<Long, RoadMap>(RoadMap.class);

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

	public List<Epic> getEpics() {
		return epics;
	}

	public void setEpics(List<Epic> epics) {
		this.epics = epics;
	}

	public List<UserStory> getUserStories() {
		return userStories;
	}

	public void setUserStories(List<UserStory> userStories) {
		this.userStories = userStories;
	}

	@Override
	public String toString() {
		return "RoadMap [id=" + id + ", title=" + title + ", epics=" + epics
				+ ", userStories=" + userStories + "]";
	}

	public Projects getProject() {
		return project;
	}

	public void setProject(Projects project) {
		this.project = project;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
