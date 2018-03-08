package models.recruitment;

import com.avaje.ebean.annotation.EnumValue;

public enum InterviewVenue {

	@EnumValue("Thrymr_Office")
	Thrymr_Office,
	@EnumValue("Skype")
	Skype,
	@EnumValue("Telephone")
	Telephone;
	
	
}
