package bean;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Controller;

import controllers.AdminLeaveController;
import controllers.Application;
import models.AppUser;
import models.Role;
import models.recruitment.JobLocation;
import models.recruitment.RecruitmentApplicant;
import models.recruitment.RecruitmentCategory;
import models.recruitment.RecruitmentJob;
import models.recruitment.RecruitmentRole;
import models.recruitment.RecruitmentSource;
import play.Logger;

public class ApplicantBean {
	
	public Long id;
	public Long referedById;
	public Long applicantCategoryid;
	public Long jobRoleid;
	public String jobid;
	public Long sourceid;
	public String dateOfBirth;
	public JobLocation preferLocation;
	public String applicationId;
	
	public static RecruitmentApplicant toApplicant(RecruitmentApplicant recruitmentApplicant,ApplicantBean applicantBean) {
		
		if(applicantBean.preferLocation!=null) {
			recruitmentApplicant.preferedLocation = applicantBean.preferLocation;
		}else{
			recruitmentApplicant.preferedLocation = null;
		}
		
	    if(recruitmentApplicant.applyDate==null) {
	    	recruitmentApplicant.applyDate=new Date();
	    }
		if(applicantBean.referedById!=null){
			recruitmentApplicant.referedBy=AppUser.find.byId(applicantBean.referedById);
		}
		if(applicantBean.applicantCategoryid!=null){
		recruitmentApplicant.applicantCategory=RecruitmentCategory.find.byId(applicantBean.applicantCategoryid);
		}
		if(applicantBean.jobRoleid!=null){
		recruitmentApplicant.recruitmentRole=RecruitmentRole.find.byId(applicantBean.jobRoleid);
		}
		if(applicantBean.jobid!=null){
			recruitmentApplicant.recruitmentJob=RecruitmentJob.find.where().eq("jobId", applicantBean.jobid).findUnique();
		}
		if(applicantBean.sourceid!=null){
			recruitmentApplicant.recruitmentSource=RecruitmentSource.find.byId(applicantBean.sourceid);
		}
		if(applicantBean.jobid!=null) {
			recruitmentApplicant.recruitmentJob=RecruitmentJob.find.where().eq("jobId", applicantBean.jobid).findUnique();
		}
		if(applicantBean.dateOfBirth!=null) {
			try{
				SimpleDateFormat sdf= new SimpleDateFormat("dd-MM-yyyy");
				Date date=sdf.parse(applicantBean.dateOfBirth);
				recruitmentApplicant.dob=date;
			}
			catch(Exception e){}
		}
		if(recruitmentApplicant != null  && recruitmentApplicant.id != null){
			RecruitmentApplicant recruitmentApplicant1 =  RecruitmentApplicant.find.byId(recruitmentApplicant.id);
			recruitmentApplicant.recruitmentSelectionRounds = recruitmentApplicant1.recruitmentSelectionRounds;
			recruitmentApplicant.updatedBy = Application.getLoggedInUser();
				
				String message = "";
	    		message = "Applicant ID ( "+recruitmentApplicant.applicationId+" ) updated by "+Application.getLoggedInUser().getFullName();
				List<Role> rolesList = new ArrayList<Role>();
				
	    		Role roleHR = Role.find.where().eq("role", "HR").findUnique();
	    		rolesList.add(roleHR);
	    		List<AppUser> appUsersList = new ArrayList<AppUser>();
	    		appUsersList.addAll(AppUser.find.where().in("role", rolesList).findList());
	    		for(AppUser appUser : appUsersList){
					if(!Application.getLoggedInUser().equals(appUser)){
						if(appUser.role.contains(roleHR)){
						 AdminLeaveController.sendNotification(appUser, Application.getLoggedInUser(), message, "/recruitment/applicants",roleHR);
						 Logger.debug("called");
						}
					}
	    		}
				
		}else{
			recruitmentApplicant.createdBy = Application.getLoggedInUser();
		}
		
		return recruitmentApplicant;
		
	}

