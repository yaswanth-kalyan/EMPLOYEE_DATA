package models.recruitment;

import com.avaje.ebean.annotation.EnumValue;

public enum SelectionRoundStatus {
	
	@EnumValue(value="Scheduled")
	Scheduled,
	@EnumValue(value="ReScheduled")
	ReScheduled,
	@EnumValue(value="Cancelled")
	Cancelled,
	@EnumValue(value="Completed")
	Completed;

}
