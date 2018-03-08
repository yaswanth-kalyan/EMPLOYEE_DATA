package controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
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
import javax.mail.util.ByteArrayDataSource;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.google.common.io.Files;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import bean.AddInterviewerBean;
import bean.AddRoundBean;
import bean.ApplicantBean;
import bean.InterviewerFeedbackBean;
import bean.JobBean;
import bean.MailingDataBean;
import bean.MailingListBean;
import bean.RecruitmentReferenceBean;
import models.Alert;
import models.AppUser;
import models.Role;
import models.Roles;
import models.UserProjectStatus;
import models.recruitment.ApplicantStatus;
import models.recruitment.InterviewerAppUser;
import models.recruitment.JobStatus;
import models.recruitment.MailType;
import models.recruitment.RecruitmentApplicant;
import models.recruitment.RecruitmentCategory;
import models.recruitment.RecruitmentInterviewType;
import models.recruitment.RecruitmentInterviewerFeedback;
import models.recruitment.RecruitmentJob;
import models.recruitment.RecruitmentMailContent;
import models.recruitment.RecruitmentQuestionTemplate;
import models.recruitment.RecruitmentReference;
import models.recruitment.RecruitmentRole;
import models.recruitment.RecruitmentSelectionRound;
import models.recruitment.RecruitmentSkill;
import models.recruitment.RecruitmentSource;
import models.recruitment.SelectionRoundStatus;
import models.recruitment.MailingList;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import utils.CalenderInviteOfInterviews;
import utils.Constants;

public class RecruitmentController extends Controller {

	public Result openings() {

		List<RecruitmentCategory> recruitmentCategories = RecruitmentCategory.find
				.all();
		if (recruitmentCategories == null || recruitmentCategories.isEmpty()) {
			String defaultCategoriesString = "Java,Android,IOS,BA,SQA,UI,DBA,DM,HR,Finance,Admin,Sales,Procurement";
			String defaultCategoriesArray[] = defaultCategoriesString
					.split(",");
			for (int i = 0; i < defaultCategoriesArray.length; i++) {
				RecruitmentCategory recruitmentCategory = new RecruitmentCategory();
				recruitmentCategory.jobCategoryName = defaultCategoriesArray[i];
				recruitmentCategory.save();
			}
		}

		List<RecruitmentSkill> recruitmentSkills = RecruitmentSkill.find.all();
		if (recruitmentSkills == null || recruitmentSkills.isEmpty()) {
			String skillString = "Struts,Spring,Hibernate,Play,Wicket,GWT,ORACLE,SQL Server,Postgressql,Jquery,Javascript,HTML,XML,Angular JS,SOAP,Restful,Jasper,Selenium,QTP,QC,Junit,Jira,Maven,Bootstrap,Python,Jmeter";
			String skillArray[] = skillString.split(",");
			for (int i = 0; i < skillArray.length; i++) {
				RecruitmentSkill recruitmentSkill = new RecruitmentSkill();
				recruitmentSkill.skillName = skillArray[i];
				recruitmentSkill.save();
			}
		}

		List<RecruitmentRole> recruitmentRoles = RecruitmentRole.find.all();
		if (recruitmentRoles == null || recruitmentRoles.isEmpty()) {
			String jobRolesString = "Trainee,Developer,Executive,Lead,Architect,Manager,Head";
			String jobRolesArray[] = jobRolesString.split(",");
			for (int i = 0; i < jobRolesArray.length; i++) {
				RecruitmentRole recruitmentRole = new RecruitmentRole();
				recruitmentRole.jobRoleName = jobRolesArray[i];
				recruitmentRole.save();

			}

		}

		List<RecruitmentSource> recruitmentSources = RecruitmentSource.find
				.all();
		if (recruitmentSources == null || recruitmentSources.isEmpty()) {
			String jobSourcesString = "Employee Referral,LinkedIn, Naukri, Facebook, Monster";
			String jobSourcesArray[] = jobSourcesString.split(",");
			for (int i = 0; i < jobSourcesArray.length; i++) {
				RecruitmentSource recruitmentSource = new RecruitmentSource();
				recruitmentSource.sourceName = jobSourcesArray[i];
				recruitmentSource.save();
			}
		}

		List<RecruitmentJob> recruitmentJobs = RecruitmentJob.find.orderBy("id asc").findList();
		int openJob = RecruitmentJob.find.where()
				.eq("jobStatus", JobStatus.Open).findRowCount();
		int closeJob = RecruitmentJob.find.where()
				.eq("jobStatus", JobStatus.Closed).findRowCount();
		int deferedJob = RecruitmentJob.find.where()
				.eq("jobStatus", JobStatus.Defered).findRowCount();
		int crossedDeadLine = Ebean
				.createQuery(RecruitmentJob.class)
				.where()
				.and(Expr.lt("lastDate", new Date()),
						Expr.eq("jobStatus", JobStatus.Open)).findRowCount();
		int partialClosed = 0;
		for (RecruitmentJob recruitmentJob : recruitmentJobs) {
			// System.out.println(job.jobId);
			int noOfApplicants = Ebean
					.createQuery(RecruitmentApplicant.class)
					.where()
					.and(Expr.eq("recruitmentJob", recruitmentJob),
							Expr.eq("status", ApplicantStatus.Selected))
					.findRowCount();
			if (noOfApplicants >= 1) {
				partialClosed++;
			}
		}

		//Collections.reverse(recruitmentJobs);

		return ok(views.html.recruitment.openings.render(recruitmentJobs,
				recruitmentSkills, recruitmentCategories, recruitmentRoles,
				openJob, closeJob, deferedJob, null, crossedDeadLine,
				partialClosed));
	}

	public Result applicants() {
		
		List<RecruitmentRole> recruitmentRoles = RecruitmentRole.find.all();
		List<RecruitmentCategory> recruitmentCategories = RecruitmentCategory.find.all();
		List<AppUser> appUsers = AppUser.find.where().eq("status", UserProjectStatus.Active).findList();
		List<RecruitmentSource> recruitmentSources = RecruitmentSource.find.all();
		List<RecruitmentApplicant> recruitmentApplicants = RecruitmentApplicant.find.orderBy("id desc").findList();
		//Collections.reverse(recruitmentApplicants);
		List<RecruitmentJob> recruitmentJobs = RecruitmentJob.find.where().or(Expr.eq("jobStatus", JobStatus.Open),Expr.eq("jobStatus", JobStatus.Defered)).findList();
		Boolean flag = false;
		return ok(views.html.recruitment.applicants.render(recruitmentApplicants, appUsers, recruitmentRoles,recruitmentCategories, recruitmentSources, null,recruitmentJobs, null,flag));
	}

	public Result addNewApplicant() {
		List<RecruitmentRole> recruitmentRoles = RecruitmentRole.find.all();
		List<RecruitmentCategory> recruitmentCategories = RecruitmentCategory.find.all();
		List<AppUser> appUsers = AppUser.find.where().eq("status", UserProjectStatus.Active).findList();
		List<RecruitmentSource> recruitmentSources = RecruitmentSource.find.all();
		List<RecruitmentApplicant> recruitmentApplicants = RecruitmentApplicant.find.all();
		Collections.reverse(recruitmentApplicants);
		List<RecruitmentJob> recruitmentJobs = RecruitmentJob.find.where().or(Expr.eq("jobStatus", JobStatus.Open),Expr.eq("jobStatus", JobStatus.Defered)).orderBy("id desc").findList();
		Boolean flag = true;
		return ok(views.html.recruitment.applicants.render(recruitmentApplicants, appUsers, recruitmentRoles,recruitmentCategories, recruitmentSources, null,recruitmentJobs, null,flag));
	
	}
	public Result addRounds(Long id) {
		RecruitmentApplicant applicant = RecruitmentApplicant.find.byId(id);
		RecruitmentApplicant recruitmentApplicant = RecruitmentApplicant.find.byId(id);
		List<RecruitmentRole> recruitmentRoles = RecruitmentRole.find.all();
		List<RecruitmentCategory> recruitmentCategories = RecruitmentCategory.find.all();
		List<AppUser> appUsers = AppUser.find.where().eq("status", UserProjectStatus.Active).findList();
		List<RecruitmentSource> recruitmentSources = RecruitmentSource.find.all();
		List<RecruitmentApplicant> recruitmentApplicants = RecruitmentApplicant.find.all();
		Collections.reverse(recruitmentApplicants);
		List<RecruitmentJob> recruitmentJobs = RecruitmentJob.find.where().or(Expr.eq("jobStatus", JobStatus.Open),Expr.eq("jobStatus", JobStatus.Defered)).orderBy("id desc").findList();
		Boolean flag = false;
		return ok(views.html.recruitment.applicants.render(recruitmentApplicants, appUsers, recruitmentRoles,recruitmentCategories, recruitmentSources,recruitmentApplicant, recruitmentJobs, applicant,flag));
	}

	public Result configure() {
		return ok(views.html.recruitment.recruitmentlandingpage.render());
	}

	public Result source() {
		List<RecruitmentSource> recruitmentSources = RecruitmentSource.find
				.orderBy("id").findList();
		return ok(views.html.recruitment.source.render(recruitmentSources));
	}

	public Result addSource() {
		RecruitmentSource recruitmentSource = Form
				.form(RecruitmentSource.class).bindFromRequest().get();
		recruitmentSource.save();
		return redirect(routes.RecruitmentController.source());
	}

	public Result editSource(Long id) {
		RecruitmentSource recruitmentSource = RecruitmentSource.find.byId(id);
		List<RecruitmentSource> recruitmentSources = RecruitmentSource.find
				.orderBy("id").findList();
		recruitmentSources.remove(recruitmentSource);
		return ok(views.html.recruitment.editSource.render(recruitmentSource,
				recruitmentSources));
	}

	public Result editSourceAction() {
		RecruitmentSource recruitmentSource = Form
				.form(RecruitmentSource.class).bindFromRequest().get();
		recruitmentSource.update();
		List<RecruitmentSource> recruitmentSources = RecruitmentSource.find
				.orderBy("id").findList();
		flash().put(
				"alert",
				new Alert("alert-success", "Job Source update successfully!")
						.toString());
		return ok(views.html.recruitment.source.render(recruitmentSources));
	}

	public Result addJob() {
		JobBean jobBean = Form.form(JobBean.class).bindFromRequest().get();
		MultipartFormData body = request().body().asMultipartFormData();
		RecruitmentJob recruitmentJob = null;
		try {
			File file = null;
			FilePart description = body.getFile("jobDescription");
			recruitmentJob = JobBean.toJob(jobBean);
			if (description != null) {
				String filename = description.getFilename();
				filename = filename.substring(filename.indexOf("."),
						filename.length());
				/*
				 * if(filename.equalsIgnoreCase(".doc")){ file =
				 * CreateWordToPdf(description.getFile(),filename);
				 * recruitmentJob.fileContentType = "application/pdf"; String
				 * FileNamePdf = description.getFilename(); FileNamePdf =
				 * FileNamePdf.substring(0,FileNamePdf.indexOf("."));
				 * FileNamePdf = FileNamePdf+".pdf";
				 * recruitmentJob.fileName=FileNamePdf; }else{ file =
				 * description.getFile(); recruitmentJob.fileContentType =
				 * description.getContentType();
				 * recruitmentJob.fileName=description.getFilename(); }
				 */
				if (filename.equalsIgnoreCase(".pdf")) {
					recruitmentJob.fileContentType = description
							.getContentType();
				}
				file = description.getFile();
				recruitmentJob.fileName = description.getFilename();
				recruitmentJob.jobDescription = Files.toByteArray(file);
			}
			recruitmentJob.createdBy = AppUser.find.byId(Long
					.parseLong(session("AppUserId")));
			String ids = null;

			RecruitmentJob recruitmentJobIdLast = RecruitmentJob.find
					.setMaxRows(1).orderBy("id desc").findUnique();
			if (recruitmentJobIdLast != null) {
				ids = recruitmentJobIdLast.jobId;
			}
			for (;;) {
				ids = JobBean.getJobsId1(ids);
				RecruitmentJob recruitmentJobId = RecruitmentJob.find.where()
						.eq("jobId", ids).findUnique();
				if (recruitmentJobId == null) {
					break;
				}
			}
			recruitmentJob.jobId = ids;
			recruitmentJob.save();
			if (file != null) {
				// file.delete();
			}
			flash().put(
					"alert",
					new Alert("alert-success",
							"New Opening Successfully Added!").toString());
		} catch (Exception e) {
			e.printStackTrace();
			flash().put(
					"alert",
					new Alert("alert-danger",
							"New Opening Not Successfully Added .").toString());
		}
		return redirect(routes.RecruitmentController.openings());
	}

