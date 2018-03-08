package models.lead;

import java.util.List;


public class LeadSummary{
	
	/*@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;*/
	
	//@ManyToOne
	public LeadStatus leadStatus = null;
	
	public int totalStatus;
	public Double totalEstimatedAmount = 0.0d;
	
	//public static Model.Finder<Long, LeadSummary> find = new Model.Finder<Long, LeadSummary>(LeadSummary.class);
	
	public static Double getTotalEstimatedAmount(List<LeadSummary> listLeadSummary){
		Double amount = 0.0d;
		if(!listLeadSummary.isEmpty()) {
			for(LeadSummary leadSummary : listLeadSummary){
				if(leadSummary != null && leadSummary.totalEstimatedAmount != null){
					amount = amount+leadSummary.totalEstimatedAmount;
				}
			}
		}
		return amount;
	}
}
