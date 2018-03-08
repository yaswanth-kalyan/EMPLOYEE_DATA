package models.chat;

import com.avaje.ebean.annotation.EnumValue;

public enum GitNotificationType {
	
	@EnumValue("COMMITCOMMENT")
	COMMITCOMMENT,
	@EnumValue("DELETEBRANCH")
	DELETEBRANCH,
	@EnumValue("DEPLOYMENTSTATUS")
	DEPLOYMENTSTATUS,
	@EnumValue("GOLLUM")
	GOLLUM,
	@EnumValue("ISSUES")
	ISSUES,
	@EnumValue("PAGEBUILD")
	PAGEBUILD,
	@EnumValue("PULLREQUEST")
	PULLREQUEST,
	@EnumValue("PUSH")
	PUSH,
	@EnumValue("COMMITSTATUS")
	COMMITSTATUS,
	@EnumValue("WATCH")
	WATCH,
	@EnumValue("CREATEBRANCH")
	CREATEBRANCH,
	@EnumValue("DEPLOYMENT")
	DEPLOYMENT,
	@EnumValue("FORK")
	FORK,
	@EnumValue("ISSUECOMMENT")
	ISSUECOMMENT,
	@EnumValue("MEMBER")
	MEMBER,
	@EnumValue("PUBLIC")
	PUBLIC,
	@EnumValue("PULLREQUESTREVIEWCOMMENT")
	PULLREQUESTREVIEWCOMMENT,
	@EnumValue("RELEASE")
	RELEASE,
	@EnumValue("TEAMADDED")
	TEAMADDED;
	
	

	
	
}
