package models.incident;

import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;

import models.AppUser;
import models.BaseEntity;

@Entity
public class Policy extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	@Column(columnDefinition="TEXT")
	public String policyName;
	
	@Lob
	public byte[] file;
	public String fileName;
	public String fileContentType;
	
	@ManyToOne
	public AppUser appUser;
	
	public static Model.Finder<Long, Policy> find = new Model.Finder<Long, Policy>(Policy.class);
	
	public static List<Policy> getAllIncident(){
		List<Policy> listIncident = Policy.find.all();
		if(listIncident != null && !listIncident.isEmpty()){
			Collections.reverse(listIncident);
		}
		return listIncident;
	}
}
