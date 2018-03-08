package models.leave;

import com.avaje.ebean.annotation.EnumValue;

public enum DurationEnum {
	
	@EnumValue("FULL_DAY")
	FULL_DAY,
	
	@EnumValue("HALF_DAY")
	HALF_DAY;
	
	public String getDuration(){
		final String array[] = this.name().toLowerCase().split("_");
		String name="";
		for (final String string : array) {
			name+=string.substring(0,1).toUpperCase()+string.substring(1)+" ";
		}
		return name;
	}

}
