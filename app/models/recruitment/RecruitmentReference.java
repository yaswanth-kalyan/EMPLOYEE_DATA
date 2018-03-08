package models.recruitment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;

import models.AppUser;
import models.BaseEntity;

@Entity
public class RecruitmentReference extends BaseEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;
	
	@Column(unique = true)
	public String candidateEmail;
	public String candidateName;
	public Float experience;
	@Lob
	public byte[] resume;
	
	public String resumeName;
	public String resumeContentTyep;

	@ManyToOne
	public AppUser referedBy;
	
	@ManyToOne
	public RecruitmentJob recruitmentJob;
	
	public static Model.Finder<Long, RecruitmentReference> find = new Model.Finder<Long, RecruitmentReference>(RecruitmentReference.class);

	public static Boolean getFileType(Long Id) {
		Boolean flag = false;
		RecruitmentReference recruitmentJob = RecruitmentReference.find.byId(Id);
		String filename = recruitmentJob.resumeName;
		filename = filename.substring(filename.indexOf("."),filename.length());
		if(filename.equalsIgnoreCase(".pdf")){
			flag = true;
		}
		return flag;
	}
	
	public static String getReferenceStatus(Long id){
		String status = "Not Registered";
		RecruitmentReference rr = RecruitmentReference.find.byId(id);
		RecruitmentApplicant ra = RecruitmentApplicant.find.where().eq("emailId", rr.candidateEmail).findUnique();
		if(ra != null){
			status = ra.status.toString();
		}
		return status;
		
	}
}
