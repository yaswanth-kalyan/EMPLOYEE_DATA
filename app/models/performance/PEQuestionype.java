package models.performance;

import com.avaje.ebean.annotation.EnumValue;

public enum PEQuestionype {

	@EnumValue("Rating_Only")
	Rating_Only,
	@EnumValue("Rating_TextBox")
	Rating_TextBox,
	@EnumValue("TextBox_Only")
	TextBox_Only;
	
}