	public static synchronized String getApplicantId() {
		String ApplId = "";
		RecruitmentApplicant recruitmentApplicant = null;
				SimpleDateFormat sf = new SimpleDateFormat("MMyy");
				String toDate=sf.format(new Date());
				List<RecruitmentApplicant> recruitmentApplicants=RecruitmentApplicant.find.where().orderBy("applyDate").findList();
		        if(recruitmentApplicants!=null && !recruitmentApplicants.isEmpty()){
		        	Long id = Long.parseLong(RecruitmentApplicant.find.orderBy("id desc").findIds().get(0).toString());
		        	recruitmentApplicant = RecruitmentApplicant.find.byId(id);
			        String applicantId1=recruitmentApplicant.applicationId;
			        Logger.debug("id "+applicantId1);
			        String lastTwoDigitOfJobId=applicantId1.substring(5);
			        String month=applicantId1.substring(1,5);
			        if(month.equalsIgnoreCase(toDate)){
				        long lastTwoDigitOfJobIdIntValue=0;
				        lastTwoDigitOfJobIdIntValue=Integer.parseInt(lastTwoDigitOfJobId);
				        lastTwoDigitOfJobIdIntValue++;
				        if(lastTwoDigitOfJobIdIntValue < 10){
				        	ApplId = "A"+toDate+"00"+lastTwoDigitOfJobIdIntValue;
				        } else if(lastTwoDigitOfJobIdIntValue < 100){
				        	ApplId = "A"+toDate+"0"+lastTwoDigitOfJobIdIntValue;
				        } else {
				        	ApplId = "A"+toDate+""+lastTwoDigitOfJobIdIntValue;
				        }
			        }else{
			        	ApplId="A"+toDate+"001";
			        }
		         } else {
		        	 ApplId = "A"+toDate+"001";
		        }
		        return ApplId;
		}
	
	public static synchronized String getApplicantId1(String ApplicantId) {
		String ApplId = "";
				SimpleDateFormat sf = new SimpleDateFormat("MMyy");
				String toDate=sf.format(new Date());
		        if(ApplicantId != null ){
			        String applicantId1=ApplicantId;
			        String lastTwoDigitOfJobId=applicantId1.substring(5);
			        String month=applicantId1.substring(1,5);
			        if(month.equalsIgnoreCase(toDate)){
				        long lastTwoDigitOfJobIdIntValue=0;
				        lastTwoDigitOfJobIdIntValue=Integer.parseInt(lastTwoDigitOfJobId);
				        lastTwoDigitOfJobIdIntValue++;
				        if(lastTwoDigitOfJobIdIntValue < 10){
				        	ApplId = "A"+toDate+"00"+lastTwoDigitOfJobIdIntValue;
				        } else if(lastTwoDigitOfJobIdIntValue < 100){
				        	ApplId = "A"+toDate+"0"+lastTwoDigitOfJobIdIntValue;
				        } else {
				        	ApplId = "A"+toDate+""+lastTwoDigitOfJobIdIntValue;
				        }
			        }else{
			        	ApplId="A"+toDate+"001";
			        }
		         } else {
		        	 ApplId = "A"+toDate+"001";
		        }
		        return ApplId;
		}
	
	public static synchronized void setIds(){
		SimpleDateFormat sf = new SimpleDateFormat("MMyy");
		String toDate=sf.format(new Date());
		List<RecruitmentApplicant> recruitmentApplicants=RecruitmentApplicant.find.where().orderBy("id").findList();
		long id = 1l;
		for(RecruitmentApplicant recruitmentApplicant : recruitmentApplicants){
			//String mon = recruitmentApplicant.applicationId.substring(1,5);
			if( id < 10){
				recruitmentApplicant.applicationId ="A"+toDate+"00"+id;
	        } else if( id < 100){
	        	recruitmentApplicant.applicationId ="A"+toDate+"0"+id;
	        } else {
	        	recruitmentApplicant.applicationId ="A"+toDate+""+id;
	        }
			id++;
			recruitmentApplicant.update();
		}
	}
	}
