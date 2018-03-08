package models.recruitment;

import com.avaje.ebean.annotation.EnumValue;

public enum ApplicantStatus {
	
	@EnumValue("Registered")
	Registered,
	@EnumValue("Shortlisted")
	Shortlisted,
	@EnumValue("Abandoned")
	Abandoned,
	@EnumValue("Selected")
	Selected,
	@EnumValue("Rejected")
	Rejected,
	@EnumValue("Offered")
	Offered,
	@EnumValue("Offered_Accepted")
	Offered_Accepted,
	@EnumValue("Joined")
	Joined,
	@EnumValue("NotJoined")
	NotJoined;
	

}
