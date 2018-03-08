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
public class Incident extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
//	@ManyToOne
//	public CIRType incidentName;
	
	public IncidentType incidentName;
	
	@Column(columnDefinition="TEXT")
	public String description;
	
	@Lob
	public byte[] image;
	public String imageName;
	public String imageContentType;
	
	@ManyToOne
	public AppUser appUser;
	
	public static Model.Finder<Long, Incident> find = new Model.Finder<Long, Incident>(Incident.class);
	
	public static List<Incident> getAllIncident(){
		List<Incident> listIncident = Incident.find.all();
		if(listIncident != null && !listIncident.isEmpty()){
			Collections.reverse(listIncident);
		}
		return listIncident;
	}
	
}
