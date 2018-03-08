package controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import models.AppUser;
import models.Attendance;
import models.AttendenceStatus;
import models.DailyReport;
import models.Problems;
import models.Projects;
import models.Role;
import models.Roles;
import models.Timesheet;
import models.TimesheetUserRemark;
import models.Todays;
import models.Tomorrows;
import models.UserProjectStatus;
import models.UsersDailyReport;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.Minutes;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import utils.Constants;
import action.BasicAuth;
import action.ManagerAnnotation;
import bean.AppUsersManageTeam;
import bean.AttendanceAverageCalculation;
import bean.DailyStatusDateWise;

public class ManagerController extends Controller {

	/* ***************** Manager home page & ManagerProject methods  *********************** */
	
	public Result home() {
		try {
			return ok(views.html.manager.managerHome.render());
		} catch (Exception e) {
			e.printStackTrace();
			return ok(views.html.error.render(e.getMessage(), 404));
		}
	}

	// list of projects of particular Manager
	public static List<Projects> managerProjects(AppUser appUser) {
		List<Projects> projects = new ArrayList<Projects>();
		projects = Projects.find.where().eq("project_manager_id",appUser.getId()).eq("status", UserProjectStatus.Active).findList();
		return projects;
	}
	
	@BasicAuth
	@ManagerAnnotation
	public Result projects() {
		Map<Role,List<Projects>> finalProjects = new HashMap<Role,List<Projects>>();
		List<Projects> mProjects = new ArrayList<Projects>();
		try {
			AppUser appUser   = AppUser.find.byId(Long.parseLong(session("AppUserId")));
			Role engineerRole = Role.find.where().eq("role", Roles.Engineer.toString()).findUnique();
			Role managerRole  = Role.find.where().eq("role", session("AppUserRole")).findUnique();
			if(appUser != null){
				mProjects = ManagerController.managerProjects(appUser);
				finalProjects.put(managerRole, mProjects);
				
				List<Projects> eProjects = appUser.getProjects();
				for(Projects project : mProjects ){
					if(eProjects.contains(project)){
						eProjects.remove(project);
					}
				}
				finalProjects.put(engineerRole, eProjects);
			}
			return ok(views.html.manager.projects.render(finalProjects));
		} catch (Exception e) {
			e.printStackTrace();
			return ok(views.html.error.render("Projects Not Found", 204));
		}
	}

	@BasicAuth
	@ManagerAnnotation
	// Engineer projects and status report one by one
	public Result appUserProjects(Long id) {
		try {	
			AppUser appUser = AppUser.find.byId(id);
			return ok(views.html.manager.teamStatusReportData.render(appUser));
		} catch (Exception e) {
			return ok(views.html.error.render("Projects Not Found", 204));
		}
	}
	
	// all engineers list for particular manager
	public static Set<AppUser> appUserList(List<Projects> Projects) {
		Set<AppUser> allAppUsers = new HashSet<AppUser>();
		List<AppUser> projectAppUsers = new ArrayList<AppUser>();
		
		for(Projects project : Projects){
			projectAppUsers = project.getAppUsers();
			allAppUsers.addAll(projectAppUsers);
		}
		
		AppUser loginAppUser = AppUser.find.byId(Long.parseLong(session("AppUserId")));
		allAppUsers.remove(loginAppUser);
		return allAppUsers;
	}
		
	/* ***************** Engineer and Manager Daily Report Methods *********************** */
	
