package models;

import com.avaje.ebean.annotation.EnumValue;

public enum UserProjectStatus {

	@EnumValue("Active")
	Active,
	@EnumValue("Inactive")
	Inactive,
	@EnumValue("Completed")
	Completed;
}
