package controllers;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Alert;
import models.AppUser;
import models.Role;
import models.lead.ClientContactNo;
import models.lead.Company;
import models.lead.CompanyContactInfo;
import models.lead.CompanyContacts;
import models.lead.ContactType;
import models.lead.Lead;
import models.lead.LeadChatComment;
import models.lead.LeadContactInfo;
import models.lead.LeadStatus;
import models.lead.LeadSummary;
import models.lead.StoreFile;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import action.AdminMarketAnnotation;
import action.BasicAuth;
import bean.CompanyBean;
import bean.CompanyContactBean;
import bean.LeadBean;
import bean.LeadCommentBean;

import com.google.common.io.Files;


public class AdminLeadController extends Controller{
	
	/* Admin & Marketing Lead Module validations */
	
	public Result isExistCompanyName() {
    	DynamicForm requestForm = Form.form().bindFromRequest();
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        String id = requestForm.get("id");
        try{
            if(id != null && !id.isEmpty()) {
                map.put("valid", !(Company.find.where().ieq("companyName", requestForm.get("companyName").trim()).ne("id", Long.parseLong(id)).findRowCount() > 0));
            } else {
            	map.put("valid", !((Company.find.where().ieq("companyName", requestForm.get("companyName").trim()).findList().size()) > 0));
            }
        }catch(Exception e){
            map.put("valid",false);
        }
        return ok(Json.toJson(map));
    }
    
    public Result isExistWebsiteName() {
    	DynamicForm requestForm = Form.form().bindFromRequest();
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        String id = requestForm.get("id");
        try{
            if(id != null && !id.isEmpty()) {
                map.put("valid", !(Company.find.where().ieq("website", requestForm.get("website").trim()).ne("id", Long.parseLong(id)).findRowCount() > 0));
            } else {
            	map.put("valid", !((Company.find.where().ieq("website", requestForm.get("website").trim()).findList().size()) > 0));
            }
        }catch(Exception e){
            map.put("valid",false);
        }
        return ok(Json.toJson(map));
    }
    
    public Result isExistContactName() {
    	DynamicForm requestForm = Form.form().bindFromRequest();
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        String id = requestForm.get("id");
        try{
            if(id != null && !id.isEmpty()) {
                map.put("valid", !(CompanyContacts.find.where().ieq("contactName", requestForm.get("contactName").trim()).ne("id", Long.parseLong(id)).findRowCount() > 0));
            } else {
            	map.put("valid", !((CompanyContacts.find.where().ieq("contactName", requestForm.get("contactName").trim()).findList().size()) > 0));
            }
        }catch(Exception e){
            map.put("valid",false);
        }
        return ok(Json.toJson(map));
    }
    
    public Result isExistLeadStatus(){
    	DynamicForm requestForm = Form.form().bindFromRequest();
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        String id = requestForm.get("id");
        try{
            if(id != null && !id.isEmpty()) {
                map.put("valid", !(LeadStatus.find.where().ieq("status", requestForm.get("status").trim()).ne("id", Long.parseLong(id)).findRowCount() > 0));
            } else {
            	map.put("valid", !((LeadStatus.find.where().ieq("status", requestForm.get("status").trim()).findList().size()) > 0));
            }
        }catch(Exception e){
            map.put("valid",false);
        }
        return ok(Json.toJson(map));
    }
    
    
   
    /* =========================================================================================    */
    
    public static void getCompanyContacts(CompanyBean companyBean,Company company){
    	for (int i = 0; i < companyBean.cid.size(); i++) {
			if(companyBean.cid.get(i) != null) {
				CompanyContacts contact = CompanyContacts.find.byId(companyBean.cid.get(i));
				if(contact != null ) {
					CompanyContactInfo companyContactInfo = new CompanyContactInfo();
					companyContactInfo.Company = company;
					companyContactInfo.companyContacts = contact;
					companyContactInfo.jobTitle = companyBean.jobTitle.get(i);
					companyContactInfo.save();
				}
			} 
		}
    }
    
    public static Boolean getFlag(List<Long> ids) {
    	Boolean flag = false;
    	if(ids.size() > 1){
	    	for(int i=0;i<ids.size();i++) {
	    		int count = 0;
				for(int j=0;j<ids.size();j++){
					if(ids.get(i).equals(ids.get(j))){
						count++;
						if(count >= 2) {
				    		flag = true;
				    	}
					}
				}
			}
    	}
		return flag;
    }
    
