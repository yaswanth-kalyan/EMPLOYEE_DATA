package models.leave;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.avaje.ebean.annotation.EnumValue;

public enum Days {

	
	@EnumValue("MONDAY")
	MONDAY,
	
	@EnumValue("TUESDAY")
	TUESDAY,
	
	@EnumValue("WEDNESDAY")
	WEDNESDAY,
	
	@EnumValue("THURSDAY")
	THURSDAY,
	
	@EnumValue("FRIDAY")
	FRIDAY,
	
	@EnumValue("SATURDAY")
	SATURDAY,
	
	@EnumValue("SUNDAY")
	SUNDAY;
	
	public String getDays(){
		final String array[] = this.name().toLowerCase().split("_");
		String name="";
		for (final String string : array) {
			name+=string.substring(0,1).toUpperCase()+string.substring(1)+" ";
		}
		return name;
	}
	
	public String getDays1(){
		final String array[] = this.name().toLowerCase().split("_");
		String name="";
		for (final String string : array) {
			name+=string.substring(0,1)+string.substring(1)+" ";
		}
		return name;
	}
	
	public static String getday(){
		SimpleDateFormat sf = new SimpleDateFormat("ddMMyy");
		Date date = new Date();
		
		return sf.format(date);
	}
}
