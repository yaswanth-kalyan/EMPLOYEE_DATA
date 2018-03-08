package controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import action.BasicAuth;
import action.EngineerAnnotation;
import bean.DailyStatusFill;
import bean.TimeSheetBean;
import models.Alert;
import models.AppUser;
import models.Attendance;
import models.DailyReport;
import models.Problems;
import models.ProjectDetails;
import models.Projects;
import models.Role;
import models.Roles;
import models.Timesheet;
import models.TimesheetUserRemark;
import models.Todays;
import models.Tomorrows;
import models.UserProjectStatus;
import models.UsersDailyReport;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import utils.Constants;

public class EngineerController extends Controller {

	/* ***************** Engineer , HR and Marketing home page methods  *********************** */
	
	@BasicAuth
	public Result home() {
		try {
			return ok(views.html.engineer.engineerHome.render());
		} catch (Exception e) {
			return ok(views.html.error.render(e.getMessage(), 404));
		}
	}
	
	@BasicAuth
	public Result HRHome() {
		try {
			//staticController.getTodayInterviewSchedule();
			return ok(views.html.engineer.hrHome.render());
		} catch (Exception e) {
			return ok(views.html.error.render(e.getMessage(), 404));
		}
	}
	
	@BasicAuth
	public Result MarketingHome() {
		try {
			return ok(views.html.engineer.engineerHome.render());
		} catch (Exception e) {
			return ok(views.html.error.render(e.getMessage(), 404));
		}
	}
	
	/* ***************** Engineer getProjects Methods *********************** */
	
	@BasicAuth
	@EngineerAnnotation
	public Result projects() {
		Map<Role,List<Projects>> finalProjects = new HashMap<Role,List<Projects>>();
		List<Projects> projects = new ArrayList<Projects>();
		List<Projects> allProjects = new ArrayList<Projects>();
		try {
			AppUser appUser   = AppUser.find.byId(Long.parseLong(session("AppUserId")));
			Role engineerRole = Role.find.where().eq("role", session("AppUserRole")).findUnique();
			Role managerRole  = Role.find.where().eq("role", Roles.Manager.toString()).findUnique();
			if (appUser != null) {
				List<Role> listRole = appUser.getRole();
				if(listRole.contains(managerRole)){
					allProjects = ManagerController.managerProjects(appUser);
					finalProjects.put(managerRole, allProjects);
				}
				projects = appUser.getProjects();
				for(Projects project : allProjects){
					if(projects.contains(project)){
						projects.remove(project);
					}
				}
				finalProjects.put(engineerRole, projects);
			}
			return ok(views.html.engineer.projects.render(finalProjects));
		} catch (Exception e) {
			e.printStackTrace();
			return ok(views.html.error.render(e.getMessage(), 204));
		}
	}
	
	public static List<ProjectDetails> getProjectList(){
		AppUser user = AppUser.find.byId(Long.parseLong(session("AppUserId")));
		List<ProjectDetails> myProjects = new ArrayList<ProjectDetails>();
		myProjects = EngineerController.getProDetails(user);
		return myProjects;
	}

	// get project details and primary contact person and mobile no
	public static List<ProjectDetails> getProDetails(AppUser appUser) {
		List<ProjectDetails> ProjectDetailsList = new ArrayList<ProjectDetails>();
		if (appUser != null) {
			List<Projects> myProjects = appUser.getProjects();
			for(Projects project : myProjects) {
				
				ProjectDetails projectDetail = new ProjectDetails();
				projectDetail.setProjectName(project.getProjectName());
				projectDetail.setClient(project.getClient());
				projectDetail.setStartedDate(project.getStartedDate());
				projectDetail.setEndedDate(project.getEndedDate());
				projectDetail.setProjectLeader(project.projectManager);
				projectDetail.setStatus(project.getStatus());
				projectDetail.setMyRole(appUser.getRole());
				
				ProjectDetailsList.add(projectDetail);
			}
		}
		return ProjectDetailsList;
	}
	
	/* ***************** Engineer and Manager Daily Report Methods *********************** */

