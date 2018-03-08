package models.pmo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import models.BaseEntity;

import org.hibernate.validator.constraints.NotBlank;

import com.avaje.ebean.Model;

@Entity
public class Bug extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

	@NotNull
	@NotBlank
	public String title;

	@Column(columnDefinition = "text")
	public String description;

	public static Model.Finder<Long, Bug> find = new Model.Finder<Long, Bug>(Bug.class);

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


	@Override
	public String toString() {
		return "Bug [id=" + id + ", title=" + title + ", description="
				+ description + ", createdOn=" + createdOn + ", lastUpdate="
				+ lastUpdate + "]";
	}
	
	

}