	public Result mailContent() {
		List<RecruitmentMailContent> MailContentList = RecruitmentMailContent.find
				.orderBy("id").findList();
		return ok(views.html.recruitment.mailContent.render(MailContentList));
	}
	
	public Result addMailContent() {
		RecruitmentMailContent mailContent = Form
				.form(RecruitmentMailContent.class).bindFromRequest().get();
		mailContent.save();
		flash().put(
				"alert",
				new Alert("alert-success",
						"New Mail Content Successfully Added!").toString());
		return redirect(routes.RecruitmentController.mailContent());
	}
	
	public Result updateMailContent() {
		RecruitmentMailContent mailContent = Form
				.form(RecruitmentMailContent.class).bindFromRequest().get();
		mailContent.update();
		flash().put(
				"alert",
				new Alert("alert-success",
						mailContent.mailType + " Mail Content Successfully Updated!").toString());
		return redirect(routes.RecruitmentController.mailContent());
	}
	
	public Result editMailContent(Long id) {
		
		RecruitmentMailContent mailContent = RecruitmentMailContent.find.byId(id);
		List<RecruitmentMailContent> MailContentList = RecruitmentMailContent.find
				.orderBy("id").findList();
		return ok(views.html.recruitment.editMailContent.render(mailContent,MailContentList));
		
	}
	
	public Result category() {
		List<RecruitmentCategory> recruitmentCategories = RecruitmentCategory.find
				.orderBy("id").findList();
		return ok(views.html.recruitment.category.render(recruitmentCategories));
	}

	public Result addCategory() {
		RecruitmentCategory recruitmentCategory = Form
				.form(RecruitmentCategory.class).bindFromRequest().get();
		recruitmentCategory.save();
		return redirect(routes.RecruitmentController.category());
	}

	public Result roles() {
		List<RecruitmentRole> recruitmentRoles = RecruitmentRole.find.orderBy(
				"id").findList();
		return ok(views.html.recruitment.roles.render(recruitmentRoles));
	}

	public Result addRole() {
		RecruitmentRole recruitmentRole = Form.form(RecruitmentRole.class)
				.bindFromRequest().get();
		recruitmentRole.save();
		return redirect(routes.RecruitmentController.roles());
	}

	public Result skills() {
		List<RecruitmentSkill> recruitmentSkills = RecruitmentSkill.find
				.orderBy("id").findList();
		return ok(views.html.recruitment.skills.render(recruitmentSkills));
	}

	public Result addSkill() {
		RecruitmentSkill recruitmentSkill = Form.form(RecruitmentSkill.class)
				.bindFromRequest().get();
		recruitmentSkill.save();
		return redirect(routes.RecruitmentController.skills());
	}

	public Result interviewType() {
		List<RecruitmentInterviewType> recruitmentInterviewTypes = RecruitmentInterviewType.find
				.orderBy("id").findList();
		return ok(views.html.recruitment.interviewType
				.render(recruitmentInterviewTypes));
	}

	public Result addInterviewType() {
		RecruitmentInterviewType recruitmentInterviewType = Form
				.form(RecruitmentInterviewType.class).bindFromRequest().get();
		recruitmentInterviewType.save();
		return redirect(routes.RecruitmentController.interviewType());
	}
	


	public Result editInterviewType(Long id) {
		RecruitmentInterviewType recruitmentInterviewType = RecruitmentInterviewType.find
				.byId(id);
		List<RecruitmentInterviewType> recruitmentInterviewTypes = RecruitmentInterviewType.find
				.orderBy("id").findList();
		recruitmentInterviewTypes.remove(recruitmentInterviewType);
		return ok(views.html.recruitment.editInterviewType.render(
				recruitmentInterviewType, recruitmentInterviewTypes));
	}

	public Result editInterviewTypeAction() {
		RecruitmentInterviewType recruitmentInterviewType = Form
				.form(RecruitmentInterviewType.class).bindFromRequest().get();
		recruitmentInterviewType.update();
		List<RecruitmentInterviewType> recruitmentInterviewTypes = RecruitmentInterviewType.find
				.orderBy("id").findList();
		flash().put(
				"alert",
				new Alert("alert-success",
						"Job Interview Type update successfully!").toString());
		return ok(views.html.recruitment.interviewType
				.render(recruitmentInterviewTypes));
	}

	public Result questionTemplate() {
		List<RecruitmentQuestionTemplate> recruitmentQuestionTemplates = RecruitmentQuestionTemplate.find
				.orderBy("id").findList();
		return ok(views.html.recruitment.questionTemplate
				.render(recruitmentQuestionTemplates));

	}

