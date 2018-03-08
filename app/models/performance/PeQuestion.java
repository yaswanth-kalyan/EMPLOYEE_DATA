package models.performance;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import models.BaseEntity;
import models.UserProjectStatus;
import play.Logger;

import com.avaje.ebean.Model;

@Entity
public class PeQuestion extends BaseEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;
	
	@Column(columnDefinition="TEXT")
	public String question;
	@Column(columnDefinition="TEXT")
	public String note;
	public Float weightage;
	public PerformanceAppraisalType AppraisalType;
	public UserProjectStatus questionStatus = UserProjectStatus.Active;  // UserProjectStatus Is a Enum 
	
	//public PEQuestionype saQuestionype; // PEQuestionype Is a Enum
	//public Boolean mandatory = false; //false = No, true = Yes
	
	public static final Model.Finder<Long,PeQuestion> find=new Model.Finder<>(PeQuestion.class);
	
	public static Float getSelfRemainingWeightage() {
		Float TotalWeightage = 100.0f;
		Float presentTotalWeightage = 0.0f;
		List<PeQuestion> performanceSelfQuestionList  = PeQuestion.find.where().eq("AppraisalType", PerformanceAppraisalType.Self_Appraisal).findList();
		if(!performanceSelfQuestionList.isEmpty()){
			for(PeQuestion performanceSelfQuestion : performanceSelfQuestionList){
				presentTotalWeightage = presentTotalWeightage+performanceSelfQuestion.weightage;
			}
			TotalWeightage = TotalWeightage - presentTotalWeightage;
		}
		return TotalWeightage;
	}
	
	public static Float getEmployeeRemainingWeightage() {
		Float TotalWeightage = 100.0f;
		Float presentTotalWeightage = 0.0f;
		List<PeQuestion> performanceEmployeeQuestionList  =  PeQuestion.find.where().eq("AppraisalType", PerformanceAppraisalType.Employee_Appraisal).findList();
		if(!performanceEmployeeQuestionList.isEmpty()){
			for(PeQuestion performanceSelfQuestion : performanceEmployeeQuestionList){
				presentTotalWeightage = presentTotalWeightage+performanceSelfQuestion.weightage;
			}
			TotalWeightage = TotalWeightage - presentTotalWeightage;
		}
		return TotalWeightage;
	}
	
	public static Float getSelfRemainingWeightage(Long Id) {
		Float TotalWeightage = 100.0f;
		Float presentTotalWeightage = 0.0f;
		List<PeQuestion> performanceSelfQuestionList  = PeQuestion.find.where().eq("AppraisalType", PerformanceAppraisalType.Self_Appraisal).findList();
		if(!performanceSelfQuestionList.isEmpty()){
			for(PeQuestion performanceSelfQuestion : performanceSelfQuestionList){
				presentTotalWeightage = presentTotalWeightage+performanceSelfQuestion.weightage;
			}
			TotalWeightage = TotalWeightage - presentTotalWeightage;
		}
		PeQuestion performanceQuestion = PeQuestion.find.byId(Id);
		TotalWeightage = TotalWeightage + performanceQuestion.weightage;
		return TotalWeightage;
	}
	
	public static Float getEmployeeRemainingWeightage(Long Id) {
		Float TotalWeightage = 100.0f;
		Float presentTotalWeightage = 0.0f;
		List<PeQuestion> performanceEmployeeQuestionList  =  PeQuestion.find.where().eq("AppraisalType", PerformanceAppraisalType.Employee_Appraisal).findList();
		if(!performanceEmployeeQuestionList.isEmpty()){
			for(PeQuestion performanceSelfQuestion : performanceEmployeeQuestionList){
				presentTotalWeightage = presentTotalWeightage+performanceSelfQuestion.weightage;
			}
			TotalWeightage = TotalWeightage - presentTotalWeightage;
		}
		PeQuestion performanceQuestion = PeQuestion.find.byId(Id);
		TotalWeightage = TotalWeightage + performanceQuestion.weightage;
		return TotalWeightage;
	}
	
	public static Boolean getSelfTotalWeightage(){
		Boolean flag = false;
		Float totalWeightage = 0.0f;
		List<PeQuestion> performanceSelfQuestionList  = PeQuestion.find.where().eq("AppraisalType", PerformanceAppraisalType.Self_Appraisal).findList();
		for(PeQuestion peQuestion : performanceSelfQuestionList){
			totalWeightage += peQuestion.weightage;
		}
		Logger.debug("vvv"+totalWeightage);
		if(totalWeightage == 100){
			flag = true;
		}
		return flag;
	}
	
	public static Boolean getEmployeefTotalWeightage(){
		Boolean flag = false;
		Float totalWeightage = 0.0f;
		List<PeQuestion> performanceSelfQuestionList  = PeQuestion.find.where().eq("AppraisalType", PerformanceAppraisalType.Employee_Appraisal).findList();
		for(PeQuestion peQuestion : performanceSelfQuestionList){
			totalWeightage += peQuestion.weightage;
		}
		Logger.debug("vvv"+totalWeightage);
		if(totalWeightage == 100){
			flag = true;
		}
		return flag;
	}
}
