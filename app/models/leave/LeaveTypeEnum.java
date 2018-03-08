package models.leave;

import com.avaje.ebean.annotation.EnumValue;

public enum LeaveTypeEnum {

	@EnumValue("Earned_Leave")
	Earned_Leave,
	@EnumValue("Casual_Leave")
	Casual_Leave,
	@EnumValue("Compensation_Off")
	Compensation_Off,
	@EnumValue("Paternal_Leave")
	Paternal_Leave,
	@EnumValue("Loss_of_Pay")
	Loss_of_Pay;
	
}
