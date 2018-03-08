package models.chat;

import com.avaje.ebean.annotation.EnumValue;

public enum GroupType {
	@EnumValue("PUBLIC")
	PUBLIC ,
	@EnumValue("PRIVATE")
	PRIVATE
}
