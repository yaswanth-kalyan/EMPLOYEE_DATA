package controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import action.AdminMarketAnnotation;
import action.BasicAuth;
import models.Alert;
import models.AppUser;
import models.Attendance;
import models.AttendenceStatus;
import models.DailyReport;
import models.Projects;
import models.Timesheet;
import models.UserProjectStatus;
import models.UsersDailyReport;
import models.lead.Company;
import models.lead.CompanyContactInfo;
import models.lead.CompanyContacts;
import models.lead.Lead;
import models.lead.LeadChatComment;
import models.lead.LeadContactInfo;
import models.lead.LeadStatus;
import models.lead.NotificationAlert;
import models.leave.AppliedLeaveType;
import models.leave.AppliedLeaves;
import models.leave.DateWiseAppliedLeaves;
import models.leave.DurationEnum;
import models.leave.LeaveStatus;
import models.recruitment.ApplicantStatus;
import models.recruitment.JobStatus;
import models.recruitment.RecruitmentApplicant;
import models.recruitment.RecruitmentInterviewType;
import models.recruitment.RecruitmentJob;
import models.recruitment.RecruitmentSelectionRound;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class staticController extends Controller{
	public Result getconfigureLeaves() {
		return ok(views.html.staticpages.configureLeaves.render());
	}
	public Result getAddNewLeaveApply() {
		return ok(views.html.staticpages.addNewLeaveApply.render());
	}
	public Result getWorkingDay() {
		return ok(views.html.staticpages.workingDays.render());
	}
	public Result getAddHoliday(){
		return ok(views.html.staticpages.addHoliday.render());
	}
	public Result getAddEntitlement(){
		return ok(views.html.staticpages.addEntitlement.render());
	}
	public Result getLeaveCalender(){
		return ok(views.html.staticpages.leaveCalender.render());
	}
	public Result getLeaveTracker(){
		return ok(views.html.staticpages.leaveTracker.render());
	}
	public Result getApplyLeave(){
		return ok(views.html.staticpages.applyLeave.render());
	}
	public Result getLeaveStatus(){
		return ok(views.html.staticpages.leaveStatus.render());
	}
	public Result test(){
		return ok(views.html.staticpages.test.render());
	}
	public Result getAllReports(){
		return ok(views.html.reports.reportsLandingPage.render());
	}
	/*public Result getAllTasks(){
		return ok(views.html.task.tasks.render());
	}*/
	 
	

	/* Admin & Marketing Lead Module Pages */
	
	@AdminMarketAnnotation
    @BasicAuth
	public Result getAddLead(){
		List<Company> listCompanys = new ArrayList<Company>();
		List<CompanyContacts> listContacts = new ArrayList<CompanyContacts>();
		List<LeadStatus> listLeadStatus = new ArrayList<LeadStatus>();
		listLeadStatus = LeadStatus.find.all();
		listCompanys = Company.find.all();
		listContacts = CompanyContacts.find.all();
		return ok(views.html.leads.addLead.render(listCompanys,listContacts,listLeadStatus));
	}
	
	@AdminMarketAnnotation
    @BasicAuth
	public Result getEditLead(Long id){
		Lead lead = Lead.find.byId(id);
		List<LeadContactInfo> listLeadContact =new ArrayList<LeadContactInfo>();
		listLeadContact  = LeadContactInfo.find.where().eq("lead_id",id).findList();
		List<CompanyContacts> listContacts = new ArrayList<CompanyContacts>();
		listContacts = CompanyContacts.find.all();
		return ok(views.html.leads.editLead.render(lead,listContacts,listLeadContact));
	}
	
	@AdminMarketAnnotation
    @BasicAuth
	public Result addLeadStatusBYLead(String id){
		session("CID",id);
		List<LeadStatus> listLeadStatus = new ArrayList<LeadStatus>();
		listLeadStatus = LeadStatus.find.all();
		return ok(views.html.leads.configureLead.render(listLeadStatus));
	}
	
	@AdminMarketAnnotation
    @BasicAuth
	public Result getConfigureLead(){
		session("CID","");
		List<LeadStatus> listLeadStatus = new ArrayList<LeadStatus>();
		listLeadStatus = LeadStatus.find.all();
		return ok(views.html.leads.configureLead.render(listLeadStatus));
	}
	
	public Result editConfigureLead(Long id){
		LeadStatus leadStatus = LeadStatus.find.byId(id);
		return ok(views.html.leads.editConfigureLead.render(leadStatus));
	}
	@AdminMarketAnnotation
    @BasicAuth
	public Result getTrackLead(){
		return ok(views.html.leads.trackLead.render());
	}
	
	@AdminMarketAnnotation
    @BasicAuth
	public Result getAllLeads(){
		List<Lead> listLead = new ArrayList<Lead>();
		listLead = Lead.find.where().orderBy("lastUpdate DESC").findList();
		return ok(views.html.leads.allLeads.render(listLead));
	}
	
	@AdminMarketAnnotation
    @BasicAuth
	public Result getManagementLead(Long leadId){
		
		Lead lead = Lead.find.byId(leadId);
		if(lead != null ){
			List<LeadContactInfo> listLeadContact =new ArrayList<LeadContactInfo>();
			listLeadContact  = LeadContactInfo.find.where().eq("lead_id",leadId).findList();
			session("leadId",leadId.toString());
			List<LeadStatus> listLeadStatus = new ArrayList<LeadStatus>();
			List<LeadChatComment> listLeadComments = new ArrayList<LeadChatComment>();
			listLeadComments = LeadChatComment.find.where().eq("leadId", leadId).findList();
			listLeadStatus = LeadStatus.find.all();
		
			List<NotificationAlert> listNotificationAlert = NotificationAlert.find.where().eq("alert", false).eq("leadId", leadId).findList();
			if(listNotificationAlert != null && !listNotificationAlert.isEmpty()){
				for(NotificationAlert notificationAlert : listNotificationAlert){
						notificationAlert.alert = true;
						notificationAlert.update();
				}
			}
			Collections.sort(listLeadComments);
			return ok(views.html.leads.leadManagement.render(listLeadStatus,listLeadComments,lead,listLeadContact));
		}else{
			flash().put(
					"alert",
					new Alert("alert-danger", "Lead Does Not Existed").toString());
			return redirect(routes.staticController.getAllLeads());
		}
	}
	
	public Result getAppendManagementLead(Long leadId){
		Lead lead = Lead.find.byId(leadId);
		if(lead != null ){
		List<LeadContactInfo> listLeadContact =new ArrayList<LeadContactInfo>();
		listLeadContact  = LeadContactInfo.find.where().eq("lead_id",leadId).findList();
		session("leadId",leadId.toString());
		List<LeadStatus> listLeadStatus = new ArrayList<LeadStatus>();
		List<LeadChatComment> listLeadComments = new ArrayList<LeadChatComment>();
		listLeadComments = LeadChatComment.find.where().eq("leadId", leadId).findList();
		listLeadStatus = LeadStatus.find.all();
		
			List<NotificationAlert> listNotificationAlert = NotificationAlert.find.where().eq("alert", false).eq("leadId", leadId).findList();
			if(listNotificationAlert != null && !listNotificationAlert.isEmpty()){
				for(NotificationAlert notificationAlert : listNotificationAlert){
						notificationAlert.alert = true;
						notificationAlert.update();
				}
			}
			Collections.sort(listLeadComments);
			return ok(views.html.leads.getLeadManagement.render(listLeadStatus,listLeadComments,lead,listLeadContact));
		}else{
			flash().put(
					"alert",
					new Alert("alert-danger", "Lead Does Not Existed").toString());
			return redirect(routes.staticController.getAllLeads());
		}
	}
	@AdminMarketAnnotation
    @BasicAuth
	public Result getAllCompany(){
		List<Company> allCompanys = new ArrayList<Company>();
		allCompanys = Company.find.all();
		return ok(views.html.leads.allCompany.render(allCompanys));
	}
	
	@AdminMarketAnnotation
    @BasicAuth
	public Result addCompanyBYLead(){
		return ok(views.html.leads.staticAddCompany.render());
	}
	
	@AdminMarketAnnotation
    @BasicAuth
	public Result getAddCompany(){
		session("COMID","");
		List<CompanyContacts> contacts =new ArrayList<CompanyContacts>();
		contacts = CompanyContacts.find.all();
		return ok(views.html.leads.addCompany.render(contacts));
	}
	
	@AdminMarketAnnotation
    @BasicAuth
	public Result getEditCompany(Long id){
		if(id != null) {
			List<CompanyContacts> contacts =new ArrayList<CompanyContacts>();
			List<CompanyContactInfo> listContacts =new ArrayList<CompanyContactInfo>();
			contacts = CompanyContacts.find.all();
			listContacts  = CompanyContactInfo.find.where().eq("company_id",id).findList();
			Company company = Company.find.byId(id);
			return ok(views.html.leads.editCompany.render(company,contacts,listContacts));
		} else{
			flash().put(
					"alert",
					new Alert("alert-danger", "ID does not existed ").toString());
			return redirect(routes.staticController.getAllCompany());
		}
	}
	
	@AdminMarketAnnotation
    @BasicAuth
	public Result addContactBYCompany(String id){
		session("CID",id);
		return ok(views.html.leads.addContact.render());
	}
	
	@AdminMarketAnnotation
    @BasicAuth
	public Result addContactBYLead(){
		//session("CID",id);
		return ok(views.html.leads.staticAddContact.render());
	}
	
	@AdminMarketAnnotation
    @BasicAuth
	public Result getAddContact(){
		session("CID","");
		return ok(views.html.leads.addContact.render());
	}
	
	@AdminMarketAnnotation
    @BasicAuth
	public Result getEditContact(Long id){
		
		List<CompanyContactInfo> companyContactInfo = new ArrayList<CompanyContactInfo>();
		CompanyContacts contact = CompanyContacts.find.byId(id);
		companyContactInfo = CompanyContactInfo.find.where().eq("company_contacts_id", contact.id).findList();
		return ok(views.html.leads.editContact.render(contact,companyContactInfo));
	}
	
	@AdminMarketAnnotation
    @BasicAuth
	public Result getAllContact(){
		List<CompanyContacts> contacts = new ArrayList<CompanyContacts>();
		contacts =  CompanyContacts.find.all();
		Collections.sort(contacts, (CompanyContacts p1, CompanyContacts p2) -> p1.id.compareTo(p2.id));
		return ok(views.html.leads.allContact.render(contacts));
	}
	
	public static List<CompanyContactInfo> getComapnyName(Long id) {
		List<CompanyContactInfo> companyContactInfo = new ArrayList<CompanyContactInfo>();
		companyContactInfo = CompanyContactInfo.find.where().eq("company_contacts_id", id).findList();
		return companyContactInfo;
	}
	
	 @BasicAuth
	 public Result allNotification(){
		return ok(views.html.leads.allNotifications.render());
	 }
	
	 public static void readNotification(){
		 List<NotificationAlert> listNotificationAlert = new ArrayList<NotificationAlert>();
		 listNotificationAlert = NotificationAlert.getListNotifications();
		 for(NotificationAlert notificationAlert : listNotificationAlert){
			 notificationAlert.alert = true;
			 notificationAlert.update();
		 }
	 }
	 public Result markAllRead(){
		 readNotification();
		 return ok();
	 }
	 
	 public Result markAllReadNoti(){
		 readNotification();
		 return redirect(routes.staticController.allNotification());
	 }
	 
	/* 
	 * 
	 * Admin DashBoard Pages Methods
	 * 
	 * 
	 * */
	 
	public Result getTodayStatus(){
		Map<String,Integer> Status = new HashMap<String,Integer>();
		List<AppUser> appUsers = new ArrayList<AppUser>();
		List<AppUser> appUserNotFilled = new ArrayList<AppUser>();
		List<AppUser> appUserFilled = new ArrayList<AppUser>();
		try {
			appUsers = AppUser.find.where().eq("status",UserProjectStatus.Active).findList();
			for(AppUser appUser : appUsers){
				DailyReport dailyReport = DailyReport.find.where().eq("app_user_id", appUser.getId()).findUnique();
				if(dailyReport != null){
					UsersDailyReport udailyReport = UsersDailyReport.find.where().eq("daily_report_id", dailyReport.getId()).eq("date", EngineerController.getTodayDate(new Date())).findUnique();
					if(udailyReport == null){
						Attendance attendance = Attendance.find.where().eq("app_user_id",appUser.getId()).eq("status", AttendenceStatus.Absent).eq("date", EngineerController.getTodayDate(new Date())).findUnique();
						if(attendance == null){
							appUserNotFilled.add(appUser);
						}
					}else{
						appUserFilled.add(appUser);
					}
				} else {
					appUserNotFilled.add(appUser);
				}
			}
			Status.put("Filled", appUserFilled.size());
			Status.put("Not FIlled", appUserNotFilled.size());
		}catch(Exception e){
			e.printStackTrace();
		}
		return ok(Json.toJson(Status));
	}
	
	public Result getRecruitmentStatus(){
		Map<String,Integer> Status = new HashMap<String,Integer>();
		List<RecruitmentJob> RecruitmentJobList = new ArrayList<RecruitmentJob>();
		try {
			for(JobStatus jobStatus : JobStatus.values()){
				RecruitmentJobList =  RecruitmentJob.find.where().eq("jobStatus",jobStatus).findList();
				Status.put(jobStatus.toString(), RecruitmentJobList.size());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return ok(Json.toJson(Status));
	}
	
	public Result getApplicantStatus(){
		Map<String,Integer> Status = new HashMap<String,Integer>();
		List<RecruitmentApplicant> applicantStatusList = new ArrayList<RecruitmentApplicant>();
		try {
			for(ApplicantStatus applicantStatus : ApplicantStatus.values()) {
				applicantStatusList.clear();
				applicantStatusList =  RecruitmentApplicant.find.where().eq("status", applicantStatus).findList();
				Status.put(applicantStatus.toString(), applicantStatusList.size());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return ok(Json.toJson(Status));
	}
	
	public static List<AppliedLeaves> getLeaves() throws ParseException{
		
		SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy");
		Date tDate  = sf.parse(sf.format(new Date())); 
		List<Attendance> attendanceList = new ArrayList<Attendance>();
		List<AppUser> appUserList = new ArrayList<AppUser>();
		List<AppliedLeaves> appliedLeavesTodayApproved = new ArrayList<AppliedLeaves>();
		List<AppliedLeaves> appliedLeavesTodayPending = new ArrayList<AppliedLeaves>();
		
		List<AppliedLeaves> notAppliedLeavesToday = new ArrayList<AppliedLeaves>();
		List<AppliedLeaves> appliedLeavesDateToday = new ArrayList<AppliedLeaves>();
		List<DateWiseAppliedLeaves> dateWiseAppliedLeavesToday = DateWiseAppliedLeaves.find.where().eq("leaveDate", tDate).findList();
		//appliedLeavesDateToday = AppliedLeaves.find.where().in("dateLeaves",dateWiseAppliedLeavesToday).findList();
		//appUserList = appliedLeavesDateToday.stream().filter(appUser).collect(Collectors.toList());
		for(DateWiseAppliedLeaves dateWiseAppliedLeaves : dateWiseAppliedLeavesToday){
			AppliedLeaves appliedLeaves = AppliedLeaves.find.where().in("dateLeaves",dateWiseAppliedLeaves).findUnique();
			if(appliedLeaves != null && appliedLeaves.leaveStatus.equals(LeaveStatus.APPROVED)){
				appliedLeavesTodayApproved.add(appliedLeaves);
				appUserList.add(appliedLeaves.appUser);
			}else if(appliedLeaves != null && appliedLeaves.leaveStatus.equals(LeaveStatus.PENDING_APPROVAL)){
				appliedLeavesTodayPending.add(appliedLeaves);
				appUserList.add(appliedLeaves.appUser);
			}
		}
		appliedLeavesDateToday.addAll(appliedLeavesTodayApproved);
		appliedLeavesDateToday.addAll(appliedLeavesTodayPending);
		/*Date tDate  = sf.parse(sf.format(new Date())); 
		AppliedLeavesToday = AppliedLeaves.find.where().ne("leaveStatus", LeaveStatus.CANCELLED).findList();
		for(AppliedLeaves appliedLeaves : AppliedLeavesToday){
			for(DateWiseAppliedLeaves DateWiseAppliedLeaves : appliedLeaves.dateLeaves){
				if(sf.parse(sf.format(DateWiseAppliedLeaves.leaveDate)).equals(tDate)){
					AppliedLeavesDateToday.add(appliedLeaves);
				}
			}
		}*/
		attendanceList = Attendance.find.where().eq("date", tDate).eq("status", AttendenceStatus.Absent).findList();
		for(Attendance attendance  : attendanceList){
			//List<AppliedLeaves> appUserAppliedLeaves = appliedLeavesDateToday.stream().filter(leaves -> !leaves.appUser.id.equals(attendance.appUser.id)).collect(Collectors.toList());
			if(!appUserList.contains(attendance.appUser)){
				AppliedLeaves appliedLeaves = new AppliedLeaves();
				appliedLeaves.leaveStatus = LeaveStatus.NOT_APPLIED;
				appliedLeaves.appUser = attendance.appUser;
				appliedLeaves.startDate = tDate;
				appliedLeaves.endDate = tDate;
				notAppliedLeavesToday.add(appliedLeaves);
			}
		}
		appliedLeavesDateToday.addAll(notAppliedLeavesToday);
		return appliedLeavesDateToday;
	}
	
	public Result getleadsSummary(){
		Map<String,Integer> Status = new HashMap<String,Integer>();
    	List<Lead> listLeads = new ArrayList<Lead>();
    	List<LeadStatus> listLeadStatus = new ArrayList<LeadStatus>();
    	try{
    		listLeadStatus = LeadStatus.find.all();
    		if(!listLeadStatus.isEmpty()){
    			for(LeadStatus leadStatus : listLeadStatus){
    				listLeads = Lead.find.where().eq("leadStatus", leadStatus).findList();
    				if(!listLeads.isEmpty()){
    					Status.put(leadStatus.status.toString(), listLeads.size());
    				}
    			}
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return ok(Json.toJson(Status));
    }
	
	
	/* 
	 * 
	 * Manager DashBoard Pages Methods
	 * 
	 * 
	 * 
	 */
	
	
	public static List<AppliedLeaves> getManagerLeaves() throws ParseException{
		
		Set<AppUser> mteamMembers = new HashSet<AppUser>();
		List<AppliedLeaves> appliedMLeavesDateToday = new ArrayList<AppliedLeaves>();
		List<Projects> ListMProjects = Projects.find.where().eq("projectManager", Application.getLoggedInUser()).findList();
		if(!ListMProjects.isEmpty()){
			for(Projects projects : ListMProjects){
				mteamMembers.addAll(projects.getAppUser());
			}
		}
		List<AppliedLeaves> appliedLeavesDateToday = getLeaves();
		for(AppliedLeaves appliedLeaves : appliedLeavesDateToday){
			if(mteamMembers.contains(appliedLeaves.appUser)){
				appliedMLeavesDateToday.add(appliedLeaves);
			}
		}
		return appliedMLeavesDateToday;
	}
	
	
	/* 
	 * 
	 * Engineer DashBoard Pages Methods
	 * 
	 * 
	 * 
	 */
	
	public Result getRating() throws ParseException{
		Map<Date,Integer> finalRatingMap = new LinkedHashMap<Date,Integer>();
		Map<Date,Integer> ratingMap = new LinkedHashMap<Date,Integer>();
		try {
			List<UsersDailyReport> usersDailyReportRatingList = new ArrayList<UsersDailyReport>();
			Calendar startDate = Calendar.getInstance();
			Calendar endDate = Calendar.getInstance();
			startDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			endDate.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
			DailyReport dailyReport = DailyReport.find.where().eq("app_user_id", Long.parseLong(session("AppUserId"))).findUnique();
			if(dailyReport != null){
				usersDailyReportRatingList  = UsersDailyReport.find.where().eq("daily_report_id", dailyReport.getId()).between("date", EngineerController.getTodayDate(startDate.getTime()),EngineerController.getTodayDate(endDate.getTime())).findList();
				 if(usersDailyReportRatingList != null &&!usersDailyReportRatingList.isEmpty()){
				    	for (UsersDailyReport usersDailyReport : usersDailyReportRatingList) {
				    		ratingMap.put(usersDailyReport.getDate(), usersDailyReport.getRate());
				    	}
				   }
			}
			 for(int i=0 ;i<6;i++){
				 if(!ratingMap.isEmpty() ){
					 if(ratingMap.containsKey(EngineerController.getTodayDate(startDate.getTime()))){
						 finalRatingMap.put(EngineerController.getTodayDate(EngineerController.getTodayDate(startDate.getTime())), ratingMap.get(EngineerController.getTodayDate(startDate.getTime())));
					 }else{
						 finalRatingMap.put(EngineerController.getTodayDate(startDate.getTime()), 0);
					 }
				 }else{
					 finalRatingMap.put(EngineerController.getTodayDate(startDate.getTime()), 0);
				 }
				 startDate.add(Calendar.DATE, 1);
			 }
			 /*for (Date key : finalRatingMap.keySet()) {
				 Logger.debug("data "+key+" rating"+finalRatingMap.get(key));
			 }*/
		}catch(Exception e){
			e.printStackTrace();
		}
		//Map<Date,Integer> finalRatingMapSort = new TreeMap<Date,Integer>(finalRatingMap);
		return ok(Json.toJson(finalRatingMap));
	}
	
	public Result getTimeSheetHours() throws ParseException {
		Map<String, Float> resultMap = new HashMap<String, Float>();
		Set<Projects> projectSet = new HashSet<Projects>();
		try {
			Calendar startDate = Calendar.getInstance();
			Calendar endDate = Calendar.getInstance();
			startDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			endDate.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
			AppUser appUser = Application.getLoggedInUser();
			List<Projects> projectList = appUser.getProjects();
			List<Projects> pmProjectList = Projects.find.where().eq("projectManager", appUser).findList();
			projectSet.addAll(projectList);
			projectSet.addAll(pmProjectList);
			if(!projectSet.isEmpty()){
				for(Projects project : projectSet){
					float projectHoursSum = 0.0f;
					List<Timesheet> timesheets = Timesheet.find.where().eq("app_user_id",appUser.getId())
							.between("date", EngineerController.getTodayDate(startDate.getTime()),EngineerController.getTodayDate(endDate.getTime())).eq("project_id", project.getId()).findList();
					
				    if(!timesheets.isEmpty()){
				    	for (Timesheet timesheet : timesheets) {
				    		projectHoursSum += timesheet.hours;
				    	}
				    }
				    resultMap.put(project.getProjectName(), projectHoursSum);
				}
			}/*else{
				resultMap.put("", 0.0f);
			}*/
		}catch(Exception e){
			e.printStackTrace();
		}
		return ok(Json.toJson(resultMap));
	}
	
	public Result getAttendanceHours() {
		Map<Date, Float> FinalresultMap = new LinkedHashMap<Date, Float>();
		Map<Date, Float> resultMap = new HashMap<Date, Float>();
		try {
			Calendar startDate = Calendar.getInstance();
			Calendar endDate = Calendar.getInstance();
			startDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			endDate.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
			AppUser appUser = AppUser.find.byId(Long.parseLong(session("AppUserId")));
			
			List<Attendance> attendanceList = Attendance.find.where().eq("app_user_id",appUser.getId()).between("date", EngineerController.getTodayDate(startDate.getTime()),EngineerController.getTodayDate(endDate.getTime())).eq("status", AttendenceStatus.Present).findList();
			float projectHoursSum = 0.0f;
			for(Attendance attendance : attendanceList){
				if(attendance.getSpendTime() != null){
					String[] output = attendance.getSpendTime().split(":");
					projectHoursSum = Float.parseFloat(output[0]+"."+output[1]);
				    resultMap.put(attendance.getDate(), projectHoursSum);
				}
			}
			for(int i=0 ;i<6;i++){
				 if(!resultMap.isEmpty() ){
						 if(resultMap.containsKey(EngineerController.getTodayDate(startDate.getTime()))){
							 FinalresultMap.put(EngineerController.getTodayDate(EngineerController.getTodayDate(startDate.getTime())), resultMap.get(EngineerController.getTodayDate(startDate.getTime())));
						 }else{
							 FinalresultMap.put(EngineerController.getTodayDate(startDate.getTime()), 0.0f);
						 }
				 }else{
					 FinalresultMap.put(EngineerController.getTodayDate(startDate.getTime()), 0.0f);
				 }
				 startDate.add(Calendar.DATE, 1);
			 }
		}catch(Exception e){
			e.printStackTrace();
		}
		return ok(Json.toJson(FinalresultMap));
	}
	
	public static Map<String,Float> getTotalLeaves() throws NumberFormatException, ParseException {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy");
		SimpleDateFormat lastSF = new SimpleDateFormat("dd-MM-yyyy");
		Date lastDate = lastSF.parse("31-03-2016"); //starting BB8
		String year = sf.format(new Date());
		Map<String,Float> resultMap = new LinkedHashMap<String,Float>();
//		List<AppliedLeaves> leavesAPPROVED = new ArrayList<AppliedLeaves>();
//		List<AppliedLeaves> leavesPENDING_APPROVAL = new ArrayList<AppliedLeaves>();
//		List<AppliedLeaves> leavesUNPLANNED = new ArrayList<AppliedLeaves>();
		List<AppliedLeaves> leavesList= AppliedLeaves.find.where().eq("app_user_id",Long.parseLong(session("AppUserId"))).findList();
		
		Float APPROVED = 0.0f;
		Float PENDING_APPROVAL = 0.0f;
		Float UNPLANNED = 0.0f;
		
		if(!leavesList.isEmpty()){
			for(AppliedLeaves appliedLeaves : leavesList){
				//if(year.equals(sf.format(appliedLeaves.createdOn))){
					if(appliedLeaves.leaveStatus.equals(LeaveStatus.APPROVED)){
						APPROVED += appliedLeaves.totalLeaves;
						//leavesAPPROVED.add(appliedLeaves);
					}else if(appliedLeaves.leaveStatus.equals(LeaveStatus.PENDING_APPROVAL)){
						//leavesPENDING_APPROVAL.add(appliedLeaves);
						PENDING_APPROVAL += appliedLeaves.totalLeaves;
					}
					
					for(DateWiseAppliedLeaves DateWiseAppliedLeaves : appliedLeaves.dateLeaves){
						//if(EngineerController.getTodayDate(DateWiseAppliedLeaves.leaveDate).before(EngineerController.getTodayDate(DateWiseAppliedLeaves.createdOn))){
						if(DateWiseAppliedLeaves.appliedLeaveType.equals(AppliedLeaveType.Unplanned)){
							//Logger.debug("Date : "+EngineerController.getTodayDate(DateWiseAppliedLeaves.leaveDate)+", "+EngineerController.getTodayDate(DateWiseAppliedLeaves.createdOn));
							if(DateWiseAppliedLeaves.duEnum.equals(DurationEnum.FULL_DAY)){
								UNPLANNED += 1.0f;
							}else{
								UNPLANNED += 0.5f;
							}
							//leavesUNPLANNED.add(appliedLeaves);
						}
					}
			//	}
			}
		}
		
		Float TO_BE_APPLIED = 0.0f;
		List<Attendance> attendanceAbsentList = Attendance.find.where().eq("app_user_id",Long.parseLong(session("AppUserId"))).eq("status", AttendenceStatus.Absent).findList();
		for(Attendance attendance : attendanceAbsentList){
			if(year.equals(sf.format(attendance.date)) && lastDate.before(lastSF.parse((lastSF.format(attendance.date))))){
				List<DateWiseAppliedLeaves> dateWiseAppliedLeavesToday = DateWiseAppliedLeaves.find.where().eq("leaveDate", EngineerController.getTodayDate(attendance.date)).findList();
				if(!dateWiseAppliedLeavesToday.isEmpty()){
					List<AppliedLeaves> appliedLeavesLists = AppliedLeaves.find.where().eq("app_user_id",Long.parseLong(session("AppUserId"))).in("dateLeaves",dateWiseAppliedLeavesToday).findList();
					if(appliedLeavesLists.isEmpty()){
						TO_BE_APPLIED++;
					}
				}else{
					TO_BE_APPLIED++;
				}
			}
		}
		
		/*if(!leavesAPPROVED.isEmpty()){
			resultMap.put(LeaveStatus.APPROVED.toString(), leavesAPPROVED.size());
		}
		if(!leavesPENDING_APPROVAL.isEmpty()){
			resultMap.put(LeaveStatus.PENDING_APPROVAL.toString(), leavesPENDING_APPROVAL.size());
		}
		if(!leavesUNPLANNED.isEmpty()){
			resultMap.put("UN_PLANNED", leavesUNPLANNED.size());
		}*/
		
		resultMap.put(LeaveStatus.APPROVED.toString(), APPROVED);
		resultMap.put(LeaveStatus.PENDING_APPROVAL.toString(), PENDING_APPROVAL);
		resultMap.put("UN_PLANNED", UNPLANNED);
		resultMap.put("TO_BE_APPLIED", TO_BE_APPLIED);
		
		return resultMap;
	}
	
	public static String getToBeAppliedDates() throws NumberFormatException, ParseException{
		SimpleDateFormat sf = new SimpleDateFormat("yyyy");
		SimpleDateFormat sf1 = new SimpleDateFormat("dd-MM-yyyy");
		Date lastDate = sf1.parse("31-03-2016");
		String year = sf.format(new Date());
		String dates = "";
		List<Attendance> attendanceAbsentList = Attendance.find.where().eq("app_user_id",Long.parseLong(session("AppUserId"))).eq("status", AttendenceStatus.Absent).findList();
		for(Attendance attendance : attendanceAbsentList){
			if(year.equals(sf.format(attendance.date)) && lastDate.before(sf1.parse((sf1.format(attendance.date))))){
				List<DateWiseAppliedLeaves> dateWiseAppliedLeavesToday = DateWiseAppliedLeaves.find.where().eq("leaveDate", EngineerController.getTodayDate(attendance.date)).findList();
				if(!dateWiseAppliedLeavesToday.isEmpty()){
					List<AppliedLeaves> appliedLeavesLists = AppliedLeaves.find.where().eq("app_user_id",Long.parseLong(session("AppUserId"))).in("dateLeaves",dateWiseAppliedLeavesToday).findList();
					if(appliedLeavesLists.isEmpty()){
						dates = dates+sf1.format(attendance.date)+",";
					}
				}else{
					dates = dates+sf1.format(attendance.date)+",";
				}
			}
		}
		return dates;
	}
	
	public static Map<RecruitmentInterviewType,List<RecruitmentSelectionRound>> getTodayInterviewSchedule() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:00:00");
		Map<RecruitmentInterviewType,List<RecruitmentSelectionRound>> todayInterviewScheduleList = new LinkedHashMap<RecruitmentInterviewType,List<RecruitmentSelectionRound>>();
		List<RecruitmentInterviewType> recruitmentInterviewTypeList = RecruitmentInterviewType.find.all();
		if(recruitmentInterviewTypeList != null){
			for(RecruitmentInterviewType recruitmentInterviewType : recruitmentInterviewTypeList) {
				List<RecruitmentSelectionRound> recruitmentSelectionRoundList = RecruitmentSelectionRound.find.
							where().eq("toDate", EngineerController.getTodayDate(new Date())).in("recruitmentInterviewType", recruitmentInterviewType).findList();
				if(!recruitmentSelectionRoundList.isEmpty()){
					todayInterviewScheduleList.put(recruitmentInterviewType, recruitmentSelectionRoundList);
				}
			}
		}
		return todayInterviewScheduleList;
	}
}


