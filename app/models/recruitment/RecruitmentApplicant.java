package models.recruitment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import models.AppUser;

import com.avaje.ebean.Model;

@Entity
public class RecruitmentApplicant extends Model implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 861850594633286442L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;
	public String applicationId;
	public String applicantName;
	public Long contactNo;
	public String emailId;
	public Date applyDate;
	public Date dob;
	@ManyToOne
	public AppUser referedBy;
	@ManyToOne
	public AppUser createdBy;
	@ManyToOne
	public AppUser updatedBy;
	@ManyToOne
	public RecruitmentCategory applicantCategory;
	@ManyToOne
	public RecruitmentRole recruitmentRole;
	public JobLocation preferedLocation;
	public String currentLocation;
	public String currentCompany;
	@Column(columnDefinition="TEXT")
	public String applicantRemark;
	public Double NoticePeriod;
	public Double CurrentCTC;
	public Double ExpectedCTC;
	public Double exprience;
	@ManyToOne
	public RecruitmentJob recruitmentJob;
	public byte[] resume;
	public String fileName;
	public String fileContentType;
	@ManyToOne
	public RecruitmentSource recruitmentSource;
	public ApplicantStatus status;
	@OneToMany(cascade=CascadeType.ALL)
	public List<RecruitmentSelectionRound> recruitmentSelectionRounds = new ArrayList<RecruitmentSelectionRound>();
	public Boolean sendIntroMailFlag = false;
	public Double getNoticePeriod() {
		return NoticePeriod;
	}
	public void setNoticePeriod(Double noticePeriod) {
		NoticePeriod = noticePeriod;
	}
	public Double getCurrentCTC() {
		return CurrentCTC;
	}
	public void setCurrentCTC(Double currentCTC) {
		CurrentCTC = currentCTC;
	}
	public Double getExpectedCTC() {
		return ExpectedCTC;
	}
	public void setExpectedCTC(Double expectedCTC) {
		ExpectedCTC = expectedCTC;
	}
	public static final Model.Finder<Long,RecruitmentApplicant> find=new Model.Finder<Long,RecruitmentApplicant>(RecruitmentApplicant.class);
	public static Boolean getFileType(Long Id) {
		Boolean flag = false;
		RecruitmentApplicant recruitmentApplicant = RecruitmentApplicant.find.byId(Id);
		String filename = recruitmentApplicant.fileName;
		filename = filename.substring(filename.indexOf("."),filename.length());
		if(filename.equalsIgnoreCase(".pdf")){
			flag = true;
		}
		return flag;
	}
	
	public static List<String> getPreview(){
		String sub = "Interview Invite 898";
		String text = "";
		return null;
		
	}
}
