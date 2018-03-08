package bean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import controllers.AdminLeaveController;
import controllers.Application;
import controllers.EngineerController;
import models.AppUser;
import models.Role;
import models.leave.AppliedLeaves;
import models.leave.DeductLeave;
import models.leave.LeaveStatus;
import models.leave.Leaves;
import play.Logger;
import play.libs.F;
import play.libs.F.Promise;
import utils.EmailService;

public class LeaveApprovalByAdminBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Long appliedId;
	
	public String leaveStatus;
	
	public String rejectedReason = "N/A";
	
	public List<Long> leaveid = new ArrayList<Long>();
	public List<Float> deLeave = new ArrayList<Float>();
	
	
	public String leaveApproval(){
		List<AppUser> appUsersList = new ArrayList<AppUser>();
		List<Role> rolesList = new ArrayList<>();
		//Logger.debug("inside method call");
		Role roleAdmin = Role.find.where().eq("role", "Admin")
				.findUnique();
		Role roleRepEngineer = Role.find.where().eq("role", "Engineer")
				.findUnique();
		rolesList.add(roleAdmin);
		Role roleHr = Role.find.where().eq("role", "HR").findUnique();
		Role roleRepManger = Role.find.where().eq("role", "Manager").findUnique();
		rolesList.add(roleHr);
		appUsersList.addAll(AppUser.find.where()
				.in("role", rolesList).findList());
		if(appliedId != null && appliedId >0){
			AppliedLeaves appliedLeaves = AppliedLeaves.find.byId(appliedId);
			if(appliedLeaves != null){
				appliedLeaves.leaveStatus = LeaveStatus.valueOf(leaveStatus);
				appliedLeaves.approvedBy = Application.getLoggedInUser();
				if(LeaveStatus.valueOf(leaveStatus).equals(LeaveStatus.REJECTED)){
					try{
						if(appliedLeaves.leaveType != null){
							String lastyear = new SimpleDateFormat("yyyy").format(new Date());
							Date startdate = new SimpleDateFormat("yyyy").parse(lastyear);
							Leaves leaves = Leaves.find.where().eq("appUser", Application.getLoggedInUser()).eq("leaveType", appliedLeaves.leaveType).eq("year", startdate).findUnique();
							if (leaves != null) {
								leaves.usedLeaves -= appliedLeaves.totalLeaves;
								leaves.remainingLeaves += appliedLeaves.totalLeaves;
								leaves.update();
							}
						}
						appliedLeaves.dateLeaves.clear();
						appliedLeaves.deductLeaves.clear();
						appliedLeaves.rejectedReason = rejectedReason;
					}catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					List<DeductLeave> deductLeaveList = new ArrayList<DeductLeave>();
					for(int i=0;i<leaveid.size();i++){
						if(deLeave.get(i) != null && deLeave.get(i) != 0.0f){
							DeductLeave deductLeave = new DeductLeave();
							Leaves leave = Leaves.find.byId(leaveid.get(i));
							deductLeave.leaveType = leave.leaveType;
							if(leave != null && appliedLeaves.endDate.before(EngineerController.getTodayDate(new Date()))){
								leave.usedLeaves += deLeave.get(i);
								leave.remainingLeaves -= deLeave.get(i);
								leave.update();
							}
							deductLeave.deductLeaves = deLeave.get(i);
							deductLeaveList.add(deductLeave);
						}
					}
					appliedLeaves.deductLeaves = deductLeaveList;
				}
				appliedLeaves.update();
				
				if(appliedLeaves.appUser.getReptManager(appliedLeaves.appUser.getReportMangerId()) != null && !appUsersList.contains(appliedLeaves.appUser.getReptManager(appliedLeaves.appUser.getReportMangerId()))){
					appUsersList.add(appliedLeaves.appUser.getReptManager(appliedLeaves.appUser.getReportMangerId()));
				}
				if(LeaveStatus.valueOf(leaveStatus).equals(LeaveStatus.REJECTED)){
					final Promise<Boolean> emailResult1 = Promise.promise(new F.Function0<Boolean>() {
						@Override
						public Boolean apply() throws Throwable {
							//cancelledNitifications(appliedLeaves);
							
							return rejectedLeavesEmails(appliedLeaves);
						}
					});
						final Promise<Boolean> emailResult = Promise.promise(new F.Function0<Boolean>() {
							@Override
							public Boolean apply() throws Throwable {
								//cancelledNitifications(appliedLeaves);
								
								return sendRejectedNotifications(appliedLeaves);
							}
						});
				}else{
					final Promise<Boolean> emailResult3 = Promise.promise(new F.Function0<Boolean>() {
						@Override
						public Boolean apply() throws Throwable {
							//cancelledNitifications(appliedLeaves);
							
							return approvedLeavesEmails(appliedLeaves);
						}
					});
					
					final Promise<Boolean> emailResult4 = Promise.promise(new F.Function0<Boolean>() {
						@Override
						public Boolean apply() throws Throwable {
							//cancelledNitifications(appliedLeaves);
							
							return approvedNotifications(appliedLeaves);
						}
					});
				}
				
				return "Updated Successfully";
			}
		}
		
		return leaveStatus;
		
	}
	
	public Boolean rejectedLeavesEmails(AppliedLeaves appliedLeaves){
		
		 AppUser appUser = Application.getLoggedInUser();
		 List<AppUser> appUsersList = new ArrayList<AppUser>();
			List<Role> rolesList = new ArrayList<>();
			//Logger.debug("inside method call");
			Role roleAdmin = Role.find.where().eq("role", "Admin")
					.findUnique();
			
			rolesList.add(roleAdmin);
			Role roleHr = Role.find.where().eq("role", "HR").findUnique();
			/*Role roleRepManger = Role.find.where().eq("role", "Manager").findUnique();
			rolesList.add(roleHr);*/
			rolesList.add(roleHr);
			appUsersList.addAll(AppUser.find.where()
					.in("role", rolesList).findList());
			if(appliedLeaves.appUser.getReportMangerId() > 0  && !appUsersList.contains(appliedLeaves.appUser.getReptManager(appliedLeaves.appUser.getReportMangerId()))){
				appUsersList.add(appliedLeaves.appUser.getReptManager(appliedLeaves.appUser.getReportMangerId()));
				}
			String content = appliedLeaves.appUser.getAppUserFullName()+"'s leave from"+" "+new SimpleDateFormat("dd-MM-yyy").format(appliedLeaves.startDate)+" to "+new SimpleDateFormat("dd-MM-yyy").format(appliedLeaves.endDate)+" "+"has been rejected by"+" "+Application.getLoggedInUser().getAppUserFullName()+"."+" \n\n Thanks BB8 Team !";
			String userContent = " Your leave from"+" "+new SimpleDateFormat("dd-MM-yyy").format(appliedLeaves.startDate)+" to "+new SimpleDateFormat("dd-MM-yyy").format(appliedLeaves.endDate)+" "+"has been rejected by"+" "+Application.getLoggedInUser().getAppUserFullName()+".\n Rejected Reason : "+appliedLeaves.rejectedReason+" \n\n Thanks BB8 Team !"; 
			for(AppUser appUser2 : appUsersList){
				if(!appUser.equals(appUser2) && !appliedLeaves.appUser.equals(appUser2)){
					EmailService.sendVerificationMail(appUser2.getEmail(), content, "Leave Rejection !!");
				}
			}
				 EmailService.sendVerificationMail(appliedLeaves.appUser.getEmail(), userContent, "Leave Rejection !!");
			
				 return true;
		
	}
	
	public Boolean approvedLeavesEmails(AppliedLeaves appliedLeaves){
		
		 AppUser appUser = Application.getLoggedInUser();
		 List<AppUser> appUsersList = new ArrayList<AppUser>();
			List<Role> rolesList = new ArrayList<>();
			//Logger.debug("inside method call");
			Role roleAdmin = Role.find.where().eq("role", "Admin")
					.findUnique();
			
			rolesList.add(roleAdmin);
			Role roleHr = Role.find.where().eq("role", "HR").findUnique();
			/*Role roleRepManger = Role.find.where().eq("role", "Manager").findUnique();
			rolesList.add(roleHr);*/
			rolesList.add(roleHr);
			appUsersList.addAll(AppUser.find.where()
					.in("role", rolesList).findList());
			if(appliedLeaves.appUser.getReportMangerId() > 0  && !appUsersList.contains(appliedLeaves.appUser.getReptManager(appliedLeaves.appUser.getReportMangerId()))){
				appUsersList.add(appliedLeaves.appUser.getReptManager(appliedLeaves.appUser.getReportMangerId()));
				}
			String content = appliedLeaves.appUser.getAppUserFullName()+"'s leave from"+" "+new SimpleDateFormat("dd-MM-yyy").format(appliedLeaves.startDate)+" to "+new SimpleDateFormat("dd-MM-yyy").format(appliedLeaves.endDate)+" "+"has been approved by"+" "+Application.getLoggedInUser().getAppUserFullName()+"."+" \n\n Thanks BB8 Team !";
			String userContent = "Your leave from"+" "+new SimpleDateFormat("dd-MM-yyy").format(appliedLeaves.startDate)+" to "+new SimpleDateFormat("dd-MM-yyy").format(appliedLeaves.endDate)+" "+"has been approved by"+" "+Application.getLoggedInUser().getAppUserFullName()+"."+" \n\n Thanks BB8 Team !"; 
			for(AppUser appUser2 : appUsersList){
				if(!appUser.equals(appUser2) && !appliedLeaves.appUser.equals(appUser2)){
					EmailService.sendVerificationMail(appUser2.getEmail(), content, "Leave Approved !!");
				}
			}
				 EmailService.sendVerificationMail(appliedLeaves.appUser.getEmail(), userContent, "Leave Approved !!");
			
				 return true;
		
	}
	
	public Boolean sendRejectedNotifications(AppliedLeaves appliedLeaves){
		List<AppUser> appUsersList = new ArrayList<AppUser>();
		List<Role> rolesList = new ArrayList<>();
		//Logger.debug("inside method call");
		Role roleAdmin = Role.find.where().eq("role", "Admin")
				.findUnique();
		Role roleRepEngineer = Role.find.where().eq("role", "Engineer")
				.findUnique();
		rolesList.add(roleAdmin);
		Role roleHr = Role.find.where().eq("role", "HR").findUnique();
		Role roleRepManger = Role.find.where().eq("role", "Manager").findUnique();
		rolesList.add(roleHr);
		appUsersList.addAll(AppUser.find.where()
				.in("role", rolesList).findList());
		if(appliedLeaves.appUser.getReptManager(appliedLeaves.appUser.getReportMangerId()) != null && !appUsersList.contains(appliedLeaves.appUser.getReptManager(appliedLeaves.appUser.getReportMangerId()))){
			appUsersList.add(appliedLeaves.appUser.getReptManager(appliedLeaves.appUser.getReportMangerId()));
			}
		String rejMessage = "Your leave from"+" "+new SimpleDateFormat("dd-MM-yyy").format(appliedLeaves.startDate)+" to "+new SimpleDateFormat("dd-MM-yyy").format(appliedLeaves.endDate)+" "+"has been rejected by"+" "+Application.getLoggedInUser().getAppUserFullName()+".";
		AdminLeaveController.sendNotification1(appliedLeaves.appUser, Application.getLoggedInUser(), rejMessage, "/leave/status");
		for(AppUser appUser : appUsersList){
			if(!Application.getLoggedInUser().equals(appUser) && !appliedLeaves.appUser.equals(appUser)){
				String rejMessage1 =appliedLeaves.appUser.getAppUserFullName()+" leave from"+" "+new SimpleDateFormat("dd-MM-yyy").format(appliedLeaves.startDate)+" to "+new SimpleDateFormat("dd-MM-yyy").format(appliedLeaves.endDate)+" "+"has been rejected by"+" "+Application.getLoggedInUser().getAppUserFullName()+".";
				if(appUser.role.contains(roleAdmin)){
					if(appliedLeaves.appUser.getReptManager(appliedLeaves.appUser.getReportMangerId()) != null){
						if(!appUser.equals(appliedLeaves.appUser.getReptManager(appliedLeaves.appUser.getReportMangerId()))){
							AdminLeaveController.sendNotification(appUser, Application.getLoggedInUser(), rejMessage1, "/leave-tracker",roleAdmin);
						}else{
							AdminLeaveController.sendNotification(appUser, Application.getLoggedInUser(), rejMessage1, "/team/leave-tracker" ,roleRepManger);
						}
					}else{
						AdminLeaveController.sendNotification(appUser, Application.getLoggedInUser(), rejMessage1, "/leave-tracker",roleAdmin);
					}
				}else if(appUser.role.contains(roleHr)){
					if(appliedLeaves.appUser.getReptManager(appliedLeaves.appUser.getReportMangerId()) != null){
						if(!appUser.equals(appliedLeaves.appUser.getReptManager(appliedLeaves.appUser.getReportMangerId()))){
							AdminLeaveController.sendNotification(appUser, Application.getLoggedInUser(), rejMessage1, "/leave-tracker",roleHr);
						}else{
							AdminLeaveController.sendNotification(appUser, Application.getLoggedInUser(), rejMessage1, "/team/leave-tracker" ,roleRepManger);
						}
					}else{
						AdminLeaveController.sendNotification(appUser, Application.getLoggedInUser(), rejMessage1, "/leave-tracker",roleHr);
					}
				}else{
					AdminLeaveController.sendNotification(appUser, Application.getLoggedInUser(), rejMessage1, "/team/leave-tracker" ,roleRepManger);
				}
				/*if(appUser.role.contains(roleAdmin)  && appliedLeaves.appUser.getReptManager(appliedLeaves.appUser.getReportMangerId()) != null && !appUser.equals(appliedLeaves.appUser.getReptManager(appliedLeaves.appUser.getReportMangerId()))){
				AdminLeaveController.sendNotification(appUser, Application.getLoggedInUser(), rejMessage1, "/leave-tracker",roleAdmin);
				}else if(appUser.role.contains(roleHr) && !appUser.equals(appliedLeaves.appUser.getReptManager(appliedLeaves.appUser.getReportMangerId()))){
					AdminLeaveController.sendNotification(appUser, Application.getLoggedInUser(), rejMessage1, "/leave-tracker",roleHr);
				}else{
					AdminLeaveController.sendNotification(appUser, Application.getLoggedInUser(), rejMessage1, "/team/leave-tracker" ,roleRepManger);
				}*/
			}
		}
		return true;
		
	}
	
	public Boolean approvedNotifications(AppliedLeaves appliedLeaves){
		List<AppUser> appUsersList = new ArrayList<AppUser>();
		List<Role> rolesList = new ArrayList<>();
		//Logger.debug("inside method call");
		Role roleAdmin = Role.find.where().eq("role", "Admin")
				.findUnique();
		Role roleRepEngineer = Role.find.where().eq("role", "Engineer")
				.findUnique();
		rolesList.add(roleAdmin);
		Role roleHr = Role.find.where().eq("role", "HR").findUnique();
		Role roleRepManger = Role.find.where().eq("role", "Manager").findUnique();
		rolesList.add(roleHr);
		appUsersList.addAll(AppUser.find.where()
				.in("role", rolesList).findList());
		if(appliedLeaves.appUser.getReptManager(appliedLeaves.appUser.getReportMangerId()) != null && !appUsersList.contains(appliedLeaves.appUser.getReptManager(appliedLeaves.appUser.getReportMangerId()))){
			appUsersList.add(appliedLeaves.appUser.getReptManager(appliedLeaves.appUser.getReportMangerId()));
			}
		String approvalMessage ="Your leave from"+" "+new SimpleDateFormat("dd-MM-yyy").format(appliedLeaves.startDate)+" to "+new SimpleDateFormat("dd-MM-yyy").format(appliedLeaves.endDate)+" "+"has been approved by"+" "+Application.getLoggedInUser().getAppUserFullName()+".";
		AdminLeaveController.sendNotification1(appliedLeaves.appUser, Application.getLoggedInUser(), approvalMessage, "/leave/status");
		for(AppUser appUser : appUsersList){
			if(!Application.getLoggedInUser().equals(appUser) && !appliedLeaves.appUser.equals(appUser)){
				String rejMessage1 =appliedLeaves.appUser.getAppUserFullName()+" leave from"+" "+new SimpleDateFormat("dd-MM-yyy").format(appliedLeaves.startDate)+" to "+new SimpleDateFormat("dd-MM-yyy").format(appliedLeaves.endDate)+" "+"has been approved by"+" "+Application.getLoggedInUser().getAppUserFullName()+".";
				if(appUser.role.contains(roleAdmin)){
					if(appliedLeaves.appUser.getReptManager(appliedLeaves.appUser.getReportMangerId()) != null){
						if(!appUser.equals(appliedLeaves.appUser.getReptManager(appliedLeaves.appUser.getReportMangerId()))){
							AdminLeaveController.sendNotification(appUser, Application.getLoggedInUser(), rejMessage1, "/leave-tracker",roleAdmin);
						}else{
							AdminLeaveController.sendNotification(appUser, Application.getLoggedInUser(), rejMessage1, "/team/leave-tracker" ,roleRepManger);
						}
					}else{
						AdminLeaveController.sendNotification(appUser, Application.getLoggedInUser(), rejMessage1, "/leave-tracker",roleAdmin);
					}
				}else if(appUser.role.contains(roleHr)){
					if(appliedLeaves.appUser.getReptManager(appliedLeaves.appUser.getReportMangerId()) != null){
						if(!appUser.equals(appliedLeaves.appUser.getReptManager(appliedLeaves.appUser.getReportMangerId()))){
							AdminLeaveController.sendNotification(appUser, Application.getLoggedInUser(), rejMessage1, "/leave-tracker",roleHr);
						}else{
							AdminLeaveController.sendNotification(appUser, Application.getLoggedInUser(), rejMessage1, "/team/leave-tracker" ,roleRepManger);
						}
					}else{
						AdminLeaveController.sendNotification(appUser, Application.getLoggedInUser(), rejMessage1, "/leave-tracker",roleHr);
					}
				}else{
					AdminLeaveController.sendNotification(appUser, Application.getLoggedInUser(), rejMessage1, "/team/leave-tracker" ,roleRepManger);
				}
	}
	
		}
		return true;
		
	}

}