    //Add Company
    @AdminMarketAnnotation
    @BasicAuth
    public Result addCompany() {
		Company company = Form.form(Company.class).bindFromRequest().get();
		CompanyBean companyBean = Form.form(CompanyBean.class).bindFromRequest().get();
		//Logger.debug("sadbkdjk123");
			try {
				if(companyBean.cid != null && !companyBean.cid.isEmpty()){
					if(getFlag(companyBean.cid)){
						flash().put(
								"alert",
								new Alert("alert-danger", "Duplicate Contacts not allowed").toString());
						return redirect(routes.staticController.getAddCompany());
					}
				}
				company.save();
				Company company1 = Company.find.where().eq("companyName",company.companyName).findUnique();
				if(companyBean.cid != null && !companyBean.cid.isEmpty()){
				 getCompanyContacts(companyBean,company1);
				}
				
				
				if(companyBean.leadId == 1){
					//session("COMID","");
					Map<Long, String> map = new HashMap<Long, String>();
					Long id = Long.parseLong(Company.find.orderBy("id desc").findIds().get(0).toString());
					Company companyLead = Company.find.byId(id);
					map.put(id, companyLead.getCompanyName());
					return ok(Json.toJson(map));
					//return ok(views.html.leads.selectAddCompany.render(Company.find.all(),id));
	    		}else{
	    			flash().put(
							"alert",
							new Alert("alert-success", company.companyName
									+ " Successfully Added").toString());
	    			return redirect(routes.staticController.getAddCompany());
	    		}
			} catch(Exception e) {
				e.printStackTrace();
				/*flash().put(
						"alert",
						new Alert("alert-danger", "Data Not Stored , Exception occured ").toString());*/
				if(companyBean.leadId == 1){
					Map<Long, String> map = new HashMap<Long, String>();
					Long id = Long.parseLong(Company.find.orderBy("id desc").findIds().get(0).toString());
					Company companyLead = Company.find.byId(id);
					map.put(id, companyLead.getCompanyName());
					return ok(Json.toJson(map));
					//return ok(views.html.leads.selectAddCompany.render(Company.find.all(),id));
	    		}else{
	    			return redirect(routes.staticController.getAddCompany());
	    		}
			}
		
	}// Model.Finder(Long.class,Role.class).orderBy("id desc").findIds().get(0);

    //Update Company
    @AdminMarketAnnotation
    @BasicAuth
    public Result updateCompany() {
    	Company company1 = Form.form(Company.class).bindFromRequest().get();
    	CompanyBean companyBean = Form.form(CompanyBean.class).bindFromRequest().get();
    	try {
	    	if(company1.id != null) {
	    		Company company = Company.find.byId(company1.id);
				if( companyBean.cid != null && !companyBean.cid.isEmpty()){
					if(getFlag(companyBean.cid)){
						flash().put(
								"alert",
								new Alert("alert-danger", "Duplicate Contacts not allowed  ").toString());
						return redirect(routes.staticController.getEditCompany(company.id));
					}
					List<CompanyContactInfo> listContacts =new ArrayList<CompanyContactInfo>();
					listContacts  = CompanyContactInfo.find.where().eq("company_id",company.id).findList();
		    		if(!listContacts.isEmpty()) {
			    		for(CompanyContactInfo contact : listContacts){
			    			contact.delete();
			    		}
		    		}
				}
				if( companyBean.cid != null && !companyBean.cid.isEmpty()){
					getCompanyContacts(companyBean,company);
				}
				company.setCompanyName(company1.companyName);
				company.setAddress(company1.address);
				company.setWebsite(company1.website);
				company.update();
	    		flash().put(
						"alert",
						new Alert("alert-success", company.companyName
								+ " Successfully Updated").toString());
	    	}
    	}catch(Exception e) {
    		e.printStackTrace();
    		flash().put(
					"alert",
					new Alert("alert-danger", "Data Not Updated , Exception occured ").toString());
    	}
    	
    	return redirect(routes.staticController.getAllCompany());
    }
    
    @AdminMarketAnnotation
    @BasicAuth
    public Result deleteCompanyContact(Long cpId , Long ctId) {
    	CompanyContactInfo companyContactInfo = CompanyContactInfo.find.byId(ctId);
    	companyContactInfo.delete();
    	return redirect(routes.staticController.getEditCompany(cpId));
    	
    }
    
    
    @AdminMarketAnnotation
    @BasicAuth
    public Result getDuplicateJobTitle(Long cpId , Long ctId) {
    	String flag = "";
    	CompanyContactInfo info = CompanyContactInfo.find.where().eq("company_id",cpId).eq("company_Contacts_id", ctId).findUnique();
    	if(info != null) {
    		CompanyContacts contact = CompanyContacts.find.byId(ctId);
    		Company company = Company.find.byId(cpId);
    		//flag = contact.contactName+" is at "+ info.jobTitle +" position in "+company.getCompanyName()+". Do you want to update the job?";
    		if(info.jobTitle != null){
    		flag = info.jobTitle;
    		session("jobtitle",info.jobTitle);
    		}
    	}
    	return ok(flag);
    }
    
