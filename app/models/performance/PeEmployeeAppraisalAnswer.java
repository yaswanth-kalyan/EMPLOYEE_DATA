package models.performance;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;

import models.BaseEntity;

@Entity
public class PeEmployeeAppraisalAnswer extends BaseEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;
	
	@ManyToOne
	public PeQuestion performanceQuestion;
	public Long rate;
	@Column(columnDefinition="TEXT")
	public String answer;
	
	public static final Model.Finder<Long,PeEmployeeAppraisalAnswer> find=new Model.Finder<>(PeEmployeeAppraisalAnswer.class);
	
}
