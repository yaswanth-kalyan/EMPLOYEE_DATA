package models.lead;

import com.avaje.ebean.annotation.EnumValue;

public enum ContactType {
	@EnumValue("Mobile")
	Mobile,
	@EnumValue("Home")
	Home,
	@EnumValue("Work")
	Work;
}
