package models.pmo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import models.BaseEntity;

import com.avaje.ebean.Model;

@Entity
public class TestExecution extends BaseEntity {
	

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	@OneToOne
	public TestCase testCase;

	public TestResult testResult = TestResult.NOT_EXECUTED;

	public static Model.Finder<Long, TestExecution> find = new Model.Finder<Long, TestExecution>(TestExecution.class);
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TestCase getTestCase() {
		return testCase;
	}

	public void setTestCase(TestCase testCase) {
		this.testCase = testCase;
	}

	public TestResult getTestResult() {
		return testResult;
	}

	public void setTestResult(TestResult testResult) {
		this.testResult = testResult;
	}

	@Override
	public String toString() {
		return "TestExecution [id=" + id + ", testCase=" + testCase
				+ ", testResult=" + testResult + "]";
	}
	
}
