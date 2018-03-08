package models;

import com.avaje.ebean.annotation.EnumValue;

public enum Roles {
	@EnumValue("Admin")
	Admin,
	@EnumValue("Manager")
	Manager,
	@EnumValue("Engineer")
	Engineer,
	@EnumValue("HR")
	HR,
	@EnumValue("Marketing")
	Marketing,
//	@EnumValue("Client")
//	Client,
//	@EnumValue("General")
//	General;
}
