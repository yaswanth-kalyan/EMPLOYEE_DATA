package models.leave;

import com.avaje.ebean.annotation.EnumValue;

public enum AppliedLeaveType {

	@EnumValue("Planned")
	Planned,
	
	@EnumValue("Unplanned")
	Unplanned,
}
