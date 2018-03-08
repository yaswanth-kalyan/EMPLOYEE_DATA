package bean;

import java.util.ArrayList;
import java.util.List;

public class MailingDataBean {

	public String SubjectName;
	
	public String BodyName;
	
	public List<String> toMailingList = new ArrayList<String>();

	
	public String getSubjectName() {
		return SubjectName;
	}

	public void setSubjectName(String subjectName) {
		SubjectName = subjectName;
	}

	public String getBodyName() {
		return BodyName;
	}

	public void setBodyName(String bodyName) {
		BodyName = bodyName;
	}

	public List<String> getToMailingList() {
		return toMailingList;
	}

	public void setToMailingList(List<String> toMailingList) {
		this.toMailingList = toMailingList;
	}


	@Override
	public String toString() {
		return "MailingDataBean [SubjectName=" + SubjectName + ", BodyName=" + BodyName + ", toMailingList="
				+ toMailingList.toString() + "]";
	}
	
	
}
