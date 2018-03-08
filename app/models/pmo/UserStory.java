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

import com.avaje.ebean.Model;

@Entity
public class UserStory extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

	@NotNull(message = "A user story must have a name")
	public String name;

	public String description;

	@OneToMany(cascade = CascadeType.ALL)
	public List<PTask> pTask = new ArrayList<PTask>();

	@ManyToOne
	@NotNull(message = "each user story must belong to atleast one epic")
	public Epic epic;

	@ManyToOne
	public Sprint sprint;

	@OneToMany(cascade = CascadeType.ALL)
	public List<TestScenario> testScenarios = new ArrayList<TestScenario>();

	public static Model.Finder<Long, UserStory> find = new Model.Finder<Long, UserStory>(UserStory.class);

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

	public List<PTask> getpTask() {
		return pTask;
	}

	public void setpTask(List<PTask> pTask) {
		this.pTask = pTask;
	}

	public Epic getEpic() {
		return epic;
	}

	public void setEpic(Epic epic) {
		this.epic = epic;
	}

	public Sprint getSprint() {
		return sprint;
	}

	public void setSprint(Sprint sprint) {
		this.sprint = sprint;
	}

	public List<TestScenario> getTestScenarios() {
		return testScenarios;
	}

	public void setTestScenarios(List<TestScenario> testScenarios) {
		this.testScenarios = testScenarios;
	}

	@Override
	public String toString() {
		return "UserStory [id=" + id + ", name=" + name + ", description="
				+ description + ", pTask=" + pTask + ", epic=" + epic
				+ ", sprint=" + sprint + ", testScenarios=" + testScenarios
				+ "]";
	}


}
