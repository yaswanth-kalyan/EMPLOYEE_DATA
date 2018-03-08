package models.chat;


import com.avaje.ebean.annotation.EnumValue;

public enum MessageContentType {
	
	@EnumValue("TEXT")
	TEXT,
    
    @EnumValue("FILE")
	FILE,
    
    @EnumValue("IMAGE")
	IMAGE,
	
	@EnumValue("URL")
	URL,
	
	@EnumValue("SNIPPET")
	SNIPPET,
	
	@EnumValue("COMMENT")
	COMMENT,
	
	
	@EnumValue("GITNOTIFICATION")
	GITNOTIFICATION,
	
	
	@EnumValue("LEFTGROUP")
	LEFTGROUP,
	@EnumValue("ADDTOGROUP")
	ADDTOGROUP,
	@EnumValue("CREATEGROUP")
	CREATEGROUP,
	@EnumValue("RENAMEGROUP")
	RENAMEGROUP,
	@EnumValue("DELETEGROUP")
	DELETEGROUP,
	
	
	@EnumValue("BIRTHDAY")
	BIRTHDAY,
	@EnumValue("LEAVESTATUS")
	LEAVESTATUS;
	
}
