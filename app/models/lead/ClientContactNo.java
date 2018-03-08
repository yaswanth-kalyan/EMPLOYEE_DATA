package models.lead;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.avaje.ebean.Model;

@Entity
public class ClientContactNo extends Model{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	public ContactType contactType;
	public Long contactNo;
	public String countryCode;
	
	@Override
	public String toString() {
		return "ClientContactMNo [id=" + id + ", contactType=" + contactType
				+ ", contactNo=" + contactNo + "]";
	}


	public static Model.Finder<Long, ClientContactNo> find = new Model.Finder<Long, ClientContactNo>(ClientContactNo.class);
}
