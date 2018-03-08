package models.recruitment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.avaje.ebean.Model;

@Entity
public class RecruitmentInterviewType extends Model {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;
	public String interviewTypeName;
	@Column(columnDefinition="TEXT")
	public String description;
	public static final Model.Finder<Long, RecruitmentInterviewType> find=new Model.Finder<Long,RecruitmentInterviewType>(RecruitmentInterviewType.class);

}