	@BasicAuth
	@EngineerAnnotation
	public Result fillStatus() {
		List<Projects> projects = new ArrayList<Projects>();
		Set<Projects> setProjects = new HashSet<Projects>();
		try {
			AppUser appUser = AppUser.find.byId(Long.parseLong(session("AppUserId")));
			Role managerRole  = Role.find.where().eq("role", Roles.Manager.toString()).findUnique();
			projects = appUser.getProjects();
			if(!projects.isEmpty()){
				setProjects.addAll(projects);
			}
			List<Role> roles  = appUser.getRole();
			if(roles.contains(managerRole)){
				List<Projects> allProjects = ManagerController.managerProjects(appUser);
				if(!allProjects.isEmpty()){
					setProjects.addAll(allProjects);
				}
			}
			if(!setProjects.isEmpty()){
				projects.clear();
				projects.addAll(setProjects);
			}
			boolean f = getIsDone();
			if (f) {
				if(session("AppUserRole").equals("Admin")){
					return ok(views.html.admin.fillStatus.render("false",projects));
				}else{
					return ok(views.html.engineer.fillStatus.render("false",projects));
				}
			}else{
				if(session("AppUserRole").equals("Admin")){
					return ok(views.html.admin.fillStatus.render("true",projects));
				}else{
					return ok(views.html.engineer.fillStatus.render("true",projects));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ok(views.html.error.render("Page Not Found", 404));
		}

	}

	@BasicAuth
	@EngineerAnnotation
	public Result dailyStatusHistory() {
		return ok(views.html.engineer.dailyStatusHistory.render());
	}
	
	
	@BasicAuth
	// @EngineerAnnotation
	// save daily status
	public Result dailyStatus1() {

		try {
			DailyStatusFill dailyStatusFill = Form.form(DailyStatusFill.class).bindFromRequest().get();
			boolean flag = getIsDone();
			if (!flag) {
				List<String> today = dailyStatusFill.getToday();
				List<String> tomorrow = dailyStatusFill.getTomorrow();
				List<String> problem = dailyStatusFill.getProblem();
				List<Todays> todays = new ArrayList<Todays>();
				List<Tomorrows> tomorrows = new ArrayList<Tomorrows>();
				List<Problems> problems = new ArrayList<Problems>();
				for (int i = 0; i < today.size(); i++) {
					Todays today1 = new Todays();
					String value = today.get(i);
					if (!value.equals("") && value != null) {
						today1.setToday(value);
						todays.add(today1);
					}
				}
				for (int i = 0; i < tomorrow.size(); i++) {
					Tomorrows tomorrow1 = new Tomorrows();
					String value = tomorrow.get(i);
					if (!value.equals("") && value != null) {
						tomorrow1.setTomorrow(value);
						tomorrows.add(tomorrow1);
					}
				}
				for (int i = 0; i < problem.size(); i++) {
					Problems problem1 = new Problems();
					String value = problem.get(i);
					if (!value.equals("") && value != null) {
						problem1.setProblem(value);
						problems.add(problem1);
					}
				}
				
				DailyReport dailyReport = DailyReport.find.where().eq("app_user_id", Long.parseLong(session("AppUserId"))).findUnique();
				if (dailyReport != null) {
					List<UsersDailyReport> usersDailyReport = dailyReport.getUsersDailyReport();
					UsersDailyReport usersDailyReport1 = new UsersDailyReport();
					usersDailyReport1.setDate(getTodayDate(new Date()));
					usersDailyReport1.setToday(todays);
					usersDailyReport1.setTomorrow(tomorrows);
					usersDailyReport1.setProblem(problems);
					usersDailyReport1.setRate(dailyStatusFill.getRate());
					usersDailyReport1.setIsDone(true);
					usersDailyReport.add(usersDailyReport1);
					dailyReport.update();
				} else {
					List<UsersDailyReport> usersDailyReport = new ArrayList<UsersDailyReport>();
					DailyReport newDailyReport = new DailyReport();
					newDailyReport.setAppUser(Application.getLoggedInUser());
					UsersDailyReport usersDailyReport1 = new UsersDailyReport();
					usersDailyReport1.setDate(getTodayDate(new Date()));
					usersDailyReport1.setToday(todays);
					usersDailyReport1.setTomorrow(tomorrows);
					usersDailyReport1.setProblem(problems);
					usersDailyReport1.setRate(dailyStatusFill.getRate());
					usersDailyReport1.setIsDone(true);
					usersDailyReport.add(usersDailyReport1);
					newDailyReport.setUsersDailyReport(usersDailyReport);
					newDailyReport.save();
				}
				
				//time sheet
				try{
						TimeSheetBean timesheetbean = Form.form(TimeSheetBean.class).bindFromRequest().get();
						if(timesheetbean.getProjectId() != null) {
							timeSheet1(timesheetbean);
						}
				}catch(Exception e) {
					e.printStackTrace();
				}
				
				sendMail(session("email"));
			}
			return redirect(routes.EngineerController.dailyStatusSuccessMsg());
			
		} catch (Exception e) {
			e.printStackTrace();
			return ok(views.html.error.render(
					"You can't send Daily status at this time", 500));
		}
	}
	
	@BasicAuth
    public Result dailyStatusSuccessMsg(){
		List<Projects> projects = new ArrayList<Projects>();
		String role = session("AppUserRole");
		if (role.equals("Engineer") || role.equals("Marketing") || role.equals("HR")) {
			return ok(views.html.engineer.fillStatus.render("false1",projects));
		} else if(session("AppUserRole").equals("Admin")){
			return ok(views.html.admin.fillStatus.render("false1",projects));
		} else {
			return ok(views.html.manager.fillStatus1.render("false1",projects));
		}
		
	}
	
	private class SMTPAuthenticator extends javax.mail.Authenticator {
		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(Constants.EMAIL_USERNAME,
					Constants.EMAIL_PASSWORD);
		}// method
	}//

	// sending mail after fill daily status
	public void sendMail(String email) {
		// mail properties outgoing server (gmail.com)
		try {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "465");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
	
			// create Session obj
			Authenticator auth = new SMTPAuthenticator();
			Session session = Session.getInstance(props, auth);
			// prepare mail msg
			MimeMessage msg = new MimeMessage(session);
			// set header values
			msg.setSubject("daily status report");
			msg.setFrom(new InternetAddress(Constants.EMAIL_FROM));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
					email));
			
