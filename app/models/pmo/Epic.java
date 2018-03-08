package models.pmo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import models.BaseEntity;
import models.Projects;

import org.hibernate.validator.constraints.NotBlank;

import com.avaje.ebean.Model;

@Entity
public class Epic extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

	@NotNull
	@NotBlank
	public String name;

	public String description;

	@ManyToOne
	public RoadMap roadMap;

	@OneToMany(cascade = CascadeType.ALL)
	public List<UserStory> userStories = new ArrayList<UserStory>();

	public static Model.Finder<Long, Epic> find = new Model.Finder<Long, Epic>(Epic.class);

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

	public RoadMap getRoadMap() {
		return roadMap;
	}

	public void setRoadMap(RoadMap roadMap) {
		this.roadMap = roadMap;
	}

	public List<UserStory> getUserStories() {
		return userStories;
	}

	public void setUserStories(List<UserStory> userStories) {
		this.userStories = userStories;
	}

	@Override
	public String toString() {
		return "Epic [id=" + id + ", name=" + name + ", description="
				+ description + ", roadMap=" + roadMap + ", userStories="
				+ userStories + "]";
	}
	
	

}