	private class SMTPAuthenticator extends javax.mail.Authenticator {
		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(Constants.EMAIL_USERNAME,Constants.EMAIL_PASSWORD);
		}
	}

	// Send mail after fill daily status
	public void sendMail(String email) {
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
			// Now set the actual message
			msg.setText("Status report for today has been submitted successfully, Thanks for your time! \n\n Regards BB8 Team !");
			Transport.send(msg);
		}// try
		catch (Exception ex) {
			ex.printStackTrace();
		}// catch
	}

	@BasicAuth
	@ManagerAnnotation
	// fetch all engineers and engineer available for particular project
	public Result manageTeamMember(String projectId) {
		List<AppUser> allAppUsers = new ArrayList<AppUser>();
		AppUser loginAppUser = AppUser.find.byId(Long.parseLong(session("AppUserId")));
		try {
			allAppUsers = AppUser.find.where().eq("status", UserProjectStatus.Active).findList();
			
			Projects project = Projects.find.byId(Long.parseLong(projectId));
			List<AppUser> projectAppUsers = project.getAppUsers();
			allAppUsers.removeAll(projectAppUsers);
			
			projectAppUsers.remove(loginAppUser);
			allAppUsers.remove(loginAppUser);
			return ok(views.html.manager.manageTeamMembersData.render(allAppUsers,projectAppUsers, project.projectManager));
		} catch (Exception e) {
			return ok(views.html.error.render(e.getMessage(), 204));
		}

	}

	@BasicAuth
	@ManagerAnnotation
	// update Engineers list for particular project
	public Result manageTeamMemberAction() {
		try {
			AppUsersManageTeam manageTeam = Form.form(AppUsersManageTeam.class)
					.bindFromRequest().get();
			List<Long> fullName = manageTeam.getAppUsersSome();
			List<AppUser> appUsersList = new ArrayList<AppUser>();
			String projectName = manageTeam.getProjectName();
			Projects project = Projects.find.byId(Long.parseLong(projectName));
			AppUser appUsersObj = null;
			if (fullName != null) {
				for (int i = 0; i < fullName.size(); i++) {
					appUsersObj = AppUser.find.byId(fullName.get(i));
					appUsersList.add(appUsersObj);
				}
				appUsersObj = AppUser.find.byId(Long
						.parseLong(session("AppUserId")));
				appUsersList.add(appUsersObj);
				project.setAppUsers(appUsersList);
				project.save();
			} else {
				project.setAppUsers(appUsersList);
				project.save();
			}

			AppUser user = AppUser.find.byId(Long
					.parseLong(session("AppUserId")));
			List<Projects> myProjects = managerProjects(user);
			// flash().put("alert",new Alert("alert-success",
			// "Team Members update successfully!").toString());
			return ok(views.html.manager.manageTeamMembers.render(
					"Team Members update successfully!", myProjects));
		} catch (Exception e) {
			return ok(views.html.error.render(
					"Managing Team members not possible at this time", 500));
		}

	}

	@BasicAuth
	@ManagerAnnotation
	// fetch all engineer and engineer for particular project
	public Result manageTeamMembers() {
		try {

			AppUser user = AppUser.find.byId(Long
					.parseLong(session("AppUserId")));
			List<Projects> myProjects = ManagerController.managerProjects(user);
			List<AppUser> appUsers1 = new ArrayList<AppUser>();
			appUsers1 = AppUser.find.where().eq("status", UserProjectStatus.Active).findList();
			List<AppUser> appUser2 = new ArrayList<AppUser>();

			List<AppUser> appUser3 = new ArrayList<AppUser>();

			Projects project = Form.form(Projects.class).bindFromRequest()
					.get();
			Projects project1 = Projects.find.where().eq("projectName", null)
					.findUnique();
			if (project1 != null) {
				List<AppUser> appUser = project1.getAppUsers();
				Iterator<AppUser> itr3 = appUser.iterator();
				AppUser appUserObj3 = null;
				while (itr3.hasNext()) {
					appUserObj3 = itr3.next();
					if (appUserObj3.getId() == Long
							.parseLong(session("AppUserId"))
							|| appUserObj3.status
									.equals(UserProjectStatus.Inactive))
						;
					else {
						appUser3.add(appUserObj3);
					}
				}

				AppUser appUserObj1 = null;
				AppUser appUserObj2 = null;
				boolean flag = false;
				Iterator<AppUser> itr1 = appUsers1.iterator();
				while (itr1.hasNext()) {
					appUserObj1 = itr1.next();
					if ((appUserObj1.status.equals(UserProjectStatus.Inactive))
							|| appUserObj3.getId() == Long
									.parseLong(session("AppUserId"))) {
						continue;
					}
					Iterator<AppUser> itr2 = appUser.iterator();
					while (itr2.hasNext()) {
						appUserObj2 = itr2.next();
						if (appUserObj1.equals(appUserObj2)
								|| appUserObj1.getId() == Long
										.parseLong(session("AppUserId"))) {
							flag = true;
							break;
						}
					}
					if (flag) {
						flag = false;
					} else {
						appUser2.add(appUserObj1);
					}
				}
			}
			return ok(views.html.manager.manageTeamMembers.render("",
					myProjects));
		} catch (Exception e) {
			return ok(views.html.error.render(e.getMessage(), 204));
		}

	}

	@BasicAuth
	@ManagerAnnotation
	// check todays status fill or not
	public Result fillStatus() {
		List<Projects> projects = new ArrayList<Projects>();
		Set<Projects> setProjects = new HashSet<Projects>();
		try {
				projects = Projects.find.where().eq("project_manager_id",Long.parseLong(session("AppUserId"))).eq("status", UserProjectStatus.Active).findList();
				if(!projects.isEmpty()){
					setProjects.addAll(projects);
				}
				AppUser appUser = AppUser.find.byId(Long.parseLong(session("AppUserId")));
				List<Projects> eProjects = appUser.getProjects();
				if(!eProjects.isEmpty()){
					setProjects.addAll(eProjects);
				}
				if(!setProjects.isEmpty()){
					projects.clear();
					projects.addAll(setProjects);
				}
				if (EngineerController.getIsDone()) {
					return ok(views.html.manager.fillStatus1.render("false",projects));
				} else {
					return ok(views.html.manager.fillStatus1.render("true",projects));
				}
		} catch (Exception e) {
			return ok(views.html.error.render(e.getMessage(), 204));
		}
	}

	@BasicAuth
	@ManagerAnnotation
	// daily status history
	public Result statusHistory() {
			return ok(views.html.manager.statusHistory.render());
	}

	@BasicAuth
	@ManagerAnnotation
	// particular manager team status history
	public Result teamStatusReport() {
		try {
			AppUser appUser = AppUser.find.byId(Long.parseLong(session("AppUserId")));
			List<Projects> myProjects = ManagerController.managerProjects(appUser);
			Set<AppUser> appUserSet = ManagerController.appUserList(myProjects);
			
			return ok(views.html.manager.teamStatusReport.render(appUserSet));
		} catch (Exception e) {
			return ok(views.html.error.render(e.getMessage(), 204));
		}
	}

	@BasicAuth
	@ManagerAnnotation
	// daily status week wise one by one engineer
	public Result dailyStatusWeekwiseUser(String date1, String date2, Long id) {
		List<UsersDailyReport> usersDailyReportList = new ArrayList<UsersDailyReport>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		try {
			DailyReport dailyReport = DailyReport.find.where().eq("app_user_id", id).findUnique();
			if (dailyReport != null) {
				try {
					Date sdate = sdf.parse(date1);
					Date edate = sdf.parse(date2);
					usersDailyReportList = UsersDailyReport.find.where().eq("daily_report_id", dailyReport.getId()).between("date", sdate, edate).findList();
				} catch (Exception e) {
					e.printStackTrace();
				}
			Collections.reverse(usersDailyReportList);
			}
			return ok(views.html.engineer.dailyStatusHistoryWeekwise
					.render(usersDailyReportList));
		} catch (Exception e) {
			return ok(views.html.error.render(e.getMessage(), 204));
		}

	}

	@BasicAuth
	@ManagerAnnotation
	public Result dailyStatusWeekwise(String date1, String date2) {
		List<UsersDailyReport> usersDailyReports = new ArrayList<UsersDailyReport>();
		List<UsersDailyReport> usersDailyReportList = new ArrayList<UsersDailyReport>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		try {
			DailyReport dailyReport = DailyReport.find.where().eq("app_user_id", Long.parseLong(session("AppUserId"))).findUnique();
			if (dailyReport != null) {
				try {
					Date sdate = sdf.parse(date1);
					Date edate = sdf.parse(date2);
					usersDailyReportList = UsersDailyReport.find.where().eq("daily_report_id", dailyReport.getId()).between("date", sdate, edate).findList();
				} catch (Exception e) {
					e.printStackTrace();
				}
				Collections.reverse(usersDailyReportList);
			}
			return ok(views.html.manager.dailyStatusHistoryWeekwise.render(usersDailyReportList));
		} catch (Exception e) {
			return ok(views.html.error.render(e.getMessage(), 204));
		}

	}

	public Result dailyStatusDateWiseRender() {
		return ok(views.html.manager.statusHistoryDateWise.render());
	}

	private List<DailyStatusDateWise> dailyStatusDateWise1(String date,AppUser appUser) {
		List<DailyStatusDateWise> dailyStatusDatewiseList = new ArrayList<DailyStatusDateWise>();
		if (date != null) {
			try {
				List<Projects> myProjects = ManagerController.managerProjects(appUser);
				Set<AppUser> appUsersList = ManagerController.appUserList(myProjects);
				for (AppUser appuser : appUsersList) {
					DailyReport dailyReport = DailyReport.find.where().eq("app_user_id", appuser.getId()).findUnique();
					if (dailyReport != null) {
						List<UsersDailyReport> userDailyReports = dailyReport.getUsersDailyReport();
						if (!userDailyReports.isEmpty()) {
							for (UsersDailyReport userDailyReport : userDailyReports) {
								SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
								Date sdate = sdf.parse(date);
								String udate = sdf.format(userDailyReport.getDate());
								Date udate1 = sdf.parse(udate);
								if (udate1.equals(sdate)) {
									DailyStatusDateWise dailyStatusDateWise = new DailyStatusDateWise();
									dailyStatusDateWise.setUsersDailyReport(userDailyReport);
									dailyStatusDateWise.setAppUser(appuser);
									dailyStatusDatewiseList.add(dailyStatusDateWise);
								}
							}
						}
					}
				}

			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		try {
			String excelFileName = "conf/excel/dailystatus.xls";
			String sheetName = "dailystatus" + date;

			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet(sheetName);
			CellStyle style = wb.createCellStyle();
			style.setWrapText(true);
			sheet.setDefaultColumnWidth(25);
			
			Iterator<DailyStatusDateWise> itr = dailyStatusDatewiseList
					.iterator();
			DailyStatusDateWise s;

			HSSFRow row1 = sheet.createRow(0);
			row1.setRowStyle(style);
			HSSFCell cell13 = row1.createCell(0);
			cell13.setCellStyle(style);
			cell13.setCellValue("S.No.");
			HSSFCell cell4 = row1.createCell(1);
			cell4.setCellValue("Engineer Name");
			HSSFCell cell5 = row1.createCell(2);
			cell5.setCellValue("Date");
			HSSFCell cell6 = row1.createCell(3);
			cell6.setCellValue("Yesterday's Work");
			HSSFCell cell7 = row1.createCell(4);
			cell7.setCellValue("Plan For Today's");
			HSSFCell cell8 = row1.createCell(5);
			cell8.setCellValue("Problem Faced");
			HSSFCell cell9 = row1.createCell(6);
			cell9.setCellValue("Rating");

			int i = 1;
			while (itr.hasNext()) {
				s = itr.next();
				sheet.setDefaultRowHeightInPoints(50);
				HSSFRow row = sheet.createRow(i);
				row.setRowStyle(style);
				// iterating c number of columns
				HSSFCell cell12 = row.createCell(0);
				cell12.setCellStyle(style);
				cell12.setCellValue(i);

				HSSFCell cell = row.createCell(1);

				cell.setCellValue(s.getAppUser().FullName);
				HSSFCell cell1 = row.createCell(2);

				cell1.setCellValue(s.getUsersDailyReport().getDate()
						.toLocaleString());
				HSSFCell cell2 = row.createCell(3);
				List<Todays> today1 = s.getUsersDailyReport().getToday();
				List<String> todays = new ArrayList<String>();
				String s1 = "";
				int count = 1;
				for (Todays to : today1) {
					s1 = s1 + " " + count + "." + to.getToday() + "\n";
					count++;
				}
				cell2.setCellStyle(style);
				cell2.setCellValue(s1);
				s1 = "";
				count = 1;
				HSSFCell cell3 = row.createCell(4);

				List<Tomorrows> p2 = s.getUsersDailyReport().getTomorrow();
				List<String> ps2 = new ArrayList<String>();
				for (Tomorrows ps : p2) {
					s1 = s1 + " " + count + "." + ps.getTomorrow() + "\n";
					count++;
				}
				cell3.setCellValue(s1);
				HSSFCell cell10 = row.createCell(5);
				List<Problems> p = s.getUsersDailyReport().getProblem();
				List<String> ps1 = new ArrayList<String>();
				s1 = "";
				count = 1;
				for (Problems ps : p) {
					s1 = s1 + " " + count + "." + ps.getProblem() + "\n";
					count++;
				}
				cell10.setCellValue(s1);
				HSSFCell cell11 = row.createCell(6);
				cell11.setCellValue(s.getUsersDailyReport().getRate());
				i++;

			}

			FileOutputStream fileOut = new FileOutputStream(excelFileName);

			// write this workbook to an Outputstream.
			wb.write(fileOut);
			fileOut.flush();
			fileOut.close();
		} catch (Exception e) {

		}
		return dailyStatusDatewiseList;
	}

	public Result dailyStatusDateWise(String date) {

		AppUser appUser = AppUser.find.byId(Long.parseLong(session("AppUserId")));
		List<DailyStatusDateWise> dailyStatusDatewiseList = dailyStatusDateWise1(date, appUser);

		return ok(views.html.manager.dailyStatusHistoryDatewiseData.render(
				dailyStatusDatewiseList, date));

	}

	public Result exlsheetDailystatusDateWise(String date) {
		AppUser appUser = AppUser.find.byId(Long
				.parseLong(session("AppUserId")));
		List<DailyStatusDateWise> dailyStatusDatewiseList = dailyStatusDateWise1(
				date, appUser);

		response().setContentType("application/x-download");
		response().setHeader("Content-disposition",
				"attachment; filename=" + date + "status.xls");
		return ok(new File("conf/excel/dailystatus.xls"));

	}

	public void mailSendingHowManyEmpFillAndHowManyNotSendManager() {

		Role role = Role.find.where().eq("role", "Manager").findUnique();
		List<AppUser> appUserList = role.appUser;
		for (AppUser user : appUserList) {
			List<Projects> myProjects = ManagerController.managerProjects(user);
			Set<AppUser> appUser1 = ManagerController.appUserList(myProjects);
			int countSender = 0;
			int countNonSender = 0;
			boolean flag = false;
			List<AppUser> nonSenderList = new ArrayList<AppUser>();
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			for (AppUser appUser : appUser1) {
				DailyReport dailyReport = DailyReport.find.where()
						.eq("appUserId", appUser.getId()).findUnique();
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
				if (flag) {
					countSender++;
					flag = false;
				} else {
					countNonSender++;
					nonSenderList.add(appUser);
				}
			}
			Properties props = new Properties();
			props.put("mail.smtp.host", Constants.EMAIL_HOST);
			props.put("mail.smtp.port", Constants.EMAIL_PORT);
			props.put("mail.smtp.auth",Constants.EMAIL_AUTH);
			//props.put("mail.smtp.socketFactory.class",
					//"javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.starttls.enable",
					Constants.EMAIL_STARTTLS_ENABLE);
			try {
				// create Session obj
				String tDate = sdf.format(new Date());
				dailyStatusDateWise1(tDate, user);
				Authenticator auth = new SMTPAuthenticator();
				Session session = Session.getInstance(props, auth);
				// prepare mail msg
				MimeMessage msg = new MimeMessage(session);

				// set header values
				msg.setSubject("Day wise Daily Status ");
				msg.setFrom(new InternetAddress(Constants.EMAIL_FROM));
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
						user.email));
				// msg text
				// Create the message part
				// BodyPart messageBodyPart = new MimeBodyPart();
				// Now set the actual message
				String message = "No. of Users filled daily status: "
						+ countSender + "\n"
						+ "No. of Users not filled daily status: "
						+ countNonSender + "\n"
						+ "List of the Users did not fill:\n";
				// msg2.setText("List of the Users did not fill:");
				for (int i = 0; i < nonSenderList.size(); i++) {
					message = message
							+ ((i + 1) + ". " + nonSenderList.get(i)
									.getFullName()) + "\n";
				}
				BodyPart messageBodyPart = new MimeBodyPart();

				// Now set the actual message
				// messageBodyPart.setText("This is message body");
				messageBodyPart.setText(message);
				// Create a multipar message
				Multipart multipart = new MimeMultipart();

				// Set text message part
				multipart.addBodyPart(messageBodyPart);

				// Part two is attachment
				messageBodyPart = new MimeBodyPart();
				String filename = "conf/excel/dailystatus.xls";
				DataSource source = new FileDataSource(filename);
				messageBodyPart.setDataHandler(new DataHandler(source));
				messageBodyPart.setFileName(new Date() + ".dailystatus.xls");
				multipart.addBodyPart(messageBodyPart);

				// Send the complete message parts
				msg.setContent(multipart);

				// Create a multipar message
				// Send the complete message parts
				// msg.setContent(messageBodyPart);
				// msg.setText(message);

				Transport.send(msg);
			}// try
			catch (Exception ex) {
				ex.printStackTrace();
			}// catch

		}
	}
	
	/* ***************** Manager Time Sheet Methods *********************** */
	
	@BasicAuth
	@ManagerAnnotation
	public Result fillTimesheet() {
		List<Projects> projects = new ArrayList<Projects>();
		Set<Projects> setProjects = new HashSet<Projects>();
		try {
			projects = ManagerController.managerProjects(AppUser.find.byId(Long.parseLong(session("AppUserId"))));
			if(!projects.isEmpty()){
				setProjects.addAll(projects);
			}
			AppUser appUser = AppUser.find.byId(Long.parseLong(session("AppUserId")));
			List<Projects> eProjects = appUser.getProjects();
			if(!eProjects.isEmpty()){
				setProjects.addAll(eProjects);
			}
			if(!setProjects.isEmpty()){
				projects.clear();
				projects.addAll(setProjects);
			}
			return ok(views.html.manager.fillTimesheet.render(projects));
		} catch (Exception e) {
			return ok(views.html.error.render(e.getMessage(), 204));
		}
	}

	@BasicAuth
	@ManagerAnnotation
	public Result timesheetHistory() {
		List<Projects> projects = new ArrayList<Projects>();
		try {
			AppUser appUser = AppUser.find.byId(Long.parseLong(session("AppUserId")));
			projects = ManagerController.managerProjects(appUser);
			projects.addAll(appUser.getProjects());
			return ok(views.html.manager.timesheetHistory.render(projects));
		} catch (Exception e) {
			return ok(views.html.error.render(e.getMessage(), 204));
		}
	}

	@BasicAuth
	@ManagerAnnotation
	public Result teamTimesheetReport() {
		Set<AppUser> appUsersSet = new HashSet<AppUser>();
		List<Projects> myProjects = new ArrayList<Projects>();
		try {
				myProjects = ManagerController.managerProjects(AppUser.find.byId(Long.parseLong(session("AppUserId"))));
				appUsersSet = ManagerController.appUserList(myProjects);
				
			return ok(views.html.manager.teamTimesheetReport.render(appUsersSet,myProjects));
		} catch (Exception e) {
			return ok(views.html.error.render(e.getMessage(), 204));
		}
	}

	public Result teamTimesheetReportDate(){
		return ok(views.html.manager.teamTimeSheetDate.render());
	}
	
	public Result teamTimeSheetsDateWise(String date) {
		Set<AppUser> appUserSet = new HashSet<AppUser>();
		Map<AppUser, List<Timesheet>> finalMap = new HashMap<AppUser, List<Timesheet>>();
		Map<AppUser, TimesheetUserRemark> finalMaprmk = new HashMap<AppUser, TimesheetUserRemark>();
		List<Timesheet> timeSheets = new ArrayList<Timesheet>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		try {
			appUserSet = ManagerController.appUserList(ManagerController.managerProjects(AppUser.find.byId(Long.parseLong(session("AppUserId")))));
			Date todayDate = sdf.parse(date);
			for(AppUser appUser : appUserSet){
				timeSheets = Timesheet.find.where().eq("app_user_id", appUser.getId()).eq("date", todayDate).findList();
				if(!timeSheets.isEmpty()){
					if(!finalMap.containsKey(appUser)) {
						finalMap.put(appUser, timeSheets);
					}
				}
				
				TimesheetUserRemark timesheetUserRemark = TimesheetUserRemark.find.where().eq("app_user_id", appUser.getId()).eq("date", todayDate).findUnique();
				if(timesheetUserRemark != null){
					if(!finalMaprmk.containsKey(appUser)) {
						finalMaprmk.put(appUser, timesheetUserRemark);
					}
				}
			}
			return ok(views.html.admin.getTimeSheetsDateWise.render(finalMap,finalMaprmk));
		}catch(ParseException e){
			e.printStackTrace();
			return ok(views.html.admin.getTimeSheetsDateWise.render(finalMap,finalMaprmk));
		}
	}
	/* ***************** Manager Attendance Methods *********************** */
	
	public Result attendanceHistory() {
		return ok(views.html.manager.attendanceHistory.render());
	}
	
	//My History Details of Manager along with report
	public Result attendanceHistoryManagerWeekwise(String sDate,String eDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		List<Attendance> attendancesList=new ArrayList<>();
		Date sdate=null;
		Date edate=null;
		Long id = Long.parseLong(session("AppUserId"));
		
		try {
			 sdate = sdf.parse(sDate);
			 edate = sdf.parse(eDate);
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
		return ok(views.html.manager.userAttendanceWeekwise.render(attendancesList,sd,ed,id));	
	}
	
	public Result userwiseManagerAttendance() {
		AppUser user = AppUser.find.byId(Long
				.parseLong(session("AppUserId")));
		List<Projects> myProjects = ManagerController.managerProjects(user);
		Set<AppUser> appUser1 = ManagerController.appUserList(myProjects);
		List<AppUser> appUsers=new ArrayList<>(appUser1);
		return ok(views.html.manager.UserWiseAttendance.render(appUsers));	
	}
	
	// User wise wise weekly attendence data in table and report
	public Result attendanceUserwiseWeek(String startDate, String endDate,Long id) {
		List<Attendance> attendancesList = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Date sdate=null;
		Date edate=null;
		try {
			 sdate = sdf.parse(startDate);
			 edate = sdf.parse(endDate);
			AppUser user = AppUser.find.byId(id);
			List<Attendance> attendances = user.attendences;

			for (Attendance attendance : attendances) {
				if (attendance.getDate().equals(sdate)
						|| attendance.getDate().after(sdate)
						&& attendance.getDate().before(edate)
						|| attendance.getDate().equals(edate)) {
					attendancesList.add(attendance);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		String sd=sdf1.format(sdate);
		String ed=sdf1.format(edate);
		
		return ok(views.html.manager.userAttendanceWeekwise.render(attendancesList,sd,ed,id));
	}

	public Result attendanceDatewiseManager() {
		return ok(views.html.manager.attendanceWeekwise
				.render());
	}
	
	public Result attendanceDatewiseManager1(String sDate,String eDate) {
		
		AppUser user = AppUser.find.byId(Long
				.parseLong(session("AppUserId")));
		List<Projects> myProjects = ManagerController.managerProjects(user);
		Set<AppUser> appUser1 = ManagerController.appUserList(myProjects);
		List<AppUser> appUsers=new ArrayList<>(appUser1);
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Date sdate = null;
		Date edate = null;
		try {
			sdate = sdf.parse(sDate);
			edate = sdf.parse(eDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		List<Attendance> attendancesList2 = null;
		List<AttendanceAverageCalculation> attendanceList1 = new ArrayList<>();
			for (int i = 0; i < 7; i++) {
				final Calendar calendar = Calendar.getInstance();
				calendar.setTime(sdate);
				if (i != 0) {
					calendar.add(Calendar.DAY_OF_YEAR, i);
				}
				Date d1 = calendar.getTime();
				List<Attendance> attendancesList = new ArrayList<>();
				//attendancesList = Attendance.find.where().eq("date", d1).findList();
				for(AppUser appUser:appUsers)
				{
					attendancesList2=appUser.attendences;
					if(!attendancesList2.isEmpty()){
						for(Attendance attendance:attendancesList2)
						{
						    if(attendance.getDate().equals(d1))	{
						    	attendancesList.add(attendance);
						    }
						}
					}
				}
				
				AttendanceAverageCalculation attendanceObj = new AttendanceAverageCalculation();
				int inh = 0, inm = 0, outh = 0, outm = 0, absentCount = 0, wfh = 0,esslInH=0,esslInM=0,esslOutH=0,esslOutM=0;
				 if(attendancesList!=null&&!attendancesList.isEmpty()){
				for (Attendance attendance : attendancesList) {
					String s = attendance.getSpendTime();
					if (attendance.getStatus()!=null&&attendance.getStatus().equals(AttendenceStatus.Absent)) {
						absentCount++;
					} else if (attendance.getStatus()!=null&&attendance.getStatus().equals(AttendenceStatus.WFH)) {
						wfh++;
					} else {
						if(attendance.getInTime()!=null&&attendance.getOutTime()!=null){
						Date intime = attendance.getInTime();
						DateTime d = new DateTime(intime);
						inh += d.getHourOfDay();
	
						inm += (d.getMinuteOfDay()) % 60;
	
						Date outtime = attendance.getOutTime();
						DateTime d2 = new DateTime(outtime);
						outh += d2.getHourOfDay();
	
						outm += (d2.getMinuteOfDay()) % 60;
						}
						if(attendance.getEsslIntime()!=null&&attendance.getEsslOuttime()!=null){
							Date esslInTime = attendance.getEsslIntime();
							DateTime d = new DateTime(esslInTime);
							esslInH += d.getHourOfDay();
	
							esslInM += (d.getMinuteOfDay()) % 60;
	
							Date esslOutTime = attendance.getEsslOuttime();
							DateTime d2 = new DateTime(esslOutTime);
							esslOutH += d2.getHourOfDay();
	
							esslOutM += (d2.getMinuteOfDay()) % 60;
						}
					}
				}
	
				if (attendancesList.size() - (absentCount + wfh) != 0) {
					String inDate1 = AdminController.inOutDateCalculation(attendancesList.size(),inh,inm,absentCount,wfh);
					String outDate1 = AdminController.inOutDateCalculation(attendancesList.size(),outh,outm,absentCount,wfh);
					String esslInDate=AdminController.inOutDateCalculation(attendancesList.size(),esslInH,esslInM,absentCount,wfh);
					String esslOutDate=AdminController.inOutDateCalculation(attendancesList.size(),esslOutH,esslOutM,absentCount,wfh);
					
					Date inDate = null;
					Date outDate = null;
					Date esslInDate1=null;
					Date esslOutDate1=null;
	
					try {
	
						String dateString = sdf.format(d1);
						
						inDate = AdminController.formatingDate(dateString,inDate1 );
						outDate =AdminController.formatingDate(dateString,outDate1 );
						esslInDate1=AdminController.formatingDate(dateString,esslInDate );
						esslOutDate1=AdminController.formatingDate(dateString,esslOutDate);
						if(inDate.equals(outDate));
						else{
						attendanceObj.inTime = inDate;
						attendanceObj.outTime = outDate;
						}
						if(esslInDate1.equals(esslOutDate1));
						else{
						attendanceObj.esslInTime=esslInDate1;
						attendanceObj.esslOutTime=esslOutDate1;
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					DateTime dt1 = new DateTime(inDate);
					DateTime dt2 = new DateTime(outDate);
					DateTime dt3 = new DateTime(esslInDate1);
					DateTime dt4 = new DateTime(esslOutDate1);
	
					/*
					 * long diffSeconds = diff / 1000 % 60; long diffMinutes = diff
					 * / (60 * 1000) % 60; long diffHours = diff / (60 * 60 * 1000);
					 */
					long diffHours = Hours.hoursBetween(dt1, dt2).getHours() % 24;
					long diffMinutes = Minutes.minutesBetween(dt1, dt2)
							.getMinutes() % 60;
					long diffHours1 = Hours.hoursBetween(dt3, dt4).getHours() % 24;
					long diffMinutes1 = Minutes.minutesBetween(dt3, dt4)
							.getMinutes() % 60;
					
	
					 if(diffHours==0&&diffMinutes==0);
		                else{
						attendanceObj.spendTime = "" + diffHours + ":" + diffMinutes;
		                }
		                if(diffHours1==0&&diffMinutes1==0);
		                else{
						attendanceObj.esslSpendTime = "" + diffHours1 + ":" + diffMinutes1;
		                }
				}
				// if(!attendancesList.isEmpty()){
				attendanceObj.absent = absentCount;
				attendanceObj.present = attendancesList.size()
						- (absentCount + wfh);
				attendanceObj.WFH = wfh;
				attendanceObj.date = d1;
				attendanceObj.notFilled = appUsers.size()
						- (absentCount + wfh + (attendancesList.size() - (absentCount + wfh)));
				// }
	
				attendanceList1.add(attendanceObj);
	
			}
		}
			/*List<Attendance> attendanceList = new ArrayList<>();
			List<Attendance> attendanceList1 = new ArrayList<>();
			Date date1=null;
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
				date1 = sdf.parse(date);
				attendanceList = Attendance.find.orderBy("date").where().eq("date", date1)
						.findList();
			} catch (Exception e) {
				e.printStackTrace();
			}
			for(AppUser appUser:appUsers)
			{
				attendanceList=appUser.attendences;
				for(Attendance attendance:attendanceList)
				{
				    if(attendance.getDate().equals(date1))	
					attendanceList1.add(attendance);
				}
			}
			return ok(views.html.manager.attendanceWeekWiseData
					.render(attendanceList1));*/
		return ok(views.html.manager.attendanceWeekWiseData
				.render(attendanceList1));
	}
	
}//ManagerController closed
