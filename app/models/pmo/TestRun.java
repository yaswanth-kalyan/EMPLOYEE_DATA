package models.pmo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import models.BaseEntity;

import com.avaje.ebean.Model;

@Entity
public class TestRun extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

	//@OneToMany
	public List<TestResult> testResults = new ArrayList<TestResult>();

	public static Model.Finder<Long, TestRun> find = new Model.Finder<Long, TestRun>(TestRun.class);

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<TestResult> getTestResults() {
		return testResults;
	}

	public void setTestResults(List<TestResult> testResults) {
		this.testResults = testResults;
	}

	@Override
	public String toString() {
		return "TestRun [id=" + id + ", testResults=" + testResults + "]";
	}
	
	

}