    public Result getSameJobTitle(){
    	String flag = "";
    		flag = session("jobtitle");
    		session("jobtitle","");
    	return ok(flag);
    }
    @AdminMarketAnnotation
    @BasicAuth
    public Result addCompanyContact() {
    	CompanyContactBean companyContactBean = Form.form(CompanyContactBean.class).bindFromRequest().get();
    	CompanyBean companyBean = Form.form(CompanyBean.class).bindFromRequest().get();
    	try {
    		List<ClientContactNo> ccNoList = new ArrayList<ClientContactNo>();
    		//Logger.debug("size"+companyContactBean.contactType.size());
    		
    		for(int i=0 ;i<companyContactBean.contactNo.size();i++) {
    			ClientContactNo ccNo = new ClientContactNo();
    			ccNo.contactType = companyContactBean.contactType.get(i);
    			ccNo.contactNo = companyContactBean.contactNo.get(i);
    			ccNo.countryCode = companyContactBean.countryCode.get(i);
    			ccNoList.add(ccNo);
    		}
    		CompanyContacts contact = new CompanyContacts();
    		contact.setContactName(companyContactBean.contactName);
    		contact.setContactNoList(ccNoList);
    		contact.setEmailID(companyContactBean.emailID);
    		contact.setLocation(companyContactBean.location);
    		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    		if(companyContactBean.dob!=null  && !companyContactBean.dob.isEmpty()) {
    			//Logger.info(companyContactBean.dob+"");
    			Date dob = sdf.parse(companyContactBean.dob);
    			contact.setDob(dob);
    		}
    		if( companyContactBean.anniversaryDate !=null && !companyContactBean.anniversaryDate.isEmpty()) {
    			Date anvDate = sdf.parse(companyContactBean.anniversaryDate);
    			contact.setAnniversaryDate(anvDate);
    		}
    		contact.save();
    		
    		if(companyBean.leadId == 2){
    			session("CID","");
    			return redirect(routes.staticController.getAddCompany());
    		}else if(companyBean.leadId == 1){
    			session("CID","");
    			
    			Map<Long, String> map = new HashMap<Long, String>();
    			Long id = Long.parseLong(CompanyContacts.find.orderBy("id desc").findIds().get(0).toString());
    			CompanyContacts companyLead = CompanyContacts.find.byId(id);
				map.put(id, companyLead.getContactName());
				return ok(Json.toJson(map));
    			
    			
    			//return ok(views.html.leads.selectAddContact.render(CompanyContacts.find.all(),id));
    		}else{	
    			flash().put(
    					"alert",
    					new Alert("alert-success", companyContactBean.contactName
    							+ "Contact Successfully Added").toString());
    			return redirect(routes.staticController.getAddContact());
    		}
    	}catch(Exception e) {
    		e.printStackTrace();
    		/*flash().put(
					"alert",
					new Alert("alert-danger", "Data Not Stored , Exception occured ").toString());*/
    		if(companyBean.leadId == 2){
    			session("CID","");
    			return redirect(routes.staticController.getAddCompany());
    		}else if(companyBean.leadId == 1){
    			session("CID","");
    			Map<Long, String> map = new HashMap<Long, String>();
    			Long id = Long.parseLong(CompanyContacts.find.orderBy("id desc").findIds().get(0).toString());
    			CompanyContacts companyLead = CompanyContacts.find.byId(id);
				map.put(id, companyLead.getContactName());
				return ok(Json.toJson(map));
    			//return ok(views.html.leads.selectAddContact.render(CompanyContacts.find.all(),id));
    		}else{	
    			return redirect(routes.staticController.getAddContact());
    		}
    	}
    	
    }
    
    @AdminMarketAnnotation
    @BasicAuth
    public Result deleteCompanyContactNos(Long contactId , Long contactNoId) {
    	if(contactId != null && contactNoId != null) {
    		CompanyContacts contact = CompanyContacts.find.byId(contactId);
    		ClientContactNo cNO = ClientContactNo.find.byId(contactNoId);
    		List<ClientContactNo> listNos= contact.contactNoList;
    		listNos.remove(cNO);
    		contact.update();
    	}
    	return redirect(routes.staticController.getEditContact(contactId));
    }
    
    @AdminMarketAnnotation
    @BasicAuth
    public Result updateCompanyContact() {
    	CompanyContactBean companyContactBean = Form.form(CompanyContactBean.class).bindFromRequest().get();
    	
    	try {
    		List<ClientContactNo> ccNoList = new ArrayList<ClientContactNo>();
    		//Logger.debug("size"+companyContactBean.contactType.size());
    		for(int i=0 ;i<companyContactBean.contactNo.size();i++) {
    			ClientContactNo ccNo = new ClientContactNo();
    			ccNo.contactType = companyContactBean.contactType.get(i);
    			ccNo.contactNo = companyContactBean.contactNo.get(i);
    			ccNo.countryCode = companyContactBean.countryCode.get(i);
    			ccNoList.add(ccNo);
    		}
    		if(companyContactBean.id != null ) {
	    		CompanyContacts contact = CompanyContacts.find.byId(companyContactBean.id);
	    		contact.setContactName(companyContactBean.contactName);
	    		contact.contactNoList.clear();
	    		contact.setContactNoList(ccNoList);
	    		contact.setEmailID(companyContactBean.emailID);
	    		contact.setLocation(companyContactBean.location);
	    		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	    		if(!companyContactBean.dob.isEmpty()) {
	    			Date dob = sdf.parse(companyContactBean.dob);
	    			contact.setDob(dob);
	    		}
	    		if(!companyContactBean.anniversaryDate.isEmpty()) {
	    			Date anvDate = sdf.parse(companyContactBean.anniversaryDate);
	    			contact.setAnniversaryDate(anvDate);
	    		}
	    		contact.update();
    		}
    		flash().put(
				"alert",
				new Alert("alert-success", companyContactBean.contactName
						+ " Successfully Updated").toString());
    	}catch(Exception e) {
    		e.printStackTrace();
    		flash().put(
					"alert",
					new Alert("alert-danger", "Data Not Updated , Exception occured ").toString());
    	}
    	return redirect(routes.staticController.getAllContact());
    }
    
    //Add LeadStatus
    @AdminMarketAnnotation
    @BasicAuth
    public Result addLeadStatus() {
    	LeadStatus leadStatus = Form.form(LeadStatus.class).bindFromRequest().get();
    	try{
    		leadStatus.save();
    		flash().put(
    				"alert",
    				new Alert("alert-success", leadStatus.status
    						+ " Lead Status Successfully Added").toString());
    		if(session("CID").equals("4")){
    			session("CID","");
    			return redirect(routes.staticController.getAddLead());
    		}else{
    			return redirect(routes.staticController.getConfigureLead());
    		}
    	}catch(Exception e) {
    		e.printStackTrace();
    		flash().put(
					"alert",
					new Alert("alert-danger", "Data Not Stored , Exception occured ").toString());
    		return redirect(routes.staticController.getConfigureLead());
    	}
    }
    
