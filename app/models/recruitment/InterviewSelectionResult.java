package models.recruitment;

import com.avaje.ebean.annotation.EnumValue;

public enum InterviewSelectionResult {
	
	@EnumValue(value="Selected")
	Selected,
	@EnumValue(value="Rejected")
	Rejected,
	@EnumValue(value="NotSure")
	NotSure;
}
