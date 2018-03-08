package bean;

import java.util.List;

import models.recruitment.InterviewSelectionResult;
import models.recruitment.InterviewVenue;
import models.recruitment.SelectionRoundStatus;

public class AddRoundBean {
	public Long id;
	public Long recruitmentId;
	public Long interviewType;
	public Long questionTemplate;
	public List<Long> interviewer;
	public String interviewConductDate;
	public String remark;
	public SelectionRoundStatus selectionRoundStatus;
	public InterviewSelectionResult interviewSelectionResult;
	public InterviewVenue interviewVenue;
	public List<Long> getInterviewer() {
		return interviewer;
	}
	public void setInterviewer(List<Long> interviewer) {
		this.interviewer = interviewer;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getRecruitmentId() {
		return recruitmentId;
	}
	public void setRecruitmentId(Long recruitmentId) {
		this.recruitmentId = recruitmentId;
	}
	public Long getInterviewType() {
		return interviewType;
	}
	public void setInterviewType(Long interviewType) {
		this.interviewType = interviewType;
	}
	public Long getQuestionTemplate() {
		return questionTemplate;
	}
	public void setQuestionTemplate(Long questionTemplate) {
		this.questionTemplate = questionTemplate;
	}
	public String getInterviewConductDate() {
		return interviewConductDate;
	}
	public void setInterviewConductDate(String interviewConductDate) {
		this.interviewConductDate = interviewConductDate;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	

}
