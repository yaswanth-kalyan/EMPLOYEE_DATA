package models.chat;

import com.avaje.ebean.annotation.EnumValue;
public enum GroupStatus {
	
	@EnumValue("LEFTGROUP")
	LEFTGROUP,
	@EnumValue("CREATEGROUP")
	CREATEGROUP,
	@EnumValue("ADDTOGROUP")
	ADDTOGROUP,
	@EnumValue("RENAMEGROUP")
	RENAMEGROUP,
	@EnumValue("DELETEGROUP")
	DELETEGROUP;

}