			// msg text
			// Create the message part
			// BodyPart messageBodyPart = new MimeBodyPart();
			// Now set the actual message
			msg.setText("Status report for today has been submitted successfully, Thanks for your time! \n\n Regards BB8 Team !");
			// Create a multipar message
			// Send the complete message parts
			// msg.setContent(messageBodyPart);
			Transport.send(msg);
		}// try
		catch (Exception ex) {
			ex.printStackTrace();
		}// catch

	}

	@BasicAuth
	public Result dailyStatusWeekwise(String date1, String date2) {
		List<UsersDailyReport> usersDailyReports = new ArrayList<UsersDailyReport>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		try {
			DailyReport dailyReport = DailyReport.find.where().eq("app_user_id", Long.parseLong(session("AppUserId"))).findUnique();
			if (dailyReport != null) {
				try {
					Date sdate = sdf.parse(date1);
					Date edate = sdf.parse(date2);
					usersDailyReports = UsersDailyReport.find.where().eq("daily_report_id", dailyReport.getId()).between("date", sdate, edate).findList();
					if(!usersDailyReports.isEmpty()){
						Collections.reverse(usersDailyReports);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return ok(views.html.engineer.dailyStatusHistoryWeekwise.render(usersDailyReports));
		} catch (Exception e) {
			e.printStackTrace();
			return ok(views.html.error.render(e.getMessage(), 204));
		}

	}
	

	public static Boolean getIsDone() {
		Boolean isDone = false;
		try {
			DailyReport dailyReport = DailyReport.find.where().eq("app_user_id", Long.parseLong(session("AppUserId"))).findUnique();
			UsersDailyReport usersDailyReport = UsersDailyReport.find.where().eq("daily_report_id", dailyReport.getId()).eq("date", getTodayDate(new Date())).findUnique();
			if(usersDailyReport != null){
				isDone = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isDone;
	}
	
	public static void changeDate(){
		List<UsersDailyReport> usersDailyReports = new ArrayList<UsersDailyReport>();
		usersDailyReports = UsersDailyReport.find.all();
		if(!usersDailyReports.isEmpty()){
			for(UsersDailyReport usersDailyReport : usersDailyReports){
				usersDailyReport.setDate(getTodayDate(usersDailyReport.getDate()));
				usersDailyReport.update();
			}
		}
	}
	
	/* ***************** Engineer and Manager Time-Sheet (Submitted)  Methods *********************** */

	@BasicAuth
	@EngineerAnnotation
	public Result fillTimeSheet() {
		List<Projects> projects = new ArrayList<Projects>();
		Set<Projects> setProjects = new HashSet<Projects>();
		try {
			AppUser appUser = AppUser.find.byId(Long.parseLong(session("AppUserId")));
			projects = appUser.getProjects();
			if(!projects.isEmpty()){
				setProjects.addAll(projects);
			}
			Role managerRole  = Role.find.where().eq("role", Roles.Manager.toString()).findUnique();
			List<Role> roles  = appUser.getRole();
			if(roles.contains(managerRole)){
				List<Projects> allProjects = ManagerController.managerProjects(appUser);
				if(!allProjects.isEmpty()){
					setProjects.addAll(allProjects);
				}
			}
			if(!setProjects.isEmpty()){
				projects.clear();
				projects.addAll(setProjects);
			}
			return ok(views.html.engineer.fillTimeSheet.render(projects));
		} catch (Exception e) {
			return ok(views.html.error.render(e.getMessage(), 204));
		}

	}

	@BasicAuth
	public Result timesheetHistory() {
		List<Projects> Projects = new ArrayList<Projects>();
		try {
			AppUser appUser = AppUser.find.byId(Long.parseLong(session("AppUserId")));
			Projects = appUser.getProjects();
			
			List<Role> listRole = appUser.getRole();
			Role managerRole = Role.find.where().eq("role",Roles.Manager.toString()).findUnique();
			if(listRole.contains(managerRole)){
				Projects.addAll(ManagerController.managerProjects(appUser));
			}
			return ok(views.html.engineer.timesheetHistory.render(Projects));
		} catch (Exception e) {
			return ok(views.html.error.render(e.getMessage(), 204));
		}
	}
	
	@BasicAuth
	public static void timeSheet1(TimeSheetBean timesheetbean) {

		try {
			List<Long> projectId = timesheetbean.getProjectId();
			List<Float> hours = timesheetbean.getHours();
			AppUser appUser = AppUser.find.byId(Long.parseLong(session("AppUserId")));
			//List<Attendance> attendances = Attendance.find.where().eq("app_user_id", appUser.getId()).eq("status", AttendenceStatus.Present).orderBy("date DESC").findList();
			Date date = getTodayDate(new Date());
			for (int i = 0; i < projectId.size(); i++) {
				Projects proejct = Projects.find.byId(projectId.get(i));
					if(proejct != null && appUser != null){
						Timesheet timesheetCheck = Timesheet.find.where().eq("app_user_id", appUser.getId()).eq("project_id", proejct.getId()).eq("date", date).findUnique();
						if(timesheetCheck == null){
						Timesheet timesheet = new Timesheet();
						timesheet.appUser = appUser;
						timesheet.project = proejct;
						timesheet.hours = hours.get(i);
						//timesheet.date = getTodayDate(attendances.get(0).date);
						timesheet.date = date;
						timesheet.save();
						}
					}
			}
			
			if(timesheetbean.getRemark() != null && appUser != null) {
				TimesheetUserRemark tSUR = TimesheetUserRemark.find.where().eq("app_user_id", appUser.getId()).eq("date", date).findUnique();
				if(tSUR == null){
					TimesheetUserRemark timesheetUserRemark = new TimesheetUserRemark();
					timesheetUserRemark.appUser =  appUser;
					//timesheetUserRemark.date = getTodayDate(attendances.get(0).date);
					timesheetUserRemark.date = date;
					timesheetUserRemark.remark = timesheetbean.getRemark();
					timesheetUserRemark.save();
				}
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void storeDate() {
		
		//Timesheet date storing
		/*List<Projects> projects = Projects.find.all();
		List<ProjectTimeSheet>  projectTimeSheets = new ArrayList<ProjectTimeSheet>();
		List<UTimeSheet> uTimeSheets = new ArrayList<UTimeSheet>();
		for(Projects project : projects){
			PTimeSheet pTimeSheet = PTimeSheet.find.where().eq("project_id", String.valueOf(project.getId())).findUnique();
			if(pTimeSheet != null){
				projectTimeSheets = ProjectTimeSheet.find.where().eq("ptime_sheet_id", pTimeSheet.getId()).findList();
				for(ProjectTimeSheet projectTimeSheet : projectTimeSheets){
					uTimeSheets = UTimeSheet.find.where().eq("project_time_sheet_id", projectTimeSheet.getId()).findList();
					for(UTimeSheet uTimeSheet : uTimeSheets){
						try {
							Date date = getTodayDate(projectTimeSheet.getDate());
							AppUser appUser = AppUser.find.byId(Long.parseLong(uTimeSheet.getAppUserID()));
							Timesheet timesheetCheck = Timesheet.find.where().eq("app_user_id", appUser.getId()).eq("project_id", project.getId()).eq("date", date).findUnique();
								if(timesheetCheck == null){
									Timesheet timesheet = new Timesheet();
									timesheet.project = project;
									timesheet.appUser = appUser;
									timesheet.hours = uTimeSheet.getHours();
									timesheet.date = date;
									timesheet.save();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						
					}
				}
			}
		}*/
	}
	
	public static void storeRemarks() {
		//User wise remarks storing
		/*List<AppUser> appUsers = AppUser.find.all();
		List<UsersTimeSheet> UsersTimeSheets = new ArrayList<UsersTimeSheet>();
		for(AppUser appUser : appUsers){
			TimeSheet timeSheet = TimeSheet.find.where().eq("app_user_id", String.valueOf(appUser.getId())).findUnique();
			if(timeSheet != null){
				UsersTimeSheets = UsersTimeSheet.find.where().eq("time_sheet_id", timeSheet.getId()).findList();
				for(UsersTimeSheet UsersTimeSheet : UsersTimeSheets){
					try {
						Date date = getTodayDate(UsersTimeSheet.getDate());
						TimesheetUserRemark tSUR = TimesheetUserRemark.find.where().eq("app_user_id", appUser.getId()).eq("date", date).findUnique();
							if(tSUR == null){
								TimesheetUserRemark timesheetUserRemark = new TimesheetUserRemark();
								timesheetUserRemark.appUser = appUser;
								timesheetUserRemark.date = date;
								timesheetUserRemark.remark = UsersTimeSheet.getRemark();
								timesheetUserRemark.save();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
				}
			}
		}*/
	}
	
	@BasicAuth
	// fill time sheet
	public Result timeSheet() {
		TimeSheetBean timesheetbean = Form.form(TimeSheetBean.class)
				.bindFromRequest().get();
		try{
			if(timesheetbean.getProjectId() != null) {
				timeSheet1(timesheetbean);
			}
			String role = session("AppUserRole");
			flash().put(
					"alert",
					new Alert("alert-success",
							"Today TimeSheet Submited Successfully").toString());
			if (role.equals("Engineer") || role.equals("Marketing") || role.equals("HR")) {
				return redirect(routes.EngineerController.fillTimeSheet());
			} else {
				return redirect(routes.ManagerController.fillTimesheet());
			}
		} catch (Exception e) {
			return ok(views.html.error.render(e.getMessage(), 204));
		}

	}

	public static Boolean getIsDoneTS() {
		Boolean isDone = false;
		try {
			List<Timesheet> timesheet = Timesheet.find.where().eq("app_user_id", Long.parseLong(session("AppUserId"))).eq("date", getTodayDate(new Date())).findList();
			if(timesheet.size() > 0){
				isDone = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return isDone;
	}

	public static Date getTodayDate(Date pdate){
		try {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String sdate = sdf.format(pdate);
		Date date = sdf.parse(sdate);
		return date;
		} catch (Exception e) {
			e.printStackTrace();
			return new Date();
		}
		
	}

	@BasicAuth
	public Result getWeekUTimeSheet(String sd, String ed) {
		Map<Date, List<Timesheet>> finalMap = new HashMap<Date, List<Timesheet>>();
		try {
			List<Timesheet> wk = new ArrayList<Timesheet>();
			AppUser appUser = AppUser.find.byId(Long
					.parseLong(session("AppUserId")));
			if (appUser != null) {
				wk = AdminController.weekTimesheet(sd, ed, appUser.getId());
				for(Timesheet timeSheet : wk) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(timeSheet.date);
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					if(!finalMap.containsKey(cal.getTime())) {
						List<Timesheet> timeSheetList = new ArrayList<Timesheet>();
						timeSheetList.add(timeSheet);
						finalMap.put(cal.getTime(), timeSheetList);
					} else {
						finalMap.get(cal.getTime()).add(timeSheet);
					}
				}
				return ok(views.html.engineer.getTimeSheet.render(finalMap));
			} else {
				return ok(views.html.engineer.getTimeSheet.render(finalMap));
			}
		} catch (Exception e) {
			return ok(views.html.error.render("", 0));
		}

	}

	@BasicAuth
	public Result getWeekPTimeSheet(String sd, String ed, Long id) {
		Map<Date, List<Timesheet>> finalMap = new HashMap<Date, List<Timesheet>>();
		try {
			List<Timesheet> wk = new ArrayList<Timesheet>();
			wk = AdminController.WeekPTimesheet(sd, ed, id);
			
			for(Timesheet timeSheet : wk) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(timeSheet.date);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				
				if(!finalMap.containsKey(cal.getTime())) {
					List<Timesheet> timeSheetList = new ArrayList<Timesheet>();
					timeSheetList.add(timeSheet);
					finalMap.put(cal.getTime(), timeSheetList);
				} else {
					finalMap.get(cal.getTime()).add(timeSheet);
				}
			}
			
			return ok(views.html.engineer.getPTimeSheet.render(finalMap));
		} catch (Exception e) {
			return ok(views.html.error.render(e.getMessage(), 204));
		}
	}

	/* ***************** Sending mail to Admin (Who are not filled status) Methods *********************** */
	
	public void mailSendingWhatAppUserFill() {
		
		List<Role> roleList = new ArrayList<>();
		Role roleEngineer = Role.find.where().eq("role", "Engineer")
				.findUnique();
		roleList.add(roleEngineer);
		Role roleManger = Role.find.where().eq("role", "Manager").findUnique();

		List<AppUser> managerList = roleManger.getAppUsers();
	
		roleList.add(roleManger);
		roleList.add(roleEngineer);
		List<AppUser> appUsers = AppUser.find.where().in("role",roleList).findList();
		
		int countSender = 0;

		int countNonSender = 0;
		boolean flag = false;
		List<AppUser> nonSenderList = new ArrayList<AppUser>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		for (AppUser appUser : appUsers) {

			DailyReport dailyReport = DailyReport.find.where()
					.eq("appUserId", appUser.getId()).findUnique();
			if (dailyReport != null) {
				List<UsersDailyReport> userDailyReportList = dailyReport
						.getUsersDailyReport();
				for (UsersDailyReport userDailyReport : userDailyReportList) {
					String uDate = sdf.format(userDailyReport.getDate());
					String tDate = sdf.format(new Date());
					if (uDate.trim().equals(tDate.trim())) {

						// mail properties outgoing server (gmail.com)

						Properties props = new Properties();
						props.put("mail.smtp.host", Constants.EMAIL_HOST);
						props.put("mail.smtp.port", Constants.EMAIL_PORT);
						props.put("mail.smtp.auth",Constants.EMAIL_AUTH);
					//	props.put("mail.smtp.socketFactory.class",
						//		"javax.net.ssl.SSLSocketFactory");
						props.put("mail.smtp.starttls.enable",
								Constants.EMAIL_STARTTLS_ENABLE);
						try {
							// create Session obj
							Authenticator auth = new SMTPAuthenticator();
							Session session = Session.getInstance(props, auth);
							// prepare mail msg
							MimeMessage msg = new MimeMessage(session);

							// set header values
							msg.setSubject("BB8 Daily Report");

							msg.setFrom(new InternetAddress(
									Constants.EMAIL_FROM));
							msg.addRecipient(Message.RecipientType.TO,
									new InternetAddress(appUser.email));
							List<Todays> listTodays = userDailyReport
									.getToday();
							String todays = "";
							int i = 1;
							for (Todays today : listTodays) {
								todays += i + "." + today.getToday() + "<br>";
								i++;
							}
							List<Tomorrows> listTomorrows = userDailyReport
									.getTomorrow();
							String tomorrows = "";
							i = 1;
							for (Tomorrows tomorrow : listTomorrows) {
								tomorrows += i + "." + tomorrow.getTomorrow()
										+ "<br>";
								i++;
							}
							List<Problems> listProblems = userDailyReport
									.getProblem();
							String problems = "";
							i = 1;
							for (Problems problem : listProblems) {
								problems += i + "." + problem.getProblem()
										+ "<br>";
								i++;
							}

							String message = "<html><body> Hi "
									+ appUser.FullName
									+ ", <br>    <center><h3>Below is your summary for today.</h3></center> <br> <caption>Daily Status </caption><table class='table table-bordered table-striped'><div class='col-md-12' id='dailyReportweek'><table class='table' border='2' > <thead><tr><th>Yesterdays work</th><th>Plan for Today</th><th>Problem faced</th><th>Rating</th></tr></thead><tbody><tr><td>"
									+ todays
									+ "</td><td>"
									+ tomorrows
									+ "</td><td>"
									+ problems
									+ "</td><td>"
									+ userDailyReport.getRate()
									+ "</td></tr></tbody></table></div><br> Thanks,<br>BB8 Team !</body></html>";

							msg.setText(message, "utf-8", "html");

							Transport.send(msg);
						//	Logger.info("DOne");
						}// try
						catch (Exception ex) {
							ex.printStackTrace();
						}// catch
						flag = true;
						break;
					}
				}
			}
			if (flag) {
				countSender++;
				flag = false;
			} else {
				countNonSender++;
				nonSenderList.add(appUser);
				Properties props = new Properties();
				props.put("mail.smtp.host", Constants.EMAIL_HOST);
				props.put("mail.smtp.port", Constants.EMAIL_PORT);
				props.put("mail.smtp.auth",Constants.EMAIL_AUTH);
				//props.put("mail.smtp.socketFactory.class",
				//		"javax.net.ssl.SSLSocketFactory");
				props.put("mail.smtp.starttls.enable",
						Constants.EMAIL_STARTTLS_ENABLE);
				try {
					// create Session obj
					Authenticator auth = new SMTPAuthenticator();
					Session session = Session.getInstance(props, auth);
					// prepare mail msg
					MimeMessage msg = new MimeMessage(session);

					// set header values
					msg.setSubject("BB8 Daily Report");
					msg.setFrom(new InternetAddress(Constants.EMAIL_FROM));
					msg.addRecipient(Message.RecipientType.TO,
							new InternetAddress(appUser.email));
					String message = "Hi"
							+ appUser.FullName
							+ ", \n Below is your summary for today. \n Daily Status <br> No Status filled today! \n Thanks,<br>BB8 Team !";

					msg.setText(message);

					Transport.send(msg);
				} catch (Exception e) {

				}
			}
		}
		
	}

	public void missingDailyStatusEmail() {
		
		List<Role> rolesList = new ArrayList<>();
		Role roleEngineer = Role.find.where().eq("role", "Engineer").findUnique();
		rolesList.add(roleEngineer);
		Role roleManger = Role.find.where().eq("role", "Manager").findUnique();
		rolesList.add(roleManger);
		List<AppUser> appUserListnew = AppUser.find.where().in("role", rolesList).eq("status", UserProjectStatus.Active).findList();
		
		boolean flag = false;
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		
		for (AppUser appUser : appUserListnew) {
			DailyReport dailyReport = DailyReport.find.where()
					.eq("app_user_id", appUser.getId()).findUnique();
			if (dailyReport != null) {
			List<UsersDailyReport> userDailyReportList = dailyReport
					.getUsersDailyReport();
				for (UsersDailyReport userDailyReport : userDailyReportList) {
					String uDate = sdf.format(userDailyReport.getDate());
					String tDate = sdf.format(new Date());
					if (uDate.trim().equals(tDate.trim())) {
						flag = true;
						break;
					} 
				}
			} 
			if(flag){
				flag = false;
			}else{
				final String username = Constants.EMAIL_USERNAME;
				final String password = Constants.EMAIL_PASSWORD;
				try {
				Properties props = new Properties();
				props.put("mail.smtp.host", Constants.EMAIL_HOST);
				props.put("mail.smtp.port", Constants.EMAIL_PORT);
				props.put("mail.smtp.auth", Constants.EMAIL_AUTH);
				props.setProperty("java.net.preferIPv4Stack", "true");
				props.setProperty("java.net.preferIPv6Addresses", "true");
				// props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
				props.put("mail.smtp.starttls.enable",
						Constants.EMAIL_STARTTLS_ENABLE);
				Authenticator auth = new SMTPAuthenticator();
				
				Session session = Session.getInstance(props, auth);
				
				MimeMessage message = new MimeMessage(session);
				
					message.setFrom(new InternetAddress(username));
					message.addRecipient(Message.RecipientType.TO, new InternetAddress(
							appUser.email));
					message.setSubject("Reminder - Fill Your 'Daily Status'");
					BodyPart messageBodyPart = new MimeBodyPart();
					messageBodyPart.setContent("", "text/html");
				
					Multipart multipart = new MimeMultipart();
					multipart.addBodyPart(messageBodyPart);
					message.setContent(multipart);
					Transport.send(message);
				} catch (AddressException e) {
					//e.printStackTrace();
				} catch (MessagingException e) {
					//e.printStackTrace();
				}
				
			}
		}
		
	}
	
	/* ***************** Engineer Attendance Methods *********************** */
	
	public Result weekwiseAttendanceRender()
	{ 
		return ok(views.html.engineer.UserwiseAttendanceweek.render());
	}
	
	//User weekly Attendence &Report Showing
	public Result weekwiseAttendanceData(String startDate,String endDate) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		List<Attendance> attendancesList=new ArrayList<Attendance>();
		Date sdate=null;
		Date edate=null;
			try {
				 sdate = sdf.parse(startDate);
				 edate = sdf.parse(endDate);
			    AppUser appUser=AppUser.find.byId(Long.parseLong(session("AppUserId")));
			    List<Attendance> attendanceList=appUser.attendences;
			    for(Attendance attendance:attendanceList)
			    {
			    	if (attendance.getDate().equals(sdate) || attendance.getDate().after(sdate)
							&& attendance.getDate().before(edate)
							|| attendance.getDate().equals(edate)) {
						attendancesList.add(attendance);
					}
			    }
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		String sd=sdf1.format(sdate);
		String ed=sdf1.format(edate);
		
		Collections.sort(attendancesList,new Comparator<Attendance>() {
		    @Override
		    public int compare(Attendance a, Attendance b) {
		        return b.date.compareTo(a.date);
		    }
		});
		return ok(views.html.engineer.userAttendanceWeekwise.render(attendancesList,sd,ed,Long.parseLong(session("AppUserId"))));	
	}

}
