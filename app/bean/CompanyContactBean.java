package bean;

import java.util.ArrayList;
import java.util.List;

import models.lead.ContactType;

public class CompanyContactBean {

	public Long id;
	public String contactName;
	public String emailID;
	public String location;
	public String dob;
	public String anniversaryDate;
	
	public List<ContactType> contactType=new ArrayList<ContactType>();;
	public List<Long> contactNo=new ArrayList<Long>();
	public List<String> countryCode=new ArrayList<String>();
}
