package models.performance;

import com.avaje.ebean.annotation.EnumValue;

public enum PeQuestionType {

	@EnumValue("Organizational_Core_Competencies")
	Organizational_Core_Competencies,
	@EnumValue("Job_Family_Competencies")
	Job_Family_Competencies,
	@EnumValue("Key_Job_Responsibilities")
	Key_Job_Responsibilities,
	@EnumValue("Goals_And_Projects")
	Goals_And_Projects;
	
}
