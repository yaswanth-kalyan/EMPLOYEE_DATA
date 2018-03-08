package models.lead;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;

import com.avaje.ebean.annotation.EnumValue;

public enum LeadSource {
	@EnumValue("Cold_Call")
	Cold_Call,
	@EnumValue("Existing_Customer")
	Existing_Customer,
	@EnumValue("Self_Generated")
	Self_Generated,
	@EnumValue("Employee")
	Employee,
	@EnumValue("Partner")
	Partner,
	@EnumValue("Conference")
	Conference,
	@EnumValue("Word_of_Mouth")
	Word_of_Mouth,
	@EnumValue("Reference")
	Reference,
	@EnumValue("Whatsapp_Groups")
	Whatsapp_Groups,
	@EnumValue("Other")
	Other;


	public static Map<String, String> options() {
	    final LinkedHashMap<String, String> vals = new LinkedHashMap<String, String>();
	    for (final LeadSource val : LeadSource.values()) {
	        vals.put(val.toString(), WordUtils.capitalizeFully(val.toString().replaceAll("_", " ")));
	    }
	    return vals;
	}

}
