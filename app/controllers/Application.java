package controllers;

 
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.common.io.Files;

import action.AdminAnnotation;
import action.AdminHRAnnotation;
import action.BasicAuth;
import bean.Bean;
import bean.CpwdBean;
import bean.ProjectBean;
import models.Alert;
import models.AppUser;
import models.ProjectDetails;
import models.Projects;
import models.Role;
import models.UserProjectStatus;
import models.chat.ChatGroup;
import models.chat.ChatGroupAppUserInfo;
import models.chat.FileComment;
import models.chat.Message;
import models.chat.MessageContentType;
import models.chat.UploadFileInfo;
import models.incident.CIRType;
import models.incident.Policy;
import models.leave.Entitlement;
import models.leave.Holidays;
import models.leave.LeaveStatus;
import models.leave.LeaveType;
import models.leave.Leaves;
import models.recruitment.MailingList;
import models.recruitment.RecruitmentReference;
import play.Logger;
import play.Play;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import utils.CalenderInviteOfInterviews;
import utils.DateUtil;
import utils.GoogleDriveEvent;

public class Application extends Controller {

	
	
	public Result storeComments(){
		for (Message message : Message.find.where().in("messageContentType", MessageContentType.FILE, MessageContentType.IMAGE, MessageContentType.SNIPPET).findList()) {
			boolean isException=false;
			try {
				synchronized (message) {
					List<UploadFileInfo> uploadFileInfoList = UploadFileInfo.find.where().eq("message",message).findList();
					if (message.comments !=null && !(message.comments.isEmpty()) && !message.comments.equalsIgnoreCase("") 
							&& uploadFileInfoList != null && (!uploadFileInfoList.isEmpty()) && uploadFileInfoList.size() >= 1) {
						List<FileComment> FileCommentList=	FileComment.find.where().eq("message",message).findList();
						if (FileCommentList == null) {
							FileComment fileComment = new FileComment();
							fileComment.comment = message.comments;
							fileComment.uploadFileInfo = uploadFileInfoList.get(0);
							fileComment.message = message;
							fileComment.commentBy = message.messageBy;
							//Logger.info("message saving");
							fileComment.save();
							//Logger.info("message saved..");
						}
					}
				}
			} catch (Exception e) {
				isException=true;
				if(isException){
					continue;
				}
			}
		}
		return ok("comments stored..");
		
	}
	
	
	
