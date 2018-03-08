package controllers;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import models.Alert;
import models.AppUser;
import models.Projects;
import models.Role;
import models.Roles;
import models.Timesheet;
import models.UserProjectStatus;
import models.performance.PeEmployeeAppraisal;
import models.performance.PeEmployeeAppraisalAnswer;
import models.performance.PeQuestion;
import models.performance.PeSelfAppraisal;
import models.performance.PeSelfAppraisalAnswer;
import models.performance.PerformanceAppraisalType;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.Constants;
import bean.EmployeeAppraisalBean;
import bean.PEMonthEmployeesDataBean;
import bean.SelfAppraisalBean;
import controllers.SampleDataController.SMTPAuthenticator;
public class PEController extends Controller {

		/* ********************************************** Checking Methods ************************************************ */
	
	//checking LoggedInUser More Role
	public static Boolean checkIsMoreRoles(){
		Boolean flag = false;
		List<Projects> projectList = Projects.find.where().eq("projectManager", Application.getLoggedInUser()).findList();
		if(!projectList.isEmpty()){
			flag = true;
		}
		return flag;
	}
	
	//Checking Date for Open Form Page (Self && Employee)
	public static Boolean getCondition(){
		Boolean flag = false;
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int month = cal.get(Calendar.MONTH);
		if(month == 1){
			if(day >= 25){
				flag = true;
			}
		}else{
			if(day >= 27){
				flag = true;
			}
		}
		
		if(day <= 3){
			flag = true;
		}
		return flag;
	}
	
	//Split the String onto Lines
	public static List<String> convertReference(String reference){
		List<String> tokenlist = new ArrayList<String>();
		if(reference != null){
	    StringTokenizer tokens = new StringTokenizer(reference,",");
	    String eachWord="";
	    	for(int i=0;i<=reference.length()-1;i++){
	    	char eachCahr=reference.charAt(i);
	    	
	    	if(eachCahr != '\n'){
	    	eachWord=eachWord+eachCahr;
	    	}else{
	    	tokenlist.add(eachWord);
	    	eachWord="";
	    	continue;
	    	}
	    	}
	    	tokenlist.add(eachWord);
		}else{
			
		}
	    	
	    return tokenlist;
	}
	
	//Get the Last Month Within Form closed 
	public static Date getLastMonth() throws ParseException{
		SimpleDateFormat sf = new SimpleDateFormat("MM-yyyy");
		Calendar lastMonth = Calendar.getInstance();
		lastMonth.setTime(getMonth());
		lastMonth.add(Calendar.MONTH, -1);
		//Logger.debug("Montgh "+lastMonth.getTime());
		return sf.parse(sf.format(lastMonth.getTime()));
	}
	
