package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.google.common.io.Files;

import models.AppUser;
import models.Attendance;
import models.AttendenceStatus;
import models.DailyReport;
import models.Gender;
import models.ProbationPeriod;
import models.Role;
import models.UserProjectStatus;
import models.UsersDailyReport;
import models.chat.UploadFileInfo;
import models.incident.Incident;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import utils.Constants;

public class SampleDataController extends Controller{
	
	
	public Result remvoeThrymrIamges(){
		Long[] thrymrImages = {7l,12l,18l,19l,22l,23l,31l,36l,44l,46l,52l,53l,54l,56l,58l,66l,70l,71l,73l,74l,75l,76l,77l,79l,80l,81l,82l,83l,84l,87l};
	//	List<AppUser> appUserList = AppUser.find.where().in("id",thrymrImages).findList();
		List<AppUser> appUserList = AppUser.find.all();
		//List<AppUser> appUserList = Ebean.find(AppUser.class).fetch("id","thumbnail") .findList();
		for (AppUser appUser : appUserList) {
			synchronized(appUser){
				//Logger.info(appUser.getThumbnail()+"	appUser.before()");
				appUser.setThumbnail(null);
			//	Logger.info(appUser.getThumbnail()+"	appUser.getThumbnail()");
				appUser.update();
			}
		}
		return ok("updating data..");
 }
	public Result updateUploadFileInfo(){
			List<UploadFileInfo> uploadFileInfoList = UploadFileInfo.find.all();
			for (UploadFileInfo uploadFileInfo : uploadFileInfoList) {
				synchronized(uploadFileInfo){
					uploadFileInfo.fileSize = String.valueOf(uploadFileInfo.uploadImage.length);
					//uploadFileInfo.reSizeImage  = 
					uploadFileInfo.update();
					//Logger.info("uploadfile info success fully updated...");
				}
			}
			return ok("updating data..");
	}
	
	public Result parseCSV(){
		try {
			Reader in = new FileReader("./thrymr.csv");
			Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(in);
			Role engineer= Role.find.byId(3l);
			int count=1;
			for (CSVRecord record : records) {
			    String email = record.get(0);
			    String firstName = record.get(1);
			    String lastName = record.get(2);
			    
			    AppUser appUser = new AppUser();
			    appUser.setFullName(firstName+" "+lastName);
			    appUser.setEmail(email);
			    appUser.setPassword(Application.encode("thrymr@123"));
			    appUser.setMobileNo(count);
			    appUser.setStatus(UserProjectStatus.Active);
			    appUser.setGender(Gender.Male);
			    appUser.getRole().add(engineer);
			    appUser.save();
			    count++;
			    //Logger.info(appUser.toString());
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ok("done completing the parsing and uploading the data");
	}
	
	public static class SMTPAuthenticator extends javax.mail.Authenticator {
		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(Constants.EMAIL_USERNAME,
					Constants.EMAIL_PASSWORD);
		}
	}
	//Send mail after fill daily status
	public  void sendMail(String email,String id,String key) {
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
			msg.setSubject("Forget Password in BB8 Thrymr Software");
			msg.setFrom(new InternetAddress(Constants.EMAIL_USERNAME));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
					email));
			// msg text

			// Create the message part
			// BodyPart messageBodyPart = new MimeBodyPart();

			// Now set the actual message
			
			//msg.setText("please click Reset Password link :  http://10.2.193.200:9000/bb8/"+id+"/"+key+"");
			msg.setText("please click Reset Password link :  https://developer.thrymr.net/bb8/"+id+"/"+key+"");

			// Create a multipar message

			// Send the complete message parts
			// msg.setContent(messageBodyPart);