	//BB8 Home Page Method
	public Result index() {
		
		AdminController.admin();
		try{
			//GoogleDriveEvent.uploadFileDrive();
//			CalenderInviteOfInterviews.sendCalenderInviteOfInterviews(MailingList.find.all(),0,new Date());
//			CalenderInviteOfInterviews.updateEvent();
			//PEController.CalculateWAR();
			//PEController.sendRedFlagNotificationHRAdmin();
			//PEController.sendMailRemindersSelf();
			//PEController.sendMailRemindersEmployee();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		//AdminController.generateRandomId();
	 	//AdminController.addUserNamesAllApppUsers();
		//AdminController.addGitHubUser();
		//AdminController.storeCompressedImage();
		//EngineerController.storeDate();
		//EngineerController.storeRemarks();
		//JobBean.setIds();
				//ApplicantBean.setIds();
				//RecruitmentController.CreateWordToPdf();
		/*if(session("AppUserId") != null) {
			AppUser appUser = AppUser.find.byId(Long.parseLong(session("AppUserId")));
				if(appUser.getLoginCheck().equals(false)){
					appUser.setLoginCheck(true);
					appUser.update();
				}
		}*/
		/*Random rn = new Random();
		for(int i =0; i < 10; i++)
		{
		    int answer = rn.nextInt(9) + 1;
		    System.out.println(answer);
		}*/
		session().clear();
		/*if(session("AppUserId") != null){
			Long id = Long.parseLong(session("AppUserId"));
			AppUser user = AppUser.find.byId(id);
	        if(user!=null){
	                   String role = user.role.get(0).role;
	                   session("AppUserId", user.id + "");
	                   session("AppUserRole", role + "");
	            return getDashBoard(user.role.get(0).role,user);
	        }
	        return ok(views.html.admin.index.render("Welcome Thrymr Software - Employee Portal"));
		}else{
           return ok(views.html.admin.index.render("Welcome Thrymr Software - Employee Portal"));
        }*/
		return ok(views.html.admin.index.render("Welcome Thrymr Software - Employee Portal"));
	}

	public Result index1() {
		return ok(views.html.engineer.engineerHomeDup.render());
	}
	
	//AppUser Logout
	public Result logout() {
		session().clear();
		flash().put("alert",new Alert("alert-success", "Logout Successfully ").toString());
		return redirect(routes.Application.index());
	}

	public Result loginError() {
		return redirect(routes.Application.index());
	}
	
	
	/* ***************** AppUser Login are Checked Methods ************************** */
	
	 public static AppUser getLoggedInUser() {

	    	Long id = null;
	    	final String idStr = session("AppUserId");
	    	
	    	if (idStr != null) {
	    	   id = Long.parseLong(idStr);
	    	    AppUser appsuer= AppUser.find.byId(id);
	    	    return appsuer;
	    	    
	    	} else {
	    	    return null;
	    	}

	    }
	
	public static Boolean isLoggedIn() {
        return session("AppUserId") == null ? false : true;
    }
	public static String getLoggedInUserRole() {
		final String role = session("AppUserRole");
		return role;
	}
	public static Role getLoggedInUserRoleId() {
		final String roles = session("AppUserRole");
		Role role = Role.find.where().eq("role",roles).findUnique();
		return role;
	}
	/*
	public static AppUser getLoggedInUser() {
		Long id = null;
		final String idStr = session("AppUserId");

		//Logger.error("***loggedin id"+idStr);
		if (idStr != null) {
			id = Long.parseLong(idStr);
			return AppUser.find.byId(id);

		}
		 final Long id = Long.parseLong(idStr); 
		// return AppUser.find.byId(id);

		return null;
	}*/
	
	/* Annotation Methods*/
	
	public static Boolean isAdmin() {
        if(session("AppUserRole") != null) {
        	return session("AppUserRole").equals("Admin") ? true : false;
        } else {
        	return false;
        }
    }
	
	public static Boolean isAdminMarket() {
        if(session("AppUserRole") != null) {
        	return session("AppUserRole").equals("Admin") || session("AppUserRole").equals("Marketing")? true : false;
        } else {
        	return false;
        }
    }
	
	public static Boolean isAdminHR() {
        if(session("AppUserRole") != null) {
        	return session("AppUserRole").equals("Admin") || session("AppUserRole").equals("HR")? true : false;
        } else {
        	return false;
        }
    }
	
	public static Boolean isManager() {
		 if(session("AppUserRole") != null) {
			 return session("AppUserRole").equals("Manager") ? true : false;
		 } else {
	        	return false;
	     }
    }
	
	public static Boolean isEngineer() {
		if(session("AppUserRole") != null) {
			return session("AppUserRole").equals("Engineer") || session("AppUserRole").equals("Marketing") || session("AppUserRole").equals("HR") ? true : false;
		} else {
	    	return false;
	    }
    }
	
	/* ***************** AppUser Change Password Method ************************** */
	
	public Result changePassword() {
		try {
			long id = Long.parseLong(session("AppUserId"));
			AppUser appUser = AppUser.find.byId(id);
			CpwdBean cpBean = Form.form(CpwdBean.class).bindFromRequest().get();
			String urole = session("AppUserRole");
			if(cpBean.cpwd.equals(decode(appUser.password))) {
				if(cpBean.npwd.equals(cpBean.rnpwd)) { 
					if(!cpBean.npwd.equals(decode(appUser.password))) {
						appUser.setPassword(encode(cpBean.npwd));
						appUser.setIsPasswordChange(false);
						appUser.update();
						flash().put("alert",new Alert("alert-success", "Password Successfully Updated ").toString());
						if(urole.equals("Admin")) {
							return redirect(routes.AdminController.home());
						} else if(urole.equals("Manager")) {
							return redirect(routes.ManagerController.home());
						} else {
							return redirect(routes.EngineerController.home());
						}
					} else if (appUser.getIsPasswordChange().equals(false)){
						flash().put("alert",new Alert("alert-danger", "New Password & Old Password both are same!").toString());
						return redirect(routes.Application.changePassword1());
					} else {
						flash().put("alert",new Alert("alert-danger", "New Password & Old Password both are same!!").toString());
						return ok(views.html.admin.firstchangepassword.render());
					}
				} else if (appUser.getIsPasswordChange().equals(false)){
					flash().put("alert",new Alert("alert-danger", "New Password & Re-Type New Password do not match!").toString());
					return redirect(routes.Application.changePassword1());
				} else {
					flash().put("alert",new Alert("alert-danger", "New Password & Re-Type New Password do not match!").toString());
					return ok(views.html.admin.firstchangepassword.render());
				}
			} else if(appUser.getIsPasswordChange().equals(false)){
				flash().put("alert",new Alert("alert-danger", "Incorrect Old Password!").toString());
				return redirect(routes.Application.changePassword1());
			} else {
				flash().put("alert",new Alert("alert-danger", "Incorrect Old Password!").toString());
				return ok(views.html.admin.firstchangepassword.render());
			}
		} catch(Exception e) {
			e.printStackTrace();
			return ok(views.html.error.render("Bad Request",400));
		}
	}
	
	
	/* ***************** AppUser Long Method ************************** */

	public Result login() {
		
		int count = 0;
		Bean bean = Form.form(Bean.class).bindFromRequest().get();

		try {
			AppUser appUser = AppUser.find.where().eq("email", bean.getEmail())
					.findUnique();
		
			if (appUser != null) {
				List<Role> roles = appUser.getRole();
				for (Role role : roles) {
					if (bean.getRole().equals(role.role)) {
						count++;
						break;
					}
				}
				
				if (bean.getPassword().equals(decode(appUser.password))) {
					if(appUser.status.equals(UserProjectStatus.Active)) { 
						if (bean.getRole().equals("Admin") && count == 1) {
								session("AppUserId", "" + appUser.getId());
								session("AppUserName", "" + appUser.getFullName());
								session("AppUserRole", "Admin");
								session("attendanceDate", "0");
								 if(appUser.getIsPasswordChange().equals(true)) {
									 	return ok(views.html.admin.firstchangepassword.render());
								 } else {
									flash().put("alert",new Alert("alert-success", "Welcome "+appUser.getFullName()).toString());
									 return ok(views.html.admin.homePage.render());
								 }
						} else if (bean.getRole().equals("Manager") && count == 1) {
								session("AppUserId", ""+appUser.getId());
								session("AppUserName", appUser.getFullName());
								session("email", appUser.getEmail());
								session("AppUserRole", "Manager");
								session("attendanceDate", "0");
									if(appUser.getIsPasswordChange().equals(true)) {
										return ok(views.html.admin.firstchangepassword.render());
									} else {
										flash().put("alert",new Alert("alert-success", "Welcome "+appUser.getFullName()).toString());
										return ok(views.html.manager.managerHome.render());
								}
						} else if (bean.getRole().equals("Engineer") || bean.getRole().equals("Marketing") && count == 1) {
								session("AppUserId", ""+ appUser.getId());
								session("AppUserName", "" + appUser.getFullName());
								session("email", appUser.getEmail());
								session("AppUserRole", bean.getRole());
								session("attendanceDate", "0");
								AppUser user = AppUser.find.byId(Long.parseLong(session("AppUserId")));
								List<ProjectDetails> myProjects =EngineerController.getProDetails(user);
									if(appUser.getIsPasswordChange().equals(true)) {
										return ok(views.html.admin.firstchangepassword.render());
									} else {
										flash().put("alert",new Alert("alert-success", "Welcome "+appUser.getFullName()).toString());
										return ok(views.html.engineer.engineerHome.render());
									}
						} else if (bean.getRole().equals("HR") && count == 1) {
								session("AppUserId", ""+ appUser.getId());
								session("AppUserName", "" + appUser.getFullName());
								session("email", appUser.getEmail());
								session("AppUserRole", bean.getRole());
								session("attendanceDate", "0");
								AppUser user = AppUser.find.byId(Long.parseLong(session("AppUserId")));
								List<ProjectDetails> myProjects =EngineerController.getProDetails(user);
									if(appUser.getIsPasswordChange().equals(true)) {
										return ok(views.html.admin.firstchangepassword.render());
									} else {
										flash().put("alert",new Alert("alert-success", "Welcome "+appUser.getFullName()).toString());
										return ok(views.html.engineer.hrHome.render());
									}
						} else {
							flash().put("alert",new Alert("alert-danger", "Invalid credentials, Please try again!").toString());
							return redirect(routes.Application.index());
						}
					} else {
						flash().put("alert",new Alert("alert-danger", "You account has been deactivated. Please contact administrator. ").toString());
						return redirect(routes.Application.index());
					}
				}else {
					flash().put("alert",new Alert("alert-danger", "Invalid credentials, Please try again! ").toString());
					return redirect(routes.Application.index());
				}
			} else {
				flash().put("alert",new Alert("alert-danger", "Invalid credentials, Please try again!").toString());
				return redirect(routes.Application.index());
			}
		} catch (Exception e) {
			e.printStackTrace();
			flash().put("alert",new Alert("alert-danger", "Invalid credentials, Please try again!").toString());
			return ok(views.html.error.render("Login Failed",480));
		}
		
	}
	
	
	/*

	 * social login

	 * @author Rupesh

	 */

	public Result signInUsingGoogle(){
        DynamicForm request = Form.form().bindFromRequest();
        HttpTransport transport = new NetHttpTransport();
        final JsonFactory jsonFactory = new JacksonFactory();
        String token = request.get("idtoken");
        final String client_id = Play.application().configuration().getString("google.clientid");
	    session().remove("AppUserId");
	    session().remove("AppUserRole");
	    try{
	        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory).setAudience(Arrays.asList(client_id)).setIssuer("accounts.google.com").build();
	                // If you retrieved the token on Android using the Play Services 8.3 API or newer, set
	                // the issuer to "https://accounts.google.com". Otherwise, set the issuer to 
	                // "accounts.google.com". If you need to verify tokens from multiple sources, build
	                // a GoogleIdTokenVerifier for each issuer and try them both.
	        // (Receive idTokenString by HTTPS POST)
	        GoogleIdToken idToken = verifier.verify(token);
	        if (idToken != null) {
	            Payload payload = idToken.getPayload();
	            String userId = payload.getSubject();
	            String email = payload.getEmail();
	           // Logger.debug("email>>>"+email);
	            AppUser user = AppUser.find.where().ieq("email",email).findUnique();
	             if(user!=null){
	                        String role = user.role.get(0).role;
	                        session("AppUserId", user.id + "");
	                        session("AppUserRole", role + "");
	                 return getDashBoard(user.role.get(0).role,user);
	             }else{
	                flash().put("alert",new Alert("alert-danger", "Email-id is not authorised. please contact administrator ").toString());
	                //return ok("false");
	            	return redirect(routes.Application.index());
	             }
	        } else {
	            flash().put("alert",new Alert("alert-danger", "Some problem with google ").toString());
	            return redirect(routes.Application.index());
	        }
	    }catch(Exception e){
	        e.printStackTrace();
	       // Logger.info("Exception");
	        //return ok("false");
	        return redirect(routes.Application.index());
	    }
    }
	public Result getDashBoard(final String role,final AppUser appUser){
		List<Role> roles = appUser.getRole();
		int count=0;
		for (Role role1 : roles) {
			if (role.equals(role1.role)) {
				count++;
				break;
			}
		}
		if (role!=null && role.equals("Admin") && count==1) {
				if(appUser.status.equals(UserProjectStatus.Active)) { 
						session("AppUserId", "" + appUser.getId());
						session("AppUserName", "" + appUser.getFullName());
						session("AppUserRole", "Admin");
						session("attendanceDate", "0");
						session("email", appUser.getEmail());
						flash().put("alert",new Alert("alert-success", "Welcome "+appUser.getFullName()).toString());
								return ok(views.html.admin.homePage.render());
							
					} else {
						flash().put("alert",new Alert("alert-danger", "You account has been deactivated. Please contact administrator. ").toString());
						return redirect(routes.Application.index());
					}
		} else if (role!=null && role.equals("Manager") && count==1) {
				if(appUser.status.equals(UserProjectStatus.Active)) {
				session("AppUserId", ""+appUser.getId());
				session("AppUserName", appUser.getFullName());
				session("email", appUser.getEmail());
				session("AppUserRole", "Manager");
				session("attendanceDate", "0");
					flash().put("alert",new Alert("alert-success", "Welcome "+appUser.getFullName()).toString());
					return ok(views.html.manager.managerHome.render());
				} else {
					flash().put("alert",new Alert("alert-danger", "You account has been deactivated. Please contact administrator. ").toString());
					return redirect(routes.Application.index());
				}
		} else if (role!=null && role.equals("Engineer") || role.equals("Marketing") && count==1) {
				if(appUser.status.equals(UserProjectStatus.Active)) {
				session("AppUserId", "" + appUser.getId());
				session("AppUserName", "" + appUser.getFullName());
				session("email", appUser.getEmail());
				session("AppUserRole", role);
				session("attendanceDate", "0");
					flash().put("alert",new Alert("alert-success", "Welcome "+appUser.getFullName()).toString());
					return ok(views.html.engineer.engineerHome.render());
				} else {
					flash().put("alert",new Alert("alert-danger", "You account has been deactivated. Please contact administrator. ").toString());
					return redirect(routes.Application.index());
				}

			} else if (role.equals("HR") && count==1) {
				if(appUser.status.equals(UserProjectStatus.Active)) {
					session("AppUserId", "" + appUser.getId());
					session("AppUserName", "" + appUser.getFullName());
					session("email", appUser.getEmail());
					session("AppUserRole", role);
					session("attendanceDate", "0");
						flash().put("alert",new Alert("alert-success", "Welcome "+appUser.getFullName()).toString());
						return ok(views.html.engineer.hrHome.render());
					} else {
						flash().put("alert",new Alert("alert-danger", "You account has been deactivated. Please contact administrator. ").toString());
						return redirect(routes.Application.index());
					}
			} else {
				flash().put("alert",new Alert("alert-danger", "Invalid credentials, Please try again!").toString());
				return redirect(routes.Application.index());
			}
	}
	
