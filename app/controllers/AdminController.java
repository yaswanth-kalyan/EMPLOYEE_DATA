package controllers;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Seconds;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.Model;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.google.common.io.Files;

import action.AdminAnnotation;
import action.AdminHRAnnotation;
import action.BasicAuth;
import bean.AppUsersManageTeam;
import bean.AttendanceAverageCalculation;
import bean.AttendenceBean;
import bean.AttendenceEditBean;
import bean.Bean;
import bean.ContactBean;
import bean.DailyStatusDateWise;
import bean.ProjectBean;
import models.Alert;
import models.AppUser;
import models.Attendance;
import models.AttendenceStatus;
import models.BiometricAttendance;
import models.Contact;
import models.DailyReport;
import models.Gender;
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
import models.chat.ChatGroup;
import models.chat.ChatGroupAppUserInfo;
import models.chat.GroupType;
import models.chat.Message;
import models.incident.CIRType;
import models.incident.Incident;
import models.incident.Policy;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.Results;
import utils.ConstructReportUrl;

/**
 * 
 * @author SureshNamburi
 *
 */
public class AdminController extends Controller {

	
	public static void generateRandomId() {
		int i = 0;
		for (Message message : Message.find.all()) {
			if (message.randomId == null || message.randomId.trim().equalsIgnoreCase("")) {
				synchronized (message) {
					String randomId = new SimpleDateFormat("DDMMYYYYhhmmssSSSSSSS").format(new Date());
					message.randomId = randomId + ++i;
					message.update();
				}
			}
		}

	}

	public static void addUserNamesAllApppUsers() {
		List<AppUser> apppUserList = AppUser.find.all();
		for (AppUser appUser : apppUserList) {
			if (appUser.getUserName() == null) {
				String email = appUser.getEmail();
				appUser.setUserName(email.substring(0, email.indexOf('@')));
				appUser.update();
			}
		}
	}

