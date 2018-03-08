package models;

import java.sql.Timestamp;

import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.CreatedTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;



@MappedSuperclass
public abstract class BaseEntity extends Model {

	@CreatedTimestamp
	public Timestamp createdOn;

	@Version
	@JsonIgnore
	public Timestamp lastUpdate;

}