	/* ***************** AppUser Registration Method ************************** */

	public Result registration() {
		AppUser users = Form.form(AppUser.class).bindFromRequest().get();
		Bean bean = Form.form(Bean.class).bindFromRequest().get();
		try {
			List<Role> userRoles = new ArrayList<Role>();
			List<String> roles = bean.getRoles();
			for (String role : roles) {
				Role role1 = Role.find.where().eq("role", role).findUnique();
				if (role1 == null) {
					Role role2 = new Role();
					role2.setRole(role);
					role2.save();
					Role role3 = Role.find.where().eq("role", role).findUnique();
					// Model.Finder(Long.class,Role.class).orderBy("id desc").findIds().get(0);
					userRoles.add(role3);
				} else {
					userRoles.add(role1);
				}
			}
			
			/*if (users.getPassword().equals(bean.password1)) {*/
				AppUser user1 = AppUser.find.where()
						.eq("email", users.getEmail()).findUnique();
				if (user1 == null) {
					AppUser user2 = AppUser.find.where()
							.eq("mobileNo", users.getMobileNo()).findUnique();
					if (user2 == null) {
						AppUser user3 = AppUser.find.where()
								.eq("FullName", users.getFullName())
								.findUnique();
						if (user3 == null) {
							users.setStatus(UserProjectStatus.Active);
							users.setIsPasswordChange(true);
							//users.setPassword(encode(users.getPassword()));
							if (bean.getjDate() != null) {
								SimpleDateFormat sdf = new SimpleDateFormat(
										"dd-MM-yyyy");
								Date jDate = sdf.parse(bean.getjDate());
								users.setJoinedDate(jDate);
								if(!bean.getDobirth().isEmpty()){
									users.setDob(sdf.parse(bean.getDobirth()));
								}
								users.setLoginCheck(true);
							}
							users.setRole(userRoles);
							try {
								MultipartFormData body = request().body().asMultipartFormData();
								FilePart picture = body.getFile("image");
								if (picture != null) {
									String fileName = picture.getFilename();
									String contentType = picture
											.getContentType();
									File file = picture.getFile();
									users.setImage(Files.toByteArray(file));
								} /*else {
									File file = new File("conf/images/thrymr.png");
									if(file != null) {
									users.setImage(Files.toByteArray(file));
									users.setThumbnail(ChatController.scale(Files.toByteArray(file)));
									}
								}*/
							} catch (Exception e) {
								e.printStackTrace();
								AppUser users1 = new AppUser();
								flash().put("alert",new Alert("alert-success", "Please upload Image ").toString());
								return ok(views.html.admin.adduser.render(users));
							}
							users.setPassword(encode("thrymr@123"));
							//users.setSocialId(users.getEmail());
							String email=users.getEmail();
							users.setUserName(email.substring(0,email.indexOf('@')));
							users.save();
							
							//Add leaves 1.5 for New Users and used code don't delete
							
							String thisSYear = new SimpleDateFormat("yyyy").format(new Date());
							final Date thisYear = new SimpleDateFormat("yyyy").parse(thisSYear);
							LeaveType earnedLeave = LeaveType.find.where().eq("leaveType","Earned Leave").findUnique();
							LeaveType CasualLeave = LeaveType.find.where().eq("leaveType","Casual Leave").findUnique();
							LeaveType losspLeave = LeaveType.find.where().eq("leaveType","Loss Of Pay").findUnique();
							LeaveType compLeave = LeaveType.find.where().eq("leaveType","Compensation Off").findUnique();
							
							if(earnedLeave != null && CasualLeave != null){
								Logger.debug("Rigiiiii");
							    Leaves eleaves= new Leaves();
							    Leaves cleaves= new Leaves();
							    
							    Entitlement elEntitlement = new Entitlement();
							    Entitlement clEntitlement = new Entitlement();
							    
							    eleaves.leaveType = earnedLeave;
							    eleaves.leaveStatus = LeaveStatus.NOT_APPLIED;
							    eleaves.appUser = users;
							    
							    cleaves.leaveType = CasualLeave;
							    cleaves.leaveStatus = LeaveStatus.NOT_APPLIED;
							    cleaves.appUser = users;
							    
							    elEntitlement.leaveType = earnedLeave;
							    elEntitlement.appUserList.add(users);
							    
							    clEntitlement.leaveType = CasualLeave;
							    clEntitlement.appUserList.add(users);
								if(users.getJoinedDate().getDate() < 15){
									eleaves.addedLeaves =1.0f;
									eleaves.usedLeaves = 0.0f;
									eleaves.remainingLeaves = 1.0f;
									
									cleaves.addedLeaves =0.5f;
									cleaves.usedLeaves = 0.0f;
									cleaves.remainingLeaves = 0.5f;
									
									elEntitlement.noOfDays = 1.0f;
									clEntitlement.noOfDays = 0.5f;
								}else{
									eleaves.addedLeaves =0.5f;
									eleaves.usedLeaves = 0.0f;
									eleaves.remainingLeaves = 0.5f;
									
									cleaves.addedLeaves =0.0f;
									cleaves.usedLeaves = 0.0f;
									cleaves.remainingLeaves = 0.0f;
									
									elEntitlement.noOfDays = 0.5f;
									clEntitlement.noOfDays = 0.0f;
								}
								eleaves.year = thisYear;
								cleaves.year = thisYear;
								elEntitlement.leavePeriod = thisYear;
								clEntitlement.leavePeriod = thisYear;
								
								eleaves.save();
								cleaves.save();
								
								elEntitlement.save();
								clEntitlement.save();
							}
							
							if(losspLeave != null){
								Leaves lpleaves= new Leaves();
								
								lpleaves.leaveType = losspLeave;
							    lpleaves.leaveStatus = LeaveStatus.NOT_APPLIED;
							    lpleaves.appUser = users;
							    lpleaves.addedLeaves =0.0f;
							    lpleaves.usedLeaves = 0.0f;
							    lpleaves.remainingLeaves = 0.0f;
							    lpleaves.year = thisYear;
							    
							    lpleaves.save();
							    
							    Entitlement lpEntitlement = new Entitlement();
							    lpEntitlement.leaveType = losspLeave;
							    lpEntitlement.leavePeriod = thisYear;
							    lpEntitlement.noOfDays = 0.0f;
							    lpEntitlement.appUserList.add(users);
							    lpEntitlement.save();
							}
							
							if(compLeave != null){
								Leaves comPnleaves= new Leaves();
								
								comPnleaves.leaveType = compLeave;
								comPnleaves.leaveStatus = LeaveStatus.NOT_APPLIED;
								comPnleaves.appUser = users;
								comPnleaves.addedLeaves =0.0f;
								comPnleaves.usedLeaves = 0.0f;
								comPnleaves.remainingLeaves = 0.0f;
							    comPnleaves.year = thisYear;
							    
							    comPnleaves.save();
							    
							    Entitlement compEntitlement = new Entitlement();
							    compEntitlement.leaveType = compLeave;
							    compEntitlement.leavePeriod = thisYear;
							    compEntitlement.noOfDays = 0.0f;
							    compEntitlement.appUserList.add(users);
							    compEntitlement.workedDate = DateUtil.convertDateToString(new Date());
							    compEntitlement.save();
							}
							
							
							//create assign to General chat group
							ChatGroup group = ChatGroup.find.where().eq("name", "General").findUnique();
							if(group != null){
								ChatGroupAppUserInfo groupMember = new ChatGroupAppUserInfo();
								groupMember.appUser = users;
								groupMember.chatGroup = group;
								groupMember.save();
							}
							
							AppUser users1 = new AppUser();
							flash().put("alert",new Alert("alert-success", "Added New User Successfully ").toString());
							//return ok(views.html.admin.adduser.render(users1));
							return redirect(routes.Application.addUser());
						} else {
							flash().put("alert",new Alert("alert-danger", "User FullName already existed").toString());
							return ok(views.html.admin.adduser.render(users));
						}
					} else {
						flash().put("alert",new Alert("alert-danger", "Mobile Number already existed").toString());
						return ok(views.html.admin.adduser.render(users));
					}
				} else {
					flash().put("alert",new Alert("alert-danger", "Email ID already existed").toString());
					return ok(views.html.admin.adduser.render(users));
				}
			/*} else {
				flash().put("alert",new Alert("alert-danger", "passwords not matching . so please try again...").toString());
				return ok(views.html.admin.adduser.render(users));
			}*/
		} catch (Exception e) {
			e.printStackTrace();
			flash().put("alert",new Alert("alert-danger", "Exception occured . so please try again..."+e.getMessage()).toString());
			return ok(views.html.admin.adduser.render(users));
			//return ok(views.html.error.render("",0));
		}
	}

