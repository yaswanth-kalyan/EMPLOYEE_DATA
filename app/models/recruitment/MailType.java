package models.recruitment;

import com.avaje.ebean.annotation.EnumValue;

public enum MailType {

	@EnumValue("Intro_Email")
	Intro_Email,
	@EnumValue("Schedule_Email")
	Schedule_Email,
	@EnumValue("Re_Schedule_Email")
	Re_Schedule_Email,@EnumValue("Interview_Schedule_Email")
	Interview_Schedule_Email,
	@EnumValue("Interview_Re_Schedule_Email")
	Interview_Re_Schedule_Email;
}
