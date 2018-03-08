package bean;

import java.util.ArrayList;
import java.util.List;

import models.lead.LeadSource;

public class LeadBean {
	
	public Long leadId;
	public Long companyId;
	public String companyName;
	public String opportunityTitle;
	public String opportunityDiscription;
	public LeadSource leadSource;
	public Double estimatedAmount;
	public Long leadStatusId;
	
	public String type;
	public List<Long> cid = new ArrayList<Long>();
	public List<String> jobTitle = new ArrayList<String>();
	@Override
	public String toString() {
		return "LeadBean [companyId=" + companyId + ", companyName="
				+ companyName + ", opportunityTitle=" + opportunityTitle
				+ ", opportunityDiscription=" + opportunityDiscription
				+ ", leadSource=" + leadSource + ", estimatedAmount="
				+ estimatedAmount + ", leadStatus=" + leadStatusId + ", cid="
				+ cid + ", jobTitle=" + jobTitle + "]";
	}

	
}
