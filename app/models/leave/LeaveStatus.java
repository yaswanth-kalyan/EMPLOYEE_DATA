package models.leave;

import com.avaje.ebean.annotation.EnumValue;

public enum LeaveStatus {

	
	@EnumValue("APPROVED")
	APPROVED,
	
	@EnumValue("APLLIED")
	APLLIED,
	
	@EnumValue("REJECTED")
	REJECTED,
	
	@EnumValue("CANCELLED")
	CANCELLED,
	
	@EnumValue("PENDING_APPROVAL")
	PENDING_APPROVAL,
	
	@EnumValue("NOT_APPLIED")
	NOT_APPLIED,
	
	@EnumValue("TAKEN")
	TAKEN;
	
	public String getLeaveStatus(){
		final String array[] = this.name().toLowerCase().split("_");
		String name="";
		for (final String string : array) {
			name+=string.substring(0,1).toUpperCase()+string.substring(1)+" ";
		}
		return name;
	}
}