    public Result updateLeadStatus(){
    	LeadStatus leadStatus = Form.form(LeadStatus.class).bindFromRequest().get();
    	try{
    		leadStatus.update();
    		flash().put(
    				"alert",
    				new Alert("alert-success", leadStatus.status
    						+ " Lead Status Successfully Updated").toString());
    	}catch(Exception e) {
    		e.printStackTrace();
    		flash().put(
					"alert",
					new Alert("alert-danger", "Data Not Updated , Exception occured ").toString());
    	}
    	return redirect(routes.staticController.getConfigureLead());
    }
    public static void storeContactsbyCompany(Company company,LeadBean leadBean){
    	for (int i = 0; i < leadBean.cid.size(); i++) {
			if(leadBean.cid.get(i) != null) {
				CompanyContacts contact = CompanyContacts.find.byId(leadBean.cid.get(i));
				if(contact != null) {
					CompanyContactInfo info = CompanyContactInfo.find.where().eq("company_id",company.id).eq("company_Contacts_id", contact.id).findUnique();
					if(info == null  && contact != null ) {
						CompanyContactInfo companyContactInfo = new CompanyContactInfo();
						companyContactInfo.Company = company;
						companyContactInfo.companyContacts = contact;
						companyContactInfo.jobTitle = leadBean.jobTitle.get(i);
						companyContactInfo.save();
					}
				} 
			}
		}
    }
    
    //Add Lead
    @AdminMarketAnnotation
    @BasicAuth
    public Result addLead() {
    	LeadBean leadBean = Form.form(LeadBean.class).bindFromRequest().get();
    	
    	//Logger.debug("list" +leadBean.toString());
    	try{
    		Lead lead = new Lead();
    		if(leadBean.companyId != null) {
    			Company company = Company.find.byId(leadBean.companyId);
    			lead.company = company;
    		}
    		lead.opportunityTitle = leadBean.opportunityTitle;
    		lead.opportunityDiscription = leadBean.opportunityDiscription;
    		if(leadBean.estimatedAmount != null) {
    			lead.estimatedAmount = leadBean.estimatedAmount;
    		}
    		if(leadBean.leadSource != null){
    		lead.leadSource = leadBean.leadSource;
    		}
    		if(leadBean.leadStatusId != null) {
    			LeadStatus status = LeadStatus.find.byId(leadBean.leadStatusId);
    			lead.leadStatus = status;
    		}
    		if(leadBean.cid != null && !leadBean.cid.isEmpty()) {
    			if(getFlag(leadBean.cid)){
    				//Logger.debug("adfnasldns"+leadBean.cid);
					flash().put(
							"alert",
							new Alert("alert-danger", "Dublicate Contacts not allowed  ").toString());
					return redirect(routes.staticController.getAddLead());
				}
    		}
    		lead.createdOn = new Date();
    		lead.lastUpdate = new Date();
    		lead.appUser = AppUser.find.byId(Long.parseLong(session("AppUserId")));
    		lead.save();
    		if(leadBean.cid != null && !leadBean.cid.isEmpty()) {
    			Long id = Long.parseLong(Lead.find.orderBy("id desc").findIds().get(0).toString());
    			Lead lead1 = Lead.find.byId(id);
    			getLeadContacts(leadBean,lead1);
    			if(leadBean.companyId != null){
    				for(Long id1 : leadBean.cid){
    					//Logger.debug("company123");
    					CompanyContactInfo info = CompanyContactInfo.find.where().eq("company_id",leadBean.companyId).eq("company_Contacts_id",id1).findUnique();
    					//Logger.debug("company343");
    						if(info != null) {
    							//Logger.debug("company45523");
    							info.delete();
    						}
    				}
    				Company company = Company.find.byId(leadBean.companyId);
    				storeContactsbyCompany(company,leadBean);
    			}
    			
    		}
    		//lead.save();
    		flash().put(
					"alert",
					new Alert("alert-success","New Lead Successfully Added").toString());
    	}catch(Exception e) {
    		e.printStackTrace();
    		flash().put(
					"alert",
					new Alert("alert-danger", "Data Not Stored , Exception occured ").toString());
    	}
    	
    	return redirect(routes.staticController.getAddLead());
    }
    
    public Result deletdLead(Long id){
    	try{
    		if(id != null){
    			Lead lead = Lead.find.byId(id);
    			lead.getComments().clear();
    			if(lead.id != null){
    				List<LeadContactInfo> listLeadContact = new ArrayList<LeadContactInfo>();
    				listLeadContact  = LeadContactInfo.find.where().eq("lead_id", lead.id).findList();
    				if(!listLeadContact.isEmpty()) {
    					for(LeadContactInfo leadContactInfo : listLeadContact) {
    						leadContactInfo.delete();
    					}
    				}
    	    	}
    			lead.delete();
    			flash().put(
    					"alert",
    					new Alert("alert-success",id+" Lead Successfully Deleted").toString());
    		}
    	}catch(Exception e) {
    		e.printStackTrace();
    		flash().put(
					"alert",
					new Alert("alert-danger", "Data Not Deleted , Exception occured ").toString());
    	}
    
    	return redirect(routes.staticController.getAllLeads());
	}
    
    
    @AdminMarketAnnotation
    @BasicAuth
    public Result deleteLeadContact(Long LeadId, Long ContactId) {
    	if(ContactId != null){
    		LeadContactInfo contact = LeadContactInfo.find.byId(ContactId);
    		contact.ccInfoList.clear();
    		contact.delete();
    	}
    	return redirect(routes.staticController.getEditLead(LeadId));
    }
    
