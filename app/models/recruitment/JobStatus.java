package models.recruitment;

import com.avaje.ebean.annotation.EnumValue;

public enum JobStatus {
	
	@EnumValue("Open")
	Open,
	@EnumValue("Closed")
	Closed,
	@EnumValue("Defered")
	Defered;
	

}
