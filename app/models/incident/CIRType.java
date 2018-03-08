package models.incident;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.avaje.ebean.Model;

import models.BaseEntity;

//@Entity
public class CIRType  extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	public String IncidentType;
	
	public static Model.Finder<Long, CIRType> find = new Model.Finder<Long, CIRType>(CIRType.class);

	public String getIncidentType() {
		return IncidentType;
	}

	public void setIncidentType(String incidentType) {
		IncidentType = incidentType;
	}
	
//	create table cirtype (
//			  id                        bigserial not null,
//			  incident_type             varchar(255),
//			  created_on                timestamp not null,
//			  last_update               timestamp not null,
//			  constraint pk_cirtype primary key (id))
//			;
//
//			INSERT INTO cirtype (incident_type,created_on,last_update) VALUES 
//			('HR','2016-10-07 16:24:17.895','2016-10-07 16:24:17.895'),
//			('PMO','2016-10-07 16:24:17.895','2016-10-07 16:24:17.895'),
//			('Sales','2016-10-07 16:24:17.895','2016-10-07 16:24:17.895'),
//			('Marketing','2016-10-07 16:24:17.895','2016-10-07 16:24:17.895'),
//			('Finance','2016-10-07 16:24:17.895','2016-10-07 16:24:17.895'),
//			('Operations','2016-10-07 16:24:17.895','2016-10-07 16:24:17.895'),
//			('Engineer','2016-10-07 16:24:17.895','2016-10-07 16:24:17.895');
//	
	
}
