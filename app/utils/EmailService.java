package utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
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

import controllers.Application;
import controllers.SampleDataController.SMTPAuthenticator;
import models.AppUser;
import models.Role;
import models.UserProjectStatus;
import play.Logger;
import play.Play;

public class EmailService {
	final private static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	final private static String from = Constants.EMAIL_FROM;
	final private static String username = Constants.EMAIL_USERNAME;
	final private static String password = Constants.EMAIL_PASSWORD;
	final private static String pdf = "&output=pdf";
	public static final String JASPER_SERVER_LINK = Play.application().configuration().getString("jasper.serverlink");
	public static final String urlCommonPart = JASPER_SERVER_LINK + ConstructReportUrl.USERNAME
			+ ConstructReportUrl.AMPERSAND + ConstructReportUrl.PASSWORD;

	public static boolean sendVerificationMail(final String email, final String htmlContent, final String subject) {
		FileOutputStream fos = null;
		String storedFileName = null;
		ByteArrayInputStream stream = null;
		URL urlNew = null;
		File file = null;
		InputStream in = null;
		String fileName = null;

		//Logger.debug("urlCommonPart " + urlCommonPart);

		try {
			Properties props = new Properties();
			props.put("mail.smtp.host", Constants.EMAIL_HOST);
			props.put("mail.smtp.port", Constants.EMAIL_PORT);
			props.put("mail.smtp.auth", Constants.EMAIL_AUTH);
			props.setProperty("java.net.preferIPv4Stack", "true");
			props.setProperty("java.net.preferIPv6Addresses", "true");
			props.put("mail.smtp.starttls.enable", Constants.EMAIL_STARTTLS_ENABLE);
			Authenticator auth = new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			};
			Session session = Session.getInstance(props, auth);
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
			message.setSubject(subject);
			BodyPart messageBodyPart = new MimeBodyPart();
			BodyPart messageBodyPart2 = new MimeBodyPart();
			messageBodyPart.setContent(htmlContent, "text/plain");
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			if (subject.equals("Request For Leave Approval !!")) {
				Long id = Application.getLoggedInUser().getId();
				// String url = ConstructReportUrl.urlCommonPart();
				// http://158.85.160.189:8080/jasperserver/flow.html?_flowId=viewReportFlow&standAlone=true&_flowId=viewReportFlow&ParentFolderUri=%2F
				// String url1 =
				// "reports%2FBB8_Reports&reportUnit=%2Freports%2FBB8_Reports%2FLeave_History_Report&j_username=jasperadmin&j_password=jasperadmin&output=pdf&id="+id;
				String url1 = urlCommonPart + ConstructReportUrl.constructFilterURL("Leave_History_Report",
						new SimpleDateFormat("YYYY").format(new Date()), id) + pdf;
				urlNew = new URL(url1);
				in = urlNew.openStream();
				fileName = "LHistory" + new SimpleDateFormat("YYYY").format(new Date()) + ".pdf";
				storedFileName = "LHistory";
				file = new File("/tmp/" + storedFileName + fileName);
				fos = new FileOutputStream(file);
				int length = -1;
				byte[] buffer = new byte[4096];
				while ((length = in.read(buffer)) > -1) {
					fos.write(buffer, 0, length);
				}
				fos.close();
				in.close();
				deletionFilesFromTmp(storedFileName, fileName, file);
				DataSource source = new FileDataSource(file);
				messageBodyPart2.setDataHandler(new DataHandler(source));
				messageBodyPart2.setFileName(fileName);
				multipart.addBodyPart(messageBodyPart2);
			}
			message.setContent(multipart);
			Transport.send(message);
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	// For Reports Display EMP:32
	public static void engineerSummaryReportPdf(String reportType) {
		String sDate = null;
		String eDate = null;
		String url = null;
		FileOutputStream fos = null;
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		List<Date> dates = getWeekDatesList();

		// List<Role> listRole = new ArrayList<Role>();
		// Role roleManager = Role.find.where().eq("role",
		// "Manager").findUnique();
		// Role roleEngineer = Role.find.where().eq("role",
		// "Engineer").findUnique();
		// listRole.add(roleEngineer);
		// listRole.add(roleManager);
		// List<AppUser> engineersList = AppUser.find.where().eq("role",
		// listRole).eq("status", UserProjectStatus.Active).findList();

		List<AppUser> engineersList = Role.find.where().eq("role", "Engineer").findUnique().appUser;
		if (dates.size() > 0) {
			eDate = dateFormat.format(dates.get(0));
			sDate = dateFormat.format(dates.get(dates.size() - 1));
		}
		// Logger.info("start Date"+sDate+"End Date is"+eDate);

		if (reportType.equalsIgnoreCase("companyDailyStatus")) {
			// Logger.info("reportType"+reportType);
			url = urlCommonPart + ConstructReportUrl.constructFilterURL("bb8_adminreport", null) + pdf;
			// String url1 = ConstructReportUrl.urlCommonPart();

			// url
			// =url1+"reports%2FBB8_Reports&reportUnit=%2Freports%2FBB8_Reports%2Fbb8_adminreport&j_username=jasperadmin&j_password=jasperadmin&output=pdf";
			email(url, sDate, eDate, reportType, "", 0L);
		} else if (reportType.equalsIgnoreCase("employeeWeeklySummary")) {
			for (int i = 0; i < engineersList.size(); i++) {
				if (engineersList.get(i).getStatus().equals(UserProjectStatus.Active)) {
					Long id = engineersList.get(i).id;
					// String url1 = ConstructReportUrl.urlCommonPart();

					// url =url1+
					// "reports%2FBB8_Reports&reportUnit=%2Freports%2FBB8_Reports%2FEmployee_Weekly_Status_Report&from="+sDate+"&to="+eDate+"&id="+id+"&j_username=jasperadmin&j_password=jasperadmin&output=pdf";
					url = urlCommonPart
							+ ConstructReportUrl.constructFilterURL("Employee_Weekly_Status_Report", sDate, eDate, id)
							+ pdf;
					email(url, sDate, eDate, reportType, engineersList.get(i).getEmail(), id);

				}
			}
			;

		} else if (reportType.equalsIgnoreCase("companyweeklyReport")) {
			// String url1 = ConstructReportUrl.urlCommonPart();

			// url
			// =url1+"reports%2FBB8_Reports&reportUnit=%2Freports%2FBB8_Reports%2FCompanyWeeklyAttendanceReport&from="+sDate+"&to="+eDate+"&j_username=jasperadmin&j_password=jasperadmin&output=pdf";
			url = urlCommonPart + ConstructReportUrl.constructFilterURL("CompanyWeeklyAttendanceReport", sDate, eDate)
					+ pdf;
			email(url, sDate, eDate, reportType, "", 0L);
		} else if (reportType.equalsIgnoreCase("employeedailystatus")) {
			sDate = dateFormat.format(new Date());
			// Logger.info("Employee Daily Status start date"+sDate);
			for (int i = 0; i < engineersList.size(); i++) {
				if (engineersList.get(i).getStatus().equals(UserProjectStatus.Active)) {
					Long id = engineersList.get(i).id;
					// String url1 = ConstructReportUrl.urlCommonPart();

					// url
					// =url1+"reports%2FBB8_Reports&reportUnit=%2Freports%2FBB8_Reports%2FEmployee_Daily_Status_Report&id="+id+"&Date="+sDate+"&j_username=jasperadmin&j_password=jasperadmin&output=pdf";
					url = urlCommonPart
							+ ConstructReportUrl.constructFilterURL("Employee_Daily_Status_Report", sDate, id) + pdf;
					email(url, sDate, eDate, reportType, engineersList.get(i).getEmail(), id);
				}
			}
		} else if (reportType.equalsIgnoreCase("employeeMonthlySummary")) {
			Date startDate = null;
			startDate = getFirstDateOfLastMonth();
			sDate = dateFormat.format(startDate);
			eDate = dateFormat.format(getLastDateOfLastMonth());

			for (int i = 0; i < engineersList.size(); i++) {
				if (engineersList.get(i).getStatus().equals(UserProjectStatus.Active)) {
					Long id = engineersList.get(i).id;
					// String url1 = ConstructReportUrl.urlCommonPart();

					// url
					// =url1+"reports%2FBB8_Reports&reportUnit=%2Freports%2FBB8_Reports%2FEmployee_Monthly_Status_Report&id="+id+"&from="+sDate+"&to="+eDate+"&j_username=jasperadmin&j_password=jasperadmin&output=pdf";
					url = urlCommonPart
							+ ConstructReportUrl.constructFilterURL("Employee_Monthly_Status_Report", sDate, eDate, id)
							+ pdf;
					email(url, sDate, eDate, reportType, engineersList.get(i).getEmail(), id);
				}
			}
		}

	}

	// Email Authentication Properties..
	private static void email(String url, String sDate, String eDate, String reportType, String email, Long id) {
		FileOutputStream fos = null;
		String storedFileName = null;
		ByteArrayInputStream stream = null;
		URL urlNew = null;
		File file = null;
		InputStream in = null;
		String fileName = null;
		try {
			urlNew = new URL(url);
			in = urlNew.openStream();

			if (reportType.equalsIgnoreCase("companyweeklyReport")) {
				fileName = sdf.format(new Date()) + ".pdf";
				storedFileName = "company";
			} else if (reportType.equalsIgnoreCase("employeeWeeklySummary")) {
				fileName = sdf.format(new Date()) + id + ".pdf";
				storedFileName = "empWeekSum";
			} else if (reportType.equalsIgnoreCase("companyDailyStatus")) {

				fileName = sdf.format(new Date()) + ".pdf";
				storedFileName = "admin";
			} else if (reportType.equalsIgnoreCase("employeedailystatus")) {
				fileName = sdf.format(new Date()) + id + ".pdf";
				storedFileName = "engineer";
			} else if (reportType.equalsIgnoreCase("employeeMonthlySummary")) {
				fileName = sdf.format(new Date()) + id + ".pdf";
				storedFileName = "empMonthSum";
			}
			file = new File("/tmp/" + storedFileName + fileName);
			fos = new FileOutputStream(file);
			int length = -1;
			byte[] buffer = new byte[4096];
			while ((length = in.read(buffer)) > -1) {
				fos.write(buffer, 0, length);
			}
			fos.close();
			in.close();
			stream = new ByteArrayInputStream(fos.toString().getBytes(Charset.forName("UTF-8")));
			Properties props = new Properties();
			props.put("mail.smtp.host", Constants.EMAIL_HOST);
			props.put("mail.smtp.port", Constants.EMAIL_PORT);
			props.put("mail.smtp.auth", Constants.EMAIL_AUTH);
			props.put("mail.smtp.starttls.enable", Constants.EMAIL_STARTTLS_ENABLE);
			Authenticator auth = new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);

				}
			};
			Session session = Session.getInstance(props, auth);
			getMimeMessge(storedFileName, session, sDate, eDate, fileName, file, email);
			//Logger.info("over");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Active AdminsList
	private static Address[] getAdminsEmailsList() throws AddressException {
		Role role = Role.find.where().eq("role", "Admin").findUnique();
		List<AppUser> adminList = AppUser.find.where().eq("role", role).eq("status", UserProjectStatus.Active)
				.findList();
		InternetAddress emails[] = new InternetAddress[adminList.size()];
		for (int i = 0; i < adminList.size(); i++) {
			emails[i] = new InternetAddress(adminList.get(i).email);
		}
		return emails;
	}

