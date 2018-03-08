package models.leave;

import com.avaje.ebean.annotation.EnumValue;

public enum DaysSection {
	
	@EnumValue("FULL_DAY")
	FULL_DAY,
	
	@EnumValue("HALF_DAY")
	HALF_DAY,
	
	@EnumValue("NOT_WORKING")
	NOT_WORKING;

	
	public String getDaysSection(){
		final String array[] = this.name().toLowerCase().split("_");
		String name="";
		for (final String string : array) {
			name+=string.substring(0,1).toUpperCase()+string.substring(1)+" ";
		}
		return name;
	}

}
