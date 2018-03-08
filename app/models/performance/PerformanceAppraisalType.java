package models.performance;

import com.avaje.ebean.annotation.EnumValue;

public enum PerformanceAppraisalType {

	@EnumValue("Self_Appraisal")
	Self_Appraisal,
	@EnumValue("Employee_Appraisal")
	Employee_Appraisal;
}
