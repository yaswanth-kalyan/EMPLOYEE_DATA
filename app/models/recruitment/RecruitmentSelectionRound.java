package models.recruitment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.avaje.ebean.Model;

@Entity
public class RecruitmentSelectionRound extends Model{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;
	@ManyToOne
	public RecruitmentInterviewType recruitmentInterviewType;
	
	@ManyToOne
	public RecruitmentQuestionTemplate QuestionTemplate;
	
	@ManyToMany(cascade=CascadeType.ALL)
	@JoinTable(name="recruitment_selection_round_interviewer")
	public List<InterviewerAppUser> interviewer=new ArrayList<InterviewerAppUser>();
	
	public Date conductDate;
	public Date toDate;
	
	public byte[] feedback;
	
	@ManyToOne
	public RecruitmentApplicant recruitmentApplicant;
	
	public Integer timeReschedule;
	
	public String remark;
	
	public SelectionRoundStatus selectionStatus;
	
	public InterviewSelectionResult selectionResult;
	
	public InterviewVenue interviewVenue;
	
	@OneToMany(cascade=CascadeType.ALL)
	public List<RecruitmentInterviewerFeedback> recruitmentInterviewerFeedbacks=new ArrayList<RecruitmentInterviewerFeedback>();
	public Boolean sendMailApplicantFlag = false;
	public Boolean sendMailInterviewerFlag = false;
	public Boolean sendNotificationFlag = false;
	
	
	public String candidateCalendarEventId;
	public String interviewerCalendarEventId;
	public String googleDriveFileId;
	
	public static final Model.Finder<Long, RecruitmentSelectionRound> find=new Model.Finder<Long, RecruitmentSelectionRound>(RecruitmentSelectionRound.class);

	public static Map<String,String> getPreview(Long Id){
		Map<String,String> StringList = new LinkedHashMap<String,String>();
		SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy - hh:mm a");
		RecruitmentSelectionRound recruitmentSelectionRound = RecruitmentSelectionRound.find.byId(Id);
		String jobName = "";
		String roleName = "";
		if(recruitmentSelectionRound.recruitmentApplicant.recruitmentJob != null){
			jobName = " - "+recruitmentSelectionRound.recruitmentApplicant.recruitmentJob.recruitmentCategory.jobCategoryName +" - ";
			roleName = recruitmentSelectionRound.recruitmentApplicant.recruitmentJob.recruitmentRole.jobRoleName;
		}
		String sub = "Interview Invite : "+recruitmentSelectionRound.recruitmentApplicant.applicationId+"-"+jobName+"-"+roleName;
		RecruitmentMailContent mailContent = RecruitmentMailContent.find.where().eq("mailType", MailType.Interview_Re_Schedule_Email).findUnique();
		String body = "Hi "+recruitmentSelectionRound.recruitmentApplicant.applicantName +" \n\n"+mailContent.mailContent+" \n\n "
				+ "Candidate Name : "+recruitmentSelectionRound.recruitmentApplicant.applicantName+" \n"
				+ " Job Position : "+recruitmentSelectionRound.recruitmentApplicant.applicantCategory.jobCategoryName+"-"+recruitmentSelectionRound.recruitmentApplicant.recruitmentRole.jobRoleName+" \n "
				+ "Date - Time : "+sf.format(recruitmentSelectionRound.conductDate)+" \n "
				//+ "Time: "+recruitmentSelectionRound.conductDate+" \n\n "
				+ "Venue : "+recruitmentSelectionRound.interviewVenue+" \n "
				+ "Round : "+recruitmentSelectionRound.recruitmentInterviewType.interviewTypeName+" \n\n "
				+ "Thanks & Regards \n HR Team \n "
				+ "Thrymr Software Pvt. Ltd. \n ";
		StringList.put("Subject", sub);
		StringList.put("Body", body);
		return StringList;
		
	}
}
