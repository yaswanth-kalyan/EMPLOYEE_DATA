package models.chat;

import com.avaje.ebean.annotation.EnumValue;

public enum Role {
	
	@EnumValue("USER")
	USER,
	
	@EnumValue("BB8_ADMIN")
	BB8_ADMIN,
	
	@EnumValue("GROUP")
	GROUP;
	
	
}
