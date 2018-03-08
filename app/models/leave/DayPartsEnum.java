package models.leave;

import com.avaje.ebean.annotation.EnumValue;

public enum DayPartsEnum {
	@EnumValue("MORINING")
	MORINING,
	
	@EnumValue("AFTERNOON")
	AFTERNOON;
	
	public String getDurationParts(){
		final String array[] = this.name().toLowerCase().split("_");
		String name="";
		for (final String string : array) {
			name+=string.substring(0,1).toUpperCase()+string.substring(1)+" ";
		}
		return name;
	}
}