	/* ******************* Password Encryption and Decryption Methods ************* */
	
	private static final String AES = "AES";
	public static String encode(String str){
        SecretKeySpec key = new SecretKeySpec(getCipherKey().getBytes(), AES);
        String encodedStr;
        byte[] b=null;
        try {
            Cipher cipher= Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE,key);
            b=cipher.doFinal(str.getBytes());
        }catch(Exception e){
            e.printStackTrace();
        }
        encodedStr=Base64.encodeBase64URLSafeString(b);
        return encodedStr;
    }    
	
	public static String decode(String token) {
	   SecretKeySpec key = new SecretKeySpec(getCipherKey().getBytes(), AES);
	   byte[] b=null;
	   try {
	       b=Base64.decodeBase64(token.getBytes());
	       Cipher cipher= Cipher.getInstance("AES/ECB/PKCS5Padding");
	       cipher.init(Cipher.DECRYPT_MODE,key);
	       b=cipher.doFinal(b);
	   }catch(Exception e){
	       e.printStackTrace();
	   }
	   return new String(b);
    }
	
	private static String getCipherKey() {
	    return "truweight>!*~`']";
	}
	
	/* ****************** Admin Pages Methods **********************8 */
	
	@BasicAuth
	public Result homePage() {
		if(session("AppUserRole").equals("Admin")) {
			return redirect(routes.AdminController.home());
		} else if(session("AppUserRole").equals("Engineer")) {
			return redirect(routes.EngineerController.home());
		} else if(session("AppUserRole").equals("Manager")){
			return redirect(routes.ManagerController.home());
		} else if(session("AppUserRole").equals("HR")){
			return redirect(routes.EngineerController.HRHome());
		} else {
			return redirect(routes.EngineerController.MarketingHome());
		}
    }
	
	@BasicAuth
	@AdminHRAnnotation
    public Result addUser() {
    	AppUser users1 = new AppUser();
        return ok(views.html.admin.adduser.render(users1));
    }
	
	@BasicAuth
	@AdminHRAnnotation
    public Result allUser() {
        return ok(views.html.admin.alluser.render(AppUser.find.all()));
    }
	
	@BasicAuth
	@AdminAnnotation
    public Result addProject() {
    	ProjectBean projectBean1 = new ProjectBean();
        return ok(views.html.admin.addproject.render(projectBean1));
    }
	
	@BasicAuth
	@AdminAnnotation
    public Result allProject() {
        return ok(views.html.admin.allproject.render(Projects.find.all()));
    }
	//@AdminAnnotation
	@BasicAuth
    public Result userProfile() {
    	long id = Long.parseLong(session("AppUserId"));
    	AppUser appUser = AppUser.find.byId(id);
        return ok(views.html.admin.userprofile.render(appUser));
    }
	
	@BasicAuth
	@AdminAnnotation
    public Result dailyStatus() {
    	List<AppUser> appUsers = AppUser.find.where().eq("status", UserProjectStatus.Active).findList();
        return ok(views.html.admin.dailystatus.render(appUsers));
    }
	
	@BasicAuth
	@AdminAnnotation
	public Result dailyStatusDate() {
		return ok(views.html.admin.dailystatusDate.render());
		
	}
	@BasicAuth
	@AdminAnnotation
    public Result timeSheets() {
		List<AppUser> appUser = AppUser.find.all();
		List<Projects> projects = Projects.find.all();
        return ok(views.html.admin.timesheets.render(appUser,projects));
    } 
	
	@BasicAuth
	@AdminAnnotation
    public Result timeSheetDate() {
        return ok(views.html.admin.timeSheetDate.render());
    } 
	
	//@AdminAnnotation
	@BasicAuth
    public Result changePassword1() {
        return ok(views.html.admin.changepassword.render());
    } 
    
	 public Result AdminmanageTeamMembers() {
		 List <Projects> projects=Projects.find.all();
	        return ok(views.html.admin.manageTeam.render("",projects));
	    }  
	 
    public Result error() {
        return ok(views.html.error.render("",0));
    }  
    
    
    
    /* *************** Forget Password Methods ******************* */
    
    public Result forgetPasswordPage() {
    	return ok(views.html.admin.forgetPasswordEmail.render());
    }
    
    public Result sendMailForgetPassword() {
    	DynamicForm form = Form.form().bindFromRequest();
    	String emailid = form.get("email");
	    try {
	    	AppUser appUser = AppUser.find.where().eq("email", emailid).findUnique();
	    	if(appUser != null ){
	    		Long v = randomInteger();
	    		String uid = encode(String.valueOf(appUser.getId()));
	    		String key = encode(String.valueOf(v));
	    		new SampleDataController().sendMail(appUser.getEmail(),uid,key);
	    		appUser.setReMgnr(String.valueOf(v));
	    		appUser.update();
	    		flash().put("alert",new Alert("alert-success", "Reset Password Link send successfully your Email id "+emailid).toString());
	    		return redirect(routes.Application.forgetPasswordPage());
	    	} else {
	    		flash().put("alert",new Alert("alert-danger", "Email Id does not exist . so please try again...").toString());
	    		return redirect(routes.Application.forgetPasswordPage());
	    	}
	    } catch(Exception e) {
	    	e.printStackTrace();
	    	return ok(views.html.error.render("Bad Request",400));
	    }
    }
    public Result bb8(String id,String key) {
    	
    	Long uid = Long.parseLong(decode(id));
    	try {
	    	AppUser appUser = AppUser.find.byId(uid);
	    	if(appUser != null && appUser.getReMgnr().equals(decode(key))){ 
	    		appUser.setReMgnr("");
	    		appUser.update();
	    	return ok(views.html.admin.forgetPassword.render(appUser.getEmail()));
	    	} else {
	    		return ok(views.html.error.render("Request Timeout",408));
	    	}
    	} catch(Exception e) {
    		e.printStackTrace();
    		return ok(views.html.error.render("Request Timeout",408));
    	}
    }
    
    public Result forgetPassword() {
    	DynamicForm form = Form.form().bindFromRequest();
    	String emailid = form.get("email");
    	String npwd = form.get("npwd");
    	String rnpwd = form.get("rnpwd");
    	try {
	    	AppUser appUser = AppUser.find.where().eq("email", emailid).findUnique();
	    	if(!npwd.equals(decode(appUser.password))) {
		    	if(npwd.equals(rnpwd)) {
		    		//Logger.debug("paaa"+npwd);
		    		appUser.setPassword(encode(npwd));
		    		appUser.update();
		    		flash().put("alert",new Alert("alert-success", "Password Successfully Updated ").toString());
		    		return redirect(routes.Application.index());
		    	} else {
		    		flash().put("alert",new Alert("alert-danger", "New Password & Re-Type New Password do not match! ").toString());
		    		return ok(views.html.admin.forgetPassword.render(emailid));
		    	}
	    	} else {
		    	flash().put("alert",new Alert("alert-danger", "New Password & Old Password both are same!! ").toString());
	    		return ok(views.html.admin.forgetPassword.render(emailid));
		    }
    	} catch(Exception e) {
    		e.printStackTrace();
    		return ok(views.html.error.render("Bad Request",400));
    	}
    	
    }
    
    public static Long randomInteger() {
    	Random rand = new Random();
        // nextInt excludes the top value so we have to add 1 to include the top value
        Long randomNum = (long) (rand.nextInt((999 - 1) + 1) + 1);
       // Logger.debug(""+randomNum);
        return randomNum;
    }
    
    public Result isExistEmailForgetPassword() {
    	DynamicForm requestForm = Form.form().bindFromRequest();
    	Map<String, Boolean> map = new HashMap<String, Boolean>();
    	try{
	        map.put("valid", ((AppUser.find.where().ieq("email", requestForm.get("email").trim()).findList().size()) > 0));
    	}catch(Exception e){
            map.put("valid",false);
        }
    	return ok(Json.toJson(map));
    }
    
    
    /*  **************** Form validation Methods ************************ */
   
    public Result isPDFfile() {
    	DynamicForm requestForm = Form.form().bindFromRequest();
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        String pdf = requestForm.get("file");
        pdf = pdf.substring(pdf.indexOf("."),pdf.length());
        try{
        	if (pdf.equalsIgnoreCase(".pdf")) {
                map.put("valid", true);
            } else {
            	 map.put("valid",false);
            }
        }catch(Exception e){
            map.put("valid",false);
        }
        return ok(Json.toJson(map));
    }
    
    public Result isExistUserName() {
    	DynamicForm requestForm = Form.form().bindFromRequest();
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        String id = requestForm.get("id");
        try{
            if(id != null && !id.isEmpty()) {
                map.put("valid", !(AppUser.find.where().ieq("FullName", requestForm.get("FullName").trim()).ne("id", Long.parseLong(id)).findRowCount() > 0));
            } else {
            	map.put("valid", !((AppUser.find.where().ieq("FullName", requestForm.get("FullName").trim()).findList().size()) > 0));
            }
        }catch(Exception e){
            map.put("valid",false);
        }
        return ok(Json.toJson(map));
    }
    
    public Result isExistPolicyName() {
    	DynamicForm requestForm = Form.form().bindFromRequest();
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        String id = requestForm.get("id");
        try{
            if(id != null && !id.isEmpty()) {
                map.put("valid", !(Policy.find.where().ieq("policyName", requestForm.get("policyName").trim()).ne("id", Long.parseLong(id)).findRowCount() > 0));
            } else {
            	map.put("valid", !((Policy.find.where().ieq("policyName", requestForm.get("policyName").trim()).findList().size()) > 0));
            }
        }catch(Exception e){
            map.put("valid",false);
        }
        return ok(Json.toJson(map));
    }
    
    public Result isExistholidayDate() {
    	DynamicForm requestForm = Form.form().bindFromRequest();
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        String id = requestForm.get("id");
        try{
        	
        	String hdayDate = requestForm.get("holidayDate").trim();
        	Date holidayDate = new SimpleDateFormat("dd-MM-yyyy").parse(hdayDate);
            if(id != null && !id.isEmpty()) {
                map.put("valid", !(Holidays.find.where().eq("holidayDate", holidayDate).ne("id", Long.parseLong(id)).findRowCount() > 0));
            } else {
            	map.put("valid", !((Holidays.find.where().eq("holidayDate", holidayDate).findList().size()) > 0));
            }
        }catch(Exception e){
            map.put("valid",false);
        }
        return ok(Json.toJson(map));
    }
    
    
    public Result isExistcompensatoryDay() {
    	DynamicForm requestForm = Form.form().bindFromRequest();
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        String id = requestForm.get("id");
        try{
        	
        	String hdayDate = requestForm.get("compensatoryDay").trim();
        	Date compensatoryDay = new SimpleDateFormat("dd-MM-yyyy").parse(hdayDate);
            if(id != null && !id.isEmpty()) {
                map.put("valid", !(Holidays.find.where().eq("correspondingWorkingDay", compensatoryDay).ne("id", Long.parseLong(id)).findRowCount() > 0));
            } else {
            	map.put("valid", !((Holidays.find.where().eq("correspondingWorkingDay", compensatoryDay).findList().size()) > 0));
            }
        }catch(Exception e){
            map.put("valid",false);
        }
        return ok(Json.toJson(map));
    }
    
    public Result isExistleaveType() {
    	DynamicForm requestForm = Form.form().bindFromRequest();
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        String id = requestForm.get("id");
        try{
            if(id != null && !id.isEmpty()) {
                map.put("valid", !(LeaveType.find.where().ieq("leaveType", requestForm.get("leaveType").trim()).ne("id", Long.parseLong(id)).findRowCount() > 0));
            } else {
            	map.put("valid", !((LeaveType.find.where().ieq("leaveType", requestForm.get("leaveType").trim()).findList().size()) > 0));
            }
        }catch(Exception e){
            map.put("valid",false);
        }
        return ok(Json.toJson(map));
    }
    
    public Result isExistUserMno() {
    	DynamicForm requestForm = Form.form().bindFromRequest();
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        String id = requestForm.get("id");
        String phNoStr = requestForm.get("mobileNo").trim();
        try{
            if(id != null && !id.isEmpty()) {
                map.put("valid", !(AppUser.find.where().eq("mobileNo", Long.parseLong(phNoStr)).ne("id", Long.parseLong(id)).findRowCount() > 0));
            } else if(phNoStr != null && !phNoStr.isEmpty()){
            	map.put("valid", !((AppUser.find.where().eq("mobileNo", Long.parseLong(phNoStr)).findList().size()) > 0));
            } else {
            	map.put("valid", false);
            }
        }catch(Exception e){
            map.put("valid",false);
        }
        return ok(Json.toJson(map));
    }
    
    public Result isExistUserEmailId() {
    	DynamicForm requestForm = Form.form().bindFromRequest();
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        String id = requestForm.get("id");
        try{
            if(id != null && !id.isEmpty()) {
                map.put("valid", !(AppUser.find.where().ieq("email", requestForm.get("email").trim()).ne("id", Long.parseLong(id)).findRowCount() > 0));
            } else {
            	map.put("valid", !((AppUser.find.where().ieq("email", requestForm.get("email").trim()).findList().size()) > 0));
            }
        }catch(Exception e){
            map.put("valid",false);
        }
        return ok(Json.toJson(map));
    }
    
    public Result isExistEsslId()
    {
    	DynamicForm requestForm = Form.form().bindFromRequest();
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        String id = requestForm.get("id");
        String phNoStr = requestForm.get("esslId").trim();
        try{
            if(id != null && !id.isEmpty()) {
                map.put("valid", !(AppUser.find.where().eq("esslId", Long.parseLong(phNoStr)).ne("id", Long.parseLong(id)).findRowCount() > 0));
            } else if(phNoStr != null && !phNoStr.isEmpty()){
            	map.put("valid", !((AppUser.find.where().eq("esslId", Long.parseLong(phNoStr)).findList().size()) > 0));
            } else {
            	map.put("valid", false);
            }
        }
        catch(NumberFormatException e1)
        {
        	  map.put("valid",true);
        }
        catch(Exception e){
            map.put("valid",false);
        }
        return ok(Json.toJson(map));
    }
    
    public Result isExistProjectName() {
    	DynamicForm requestForm = Form.form().bindFromRequest();
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        String id = requestForm.get("id");
        try{
            if(id != null && !id.isEmpty()) {
                map.put("valid", !(Projects.find.where().ieq("projectName", requestForm.get("projectName").trim()).ne("id", Long.parseLong(id)).findRowCount() > 0));
            } else {
            	map.put("valid", !((Projects.find.where().ieq("projectName", requestForm.get("projectName").trim()).findList().size()) > 0));
            }
        }catch(Exception e){
            map.put("valid",false);
        }
        return ok(Json.toJson(map));
    }
    
    public Result isExistIncidentType() {
    	DynamicForm requestForm = Form.form().bindFromRequest();
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        String id = requestForm.get("id");
        try{
            if(id != null && !id.isEmpty()) {
                map.put("valid", !(CIRType.find.where().ieq("IncidentType", requestForm.get("IncidentType").trim()).ne("id", Long.parseLong(id)).findRowCount() > 0));
            } else {
            	map.put("valid", !((CIRType.find.where().ieq("IncidentType", requestForm.get("IncidentType").trim()).findList().size()) > 0));
            }
        }catch(Exception e){
            map.put("valid",false);
        }
        return ok(Json.toJson(map));
    }
    
    public Result isExistCandidateEmail() {
    	DynamicForm requestForm = Form.form().bindFromRequest();
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        String id = requestForm.get("id");
        try{
            if(id != null && !id.isEmpty()) {
                map.put("valid", !(RecruitmentReference.find.where().ieq("candidateEmail", requestForm.get("candidateEmail").trim()).ne("id", Long.parseLong(id)).findRowCount() > 0));
            } else {
            	map.put("valid", !((RecruitmentReference.find.where().ieq("candidateEmail", requestForm.get("candidateEmail").trim()).findList().size()) > 0));
            }
        }catch(Exception e){
            map.put("valid",false);
        }
        return ok(Json.toJson(map));
    }
    
    @BasicAuth
    public Result getDynamicDashboard(String role,Long appUserId){
    	AppUser appUser = AppUser.find.byId(appUserId);
    	if(role.equals("Admin")){
    		session("AppUserRole", "Admin");
    		return redirect(routes.AdminController.home());
    	}else if(role.equals("Manager")){
    		session("AppUserRole", "Manager");
    		return redirect(routes.ManagerController.home());
    	}else if(role.equals("Engineer")){
    		session("AppUserRole", "Engineer");
    		return redirect(routes.EngineerController.home());
    	}else if(role.equals("HR")){
    		session("AppUserRole", "HR");
    		return redirect(routes.EngineerController.HRHome());
    	}else{
    		session("AppUserRole", "Marketing");
    		return redirect(routes.EngineerController.MarketingHome());
    	}
    	
    }
   
}