    public static void getLeadContacts(LeadBean leadBean,Lead lead){
    	for (int i = 0; i < leadBean.cid.size(); i++) {
			if(leadBean.cid.get(i) != null) {
				CompanyContacts contact = CompanyContacts.find.byId(leadBean.cid.get(i));
				if(contact != null) {
					LeadContactInfo leadContactInfo = new LeadContactInfo();
					leadContactInfo.lead = lead;
					leadContactInfo.companyContact = contact;
					if(leadBean.jobTitle.size() > 0){
					leadContactInfo.jobTitle = leadBean.jobTitle.get(i);
					}
					leadContactInfo.save();
				}
			}
		}
    	
    }
    
    @AdminMarketAnnotation
    @BasicAuth
    public Result updateLead() {
    	LeadBean leadBean = Form.form(LeadBean.class).bindFromRequest().get();
    	try{
    		Lead lead = Lead.find.byId(leadBean.leadId);
    		lead.setCompany(null);
    		if(leadBean.companyId != null) {
    			Company company = Company.find.byId(leadBean.companyId);
    			lead.setCompany(company);
    		}
    		lead.setOpportunityTitle(leadBean.opportunityTitle);
    		lead.setOpportunityDiscription(leadBean.opportunityDiscription);
    		if(leadBean.estimatedAmount != null) {
    			lead.setEstimatedAmount(leadBean.estimatedAmount);
    		}
    		if(leadBean.leadSource != null){
        		lead.setLeadSource(leadBean.leadSource);
        		}
    		if(leadBean.leadStatusId != null) {
    			LeadStatus status = LeadStatus.find.byId(leadBean.leadStatusId);
    			lead.setLeadStatus(status); 
    		}
    		if(leadBean.cid != null && !leadBean.cid.isEmpty()) {
    			if(getFlag(leadBean.cid)){
					flash().put(
							"alert",
							new Alert("alert-danger", "Dublicate Contacts not allowed  ").toString());
					return redirect(routes.staticController.getEditLead(leadBean.leadId));
				}
    			
    			List<LeadContactInfo> listLeadContact =new ArrayList<LeadContactInfo>();
    			listLeadContact  = LeadContactInfo.find.where().eq("lead_id",lead.id).findList();
    			if(!listLeadContact.isEmpty()) {
    				for(LeadContactInfo contact : listLeadContact){
    					contact.delete();
    				}
    			}
    			
    			getLeadContacts(leadBean,lead);
    			
    			if(leadBean.companyId != null) {
    				for(Long id : leadBean.cid){
    					CompanyContactInfo info = CompanyContactInfo.find.where().eq("company_id",leadBean.companyId).eq("company_Contacts_id",id).findUnique();
    						if(info != null) {
    							info.delete();
    						}
    				}
    				
    				Company company = Company.find.byId(leadBean.companyId);
    				storeContactsbyCompany(company,leadBean);
    			}
    		}
    		lead.lastUpdate = new Date();
    		lead.update();
    		flash().put(
					"alert",
					new Alert("alert-success","Lead ID"+lead.id+" Successfully Updated").toString());
    	}catch(Exception e) {
    		e.printStackTrace();
    		flash().put(
					"alert",
					new Alert("alert-danger", "Data Not Updated , Exception occured ").toString());
    	}
    	return redirect(routes.staticController.getAllLeads());
    }
    
    
    public Result storeLeadComment() {
    	LeadCommentBean leadCommentBean = Form.form(LeadCommentBean.class).bindFromRequest().get();
    	LeadChatComment leadChatComment =new LeadChatComment();
    	try{
    		String message = "";
    		message = " posted in a lead discussion.";
    		
    		leadChatComment.leadId = Long.parseLong(session("leadId"));
    		leadChatComment.appUserId = Long.parseLong(session("AppUserId"));
    		leadChatComment.comment = leadCommentBean.comment;
    		if(leadCommentBean.leadStatusId != null) {
    			LeadStatus status = LeadStatus.find.byId(leadCommentBean.leadStatusId);
    			leadChatComment.leadStatus = status;
    			Lead lead = Lead.find.byId(Long.parseLong(session("leadId")));
    			lead.setLeadStatus(status);
    			if(lead.getCompany() != null){
    			//notificationAlert.notification = " posted in "+lead.getCompany().getCompanyName()+"'s lead discussion.";
    			message = " posted in "+lead.getCompany().getCompanyName()+"'s lead discussion.";
    			}
    			lead.lastUpdate = new Date();
    			lead.update();
    		}
    		leadChatComment.commentDate = new Date();
    		if(leadChatComment.leadId != null){
    			Lead lead = Lead.find.byId(leadChatComment.leadId);
    			leadChatComment.leads.add(lead);
    		}
    		try {
				MultipartFormData body = request().body().asMultipartFormData();
				FilePart picture = body.getFile("file");
				if (picture != null) {
					String fileName = picture.getFilename();
					String contentType = picture.getContentType();
					File file = picture.getFile();
					StoreFile storeFile = new StoreFile();
					storeFile.setFile(Files.toByteArray(file));
					storeFile.contentType = contentType;
					storeFile.fileName = fileName;
					storeFile.save();
					leadChatComment.listFIles.add(storeFile);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
    		leadChatComment.save();
    		
    		List<Role> rolesList = new ArrayList<Role>();
    		Role roleMarketing = Role.find.where().eq("role", "Marketing").findUnique();
    		Role roleAdmin = Role.find.where().eq("role", "Admin").findUnique();
    		rolesList.add(roleAdmin);
    		rolesList.add(roleMarketing);
    		List<AppUser> appUsersList = new ArrayList<AppUser>();
    		appUsersList.addAll(AppUser.find.where().in("role", rolesList).findList());
    		for(AppUser appUser : appUsersList){
				if(!Application.getLoggedInUser().equals(appUser)){
					if(appUser.role.contains(roleMarketing)){
					 AdminLeaveController.sendNotification(appUser, Application.getLoggedInUser(), Application.getLoggedInUser().getFullName()+message, "/lead-management/"+Long.parseLong(session("leadId")),roleMarketing);
					}
					if(appUser.role.contains(roleAdmin)){
						 AdminLeaveController.sendNotification(appUser, Application.getLoggedInUser(), Application.getLoggedInUser().getFullName()+message, "/lead-management/"+Long.parseLong(session("leadId")),roleAdmin);
					}
				}
    		}
    		
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    	/*//append page
    	Lead lead = Lead.find.byId(leadChatComment.leadId);
		List<LeadContactInfo> listLeadContact =new ArrayList<LeadContactInfo>();
		listLeadContact  = LeadContactInfo.find.where().eq("lead_id",leadChatComment.leadId).findList();
		session("leadId",leadChatComment.leadId.toString());
		List<LeadStatus> listLeadStatus = new ArrayList<LeadStatus>();
		List<LeadChatComment> listLeadComments = new ArrayList<LeadChatComment>();
		listLeadComments = LeadChatComment.find.where().eq("leadId", leadChatComment.leadId).findList();
		listLeadStatus = LeadStatus.find.all();
		
			List<NotificationAlert> listNotificationAlert = NotificationAlert.find.where().eq("alert", false).eq("leadId", leadChatComment.leadId).findList();
			if(listNotificationAlert != null && !listNotificationAlert.isEmpty()){
				for(NotificationAlert notificationAlert : listNotificationAlert){
						notificationAlert.alert = true;
						notificationAlert.update();
				}
			}
			Collections.sort(listLeadComments);
			return ok(views.html.leads.getLeadManagement.render(listLeadStatus,listLeadComments,lead,listLeadContact));*/
    	return redirect(routes.staticController.getManagementLead(leadChatComment.leadId));
    }
    
    
    public Result updateLeadComment(Long id, String data){
    	//Logger.debug("id  "+id +"  comment "+data);
    	LeadChatComment leadChatComment = LeadChatComment.find.byId(id);
    	leadChatComment.comment = data;
    	leadChatComment.update();
    	Long leadId = Long.parseLong(session("leadId"));
    	return redirect(routes.staticController.getManagementLead(leadId));
    }
    
    public Result getFile(Long fileId){
    	StoreFile file = StoreFile.find.byId(fileId);
    	byte[] uploadFile = file.getFile();
    	String filename = file.fileName;
    	String contentType = file.contentType;
    	 response().setContentType("APPLICATION/OCTET-STREAM");  
         response().setHeader("Content-Disposition","attachment; filename=\"" + filename + "\"");   
		return ok(uploadFile).as(contentType);
    }
    
    public Result deleteComment(Long id,String lId) {
    	Long leadId = Long.parseLong(lId);
    	try{
    		if(id != null){
    		LeadChatComment leadChatComment = LeadChatComment.find.byId(id);
    		leadChatComment.leads.clear();
    		leadChatComment.listFIles.clear();
    		leadChatComment.delete();
    		}
    		flash().put(
					"alert",
					new Alert("alert-success","Comment Successfully Deleted").toString());
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return redirect(routes.staticController.getManagementLead(leadId));
    }
    
    
    public static void createAllCompanysExcellsheet(){
    	List<Company> listCompanys = new ArrayList<Company>();
    	listCompanys = Company.find.all();
    	try {
			/*String excelFileName = "conf/excel/AllCompany'sData.xls";
			String sheetName = "AllCompany'sData";
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet(sheetName);
			
			sheet.setDefaultColumnWidth(25);

			HSSFCellStyle cellStyle = wb.createCellStyle();
			// cell.setCellStyle(cellStyle );
			cellStyle.setWrapText(true);
			HSSFRow row1 = sheet.createRow(0);

			HSSFCell cell1 = row1.createCell(0);
			cell1.setCellValue("Company ID");
			HSSFCell cell2 = row1.createCell(1);
			cell2.setCellValue("Company Name");
			HSSFCell cell3 = row1.createCell(2);
			cell3.setCellValue("Address");
			HSSFCell cell4 = row1.createCell(3);
			cell4.setCellValue("Website");
			HSSFCell cell5 = row1.createCell(4);
			cell5.setCellValue("No.Of Lead's");
			int i = 1;
			for(Company company : listCompanys){
				HSSFRow row = sheet.createRow(i);
				
				HSSFCell cell6 = row.createCell(0);
				cell6.setCellValue(company.getId());
				
				HSSFCell cell7 = row.createCell(1);
				cell7.setCellValue(company.getCompanyName());
				
				HSSFCell cell8 = row.createCell(2);
				cell8.setCellValue(company.getAddress());
				
				HSSFCell cell9 = row.createCell(3);
				cell9.setCellValue(company.getWebsite());
				
				HSSFCell cell10 = row.createCell(4);
				cell10.setCellValue(Lead.getLeadsbyComapny(company.id));
				i++;
			}
			FileOutputStream fileOut = new FileOutputStream(excelFileName);
			// write this workbook to an Outputstream.
			wb.write(fileOut);
			fileOut.flush();
			fileOut.close();*/
    		
    		
    		FileWriter fileWriter = null;
    		fileWriter = new FileWriter("conf/excel/AllCompany'sData.csv");
    		fileWriter.append("Comapny Name,Address,Website,No.of Leads");
    		fileWriter.append("\n");
    		for(Company company : listCompanys){
    			fileWriter.append(company.getCompanyName());
    			fileWriter.append(company.getAddress());
    			fileWriter.append(company.getWebsite());
    			fileWriter.append(Lead.getLeadsbyComapny(company.id).toString());
    			fileWriter.append(",");
    			fileWriter.append("\n");
    		//	Logger.debug("asdnkld");

    		}
    		fileWriter.flush();
    		fileWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public Result downloadAllCompanysExcellSheet(){
    	try{
    	createAllCompanysExcellsheet();
    	
    	File file = new File("conf/excel/AllCompany'sData.csv");
		String filename = new Date().toLocaleString()+" AllCompany'sData.csv";
		response().setContentType("application/csv");
		response().setHeader("Content-Disposition",
				"attachment; filename=\"" + filename + "\"");
		return ok(file).as("application/csv");
    	}catch (Exception e) {
			e.printStackTrace();
			return redirect(routes.staticController.getAllCompany());
		}
    }
    
    public Result leadsSummary(){
    	List<Lead> listLeads = new ArrayList<Lead>();
    	List<LeadSummary> listLeadSummary = new ArrayList<LeadSummary>();
    	List<LeadStatus> listLeadStatus = new ArrayList<LeadStatus>();
    	try{
    		listLeadStatus = LeadStatus.find.all();
    		if(!listLeadStatus.isEmpty()){
    			for(LeadStatus leadStatus : listLeadStatus){
    				listLeads = Lead.find.where().eq("leadStatus", leadStatus).findList();
    				if(!listLeads.isEmpty()){
    					LeadSummary leadSummary = new LeadSummary();
    					leadSummary.leadStatus = leadStatus;
    					leadSummary.totalStatus = listLeads.size();
    					for(Lead lead : listLeads){
    						if(lead.getEstimatedAmount() != null){
    						leadSummary.totalEstimatedAmount = leadSummary.totalEstimatedAmount+lead.estimatedAmount;
    						}
    					}
    					//leadSummary.save();
    					listLeadSummary.add(leadSummary);
    				}
    			}
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return ok(views.html.leads.leadsSummary.render(listLeadSummary));
    }
    
    /*public Result uploadContactExcellSheet(){
    	
    	try {
			MultipartFormData body = request().body().asMultipartFormData();
			MultipartFormData.FilePart picture = body.getFile("file");
			
			if (picture != null) {
				File file = picture.getFile();
				FileInputStream fs = new FileInputStream(file);
				XSSFWorkbook workbook = new XSSFWorkbook(fs);
				XSSFSheet sheet = workbook.getSheetAt(0);
				//int totalNoOfRows = firstSheet.getRows();
				//int totalNoOfCols = firstSheet.getColumns();
				for(Row row : sheet){
					String name = "";
					for(Cell cell : row){
						if(cell.getRowIndex() != 0){
							CompanyContacts contact = new CompanyContacts();
							if (Cell.CELL_TYPE_STRING == cell.getCellType() && cell.getColumnIndex() == 0 || cell.getColumnIndex() == 1) {
								name = name.concat(cell.getStringCellValue()+" ");
								Logger.debug(" name "+name);
							}
							contact.setContactName(name);
							if (Cell.CELL_TYPE_STRING == cell.getCellType() && cell.getColumnIndex() == 2) {
								contact.setEmailID(cell.getStringCellValue());
							}
							if (Cell.CELL_TYPE_STRING == cell.getCellType() && cell.getColumnIndex() == 3) {
								contact.setLocation(cell.getStringCellValue());
							}
							if (Cell.CELL_TYPE_STRING == cell.getCellType() && cell.getColumnIndex() == 4) {
								SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
								Date dob = sdf.parse(cell.getStringCellValue());
								contact.setDob(dob);
							}
							List<ClientContactNo> contactNoList = new ArrayList<ClientContactNo>();
							if (Cell.CELL_TYPE_NUMERIC == cell.getCellType() && cell.getColumnIndex() == 5 ) {
								Long val = (long) cell.getNumericCellValue();
									if(val != null) {
										ClientContactNo contactNo = new ClientContactNo(); 
										contactNo.contactType = ContactType.Home;
										contactNo.contactNo = (long) cell.getNumericCellValue();
										contactNoList.add(contactNo);
									}
							}
							
							if (Cell.CELL_TYPE_NUMERIC == cell.getCellType() && cell.getColumnIndex() == 6 ) {
								Long val = (long) cell.getNumericCellValue();
									if(val != null) {
										ClientContactNo contactNo = new ClientContactNo(); 
										contactNo.contactType = ContactType.Mobile;
										contactNo.contactNo = (long) cell.getNumericCellValue();
										contactNoList.add(contactNo);
									}
							}
							if (Cell.CELL_TYPE_NUMERIC == cell.getCellType() && cell.getColumnIndex() == 7 ) {
								Long val = (long) cell.getNumericCellValue();
									if(val != null) {
										ClientContactNo contactNo = new ClientContactNo(); 
										contactNo.contactType = ContactType.Work;
										contactNo.contactNo = (long) cell.getNumericCellValue();
										contactNoList.add(contactNo);
									}
							}
							contact.setContactNoList(contactNoList);
							contact.save();
						}
					}
				}
			}
			flash().put(
					"alert",
					new Alert("alert-success","ExcellSheet Successfully Uploaded").toString());
		}catch(Exception e){
			e.printStackTrace();
			flash().put(
					"alert",
					new Alert("alert-danger","ExcellSheet Not Uploaded").toString());
		}
    	
    	return redirect(routes.staticController.getAllContact());
    }*/
    
    public Result uploadContactExcellSheet(){
    	try {
			MultipartFormData body = request().body().asMultipartFormData();
			MultipartFormData.FilePart picture = body.getFile("file");
			
			if (picture != null) {
				File file = picture.getFile();
				String filename = picture.getFilename();
				filename = filename.substring(filename.indexOf("."),filename.length());
				if(!filename.equals(".csv")){
					flash().put(
							"alert",
							new Alert("alert-danger","Only Upload CSV file").toString());
					return redirect(routes.staticController.getAllContact());
				}
				Reader in = new FileReader(file);
				Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(in);
				Long count = 0l;
				for (CSVRecord record : records) {
					if(count > 1){
						String email = record.get(5);
						String firstName = record.get(1);
					    String lastName = record.get(3);
					    String companyName = record.get(29);
					    String jobTitle = record.get(31);
					    String work = record.get(34);
					    String home = record.get(40);
					    String mobile = record.get(43);
					    String dob = record.get(52);//May 27, 2013 SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
					    if(email.trim() != null && !email.isEmpty()){
						    CompanyContacts contactCheck = CompanyContacts.find.where().eq("emailID", email.trim()).findUnique();
						    if(contactCheck == null){
						    	CompanyContacts contact = new  CompanyContacts();
						    	contact.setContactName(firstName+" "+lastName);
						    	contact.setEmailID(email.trim());
						    	List<ClientContactNo> contactNoList = new ArrayList<ClientContactNo>();
						    		if(work != null && !work.isEmpty()){
						    			Long workPhone = Long.parseLong(work);
						    			ClientContactNo contactNo = new ClientContactNo(); 
						    			contactNo.contactType = ContactType.Work;
						    			contactNo.contactNo = workPhone;
						    			contactNoList.add(contactNo);
						    		}
						    		if(home != null && !home.isEmpty()){
						    			Long homePhone = Long.parseLong(home);
						    			ClientContactNo contactNo = new ClientContactNo(); 
						    			contactNo.contactType = ContactType.Home;
						    			contactNo.contactNo = homePhone;
						    			contactNoList.add(contactNo);
						    		}
						    		if(mobile != null && !mobile.isEmpty()){
						    			Long mobilePhone = Long.parseLong(mobile);
						    			ClientContactNo contactNo = new ClientContactNo(); 
						    			contactNo.contactType = ContactType.Mobile;
						    			contactNo.contactNo = mobilePhone;
						    			contactNoList.add(contactNo);
						    		}
						    		contact.setContactNoList(contactNoList);
						    		contact.save();
						    		Long contactId =Long.parseLong(CompanyContacts.find.orderBy("id desc").findIds().get(0).toString());
						    		Long companyId = null;
						    		
						    		if(companyName.trim() != null && !companyName.isEmpty()){
						    			Company companyCheck = Company.find.where().eq("companyName", companyName.trim()).findUnique();
						    			if(companyCheck == null) {
						    				Company company = new Company();
						    				company.setCompanyName(companyName.trim());
						    				company.save();
						    				companyId =Long.parseLong(Company.find.orderBy("id desc").findIds().get(0).toString());
						    			}else{
						    				companyId = companyCheck.getId();
						    			}
						    		}
						    		
						    		if(contactId!= null && companyId != null ){
						    			CompanyContactInfo companyContactInfo = new CompanyContactInfo();
						    			companyContactInfo.Company = Company.find.byId(companyId);
						    			companyContactInfo.companyContacts = CompanyContacts.find.byId(contactId);
						    			companyContactInfo.jobTitle = jobTitle.trim();
						    			companyContactInfo.save();
						    		}
						    }
						}
					}
					count ++;
				}
			}
			flash().put(
					"alert",
					new Alert("alert-success","CSV file Successfully Uploaded").toString());
    	}catch(Exception e){
    		e.printStackTrace();
    		flash().put(
					"alert",
					new Alert("alert-danger","CSV file Not Uploaded").toString());
    	}
    	
    	return redirect(routes.staticController.getAllContact());
    }
}
