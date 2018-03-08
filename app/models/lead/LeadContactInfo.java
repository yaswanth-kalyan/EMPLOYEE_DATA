package models.lead;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;

@Entity
public class LeadContactInfo extends Model{
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	@ManyToOne
	public Lead lead;
	
	@ManyToOne
	public CompanyContacts companyContact;
	public String jobTitle;

	@ManyToMany(cascade=CascadeType.ALL)
	@JoinTable(name="Lead_CCInfo")
	public List<Lead> ccInfoList =new ArrayList<Lead>();
	
	@Override
	public String toString() {
		return "LeadContactInfo [id=" + id + ", lead=" 
				+ ", companyContact=" + companyContact + ", jobTitle="
				+ jobTitle + "]";
	}

	public static Model.Finder<Long, LeadContactInfo> find = new Model.Finder<Long, LeadContactInfo>(LeadContactInfo.class);
	
	public static List<LeadContactInfo> getAllLeadContactInfoByLeadId(Long id) {
		List<LeadContactInfo> listLeadContactInfo = new ArrayList<LeadContactInfo>();
		listLeadContactInfo = LeadContactInfo.find.where().eq("lead_id", id).findList();
	return listLeadContactInfo;
	}
}
