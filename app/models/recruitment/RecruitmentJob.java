package models.recruitment;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import models.AppUser;

import com.avaje.ebean.Model;
@Entity
public class RecruitmentJob extends Model implements Serializable{
	private static final long serialVersionUID = -6583311380243449860L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;
	public String jobId;
	public byte[] jobDescription;
	public String fileName;
	public String fileContentType;
	@ManyToOne
	public RecruitmentCategory recruitmentCategory;
	@ManyToOne
	public RecruitmentRole recruitmentRole;
	public Float jobExperience;
	public Integer noOfOpenning;
	public JobLocation jobLocation;
	public JobType jobType;
	public Date openDate;
	public Date lastDate;
	@ManyToMany
	@JoinTable(name="recruitment_mandatoryskills")
	public List<RecruitmentSkill> mandatorySkills;
	@ManyToMany
	@JoinTable(name="recruitment_desiredskills")
	public List<RecruitmentSkill> desiredSkills;
	public JobStatus jobStatus;
	@Column(columnDefinition="TEXT")
	public String remark;
	@ManyToOne
	public AppUser createdBy;
	
	public static final Finder<Long,RecruitmentJob> find=new Model.Finder<Long,RecruitmentJob>(RecruitmentJob.class);

	public static Boolean getFileType(Long Id) {
		Boolean flag = false;
		RecruitmentJob recruitmentJob = RecruitmentJob.find.byId(Id);
		String filename = recruitmentJob.fileName;
		filename = filename.substring(filename.indexOf("."),filename.length());
		if(filename.equalsIgnoreCase(".pdf")){
			flag = true;
		}
		return flag;
	}
}
