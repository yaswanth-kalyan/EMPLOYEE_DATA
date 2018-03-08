package models;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.avaje.ebean.Model;
@Entity
public class TaskStatus extends Model  {
  
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	public String status;
	@Column(columnDefinition="TEXT")
	public String description;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public static final Finder<Long, TaskStatus> find=new Model.Finder<Long, TaskStatus>(TaskStatus.class);
			
		}


