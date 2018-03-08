package models.recruitment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;

@Entity
public class RecruitmentInterviewerFeedback extends Model{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long Id;
	@ManyToOne
	public RecruitmentApplicant recruitmentApplicant;
	@ManyToOne
	public RecruitmentSelectionRound recruitmentSelectionRound;
	public String feedBack;
	@Column(columnDefinition="TEXT")
	public String remark;
	@ManyToOne
	public InterviewerAppUser interviewerAppUser;
	public static final Model.Finder<Long, RecruitmentInterviewerFeedback> find=new Model.Finder<Long, RecruitmentInterviewerFeedback>(RecruitmentInterviewerFeedback.class);
}
