package models.pmo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;

import models.BaseEntity;

@Entity
public class TestCase extends BaseEntity {
	

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

	/**
	 * simple/short description of the test case
	 */
	public String name;

	/**
	 * conditions that should be present before executing the test case
	 */
	public String preConditions;

	/**
	 * test case description
	 */
	public String descriptions;

	/**
	 * steps to execute the test case
	 */
	public String steps;

	/**
	 * sample input
	 */
	public String sampleInput;

	/**
	 * expected output
	 */
	public String expectedOutput;
	
	@ManyToOne
	public TestScenario testScenario;
	
	public static Model.Finder<Long, TestCase> find = new Model.Finder<Long, TestCase>(TestCase.class);

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

	public String getPreConditions() {
		return preConditions;
	}

	public void setPreConditions(String preConditions) {
		this.preConditions = preConditions;
	}

	public String getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(String descriptions) {
		this.descriptions = descriptions;
	}

	public String getSteps() {
		return steps;
	}

	public void setSteps(String steps) {
		this.steps = steps;
	}

	public String getSampleInput() {
		return sampleInput;
	}

	public void setSampleInput(String sampleInput) {
		this.sampleInput = sampleInput;
	}

	public String getExpectedOutput() {
		return expectedOutput;
	}

	public void setExpectedOutput(String expectedOutput) {
		this.expectedOutput = expectedOutput;
	}

	public TestScenario getTestScenario() {
		return testScenario;
	}

	public void setTestScenario(TestScenario testScenario) {
		this.testScenario = testScenario;
	}

	@Override
	public String toString() {
		return "TestCase [id=" + id + ", name=" + name + ", preConditions="
				+ preConditions + ", descriptions=" + descriptions + ", steps="
				+ steps + ", sampleInput=" + sampleInput + ", expectedOutput="
				+ expectedOutput + ", testScenario=" + testScenario + "]";
	}
	
	

}
