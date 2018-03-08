package models.pmo;

import com.avaje.ebean.annotation.EnumValue;

public enum TestResult {

	@EnumValue("P")
	PASS, 
	@EnumValue("F")
	FAIL, 
	@EnumValue("NE")
	NOT_EXECUTED, 
	@EnumValue("B")
	BLOCKED,
	@EnumValue("O")
	OTHER;
}