	public static void addGitHubUser() {
		try {
			AppUser admin = AppUser.find.where().eq("email", "github@thrymr.net").findUnique();
			if (admin == null) {
				AppUser admin1 = new AppUser();
				admin1.setEmail("github@thrymr.net");
				admin1.setPassword(Application.encode("github"));
				admin1.setFullName("Github");
				File file = new File("conf/images/thrymr.png");
				try {
					admin1.setImage(Files.toByteArray(file));
					admin1.setThumbnail(ChatController.scale(Files.toByteArray(file)));
				} catch (IOException e) {
					e.printStackTrace();
				}
				admin1.setUserName("github");
				admin1.setMobileNo(1234567890l);
				admin1.setGender(Gender.Male);
				admin1.setGitId("bb8@github");
				Role roles = Role.find.where().eq("role", "Engineer").findUnique();
				List<Role> lRole = new ArrayList<Role>();
				lRole.add(roles);
				admin1.setRole(lRole);
				admin1.setStatus(UserProjectStatus.Inactive);
				admin1.setIsPasswordChange(true);
				admin1.setLoginCheck(true);

				admin1.save();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	
	/**
	 * @author sureshnamburi
	 * @return void 
	 * @category - Admin Registration
	 * 
	 */
	
	public static void admin() {
		try {
			Role role1 = Role.find.where().eq("role", "Admin").findUnique();
			if (role1 == null) {
				AppUser admin1 = new AppUser();
				admin1.setEmail("Admin@gmail.com");
				admin1.setPassword(Application.encode("Admin"));
				admin1.setFullName("Administration");
				File file = new File("conf/images/thrymr.png");
				try {
					admin1.setImage(Files.toByteArray(file));
					admin1.setThumbnail(ChatController.scale(Files.toByteArray(file)));
				} catch (IOException e) {
					e.printStackTrace();
				}
				admin1.setMobileNo(9874561230l);
				admin1.setGender(Gender.Male);

				for (Roles roles : Roles.values()) {
					Role role = new Role();
					role.setRole(roles.toString());
					role.save();
				}

				Role roles = Role.find.where().eq("role", "Admin").findUnique();
				List<Role> lRole = new ArrayList<Role>();
				lRole.add(roles);
				admin1.setRole(lRole);
				admin1.setStatus(UserProjectStatus.Active);
				admin1.setIsPasswordChange(true);
				admin1.setLoginCheck(true);
				admin1.setUserName("Admin");
				admin1.save();
				
				//create General ChartGroup;
				
				ChatGroup group = new ChatGroup();
				group.createdBy = admin1;
				group.name = "General";
				group.groupType = GroupType.PUBLIC;
				group.description = "";
				group.save();
				
				if(group != null){
					ChatGroupAppUserInfo groupMember = new ChatGroupAppUserInfo();
					groupMember.appUser = admin1;
					groupMember.chatGroup = group;
					groupMember.save();
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @author sureshn
	 * @category Admin Home Page
	 * @return Result
	 */
	@AdminAnnotation
	@BasicAuth
	public Result home() {
		return ok(views.html.admin.homePage.render());
	}

	/* ***************** AppUser Action Methods *********************** */

	/**
	 * @author sureshnamburi
	 * @param id
	 * @return Result
	 * @category - Edit AppUser
	 */
	@BasicAuth
	public Result editUser(Long id) {
		AppUser appUser = AppUser.find.byId(id);
		return ok(views.html.admin.edituser.render(appUser));
	}

	
	/**
	 * @author sureshnamburi
	 * @param fileData
	 * @return byte[]
	 * @category - image compressed method (35 X 35) size
	 */
	@BasicAuth
	public static byte[] scale(byte[] fileData) {
		ByteArrayInputStream in = new ByteArrayInputStream(fileData);
		try {
			BufferedImage img = ImageIO.read(in);
			int scaledWidth = 35;
			int scaledHeight = 35;

			Image scaledImage = img.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
			BufferedImage imageBuff = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);
			imageBuff.getGraphics().drawImage(scaledImage, 0, 0, new Color(0, 0, 0), null);

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			ImageIO.write(imageBuff, "jpg", buffer);

			return buffer.toByteArray();

		} catch (IOException e) {
			throw new RuntimeException("IOException in scale");
		}
	}

	/**
	 * @author sureshnamburi
	 * @param id
	 * @return Result
	 */
	
	@BasicAuth
	public Result getimageProfile(Long id) {
		RawSql rawSql = RawSqlBuilder.parse("select id,image from app_user where id =" + id).create();
		Query<AppUser> query = Ebean.find(AppUser.class);
		query.setRawSql(rawSql);
		AppUser appUser = query.findUnique();
		ByteArrayInputStream input = null;
		try {
			if (appUser != null && appUser.getImage() != null) {
				input = new ByteArrayInputStream(appUser.getImage());
				return ok(input).as("image/jpg");
			} else {
				return Results.ok("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ok(input).as("image/jpg");
	}

	// get AppUser Image
	@BasicAuth
	public Result getimage(Long id) {
		AppUser appUser = AppUser.find.byId(id);
		ByteArrayInputStream input = null;
		try {
			if (appUser != null && appUser.getThumbnail() != null) {
				input = new ByteArrayInputStream(appUser.getThumbnail());
			} else {
				File file = new File("conf/images/thrymr.png");
				input = new ByteArrayInputStream(ChatController.scale(Files.toByteArray(file)));
				// return Results.internalServerError("image not available");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		long expiry = new Date().getTime() + 840000 * 1000;
		response().setHeader("Cache-Control", "max-age=" + 84000000);
		response().setHeader("ETag", "3e86-410-3596fbbc");
		return ok(input).as("image/png");
	}

	public static void updateAppUser(Bean bean, AppUser users) throws Exception {
		AppUser updateUser = AppUser.find.byId(bean.id);
		try {
			List<AppUser> appUsers = new ArrayList<AppUser>();
			appUsers = AppUser.find.where().eq("email", users.getEmail()).ne("id", users.id).findList();
			if (appUsers != null && !appUsers.isEmpty()) {
				throw new Exception("Email ID. already existed");
			}
			appUsers = AppUser.find.where().eq("mobileNo", users.getMobileNo()).ne("id", users.id).findList();
			if (appUsers != null && !appUsers.isEmpty()) {
				throw new Exception("Mobile No. already existed");
			}
			appUsers = AppUser.find.where().eq("FullName", users.FullName).ne("id", users.id).findList();
			if (appUsers != null && !appUsers.isEmpty()) {
				throw new Exception("User Name. already existed");
			}
			if (users.getEsslId() != null) {
				appUsers = AppUser.find.where().eq("esslId", users.getEsslId()).ne("id", users.id).findList();
				if (appUsers != null && !appUsers.isEmpty()) {
					throw new Exception("Essl Id already existed");
				}
			}

			List<Role> userRoles = new ArrayList<Role>();
			List<String> roles = bean.getRoles();
			if (roles != null && !roles.isEmpty()) {
				List<Role> appRoles = updateUser.getRole();
				appRoles.clear();
				updateUser.update();
				for (String urole : roles) {
					Role role1 = Role.find.where().eq("role", urole).findUnique();
					if (role1 == null) {
						Role role2 = new Role();
						role2.setRole(urole);
						role2.save();
						Role role3 = Role.find.where().eq("role", urole).findUnique();
						// Model.Finder(Long.class,Role.class).orderBy("id
						// desc").findIds().get(0);
						userRoles.add(role3);
					} else {
						userRoles.add(role1);
					}
				}

				updateUser.setRole(userRoles);
			}
			if (users.getReportMangerId() != 0) {
				updateUser.setReportMangerId(users.getReportMangerId());

			}

			updateUser.setFullName(users.FullName);
			updateUser.setGender(users.gender);
			updateUser.setOrganisation(users.organisation);
			updateUser.setJobTitle(users.jobTitle);
			
			if(users.email != null && !users.email.isEmpty()){
				updateUser.setEmail(users.email);
				String email = updateUser.getEmail();
				updateUser.setUserName(email.substring(0, email.indexOf('@')));
			}
			updateUser.setMobileNo(users.mobileNo);
			// updateUser.setPassword(Application.encode(users.password));

			updateUser.setGitId(users.getGitId());
			if(users.getEmployeeId() != null && !users.getEmployeeId().isEmpty()){
				updateUser.setEmployeeId(users.getEmployeeId());
			}
			if(users.getEsslId() != null){
				updateUser.setEsslId(users.getEsslId());
			}
			if (users.status != null) {
				updateUser.setStatus(users.status);
			}
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			if(bean.getjDate() != null && !bean.getjDate().isEmpty()){
				Date jDate = sdf.parse(bean.getjDate());
				updateUser.setJoinedDate(jDate);
			} 
			if (!bean.getDobirth().isEmpty()) {
				updateUser.setDob(sdf.parse(bean.getDobirth()));
			} 
			try {
				MultipartFormData body = request().body().asMultipartFormData();
				FilePart picture = body.getFile("image");
				if (picture != null) {
				//	String fileName = picture.getFilename();
				//	String contentType = picture.getContentType();
					File file = picture.getFile();
					updateUser.setImage(Files.toByteArray(file));
					updateUser.setThumbnail(ChatController.scale(Files.toByteArray(file)));
				}
			} catch (Exception e) {
				e.printStackTrace();
				// return redirect(routes.Application.error());
			}

			if (Long.parseLong(session("AppUserId")) == bean.id) {
				session("AppUserName", "" + users.getFullName());
				session("email", users.getEmail());
			}
			
			updateUser.experience = users.experience;
			updateUser.update();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// AppUser Update

	@BasicAuth
	public Result updateUser() {
		Bean bean = Form.form(Bean.class).bindFromRequest().get();
		AppUser users = Form.form(AppUser.class).bindFromRequest().get();
		AppUser updateUser = AppUser.find.byId(bean.id);

		try {
			updateAppUser(bean, users);
			flash().put("alert",
					new Alert("alert-success", updateUser.FullName + " Profile Successfully Updated").toString());
			return redirect(routes.Application.allUser());

		} catch (Exception e) {
			e.printStackTrace();
			flash().put("alert", new Alert("alert-danger", "" + e).toString());
			return redirect(routes.AdminController.editUser(bean.id));
		}
	}

	// individual User Profile Update(Module wise)
	@BasicAuth
	public Result updateProfile() {
		Bean bean = Form.form(Bean.class).bindFromRequest().get();
		AppUser users = Form.form(AppUser.class).bindFromRequest().get();
		AppUser updateUser = AppUser.find.byId(users.id);
		try {
			//updateAppUser(bean, users);
			
			updateUser.setFullName(users.FullName);
			updateUser.setGender(users.gender);
			updateUser.setOrganisation(users.organisation);
			updateUser.setJobTitle(users.jobTitle);
			updateUser.setMobileNo(users.mobileNo);
			updateUser.setGitId(users.getGitId());
			if (!bean.getDobirth().isEmpty()) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
				updateUser.setDob(sdf.parse(bean.getDobirth()));
			}
			try {
				MultipartFormData body = request().body().asMultipartFormData();
				FilePart picture = body.getFile("image");
				if (picture != null) {
					//String fileName = picture.getFilename();
					//String contentType = picture.getContentType();
					File file = picture.getFile();
					updateUser.setImage(Files.toByteArray(file));
					updateUser.setThumbnail(ChatController.scale(Files.toByteArray(file)));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			updateUser.update();
			flash().put("alert",
					new Alert("alert-success", updateUser.FullName + " Profile Successfully Updated").toString());
			if (session("AppUserRole").equals("Admin")) {
				return redirect(routes.AdminController.home());
			} else if (session("AppUserRole").equals("Engineer")) {
				return redirect(routes.EngineerController.home());
			} else if (session("AppUserRole").equals("Manager")) {
				return redirect(routes.ManagerController.home());
			} else if (session("AppUserRole").equals("HR")) {
				return redirect(routes.EngineerController.HRHome());
			} else {
				return redirect(routes.EngineerController.MarketingHome());
			}
			// return redirect(routes.Application.homePage());
		} catch (Exception e) {
			e.printStackTrace();
			flash().put("alert", new Alert("alert-danger", "" + e).toString());
			return redirect(routes.Application.userProfile());
		}
	}

	// Deleted AppUser
	@AdminAnnotation
	@BasicAuth
	public Result deleteAppUser(Long id) {
		// Logger.debug("delete method");
		try {
			AppUser deleteAppUser = AppUser.find.byId(id);
			/*
			 * if (!deleteAppUser.getProjects().isEmpty()) {
			 * deleteAppUser.getProjects().clear(); } if
			 * (!deleteAppUser.role.isEmpty()) { deleteAppUser.role.clear(); }
			 * deleteAppUser.update(); Logger.debug("deleted role");
			 * List<Notification> notifications = new ArrayList<Notification>();
			 * notifications = Notification.find.where().eq("messageTo",
			 * deleteAppUser).findList(); if(!notifications.isEmpty()){
			 * for(Notification notification : notifications){ if(notification
			 * != null){ notification.delete(); } } }
			 * Logger.debug("deleted notification"); List<ChatGroupAppUserInfo>
			 * ChatGroupAppUserInfos = new ArrayList<ChatGroupAppUserInfo>();
			 * ChatGroupAppUserInfos =
			 * ChatGroupAppUserInfo.find.where().eq("appUser",
			 * deleteAppUser).findList(); if(!ChatGroupAppUserInfos.isEmpty()){
			 * for(ChatGroupAppUserInfo chatGroupAppUserInfo :
			 * ChatGroupAppUserInfos){ if(chatGroupAppUserInfo != null){
			 * chatGroupAppUserInfo.delete(); } } }
			 * Logger.debug("deleted chatGroupAppUserInfo"); List<Message>
			 * messages = new ArrayList<Message>(); messages =
			 * Message.find.where().eq("messageTo", deleteAppUser).findList();
			 * if(!messages.isEmpty()){ for(Message message : messages){
			 * if(message != null){ UploadFileInfo uploadFileInfo =
			 * UploadFileInfo.find.where().eq("message", message).findUnique();
			 * if(uploadFileInfo != null){ uploadFileInfo.delete(); }
			 * GitNotification gitNotification =
			 * GitNotification.find.where().eq("message", message).findUnique();
			 * if(gitNotification != null){ gitNotification.delete(); }
			 * MessageAttachment messageAttachment =
			 * MessageAttachment.find.where().eq("message",
			 * message).findUnique(); if(messageAttachment != null){
			 * messageAttachment.delete(); } message.delete(); } } }
			 * Logger.debug("deleted message");
			 * 
			 * List<Message> messagesBy = new ArrayList<Message>(); messagesBy =
			 * Message.find.where().eq("messageBy", deleteAppUser).findList();
			 * if(!messagesBy.isEmpty()){ for(Message message : messagesBy){
			 * if(message != null){ UploadFileInfo uploadFileInfo =
			 * UploadFileInfo.find.where().eq("message", message).findUnique();
			 * if(uploadFileInfo != null){ uploadFileInfo.delete(); }
			 * GitNotification gitNotification =
			 * GitNotification.find.where().eq("message", message).findUnique();
			 * if(gitNotification != null){ gitNotification.delete(); }
			 * 
			 * MessageAttachment messageAttachment =
			 * MessageAttachment.find.where().eq("message",
			 * message).findUnique(); if(messageAttachment != null){
			 * messageAttachment.delete(); } message.delete(); } } }
			 * Logger.debug("deleted messageBy");
			 * 
			 * List<Leaves> leaves = new ArrayList<Leaves>(); leaves =
			 * Leaves.find.where().eq("appUser", deleteAppUser).findList();
			 * if(!leaves.isEmpty()){ for(Leaves leave : leaves){
			 * leave.delete(); } } Logger.debug("deleted leave");
			 * List<NotificationAlert> notificationAlerts =new
			 * ArrayList<NotificationAlert>(); notificationAlerts =
			 * NotificationAlert.find.where().eq("notifiedTo",
			 * deleteAppUser).findList(); if(!notificationAlerts.isEmpty()){
			 * for(NotificationAlert notificationAlert : notificationAlerts){
			 * notificationAlert.delete(); } }
			 * 
			 * deleteAppUser.getTaskList().clear(); deleteAppUser.update();
			 * Logger.debug("deleted getTaskList"); List<AppliedLeaves>
			 * appliedLeavesList = new ArrayList<AppliedLeaves>();
			 * appliedLeavesList = AppliedLeaves.find.where().eq("appUser",
			 * deleteAppUser).findList();
			 * Logger.debug("lllllll"+appliedLeavesList); for(AppliedLeaves
			 * appliedLeaves : appliedLeavesList){ appliedLeaves.delete();
			 * Logger.debug("deleted appliedLeaves"); }
			 * Logger.debug("deleted appliedLeaves");
			 * 
			 * 
			 * 
			 * deleteAppUser.getEntitlement().clear(); deleteAppUser.update();
			 * Logger.debug("deleted getEntitlement");
			 * 
			 * ChatAppUserLastSeenTabInfo chatAppUser=
			 * ChatAppUserLastSeenTabInfo.find.where().eq("loggedInUser",
			 * deleteAppUser).findUnique(); if(chatAppUser != null){
			 * chatAppUser.delete(); } Logger.debug("deleted chatAppUser");
			 * 
			 * deleteAppUser.delete();
			 * 
			 * Logger.debug("deleted deleteAppUser");
			 */

			if (deleteAppUser != null) {
				deleteAppUser.setStatus(UserProjectStatus.Inactive);
				deleteAppUser.setLoginCheck(false);
				deleteAppUser.update();
			}
			flash().put("alert",
					new Alert("alert-success", deleteAppUser.FullName + " Profile Successfully Deleted").toString());
			return redirect(routes.Application.allUser());
		} catch (Exception e) {
			e.printStackTrace();
			return ok(views.html.error.render("Bad Request", 400));
		}
	}

	public static void storeCompressedImage() {
		List<AppUser> appUsers = new ArrayList<AppUser>();
		try {
			appUsers = AppUser.find.all();
			for (AppUser appUser : appUsers) {
				synchronized (appUser) {
					if (appUser.getImage() != null) {
						if (appUser.getThumbnail() == null) {
							appUser.setThumbnail(ChatController.scale(appUser.getImage()));
							appUser.update();
						}
					} else {
						File file = new File("conf/images/thrymr.png");
						appUser.setImage(Files.toByteArray(file));
						appUser.setThumbnail(ChatController.scale(Files.toByteArray(file)));
						appUser.update();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*
	 * ************************ Projects Action Methods
	 * ******************************
	 */

	// Add Projects by Admin
	@AdminAnnotation
	@BasicAuth
	public Result addProject() {

		ProjectBean projectBean = Form.form(ProjectBean.class).bindFromRequest().get();
		ContactBean clientContact = Form.form(ContactBean.class).bindFromRequest().get();

		Projects projectss = Projects.find.where().eq("projectName", projectBean.projectName).findUnique();

		try {
			if (projectss == null) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
				Projects projects1 = new Projects();
				if (!projectBean.getStartedDate().isEmpty()) {
					Date sdate = sdf.parse(projectBean.getStartedDate());
					projects1.setStartedDate(sdate);
				}
				if (!projectBean.getEndedDate().isEmpty()) {
					Date edate = sdf.parse(projectBean.getEndedDate());
					projects1.setEndedDate(edate);
				}
				projects1.setProjectName(projectBean.getProjectName());
				projects1.setClient(projectBean.getClient());
				// projects1.setProjectLeader(projectBean.getProjectLeader());
				if (projectBean.getProjectManagerId() != 0) {
					projects1.setProjectManager(AppUser.find.byId(projectBean.getProjectManagerId()));
				}
				projects1.setDescription(projectBean.getDescription());
				projects1.setStatus(UserProjectStatus.Active);

				List name = clientContact.getName();
				List MobileNo = clientContact.getMoibileNo();
				List email = clientContact.getEmail();
				List<Contact> lContect = new ArrayList<Contact>();
				for (int i = 0; i < name.size(); i++) {
					Contact contect = new Contact();
					contect.setName((String) name.get(i));
					contect.setMoibileNo((String) MobileNo.get(i));
					contect.setEmail((String) email.get(i));
					contect.save();
					long id = (Long) new Model.Finder(Long.class, Contact.class).orderBy("id desc").findIds().get(0);
					Contact contect1 = Contact.find.byId(id);
					lContect.add(contect1);
				}
				projects1.setClientContect(lContect);
				projects1.save();
				ProjectBean projectBean1 = new ProjectBean();
				List<Projects> projects2 = Projects.find.all();
				flash().put("alert", new Alert("alert-success", "Added New Project Successfully.").toString());
				return redirect(routes.Application.addProject());
			} else {
				flash().put("alert", new Alert("alert-danger", "Project Already Existed .").toString());
				return ok(views.html.admin.addproject.render(projectBean));
			}
		} catch (ParseException e) {
			e.printStackTrace();
			flash().put("alert", new Alert("alert-success", "Date Formate Wrong.").toString());
			return ok(views.html.admin.addproject.render(projectBean));
		}
	}

	// Edit Project
	@AdminAnnotation
	@BasicAuth
	public Result editProjects(Long id) {
		try {
			Projects project = Projects.find.byId(id);
			return ok(views.html.admin.editprojects.render(project));
		} catch (Exception e) {
			e.printStackTrace();
			return ok(views.html.error.render("Bad Request", 400));
		}
	}

	// delete Project secondary Client contact details
	@AdminAnnotation
	@BasicAuth
	public Result deleteContact(Long pid, Long cid) {
		try {
			Projects project = Projects.find.byId(pid);
			Contact contact = Contact.find.byId(cid);
			List<Contact> lContact = project.getClientContect();
			lContact.remove(contact);
			project.update();
			return ok(views.html.admin.editprojects.render(project));
		} catch (Exception e) {
			e.printStackTrace();
			return ok(views.html.error.render("Bad Request", 400));
		}
	}

	// Project Profile Update
	@AdminAnnotation
	@BasicAuth
	public Result updateProject() {

		ProjectBean projectBean = Form.form(ProjectBean.class).bindFromRequest().get();
		ContactBean clientContact = Form.form(ContactBean.class).bindFromRequest().get();

		Projects project = Projects.find.byId(projectBean.pid);

		try {
			Projects sproject = Projects.find.where().eq("projectName", projectBean.projectName).findUnique();
			if (sproject != null && !project.projectName.equals(projectBean.projectName)) {
				throw new Exception("Project Nme already existed");
			}
			List cid = clientContact.id;
			List name = clientContact.getName();
			List MobileNo = clientContact.getMoibileNo();
			List email = clientContact.getEmail();
			for (int i = 0; i < cid.size(); i++) {
				long ccid = Long.parseLong((String) cid.get(i));
				Contact pContact = Contact.find.byId(ccid);
				pContact.setName((String) name.get(i));
				pContact.setMoibileNo((String) MobileNo.get(i));
				pContact.setEmail((String) email.get(i));
				pContact.update();
			}
			List<Contact> llContect = project.getClientContect();
			for (int i = cid.size(); i < name.size(); i++) {
				Contact pContact = new Contact();
				pContact.setName((String) name.get(i));
				pContact.setMoibileNo((String) MobileNo.get(i));
				pContact.setEmail((String) email.get(i));
				pContact.save();
				llContect.add(pContact);
			}
			project.setStatus(projectBean.status);
			project.setProjectName(projectBean.projectName);
			project.setClient(projectBean.client);
			if (projectBean.getProjectManagerId() != 0) {
				project.setProjectManager(AppUser.find.byId(projectBean.getProjectManagerId()));
			}
			project.setDescription(projectBean.description);
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			if (!projectBean.getStartedDate().isEmpty()) {
				Date sdate = sdf.parse(projectBean.getStartedDate());
				project.setStartedDate(sdate);
			}
			if (!projectBean.getEndedDate().isEmpty()) {
				Date edate = sdf.parse(projectBean.getEndedDate());
				project.setEndedDate(edate);
			}
			project.update();
			List<Projects> projects1 = Projects.find.all();
			flash().put("alert",
					new Alert("alert-success", project.projectName + " Project Successfully Updated").toString());
			return redirect(routes.Application.allProject());
		} catch (Exception e) {
			e.printStackTrace();
			flash().put("alert",
					new Alert("alert-danger", projectBean.projectName + " Project Name Already Existed ").toString());
			return redirect(routes.AdminController.editProjects(projectBean.pid));
		}
	}

	/*
	 * ******************************** Daily Reports Methods
	 * **********************************
	 */
	public static Boolean checkIsMoreRoles() {
		Boolean flag = false;
		AppUser appUser = Application.getLoggedInUser();
		if (appUser.getRole().size() > 1) {
			flag = true;
		}
		return flag;
	}

	@BasicAuth
	@AdminAnnotation
	public Result fillStatus() {
		List<Projects> projects = new ArrayList<Projects>();
		Set<Projects> setProjects = new HashSet<Projects>();
		try {
			AppUser appUser = AppUser.find.byId(Long.parseLong(session("AppUserId")));
			Role managerRole = Role.find.where().eq("role", Roles.Manager.toString()).findUnique();
			projects = appUser.getProjects();
			if (!projects.isEmpty()) {
				setProjects.addAll(projects);
			}
			List<Role> roles = appUser.getRole();
			if (roles.contains(managerRole)) {
				List<Projects> allProjects = ManagerController.managerProjects(appUser);
				if (!allProjects.isEmpty()) {
					setProjects.addAll(allProjects);
				}
			}
			if (!setProjects.isEmpty()) {
				projects.clear();
				projects.addAll(setProjects);
			}
			boolean f = EngineerController.getIsDone();
			if (f) {
				return ok(views.html.admin.fillStatus.render("false", projects));
			} else {
				return ok(views.html.admin.fillStatus.render("true", projects));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ok(views.html.error.render("Page Not Found", 404));
		}

	}

	// get dailyReports in Admin
	@AdminAnnotation
	@BasicAuth
	public Result getdailyReport(Long id) {
		try {
			AppUser appUser = AppUser.find.byId(id);
			return ok(views.html.admin.getdailyReport.render(appUser));
		} catch (Exception e) {
			e.printStackTrace();
			return ok(views.html.error.render("Bad Request", 400));
		}
	}

	@AdminAnnotation
	@BasicAuth
	public Result deleteDailyReport(Long id) {
		UsersDailyReport usersDailyReport = UsersDailyReport.find.byId(id);
		usersDailyReport.getToday().clear();
		usersDailyReport.getTomorrow().clear();
		usersDailyReport.getProblem().clear();
		usersDailyReport.update();
		usersDailyReport.delete();
		flash().put("alert", new Alert("alert-success", " Today Daily Report Deleted Successfully").toString());
		return redirect(routes.Application.dailyStatusDate());
	}

	// get Weekly wise DR
	@BasicAuth
	public Result getWeekDR(String sd, String ed, Long id) {
		List<UsersDailyReport> wk = new ArrayList<UsersDailyReport>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		try {
			DailyReport dailyReport = DailyReport.find.where().eq("app_user_id", id).findUnique();

			if (dailyReport != null) {
				try {
					Date sdate = sdf.parse(sd);
					Date edate = sdf.parse(ed);
					wk = UsersDailyReport.find.where().eq("daily_report_id", dailyReport.getId())
							.between("date", sdate, edate).findList();
					return ok(views.html.admin.getdailyReportWeek.render(wk));
				} catch (ParseException e) {
					e.printStackTrace();
					return ok(views.html.admin.getdailyReportWeek.render(wk));
				}
			} else {
				return ok(views.html.admin.getdailyReportWeek.render(wk));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ok(views.html.error.render("Bad Request", 400));
		}
	}

	public Result dailyStatusDateWise(String date) {
		List<DailyStatusDateWise> dailyStatusDatewiseList = new ArrayList<DailyStatusDateWise>();
		try {
			if (date != null) {
				session("Date", date);
				dailyStatusDatewiseList = getdailyStatusDateWise(date);
				// createExcelSheet(dailyStatusDatewiseList);
				return ok(views.html.admin.getDailyReportsDateWise.render(dailyStatusDatewiseList));
			} else {
				return ok(views.html.admin.getDailyReportsDateWise.render(dailyStatusDatewiseList));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ok(views.html.admin.getDailyReportsDateWise.render(dailyStatusDatewiseList));
		}
	}

	public static List<DailyStatusDateWise> getdailyStatusDateWise(String date) {
		List<DailyStatusDateWise> dailyStatusDatewiseList = new ArrayList<DailyStatusDateWise>();
		try {
			if (date != null) {
				List<AppUser> appUsersList = AppUser.find.where().eq("status", UserProjectStatus.Active).findList();
				for (AppUser appuser : appUsersList) {
					DailyReport dailyReport = DailyReport.find.where().eq("app_user_id", appuser.getId()).findUnique();
					if (dailyReport != null) {
						SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
						Date sdate = sdf.parse(date);
						UsersDailyReport usersDailyReport = UsersDailyReport.find.where()
								.eq("daily_report_id", dailyReport.getId()).eq("date", sdate).findUnique();
						if (usersDailyReport != null) {
							DailyStatusDateWise dailyStatusDateWise = new DailyStatusDateWise();
							dailyStatusDateWise.setUsersDailyReport(usersDailyReport);
							dailyStatusDateWise.setAppUser(appuser);
							dailyStatusDatewiseList.add(dailyStatusDateWise);
						}
					}
				}
				return dailyStatusDatewiseList;
			} else {
				return dailyStatusDatewiseList;
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return dailyStatusDatewiseList;
		}

	}

	public static void createExcelSheet(List<DailyStatusDateWise> dailyStatusDatewiseList) {

		try {
			String excelFileName = "conf/excel/DailyReports.xls";
			String sheetName = session("Date") + " DailyReports";
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet(sheetName);
			sheet.setDefaultColumnWidth(25);

			HSSFCellStyle cellStyle = wb.createCellStyle();
			cellStyle.setWrapText(true);
			HSSFRow row1 = sheet.createRow(0);

			HSSFCell cell4 = row1.createCell(0);
			cell4.setCellValue("S.No");
			HSSFCell cell5 = row1.createCell(1);
			cell5.setCellValue("Engineer Name");
			HSSFCell cell6 = row1.createCell(2);
			cell6.setCellValue("Date");
			HSSFCell cell7 = row1.createCell(3);
			cell7.setCellValue("Yesterday's Work");
			HSSFCell cell8 = row1.createCell(4);
			cell8.setCellValue("Plan For Today's");
			HSSFCell cell9 = row1.createCell(5);
			cell9.setCellValue("Problem Faced");
			HSSFCell cell10 = row1.createCell(6);
			cell10.setCellValue("Rating");
			int i = 1, count = 1, j = 0;

			// StringBuilder builder = new StringBuilder();
			// StringBuilder builder1 = new StringBuilder();
			// StringBuilder builder2 = new StringBuilder();
			for (DailyStatusDateWise dailyStatusDateWise : dailyStatusDatewiseList) {
				String today1 = "", tm = "", pblm = "";
				sheet.setDefaultRowHeightInPoints(50);
				HSSFRow row = sheet.createRow(i);

				HSSFCell cell = row.createCell(0);
				cell.setCellValue(count);

				HSSFCell cell23 = row.createCell(1);
				// cell23.setCellStyle(cellStyle);
				cell23.setCellValue(dailyStatusDateWise.getAppUser().getFullName());

				HSSFCell cell22 = row.createCell(2);
				// cell22.setCellStyle(cellStyle);
				cell22.setCellValue(dailyStatusDateWise.getUsersDailyReport().getDate().toLocaleString());
				j = i;
				int num = 1;
				HSSFCell cell1 = row.createCell(3);

				// cell1.setCellStyle(cellStyle);
				for (Todays today : dailyStatusDateWise.usersDailyReport.getToday()) {
					today1 = today1.concat(num + "." + today.getToday()) + ". \n\r";
					num++;
					// builder.append(today.getToday()+"\n");
				}
				// cell1.setCellValue(builder.toString());
				cell1.setCellValue(today1);
				num = 1;
				HSSFCell cell2 = row.createCell(4);
				// cell2.setCellStyle(cellStyle);
				for (Tomorrows tomorrow : dailyStatusDateWise.usersDailyReport.getTomorrow()) {
					tm = tm.concat(num + "." + tomorrow.getTomorrow()) + ". \n\r";
					num++;
					// builder1.append(tomorrow.getTomorrow()+"\n");
				}
				// cell2.setCellValue(builder1.toString());
				cell2.setCellValue(tm);
				num = 1;
				HSSFCell cell3 = row.createCell(5);
				// cell3.setCellStyle(cellStyle);
				for (Problems problem : dailyStatusDateWise.usersDailyReport.getProblem()) {
					pblm = pblm.concat(num + "." + problem.getProblem()) + ". \n\r";
					num++;
					// builder2.append(problem.getProblem()+"\n");
				}
				// cell3.setCellValue(builder2.toString());
				cell3.setCellValue(pblm);

				HSSFCell cell41 = row.createCell(6);
				cell41.setCellValue(dailyStatusDateWise.getUsersDailyReport().getRate());
				i++;
				count++;
			}

			FileOutputStream fileOut = new FileOutputStream(excelFileName);
			// write this workbook to an Outputstream.
			wb.write(fileOut);
			fileOut.flush();
			fileOut.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public Result downloadExcelSheet() {
		try {
			String date = session("Date");
			createExcelSheet(getdailyStatusDateWise(date));
		} catch (Exception e) {
			e.printStackTrace();
		}
		File file = new File("conf/excel/DailyReports.xls");
		String filename = session("Date") + " DailyReports.xls";
		response().setContentType("application/xls");
		response().setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
		return ok(file).as("application/xls");
	}

	/*
	 * ****************************** TimeSheet Methods
	 * ********************************
	 */

	// get Weekly wise TS(AppUser wise)
	@BasicAuth
	public Result getWeekTimeSheet(String sd, String ed, Long id) {
		List<Timesheet> wk = new ArrayList<Timesheet>();
		Map<Date, List<Timesheet>> finalMap = new HashMap<Date, List<Timesheet>>();
		wk = weekTimesheet(sd, ed, id);

		for (Timesheet timeSheet : wk) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(timeSheet.date);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);

			if (!finalMap.containsKey(cal.getTime())) {
				List<Timesheet> timeSheetList = new ArrayList<Timesheet>();
				timeSheetList.add(timeSheet);
				finalMap.put(cal.getTime(), timeSheetList);
			} else {
				finalMap.get(cal.getTime()).add(timeSheet);
			}
		}
		return ok(views.html.admin.getTimeSheet.render(finalMap));
	}

	public static List<Timesheet> weekTimesheet(String sd, String ed, Long id) {
		List<Timesheet> timesheetUserWise = new ArrayList<Timesheet>();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			Date sdate = sdf.parse(sd);
			Date edate = sdf.parse(ed);
			timesheetUserWise = Timesheet.find.where().eq("app_user_id", id).between("date", sdate, edate)
					.orderBy("date DESC").findList();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return timesheetUserWise;
	}

	// get Weekly wise TS(project Wise)
	@BasicAuth
	public Result getWeekPTimeSheet(String sd, String ed, Long id) {
		List<Timesheet> wk = new ArrayList<Timesheet>();
		Map<Date, List<Timesheet>> finalMap = new HashMap<Date, List<Timesheet>>();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			Date sdate = sdf.parse(sd);
			Date edate = sdf.parse(ed);
			wk = Timesheet.find.where().eq("project_id", id).between("date", sdate, edate).findList();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		for (Timesheet timeSheet : wk) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(timeSheet.date);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);

			if (!finalMap.containsKey(cal.getTime())) {
				List<Timesheet> timeSheetList = new ArrayList<Timesheet>();
				timeSheetList.add(timeSheet);
				finalMap.put(cal.getTime(), timeSheetList);
			} else {
				finalMap.get(cal.getTime()).add(timeSheet);
			}
		}
		return ok(views.html.admin.getPTimeSheet.render(finalMap));
	}

	public static List<Timesheet> WeekPTimesheet(String sd, String ed, Long id) {
		List<Timesheet> timesheetUserWise = new ArrayList<Timesheet>();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			Date sdate = sdf.parse(sd);
			Date edate = sdf.parse(ed);
			timesheetUserWise = Timesheet.find.where().eq("project_id", id)
					.eq("app_user_id", Long.parseLong(session("AppUserId"))).between("date", sdate, edate)
					.orderBy("date DESC").findList();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return timesheetUserWise;

	}

	// fetch all engineers and engineer available for particular project
	public Result manageTeamMemberByAdmin(String val) {
		try {
			List<AppUser> appUsers1 = new ArrayList<AppUser>();
			appUsers1 = AppUser.find.where().eq("status", UserProjectStatus.Active).findList();
			Projects project1 = Projects.find.byId(Long.parseLong(val));
			List<AppUser> appUser = project1.getAppUsers();
			appUsers1.removeAll(appUser);
			appUser.remove(project1.projectManager);
			appUsers1.remove(project1.projectManager);
			return ok(views.html.manager.manageTeamMembersData.render(appUsers1, appUser, project1.projectManager));
		} catch (Exception e) {
			return ok(views.html.error.render(e.getMessage(), 204));
		}
	}

	public Result manageTeamMemberActionByAdmin() {
		try {
			AppUsersManageTeam manageTeam = Form.form(AppUsersManageTeam.class).bindFromRequest().get();
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
				/*
				 * appUsersObj = AppUser.find.byId(Long
				 * .parseLong(session("AppUserId")));
				 * appUsersList.add(appUsersObj);
				 */
				project.setAppUsers(appUsersList);
				project.save();
			} else {
				project.setAppUsers(appUsersList);
				project.save();
			}

			AppUser user = AppUser.find.byId(Long.parseLong(session("AppUserId")));
			List<Projects> myProjects = Projects.find.all();
			// flash().put("alert",new Alert("alert-success",
			// "Team Members update successfully!").toString());
			return ok(views.html.admin.manageTeam.render("Team Members update successfully!", myProjects));
		} catch (Exception e) {
			return ok(views.html.error.render("Managing Team members not possible at this time", 500));
		}

	}

	public Result timeSheetsDateWise(String date) {
		Map<AppUser, List<Timesheet>> finalMap = new HashMap<AppUser, List<Timesheet>>();
		Map<AppUser, TimesheetUserRemark> finalMaprmk = new HashMap<AppUser, TimesheetUserRemark>();
		List<Timesheet> timeSheets = new ArrayList<Timesheet>();
		List<TimesheetUserRemark> timesheetUserRemarks = new ArrayList<TimesheetUserRemark>();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			Date todayDate = sdf.parse(date);
			timeSheets = Timesheet.find.where().eq("date", todayDate).findList();
			timesheetUserRemarks = TimesheetUserRemark.find.where().eq("date", todayDate).findList();
			for (Timesheet timeSheet : timeSheets) {
				if (!finalMap.containsKey(timeSheet.appUser)) {
					List<Timesheet> timeSheetList = new ArrayList<Timesheet>();
					timeSheetList.add(timeSheet);
					finalMap.put(timeSheet.appUser, timeSheetList);
				} else {
					finalMap.get(timeSheet.appUser).add(timeSheet);
				}
			}

			for (TimesheetUserRemark timeSheetrm : timesheetUserRemarks) {
				if (!finalMaprmk.containsKey(timeSheetrm.appUser)) {
					finalMaprmk.put(timeSheetrm.appUser, timeSheetrm);
				}
			}

			return ok(views.html.admin.getTimeSheetsDateWise.render(finalMap, finalMaprmk));
		} catch (ParseException e) {
			e.printStackTrace();
			return ok(views.html.admin.getTimeSheetsDateWise.render(finalMap, finalMaprmk));
		}
	}
	/*
	 * ************************ AttendenceRender Action Methods *
	 * ******************************
	 */

	@AdminHRAnnotation
	public Result attendenceRender() {
		List<AppUser> appUserList = new ArrayList<AppUser>();
		List<Attendance> attendanceList = new ArrayList<Attendance>();
		List<AppUser> appUserListThoseFill = new ArrayList<AppUser>();
		appUserList = AppUser.find.where().eq("status", UserProjectStatus.Active).findList();
		Date date = null;
		try {
			if (session("attendanceDate").equals("0")) {
				date = EngineerController.getTodayDate(new Date());
			} else {
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
				date = sdf.parse(session("attendanceDate").trim());
				session("attendanceDate", "0");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		attendanceList = Attendance.find.where().eq("date", date).findList();

		if (attendanceList != null) {
			for (Attendance attendance : attendanceList) {
				if (attendance.getInTime() != null || attendance.getStatus() != null) {
					appUserListThoseFill.add(attendance.appUser);
				}
			}
			appUserList.removeAll(appUserListThoseFill);
		}
		return ok(views.html.admin.addAttendence.render(appUserList, attendanceList, date));
	}

	public Result attendence(String date) {
		List<AppUser> appUserList = new ArrayList<AppUser>();
		appUserList = AppUser.find.where().eq("status", UserProjectStatus.Active).findList();
		Date date1 = null;

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			date1 = sdf.parse(date);
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<Attendance> attendanceList = new ArrayList<Attendance>();
		attendanceList = Attendance.find.where().eq("date", date1).findList();

		List<AppUser> appUserListThoseFill = new ArrayList<AppUser>();
		if (attendanceList != null) {
			for (Attendance attendance : attendanceList) {
				if (attendance.getInTime() != null || attendance.getStatus() != null) {
					appUserListThoseFill.add(attendance.appUser);
				}
			}
			appUserList.removeAll(appUserListThoseFill);
		}

		return ok(views.html.admin.attendencefillingform.render(appUserList, attendanceList));
	}

	public Result attendenceSubmit() {
		AttendenceBean attendence = Form.form(AttendenceBean.class).bindFromRequest().get();
		List<AppUser> appUserList1 = new ArrayList<AppUser>();
		for (Long id : attendence.ids) {
			AppUser appUser = AppUser.find.byId(id);
			appUserList1.add(appUser);
		}
		int j = 0;
		// Attendance attendance = null;
		for (int i = 0; i < attendence.ids.size(); i++) {
			AppUser appUser = AppUser.find.byId(attendence.ids.get(i));
			try {

				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
				Date d = sdf.parse(attendence.date1);
				SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				String d5 = sdf3.format(d);
				Date d6 = sdf3.parse(d5);
				Query<Attendance> query1 = Ebean.createQuery(Attendance.class);
				query1.where(Expr.and(Expr.eq("date", d6), Expr.eq("appUser", appUser)));
				Attendance attendance = query1.findUnique();
				boolean flag = false;
				if (attendance != null)
					;
				else {
					flag = true;
					attendance = new Attendance();
					attendance.setDate(d6);
				}

				List<String> statusList = attendence.status;
				String status = statusList.get(i);
				if (status.equalsIgnoreCase("present")) {
					attendance.setStatus(AttendenceStatus.Present);
					Date inDate = null;
					String inTimeVal = "";
					List<String> inTimeList = attendence.inTime;
					if (!inTimeList.isEmpty() && inTimeList.size() > j) {
						inTimeVal = inTimeList.get(j);
						if (!inTimeVal.equals("")) {
							SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
							inDate = sdf1.parse(attendence.date1 + " " + inTimeVal);

						}
					}
					Date outDate = null;
					String outTimeVal = "";
					List<String> outTimeList = attendence.outTime;
					if (!outTimeList.isEmpty() && outTimeList.size() > j) {
						outTimeVal = outTimeList.get(j);
						j++;
						if (!outTimeVal.equals("")) {
							SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
							outDate = sdf2.parse(attendence.date1 + " " + outTimeVal);

						}
					}
					Date d1 = null;
					String d2 = "";
					long diff = outDate.getTime() - inDate.getTime();

					if (diff < 0) {
						final Calendar calendar = Calendar.getInstance();
						calendar.setTime(d);
						calendar.add(Calendar.DAY_OF_YEAR, 1);
						d1 = calendar.getTime();
						d2 = sdf.format(d1);
						outDate = sdf.parse(d2 + " " + attendance.outTime);
					}
					DateTime dt1 = new DateTime(inDate);
					DateTime dt2 = new DateTime(outDate);

					/*
					 * long diffSeconds = diff / 1000 % 60; long diffMinutes =
					 * diff / (60 * 1000) % 60; long diffHours = diff / (60 * 60
					 * * 1000);
					 */
					if (!inTimeVal.equals("") && !outTimeVal.equals("")) {
						long diffHours = Hours.hoursBetween(dt1, dt2).getHours() % 24;
						long diffMinutes = Minutes.minutesBetween(dt1, dt2).getMinutes() % 60;
						attendance.setSpendTime(minuteAdjustZeroToNine(diffHours, diffMinutes));
					}

					attendance.setInTime(inDate);
					attendance.setOutTime(outDate);

				} else if (status.equalsIgnoreCase("absent")) {
					attendance.setStatus(AttendenceStatus.Absent);
				} else if (status.equalsIgnoreCase("WFH")){
					attendance.setStatus(AttendenceStatus.WFH);
				}else{
					attendance.setStatus(AttendenceStatus.CL);
				}
				if (flag == false) {
					attendance.appUser = appUser;
					attendance.update();
				} else {
					attendance.appUser = appUser;
					attendance.save();
					flag = false;
				}
				appUser.attendences.add(attendance);
				appUser.update();

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return redirect(routes.AdminController.attendenceSubmitmsg(attendence.date1));
		// return
		// ok(views.html.admin.attendence.render(appUserList,attendanceList1,
		// date1));
	}

	public Result attendenceSubmitmsg(String date1) {
		session("attendanceDate", date1.toString());
		/*
		 * Date date = null; try { SimpleDateFormat sdf = new
		 * SimpleDateFormat("dd-MM-yyyy"); date = sdf.parse(date1); } catch
		 * (Exception e) { e.printStackTrace(); }
		 * 
		 * List<AppUser> appUserList = new ArrayList<AppUser>(); appUserList =
		 * AppUser.find.where() .eq("status",
		 * UserProjectStatus.Active).findList();
		 * 
		 * List<Attendance> attendanceList1 = new ArrayList<Attendance>();
		 * attendanceList1 = Attendance.find.where().eq("date",
		 * date).findList(); List<AppUser> appUserListThoseFill = new
		 * ArrayList<AppUser>(); if (attendanceList1 != null) { for (Attendance
		 * attendance : attendanceList1) { if (attendance.getInTime() != null ||
		 * attendance.getStatus() != null) {
		 * appUserListThoseFill.add(attendance.appUser); } }
		 * appUserList.removeAll(appUserListThoseFill); }
		 */
		return redirect(routes.AdminController.attendenceRender());
		/*
		 * return ok(views.html.admin.addAttendence.render(appUserList,
		 * attendanceList1, date));
		 */
	}

	@AdminHRAnnotation
	public Result userwiseAttendanceRender() {

		List<AppUser> appUsersActive = AppUser.find.where().eq("status", UserProjectStatus.Active).findList();

		return ok(views.html.admin.UserWiseAttendance.render(appUsersActive));
	}

	public Result userwiseAttendanceWeekRender(Long id) {
		return ok(views.html.admin.UserwiseAttendanceweek.render(id));
	}

	// User weekly Attendence &Report Showing
	public Result weekllyAttendanceUserwise(String startDate, String endDate, Long id) {
		Date sdate = null;
		Date edate = null;
		List<Attendance> attendancesList = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		try {
			sdate = sdf.parse(startDate);
			edate = sdf.parse(endDate);
			AppUser user = AppUser.find.byId(id);
			List<Attendance> attendances = user.attendences;

			for (Attendance attendance : attendances) {
				if (attendance.getDate().equals(sdate)
						|| attendance.getDate().after(sdate) && attendance.getDate().before(edate)
						|| attendance.getDate().equals(edate)) {

					// Logger.info("" + attendance);
					attendancesList.add(attendance);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		String sd = sdf1.format(sdate);
		String ed = sdf1.format(edate);

		return ok(views.html.admin.userAttendanceWeekwise.render(attendancesList, sd, ed, id));

	}

	public static String inOutDateCalculation(int listSize, int inh, int inm, int absentCount, int wfh) {

		String inDate1 = "";
		if (listSize != 0) {
			int remainder = inh % listSize;
			inm = (remainder * 60) + inm;
			inh = inh / listSize;
			inm = inm / listSize;
		}
		if (inh > 12) {
			inh = inh - 12;
			inDate1 = inh + ":" + inm + " PM";
		} else {
			inDate1 = inh + ":" + inm + " AM";
		}

		return inDate1;
	}

	public static Date formatingDate(String dateString, String inDate1) {
		Date inDate = null;

		try {
			SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
			inDate = sdf1.parse(dateString + " " + inDate1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inDate;
	}

	public Result weekllyAttendance(String sDate, String eDate) {

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Date sdate = null;
		Date edate = null;
		try {
			sdate = sdf.parse(sDate);
			edate = sdf.parse(eDate);
		} catch (Exception e) {
			e.printStackTrace();
		}

		int count = AppUser.find.where().eq("status", UserProjectStatus.Active).findRowCount();
		List<Attendance> attendancesList = null;
		List<AttendanceAverageCalculation> attendanceList1 = new ArrayList<AttendanceAverageCalculation>();

		for (int i = 0; i < 7; i++) {
			final Calendar calendar = Calendar.getInstance();
			calendar.setTime(sdate);
			if (i != 0) {
				calendar.add(Calendar.DAY_OF_YEAR, i);
			}
			Date d1 = calendar.getTime();
			attendancesList = Attendance.find.where().eq("date", d1).findList();
			int notFilled = count - attendancesList.size();
			int esslRows = (count - notFilled) - Ebean.createQuery(Attendance.class)
					.where(Expr.and(Expr.eq("date", d1), Expr.eq("esslIntime", null))).findRowCount();
			int registerRows = Ebean.createQuery(Attendance.class)
					.where(Expr.and(Expr.eq("date", d1), Expr.eq("status", AttendenceStatus.Present))).findRowCount();
			int absentRows = Ebean.createQuery(Attendance.class)
					.where(Expr.and(Expr.eq("date", d1), Expr.eq("status", AttendenceStatus.Absent))).findRowCount();
			int wfhRows = Ebean.createQuery(Attendance.class)
					.where(Expr.and(Expr.eq("date", d1), Expr.eq("status", AttendenceStatus.WFH))).findRowCount();
			AttendanceAverageCalculation attendanceObj = new AttendanceAverageCalculation();
			int inh = 0, inm = 0, outh = 0, outm = 0, esslInH = 0, esslInM = 0, esslOutH = 0, esslOutM = 0;
			if (attendancesList != null && !attendancesList.isEmpty()) {
				for (Attendance attendance : attendancesList) {
					String s = attendance.getSpendTime();
					if (attendance.getStatus() != null && attendance.getStatus().equals(AttendenceStatus.Absent)) {

					} else if (attendance.getStatus() != null && attendance.getStatus().equals(AttendenceStatus.WFH)) {

					} else {
						if (attendance.getInTime() != null && attendance.getOutTime() != null) {
							Date intime = attendance.getInTime();
							DateTime d = new DateTime(intime);
							inh += d.getHourOfDay();

							inm += (d.getMinuteOfDay()) % 60;

							Date outtime = attendance.getOutTime();
							DateTime d2 = new DateTime(outtime);
							outh += d2.getHourOfDay();

							outm += (d2.getMinuteOfDay()) % 60;

						}
						if (attendance.getEsslIntime() != null && attendance.getEsslOuttime() != null) {
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

				if (attendancesList.size() - (absentRows + wfhRows) != 0) {
					String inDate1 = inOutDateCalculation(registerRows, inh, inm, absentRows, wfhRows);
					String outDate1 = inOutDateCalculation(registerRows, outh, outm, absentRows, wfhRows);
					String esslInDate = inOutDateCalculation(esslRows, esslInH, esslInM, absentRows, wfhRows);
					String esslOutDate = inOutDateCalculation(esslRows, esslOutH, esslOutM, absentRows, wfhRows);

					Date inDate = null;
					Date outDate = null;
					Date esslInDate1 = null;
					Date esslOutDate1 = null;

					try {

						String dateString = sdf.format(d1);

						inDate = formatingDate(dateString, inDate1);
						outDate = formatingDate(dateString, outDate1);
						esslInDate1 = formatingDate(dateString, esslInDate);
						esslOutDate1 = formatingDate(dateString, esslOutDate);
						if (inDate.equals(outDate))
							;
						else {
							attendanceObj.inTime = inDate;
							attendanceObj.outTime = outDate;
						}
						if (esslInDate1.equals(esslOutDate1))
							;
						else {
							attendanceObj.esslInTime = esslInDate1;
							attendanceObj.esslOutTime = esslOutDate1;
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
					DateTime dt1 = new DateTime(inDate);
					DateTime dt2 = new DateTime(outDate);
					DateTime dt3 = new DateTime(esslInDate1);
					DateTime dt4 = new DateTime(esslOutDate1);

					long diffHours = Hours.hoursBetween(dt1, dt2).getHours() % 24;
					long diffMinutes = Minutes.minutesBetween(dt1, dt2).getMinutes() % 60;
					long diffHours1 = Hours.hoursBetween(dt3, dt4).getHours() % 24;
					long diffMinutes1 = Minutes.minutesBetween(dt3, dt4).getMinutes() % 60;

					if (diffHours == 0 && diffMinutes == 0)
						;
					else {
						attendanceObj.spendTime = minuteAdjustZeroToNine(diffHours, diffMinutes);
					}
					if (diffHours1 == 0 && diffMinutes1 == 0)
						;
					else {
						attendanceObj.esslSpendTime = minuteAdjustZeroToNine(diffHours1, diffMinutes1);
					}
				}
				attendanceObj.absent = absentRows;
				attendanceObj.present = registerRows;
				attendanceObj.esslPresent = esslRows;
				attendanceObj.WFH = wfhRows;
				attendanceObj.date = d1;
				attendanceObj.notFilled = notFilled;

				attendanceList1.add(attendanceObj);

			}
		}
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
		String sd = sdf1.format(sdate);
		String ed = sdf1.format(edate);

		return ok(views.html.admin.attendanceWeekWiseData.render(attendanceList1, sd, ed));
	}

	@AdminHRAnnotation
	public Result attendanceDatewiseRender() {
		return ok(views.html.admin.attendanceWeekwise.render());
	}

	public Result editAttendance(Long id) {
		Attendance attendance = Attendance.find.byId(id);
		List<Attendance> attendanceList = new ArrayList<>();
		attendanceList = Attendance.find.where().eq("date", attendance.getDate()).findList();
		attendanceList.remove(attendance);
		return ok(views.html.admin.editAttendance.render(attendance, attendanceList));
	}

	public Result editAttendanceAction() {
		AttendenceEditBean attendance = Form.form(AttendenceEditBean.class).bindFromRequest().get();
		Attendance attendance1 = Attendance.find.byId(attendance.id);
		AppUser appUser = attendance1.getAppUser();
		try {
			if (attendance.status.equalsIgnoreCase("present")) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
				String d = sdf.format(attendance1.date);
				attendance1.setStatus(AttendenceStatus.Present);
				Date inDate = null;
				SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
				inDate = sdf1.parse(d + " " + attendance.inTime);
				Date outDate = null;

				SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
				outDate = sdf2.parse(d + " " + attendance.outTime);
				Date d1 = null;
				String d2 = "";
				long diff = outDate.getTime() - inDate.getTime();
				if (diff < 0) {
					final Calendar calendar = Calendar.getInstance();
					calendar.setTime(attendance1.date);
					calendar.add(Calendar.DAY_OF_YEAR, 1);
					d1 = calendar.getTime();
					d2 = sdf.format(d1);
					outDate = sdf2.parse(d2 + " " + attendance.outTime);
				}
				DateTime dt1 = new DateTime(inDate);
				DateTime dt2 = new DateTime(outDate);

				long diffHours = Hours.hoursBetween(dt1, dt2).getHours() % 24;
				long diffMinutes = Minutes.minutesBetween(dt1, dt2).getMinutes() % 60;
				long diffSeconds = Seconds.secondsBetween(dt1, dt2).getSeconds() % 60;

				attendance1.setInTime(inDate);
				attendance1.setOutTime(outDate);

				attendance1.setSpendTime(minuteAdjustZeroToNine(diffHours, diffMinutes));
				attendance1.update();
				appUser.attendences.add(attendance1);
				appUser.update();
				;

			} else if (attendance.status.equalsIgnoreCase("absent")) {
				attendance1.setStatus(AttendenceStatus.Absent);
				attendance1.setInTime(null);
				attendance1.setOutTime(null);
				attendance1.setSpendTime(null);
				attendance1.update();
				appUser.attendences.add(attendance1);
				appUser.update();
			} else if (attendance.status.equalsIgnoreCase("WFH")) {

				attendance1.setStatus(AttendenceStatus.WFH);
				attendance1.setInTime(null);
				attendance1.setOutTime(null);
				attendance1.setSpendTime(null);
				attendance1.update();
				appUser.attendences.add(attendance1);
				appUser.update();
			} else {
				attendance1.setStatus(AttendenceStatus.CL);
				attendance1.setInTime(null);
				attendance1.setOutTime(null);
				attendance1.setSpendTime(null);
				attendance1.update();
				appUser.attendences.add(attendance1);
				appUser.update();
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<AppUser> appUserList = AppUser.find.where().eq("status", UserProjectStatus.Active).findList();
		List<Attendance> attendanceList1 = new ArrayList<>();
		attendanceList1 = Attendance.find.where().eq("date", attendance1.date).findList();

		List<AppUser> appUserListThoseFill = new ArrayList<>();
		if (attendanceList1 != null) {
			for (Attendance attendance2 : attendanceList1) {
				if (attendance2.getInTime() != null || attendance.getStatus() != null) {
					appUserListThoseFill.add(attendance2.appUser);
				}
			}
			appUserList.removeAll(appUserListThoseFill);
		}
		return ok(views.html.admin.addAttendence.render(appUserList, attendanceList1, attendance1.date));
	}

	public Result uploadAttendance() {
		SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy");

		try {

			MultipartFormData body = request().body().asMultipartFormData();
			MultipartFormData.FilePart picture = body.getFile("file");

			if (picture != null) {

				String filename = picture.getFilename();
				filename = filename.substring(filename.indexOf("."), filename.length());

				if (!filename.equals(".dat")) {
					flash().put("alert", new Alert("alert-danger", "Only Upload '.dat' file").toString());
					return redirect(routes.AdminController.attendenceRender());
				}

				File file = picture.getFile();
				FileReader input = new FileReader(file.toPath().toFile());
				BufferedReader br = new BufferedReader(input);
				String s = "";
				Date inDate = null;
				Date outDate = null;
				int flag = 0, flag1 = 0, count = 0;
				while ((s = br.readLine()) != null) {

					String s1[] = s.trim().split("\t");
					long esslId = Long.parseLong(s1[0]);

					Date date = null;
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					date = sdf.parse(s1[1]);
					// Logger.debug("Date "+date);
					BiometricAttendance BAttendance = BiometricAttendance.find.where().eq("esslId", esslId)
							.eq("date", date).findUnique();

					if (BAttendance == null) {

						BAttendance = new BiometricAttendance();

						BAttendance.setDate(date);
						BAttendance.setEsslId(esslId);
						BAttendance.setStatusCode(Integer.parseInt(s1[4]));
						BAttendance.save();

						if (flag == 0) {
							inDate = date;
						}
						outDate = date;

						flag++;
					} else {
						if (flag1 == 0) {
							inDate = date;
							flag1++;
						}
						outDate = date;
						count++;
					}
				}

				DateTime dt1 = new DateTime(inDate);
				DateTime dt2 = new DateTime(outDate);
				int dateBetween = Days.daysBetween(dt1, dt2).getDays();
				Date secondDate = null;
				Date firstDate = null;

				if (sf.format(outDate).equals(sf.format(new Date()))) {
					dateBetween = dateBetween + 1;
					// Logger.debug("daBTW"+dateBetween);
				}

				for (int i = 0; i <= dateBetween; i++) {

					if (i == 0) {
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(inDate);
						calendar.set(Calendar.HOUR_OF_DAY, 0);
						calendar.set(Calendar.MINUTE, 0);
						calendar.set(Calendar.SECOND, 0);
						firstDate = calendar.getTime();

						calendar.setTime(firstDate);
						calendar.set(Calendar.HOUR_OF_DAY, 23);
						calendar.set(Calendar.MINUTE, 59);
						calendar.set(Calendar.SECOND, 59);
						secondDate = calendar.getTime();

					} else {
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(inDate);
						calendar.add(Calendar.DAY_OF_YEAR, i);
						calendar.set(Calendar.HOUR_OF_DAY, 0);
						calendar.set(Calendar.MINUTE, 0);
						calendar.set(Calendar.SECOND, 0);
						firstDate = calendar.getTime();

						Calendar calendar1 = Calendar.getInstance();
						calendar1.setTime(firstDate);
						calendar1.set(Calendar.HOUR_OF_DAY, 23);
						calendar1.set(Calendar.MINUTE, 59);
						calendar1.set(Calendar.SECOND, 59);
						secondDate = calendar1.getTime();

					}

					BiometricAttendance inBiometric = null;
					BiometricAttendance outBiometric = null;

					List<AppUser> appUsers = AppUser.find.where().eq("status", UserProjectStatus.Active).findList();
					for (AppUser appUser : appUsers) {

						// Logger.debug(firstDate+"two dates "+secondDate);
						List<BiometricAttendance> BAttendanceList = BiometricAttendance.find.where()
								.eq("esslId", appUser.getEsslId()).between("date", firstDate, secondDate).findList();

						for (BiometricAttendance bAttendance : BAttendanceList) {
							if (bAttendance.getStatusCode() == 0) {
								inBiometric = bAttendance;
								break;
							}

						}

						for (int j = BAttendanceList.size() - 1; j >= 0; j--) {
							if (BAttendanceList.get(j).getStatusCode() == 101) {
								outBiometric = (BiometricAttendance) BAttendanceList.get(j);
								break;
							}

						}

						long hours = 0, minutes = 0;
						/*
						 * int m = 0, n = 1;
						 * Logger.debug("size "+BAttendanceList.size());
						 * for(BiometricAttendance BiometricAttendance:
						 * BAttendanceList){
						 * Logger.debug("Date "+BiometricAttendance.date +
						 * " Status Code "+BiometricAttendance.statusCode); }
						 */
						/*
						 * int k =0; while (n < BAttendanceList.size()) {
						 * 
						 * DateTime dt3 = null; DateTime dt4 = null;
						 * 
						 * if ((BAttendanceList.get(m).getStatusCode() == 0) &&
						 * (BAttendanceList.get(n).getStatusCode() == 101)) {
						 * Logger.debug("if"+k); Date mdate =
						 * BAttendanceList.get(m).getDate(); Date ndate =
						 * BAttendanceList.get(n).getDate();
						 * 
						 * if (mdate.getHours() == 00) { mdate.setHours(12); }
						 * if (ndate.getHours() == 00) { ndate.setHours(12); }
						 * dt3 = new DateTime(mdate); dt4 = new DateTime(ndate);
						 * if ((n - m) == 1) { n = n + 2; m = m + 2; } else { n
						 * = n + 2; m = n - 1; } long diffHours =
						 * Hours.hoursBetween(dt3,dt4).getHours() % 24; long
						 * diffMinutes = Minutes.minutesBetween(dt3,
						 * dt4).getMinutes() % 60; hours = hours + diffHours;
						 * minutes = minutes + diffMinutes;
						 * 
						 * } else if ((BAttendanceList.get(m).getStatusCode() ==
						 * 0) && (BAttendanceList.get(n).getStatusCode() == 0))
						 * { Logger.debug("else-if1"+k); n++; } else if
						 * ((BAttendanceList.get(m).getStatusCode() == 101) &&
						 * (BAttendanceList.get(n).getStatusCode() == 101)) {
						 * Logger.debug("else-if2"+k); m = m + 2; n = n + 2; }
						 * else { m++; n++; Logger.debug("else3"+k); } k++; }
						 */

						/* Starting - extra code for testing */

						/*
						 * long finalhours = 0, finalminutes = 0; if(1 <
						 * BAttendanceList.size()){ if
						 * ((BAttendanceList.get(0).getStatusCode() == 0) &&
						 * (BAttendanceList.get(BAttendanceList.size()-1).
						 * getStatusCode() == 101)) { Date sDate =
						 * BAttendanceList.get(0).getDate(); Date eDate =
						 * BAttendanceList.get(BAttendanceList.size()-1).getDate
						 * ();
						 * 
						 * long diff = eDate.getTime() - sDate.getTime(); long
						 * diffSeconds = diff / 1000 % 60; long diffMinutes =
						 * diff / (60 * 1000) % 60; long diffHours = diff / (60
						 * * 60 * 1000);
						 * 
						 * finalhours = finalhours + diffHours; finalminutes =
						 * finalminutes + diffMinutes;
						 * 
						 * } }
						 */

						int countIndex = 1;
						Boolean strtIndex = false;
						while (countIndex < BAttendanceList.size()) {

							if (countIndex + 1 == BAttendanceList.size()) {
								break;
							}

							if (BAttendanceList.get(countIndex - 1).getStatusCode() == 0) {
								strtIndex = true;
							}

							if (strtIndex == true && BAttendanceList.get(countIndex).getStatusCode() == 101
									&& BAttendanceList.get(countIndex + 1).getStatusCode() == 0) {

								Date sDate = BAttendanceList.get(countIndex).getDate();
								Date eDate = BAttendanceList.get(countIndex + 1).getDate();

								long diff = eDate.getTime() - sDate.getTime();
								long diffSeconds = diff / 1000 % 60;
								long diffMinutes = diff / (60 * 1000) % 60;
								long diffHours = diff / (60 * 60 * 1000);

								hours = hours + diffHours;
								minutes = minutes + diffMinutes;
								// Logger.debug("hours "+hours +"
								// minutes"+minutes);
								// Logger.debug("sDate "+sDate +" eDate"+eDate);
							}
							countIndex++;
						}

						hours = hours + minutes / 60;
						minutes = minutes % 60;

						/* Ending - extra code for testing */

						Attendance attendanceCheck = Attendance.find.where().eq("appUser", appUser)
								.eq("date", firstDate).findUnique();
						if (attendanceCheck == null) {
							if (inBiometric != null && sf.format(inBiometric.getDate()).equals(sf.format(new Date()))) {

								Attendance attendance = new Attendance();

								Calendar calendar = Calendar.getInstance();
								calendar.setTime(inBiometric.getDate());
								calendar.set(Calendar.HOUR_OF_DAY, 0);
								calendar.set(Calendar.MINUTE, 0);
								calendar.set(Calendar.SECOND, 0);

								attendance.setDate(calendar.getTime());
								attendance.setEsslIntime(inBiometric.getDate());
								attendance.setAppUser(appUser);
								attendance.save();

								appUser.getAttendences().add(attendance);
								appUser.update();

								inBiometric = null;
								outBiometric = null;

							} else if (inBiometric != null && outBiometric != null
									&& !inBiometric.getDate().equals(new Date())) {

								Attendance attendance = new Attendance();

								Calendar calendar = Calendar.getInstance();
								calendar.setTime(inBiometric.getDate());
								calendar.set(Calendar.HOUR_OF_DAY, 0);
								calendar.set(Calendar.MINUTE, 0);
								calendar.set(Calendar.SECOND, 0);

								attendance.setDate(calendar.getTime());
								attendance.setEsslIntime(inBiometric.getDate());
								attendance.setEsslOuttime(outBiometric.getDate());

								DateTime dt3 = new DateTime(inBiometric.getDate());
								DateTime dt4 = new DateTime(outBiometric.getDate());

								long diffHours = Hours.hoursBetween(dt3, dt4).getHours() % 24;
								long diffMinutes = Minutes.minutesBetween(dt3, dt4).getMinutes() % 60;

								attendance.setEsslSpendtime(minuteAdjustZeroToNine(diffHours, diffMinutes));

								attendance.setEsslBreakTime(minuteAdjustZeroToNine(hours, minutes));

								diffMinutes = diffHours * 60 + diffMinutes;
								minutes = hours * 60 + minutes;

								diffMinutes = diffMinutes - minutes;

								hours = diffMinutes / 60;
								minutes = diffMinutes % 60;

								attendance.setTimeInOffice(minuteAdjustZeroToNine(hours, minutes));
								attendance.setAppUser(appUser);
								attendance.save();

								appUser.getAttendences().add(attendance);
								appUser.update();

								inBiometric = null;
								outBiometric = null;
							}

						} else {

							if (inBiometric != null && sf.format(inBiometric.getDate()).equals(sf.format(new Date()))) {

								Calendar calendar = Calendar.getInstance();
								calendar.setTime(inBiometric.getDate());
								calendar.set(Calendar.HOUR_OF_DAY, 0);
								calendar.set(Calendar.MINUTE, 0);
								calendar.set(Calendar.SECOND, 0);

								attendanceCheck.setDate(calendar.getTime());
								attendanceCheck.setEsslIntime(inBiometric.getDate());
								attendanceCheck.setAppUser(appUser);
								attendanceCheck.update();

								appUser.getAttendences().add(attendanceCheck);
								appUser.update();

								inBiometric = null;
								outBiometric = null;

							} else if (inBiometric != null && outBiometric != null) {

								attendanceCheck.setEsslIntime(inBiometric.getDate());
								attendanceCheck.setEsslOuttime(outBiometric.getDate());

								DateTime dt3 = new DateTime(inBiometric.getDate());
								DateTime dt4 = new DateTime(outBiometric.getDate());

								long diffHours = Hours.hoursBetween(dt3, dt4).getHours() % 24;
								long diffMinutes = Minutes.minutesBetween(dt3, dt4).getMinutes() % 60;

								attendanceCheck.setEsslSpendtime(minuteAdjustZeroToNine(diffHours, diffMinutes));
								attendanceCheck.setEsslBreakTime(minuteAdjustZeroToNine(hours, minutes));

								diffMinutes = diffHours * 60 + diffMinutes;
								minutes = hours * 60 + minutes;

								diffMinutes = diffMinutes - minutes;

								hours = diffMinutes / 60;
								minutes = diffMinutes % 60;

								attendanceCheck.setTimeInOffice(minuteAdjustZeroToNine(hours, minutes));
								attendanceCheck.update();

								appUser.getAttendences().add(attendanceCheck);
								appUser.update();

								inBiometric = null;
								outBiometric = null;
							}
						} // end if-else - attendanceCheck

					} // end for loop - appUser List

				} //// end if file checking
				flash().put("alert",
						new Alert("alert-success", "Your data has been uploaded successfully !").toString());
			} else {
				flash().put("alert", new Alert("alert-danger", "File is Empty!").toString());
			}
		} catch (Exception e) {
			flash().put("alert", new Alert("alert-danger", "File not Supported!").toString());
			e.printStackTrace();

		}
		return redirect(routes.AdminController.attendenceRender());
	}

	public Result uploadAttendanceXls() throws IOException, ParseException {
		SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy");
		AttendenceBean attendenceBean = Form.form(AttendenceBean.class).bindFromRequest().get();
		int diffInDays = (int) ((sf.parse(attendenceBean.toDate).getTime()
				- sf.parse(attendenceBean.fromDate).getTime()) / (1000 * 60 * 60 * 24));
		MultipartFormData body = request().body().asMultipartFormData();
		MultipartFormData.FilePart picture = body.getFile("file");
		try {
			if (picture != null) {
				String filename = picture.getFilename();
				filename = filename.substring(filename.indexOf("."), filename.length());
				if (!filename.equals(".xlsx")) {
					flash().put("alert", new Alert("alert-danger", "Only Upload xlsx file").toString());
					return redirect(routes.AdminController.attendenceRender());
				}
				File file = picture.getFile();
				FileReader input = new FileReader(file.toPath().toFile());
				FileInputStream inputStream = new FileInputStream(file);
				Workbook workbook = new XSSFWorkbook(inputStream);
				for (int d = 0; d <= diffInDays; d++) {
					String dd = attendenceBean.fromDate;
					Calendar c = Calendar.getInstance();
					c.setTime(sf.parse(dd));
					c.add(Calendar.DATE, d); // number of days to add
					dd = sf.format(c.getTime());
					Date dd1 = sf.parse(dd);
					for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
						Sheet firstSheet = workbook.getSheetAt(i);
						Date sheetDate = sf.parse(firstSheet.getSheetName());
						String tdate = sf.format(new Date());
						Date todayDate = sf.parse(tdate);
						if (dd1.equals(sheetDate)) {
							int p = 0;
							for (Row row : firstSheet) {
								if (p > 0) {
									Date inTime = null;
									Date outTime = null;
									Attendance attendance = new Attendance();
									attendance.setDate(dd1);
									int k = 0;
									for (Cell cell : row) {
										switch (cell.getCellType()) {
										case Cell.CELL_TYPE_STRING:
											if (k == 1) {
												AppUser appUser = null;
												appUser = AppUser.find.where()
														.eq("employeeId", cell.getStringCellValue()).findUnique();
												attendance.setAppUser(appUser);
											} else {
												String status = cell.getStringCellValue();
												if (status.equalsIgnoreCase("p")) {
													attendance.setStatus(AttendenceStatus.Present);
												} else if (status.equalsIgnoreCase("a")) {
													attendance.setStatus(AttendenceStatus.Absent);
												} else {
													attendance.setStatus(AttendenceStatus.WFH);
												}
											}
											break;
										case Cell.CELL_TYPE_NUMERIC:
											Date date = cell.getDateCellValue();
											SimpleDateFormat sfd = new SimpleDateFormat("hh:mm a");
											SimpleDateFormat sfdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
											String time = sfd.format(date);
											Date times = sfdf.parse(sf.format(dd1) + " " + time);
											if (k == 3 && attendance.getStatus().equals(AttendenceStatus.Present)
													&& cell.getDateCellValue() != null) {
												inTime = sfd.parse(time);
												attendance.setInTime(times);
											} else if (k == 4 && attendance.getStatus().equals(AttendenceStatus.Present)
													&& cell.getDateCellValue() != null) {
												outTime = sfd.parse(time);
												attendance.setOutTime(times);
											}
										default:
											break;
										}
										k++;
									}
									if (attendance.getAppUser() != null
											&& attendance.getStatus().equals(AttendenceStatus.Present)
											&& attendance.getInTime() != null && attendance.getOutTime() != null) {
										long diff = outTime.getTime() - inTime.getTime();
										long diffMinutes = diff / (60 * 1000) % 60;
										long diffHours = diff / (60 * 60 * 1000) % 24;
										attendance.setSpendTime(diffHours + ":" + diffMinutes);
									}
									if (attendance.getAppUser() != null) {
										Attendance attendanceCheck = Attendance.find.where()
												.eq("date", attendance.getDate()).eq("appUser", attendance.getAppUser())
												.findUnique();
										if (attendanceCheck == null) {
											attendance.save();
										} else {
											attendanceCheck.setDate(attendance.getDate());
											attendanceCheck.setStatus(attendance.getStatus());
											if (attendance.getInTime() != null) {
												attendanceCheck.setInTime(attendance.getInTime());
											}
											if (attendance.getOutTime() != null) {
												attendanceCheck.setOutTime(attendance.getOutTime());
												attendanceCheck.setSpendTime(attendance.getSpendTime());
											}
											attendanceCheck.update();
										}
									}
								}
								p++;
							}
						}
					}
				}
				inputStream.close();
			}
			flash().put("alert", new Alert("alert-success", " Attendance Xls file Successfully Uploaded").toString());
			return redirect(routes.AdminController.attendenceRender());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			flash().put("alert", new Alert("alert-danger", "" + e).toString());
			return redirect(routes.AdminController.attendenceRender());
		}
	}

	/*
	 * ************************ Leads Action Methods *
	 * ******************************
	 */
	public static String minuteAdjustZeroToNine(long hours, long minutes) {
		String time = "";
		if (minutes <= 9) {
			time = hours + ":0" + minutes;
		} else {
			time = hours + ":" + minutes;
		}
		return time;

	}

	/**
	 * All Reports Methods Start....EMP32
	 */
	// Reports URL Redirection for Doctor,service,User reports
	public Result redirectMultiParamsReportUrl(String url) {
		StringBuilder reportUrl = new StringBuilder();
		reportUrl.append(ConstructReportUrl.urlCommonPart());
		reportUrl.append(url);
		reportUrl.append(ConstructReportUrl.AMPERSAND);
		reportUrl.append(ConstructReportUrl.USERNAME);
		reportUrl.append(ConstructReportUrl.AMPERSAND);
		reportUrl.append(ConstructReportUrl.PASSWORD);
		return ok(views.html.reports.reportUrlRedirect.render(reportUrl.toString()));
	}

	// DSCDateWiseReport or SEDateWiseReport .....EMP:32
	public Result DSCDateWiseReport(String reportType, Long id) {
		if (reportType == "dscdr") {
			return ok(views.html.reports.DSCDatewisereport.render(reportType, id));
		} else if (reportType == "sedr") {
			return ok(views.html.reports.DSCDatewisereport.render(reportType, Long.parseLong(session("AppUserId"))));
		} else {
			return ok(views.html.reports.DSCDatewisereport.render(reportType, Long.parseLong(session("AppUserId"))));
		}
	}

	// Selection of app user for admin & Week wise employee Summary and
	// Attendance Reports ....EMP:32
	public Result employeeReport(String typeOfReport) {
		if (typeOfReport.equalsIgnoreCase("sumarry-Week-Wise-Report")) {

			return ok(views.html.reports.engineer_employeereport.render(Long.parseLong(session("AppUserId")),
					"sumarry-Week-Wise-Report"));

		} else if (typeOfReport.equalsIgnoreCase("sumarry-Week-Wiseemp-Report")) {
			return ok(views.html.reports.engineer_employeereport.render(null, "sumarry-Week-Wiseemp-Report"));
		} else if (typeOfReport.equalsIgnoreCase("attendance")) {
			return ok(views.html.reports.engineer_employeereport.render(Long.parseLong(session("AppUserId")),
					"attendance"));

		} else if (typeOfReport.equalsIgnoreCase("attendanceComapany")) {

			return ok(views.html.reports.engineer_employeereport.render(null, "attendanceComapany"));

		} else if (typeOfReport.equalsIgnoreCase("empweek-Wise-Report")) {

			return ok(views.html.reports.engineer_employeereport.render(null, "empweek-Wise-Report"));

		} else if (typeOfReport.equalsIgnoreCase("Employee_Monthly_Status_Report")) {
			return ok(views.html.reports.EmployeeMonthlyReport.render(null, "Employee_Monthly_Status_Report"));

		} else if (typeOfReport.equalsIgnoreCase("Employee_Project_Details")) {
			return ok(views.html.reports.EmployeeMonthlyReport.render(null, "Employee_Project_Details"));

		} else if (typeOfReport.equalsIgnoreCase("all_employeeDsr")) {
			return ok(views.html.reports.EmployeeMonthlyReport.render(null, "All_Employee_Status"));

		} else if (typeOfReport.equalsIgnoreCase("anytime_report")) {
			return ok(views.html.reports.EmployeeMonthlyReport.render(Long.parseLong(session("AppUserId")),
					"anytime_report"));

		} else if (typeOfReport.equalsIgnoreCase("all-employee-biometric-report")){
			return ok(views.html.reports.EmployeeMonthlyReport.render(Long.parseLong(session("AppUserId")),
					"all-employee-biometric-report.xls"));
		} else{
			// Logger.info("else");
			return ok(views.html.reports.engineer_employeereport.render(Long.parseLong(session("AppUserId")),
					"DSMonth-Wise-Report"));

		}
	}
	// All Reports Methods END

	// Start Incident - Policies Coding

	public Result reportCriticalIncident() {
		return ok(views.html.admin.reportCriticalIncident.render());
	}

	public Result userPolicy() {
		return ok(views.html.admin.userPolicy.render());
	}

	public Result allPolicy() {
		return ok(views.html.admin.allPolicy.render());
	}

	public Result addIncident() {

		Incident incident = Form.form(Incident.class).bindFromRequest().get();
		DynamicForm requestForm = Form.form().bindFromRequest();
		try {
			MultipartFormData body = request().body().asMultipartFormData();
			FilePart incidentImage = body.getFile("image");
			File imageFile = null;
			String imageFileName = null;
			if (incidentImage != null) {
				String filename = incidentImage.getFilename();
				filename = filename.substring(filename.indexOf("."), filename.length());
				incident.imageContentType = incidentImage.getContentType();
				incident.imageName = incidentImage.getFilename();
				imageFile = incidentImage.getFile();
				incident.image = Files.toByteArray(incidentImage.getFile());

				if (incident.image != null) {
					imageFileName = "conf/excel/" + incident.imageName;
					imageFile = new File(imageFileName);
					FileOutputStream outputStream = new FileOutputStream(imageFile);
					outputStream.write(incident.image);
				}

			}

			// CIRType cIRType =
			// CIRType.find.byId(Long.parseLong(requestForm.get("CIRNameId")));
			// incident.incidentName = cIRType;

			AppUser appUser = Application.getLoggedInUser();
			incident.appUser = appUser;
			incident.save();

			// send incident mail
			Set<AppUser> setUsers = new HashSet<AppUser>();
			List<Role> listRole = new ArrayList<Role>();
			Role roleAdmin = Role.find.where().eq("role", "Admin").findUnique();
			Role roleHR = Role.find.where().eq("role", "HR").findUnique();
			listRole.add(roleAdmin);
			listRole.add(roleHR);
			setUsers.addAll(
					AppUser.find.where().in("role", listRole).eq("status", UserProjectStatus.Active).findList());
			if (appUser.getReportMangerId() != 0) {
				setUsers.add(AppUser.find.byId(appUser.getReportMangerId()));
			}
			List<AppUser> adminHRRMGUsers = new ArrayList<AppUser>(setUsers);
			SampleDataController.sendIncidentMail(appUser, adminHRRMGUsers, imageFileName, incident);

			if (imageFile != null) {
				imageFile.delete();
			}
			flash().put("alert",
					new Alert("alert-success", "Report Critical Incident Successfully Submitted!").toString());
		} catch (Exception e) {
			flash().put("alert",
					new Alert("alert-danger", "Report Critical Incident Not Successfully Submitted!").toString());
			e.printStackTrace();
		}
		return redirect(routes.AdminController.reportCriticalIncident());
	}

	public Result getViewImage(Long id) {
		Incident incident = Incident.find.byId(id);
		String uploadFileContentType = incident.imageContentType;
		ByteArrayInputStream input = null;
		try {
			if (incident != null && incident.imageContentType != null) {
				input = new ByteArrayInputStream(incident.image);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ok(input).as(uploadFileContentType);
	}

	public Result downloadIncidentImage(Long id) {
		Incident incident = Incident.find.byId(id);
		String uploadFileContentType = incident.imageContentType;
		String uploadFileName = incident.imageName;
		byte[] uploadFile = incident.image;
		response().setContentType("APPLICATION/OCTET-STREAM");
		response().setHeader("Content-Disposition", "attachment; filename=\"" + uploadFileName + "\"");
		return ok(uploadFile).as(uploadFileContentType);
	}

	public Result addPolicy() {
		Policy policy = Form.form(Policy.class).bindFromRequest().get();
		try {
			MultipartFormData body = request().body().asMultipartFormData();
			FilePart policyFile = body.getFile("file");
			if (policyFile != null) {
				policy.fileContentType = policyFile.getContentType();
				policy.fileName = policyFile.getFilename();
				policy.file = Files.toByteArray(policyFile.getFile());
			}
			policy.appUser = Application.getLoggedInUser();
			policy.save();

			flash().put("alert", new Alert("alert-success", "Policy Successfully Added!").toString());
		} catch (Exception e) {
			flash().put("alert", new Alert("alert-danger", "Policy Not Successfully Added!").toString());
			e.printStackTrace();
		}
		return redirect(routes.AdminController.userPolicy());
	}

	public Result getPolicyDocument(Long id) {
		Policy policy = Policy.find.byId(id);
		String uploadFileContentType = policy.fileContentType;
		ByteArrayInputStream input = null;
		try {
			if (policy != null && policy.fileContentType != null) {
				input = new ByteArrayInputStream(policy.file);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ok(input).as(uploadFileContentType);
	}

	public Result editPocily(Long pid) {

		Policy policy = Policy.find.byId(pid);

		return ok(views.html.admin.editPolicy.render(policy));

	}

	public Result updatePolicy() {
		Policy policy = Form.form(Policy.class).bindFromRequest().get();
		try {
			MultipartFormData body = request().body().asMultipartFormData();
			FilePart policyFile = body.getFile("file");
			if (policyFile != null) {
				policy.fileContentType = policyFile.getContentType();
				policy.fileName = policyFile.getFilename();
				policy.file = Files.toByteArray(policyFile.getFile());
			}
			// policy.appUser = Application.getLoggedInUser();
			policy.update();

			flash().put("alert",
					new Alert("alert-success", policy.policyName + " Policy Successfully Updated!").toString());
		} catch (Exception e) {
			flash().put("alert",
					new Alert("alert-danger", policy.policyName + " Policy Not Successfully Updated!").toString());
			e.printStackTrace();
		}
		return redirect(routes.AdminController.userPolicy());
	}

	public Result deletePocily(Long pid) {
		try {
		Policy policy = Policy.find.byId(pid);
		policy.delete();
				flash().put(
						"alert",
						new Alert("alert-success",
								policy.policyName+" Policy Successfully Deleted!").toString());
		}catch (Exception e) {
			flash().put(
					"alert",
					new Alert("alert-danger",
							" Policy Not Successfully Deleted!").toString());
			e.printStackTrace();
		}
		return redirect(routes.AdminController.userPolicy());
	}

	public Result addCIRType() {
		try {
			DynamicForm requestForm = Form.form().bindFromRequest();
			CIRType cIRType = Form.form(CIRType.class).bindFromRequest().get();
			cIRType.IncidentType = requestForm.get("IncidentType");
			cIRType.save();
		} catch (Exception e) {
			// TODO: handle exception
		}
		/*
		 * CIRType cIRType = new CIRType(); cIRType.IncidentType = "HR";
		 * cIRType.save(); CIRType cIRType1 = new CIRType();
		 * cIRType1.IncidentType = "PMO"; cIRType1.save(); CIRType cIRType2 =
		 * new CIRType(); cIRType2.IncidentType = "Sales"; cIRType2.save();
		 * CIRType cIRType3 = new CIRType(); cIRType3.IncidentType =
		 * "Marketing"; cIRType3.save(); CIRType cIRType4 = new CIRType();
		 * cIRType4.IncidentType = "Finance"; cIRType4.save(); CIRType cIRType5
		 * = new CIRType(); cIRType5.IncidentType = "Operations";
		 * cIRType5.save(); CIRType cIRType6 = new CIRType();
		 * cIRType6.IncidentType = "Engineer"; cIRType6.save();
		 */
		return redirect(routes.AdminController.reportCriticalIncident());
	}

}
