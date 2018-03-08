package models.incident;

import com.avaje.ebean.annotation.EnumValue;

public enum IncidentType {

	@EnumValue("HR")
	HR,
	@EnumValue("PMO")
	PMO,
	@EnumValue("Sales")
	Sales,
	@EnumValue("Marketing")
	Marketing,
	@EnumValue("Finance")
	Finance,
	@EnumValue("Operations")
	Operations,
	@EnumValue("Engineer")
	Engineer,
	@EnumValue("Others")
	Others;
}
