package models;
import com.avaje.ebean.annotation.EnumValue;
public enum AttendenceStatus {
		@EnumValue("Present")
		Present,
		@EnumValue("Absent")
		Absent,
		@EnumValue("WFH")
		WFH,
		@EnumValue("CL")
		CL,
		
	}


