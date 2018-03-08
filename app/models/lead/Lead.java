package models.lead;

import java.text.Format;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import models.AppUser;

import com.avaje.ebean.Model;

@Entity
public class Lead extends Model implements Comparable<Lead>{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	@ManyToOne
	public Company company;
	
	@ManyToOne
	public AppUser appUser;
	
	@Column(columnDefinition="TEXT")
	public String opportunityTitle;
	@Column(columnDefinition="TEXT")
	public String opportunityDiscription;
	public LeadSource leadSource;
	public Double estimatedAmount;
	public Date createdOn ;
	public Date lastUpdate ;
	@ManyToOne
	public LeadStatus leadStatus = null;
	
	/*@ManyToMany(cascade=CascadeType.ALL)
	@JoinTable(name="Company_Lead")
	public List<Company> Listcompanys = new ArrayList<Company>();*/

/*	@ManyToMany(cascade=CascadeType.ALL)
	@JoinTable(name="Lead_CCInfo")
	public List<LeadContactInfo> ccInfoList =new ArrayList<LeadContactInfo>();
	*/
	
	@ManyToMany(cascade=CascadeType.ALL)
	@JoinTable(name="Lead_Comment")
	public List<LeadChatComment> comments =new ArrayList<LeadChatComment>();
	
	public static Model.Finder<Long, Lead> find = new Model.Finder<Long, Lead>(Lead.class);
	@Override
	public String toString() {
		return "Lead [id=" + id + ", companyName=" + company
				+ ", opportunityTitle=" + opportunityTitle
				+ ", opportunityDiscription=" + opportunityDiscription
				+ ", leadSource=" + leadSource + ", estimatedAmount="
				+ estimatedAmount + ", leadStatus=" + leadStatus
				 + ", comments=" + comments + "]";
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}
	public String getOpportunityTitle() {
		return opportunityTitle;
	}
	public void setOpportunityTitle(String opportunityTitle) {
		this.opportunityTitle = opportunityTitle;
	}
	public String getOpportunityDiscription() {
		return opportunityDiscription;
	}
	public void setOpportunityDiscription(String opportunityDiscription) {
		this.opportunityDiscription = opportunityDiscription;
	}
	public LeadSource getLeadSource() {
		return leadSource;
	}
	public void setLeadSource(LeadSource leadSource) {
		this.leadSource = leadSource;
	}
	public Double getEstimatedAmount() {
		return estimatedAmount;
	}
	public void setEstimatedAmount(Double estimatedAmount) {
		this.estimatedAmount = estimatedAmount;
	}
	public LeadStatus getLeadStatus() {
		return leadStatus;
	}
	public void setLeadStatus(LeadStatus leadStatus) {
		this.leadStatus = leadStatus;
	}
	public List<LeadChatComment> getComments() {
		return comments;
	}
	public void setComments(List<LeadChatComment> comments) {
		this.comments = comments;
	}
	
	public static Long getLeadsbyComapny(Long id){
		List<Lead> listLeads = new ArrayList<Lead>();
		listLeads = Lead.find.where().eq("company_id", id).findList();
		return (long) listLeads.size();
	}
	public int compareTo(Lead o) {
		// TODO Auto-generated method stub
		return lastUpdate.compareTo(o.lastUpdate);
	}
	

	public static String getIndianCurrencyFormat(Double amount){
		
		Format format = com.ibm.icu.text.NumberFormat.getCurrencyInstance(new Locale("en", "in"));
		String formattedAmount = format.format(amount);
		return formattedAmount;
	}
}
