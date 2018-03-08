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

import models.BaseEntity;

import com.avaje.ebean.Model;

@Entity
public class TestScenario extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	@ManyToOne
	public UserStory userStory;
	
	@OneToMany(cascade = CascadeType.ALL)
	public List<TestCase> testCases = new ArrayList<TestCase>();

	public static Model.Finder<Long, TestScenario> find = new Model.Finder<Long, TestScenario>(TestScenario.class);

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UserStory getUserStory() {
		return userStory;
	}

	public void setUserStory(UserStory userStory) {
		this.userStory = userStory;
	}

	public List<TestCase> getTestCases() {
		return testCases;
	}

	public void setTestCases(List<TestCase> testCases) {
		this.testCases = testCases;
	}

	@Override
	public String toString() {
		return "TestScenario [id=" + id + ", userStory=" + userStory
				+ ", testCases=" + testCases + "]";
	}
	
	

}
