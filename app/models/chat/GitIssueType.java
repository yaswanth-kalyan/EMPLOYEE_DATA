package models.chat;

import com.avaje.ebean.annotation.EnumValue;

public enum GitIssueType {
	@EnumValue("OPEN")
	OPEN,
	@EnumValue("COMMENT")
	COMMENT,
	@EnumValue("CLOSE")
	CLOSE,
	@EnumValue("REOPEN")
	REOPEN,
	@EnumValue("ASSIGNED")
	ASSIGNED;
	
	

}
