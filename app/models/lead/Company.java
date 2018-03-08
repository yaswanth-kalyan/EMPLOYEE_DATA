package models.lead;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.avaje.ebean.Model;

@Entity
public class Company extends Model implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	@Column(unique=true)
	public String companyName;
	@Column(columnDefinition="TEXT")
	public String address;
	public String website;
	
	/*@ManyToMany
	@JoinTable(name="Company_Lead")
	public List<Lead> leads =new ArrayList<Lead>();*/
	
	/*@ManyToMany(cascade=CascadeType.ALL)
	@JoinTable(name="Company_CCInfo")
	public List<CompanyContactInfo> ccInfoList =new ArrayList<CompanyContactInfo>();
*/

	
	public static Model.Finder<Long, Company> find = new Model.Finder<Long, Company>(Company.class);


	@Override
	public String toString() {
		return "Company [id=" + id + ", companyName=" + companyName
				+ ", address=" + address + ", website=" + website + "]";
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}



	public static List<Company> getAllCompanys(){
		List<Company> listCompanys = new ArrayList<Company>();
		listCompanys = Company.find.all();
		return listCompanys;
	}

}