	// Week DatesList
	private static List<Date> getWeekDatesList() {
		Date date = new Date();
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek() + 1);
		Date dayFirstOfWeek = cal.getTime();
		List<Date> dates = new ArrayList<Date>();
		if (dates != null) {
			dates.clear();
		}
		Calendar calendar = null;
		for (int i = 0; i <= 7; i++) {
			calendar = Calendar.getInstance();
			calendar.setTime(dayFirstOfWeek);
			if (i != 0) {
				calendar.add(Calendar.DAY_OF_YEAR, -i);
				dates.add(calendar.getTime());
			}
		}
		return dates;
	}

	// Email Message Body Construction
	private static String getTextMessage(String type, String sDate, String eDate) {
		StringBuilder textMessage = new StringBuilder();
		//Logger.info("getTextMessage" + type);

		if (type.equalsIgnoreCase("company")) {
			textMessage.append("<html><Body>Hi,<br><br>");
			textMessage.append("Please find attached Weekly attendance report for all the employees for last Week: "
					+ sDate + "-" + eDate);
			textMessage.append("<br><br>Thanks,");
			textMessage.append("<br>BB8 Team");
			textMessage.append("</Body></html>");
		} else if (type.equalsIgnoreCase("admin")) {
			textMessage.append("<html><Body>Hi,<br><br>");
			textMessage.append("Please find attached today's 'Daily Status' Report for all the Employees.");
			textMessage.append("<br><br>Thanks,");
			textMessage.append("<br>BB8 Team");
			textMessage.append("</Body></html>");
		} else if (type.equalsIgnoreCase("engineer")) {
			textMessage.append("<html><Body>Hi,<br><br>");
			textMessage.append("Please find attached your BB8 report for today.");
			textMessage.append("<br><br>Thanks,");
			textMessage.append("<br>BB8 Team");
			textMessage.append("</Body></html>");
		} else if (type.equalsIgnoreCase("empWeekSum")) {
			textMessage.append("<html><Body>Hi,<br><br>");
			textMessage.append("Please find attached your BB8 report for lastWeek: " + sDate + "-" + eDate);
			textMessage.append("<br><br>Thanks,");
			textMessage.append("<br>BB8 Team");
			textMessage.append("</Body></html>");
		} else if (type.equalsIgnoreCase("empMonthSum")) {
			textMessage.append("<html><Body>Hi,<br><br>");
			textMessage.append("Please find attached your BB8 report for last Month: " + sDate + "-" + eDate);
			textMessage.append("<br><br>Thanks,");
			textMessage.append("<br>BB8 Team");
			textMessage.append("</Body></html>");
		}
		return textMessage.toString();

	}

	// Email Sending to that Particular mails based on pdf report type
	private static void getMimeMessge(String type, Session session, String sDate, String eDate, String fileName,
			File file, String email) {
		deletionFilesFromTmp(type, fileName, file);
		MimeMessage message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress(from));
			if (type.equalsIgnoreCase("admin")) {
				message.setSubject("Thrymr 'Daily Status' Report");
				message.setRecipients(Message.RecipientType.TO, getAdminsEmailsList());

			} else if (type.equalsIgnoreCase("company")) {

				message.setSubject("Thrymr - Company Weekly Attendance Report");
				message.setRecipients(Message.RecipientType.TO, getAdminsEmailsList());
			} else if (type.equalsIgnoreCase("engineer")) {
				message.setSubject("Your Today's BB8 Report");
				message.setRecipients(Message.RecipientType.TO, email);
			} else if (type.equalsIgnoreCase("empWeekSum")) {
				// Logger.info("empWeekSum");
				message.setSubject("Your's Weekly BB8 Report");
				message.setRecipients(Message.RecipientType.TO, email);
			} else if (type.equalsIgnoreCase("empMonthSum")) {
				message.setSubject("Monthly BB8 Report - " + new SimpleDateFormat("MMM-YYYY").format(new Date(sDate)));
				message.setRecipients(Message.RecipientType.TO, email);
			}

			if (file != null) {
				DataSource source = new FileDataSource(file);
				MimeBodyPart messageBodyPart2 = new MimeBodyPart();
				messageBodyPart2.setDataHandler(new DataHandler(source));
				MimeBodyPart messageBodyPart1 = new MimeBodyPart();
				messageBodyPart1.setContent(getTextMessage(type, sDate, eDate), "text/html");
				messageBodyPart2.setFileName(fileName);
				Multipart multipart = new MimeMultipart();
				multipart.addBodyPart(messageBodyPart2);
				multipart.addBodyPart(messageBodyPart1);
				message.setContent(multipart);
				Transport.send(message);
			} else {
				//Logger.info("null");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Deletiom of files from tmp folder based on type of pdf report
	private static void deletionFilesFromTmp(String type, String fileName, File file) {
		File directory = new File("/tmp/");
		File[] fList = directory.listFiles();
		if (fList != null) {
			for (File file1 : fList) {
				if (file1.isFile() && file1.getName().contains(type)) {
					if (!Utility.isSameDay(new Date(file1.lastModified()), new Date())) {
						file1.delete();
					}
				}
			}
		}
	}
	// END

	public static Date getFirstDateOfLastMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -1);
		calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		return calendar.getTime();
	}

	public static Date getLastDateOfLastMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -1);
		calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return calendar.getTime();
	}

	public static Date getFirstDateOfCurrentMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DATE));
		return calendar.getTime();
	}

	public static Boolean sendIndividualMail(String emailID, String subject, String message) {
		//Logger.debug("Called");
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "465");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

		try {
			// create Session obj
			Authenticator auth = new SMTPAuthenticator();

			Session session = Session.getInstance(props, auth);

			// prepare mail msg
			MimeMessage msg = new MimeMessage(session);
			msg.setSubject(subject);
			msg.setFrom(new InternetAddress(Constants.EMAIL_USERNAME));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(emailID));
			msg.setContent(message, "text/html; charset=utf-8");
			msg.setSentDate(new Date());

			Transport.send(msg);
		} // try
		catch (Exception ex) {
			// ex.printStackTrace();
		} // catch

		return true;
	}
}