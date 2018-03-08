package models.lead;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;

@Entity
public class CompanyContactInfo extends Model{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	@ManyToOne
	public Company Company;
	
	@ManyToOne
	public CompanyContacts companyContacts ;
	public String jobTitle;
	
	/*@ManyToMany(cascade=CascadeType.ALL)
	@JoinTable(name="Company_CCInfo")
	public List<Company> ccInfoList =new ArrayList<Company>();*/
	public Long getId() {
		return id;
	}
	
	public static List<CompanyContactInfo> getCompanyList(Long id){
		List<CompanyContactInfo> listCompanys =new ArrayList<CompanyContactInfo>();
		listCompanys  = CompanyContactInfo.find.where().eq("company_contacts_id",id).findList();
		return listCompanys;
	}
	
	public static Model.Finder<Long, CompanyContactInfo> find = new Model.Finder<Long, CompanyContactInfo>(CompanyContactInfo.class);
	
	public static String getJobTitle(Long cmId,Long cntId){
		String jT=" ";
		if(cmId != null && cntId != null){
			CompanyContactInfo info = CompanyContactInfo.find.where().eq("company_id",cmId).eq("company_Contacts_id", cntId).findUnique();
			if(info != null){
				jT = info.jobTitle;
			}
		}
		return jT;
	}
	
}