			Transport.send(msg);
		}// try
		catch (Exception ex) {
			ex.printStackTrace();
		}// catch

	}
	public Result  addBb8BotUser(){
		try{
			AppUser admin = AppUser.find.where().ieq("email", "bb8bot@thrymr.net").findUnique();
			if (admin == null) {
				AppUser admin1 = new AppUser();
				admin1.setEmail("bb8bot@thrymr.net");
				admin1.setPassword(Application.encode("github"));
				admin1.setFullName("bb8bot");
				File file = new File("conf/images/thrymr.png");
				try {
					admin1.setImage(Files.toByteArray(file));
					admin1.setThumbnail(ChatController.scale(Files.toByteArray(file)));
				} catch (IOException e) {
					e.printStackTrace();
				}
				admin1.setUserName("bb8bot");
				admin1.setMobileNo(9876543210l);
				admin1.setGender(Gender.Male);
				admin1.setGitId("bb8bot@github");
				Role roles = Role.find.where().eq("role", "Engineer").findUnique();
				List<Role> lRole = new ArrayList<Role>();
				lRole.add(roles);
				admin1.setRole(lRole);
				admin1.setStatus(UserProjectStatus.Active);
				admin1.setIsPasswordChange(true);
				admin1.setLoginCheck(true);
	
				admin1.save();
			//	Logger.debug("savbesddd");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ok("addBb8BotUser");
		
	}
	
	
	public static void sendMailNotFilled(){
		List<AppUser> appUsers = new ArrayList<AppUser>();
		List<AppUser> appUserDailyReport = new ArrayList<AppUser>();
		List<AppUser> adminUsers = new ArrayList<AppUser>();
		try {
			appUsers = AppUser.find.where().eq("status",UserProjectStatus.Active).findList();
			for(AppUser appUser : appUsers){
				DailyReport dailyReport = DailyReport.find.where().eq("app_user_id", appUser.getId()).findUnique();
				if(dailyReport != null){
					UsersDailyReport udailyReport = UsersDailyReport.find.where().eq("daily_report_id", dailyReport.getId()).eq("date", EngineerController.getTodayDate(new Date())).findUnique();
					if(udailyReport == null){
						Attendance attendance = Attendance.find.where().eq("app_user_id",appUser.getId()).eq("status", AttendenceStatus.Absent).eq("date", EngineerController.getTodayDate(new Date())).findUnique();
						if(attendance == null){
							appUserDailyReport.add(appUser);
						}
					}
				} else {
					appUserDailyReport.add(appUser);
				}
			}
			Role roleAdmin = Role.find.where().eq("role", "Admin").findUnique();
			adminUsers.addAll(AppUser.find.where().in("role", roleAdmin).findList());
    		long i = 1;
			for(AppUser appUser : appUserDailyReport){
				synchronized (appUser) {
					//Logger.debug(appUser.getFullName() +" --> "+ i++);
					sendMail(appUser,adminUsers);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	// sending mail after fill daily status
	public static void sendMail(AppUser appUser, List<AppUser> admins) {
		List<AppUser> adminUsers = new ArrayList<AppUser>();
		adminUsers.addAll(admins);
		// mail properties outgoing server (gmail.com)
		try {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "465");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
	
			Authenticator auth = new SMTPAuthenticator();
			Session session = Session.getInstance(props, auth);
			MimeMessage msg = new MimeMessage(session);
			msg.setSubject("Daily Status Not Filled");
			msg.setFrom(new InternetAddress(Constants.EMAIL_FROM));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
					appUser.getEmail()));
			
			if(adminUsers.contains(appUser)){
				adminUsers.remove(appUser);
			}
			
			InternetAddress[] myCcList;
			Long id = appUser.getReportMangerId();
			if(id != 0){
				myCcList = new InternetAddress[(adminUsers.size())+1];
			}else{
				myCcList = new InternetAddress[adminUsers.size()];
			}
			
			for(int i=0;i<adminUsers.size();i++){
				myCcList[i] = new InternetAddress(adminUsers.get(i).getEmail());
			}
			if(id != 0){
				AppUser reportManager = AppUser.find.byId(id);
				if(reportManager.getStatus().equals(UserProjectStatus.Active)){
					myCcList[myCcList.length-1] = new InternetAddress(reportManager.getEmail());
				}
			}
			msg.setRecipients(Message.RecipientType.CC, myCcList);
			
			MimeBodyPart messageBodyPart1 = new MimeBodyPart();
			StringBuilder textMessage = new StringBuilder();
			textMessage.append("<html><Body>Hi "+appUser.getFullName()+",<br><br>");
			textMessage.append("You missed to fill your daily status today.");
			textMessage.append("<br><br>Thanks,");
			textMessage.append("<br>BB8 Team");
			textMessage.append("</Body></html>");
			messageBodyPart1.setContent(textMessage.toString(), "text/html");
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart1);
			msg.setContent(multipart);
			Transport.send(msg);
		}// try
		catch (Exception ex) {
			ex.printStackTrace();
		}// catch

	}
	
	public static void sendIncidentMail(AppUser appUser, List<AppUser> admins,String imageFileName,Incident incident) {
		List<AppUser> adminUsers = new ArrayList<AppUser>();
		adminUsers.addAll(admins);
		// mail properties outgoing server (gmail.com)
		try {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "465");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
	
			Authenticator auth = new SMTPAuthenticator();
			Session session = Session.getInstance(props, auth);
			MimeMessage msg = new MimeMessage(session);
			msg.setSubject("Critical Incident");
			msg.setFrom(new InternetAddress(Constants.EMAIL_FROM));
			//msg.addRecipient(Message.RecipientType.TO, new InternetAddress(appUser.getEmail()));
			
			if(adminUsers.contains(appUser)){
				adminUsers.remove(appUser);
			}
			
			InternetAddress[] myCcList = new InternetAddress[adminUsers.size()];
			for(int i=0;i<adminUsers.size();i++){
					myCcList[i] = new InternetAddress(adminUsers.get(i).getEmail());
			}
			msg.setRecipients(Message.RecipientType.TO, myCcList);
			
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			StringBuilder textMessage = new StringBuilder();
			textMessage.append("<html><Body>Hi,<br><br>");
			textMessage.append("A new Critical Incident has been reported by "+appUser.getFullName()+"<br>");
			textMessage.append("Incident Related to : "+incident.incidentName+"<br>");
			textMessage.append("Description         : "+incident.description);
			textMessage.append("<br><br>Thanks,");
			textMessage.append("<br>BB8 Team");
			textMessage.append("</Body></html>");
			messageBodyPart.setContent(textMessage.toString(), "text/html");
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			
			if(imageFileName != null){
				MimeBodyPart attachPart = new MimeBodyPart();
				String attachFile = imageFileName;
				 
				DataSource source = new FileDataSource(attachFile);
				attachPart.setDataHandler(new DataHandler(source));
				attachPart.setFileName(new File(attachFile).getName());
				multipart.addBodyPart(attachPart);
			}
			msg.setContent(multipart);
			Transport.send(msg);
		}// try
		catch (Exception ex) {
			ex.printStackTrace();
		}// catch

	}
	
//	public Result sendMails(){
//		sendMailProbationPeriodCompletion6MonthsEmp();
//		return ok("Done");
//	}
	
	
	public static void sendMailProbationPeriodCompletion6MonthsEmps() {
		
		try {
			
			Calendar joiningDate = Calendar.getInstance();
			joiningDate.add(Calendar.MONTH, -6);
			joiningDate.add(Calendar.DATE, 14);

			Calendar joiningDate3 = Calendar.getInstance();
			joiningDate3.add(Calendar.MONTH, -3);
			joiningDate3.add(Calendar.DATE, 14);
			
			Calendar periodEndDate = Calendar.getInstance();
			periodEndDate.add(Calendar.DATE, 14);

			//Logger.debug("JD 6 : "+joiningDate.getTime()+" JD 3"+joiningDate3.getTime());
			
			List<AppUser> appUsers = AppUser.find.where()
					.eq("joinedDate", EngineerController.getTodayDate(joiningDate.getTime()))
					.eq("status", UserProjectStatus.Active).eq("experience", ProbationPeriod.No).findList();
			
			List<AppUser> appUsers3 = AppUser.find.where()
					.eq("joinedDate", EngineerController.getTodayDate(joiningDate3.getTime()))
					.eq("status", UserProjectStatus.Active).eq("experience", ProbationPeriod.Yes).findList();

			appUsers.addAll(appUsers3);
			if (appUsers != null && !appUsers.isEmpty()) {

//				Set<AppUser> setUsers = new HashSet<AppUser>();
//				List<Role> listRole = new ArrayList<Role>();
//				Role roleAdmin = Role.find.where().eq("role", "Admin").findUnique();
//				Role roleHR = Role.find.where().eq("role", "HR").findUnique();
//				listRole.add(roleAdmin);
//				listRole.add(roleHR);
//				setUsers.addAll(
//						AppUser.find.where().in("role", listRole).eq("status", UserProjectStatus.Active).findList());
//
//				
//				List<AppUser> adminUsers = new ArrayList<AppUser>();
//				adminUsers.addAll(setUsers);
				
//				List<String> adminUsers = new ArrayList<String>();
//				adminUsers.add("rishi@thrymr.net");
//				adminUsers.add("buta@thrymr.net");
//				adminUsers.add("sonia@thrymr.net");
//				adminUsers.add("sudha@thrymr.net");



				// mail properties outgoing server (gmail.com)
				try {
					Properties props = new Properties();
					props.put("mail.smtp.host", "smtp.gmail.com");
					props.put("mail.smtp.port", "465");
					props.put("mail.smtp.auth", "true");
					props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

					Authenticator auth = new SMTPAuthenticator();
					Session session = Session.getInstance(props, auth);
					MimeMessage msg = new MimeMessage(session);
					msg.setSubject("Probation period completion alert!");
					msg.setFrom(new InternetAddress(Constants.EMAIL_FROM));
					// msg.addRecipient(Message.RecipientType.TO, new
					// InternetAddress(appUser.getEmail()));

//					InternetAddress[] myCcList = new InternetAddress[adminUsers.size()];
//					 for(int i=0;i<adminUsers.size();i++){
//						 myCcList[i] = new InternetAddress(adminUsers.get(i));
//					 }
//					 msg.setRecipients(Message.RecipientType.TO, myCcList);

//					msg.setRecipients(Message.RecipientType.TO, "sureshnamburi@thrymr.net,suresh.namburi3@gmail.com");
					
					msg.setRecipients(Message.RecipientType.TO, "rishi@thrymr.net,buta@thrymr.net,sonia@thrymr.net,sudha@thrymr.net");

					MimeBodyPart messageBodyPart = new MimeBodyPart();
					StringBuilder textMessage = new StringBuilder();
					textMessage.append("<html><Body>Hi,<br><br>");
					textMessage.append(
							"Please note that following are the employees approaching the end of their probation period. <br><br><br>");
					textMessage.append(
							"<table width=50%; rules=all style=border:1px solid #3A5896; cellpadding=10><thead><tr>"
							+ "<th align=center>Employee Id</th>"
							+ "<th align=center>Employee Name</th>"
							+ "<th align=center>Joining Date</th>"
							+ "<th align=center>Date of completion</th>"
							+ "<th align=center>Probation Period</th>"
							+ "</tr></thead>");
					textMessage.append("<tbody>");
					for (AppUser appUser : appUsers) {
						int pp = appUser.experience.equals(ProbationPeriod.No)? 6:3;
						textMessage.append("<tr><td align=center>" + appUser.getEmployeeId() + "</td><td align=center>"
								+ appUser.getFullName() + "</td><td align=center>"
								+ new SimpleDateFormat("dd-MM-yyyy").format(appUser.getJoinedDate())
								+ "</td><td align=center>"
								+ new SimpleDateFormat("dd-MM-yyyy").format(periodEndDate.getTime()) + "</td>"
										+ "</td><td align=center>"
										+ pp + " Months </td>"
										+ "</tr>");
					}
					textMessage.append("</tbody></table><br><br>");
					textMessage.append("<br><br>Thanks,");
					textMessage.append("<br>BB8 Team");
					textMessage.append("</Body></html>");
					messageBodyPart.setContent(textMessage.toString(), "text/html");
					Multipart multipart = new MimeMultipart();
					multipart.addBodyPart(messageBodyPart);
					msg.setContent(multipart);
					Transport.send(msg);
				} // try
				catch (Exception ex) {
					ex.printStackTrace();
				} // catch

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
