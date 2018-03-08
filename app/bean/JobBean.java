package bean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import models.recruitment.JobLocation;
import models.recruitment.JobStatus;
import models.recruitment.JobType;
import models.recruitment.RecruitmentCategory;
import models.recruitment.RecruitmentJob;
import models.recruitment.RecruitmentRole;
import models.recruitment.RecruitmentSkill;

import org.joda.time.DateTime;

import play.Logger;
public class JobBean {
	
	public Long id;
	public String jobId;
	public byte[] jobDescription;
	public Long jobCategory;
	public Long jobRole;
	public Float jobExperience;
	public Integer noOfOpenning;
	public JobLocation jobLocation;
	public JobType jobType;
	public String lastDate;
	public List<Long> mandatorySkills;
	public List<Long> desiredSkills;
	public JobStatus jobStatus;
	public String remarks;
	
	public static RecruitmentJob toJob(JobBean jobBean) {
		RecruitmentJob recruitmentJob=null;
		if(jobBean.id != null){
			recruitmentJob=RecruitmentJob.find.byId(jobBean.id);
				recruitmentJob.mandatorySkills.clear();
				recruitmentJob.update();
				if(jobBean.mandatorySkills!=null){
				for(Long id:jobBean.mandatorySkills)
				{
					recruitmentJob.mandatorySkills.add(RecruitmentSkill.find.byId(id));
				}
				}

				recruitmentJob.desiredSkills.clear();
				recruitmentJob.update();
				if(jobBean.desiredSkills != null && !jobBean.desiredSkills.isEmpty() ){
					for(Long id:jobBean.desiredSkills)
					{
						recruitmentJob.desiredSkills.add(RecruitmentSkill.find.byId(id));
					}

					}
				
		}
		else{
			recruitmentJob=new RecruitmentJob();
			SimpleDateFormat sf = new SimpleDateFormat("MMyy");
			DateTime dateTime=new DateTime();
			List<RecruitmentJob> recruitmentJobs=RecruitmentJob.find.where().orderBy("openDate").findList();
	        if(jobBean.mandatorySkills != null) {
	    		for(Long id:jobBean.mandatorySkills) {
	    			recruitmentJob.mandatorySkills.add(RecruitmentSkill.find.byId(id));
	    		}
	    	}
	    	if(jobBean.desiredSkills != null){
	    		for(Long id:jobBean.desiredSkills) {
	    			recruitmentJob.desiredSkills.add(RecruitmentSkill.find.byId(id));
	    		}
	    	}
		}
		
        if(jobBean.jobLocation != null) {
        	recruitmentJob.jobLocation=jobBean.jobLocation;
        }
		recruitmentJob.jobStatus = jobBean.jobStatus;
		recruitmentJob.jobType = jobBean.jobType;
		recruitmentJob.noOfOpenning=jobBean.noOfOpenning;
		recruitmentJob.jobExperience=jobBean.jobExperience;
		recruitmentJob.recruitmentCategory=RecruitmentCategory.find.byId(jobBean.jobCategory);
		recruitmentJob.recruitmentRole=RecruitmentRole.find.byId(jobBean.jobRole);
		recruitmentJob.openDate=new Date();
	    Date lastDate=null;
		try{
		SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
		
		lastDate=sdf.parse(jobBean.lastDate);
		}
		catch(Exception e)
		{
			
		}
		recruitmentJob.lastDate=lastDate;
		if(jobBean.remarks!=null)
		recruitmentJob.remark=jobBean.remarks;
		
		return recruitmentJob;
		
	}
	
	public static synchronized String getJobsId(){
		String jobsId = "";
		SimpleDateFormat sf = new SimpleDateFormat("MMyy");
		String toDate=sf.format(new Date());
		RecruitmentJob recruitmentJob = null;
		//List<RecruitmentJob> recruitmentJobs=RecruitmentJob.find.where().orderBy("openDate").findList();
		Long id = Long.parseLong(RecruitmentJob.find.orderBy("id desc").findIds().get(0).toString());
        if(id != null) {
        	recruitmentJob = RecruitmentJob.find.byId(id);
	        String jobId1 = recruitmentJob.jobId;
	        Logger.debug("id "+jobId1);
	        String lastTwoDigitOfJobId=jobId1.substring(5);
	        String month=jobId1.substring(1,5);
	        if(month.equalsIgnoreCase(toDate)){
		        //Logger.debug("mm "+month);
		        long lastTwoDigitOfJobIdIntValue = 0;
		        lastTwoDigitOfJobIdIntValue=Integer.parseInt(lastTwoDigitOfJobId);
		        lastTwoDigitOfJobIdIntValue++;
		        if(lastTwoDigitOfJobIdIntValue < 10){
		        	jobsId="J"+toDate+"00"+lastTwoDigitOfJobIdIntValue;
		        } else if(lastTwoDigitOfJobIdIntValue < 100){
		        	jobsId="J"+toDate+"0"+lastTwoDigitOfJobIdIntValue;
		        } else {
		        	jobsId="J"+toDate+""+lastTwoDigitOfJobIdIntValue;
		        }
	        }else{
	        	jobsId="J"+toDate+"001";
	        }
        } else {
        	jobsId="J"+toDate+"001";
        }
        return jobsId;
	}
	
	public static synchronized String getJobsId1(String jobId){
		String jobsId = "";
		SimpleDateFormat sf = new SimpleDateFormat("MMyy");
		String toDate=sf.format(new Date());
		RecruitmentJob recruitmentJob = null;
        if(jobId != null) {
	        String jobId1 = jobId;
	        String lastTwoDigitOfJobId=jobId1.substring(5);
	        String month=jobId1.substring(1,5);
	        if(month.equalsIgnoreCase(toDate)) {
		        long lastTwoDigitOfJobIdIntValue = 0;
		        lastTwoDigitOfJobIdIntValue=Integer.parseInt(lastTwoDigitOfJobId);
		        lastTwoDigitOfJobIdIntValue++;
		        if(lastTwoDigitOfJobIdIntValue < 10){
		        	jobsId="J"+toDate+"00"+lastTwoDigitOfJobIdIntValue;
		        } else if(lastTwoDigitOfJobIdIntValue < 100){
		        	jobsId="J"+toDate+"0"+lastTwoDigitOfJobIdIntValue;
		        } else {
		        	jobsId="J"+toDate+""+lastTwoDigitOfJobIdIntValue;
		        }
	        }else{
	        	jobsId="J"+toDate+"001";
	        }
        } else {
        	jobsId="J"+toDate+"001";
        }
        return jobsId;
	}
	
	public static synchronized void setIds(){
		SimpleDateFormat sf = new SimpleDateFormat("MMyy");
		String toDate=sf.format(new Date());
		List<RecruitmentJob> recruitmentJobs=RecruitmentJob.find.where().orderBy("id").findList();
		long id = 1l;
		for(RecruitmentJob RecruitmentJob : recruitmentJobs){
			//String mon = RecruitmentJob.jobId.substring(1,5);
			if( id < 10){
				 RecruitmentJob.jobId ="J"+toDate+"00"+id;
	        } else if( id < 100){
	        	 RecruitmentJob.jobId ="J"+toDate+"0"+id;
	        } else {
	        	 RecruitmentJob.jobId ="J"+toDate+""+id;
	        }
			id++;
			RecruitmentJob.update();
		}
	}
}