	//Get the Present Month Within Form closed 
	public static Date getMonth() throws ParseException{
		SimpleDateFormat sf = new SimpleDateFormat("MM-yyyy");
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DAY_OF_MONTH);
		if(day <= 3){
			cal.add(Calendar.DATE, -day);
		}
		//Date thisMonth = sf.parse(sf.format(cal.getTime()));
		return sf.parse(sf.format(cal.getTime()));
	}
	
	//Checking The Month For Edit Purpose (Self && Employee )
	public static Boolean checkMonthday(Date date){
		SimpleDateFormat sf = new SimpleDateFormat("MM-yyyy");
		Boolean flag = false;
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DAY_OF_MONTH);
		if(sf.format(date).equals(sf.format(new Date()))){
			int month = cal.get(Calendar.MONTH);
			if(month == 1){
				if(day >= 25){
					flag = true;
				}
			}else{
				if(day >= 27){
					flag = true;
				}
			}
		}
		Calendar lastMonth = Calendar.getInstance();
		lastMonth.setTime(date);
		lastMonth.add(Calendar.MONTH, 1);
		if(sf.format(lastMonth.getTime()).equals(sf.format(new Date()))){
			if(day <= 3){
				flag = true;
			}
		}
		return flag;
		
	}
	
	/* ********************************************** Add And Update Self - Employee Questions Methods ************************************************ */
	
	
	public Result configureQuestionnaire() {
		return ok(views.html.performance.configure.render());
	}
	
	public Result SelfAppraisalQuestion() {
		List<PeQuestion> selfAppraisalQuestionList = PeQuestion.find.where().eq("AppraisalType", PerformanceAppraisalType.Self_Appraisal).findList();
		return ok(views.html.performance.selfAppraisalQuestion.render(selfAppraisalQuestionList,null));
	}
	
	public Result EmployeeAppraisalQuestion() {
		List<PeQuestion> EmployeeAppraisalQuestionList = PeQuestion.find.where().eq("AppraisalType", PerformanceAppraisalType.Employee_Appraisal).findList();
		return ok(views.html.performance.employeeAppraisalQuestion.render(EmployeeAppraisalQuestionList,null));
	}
	
	public Result EditSelfAppraisalQuestion(Long Id) {
		PeQuestion performanceQuestion = PeQuestion.find.byId(Id);
		List<PeQuestion> selfAppraisalQuestionList = PeQuestion.find.where().eq("AppraisalType", PerformanceAppraisalType.Self_Appraisal).findList();
		return ok(views.html.performance.selfAppraisalQuestion.render(selfAppraisalQuestionList,performanceQuestion));
	}
	
	public Result EditEmployeeAppraisalQuestion(Long Id) {
		PeQuestion performanceQuestion = PeQuestion.find.byId(Id);
		List<PeQuestion> EmployeeAppraisalQuestionList = PeQuestion.find.where().eq("AppraisalType", PerformanceAppraisalType.Employee_Appraisal).findList();
		return ok(views.html.performance.employeeAppraisalQuestion.render(EmployeeAppraisalQuestionList,performanceQuestion));
	}
	public Result AddUpdateSelfAppraisalQuestion() {
		PeQuestion performanceSelfQuestion = Form.form(PeQuestion.class).bindFromRequest().get();
		try {
			if(performanceSelfQuestion.id != null){
				if(performanceSelfQuestion.questionStatus.equals(UserProjectStatus.Inactive)){
					performanceSelfQuestion.weightage = 0.0f;
				}
				performanceSelfQuestion.update();
				flash().put(
						"alert",
						new Alert("alert-success",
								performanceSelfQuestion.id+" Self Appraisal Question Successfully Updated!").toString());
			}else{
				performanceSelfQuestion.AppraisalType = PerformanceAppraisalType.Self_Appraisal;
				performanceSelfQuestion.save();
				flash().put(
						"alert",
						new Alert("alert-success",
								"New Self Appraisal Question Successfully Added!").toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			flash().put(
					"alert",
					new Alert("alert-danger",
							"New Self Appraisal Question Not Successfully Added .").toString());
			//return ok(views.html.error.render(e.getMessage(), 404));
		}
		return redirect(routes.PEController.SelfAppraisalQuestion());
	}
	
	public Result AddUpdateEmployeeAppraisalQuestion() {
		PeQuestion performanceEmployeeQuestion = Form.form(PeQuestion.class).bindFromRequest().get();
		try {
			if(performanceEmployeeQuestion.id != null){
				if(performanceEmployeeQuestion.questionStatus.equals(UserProjectStatus.Inactive)){
					performanceEmployeeQuestion.weightage = 0.0f;
				}
				performanceEmployeeQuestion.update();
				flash().put(
						"alert",
						new Alert("alert-success",
								performanceEmployeeQuestion.id+" Employee Appraisal Question Successfully Updated!").toString());
			}else{
				performanceEmployeeQuestion.AppraisalType = PerformanceAppraisalType.Employee_Appraisal;
				performanceEmployeeQuestion.save();
				flash().put(
						"alert",
						new Alert("alert-success",
								"New Employee Appraisal Question Successfully Updated!").toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			flash().put(
					"alert",
					new Alert("alert-danger",
							"New Self Employee Question Not Successfully Added .").toString());
		}
		return redirect(routes.PEController.EmployeeAppraisalQuestion());
	}
	
	/* ********************************************** Add And Update Self Appraisal  Methods ************************************************ */
	
	public Result SelfAppraisal() {
		List<PeQuestion> selfAppraisalQuestionList = PeQuestion.find.where().eq("AppraisalType", PerformanceAppraisalType.Self_Appraisal).eq("questionStatus", UserProjectStatus.Active).findList();
		return ok(views.html.performance.selfAppraisal.render(selfAppraisalQuestionList));
	}
	
	public Result addselfAppraisal() throws ParseException {
		SelfAppraisalBean selfAppraisalBean = Form.form(SelfAppraisalBean.class).bindFromRequest().get();
		
		try{
			if(!PeSelfAppraisal.checkSubmit(Long.parseLong(session("AppUserId")))){
				
				Double totalRating = 0.0d;
				List<PeSelfAppraisalAnswer> answerList = new ArrayList<PeSelfAppraisalAnswer>();
				SimpleDateFormat sf =new SimpleDateFormat("MM-yyyy");
				
				PeSelfAppraisal selfAppraisal = new PeSelfAppraisal();
				selfAppraisal.appUser = Application.getLoggedInUser();
				selfAppraisal.monthDate = sf.parse(sf.format(getMonth()));
				selfAppraisal.issue = selfAppraisalBean.issue;
				for(int i=0;i<selfAppraisalBean.questionId.size();i++){
					PeSelfAppraisalAnswer SEAnswer = new PeSelfAppraisalAnswer();
						PeQuestion performanceQuestion = PeQuestion.find.byId(selfAppraisalBean.questionId.get(i));
						SEAnswer.performanceQuestion = performanceQuestion;
						SEAnswer.rate = selfAppraisalBean.rate.get(i);
						totalRating += ((performanceQuestion.weightage) * (selfAppraisalBean.rate.get(i)));
						SEAnswer.answer = selfAppraisalBean.answer.get(i);
						answerList.add(SEAnswer);
				}
				selfAppraisal.setSaar(Math.round(totalRating/100*100.0)/100.0);
				selfAppraisal.answerList = answerList;
				
				selfAppraisal.save();
				flash().put(
						"alert",
						new Alert("alert-success",
								"Your Self Appraisal Successfully Submited!").toString());
			}else{
				flash().put(
						"alert",
						new Alert("alert-success",
								"Your Self Appraisal Already Successfully Submited!").toString());
			}
				//return redirect(routes.PerformanceController.SelfAppraisal());
		}catch(Exception e){
			e.printStackTrace();
			flash().put(
					"alert",
					new Alert("alert-danger",
							"Your Self Appraisal Not Successfully Submited!").toString());
			//return redirect(routes.PerformanceController.SelfAppraisal());
		}
		return redirect(routes.PEController.SelfAppraisal());
	}
	
	public Result editSelfAppraisal(Long Id) {
		PeSelfAppraisal peSelfAppraisal = PeSelfAppraisal.find.byId(Id);
		return ok(views.html.performance.editSelfAppraisal.render(peSelfAppraisal));
	}
	
	public Result updateSelfAppraisal() {
		SelfAppraisalBean selfAppraisalBean = Form.form(SelfAppraisalBean.class).bindFromRequest().get();
		try{
			Double totalRating = 0.0d;
			List<PeSelfAppraisalAnswer> answerList = new ArrayList<PeSelfAppraisalAnswer>();
			SimpleDateFormat sf =new SimpleDateFormat("MM-yyyy");
			
			PeSelfAppraisal selfAppraisal1 = PeSelfAppraisal.find.byId(selfAppraisalBean.id);
			selfAppraisal1.Saar = null;
			selfAppraisal1.answerList.clear();
			selfAppraisal1.update();
			PeSelfAppraisal selfAppraisal = PeSelfAppraisal.find.byId(selfAppraisalBean.id);
			selfAppraisal.appUser = Application.getLoggedInUser();
			selfAppraisal.monthDate = sf.parse(sf.format(getMonth()));
			selfAppraisal.issue = selfAppraisalBean.issue;
			for(int i=0;i<selfAppraisalBean.questionId.size();i++){
				PeSelfAppraisalAnswer SEAnswer = new PeSelfAppraisalAnswer();
					PeQuestion performanceQuestion = PeQuestion.find.byId(selfAppraisalBean.questionId.get(i));
					SEAnswer.performanceQuestion = performanceQuestion;
					SEAnswer.rate = selfAppraisalBean.rate.get(i);
					totalRating += ((performanceQuestion.weightage) * (selfAppraisalBean.rate.get(i)));
					SEAnswer.answer = selfAppraisalBean.answer.get(i);
					answerList.add(SEAnswer);
			}
			selfAppraisal.setSaar(Math.round(totalRating/100*100.0)/100.0);
			selfAppraisal.Saar = totalRating/100;
			selfAppraisal.answerList = answerList;
			
			selfAppraisal.update();
			flash().put(
					"alert",
					new Alert("alert-success",
							"Your Self Appraisal Successfully Updated!").toString());
			//return redirect(routes.PerformanceController.SelfAppraisal());
		}catch(Exception e){
			e.printStackTrace();
			flash().put(
					"alert",
					new Alert("alert-danger",
							"Your Self Appraisal Not Successfully Updated!").toString());
			//return redirect(routes.PerformanceController.SelfAppraisal());
		}
		return redirect(routes.PEController.selfAppraisalHistory());
	}
	public Result selfAppraisalHistory() {
		List<PeSelfAppraisal> peSelfAppraisalList = PeSelfAppraisal.find.where().eq("appUser", Application.getLoggedInUser()).findList();
		return ok(views.html.performance.selfAppraisalHistory.render(peSelfAppraisalList));
	}
	
	public Result monthSAppraisalHistory(String Month) throws ParseException {
		SimpleDateFormat sf =new SimpleDateFormat("MM-yyyy");
		PeSelfAppraisal peSelfAppraisal = PeSelfAppraisal.find.where().eq("appUser", Application.getLoggedInUser()).eq("monthDate", sf.parse(Month)).findUnique();
		//Date month =  getMonth();
		//Logger.debug("month "+month);
		return ok(views.html.performance.monthSelfAppraisalHistory.render(peSelfAppraisal));
	}
	
	
	/* ********************************************** Add And Update Employee Appraisal  Methods ************************************************ */

	
	public static Double getTotalHours(AppUser appUser,Projects project,Date Month) throws ParseException{
		Double totalHours = 0.0d;
		
		Calendar startDate = Calendar.getInstance();    
		startDate.setTime(Month); //------>
		startDate.set(Calendar.DAY_OF_MONTH, startDate.getActualMinimum(Calendar.DAY_OF_MONTH));
		//AppUser app = AppUser.find.orderBy("id desc").setMaxRows(1).findUnique();
		Calendar endDate = Calendar.getInstance();    
		endDate.setTime(Month); //------>
		endDate.set(Calendar.DAY_OF_MONTH, endDate.getActualMaximum(Calendar.DAY_OF_MONTH));
		List<Timesheet> timesheetList = Timesheet.find.where().eq("appUser", appUser).eq("project", project).between("Date", startDate.getTime(), endDate.getTime()).findList();
		for(Timesheet timesheet : timesheetList){
			totalHours += timesheet.hours;
		}
		return totalHours;
	}
	
	public static Set<AppUser> getAppUsers(List<Projects> projectList,Date month) throws ParseException {
		
		List<AppUser> appUserList = new ArrayList<AppUser>();
			for(Projects project : projectList){
				for(AppUser appUser : project.getAppUser()){
					Double totalHours = getTotalHours(appUser,project,month);
					if( totalHours != 0.0d){
						appUserList.add(appUser);
					}
					
				}
			}
			Set<AppUser> appUserSet = new HashSet<AppUser>(appUserList);
				
		return appUserSet;
	}
	
	public Result employeeAppraisal() {
		List<AppUser> teamMemberList = new ArrayList<AppUser>();
		try{
			List<Projects> projectList = Projects.find.where().eq("projectManager", Application.getLoggedInUser()).findList();
			Set<AppUser> appUserSet = getAppUsers(projectList,getMonth());
			for(AppUser appUser : appUserSet){
				Boolean flag = PeEmployeeAppraisal.checkSubmit(appUser.id);
				if(!flag){
					teamMemberList.add(appUser);
				}
			}	
		}catch(Exception e){
			e.printStackTrace();
		}
		return ok(views.html.performance.employeeAppraisal.render(teamMemberList));
	}
	
	public Result employeeAppraisalForm(Long Id) throws ParseException {
		Long teamMemberId = null;
		
		Boolean flag = PeEmployeeAppraisal.checkSubmit(Id);
		if(!flag){
			teamMemberId = Id;
		}
		//Logger.debug("id" +teamMemberId);
		List<PeQuestion> employeeAppraisalQuestionList = PeQuestion.find.where().eq("AppraisalType", PerformanceAppraisalType.Employee_Appraisal).eq("questionStatus", UserProjectStatus.Active).findList();
		return ok(views.html.performance.employeeAppraisalForm.render(employeeAppraisalQuestionList,teamMemberId));
	}
	
	public Result addEmployeeAppraisal() {
		EmployeeAppraisalBean employeeAppraisalBean = Form.form(EmployeeAppraisalBean.class).bindFromRequest().get();
		/*String id = request().body().asFormUrlEncoded().get("teamMemberId")[0];*/
		try{
			//Logger.debug("Team ID"+employeeAppraisalBean.teamMemberId);
			if(employeeAppraisalBean.teamMemberId != null){
				AppUser teamMember = AppUser.find.byId(employeeAppraisalBean.teamMemberId);
				if(!PeEmployeeAppraisal.checkSubmit(employeeAppraisalBean.teamMemberId)){
					
					Double totalRating = 0.0d;
					List<PeEmployeeAppraisalAnswer> answerList = new ArrayList<PeEmployeeAppraisalAnswer>();
					SimpleDateFormat sf =new SimpleDateFormat("MM-yyyy");
					
					PeEmployeeAppraisal employeeAppraisal = new PeEmployeeAppraisal();
					employeeAppraisal.projectManager = Application.getLoggedInUser();
					employeeAppraisal.projectTeamMember = teamMember;
					employeeAppraisal.monthDate = sf.parse(sf.format(getMonth()));
					employeeAppraisal.issue = employeeAppraisalBean.issue;
					for(int i=0;i<employeeAppraisalBean.questionId.size();i++){
						PeEmployeeAppraisalAnswer SEAnswer = new PeEmployeeAppraisalAnswer();
							PeQuestion performanceQuestion = PeQuestion.find.byId(employeeAppraisalBean.questionId.get(i));
							SEAnswer.performanceQuestion = performanceQuestion;
							SEAnswer.rate = employeeAppraisalBean.rate.get(i);
							totalRating += ((performanceQuestion.weightage) * (employeeAppraisalBean.rate.get(i)));
							SEAnswer.answer = employeeAppraisalBean.answer.get(i);
							answerList.add(SEAnswer);
					}
					employeeAppraisal.setPr(Math.round(totalRating/100*100.0)/100.0);
					employeeAppraisal.answerList = answerList;
					
					employeeAppraisal.save();
					flash().put(
							"alert",
							new Alert("alert-success",
									teamMember.getFullName()+" Employee Appraisal Successfully Submited!").toString());
				}else{
					flash().put(
							"alert",
							new Alert("alert-success",
									teamMember.getFullName()+" Employee Appraisal Already Successfully Submited!").toString());
				}
			}else{
				throw new Exception();
			}
				//return redirect(routes.PerformanceController.employeeAppraisal());
		}catch(Exception e){
			e.printStackTrace();
			flash().put(
					"alert",
					new Alert("alert-danger",
							"Your TeamMember Employee Appraisal Not Successfully Submited!").toString());
			//return redirect(routes.PerformanceController.employeeAppraisal());
		}
		return redirect(routes.PEController.employeeAppraisal());
	}
	
	public Result employeeAppraisalHistory() {
		List<AppUser> teamMemberList = new ArrayList<AppUser>();
		try{
			List<Projects> projectList = Projects.find.where().eq("projectManager", Application.getLoggedInUser()).findList();
			Set<AppUser> appUserSet = getAppUsers(projectList,getMonth());
			teamMemberList.addAll(appUserSet);
		}catch(Exception e){
			e.printStackTrace();
		}
		return ok(views.html.performance.employeeAppraisalHistory.render(teamMemberList));
	}
	
	public Result monthEAppraisalHistory(Long Id, String Month) throws ParseException{
		SimpleDateFormat sf =new SimpleDateFormat("MM-yyyy");
		PeEmployeeAppraisal peEmployeeAppraisal = PeEmployeeAppraisal.find.where().eq("projectManager", Application.getLoggedInUser()).
				eq("project_team_member_id", Id).eq("monthDate", sf.parse(Month)).findUnique();
		return ok(views.html.performance.monthEmployeeAppraisalHistory.render(peEmployeeAppraisal));
	}
	
	public Result editEmployeeAppraisal(Long Id) {
		PeEmployeeAppraisal peEmployeeAppraisal = PeEmployeeAppraisal.find.byId(Id);
		return ok(views.html.performance.editEmployeeAppraisal.render(peEmployeeAppraisal));
	}
	
	public Result updateEmployeeAppraisal(){
		EmployeeAppraisalBean employeeAppraisalBean = Form.form(EmployeeAppraisalBean.class).bindFromRequest().get();
		try{
			if(employeeAppraisalBean.id != null){
				PeEmployeeAppraisal employeeAppraisal1 = PeEmployeeAppraisal.find.byId(employeeAppraisalBean.id);
				employeeAppraisal1.Pr = null;
				employeeAppraisal1.answerList.clear();
				employeeAppraisal1.update();
				PeEmployeeAppraisal employeeAppraisal = PeEmployeeAppraisal.find.byId(employeeAppraisalBean.id);
				if(!PeEmployeeAppraisal.checkSubmit(employeeAppraisalBean.teamMemberId)){
					
					Double totalRating = 0.0d;
					List<PeEmployeeAppraisalAnswer> answerList = new ArrayList<PeEmployeeAppraisalAnswer>();
					SimpleDateFormat sf =new SimpleDateFormat("MM-yyyy");
					
					
					employeeAppraisal.monthDate = sf.parse(sf.format(getMonth()));
					employeeAppraisal.issue = employeeAppraisalBean.issue;
					for(int i=0;i<employeeAppraisalBean.questionId.size();i++){
						PeEmployeeAppraisalAnswer SEAnswer = new PeEmployeeAppraisalAnswer();
							PeQuestion performanceQuestion = PeQuestion.find.byId(employeeAppraisalBean.questionId.get(i));
							SEAnswer.performanceQuestion = performanceQuestion;
							SEAnswer.rate = employeeAppraisalBean.rate.get(i);
							totalRating += ((performanceQuestion.weightage) * (employeeAppraisalBean.rate.get(i)));
							SEAnswer.answer = employeeAppraisalBean.answer.get(i);
							answerList.add(SEAnswer);
					}
					employeeAppraisal.setPr(Math.round(totalRating/100*100.0)/100.0);
					employeeAppraisal.answerList = answerList;
					
					employeeAppraisal.save();
					flash().put(
							"alert",
							new Alert("alert-success",
									employeeAppraisal.projectTeamMember.getFullName()+" Employee Appraisal Successfully Updated!").toString());
				}else{
					flash().put(
							"alert",
							new Alert("alert-success",
									employeeAppraisal.projectTeamMember.getFullName()+" Employee Appraisal Already Successfully Updated!").toString());
				}
			}else{
				throw new Exception();
			}
				//return redirect(routes.PerformanceController.employeeAppraisal());
		}catch(Exception e){
			e.printStackTrace();
			flash().put(
					"alert",
					new Alert("alert-danger",
							"Your TeamMember Employee Appraisal Not Successfully Updated!").toString());
			//return redirect(routes.PerformanceController.employeeAppraisal());
		}
		return redirect(routes.PEController.employeeAppraisalHistory());
	}
	
	public Result teamMemberSelfAppraisalHistory() {
		List<AppUser> teamMemberList = new ArrayList<AppUser>();
		try{
			List<Projects> projectList = Projects.find.where().eq("projectManager", Application.getLoggedInUser()).findList();
			Set<AppUser> appUserSet = getAppUsers(projectList,getMonth());
			teamMemberList.addAll(appUserSet);
		}catch(Exception e){
			e.printStackTrace();
		}
		return ok(views.html.performance.teamMemberSelfAppraisalHistory.render(teamMemberList));
	}
	
	public Result monthteamSAppraisalHistory(Long Id,String Month) throws ParseException {
		SimpleDateFormat sf =new SimpleDateFormat("MM-yyyy");
		PeSelfAppraisal peSelfAppraisal = PeSelfAppraisal.find.where().eq("appUser", AppUser.find.byId(Id)).eq("monthDate", sf.parse(Month)).findUnique();
		return ok(views.html.performance.teamMemberSelfAppraisalForm.render(peSelfAppraisal));
	}
	
	
	public Result monthTeamMemberList(String month) throws ParseException {
		SimpleDateFormat sf =new SimpleDateFormat("MM-yyyy");
		Date selectedMonth = sf.parse(month); 
		List<AppUser> teamMemberList = new ArrayList<AppUser>();
		try{
			List<Projects> projectList = Projects.find.where().eq("projectManager", Application.getLoggedInUser()).findList();
			Set<AppUser> appUserSet = getAppUsers(projectList,selectedMonth);
			teamMemberList.addAll(appUserSet);
		}catch(Exception e){
			e.printStackTrace();
		}
		return ok(views.html.performance.monthTeamMemberList.render(teamMemberList));
	}
	
	/* ********************************************** PE DashBoard Methods ************************************************ */

	
	public Result PEDashBoard(){
		return ok(views.html.performance.PEDashBoard.render());
	}
	
	public Result monthEmployeesHistory(String Month) throws ParseException{
		SimpleDateFormat sf = new SimpleDateFormat("MM-yyyy");
		Date selectedMonth = sf.parse(Month);
		List<PEMonthEmployeesDataBean> monthEDataList = new  ArrayList<PEMonthEmployeesDataBean>();
		List<AppUser> employeeList = AppUser.find.where().eq("status", UserProjectStatus.Active).findList();
		for(AppUser appUser : employeeList){
			PeSelfAppraisal peSelfAppraisal = PeSelfAppraisal.find.where().eq("appUser", appUser).eq("monthDate", selectedMonth).findUnique();
			
			Set<PeEmployeeAppraisal> pmSet = new HashSet<PeEmployeeAppraisal>();
			pmSet.clear();
			for(Projects project : appUser.getProjects()){
				PeEmployeeAppraisal peEmployeeAppraisal = PeEmployeeAppraisal.find.where().eq("projectManager", project.getProjectManager()).eq("projectTeamMember", appUser).eq("monthDate", selectedMonth).findUnique();					
				if(peEmployeeAppraisal != null){
					pmSet.add(peEmployeeAppraisal);
				}
			}
			List<PeEmployeeAppraisal> pmList = new ArrayList<PeEmployeeAppraisal>();
			pmList.clear();
			pmList.addAll(pmSet);
			if(peSelfAppraisal != null || !pmList.isEmpty()){
				PEMonthEmployeesDataBean peMonthEmployeesData = new PEMonthEmployeesDataBean();
				peMonthEmployeesData.appUser = appUser;
				peMonthEmployeesData.peSelfAppraisal = peSelfAppraisal;
				peMonthEmployeesData.month = selectedMonth;
				peMonthEmployeesData.pmPeEmployeeAppraisalList = pmList;
				monthEDataList.add(peMonthEmployeesData);
			}/*else{
				peMonthEmployeesData.appUserId = appUser.getId();
				peMonthEmployeesData.FullName = appUser.getFullName();
				peMonthEmployeesData.employeeId = appUser.getEmployeeId();
				peMonthEmployeesData.selfId = null;
				peMonthEmployeesData.Saar = null;
				peMonthEmployeesData.War = null;
			}*/
			
		}
		return ok(views.html.performance.monthEmployeesHistory.render(monthEDataList));
	}
	
	public Result PEMonthSeflHistory(Long Id) throws ParseException {
		//Logger.debug("Id : "+Id);
		PeSelfAppraisal peSelfAppraisal = PeSelfAppraisal.find.byId(Id);
		return ok(views.html.performance.PEMonthSelfHistory.render(peSelfAppraisal));
	}
	
	public Result PEMonthEmployeeHistory(Long Id) {
		//Logger.debug("Id : "+Id);
		PeEmployeeAppraisal peEmployeeAppraisal = PeEmployeeAppraisal.find.byId(Id);
		return ok(views.html.performance.PEMonthEmployeeHistory.render(peEmployeeAppraisal));
	}
	
	public Result PEChart() {
		List<AppUser> employeeList = AppUser.find.where().eq("status", UserProjectStatus.Active).findList();
		return ok(views.html.performance.PEChart.render(employeeList));
	}
	
	public Result PELineChart(Long Id,String StartMonth,String EndMonth) throws ParseException {
		Map<Date,Double> SAARMAP = new LinkedHashMap<Date,Double>();
		Map<Date,Double> WARMAP = new LinkedHashMap<Date,Double>();
		Map<String,Map<Date,Double>> finalRatingMap = new LinkedHashMap<String,Map<Date,Double>>();
		SimpleDateFormat sf =new SimpleDateFormat("MM-yyyy");
		Date startMonth = sf.parse(StartMonth);
		Date endMonth = sf.parse(EndMonth);
		List<PeSelfAppraisal> peSelfAppraisalList = PeSelfAppraisal.find.where().eq("appUser", AppUser.find.byId(Id)).between("monthDate", startMonth, endMonth).findList();
		//Logger.debug("Size :"+peSelfAppraisalList);
		for(PeSelfAppraisal peSelfAppraisal : peSelfAppraisalList){
			//Logger.debug("Size :"+SAARMAP.size());
			SAARMAP.put(peSelfAppraisal.monthDate, peSelfAppraisal.getSaar());
			WARMAP.put(peSelfAppraisal.monthDate, peSelfAppraisal.getWar());
		}
		//Logger.debug("Size :"+SAARMAP.size());
		//Logger.debug("Size :"+WARMAP.size());
		if(!SAARMAP.isEmpty() && !WARMAP.isEmpty()){
			finalRatingMap.put("SAAR", SAARMAP);
			finalRatingMap.put("WAR", WARMAP);
		}
		return ok(Json.toJson(finalRatingMap));
	}
	
	/* **********************************************  Daily && Monthly Sending Mail Methods ************************************************ */
	
	@SuppressWarnings("null")
	public static void CalculateWAR() throws ParseException{
		//Logger.debug("Called");
		SimpleDateFormat sf = new SimpleDateFormat("MM-yyyy");
		Calendar lastMonth = Calendar.getInstance();
		lastMonth.add(Calendar.MONTH, -1);
		Date lastMnth = sf.parse(sf.format(lastMonth.getTime()));
		List<AppUser> employeeList = AppUser.find.where().eq("status", UserProjectStatus.Active).findList();
		for(AppUser appUser : employeeList){
			Double sumPR = 0.0d;
			Double sumHours = 0.0d;
			Double WAR = null;
			for(Projects project : appUser.getProjects()){
				PeEmployeeAppraisal peEmployeeAppraisal = PeEmployeeAppraisal.find.where().eq("projectManager", project.getProjectManager()).eq("projectTeamMember", appUser).eq("monthDate", lastMnth).findUnique();
				Double totalHours = getTotalHours(appUser, project, lastMnth);
				if(peEmployeeAppraisal != null && totalHours != 0.0d){
					//Logger.debug(appUser.getFullName()+", "+peEmployeeAppraisal.getPr()+", "+totalHours);
					sumPR += (peEmployeeAppraisal.getPr() * totalHours);
					sumHours += totalHours;
				}
			}
			if(sumPR != 0.0d && sumHours!= 0.0d){
				WAR = sumPR/sumHours;
			}
			PeSelfAppraisal peSelfAppraisal = PeSelfAppraisal.find.where().eq("appUser", appUser).eq("monthDate", lastMnth).findUnique();
			if(peSelfAppraisal != null){
				if(WAR != null){
					peSelfAppraisal.setWar(Math.round(WAR*100.0)/100.0);
				}else{
					peSelfAppraisal.setWar(WAR);
				}
				peSelfAppraisal.update();
			}
		}
	}
	
	
	public static void sendRedFlagNotificationHRAdmin() throws ParseException{
		SimpleDateFormat sf = new SimpleDateFormat("MMM yyyy");
		
		Set<AppUser> adminHrSet = new HashSet<AppUser>();
		Set<AppUser> AppUserSet = new HashSet<AppUser>();
		List<AppUser> adminHrList = new ArrayList<AppUser>();
		
		Role roleHr = Role.find.where().eq("role", Roles.HR.toString()).findUnique();
		adminHrList = AppUser.find.where().in("role", roleHr).eq("status", UserProjectStatus.Active).findList();
		adminHrSet.addAll(adminHrList);
		
		Role roleAdmin = Role.find.where().eq("role", Roles.Admin.toString()).findUnique();
		adminHrList = AppUser.find.where().in("role", roleAdmin).eq("status", UserProjectStatus.Active).findList();
		adminHrSet.addAll(adminHrList);
		List<PeSelfAppraisal> peSelfAppraisalList = PeSelfAppraisal.find.where().eq("monthDate", getLastMonth()).orderBy("War").findList();
		
		//List<PeEmployeeAppraisal> peEmployeeAppraisalList = PeEmployeeAppraisal.find.where().lt("Pr", 6).eq("monthDate", getMonth()).findList();
		
		String month = sf.format(getLastMonth());
		
		String message = "";/*"Hi ,<br>"+sf.format(getLastMonth())+" Month Appraisal Rating (SAAR And WAR) Less then 6 Employees list Given bellow <br><br>";*/
				message +="<table width=50%; rules=all style=border:1px solid #3A5896; cellpadding=10>"
						+ "<thead>"
						+ "<tr><th align=center>Employee Id</th><th align=center>Employee Name</th><th align=center>SAAR</th><th align=center>WAR</th></tr>"
						+ "</thead>"
						+ "<tbody>";
						for(PeSelfAppraisal peSelfAppraisal :  peSelfAppraisalList){
							message +="<tr><td align=center>"+peSelfAppraisal.appUser.getEmployeeId()+"</td>"
									+ "<td align=center>"+peSelfAppraisal.appUser.getFullName()+"</td>";
							if(peSelfAppraisal.getSaar() < 6){
								message += "<td align=right style=color:red>"+peSelfAppraisal.getSaar()+"</td>";
							}else{
								message += "<td align=right>"+peSelfAppraisal.getSaar()+"</td>";
							}
							if(peSelfAppraisal.getWar() != null && peSelfAppraisal.getWar() < 6){
								message += "<td align=right style=color:red>"+peSelfAppraisal.getWar()+"</td></tr>";
							}else if(peSelfAppraisal.getWar() != null){
								message += "<td align=right>"+peSelfAppraisal.getWar()+"</td></tr>";
							}else{
								message += "<td align=right></td></tr>";
							}
						}
						message +="</tbody></table><br><br>";
				
						sendMailRedFlagNotificationHRAdmin(adminHrSet,message,month);
	}
	
	private static class SMTPAuthenticator extends javax.mail.Authenticator {
		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(Constants.EMAIL_USERNAME,
					Constants.EMAIL_PASSWORD);
		}
	}
	
	public static void sendMailRedFlagNotificationHRAdmin(Set<AppUser> adminHrSet,String message,String month) {
		// mail properties outgoing server (gmail.com)
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "465");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");

		try {
			// create Session obj
			Authenticator auth = new SMTPAuthenticator();

			Session session = Session.getInstance(props, auth);

			// prepare mail msg
			MimeMessage msg = new MimeMessage(session);
			// set header values
			msg.setSubject("Performance Evaluation Report - "+month);
			msg.setFrom(new InternetAddress(Constants.EMAIL_USERNAME));
			List<AppUser> activeHRs = new ArrayList<AppUser>(adminHrSet);
			if(!activeHRs.isEmpty()){
				InternetAddress[] myCcList = new InternetAddress[(activeHRs.size())];
					for(int i=0;i<activeHRs.size();i++){
						myCcList[i] = new InternetAddress(activeHRs.get(i).getEmail());
					}
						msg.setRecipients(Message.RecipientType.TO, myCcList);
			}
			msg.setContent(message, "text/html");

			Transport.send(msg);
		}// try
		catch (Exception ex) {
			ex.printStackTrace();
		}// catch

	}

	
	public static void sendMailRemindersSelf() throws ParseException{
		SimpleDateFormat sf = new SimpleDateFormat("MMM yyyy");
		String subject = "Reminder - Self Appraisal - "+sf.format(getMonth());
		String message = "Hi,<br><br>Please fill your self appraisal for "+sf.format(getMonth())+".<br><br>Thanks & Regards<br>BB8 Team";
		List<AppUser> employeeList = AppUser.find.where().eq("status", UserProjectStatus.Active).findList();
		for(AppUser appUser : employeeList){
			PeSelfAppraisal peSelfAppraisal = PeSelfAppraisal.find.where().eq("appUser", appUser).eq("monthDate", getMonth()).findUnique();
			if(peSelfAppraisal == null){
				sendMailRemindersAll(appUser,subject,message);
			}
		}
		
	}
	
	public static void sendMailRemindersEmployee() throws ParseException{
		Set<AppUser> finalTeamMemberList = new HashSet<AppUser>();
		Set<AppUser> teamMemberList = new HashSet<AppUser>();
		SimpleDateFormat sf = new SimpleDateFormat("MMM yyyy");
		String subject = "Reminder - Fill Your 'PE - Employee Appraisal' "+sf.format(getMonth());
		
		Role managerRole = Role.find.where().eq("role", Roles.Manager.toString()).findUnique();
		List<AppUser> managerList = managerRole.getAppUsers();
		for(AppUser appUser : managerList){
			teamMemberList.clear();
			finalTeamMemberList.clear();
			List<Projects> projectPMList = Projects.find.where().eq("projectManager", appUser).eq("status", UserProjectStatus.Active).findList();
			if(!projectPMList.isEmpty()){
				
				for(Projects Projects : projectPMList){
					teamMemberList.addAll(Projects.getAppUser());
				}
				
				for(AppUser teamMember : teamMemberList){
					if(teamMember.status.equals(UserProjectStatus.Active)){
						PeEmployeeAppraisal peEmployeeAppraisal = PeEmployeeAppraisal.find.where().eq("projectManager", appUser).eq("projectTeamMember", teamMember).eq("monthDate", getMonth()).findUnique();
						if(peEmployeeAppraisal == null){
							finalTeamMemberList.add(teamMember);
						}
					}
				}
				
				if(!finalTeamMemberList.isEmpty()){
					String message = "Hi ,<br><br>Please fill your appraisal for for below team members : <br><br>";
					for(AppUser teamMember : finalTeamMemberList){
						message += teamMember.getFullName()+"<br>";
					}
					message += "<br><br>Thanks & Regards<br>BB8 Team";
					sendMailRemindersAll(appUser,subject,message);
				}
				
			}
		}
		
		
		/*List<Projects> projectList = Projects.find.where().eq("status", UserProjectStatus.Active).findList();
		for(Projects project : projectList){
			finalTeamMemberList.clear();
			for(AppUser teamMember : project.getAppUser()){
				if(teamMember.status.equals(UserProjectStatus.Active)){
					PeEmployeeAppraisal peEmployeeAppraisal = PeEmployeeAppraisal.find.where().eq("projectManager", project.getProjectManager()).eq("projectTeamMember", teamMember).eq("monthDate", getMonth()).findUnique();
					Double totalHours = getTotalHours(teamMember, project, getMonth());
					if(peEmployeeAppraisal == null && totalHours != 0.0d){
						finalTeamMemberList.add(teamMember);
					}
				}
			}
			if(!finalTeamMemberList.isEmpty()){
				String message = "Hi ,<br><br>Please fill your appraisal for for below team members : <br><br>";
				for(AppUser teamMember : finalTeamMemberList){
					message += teamMember.getFullName()+"<br>";
				}
				message += "<br><br>Thanks & Regards<br>BB8 Team";
				sendMailRemindersAll(project.getProjectManager(),subject,message);
			}
		}*/
	}
	
	public static void sendMailRemindersAll(AppUser appUser,String subject,String message) {
		// mail properties outgoing server (gmail.com)
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "465");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");

		try {
			Authenticator auth = new SMTPAuthenticator();
			Session session = Session.getInstance(props, auth);
			MimeMessage msg = new MimeMessage(session);
			msg.setSubject(subject);
			msg.setFrom(new InternetAddress(Constants.EMAIL_USERNAME));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(appUser.getEmail()));
			msg.setContent(message, "text/html");
			Transport.send(msg);
		}// try
		catch (Exception ex) {
			ex.printStackTrace();
		}// catch

	}
	
	  public Result peDummy(){
	    	List<PeQuestion> selfAppraisalQuestionList = PeQuestion.find.where().eq("AppraisalType", PerformanceAppraisalType.Self_Appraisal).eq("questionStatus", UserProjectStatus.Active).findList();
	    	return ok(views.html.performance.selfAppraisalDummy.render(selfAppraisalQuestionList));
	    }
	
}
