package models.recruitment;

import com.avaje.ebean.annotation.EnumValue;

public enum JobType {
	
	@EnumValue("Full-Time")
	Full_Time,
	@EnumValue("Part-Time")
	Part_Time,
	@EnumValue("Contract")
	Contract;

}
