package models.recruitment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.avaje.ebean.Model;

@Entity
public class RecruitmentQuestionTemplate extends Model {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;
	public String questionTemplateName;
	public byte[] questionTemplate;
	@Column(columnDefinition="TEXT")
	public String description;
	public String filename;
	public String fileContentType;
	public static final Model.Finder<Long, RecruitmentQuestionTemplate> find=new Model.Finder<Long,RecruitmentQuestionTemplate>(RecruitmentQuestionTemplate.class);
	

}
