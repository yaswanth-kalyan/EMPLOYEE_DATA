package models.lead;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.avaje.ebean.Model;

@Entity
public class CompanyContacts extends Model implements Comparable<CompanyContacts>{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	public String contactName;
	
	@OneToMany(cascade=CascadeType.ALL)
	public List<ClientContactNo> contactNoList = new ArrayList<ClientContactNo>();
	public String emailID;
	public String location;
	public Date dob;
	public Date anniversaryDate;
	
	/*@ManyToMany
	@JoinTable(name="CCInfo")
	public List<LeadContactInfo> leadContactInfo = new ArrayList<LeadContactInfo>();*/
	public static Model.Finder<Long, CompanyContacts> find = new Model.Finder<Long, CompanyContacts>(CompanyContacts.class);
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	public List<ClientContactNo> getContactNoList() {
		return contactNoList;
	}
	public void setContactNoList(List<ClientContactNo> contactNoList) {
		this.contactNoList = contactNoList;
	}
	public String getEmailID() {
		return emailID;
	}
	public void setEmailID(String emailID) {
		this.emailID = emailID;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public Date getDob() {
		return dob;
	}
	public void setDob(Date dob) {
		this.dob = dob;
	}
	public Date getAnniversaryDate() {
		return anniversaryDate;
	}
	public void setAnniversaryDate(Date anniversaryDate) {
		this.anniversaryDate = anniversaryDate;
	}
	
	public static List<CompanyContacts> getAllContacts(){
		List<CompanyContacts> listContacts = new ArrayList<CompanyContacts>();
		listContacts = CompanyContacts.find.all();
		return listContacts;
	}
	public int compareTo(CompanyContacts o) {
		// TODO Auto-generated method stub
		return (int)(this.id-o.id);
	}
}