	public Result addQuestionTemplate() {
		RecruitmentQuestionTemplate recruitmentQuestionTemplate = Form
				.form(RecruitmentQuestionTemplate.class).bindFromRequest()
				.get();
		MultipartFormData body = request().body().asMultipartFormData();
		FilePart questionTemplateFilePart = body.getFile("questionTemplate");
		File questionTemplateFile = questionTemplateFilePart.getFile();
		String fileName = questionTemplateFilePart.getFilename();
		try {
			recruitmentQuestionTemplate.questionTemplate = Files
					.toByteArray(questionTemplateFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		recruitmentQuestionTemplate.filename = fileName;
		recruitmentQuestionTemplate.fileContentType = questionTemplateFilePart
				.getContentType();
		recruitmentQuestionTemplate.save();
		return redirect(routes.RecruitmentController.questionTemplate());
	}

	public Result questionTemplateEdit(Long id) {
		RecruitmentQuestionTemplate recruitmentQuestionTemplate = RecruitmentQuestionTemplate.find
				.byId(id);
		List<RecruitmentQuestionTemplate> recruitmentQuestionTemplates = RecruitmentQuestionTemplate.find
				.orderBy("id").findList();
		recruitmentQuestionTemplates.remove(recruitmentQuestionTemplate);
		return ok(views.html.recruitment.editQuestionTemplate.render(
				recruitmentQuestionTemplate, recruitmentQuestionTemplates));

	}

	public Result editQuetionTemplateAction() {
		RecruitmentQuestionTemplate recruitmentQuestionTemplate = Form
				.form(RecruitmentQuestionTemplate.class).bindFromRequest()
				.get();
		MultipartFormData body = request().body().asMultipartFormData();
		FilePart questionTemplateFilePart = body.getFile("questionTemplate");
		File questionTemplateFile = questionTemplateFilePart.getFile();
		String fileName = questionTemplateFilePart.getFilename();
		try {
			recruitmentQuestionTemplate.questionTemplate = Files
					.toByteArray(questionTemplateFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		recruitmentQuestionTemplate.filename = fileName;
		recruitmentQuestionTemplate.fileContentType = questionTemplateFilePart
				.getContentType();
		recruitmentQuestionTemplate.update();
		return redirect(routes.RecruitmentController.questionTemplate());
	}

	public Result getQuestionTemplate(Long id) {
		RecruitmentQuestionTemplate recruitmentQuestionTemplate = RecruitmentQuestionTemplate.find
				.byId(id);
		String uploadFileContentType = recruitmentQuestionTemplate.fileContentType;
		ByteArrayInputStream input = null;
		try {
			if (recruitmentQuestionTemplate != null
					&& recruitmentQuestionTemplate.fileContentType != null) {
				input = new ByteArrayInputStream(
						recruitmentQuestionTemplate.questionTemplate);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ok(input).as(uploadFileContentType);
	}

	public Result editCategory(Long id) {
		RecruitmentCategory recruitmentCategory = RecruitmentCategory.find
				.byId(id);
		List<RecruitmentCategory> recruitmentCategories = RecruitmentCategory.find
				.orderBy("id").findList();
		recruitmentCategories.remove(recruitmentCategory);
		return ok(views.html.recruitment.editJobCategory.render(
				recruitmentCategory, recruitmentCategories));
	}

	public Result editCategoryAction() {
		RecruitmentCategory recruitmentCategory = Form
				.form(RecruitmentCategory.class).bindFromRequest().get();
		recruitmentCategory.update();
		List<RecruitmentCategory> recruitmentCategories = RecruitmentCategory.find
				.orderBy("id").findList();
		flash().put(
				"alert",
				new Alert("alert-success", "Job Category update successfully!")
						.toString());
		return ok(views.html.recruitment.category.render(recruitmentCategories));
	}

	public Result editSkill(Long id) {
		RecruitmentSkill recruitmentSkill = RecruitmentSkill.find.byId(id);
		List<RecruitmentSkill> recruitmentSkills = RecruitmentSkill.find
				.orderBy("id").findList();
		recruitmentSkills.remove(recruitmentSkill);
		return ok(views.html.recruitment.editSkill.render(recruitmentSkill,
				recruitmentSkills));
	}

	public Result editSkillAction() {
		RecruitmentSkill recruitmentSkill = Form.form(RecruitmentSkill.class)
				.bindFromRequest().get();
		recruitmentSkill.update();
		List<RecruitmentSkill> recruitmentSkills = RecruitmentSkill.find
				.orderBy("id").findList();
		flash().put(
				"alert",
				new Alert("alert-success", "Job skill update successfully!")
						.toString());
		return ok(views.html.recruitment.skills.render(recruitmentSkills));
	}

	public Result editRole(Long id) {
		RecruitmentRole recruitmentRole = RecruitmentRole.find.byId(id);
		List<RecruitmentRole> recruitmentRoles = RecruitmentRole.find.orderBy(
				"id").findList();
		recruitmentRoles.remove(recruitmentRole);
		return ok(views.html.recruitment.editJobRole.render(recruitmentRole,
				recruitmentRoles));
	}

	public Result editRoleAction() {
		RecruitmentRole recruitmentRole = Form.form(RecruitmentRole.class)
				.bindFromRequest().get();
		recruitmentRole.update();
		List<RecruitmentRole> recruitmentRoles = RecruitmentRole.find.orderBy(
				"id").findList();
		flash().put(
				"alert",
				new Alert("alert-success", "Job Role update successfully!")
						.toString());
		return ok(views.html.recruitment.roles.render(recruitmentRoles));
	}

	public Result addApplicant() {
		ApplicantBean applicantBean = Form.form(ApplicantBean.class)
				.bindFromRequest().get();
		RecruitmentApplicant recruitmentApplicant = Form
				.form(RecruitmentApplicant.class).bindFromRequest().get();
		try {
			File file = null;
			MultipartFormData body = request().body().asMultipartFormData();
			FilePart description = body.getFile("resume");
			if (description != null) {
				String filename = description.getFilename();
				filename = filename.substring(filename.indexOf("."),
						filename.length());
				/*
				 * if(filename.equalsIgnoreCase(".doc")){ file =
				 * CreateWordToPdf(description.getFile(),filename);
				 * recruitmentApplicant.fileContentType = "application/pdf";
				 * String FileNamePdf = description.getFilename(); FileNamePdf =
				 * FileNamePdf.substring(0,FileNamePdf.indexOf("."));
				 * FileNamePdf = FileNamePdf+".pdf";
				 * Logger.debug("file nnnn"+FileNamePdf+" dsfsdfsd "+filename);
				 * recruitmentApplicant.fileName=FileNamePdf; }else{ file =
				 * description.getFile(); recruitmentApplicant.fileContentType =
				 * description.getContentType();
				 * recruitmentApplicant.fileName=description.getFilename(); }
				 */
				if (filename.equalsIgnoreCase(".pdf")) {
					recruitmentApplicant.fileContentType = description
							.getContentType();
				}
				file = description.getFile();
				recruitmentApplicant.fileName = description.getFilename();
				recruitmentApplicant.resume = Files.toByteArray(file);
			}
			
			RecruitmentApplicant applicant1 = ApplicantBean.toApplicant(recruitmentApplicant, applicantBean);
			
			String ids = null;
			RecruitmentApplicant applicantIdLast = RecruitmentApplicant.find
					.setMaxRows(1).orderBy("id desc").findUnique();
			if (applicantIdLast != null) {
				ids = applicantIdLast.applicationId;
			}
			for (;;) {
				ids = ApplicantBean.getApplicantId1(ids);
				RecruitmentApplicant recruitmentApplicantId = RecruitmentApplicant.find
						.where().eq("applicationId", ids).findUnique();
				if (recruitmentApplicantId == null) {
					break;
				}
			}
			applicant1.applicationId = ids;
			applicant1.save();
			if (file != null) {
				// file.delete();
			}
			flash().put(
					"alert",
					new Alert("alert-success",
							"New Applicant Successfully Added!").toString());
		} catch (Exception e) {
			flash().put(
					"alert",
					new Alert("alert-danger",
							"New Applicant Not Successfully Added .")
							.toString());
			e.printStackTrace();
		}
		return redirect(routes.RecruitmentController.applicants());
	}

	public Result editOpenning(Long id) {
		RecruitmentJob recruitmentJob = RecruitmentJob.find.byId(id);
		List<RecruitmentCategory> recruitmentCategories = RecruitmentCategory.find
				.all();
		List<RecruitmentSkill> recruitmentSkills = RecruitmentSkill.find.all();
		List<RecruitmentRole> recruitmentRoles = RecruitmentRole.find.all();
		List<RecruitmentJob> recruitmentJobs = RecruitmentJob.find.all();
		// Collections.reverse(jobs);
		int openJob = RecruitmentJob.find.where()
				.eq("jobStatus", JobStatus.Open).findRowCount();
		int closeJob = RecruitmentJob.find.where()
				.eq("jobStatus", JobStatus.Closed).findRowCount();
		int deferedJob = RecruitmentJob.find.where()
				.eq("jobStatus", JobStatus.Defered).findRowCount();
		int crossedDeadLine = Ebean.createQuery(RecruitmentJob.class)
				.where(Expr.lt("lastDate", new Date())).findRowCount();
		int partialClosed = 0;
		for (RecruitmentJob job1 : recruitmentJobs) {
			int noOfApplicants = Ebean.createQuery(RecruitmentApplicant.class)
					.where(Expr.eq("recruitmentJob", job1)).findRowCount();
			if (noOfApplicants >= job1.noOfOpenning / 2) {
				partialClosed++;
			}
		}
		Collections.reverse(recruitmentJobs);
		return ok(views.html.recruitment.openings.render(recruitmentJobs,
				recruitmentSkills, recruitmentCategories, recruitmentRoles,
				openJob, closeJob, deferedJob, recruitmentJob, crossedDeadLine,
				partialClosed));
	}

	public Result editOpenningAction() {

		JobBean jobBean = Form.form(JobBean.class).bindFromRequest().get();
		MultipartFormData body = request().body().asMultipartFormData();
		RecruitmentJob recruitmentJob = recruitmentJob = JobBean.toJob(jobBean);
		try {
			File file = null;
			FilePart description = body.getFile("jobDescription");
			//Logger.debug("clllasss");
			if (description != null) {
				String filename = description.getFilename();
				filename = filename.substring(filename.indexOf("."),
						filename.length());
				/*
				 * if(filename.equalsIgnoreCase(".doc")){ file =
				 * CreateWordToPdf(description.getFile(),filename); String
				 * FileNamePdf = description.getFilename(); FileNamePdf =
				 * FileNamePdf.substring(0,FileNamePdf.indexOf("."));
				 * FileNamePdf = FileNamePdf+".pdf";
				 * recruitmentJob.fileName=FileNamePdf;
				 * recruitmentJob.fileContentType = "application/pdf"; }else{
				 * file = description.getFile(); recruitmentJob.fileContentType
				 * = description.getContentType();
				 * recruitmentJob.fileName=description.getFilename(); }
				 */
				recruitmentJob.fileContentType = null;
				if (filename.equalsIgnoreCase(".pdf")) {
					recruitmentJob.fileContentType = description
							.getContentType();
				}
				file = description.getFile();
				recruitmentJob.fileName = description.getFilename();
				recruitmentJob.jobDescription = Files.toByteArray(file);
			}
			recruitmentJob.update();
			if (file != null) {
				// file.delete();
			}
			flash().put(
					"alert",
					new Alert("alert-success", "Job Opening ( Job ID : "
							+ recruitmentJob.jobId + " ) Successfully Updated!")
							.toString());
		} catch (Exception e) {
			flash().put(
					"alert",
					new Alert("alert-danger", "Job Opening ( Job ID:"
							+ recruitmentJob.jobId
							+ " ) Not Successfully Updated .").toString());
			e.printStackTrace();
		}
		return redirect(routes.RecruitmentController.openings());

	}

	public Result updateApplicant(Long id) {
		RecruitmentApplicant recruitmentApplicant = RecruitmentApplicant.find
				.byId(id);
		List<RecruitmentRole> recruitmentRoles = RecruitmentRole.find.all();
		List<RecruitmentCategory> recruitmentCategories = RecruitmentCategory.find
				.all();
		List<AppUser> appUsers = AppUser.find.where()
				.eq("status", UserProjectStatus.Active).findList();
		List<RecruitmentSource> recruitmentSources = RecruitmentSource.find
				.all();
		List<RecruitmentApplicant> recruitmentApplicants = RecruitmentApplicant.find
				.all();
		Collections.reverse(recruitmentApplicants);
		List<RecruitmentJob> recruitmentJobs = RecruitmentJob.find
				.where()
				.or(Expr.eq("jobStatus", JobStatus.Open),
						Expr.eq("jobStatus", JobStatus.Defered)).orderBy("id desc").findList();
		Boolean flag = true;
		return ok(views.html.recruitment.applicants.render(
				recruitmentApplicants, appUsers, recruitmentRoles,
				recruitmentCategories, recruitmentSources,
				recruitmentApplicant, recruitmentJobs, null,flag));
	}

	public Result editApplicant() {
		ApplicantBean applicantBean = Form.form(ApplicantBean.class)
				.bindFromRequest().get();
		RecruitmentApplicant recruitmentApplicant = Form
				.form(RecruitmentApplicant.class).bindFromRequest().get();
		try {
			File file = null;
			MultipartFormData body = request().body().asMultipartFormData();
			FilePart description = body.getFile("resume");
			if (description != null) {
				String filename = description.getFilename();
				filename = filename.substring(filename.indexOf("."),
						filename.length());
				/*
				 * if(filename.equalsIgnoreCase(".doc")){ file =
				 * CreateWordToPdf(description.getFile(),filename);
				 * recruitmentApplicant.fileContentType = "application/pdf";
				 * String FileNamePdf = description.getFilename(); FileNamePdf =
				 * FileNamePdf.substring(0,FileNamePdf.indexOf("."));
				 * FileNamePdf = FileNamePdf+".pdf";
				 * recruitmentApplicant.fileName=FileNamePdf; }else{ file =
				 * description.getFile(); recruitmentApplicant.fileContentType =
				 * description.getContentType();
				 * recruitmentApplicant.fileName=description.getFilename(); }
				 */
				recruitmentApplicant.fileContentType = null;
				if (filename.equalsIgnoreCase(".pdf")) {
					recruitmentApplicant.fileContentType = description
							.getContentType();
				}
				file = description.getFile();
				recruitmentApplicant.fileName = description.getFilename();
				recruitmentApplicant.resume = Files.toByteArray(file);
			}
			RecruitmentApplicant applicant1 = ApplicantBean.toApplicant(
					recruitmentApplicant, applicantBean);
			applicant1.update();
			if (file != null) {
				// file.delete();
			}
			flash().put(
					"alert",
					new Alert("alert-success", "Applicant ( Applicant Id : "
							+ recruitmentApplicant.applicationId
							+ " ) Successfully Updated!").toString());
		} catch (Exception e) {
			flash().put(
					"alert",
					new Alert("alert-danger", "Applicant  ( Applicant Id : "
							+ recruitmentApplicant.applicationId
							+ " ) Not Successfully Updated .").toString());
			e.printStackTrace();
		}
		return redirect(routes.RecruitmentController.applicants());

	}

	public Result deleteJobCategory(Long id) {
		RecruitmentCategory recruitmentCategory = RecruitmentCategory.find
				.byId(id);
		int jobCategoryCount = RecruitmentJob.find.where()
				.eq("recruitmentCategory", recruitmentCategory).findRowCount();
		int applicantCategoryCount = RecruitmentApplicant.find.where()
				.eq("applicantCategory", recruitmentCategory).findRowCount();
		if (jobCategoryCount > 0 || applicantCategoryCount > 0) {
			flash().put(
					"alert",
					new Alert("alert-danger",
							"You can't delete this category belongs to some jobs or applicants!")
							.toString());
			return redirect(routes.RecruitmentController.category());

		} else {
			recruitmentCategory.delete();
			flash().put(
					"alert",
					new Alert("alert-danger",
							"JobCategory deleted successfully!").toString());
			return redirect(routes.RecruitmentController.category());
		}
	}

	public Result deleteRole(Long id) {
		RecruitmentRole recruitmentRole = RecruitmentRole.find.byId(id);
		int jobJobRoleCount = RecruitmentJob.find.where()
				.eq("recruitmentRole", recruitmentRole).findRowCount();
		int applicantJobRoleCount = RecruitmentApplicant.find.where()
				.eq("recruitmentRole", recruitmentRole).findRowCount();
		if (jobJobRoleCount > 0 || applicantJobRoleCount > 0) {
			flash().put(
					"alert",
					new Alert("alert-danger",
							"You can't delete this JobRole belongs to some jobs or applicants!")
							.toString());
			return redirect(routes.RecruitmentController.roles());
		} else {
			recruitmentRole.delete();
			flash().put(
					"alert",
					new Alert("alert-danger", "JobRole deleted successfully!")
							.toString());
			return redirect(routes.RecruitmentController.roles());

		}
	}

	public Result deleteSkill(Long id) {
		RecruitmentSkill recruitmentSkill = RecruitmentSkill.find.byId(id);
		int countMandatoryOrDesiredSkillsExistInJob = RecruitmentJob.find
				.where()
				.or(Expr.eq("jobStatus", JobStatus.Open),
						Expr.eq("jobStatus", JobStatus.Defered))
				.or(Expr.eq("mandatorySkills.id", recruitmentSkill.id),
						Expr.eq("desiredSkills.id", recruitmentSkill.id))
				.findRowCount();
		if (countMandatoryOrDesiredSkillsExistInJob > 0) {
			flash().put(
					"alert",
					new Alert("alert-danger",
							"You can't delete this skill belongs to some jobs!")
							.toString());
			return redirect(routes.RecruitmentController.skills());
		} else {
			recruitmentSkill.delete();
			flash().put(
					"alert",
					new Alert("alert-danger", "Skill deleted successfully!")
							.toString());
			return redirect(routes.RecruitmentController.skills());
		}

	}

	public Result deleteSource(Long id) {
		RecruitmentSource recruitmentSource = RecruitmentSource.find.byId(id);

		int applicantJobSourceCount = RecruitmentApplicant.find.where()
				.eq("recruitmentSource", recruitmentSource).findRowCount();
		if (applicantJobSourceCount > 0) {
			flash().put(
					"alert",
					new Alert("alert-danger",
							"You can't delete this Source belongs to some applicants!")
							.toString());
			return redirect(routes.RecruitmentController.source());
		} else {
			recruitmentSource.delete();
			flash().put(
					"alert",
					new Alert("alert-danger", "Source deleted successfully!")
							.toString());
			return redirect(routes.RecruitmentController.source());

		}
	}

	public Result deleteJobInterviewType(Long id) {
		RecruitmentInterviewType recruitmentInterviewType = RecruitmentInterviewType.find
				.byId(id);
		int recruitmentSelectionRoundCount = RecruitmentSelectionRound.find
				.where()
				.eq("recruitmentInterviewType", recruitmentInterviewType)
				.findRowCount();

		if (recruitmentSelectionRoundCount > 0) {
			flash().put(
					"alert",
					new Alert("alert-danger",
							"You can't delete this Interview Type belongs to some Selection Round!")
							.toString());
			return redirect(routes.RecruitmentController.interviewType());

		} else {
			recruitmentInterviewType.delete();
			flash().put(
					"alert",
					new Alert("alert-danger",
							"Interview Type deleted successfully!").toString());
			return redirect(routes.RecruitmentController.interviewType());
		}
	}

	public Result deleteQuestionTemplate(Long id) {
		RecruitmentQuestionTemplate recruitmentQuestionTemplate = RecruitmentQuestionTemplate.find
				.byId(id);
		int recruitmentQuestionTemplateCount = RecruitmentSelectionRound.find
				.where().eq("QuestionTemplate", recruitmentQuestionTemplate)
				.findRowCount();

		if (recruitmentQuestionTemplateCount > 0) {
			flash().put(
					"alert",
					new Alert("alert-danger",
							"You can't delete this QuestionTemplate belongs to some Selection Round!")
							.toString());
			return redirect(routes.RecruitmentController.questionTemplate());

		} else {
			recruitmentQuestionTemplate.delete();
			flash().put(
					"alert",
					new Alert("alert-danger",
							"QuestionTemplate deleted successfully!")
							.toString());
			return redirect(routes.RecruitmentController.questionTemplate());
		}
	}

	public Result downloadJobDescription(Long id) {
		RecruitmentJob recruitmentJob = RecruitmentJob.find.byId(id);
		String uploadFileContentType = recruitmentJob.fileContentType;
		String uploadFileName = recruitmentJob.fileName;
		byte[] uploadFile = recruitmentJob.jobDescription;
		response().setContentType("APPLICATION/OCTET-STREAM");
		response().setHeader("Content-Disposition",
				"attachment; filename=\"" + uploadFileName + "\"");
		return ok(uploadFile).as(uploadFileContentType);

	}

	public Result getJobDescription(Long id) {
		RecruitmentJob recruitmentJob = RecruitmentJob.find.byId(id);
		String uploadFileContentType = recruitmentJob.fileContentType;
		ByteArrayInputStream input = null;
		try {
			if (recruitmentJob != null
					&& recruitmentJob.fileContentType != null) {
				input = new ByteArrayInputStream(recruitmentJob.jobDescription);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ok(input).as(uploadFileContentType);
	}

	public Result downloadApplicantResume(Long id) {
		RecruitmentApplicant recruitmentApplicant = RecruitmentApplicant.find
				.byId(id);
		String uploadFileContentType = recruitmentApplicant.fileContentType;
		String uploadFileName = recruitmentApplicant.fileName;
		byte[] uploadFile = recruitmentApplicant.resume;
		response().setContentType("APPLICATION/OCTET-STREAM");
		response().setHeader("Content-Disposition",
				"attachment; filename=\"" + uploadFileName + "\"");
		return ok(uploadFile).as(uploadFileContentType);
	}

	public Result getResume(Long id) {
		RecruitmentApplicant recruitmentApplicant = RecruitmentApplicant.find
				.byId(id);
		String uploadFileContentType = recruitmentApplicant.fileContentType;
		ByteArrayInputStream input = null;
		try {
			if (recruitmentApplicant != null
					&& recruitmentApplicant.fileContentType != null) {
				input = new ByteArrayInputStream(recruitmentApplicant.resume);
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return ok(input).as(uploadFileContentType);
	}

	public Result addRoundAction() {
		AddRoundBean addRoundBean = Form.form(AddRoundBean.class).bindFromRequest().get();
		try {
			RecruitmentSelectionRound recruitmentSelectionRound = new RecruitmentSelectionRound();
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
			if(addRoundBean.getInterviewConductDate() != null) {
				//Logger.info(addRoundBean.getInterviewConductDate());
				Date conductDate = sdf.parse(addRoundBean.getInterviewConductDate());
				recruitmentSelectionRound.conductDate = conductDate;
			}
			if(addRoundBean.getInterviewer() != null && !addRoundBean.getInterviewer().isEmpty())
				for (Long id : addRoundBean.getInterviewer()) {
					recruitmentSelectionRound.interviewer.add(InterviewerAppUser.find.byId(id));
			}
			if(addRoundBean.questionTemplate != null) {
				recruitmentSelectionRound.QuestionTemplate = RecruitmentQuestionTemplate.find.byId(addRoundBean.questionTemplate);
			}
			if(addRoundBean.interviewType != null) {
				recruitmentSelectionRound.recruitmentInterviewType = RecruitmentInterviewType.find.byId(addRoundBean.interviewType);
			}
			if(addRoundBean.recruitmentId != null) {
				recruitmentSelectionRound.recruitmentApplicant = RecruitmentApplicant.find.byId(addRoundBean.recruitmentId);
			}
			if(addRoundBean.remark != null) {
				recruitmentSelectionRound.remark = addRoundBean.remark;
			}
			if(addRoundBean.selectionRoundStatus != null) {
				recruitmentSelectionRound.selectionStatus = addRoundBean.selectionRoundStatus;
			}
			if(addRoundBean.interviewSelectionResult != null) {
				recruitmentSelectionRound.selectionResult = addRoundBean.interviewSelectionResult;
			}
			if(addRoundBean.interviewVenue != null) {
				recruitmentSelectionRound.interviewVenue = addRoundBean.interviewVenue;
			}
			recruitmentSelectionRound.toDate = EngineerController.getTodayDate(sdf.parse(addRoundBean.getInterviewConductDate()));
			recruitmentSelectionRound.save();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return redirect(routes.RecruitmentController.addRounds(addRoundBean.recruitmentId));
	}

	public Result editRecruitmentSelectionRound(Long id) {
		RecruitmentSelectionRound recruitmentSelectionRound = RecruitmentSelectionRound.find
				.byId(id);
		return ok(views.html.recruitment.editInterviewRound
				.render(recruitmentSelectionRound));
	}

	public Result selectionRoundEdit() {
		AddRoundBean addRoundBean = Form.form(AddRoundBean.class)
				.bindFromRequest().get();
		try {
			RecruitmentSelectionRound recruitmentSelectionRound = RecruitmentSelectionRound.find.byId(addRoundBean.id);
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
			if (addRoundBean.getInterviewConductDate() != null) {
				Date conductDate = sdf.parse(addRoundBean
						.getInterviewConductDate());
				recruitmentSelectionRound.conductDate = conductDate;
			}
			if (addRoundBean.getInterviewer() != null
					&& !addRoundBean.getInterviewer().isEmpty())
				recruitmentSelectionRound.interviewer.clear();
			recruitmentSelectionRound.update();
			for (Long id : addRoundBean.getInterviewer()) {
				recruitmentSelectionRound.interviewer
						.add(InterviewerAppUser.find.byId(id));
			}
			if (addRoundBean.questionTemplate == null) {
				recruitmentSelectionRound.QuestionTemplate = null;
			} else {
				recruitmentSelectionRound.QuestionTemplate = RecruitmentQuestionTemplate.find
						.byId(addRoundBean.questionTemplate);
			}
			if (addRoundBean.interviewType == null) {
				recruitmentSelectionRound.recruitmentInterviewType = null;
			} else {
				recruitmentSelectionRound.recruitmentInterviewType = RecruitmentInterviewType.find
						.byId(addRoundBean.interviewType);
			}
			if (addRoundBean.recruitmentId == null) {
				recruitmentSelectionRound.recruitmentApplicant = null;
			} else {
				recruitmentSelectionRound.recruitmentApplicant = RecruitmentApplicant.find
						.byId(addRoundBean.recruitmentId);
			}
			if (addRoundBean.remark == null) {
				recruitmentSelectionRound.remark = null;
			} else {
				recruitmentSelectionRound.remark = addRoundBean.remark;
			}
			if (addRoundBean.selectionRoundStatus == null) {
				recruitmentSelectionRound.selectionStatus = null;
			} else {
				recruitmentSelectionRound.selectionStatus = addRoundBean.selectionRoundStatus;
			}
			if (addRoundBean.interviewSelectionResult == null) {
				recruitmentSelectionRound.selectionResult = null;
			} else {
				recruitmentSelectionRound.selectionResult = addRoundBean.interviewSelectionResult;
			}
			if (addRoundBean.interviewVenue == null) {
				recruitmentSelectionRound.interviewVenue = addRoundBean.interviewVenue;
			}
			recruitmentSelectionRound.toDate = EngineerController.getTodayDate(sdf.parse(addRoundBean.getInterviewConductDate()));
			recruitmentSelectionRound.update();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return redirect(routes.RecruitmentController
				.addRounds(addRoundBean.recruitmentId));
	}

	public Result addInterviewer() {
		AddInterviewerBean addInterviewerBean = Form
				.form(AddInterviewerBean.class).bindFromRequest().get();
		if (addInterviewerBean.ids != null && !addInterviewerBean.ids.isEmpty()) {
			for (Long id : addInterviewerBean.ids) {
				AppUser appUser = AppUser.find.byId(id);
				InterviewerAppUser interviewerAppUser = InterviewerAppUser.find
						.where().eq("interviewer", appUser).findUnique();
				if (interviewerAppUser == null) {
					InterviewerAppUser interviewerAppUser1 = new InterviewerAppUser();
					interviewerAppUser1.interviewer = appUser;
					interviewerAppUser1.save();
				}
			}
		}
		return ok();
	}

	public Result interviewerFeedbackRender() {
		InterviewerAppUser interviewerAppUser = InterviewerAppUser.find.where().eq("interviewer",AppUser.find.byId(Long.parseLong(session("AppUserId")))).findUnique();
		List<RecruitmentApplicant> recruitmentApplicantsList = null;
		if (interviewerAppUser != null) {
			List<RecruitmentSelectionRound> recruitmentSelectionRounds = interviewerAppUser.recruitmentSelectionRounds;
			Set<RecruitmentApplicant> recruitmentApplicantsSet = new HashSet<RecruitmentApplicant>();
			
			List<RecruitmentInterviewerFeedback> reIvFbList = RecruitmentInterviewerFeedback.find.all();
			
			for (RecruitmentSelectionRound recruitmentSelectionRound : recruitmentSelectionRounds) {
				if(RecruitmentInterviewerFeedback.find.where().eq("recruitmentSelectionRound", recruitmentSelectionRound).eq("interviewerAppUser", interviewerAppUser).findUnique() == null){
					recruitmentApplicantsSet.add(recruitmentSelectionRound.recruitmentApplicant);
				}
			}
			recruitmentApplicantsList = new ArrayList<RecruitmentApplicant>(recruitmentApplicantsSet);
		}
		return ok(views.html.recruitment.interviewerFeedback.render(recruitmentApplicantsList));
	}

	public Result getInterviewRounds(Long id) {
		RecruitmentApplicant recruitmentApplicant = RecruitmentApplicant.find.byId(id);
		Map<String, String> jsonMap = new HashMap<String, String>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		for (RecruitmentSelectionRound recruitmentSelectionRound : recruitmentApplicant.recruitmentSelectionRounds) {
			AppUser appUser=Application.getLoggedInUser();
			InterviewerAppUser interviewerAppUser=InterviewerAppUser.find.where().eq("interviewer",appUser).findUnique();
			
			RecruitmentInterviewerFeedback recruitmentInterviewerFeedback=RecruitmentInterviewerFeedback.find.where().eq("recruitmentSelectionRound", recruitmentSelectionRound).eq("interviewerAppUser",interviewerAppUser).findUnique();
			
			if (recruitmentSelectionRound.interviewer.contains(InterviewerAppUser.find.where().eq("interviewer",appUser).findUnique()) && !recruitmentSelectionRound.recruitmentInterviewerFeedbacks.contains(recruitmentInterviewerFeedback)) {
				String dateAndTime = sdf.format(recruitmentSelectionRound.conductDate);
				jsonMap.put("" + recruitmentSelectionRound.id,""+ recruitmentSelectionRound.recruitmentInterviewType.interviewTypeName+ " (" + dateAndTime + ")");
			}

		}
		return ok(Json.toJson(jsonMap));
	}

	public Result addInterviewerFeedBack() {
		
		InterviewerFeedbackBean interviewerFeedbackBean = Form.form(InterviewerFeedbackBean.class).bindFromRequest().get();
		RecruitmentInterviewerFeedback recruitmentInterviewerFeedback = new RecruitmentInterviewerFeedback();
		recruitmentInterviewerFeedback.interviewerAppUser = InterviewerAppUser.find.where().eq("interviewer",AppUser.find.byId(Long.parseLong(session("AppUserId")))).findUnique();
		recruitmentInterviewerFeedback.recruitmentApplicant = RecruitmentApplicant.find.byId(interviewerFeedbackBean.applicantId);
		recruitmentInterviewerFeedback.recruitmentSelectionRound = RecruitmentSelectionRound.find.byId(interviewerFeedbackBean.interviewRoundId);
		recruitmentInterviewerFeedback.feedBack = interviewerFeedbackBean.feedback;
		recruitmentInterviewerFeedback.remark = interviewerFeedbackBean.remark;
		recruitmentInterviewerFeedback.save();
		
		//send mail RecruitmentInterviewerFeedback recruitmentInterviewerFeedback
		sendInterviewerFeedBack(recruitmentInterviewerFeedback);
		flash().put(
				"alert",
				new Alert("alert-success", "Feedback save successfully!")
						.toString());
		return redirect(routes.RecruitmentController.interviewerFeedbackRender());
	}

	public Result selectionRoundDelete(Long id) {
		RecruitmentSelectionRound recruitmentSelectionRound = RecruitmentSelectionRound.find
				.byId(id);
		recruitmentSelectionRound.delete();
		flash().put(
				"alert",
				new Alert("alert-danger", "Selection Round deleted!")
						.toString());
		return redirect(routes.RecruitmentController
				.addRounds(recruitmentSelectionRound.recruitmentApplicant.id));
	}

	public Result isExistInterviewType() {
		DynamicForm requestForm = Form.form().bindFromRequest();
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		String id = requestForm.get("id");
		try {
			if (id != null && !id.isEmpty()) {
				map.put("valid",
						!(RecruitmentInterviewType.find
								.where()
								.ieq("interviewTypeName",
										requestForm.get("interviewTypeName")
												.trim())
								.ne("id", Long.parseLong(id)).findRowCount() > 0));
			} else {
				map.put("valid",
						!((RecruitmentInterviewType.find
								.where()
								.ieq("interviewTypeName",
										requestForm.get("interviewTypeName")
												.trim()).findList().size()) > 0));
			}
		} catch (Exception e) {
			map.put("valid", false);
		}
		return ok(Json.toJson(map));
	}

	public Result isExistCategory() {
		DynamicForm requestForm = Form.form().bindFromRequest();
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		String id = requestForm.get("id");
		try {
			if (id != null && !id.isEmpty()) {
				map.put("valid",
						!(RecruitmentCategory.find
								.where()
								.ieq("jobCategoryName",
										requestForm.get("jobCategoryName")
												.trim())
								.ne("id", Long.parseLong(id)).findRowCount() > 0));
			} else {
				map.put("valid",
						!((RecruitmentCategory.find
								.where()
								.ieq("jobCategoryName",
										requestForm.get("jobCategoryName")
												.trim()).findList().size()) > 0));
			}
		} catch (Exception e) {
			map.put("valid", false);
		}
		return ok(Json.toJson(map));
	}
	
	public Result isExistMailType() {
		DynamicForm requestForm = Form.form().bindFromRequest();
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		String id = requestForm.get("id");
		try {
			if (id != null && !id.isEmpty()) {
				map.put("valid",
						!(RecruitmentMailContent.find
								.where()
								.ieq("mailType",
										requestForm.get("mailType")
												.trim())
								.ne("id", Long.parseLong(id)).findRowCount() > 0));
			} else {
				map.put("valid",
						!((RecruitmentMailContent.find
								.where()
								.ieq("mailType",
										requestForm.get("mailType")
												.trim()).findList().size()) > 0));
			}
		} catch (Exception e) {
			map.put("valid", false);
		}
		return ok(Json.toJson(map));
	}

	public Result isExistRole() {
		DynamicForm requestForm = Form.form().bindFromRequest();
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		String id = requestForm.get("id");
		try {
			if (id != null && !id.isEmpty()) {
				map.put("valid",
						!(RecruitmentRole.find
								.where()
								.ieq("jobRoleName",
										requestForm.get("jobRoleName").trim())
								.ne("id", Long.parseLong(id)).findRowCount() > 0));
			} else {
				map.put("valid",
						!((RecruitmentRole.find
								.where()
								.ieq("jobRoleName",
										requestForm.get("jobRoleName").trim())
								.findList().size()) > 0));
			}
		} catch (Exception e) {
			map.put("valid", false);
		}
		return ok(Json.toJson(map));
	}

	public Result isExistSkill() {
		DynamicForm requestForm = Form.form().bindFromRequest();
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		String id = requestForm.get("id");
		try {
			if (id != null && !id.isEmpty()) {
				map.put("valid",
						!(RecruitmentSkill.find
								.where()
								.ieq("skillName",
										requestForm.get("skillName").trim())
								.ne("id", Long.parseLong(id)).findRowCount() > 0));
			} else {
				map.put("valid",
						!((RecruitmentSkill.find
								.where()
								.ieq("skillName",
										requestForm.get("skillName").trim())
								.findList().size()) > 0));
			}
		} catch (Exception e) {
			map.put("valid", false);
		}
		return ok(Json.toJson(map));
	}

	public Result isExistSource() {
		DynamicForm requestForm = Form.form().bindFromRequest();
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		String id = requestForm.get("id");
		try {
			if (id != null && !id.isEmpty()) {
				map.put("valid",
						!(RecruitmentSource.find
								.where()
								.ieq("sourceName",
										requestForm.get("sourceName").trim())
								.ne("id", Long.parseLong(id)).findRowCount() > 0));
			} else {
				map.put("valid",
						!((RecruitmentSource.find
								.where()
								.ieq("sourceName",
										requestForm.get("sourceName").trim())
								.findList().size()) > 0));
			}
		} catch (Exception e) {
			map.put("valid", false);
		}
		return ok(Json.toJson(map));
	}

	public Result isExistApplicantEmailId() {
		DynamicForm requestForm = Form.form().bindFromRequest();
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		String id = requestForm.get("id");
		try {
			if (id != null && !id.isEmpty()) {
				map.put("valid",
						!(RecruitmentApplicant.find
								.where()
								.ieq("emailId",
										requestForm.get("emailId").trim())
								.ne("id", Long.parseLong(id)).findRowCount() > 0));
			} else {
				map.put("valid",
						!((RecruitmentApplicant.find
								.where()
								.ieq("emailId",
										requestForm.get("emailId").trim())
								.findList().size()) > 0));
			}
		} catch (Exception e) {
			map.put("valid", false);
		}
		return ok(Json.toJson(map));
	}

	public Result isExistContactNo() {
		DynamicForm requestForm = Form.form().bindFromRequest();
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		String id = requestForm.get("id");
		String phNoStr = requestForm.get("contactNo").trim();
		try {
			if (id != null && !id.isEmpty()) {
				map.put("valid",
						!(RecruitmentApplicant.find.where()
								.eq("contactNo", Long.parseLong(phNoStr))
								.ne("id", Long.parseLong(id)).findRowCount() > 0));
			} else if (phNoStr != null && !phNoStr.isEmpty()) {
				map.put("valid",
						!((RecruitmentApplicant.find.where()
								.eq("contactNo", Long.parseLong(phNoStr))
								.findList().size()) > 0));
			} else {
				map.put("valid", false);
			}
		} catch (Exception e) {
			map.put("valid", false);
		}
		return ok(Json.toJson(map));
	}

	public Result checkResumeFormat(){
		DynamicForm requestForm = Form.form().bindFromRequest();
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        String fileFormat = requestForm.get("resume");
        Logger.debug(fileFormat);
        fileFormat = fileFormat.substring(fileFormat.indexOf("."),fileFormat.length());
        try{
        	if (fileFormat.equalsIgnoreCase(".pdf") || fileFormat.equalsIgnoreCase(".doc") || fileFormat.equalsIgnoreCase(".docx")) {
                map.put("valid", true);
            } else {
            	 map.put("valid",false);
            }
        }catch(Exception e){
            map.put("valid",false);
        }
        return ok(Json.toJson(map));
	}
	
	public static File CreateWordToPdf(File files, String filename) {
		POIFSFileSystem fs = null;
		Document document = new Document();
		File outFIle = new File("conf/excel/testToday_" + new Date().toString()
				+ ".pdf");
		try {
			System.out.println("Starting the test");
			fs = new POIFSFileSystem(new FileInputStream(files));

			HWPFDocument doc = new HWPFDocument(fs);
			WordExtractor we = new WordExtractor(doc);

			OutputStream file = new FileOutputStream(outFIle);

			PdfWriter writer = PdfWriter.getInstance(document, file);

			Range range = doc.getRange();
			document.open();
			writer.setPageEmpty(true);
			document.newPage();
			writer.setPageEmpty(true);

			String[] paragraphs = we.getParagraphText();
			for (int i = 0; i < paragraphs.length; i++) {

				org.apache.poi.hwpf.usermodel.Paragraph pr = range
						.getParagraph(i);
				// CharacterRun run = pr.getCharacterRun(i);
				// run.setBold(true);
				// run.setCapitalized(true);
				// run.setItalic(true);
				paragraphs[i] = paragraphs[i].replaceAll("\\cM?\r?\n", "");
				// System.out.println("Length:" + paragraphs[i].length());
				// System.out.println("Paragraph" + i + ": "
				// + paragraphs[i].toString());

				// add the paragraph to the document
				document.add(new Paragraph(paragraphs[i]));
			}

			System.out.println("Document testing completed");
		} catch (Exception e) {
			System.out.println("Exception during test");
			e.printStackTrace();
		} finally {
			// close the document
			document.close();
		}
		return outFIle;

	}
	
	
	/*  ******
	 * 
	 *  Mail Sending Coding
	 *
	 * ****** */
	public static List<AppUser> getActiveHrList() {
		Role roleHr = Role.find.where().eq("role", Roles.HR.toString()).findUnique();
		List<AppUser> appUserHRList = AppUser.find.where().eq("status",UserProjectStatus.Active).in("role", roleHr).findList();
		return appUserHRList;
	}
	
	public static List<AppUser> getMailingList(){
		List<MailingList> mailingLists = MailingList.find.all();
		List<AppUser> activeHRList = new ArrayList<AppUser>();
		for (MailingList mailingList : mailingLists) {
			activeHRList.add(mailingList.appUser);
		}
		return activeHRList;
	}
	
	public Result SendMailApplicant(Long Id){
		try {
			
			MailingDataBean mailingDataBean = Form.form(MailingDataBean.class).bindFromRequest().get();
//			DynamicForm form = Form.form().bindFromRequest();
			String subject = mailingDataBean.getSubjectName();
			String Body = mailingDataBean.getBodyName();
//			List<String> ToMailingList = mailingDataBean.getToMailingList();
			List<String> ToMailingList = new ArrayList<String>();
			for(AppUser appUser : getMailingList()){
				ToMailingList.add(appUser.getEmail());
			}
			
			Logger.debug(mailingDataBean.toString());
			
			RecruitmentApplicant recruitmentApplicant = RecruitmentApplicant.find.byId(Id);
			if(recruitmentApplicant != null){
				String fileName = null;
				File outputFile = null;
				if(recruitmentApplicant.recruitmentJob != null){
					byte[] JobFile = recruitmentApplicant.recruitmentJob.jobDescription;
					if(JobFile != null){
						fileName = "conf/excel/"+recruitmentApplicant.recruitmentJob.fileName;
						outputFile = new File(fileName);
						FileOutputStream outputStream = new FileOutputStream(outputFile);
						outputStream.write(JobFile);
						outputStream.close();
					}
				}
				Boolean flag = sendMailInterviewApplicant(ToMailingList,recruitmentApplicant,fileName,subject,Body);
				if(outputFile != null){
					outputFile.delete();
				}
				if(flag){
					recruitmentApplicant.sendIntroMailFlag = true;
					recruitmentApplicant.update();
					return ok("Intro Mail successfully sent to Applicant "+recruitmentApplicant.applicationId);
				}else{
					return ok("Intro Mail not successfully sent"+recruitmentApplicant.applicationId);
				}
			}else{
				return ok("Intro Mail not successfully sent");
			}
		}catch(Exception e){
			e.printStackTrace();
			return ok("Exception Occured");
		}
		
	}
	
	
	private static class SMTPAuthenticator extends javax.mail.Authenticator {
		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(Constants.EMAIL_HR_USERNAME,
					Constants.EMAIL_HR_PASSWORD);
		}
	}
	//Send mail after fill daily status
	public  Boolean sendMailInterviewApplicant(List<String> ToMailingList,RecruitmentApplicant recruitmentApplicant,String fileName,String Subject,String Body) {
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
			
			/*msg.setSubject("Hello from Thrymr Software!!!");*/
			msg.setSubject(Subject);
			msg.setFrom(new InternetAddress(Constants.EMAIL_HR_USERNAME));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
					recruitmentApplicant.emailId));
			
				//List<AppUser> activeHRs = getMailingList();
				if(!ToMailingList.isEmpty()){
					InternetAddress[] myCcList = new InternetAddress[(ToMailingList.size())];
						for(int i=0;i<ToMailingList.size();i++){
							myCcList[i] = new InternetAddress(ToMailingList.get(i));
						}
							msg.setRecipients(Message.RecipientType.CC, myCcList);
				}
//			RecruitmentMailContent mailContent = RecruitmentMailContent.find.where().eq("mailType", MailType.Intro_Email).findUnique();
			BodyPart messageBodyPart = new MimeBodyPart();
			/*messageBodyPart.setText("Hi "+recruitmentApplicant.applicantName +", \n\n"+mailContent.mailContent);*/
			messageBodyPart.setText(Body);
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
				if(fileName != null){
				
				MimeBodyPart attachPart = new MimeBodyPart();
				String attachFile = fileName;
				 
				DataSource source = new FileDataSource(attachFile);
				attachPart.setDataHandler(new DataHandler(source));
				attachPart.setFileName(new File(attachFile).getName());
				multipart.addBodyPart(attachPart);
				 
				//second attached file
				}
			MimeBodyPart attachPart1 = new MimeBodyPart();
			String attachFile1 = "conf/excel/Thrymr Software.pdf";
			
			DataSource source1 = new FileDataSource(attachFile1);
			attachPart1.setDataHandler(new DataHandler(source1));
			attachPart1.setFileName(new File(attachFile1).getName());
			multipart.addBodyPart(attachPart1);
			
			msg.setContent(multipart);
			Transport.send(msg);
			return true;
		}// try
		catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}// catch
	}
	
	public Result SendMailCalendarApplicant(Long Id) {
		try {
			
			DynamicForm form = Form.form().bindFromRequest();
			
			MailingDataBean mailingDataBean = Form.form(MailingDataBean.class).bindFromRequest().get();
			String subject = mailingDataBean.getSubjectName();
			String Body = mailingDataBean.getBodyName();
//			List<String> ToMailingList = mailingDataBean.getToMailingList();
			List<String> ToMailingList = new ArrayList<String>();
			for(AppUser appUser : getMailingList()){
				ToMailingList.add(appUser.getEmail());
			}
			
//			Logger.debug(mailingDataBean.toString());
			//Logger.debug("Body " +Body );
			RecruitmentMailContent recruitmentMailContent = new RecruitmentMailContent();
			recruitmentMailContent.mailContent = form.get("body");
			List<String> mailIdList = new ArrayList<String>();	
			RecruitmentSelectionRound recruitmentSelectionRound = RecruitmentSelectionRound.find.byId(Id);
			if(recruitmentSelectionRound != null){
				mailIdList.add(recruitmentSelectionRound.recruitmentApplicant.emailId);
				for(InterviewerAppUser appUser : recruitmentSelectionRound.interviewer){
					mailIdList.add(appUser.interviewer.getEmail());
				}
				for(String mailId : mailIdList){
					//SendMailCalendar(mailId,recruitmentSelectionRound);
				}
				File outputFileResume = null;
				String fileName = null;
				byte[] resume = recruitmentSelectionRound.recruitmentApplicant.resume;
				if(resume != null){
					fileName = "conf/excel/"+recruitmentSelectionRound.recruitmentApplicant.fileName;
					outputFileResume = new File(fileName);
					FileOutputStream outputStream = new FileOutputStream(outputFileResume);
					outputStream.write(resume);
					outputStream.close();
				}
				if(recruitmentSelectionRound.recruitmentApplicant.emailId != null){
					Set<String> mailIdsList = new HashSet<String>();
					mailIdsList.add(recruitmentSelectionRound.recruitmentApplicant.emailId);
					//SendMailCalendarCondidate(recruitmentSelectionRound.recruitmentApplicant.emailId,recruitmentSelectionRound,subject,Body);
					
					String calendar_subject_scheduled = "Interview Scheduled - Round : "+recruitmentSelectionRound.recruitmentInterviewType.interviewTypeName;
					String calendar_subject_reScheduled = "Interview Re-Scheduled - Round : "+recruitmentSelectionRound.recruitmentInterviewType.interviewTypeName;
					
					if(recruitmentSelectionRound.selectionStatus.equals(SelectionRoundStatus.Scheduled)){
						CalenderInviteOfInterviews.sendCalenderInviteOfInterviews(mailIdsList,recruitmentSelectionRound,fileName,calendar_subject_scheduled,Body,false);
					}else if(recruitmentSelectionRound.candidateCalendarEventId != null){
						CalenderInviteOfInterviews.updateExistingCalendarEvent(mailIdsList,recruitmentSelectionRound,calendar_subject_reScheduled,Body,false);
					}
				}
				if(recruitmentSelectionRound.interviewer != null){
					//SendMailCalendarInterviewer(recruitmentSelectionRound.interviewer,recruitmentSelectionRound,fileName);
					
					Set<String> mailIdsList = new HashSet<String>(ToMailingList);
					for(InterviewerAppUser interviewerUser : recruitmentSelectionRound.interviewer){
						mailIdsList.add(interviewerUser.interviewer.getEmail());
					}
					
//					Logger.debug(mailIdsList.toString());
					String jobName = "";
					String roleName = "";
					if(recruitmentSelectionRound.recruitmentApplicant.recruitmentJob != null){
						jobName = " - "+recruitmentSelectionRound.recruitmentApplicant.recruitmentJob.recruitmentCategory.jobCategoryName +" - ";
						roleName = recruitmentSelectionRound.recruitmentApplicant.recruitmentJob.recruitmentRole.jobRoleName;
					}
					SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy - hh:mm a");
					
					if(recruitmentSelectionRound.selectionStatus.equals(SelectionRoundStatus.Scheduled)){
						
						String calendar_subject_scheduled = "Interview Scheduled : "+recruitmentSelectionRound.recruitmentApplicant.applicationId+""+jobName+""+roleName;
						RecruitmentMailContent mailContent = RecruitmentMailContent.find.where().eq("mailType", MailType.Interview_Schedule_Email).findUnique();
						String description = "Hi, \n\n"+mailContent.mailContent+" \n\n "
									+ "Candidate Name : "+recruitmentSelectionRound.recruitmentApplicant.applicantName+" \n"
									+ " Job Position : "+jobName+""+roleName+" \n "
									+ "Date - Time : "+sf.format(recruitmentSelectionRound.conductDate)+" \n "
									+ "Venue : "+recruitmentSelectionRound.interviewVenue+" \n "
									+ "Round : "+recruitmentSelectionRound.recruitmentInterviewType.interviewTypeName+" \n\n "
									+ "Thanks & Regards \n HR Team \n "
									+ "Thrymr Software Pvt. Ltd. \n ";
						
						
						CalenderInviteOfInterviews.sendCalenderInviteOfInterviews(mailIdsList,recruitmentSelectionRound,fileName,calendar_subject_scheduled,description,true);
					}else if(recruitmentSelectionRound.interviewerCalendarEventId != null){
						
						String calendar_subject_reScheduled = "Interview Re-Scheduled : "+recruitmentSelectionRound.recruitmentApplicant.applicationId+""+jobName+""+roleName;
						RecruitmentMailContent mailContent = RecruitmentMailContent.find.where().eq("mailType", MailType.Interview_Re_Schedule_Email).findUnique();
						String description = "Hi "+recruitmentSelectionRound.recruitmentApplicant.applicantName +", \n\n"+mailContent.mailContent+" \n\n "
								+ "Candidate Name : "+recruitmentSelectionRound.recruitmentApplicant.applicantName+" \n"
								+ " Job Position : "+jobName+""+roleName+" \n "
								+ "Date - Time : "+sf.format(recruitmentSelectionRound.conductDate)+" \n "
								+ "Venue : "+recruitmentSelectionRound.interviewVenue+" \n "
								+ "Round : "+recruitmentSelectionRound.recruitmentInterviewType.interviewTypeName+" \n\n "
								+ "Thanks & Regards \n HR Team \n "
								+ "Thrymr Software Pvt. Ltd. \n ";
						
						CalenderInviteOfInterviews.updateExistingCalendarEvent(mailIdsList,recruitmentSelectionRound,calendar_subject_reScheduled,description,true);
					}
				}
				if(outputFileResume != null){
					outputFileResume.delete();
				}
				if(recruitmentSelectionRound.selectionStatus.equals(SelectionRoundStatus.Scheduled) && recruitmentSelectionRound.sendMailApplicantFlag.equals(false)){
					recruitmentSelectionRound.sendMailApplicantFlag = true;
					recruitmentSelectionRound.update();
				}else if(recruitmentSelectionRound.selectionStatus.equals(SelectionRoundStatus.ReScheduled) && recruitmentSelectionRound.sendMailInterviewerFlag.equals(false)){
					recruitmentSelectionRound.sendMailInterviewerFlag = true;
					recruitmentSelectionRound.update();
				}
				
				return ok("Successfully send Notification Event Interview "+recruitmentSelectionRound.selectionStatus+" to Applicant "+recruitmentSelectionRound.recruitmentApplicant.applicationId);
			}else{
				throw new Exception("");
			}
		}catch(Exception e){
			e.printStackTrace();
			return ok("Event Notification Not Successfully Send");
		}
	}
	
	public static String sendNotificationEventScheduled(RecruitmentSelectionRound recruitmentSelectionRound){
		return "Successfully send Notification Event Interview Scheduled to Applicant "+recruitmentSelectionRound.recruitmentApplicant.applicationId;
	}
	
	public static String sendNotificationEventReScheduled(RecruitmentSelectionRound recruitmentSelectionRound){
		return "Successfully send Notification Event Interview Re-Scheduled to Applicant "+recruitmentSelectionRound.recruitmentApplicant.applicationId;
	}
	
	public Result sendNotificationEventCancelled(Long Id){
		
		RecruitmentSelectionRound recruitmentSelectionRound = RecruitmentSelectionRound.find.byId(Id);
		
		if(recruitmentSelectionRound.recruitmentApplicant.emailId != null && recruitmentSelectionRound.candidateCalendarEventId != null){
			if(recruitmentSelectionRound.selectionStatus.equals(SelectionRoundStatus.Cancelled)){
				CalenderInviteOfInterviews.deleteCalendarEvent(recruitmentSelectionRound.candidateCalendarEventId, null);
			}
		}
		
		if(recruitmentSelectionRound.interviewer != null && recruitmentSelectionRound.interviewerCalendarEventId != null){
			if(recruitmentSelectionRound.selectionStatus.equals(SelectionRoundStatus.Cancelled)){
				CalenderInviteOfInterviews.deleteCalendarEvent(recruitmentSelectionRound.interviewerCalendarEventId, recruitmentSelectionRound.googleDriveFileId);
			}
		}
		
		if(recruitmentSelectionRound.selectionStatus.equals(SelectionRoundStatus.Cancelled) && recruitmentSelectionRound.sendNotificationFlag.equals(false)){
			recruitmentSelectionRound.sendNotificationFlag = true;
			recruitmentSelectionRound.update();
		}
		
		return ok("Successfully send Notification Event Cancelled to Applicant "+recruitmentSelectionRound.recruitmentApplicant.applicationId);
	}
	
	public  void SendMailCalendarCondidate(String mailId,RecruitmentSelectionRound recruitmentSelectionRound,String Subject,String Body) {
		// mail properties outgoing server (gmail.com)
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "465");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");

		try {
			
//			CalenderInviteOfInterviews.sendCalenderInviteOfInterviews(mailId,0,recruitmentSelectionRound);
			
			// create Session obj
			Authenticator auth = new SMTPAuthenticator();

			Session session = Session.getInstance(props, auth);

			// prepare mail msg
			MimeMessage msg = new MimeMessage(session);
			msg.setSubject(Subject);
			msg.setText(Body);
			// set header values
			/*String jobName = "";
			String roleName = "";
			if(recruitmentSelectionRound.recruitmentApplicant.recruitmentJob != null){
				jobName = recruitmentSelectionRound.recruitmentApplicant.recruitmentJob.recruitmentCategory.jobCategoryName +" - ";
				roleName = recruitmentSelectionRound.recruitmentApplicant.recruitmentJob.recruitmentRole.jobRoleName;
			}
			SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy - hh:mm a");
			if(recruitmentSelectionRound.selectionStatus.equals(SelectionRoundStatus.Scheduled)){
				msg.setSubject(" Interview scheduled with Thrymr Software");
				RecruitmentMailContent mailContent = RecruitmentMailContent.find.where().eq("mailType", MailType.Schedule_Email).findUnique();
				msg.setText("Hi "+recruitmentSelectionRound.recruitmentApplicant.applicantName +", \n\n"+mailContent.mailContent+" \n\n "
							+ "Job Position : "+jobName+""+roleName+" \n "
							+ "Date - Time : "+sf.format(recruitmentSelectionRound.conductDate)+" \n "
							//+ "Time: "+recruitmentSelectionRound.conductDate+" \n\n "
							+ "Venue: "+recruitmentSelectionRound.interviewVenue+" \n "
							+ "Round: "+recruitmentSelectionRound.recruitmentInterviewType.interviewTypeName+" \n\n "
									+ "Looking forward to have you with us, \n\n"
									+ " Thanks & Regards \n HR Team \n\n "
									+ "Thrymr Software Pvt. Ltd. \n "
									+ "5th Floor, Kapil Towers \n "
									+ "Beside ICICI Bank Building on ISB Main Road \n "
									+ "Nanakramguda, Financial District \n "
									+ "Gachibowli, Hyderabad \n "
									+ "64550008 / 65890009");
			}else{
				msg.setSubject("Interview Re-Scheduled with Thrymr Software");
				RecruitmentMailContent mailContent = RecruitmentMailContent.find.where().eq("mailType", MailType.Re_Schedule_Email).findUnique();
				msg.setText("Hi "+recruitmentSelectionRound.recruitmentApplicant.applicantName +", \n\n"+mailContent.mailContent+" \n\n "
						+ "Job Position : "+jobName+""+roleName+" \n "
						+ "Date - Time : "+sf.format(recruitmentSelectionRound.conductDate)+" \n "
						//+ "Time: "+recruitmentSelectionRound.conductDate+" \n\n "
						+ "Venue: "+recruitmentSelectionRound.interviewVenue+" \n "
						+ "Round: "+recruitmentSelectionRound.recruitmentInterviewType.interviewTypeName+" \n\n "
						+ "Looking forward to have you with us, \n\n"
									+ "Thanks & Regards \n HR Team \n\n "
									+ "Thrymr Software Pvt. Ltd. \n "
									+ "5th Floor, Kapil Towers \n "
									+ "Beside ICICI Bank Building on ISB Main Road \n "
									+ "Nanakramguda, Financial District \n "
									+ "Gachibowli, Hyderabad \n "
									+ "64550008 / 65890009");
			}*/
			
			msg.setFrom(new InternetAddress(Constants.EMAIL_HR_USERNAME));
			/*InternetAddress[] myCcList = new InternetAddress[(mailIds.size())];
            for(int i=0;i<mailIds.size();i++){
				myCcList[i] = new InternetAddress(mailIds.get(i));
			}
            
            msg.setRecipients(Message.RecipientType.TO, myCcList);*/
			
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(mailId));
            
            List<AppUser> activeHRs = getMailingList();
			if(!activeHRs.isEmpty()){
				InternetAddress[] myCcListHr = new InternetAddress[(activeHRs.size())];
					for(int i=0;i<activeHRs.size();i++){
						myCcListHr[i] = new InternetAddress(activeHRs.get(i).getEmail());
					}
						msg.setRecipients(Message.RecipientType.CC, myCcListHr);
			}
			
		
			Transport.send(msg);
		}// try
		catch (Exception ex) {
			ex.printStackTrace();
		}// catch
	}
	
	public  void SendMailCalendarInterviewer(List<InterviewerAppUser> mailIdList,RecruitmentSelectionRound recruitmentSelectionRound,String resumeName) {
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
			String jobName = "";
			String roleName = "";
			if(recruitmentSelectionRound.recruitmentApplicant.recruitmentJob != null){
				jobName = " - "+recruitmentSelectionRound.recruitmentApplicant.recruitmentJob.recruitmentCategory.jobCategoryName +" - ";
				roleName = recruitmentSelectionRound.recruitmentApplicant.recruitmentJob.recruitmentRole.jobRoleName;
			}
			
			// set header values
			SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy - hh:mm a");
			if(recruitmentSelectionRound.selectionStatus.equals(SelectionRoundStatus.Scheduled)){
				msg.setSubject("Interview Invite : "+recruitmentSelectionRound.recruitmentApplicant.applicationId+""+jobName+""+roleName);
				RecruitmentMailContent mailContent = RecruitmentMailContent.find.where().eq("mailType", MailType.Interview_Schedule_Email).findUnique();
				BodyPart messageBodyPart = new MimeBodyPart();
				messageBodyPart.setText("Hi, \n\n"+mailContent.mailContent+" \n\n "
							+ "Candidate Name : "+recruitmentSelectionRound.recruitmentApplicant.applicantName+" \n"
							+ " Job Position : "+jobName+""+roleName+" \n "
							+ "Date - Time : "+sf.format(recruitmentSelectionRound.conductDate)+" \n "
							//+ "Time: "+recruitmentSelectionRound.conductDate+" \n\n "
							+ "Venue : "+recruitmentSelectionRound.interviewVenue+" \n "
							+ "Round : "+recruitmentSelectionRound.recruitmentInterviewType.interviewTypeName+" \n\n "
							+ "Thanks & Regards \n HR Team \n "
							+ "Thrymr Software Pvt. Ltd. \n ");
			
				Multipart multipart = new MimeMultipart();
				multipart.addBodyPart(messageBodyPart);
				if(resumeName != null){
				
				MimeBodyPart attachPart = new MimeBodyPart();
				String attachFile = resumeName;
				 
				DataSource source = new FileDataSource(attachFile);
				attachPart.setDataHandler(new DataHandler(source));
				attachPart.setFileName(new File(attachFile).getName());
				multipart.addBodyPart(attachPart);
				}
				msg.setContent(multipart);
			}else{
				msg.setSubject("Interview Invite : "+recruitmentSelectionRound.recruitmentApplicant.applicationId+"-"+jobName+"-"+roleName);
				RecruitmentMailContent mailContent = RecruitmentMailContent.find.where().eq("mailType", MailType.Interview_Re_Schedule_Email).findUnique();
				BodyPart messageBodyPart = new MimeBodyPart();
				messageBodyPart.setText("Hi "+recruitmentSelectionRound.recruitmentApplicant.applicantName +", \n\n"+mailContent.mailContent+" \n\n "
						+ "Candidate Name : "+recruitmentSelectionRound.recruitmentApplicant.applicantName+" \n"
						+ " Job Position : "+jobName+""+roleName+" \n "
						+ "Date - Time : "+sf.format(recruitmentSelectionRound.conductDate)+" \n "
						//+ "Time: "+recruitmentSelectionRound.conductDate+" \n\n "
						+ "Venue : "+recruitmentSelectionRound.interviewVenue+" \n "
						+ "Round : "+recruitmentSelectionRound.recruitmentInterviewType.interviewTypeName+" \n\n "
						+ "Thanks & Regards \n HR Team \n "
						+ "Thrymr Software Pvt. Ltd. \n ");
				
				Multipart multipart = new MimeMultipart();
				multipart.addBodyPart(messageBodyPart);
				if(resumeName != null){
				
				MimeBodyPart attachPart = new MimeBodyPart();
				String attachFile = resumeName;
				 
				DataSource source = new FileDataSource(attachFile);
				attachPart.setDataHandler(new DataHandler(source));
				attachPart.setFileName(new File(attachFile).getName());
				multipart.addBodyPart(attachPart);
				}
				msg.setContent(multipart);
			}
			
			msg.setFrom(new InternetAddress(Constants.EMAIL_HR_USERNAME));
			InternetAddress[] myCcList = new InternetAddress[(mailIdList.size())];
            for(int i=0;i<mailIdList.size();i++){
				myCcList[i] = new InternetAddress(mailIdList.get(i).interviewer.getEmail());
			}
            
            msg.setRecipients(Message.RecipientType.TO, myCcList);
			
			//msg.addRecipient(Message.RecipientType.TO, new InternetAddress(mailId));
            
            List<AppUser> activeHRs = getMailingList();
			if(!activeHRs.isEmpty()){
				InternetAddress[] myCcListHr = new InternetAddress[(activeHRs.size())];
					for(int i=0;i<activeHRs.size();i++){
						myCcListHr[i] = new InternetAddress(activeHRs.get(i).getEmail());
					}
						msg.setRecipients(Message.RecipientType.CC, myCcListHr);
			}
			
		
			Transport.send(msg);
		}// try
		catch (Exception ex) {
			ex.printStackTrace();
		}// catch
	}
	
	public Result previewMailContent(Long Id) {
		Map<String,String> StringList = new LinkedHashMap<String,String>();
		SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy - hh:mm a");
		RecruitmentSelectionRound recruitmentSelectionRound = RecruitmentSelectionRound.find.byId(Id);
		String jobName = "";
		String roleName = "";
		String sub = "";
		String body = "";
		if(recruitmentSelectionRound.recruitmentApplicant.recruitmentJob != null){
			jobName = " - "+recruitmentSelectionRound.recruitmentApplicant.recruitmentJob.recruitmentCategory.jobCategoryName +" - ";
			roleName = recruitmentSelectionRound.recruitmentApplicant.recruitmentJob.recruitmentRole.jobRoleName;
		}
		if(recruitmentSelectionRound.selectionStatus.equals(SelectionRoundStatus.Scheduled)){
		sub = "Interview scheduled with Thrymr Software";
		RecruitmentMailContent mailContent = RecruitmentMailContent.find.where().eq("mailType", MailType.Schedule_Email).findUnique();
		body = "Hi "+recruitmentSelectionRound.recruitmentApplicant.applicantName +", \n\n"+mailContent.mailContent+" \n\n "
				+ "Job Position : "+jobName+""+roleName+" \n "
				+ "Date - Time : "+sf.format(recruitmentSelectionRound.conductDate)+" \n "
				//+ "Time: "+recruitmentSelectionRound.conductDate+" \n\n "
				+ "Venue: "+recruitmentSelectionRound.interviewVenue+" \n "
				+ "Round: "+recruitmentSelectionRound.recruitmentInterviewType.interviewTypeName+" \n\n "
						+ "Looking forward to have you with us, \n\n"
						+ " Thanks & Regards \n HR Team \n\n "
						+ "Thrymr Software Pvt. Ltd. \n "
						+ "5th Floor, Kapil Towers \n "
						+ "Beside ICICI Bank Building on ISB Main Road \n "
						+ "Nanakramguda, Financial District \n "
						+ "Gachibowli, Hyderabad \n "
						+ "64550008 / 65890009";
		}else{
			sub = "Interview Re-Scheduled with Thrymr Software";
			RecruitmentMailContent mailContent = RecruitmentMailContent.find.where().eq("mailType", MailType.Re_Schedule_Email).findUnique();
			body = "Hi "+recruitmentSelectionRound.recruitmentApplicant.applicantName +", \n\n"+mailContent.mailContent+" \n\n "
						+ "Job Position : "+jobName+""+roleName+" \n "
						+ "Date - Time : "+sf.format(recruitmentSelectionRound.conductDate)+" \n "
						//+ "Time: "+recruitmentSelectionRound.conductDate+" \n\n "
						+ "Venue: "+recruitmentSelectionRound.interviewVenue+" \n "
						+ "Round: "+recruitmentSelectionRound.recruitmentInterviewType.interviewTypeName+" \n\n "
								+ "Looking forward to have you with us, \n\n"
								+ " Thanks & Regards \n HR Team \n\n "
								+ "Thrymr Software Pvt. Ltd. \n "
								+ "5th Floor, Kapil Towers \n "
								+ "Beside ICICI Bank Building on ISB Main Road \n "
								+ "Nanakramguda, Financial District \n "
								+ "Gachibowli, Hyderabad \n "
								+ "64550008 / 65890009";
		}
		StringList.put("Subject", sub);
		StringList.put("Body", body);
		//Logger.info("subject = " + body);
		return ok(Json.toJson(StringList));
		
	}
	
	public Result previewIntroMailContent(Long Id) {
		Map<String,String> StringList = new LinkedHashMap<String,String>();
		SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy - hh:mm a");
		RecruitmentApplicant recruitmentApplicant = RecruitmentApplicant.find.byId(Id);
		String sub = "Hello from Thrymr Software!!!";
		RecruitmentMailContent mailContent = RecruitmentMailContent.find.where().eq("mailType", MailType.Intro_Email).findUnique();
		String body = "Hi "+recruitmentApplicant.applicantName +", \n\n"+mailContent.mailContent;
		StringList.put("Subject", sub);
		StringList.put("Body", body);
		//Logger.info("subject = " + body);
		return ok(Json.toJson(StringList));
	}
	
	/*public static String SendMailCalendar(List<String> mailIds,Date eventDate){
		
		int sequence = 1;
		String method = null;
		switch(sequence){
			case 0: 
				method = "REQUEST";
				break;
			case 1:
				method = "REQUEST";
				break;
		}
		
		try {
			 String from = Constants.EMAIL_USERNAME;
	            
            Properties props = new Properties();
    		props.put("mail.smtp.host", "smtp.gmail.com");
    		props.put("mail.smtp.port", "465");
    		props.put("mail.smtp.auth", "true");
    		props.put("mail.smtp.socketFactory.class",
    				"javax.net.ssl.SSLSocketFactory");

    		Authenticator auth = new SMTPAuthenticator();
            Session session = Session.getDefaultInstance(props, auth);

            // Define message
            MimeMessage message = new MimeMessage(session);
            message.addHeaderLine("method=" + method);
            message.addHeaderLine("charset=UTF-8");
            message.addHeaderLine("component=VEVENT");

            message.setSubject("Thrymr Software - Interview Schedule");
			
            message.setText("Thrymr Software - Interview Schedule");
            
            message.setFrom(new InternetAddress(from));
            
            InternetAddress[] myCcList = new InternetAddress[(mailIds.size())];
            for(int i=0;i<mailIds.size();i++){
				myCcList[i] = new InternetAddress(mailIds.get(i));
			}
            
            message.setRecipients(Message.RecipientType.TO, myCcList);
            message.setSubject("Interview Event");

            StringBuffer sb = new StringBuffer();


            String vevent = getEvent(sequence, Constants.EMAIL_USERNAME, method,eventDate);

            StringBuffer buffer = sb.append(vevent);


            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Fill the message
            messageBodyPart.setHeader("Content-Type", "text/calendar; charset=UTF-8; method=REQUEST");

            messageBodyPart.setHeader("Content-Class", "urn:content-classes:calendarmessage");
            messageBodyPart.setHeader("Content-ID", "calendar_message");
            messageBodyPart.setDataHandler(new DataHandler( new ByteArrayDataSource(buffer.toString(), "text/calendar")));// very important

            // Create a Multipart
            Multipart multipart = new MimeMultipart();

            // Add part one
            multipart.addBodyPart(messageBodyPart);

            // Put parts in message
            message.setContent(multipart);

            // send message
            Transport.send(message);
            System.out.println("Done");
            return "Calendar Event Successfully Send";
        } catch (MessagingException me) {
            me.printStackTrace();
            return "Alrerady Send Same Data and Time Calendar Event.";
        } catch (Exception ex) {
            ex.printStackTrace();
            return "Alrerady Send Same Data and Time Calendar Event.";
        }
	}
	
	public static String getEvent(int sequence, String uid, String method,Date eventDate){
		ICalendar icals = new ICalendar();
		
		icals.setMethod(method);
		icals.setCalendarScale(CalendarScale.gregorian());
		
        VEvent event = new VEvent();
        event.setUid(uid);
        event.setStatus(biweekly.property.Status.confirmed());
        event.setDescription("Meeting");
        event.setSummary("Interview");//type
        event.setOrganizer("developer@thrymr.net");
        event.setLocation("Office room");
        event.setTransparency(Transparency.opaque());
        
        
        event.setCreated(eventDate);
        event.setLastModified(eventDate);
        
        event.setSequence(sequence);
       

        Calendar start = Calendar.getInstance();
        start.setTime(eventDate);
        start.add(Calendar.HOUR_OF_DAY, sequence + 2);
        Calendar end = Calendar.getInstance();
        end.setTime(eventDate);
        end.add(Calendar.HOUR_OF_DAY, sequence + 2);

        event.setDateStart(eventDate);
        event.setDateEnd(end.getTime());
        icals.addEvent(event);

        WriterChainText text = Biweekly.write(icals);
        String result = text.go(); 
        
        
        System.out.println(result);
        
        return result;
	}
*/
	
	
	public Result jobOpening(){
		List<RecruitmentJob> jobOpeningList = RecruitmentJob.find.where().eq("jobStatus", JobStatus.Open).findList();
		return ok(views.html.recruitment.jobOpening.render(jobOpeningList,true,null,null));
	}
	
	public Result jobReference(Long jobId){
		RecruitmentJob recruitmentJob = RecruitmentJob.find.byId(jobId);
		return ok(views.html.recruitment.jobOpening.render(null,false,recruitmentJob,null));
	}
	
	public Result addReference(){
		
		RecruitmentReferenceBean jobRBean = Form.form(RecruitmentReferenceBean.class).bindFromRequest().get();
		MultipartFormData body = request().body().asMultipartFormData();
		RecruitmentReference recruitmentReference = new RecruitmentReference();
		try {
			File outputFile = null;
			String filePaht = null;
			FilePart candidateResume = body.getFile("resume");
			if (candidateResume != null) {
				String filename = candidateResume.getFilename();
				filePaht = "conf/excel/reference"+filename;
				recruitmentReference.resumeContentTyep = candidateResume.getContentType();
				recruitmentReference.resumeName = candidateResume.getFilename();
				recruitmentReference.resume = Files.toByteArray(candidateResume.getFile());
				
				outputFile = new File(filePaht);
				FileOutputStream outputStream = new FileOutputStream(outputFile);
				outputStream.write(recruitmentReference.resume);
				
			}
			recruitmentReference.candidateName = jobRBean.candidateName;
			recruitmentReference.candidateEmail = jobRBean.candidateEmail;
			recruitmentReference.experience = jobRBean.experience;
			recruitmentReference.referedBy = Application.getLoggedInUser();
			recruitmentReference.recruitmentJob = RecruitmentJob.find.byId(jobRBean.jobId);
			recruitmentReference.save();
			
			sendReferralMailToHR(jobRBean,filePaht);
			if(outputFile != null){
				outputFile.delete();
			}
			flash().put(
					"alert",
					new Alert("alert-success", "Your Reference Successfully Done!").toString());
		}catch(Exception e){
			e.printStackTrace();
		}
		return redirect(routes.RecruitmentController.jobOpening());
	}
	
	
	public Result deleteReference(Long Id){
		RecruitmentReference.find.byId(Id).delete();
		flash().put(
				"alert",
				new Alert("alert-success", "Your Reference Successfully Deleted!").toString());
		return redirect(routes.RecruitmentController.jobReferenceEmpHistory());
	}
	
	
	public Result jobReferenceEmpHistory() {
		List<RecruitmentReference> referenceEmpHistory = RecruitmentReference.find.where().eq("referedBy", Application.getLoggedInUser()).findList();
		return ok(views.html.recruitment.jobOpening.render(null,false,null,referenceEmpHistory));
	}
	
	public Result jobReferralHistory(){
		List<RecruitmentReference> referenceEmpHistory = RecruitmentReference.find.all();
		return ok(views.html.recruitment.jobReferals.render(referenceEmpHistory));
	}
	
	public Result jobByReferralHistory(Long jobId){
		List<RecruitmentReference> referenceEmpHistory = RecruitmentReference.find.where().eq("recruitmentJob", RecruitmentJob.find.byId(jobId)).findList();
		return ok(views.html.recruitment.jobReferals.render(referenceEmpHistory));
	}
	
	public Result downloadReferralResume(Long id) {
		RecruitmentReference recruitmentReference = RecruitmentReference.find.byId(id);
		String uploadFileContentType = recruitmentReference.resumeContentTyep;
		String uploadFileName = recruitmentReference.resumeName;
		byte[] uploadFile = recruitmentReference.resume;
		response().setContentType("APPLICATION/OCTET-STREAM");
		response().setHeader("Content-Disposition",
				"attachment; filename=\"" + uploadFileName + "\"");
		return ok(uploadFile).as(uploadFileContentType);

	}
	
	public  void sendReferralMailToHR(RecruitmentReferenceBean jobRBean,String filePaht) {
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
			AppUser appUser = Application.getLoggedInUser();
			RecruitmentJob recruitmentJob = RecruitmentJob.find.byId(jobRBean.jobId);
			msg.setSubject("Referal Submitted : "+recruitmentJob.jobId+" - "+appUser.getEmployeeId());
			msg.setFrom(new InternetAddress(Constants.EMAIL_HR_USERNAME));
			
			 List<AppUser> activeHRs = getMailingList();
				if(!activeHRs.isEmpty()){
					InternetAddress[] myCcListHr = new InternetAddress[(activeHRs.size())];
						for(int i=0;i<activeHRs.size();i++){
							myCcListHr[i] = new InternetAddress(activeHRs.get(i).getEmail());
						}
							msg.setRecipients(Message.RecipientType.TO, myCcListHr);
				}
			
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText("Team HR, \n\n"
									+""+appUser.getFullName()+" ("+appUser.getEmployeeId()+") has made a referral for below job opening. PFA resume of the candidate. \n\n"
									+ "Job ID : "+recruitmentJob.jobId+"\n"
									+ "Category - Role : "+recruitmentJob.recruitmentCategory.jobCategoryName +" - "+recruitmentJob.recruitmentRole.jobRoleName+"\n\n "
									+ "Thanks, \n BB8 Team.\n");
			
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			
			if(filePaht != null){
				MimeBodyPart attachPart = new MimeBodyPart();
				String attachFile = filePaht;
				 
				DataSource source = new FileDataSource(attachFile);
				attachPart.setDataHandler(new DataHandler(source));
				attachPart.setFileName(new File(attachFile).getName());
				multipart.addBodyPart(attachPart);
			}
			msg.setContent(multipart);
			
			if(!activeHRs.isEmpty()){
				Transport.send(msg);
			}
		}// try
		catch (Exception ex) {
			ex.printStackTrace();
		}// catch

	}
	
	public Result MailingList(){
		Role roleHr = Role.find.where().eq("role", Roles.HR.toString()).findUnique();
		List<AppUser> appUserHrs = AppUser.find.where().eq("status",UserProjectStatus.Active).in("role", roleHr).findList();
		List<MailingList> mailingList = MailingList.find.all();
		List<AppUser> mailingAPList = new ArrayList<AppUser>();
		for (MailingList mailingList2 : mailingList) {
			mailingAPList.add(mailingList2.appUser);
		}
		return ok(views.html.recruitment.mailingList.render(appUserHrs,mailingAPList));
	}
	
	public Result MailingListSubmission(){
		MailingListBean jobRBean = Form.form(MailingListBean.class).bindFromRequest().get();
		Ebean.delete(MailingList.find.all());
		for (Long id : jobRBean.appUsersidList) {
			if(id != null){
				MailingList mailingList = new MailingList();
				mailingList.appUser = AppUser.find.byId(id);
				mailingList.save();
			}
		}
		flash().put(
				"alert",
				new Alert("alert-success", "Mailing List updated successfully!").toString());
		return redirect(routes.RecruitmentController.MailingList());
	}
	
	public  void sendInterviewerFeedBack(RecruitmentInterviewerFeedback rmIvFb) {
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
			AppUser appUser = Application.getLoggedInUser();
//			RecruitmentJob recruitmentJob = RecruitmentJob.find.byId(jobRBean.jobId);
			msg.setSubject(rmIvFb.recruitmentApplicant.applicationId+"- Feedback Submitted -"+appUser.getEmployeeId());
			msg.setFrom(new InternetAddress(Constants.EMAIL_HR_USERNAME));
			
			 List<AppUser> activeHRs = getMailingList();
				if(!activeHRs.isEmpty()){
					InternetAddress[] myCcListHr = new InternetAddress[(activeHRs.size())];
						for(int i=0;i<activeHRs.size();i++){
							myCcListHr[i] = new InternetAddress(activeHRs.get(i).getEmail());
						}
							msg.setRecipients(Message.RecipientType.TO, myCcListHr);
				}
				
			String body = "Team HR, <br><br> "
						+ "<table>"
						+ "<tr><td>Applicant Name</td><td> :&nbsp; "+rmIvFb.recruitmentApplicant.applicantName+"</td></tr>"
						+ "<tr><td>Interview Round</td><td> :&nbsp; "+rmIvFb.recruitmentSelectionRound.recruitmentInterviewType.interviewTypeName+"</td></tr>"
						+ "<tr><td>Date - Time</td><td> :&nbsp; "+new SimpleDateFormat("dd-MMM-yyyy - hh:mm a").format( rmIvFb.recruitmentSelectionRound.conductDate)+"</td></tr>"
						+ "<tr><td>Remarks</td><td> :&nbsp; "+rmIvFb.remark+"</td></tr>"
						+ "<tr><td>FeedBack</td><td> :&nbsp; "+rmIvFb.feedBack+"</td></tr>"
						+ "<tr><td></td><td></td></tr>"
						+ "<tr><td>Interviewer Name</td><td> :&nbsp; "+appUser.getFullName()+" ( "+appUser.getEmployeeId()+" )</td></tr>"
						+ "</table> <br><br>"
						+ "Thanks, <br> BB8 Team.\n";

			msg.setContent(body,"text/html");
			
			if(!activeHRs.isEmpty()){
				Transport.send(msg);
			}
		}// try
		catch (Exception ex) {
			ex.printStackTrace();
		}// catch

	}
}
