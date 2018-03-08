package models.leave;

import com.avaje.ebean.annotation.EnumValue;

public enum PartialDaysEnum {

	@EnumValue("ALL_DAYS")
	ALL_DAYS,
	
	@EnumValue("START_DATE")
	START_DAY,
	
	@EnumValue("END_DATE")
	END_DAY,
	
	@EnumValue("START_AND_END_DATE")
	START_AND_END_DAY;
	
	public String getPartialDays(){
		final String array[] = this.name().toLowerCase().split("_");
		String name="";
		for (final String string : array) {
			name+=string.substring(0,1).toUpperCase()+string.substring(1)+" ";
		}
		return name;
	}
	
}




	