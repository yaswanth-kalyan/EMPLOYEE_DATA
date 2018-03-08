package bean;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.itextpdf.text.Anchor;

import controllers.AdminLeaveController;
import controllers.Application;
import models.AppUser;
import models.Role;
import models.leave.AppliedLeaveType;
import models.leave.AppliedLeaves;
import models.leave.DateWiseAppliedLeaves;
import models.leave.DurationEnum;
import models.leave.Holidays;
import models.leave.LeaveStatus;
import models.leave.LeaveType;
import models.leave.Leaves;
import models.leave.PartialDaysEnum;
import play.Logger;
import play.libs.F;
import play.libs.F.Promise;
import utils.EmailService;

public class ApplyLeaveBean implements Serializable {

	public Long leaveId;

	public String fromDate;

	public String toDate;

	public String reason;

	public String duration;

	public String partialEnum;

	@SuppressWarnings("deprecation")
	public String toApplyLeavenew() {
		if (leaveId != null && !leaveId.toString().isEmpty()) {
			Anchor anchor = new Anchor("https://developer.thrymr.net/team/leave-tracker");
			anchor.setReference("https://developer.thrymr.net/team/leave-tracker");

			LeaveType leaveType = LeaveType.find.byId(leaveId);
			String date = new SimpleDateFormat("yyyy").format(new Date());
			Integer count = 0;
			try {
				Leaves leave = Leaves.find.where().eq("appUser", Application.getLoggedInUser()).eq("leaveType", leaveType).eq("year", new SimpleDateFormat("yyyy").parse(date)).findUnique();
				if (leave != null) {
					if (!fromDate.isEmpty() && !toDate.isEmpty()) {
						try {
							final Date comingDate = new SimpleDateFormat("dd-MM-yyyy").parse(fromDate.trim());
							final Date todates = new SimpleDateFormat("dd-MM-yyyy").parse(toDate.trim());
							int realDiff = (int) ((todates.getTime() - comingDate.getTime()) / (1000 * 60 * 60 * 24));
							int diffDates = (int) ((todates.getTime() - comingDate.getTime()) / (1000 * 60 * 60 * 24))+ 1;
							for (int s = 0; s <= realDiff; s++) {
								//DateWiseAppliedLeaves dateLeaves = new DateWiseAppliedLeaves();
								Calendar cal = Calendar.getInstance();
								cal.setTime(comingDate);
								cal.add(Calendar.DATE, s);
								Holidays holidays = Holidays.find.where().eq("holidayDate", cal.getTime()).findUnique();
								Holidays holiday1 = Holidays.find.where().eq("correspondingWorkingDay", cal.getTime()).findUnique();
								if (holidays != null) {
									count++;
								} else if (holidays == null && (cal.get(Calendar.DAY_OF_WEEK) == 1 || cal.get(Calendar.DAY_OF_WEEK) == 7 ) && holiday1 == null) {
										count++;
								}

							}
							if (DurationEnum.valueOf(duration).equals(DurationEnum.FULL_DAY)) {
								Float differrr = (float) diffDates - (float) count;
								if (differrr <= leave.remainingLeaves) {
									if (differrr > (float) 0) {
										AppliedLeaves appliedLeaves = new AppliedLeaves();
										appliedLeaves.leaveStatus = LeaveStatus.PENDING_APPROVAL;
										appliedLeaves.startDate = comingDate;
										appliedLeaves.endDate = todates;
										appliedLeaves.reason = reason;
										appliedLeaves.leaveType = leaveType;
										appliedLeaves.appUser = Application.getLoggedInUser();
										appliedLeaves.totalLeaves = differrr;
										for (int j = 0; j <= realDiff; j++) {
											DateWiseAppliedLeaves dateLeaves = new DateWiseAppliedLeaves();
											Calendar cal = Calendar.getInstance();
											cal.setTime(comingDate);
											cal.add(Calendar.DATE, j);
											Holidays holidays = Holidays.find.where().eq("holidayDate", cal.getTime()).findUnique();
											Holidays holiday1 = Holidays.find.where().eq("correspondingWorkingDay", cal.getTime()).findUnique();
											if (holidays != null) {
											} else if (holidays == null && (cal.get(Calendar.DAY_OF_WEEK) == 1 || cal.get(Calendar.DAY_OF_WEEK) == 7 ) && holiday1 == null) {
											} else {
												dateLeaves.leaveDate = cal.getTime();
												dateLeaves.duEnum = DurationEnum.valueOf(duration);
												
												Date todayDate  = new Date(); 
												long diff1 = dateLeaves.leaveDate.getTime() - todayDate.getTime();
												Long days = TimeUnit.DAYS.convert(diff1, TimeUnit.MILLISECONDS);
												if(days < 15){
													dateLeaves.appliedLeaveType = AppliedLeaveType.Unplanned;
												}else{
													dateLeaves.appliedLeaveType = AppliedLeaveType.Planned;
												}
												
												appliedLeaves.dateLeaves.add(dateLeaves);
											}
										}
										appliedLeaves.save();
										leave.usedLeaves += (differrr);
										leave.remainingLeaves = leave.remainingLeaves - (differrr);
										leave.update();
										final Promise<Boolean> emailResult = Promise
												.promise(new F.Function0<Boolean>() {
													@Override
													public Boolean apply() throws Throwable {

														return appliedNitifications(appliedLeaves);
													}
												});

										final Promise<Boolean> emailResult1 = Promise
												.promise(new F.Function0<Boolean>() {
													@Override
													public Boolean apply() throws Throwable {

														return leaveAppliedEmail(appliedLeaves, differrr);
													}
												});
										return "Successfully Applied";
									} else {
										return "They are holidays";
									}
								} else {
									return "Not enough leaves";
								}
							}
							if (DurationEnum.valueOf(duration).equals(DurationEnum.HALF_DAY)) {
								if (PartialDaysEnum.valueOf(partialEnum).equals(PartialDaysEnum.ALL_DAYS)) {
									Float diff = (((float) diffDates - (float) count) / (float) 2);
									Logger.debug(diff + "sasasas......" + leave.remainingLeaves);
									if (diff <= leave.remainingLeaves) {
										if (diff > (float) 0) {
											AppliedLeaves appliedLeaves = new AppliedLeaves();
											appliedLeaves.leaveStatus = LeaveStatus.PENDING_APPROVAL;
											appliedLeaves.startDate = comingDate;
											appliedLeaves.endDate = todates;
											appliedLeaves.reason = reason;
											appliedLeaves.leaveType = leaveType;
											appliedLeaves.appUser = Application.getLoggedInUser();
											appliedLeaves.totalLeaves = diff;
											for (int j = 0; j <= realDiff; j++) {
												DateWiseAppliedLeaves dateLeaves = new DateWiseAppliedLeaves();
												Calendar cal = Calendar.getInstance();
												cal.setTime(comingDate);
												cal.add(Calendar.DATE, j);
												Holidays holidays = Holidays.find.where().eq("holidayDate", cal.getTime()).findUnique();
												Holidays holiday1 = Holidays.find.where().eq("correspondingWorkingDay", cal.getTime()).findUnique();
												if (holidays != null) {
												} else if (holidays == null && (cal.get(Calendar.DAY_OF_WEEK) == 1 || cal.get(Calendar.DAY_OF_WEEK) == 7 ) && holiday1 == null) {
												} else {
													dateLeaves.leaveDate = cal.getTime();
													dateLeaves.duEnum = DurationEnum.valueOf(duration);
													appliedLeaves.dateLeaves.add(dateLeaves);
													
													
													Date todayDate  = new Date(); 
													long diff1 = dateLeaves.leaveDate.getTime() - todayDate.getTime();
													Long days = TimeUnit.DAYS.convert(diff1, TimeUnit.MILLISECONDS);
													if(days < 15){
														dateLeaves.appliedLeaveType = AppliedLeaveType.Unplanned;
													}else{
														dateLeaves.appliedLeaveType = AppliedLeaveType.Planned;
													}
												}
											}
											appliedLeaves.save();
											leave.usedLeaves += diff;
											leave.remainingLeaves = leave.remainingLeaves - diff;
											leave.update();
											final Promise<Boolean> emailResult = Promise
													.promise(new F.Function0<Boolean>() {
														@Override
														public Boolean apply() throws Throwable {

															return appliedNitifications(appliedLeaves);
														}
													});
											final Promise<Boolean> emailResult1 = Promise
													.promise(new F.Function0<Boolean>() {
														@Override
														public Boolean apply() throws Throwable {

															return leaveAppliedEmail(appliedLeaves, diff);
														}
													});
											return "successfully applied";
										} else {
											return "They are holidays";
										}
									} else {
										return "Not enough leaves";
									}
								}

								if (PartialDaysEnum.valueOf(partialEnum).equals(PartialDaysEnum.START_DAY) || PartialDaysEnum.valueOf(partialEnum).equals(PartialDaysEnum.END_DAY)) {
									Float diff1 = 0f;
									if (PartialDaysEnum.valueOf(partialEnum).equals(PartialDaysEnum.START_DAY)) {
										
										Holidays holidays = Holidays.find.where().eq("holidayDate", comingDate).findUnique();
										if (holidays != null) {
											diff1 = ((float) diffDates - (float) count);
										} else if (comingDate.getDay() == 6 || comingDate.getDay() == 0) {
											Holidays holiday1 = Holidays.find.where().eq("correspondingWorkingDay", comingDate).findUnique();
											if (holiday1 == null) {
												diff1 = ((float) diffDates - (float) count);
											} else {
												diff1 = ((float) diffDates - (float) 1 + (float) 0.5 - (float) count);
											}

										} else {
											diff1 = ((float) diffDates - (float) 1 + (float) 0.5 - (float) count);
										}
										
									} else if (PartialDaysEnum.valueOf(partialEnum).equals(PartialDaysEnum.END_DAY)) {
										
										Holidays holidays = Holidays.find.where().eq("holidayDate", todates).findUnique();
										if (holidays != null) {
											diff1 = ((float) diffDates - (float) count);
										} else if (todates.getDay() == 6 || todates.getDay() == 0) {
											Holidays holiday1 = Holidays.find.where().eq("correspondingWorkingDay", todates).findUnique();
											if (holiday1 == null) {
												diff1 = ((float) diffDates - (float) count);
											} else {
												diff1 = ((float) diffDates - (float) 1 + (float) 0.5 - (float) count);
											}

										} else {
											diff1 = ((float) diffDates - (float) 1 + (float) 0.5 - (float) count);
										}

									} else {
										diff1 = ((float) diffDates - (float) 1 + (float) 0.5 - (float) count);
									}
									if ((diff1) <= leave.remainingLeaves) {
										if (diff1 > (float) 0) {
											AppliedLeaves appliedLeaves = new AppliedLeaves();
											appliedLeaves.leaveStatus = LeaveStatus.PENDING_APPROVAL;
											appliedLeaves.startDate = comingDate;
											appliedLeaves.endDate = todates;
											appliedLeaves.reason = reason;
											appliedLeaves.leaveType = leaveType;
											appliedLeaves.appUser = Application.getLoggedInUser();
											appliedLeaves.totalLeaves = diff1;
											for (int j = 0; j <= realDiff; j++) {
												DateWiseAppliedLeaves dateLeaves = new DateWiseAppliedLeaves();
												Calendar cal = Calendar.getInstance();
												cal.setTime(comingDate);
												cal.add(Calendar.DATE, j);
												Holidays holidays = Holidays.find.where().eq("holidayDate", cal.getTime()).findUnique();
												Holidays holiday1 = Holidays.find.where().eq("correspondingWorkingDay", cal.getTime()).findUnique();
												
												if (holidays != null) {
												} else if (holidays == null && (cal.get(Calendar.DAY_OF_WEEK) == 1
														|| cal.get(Calendar.DAY_OF_WEEK) == 7) && holiday1 == null) {
												} else {
													dateLeaves.leaveDate = cal.getTime();
													if (PartialDaysEnum.valueOf(partialEnum).equals(PartialDaysEnum.START_DAY) && j == 0) {
														dateLeaves.duEnum = DurationEnum.valueOf(duration);
													} else if (PartialDaysEnum.valueOf(partialEnum).equals(PartialDaysEnum.END_DAY) && j == realDiff) {
														dateLeaves.duEnum = DurationEnum.valueOf(duration);
													} else {
														dateLeaves.duEnum = DurationEnum.FULL_DAY;
													}
													
													Date todayDate  = new Date(); 
													long diff11 = dateLeaves.leaveDate.getTime() - todayDate.getTime();
													Long days = TimeUnit.DAYS.convert(diff11, TimeUnit.MILLISECONDS);
													if(days < 15){
														dateLeaves.appliedLeaveType = AppliedLeaveType.Unplanned;
													}else{
														dateLeaves.appliedLeaveType = AppliedLeaveType.Planned;
													}
													
													appliedLeaves.dateLeaves.add(dateLeaves);
												}
											}
											appliedLeaves.save();
											leave.usedLeaves = ((float) leave.usedLeaves + diff1);
											leave.remainingLeaves = ((float) leave.remainingLeaves - diff1);
											leave.update();
											Float maildiff = diff1;
											final Promise<Boolean> emailResult = Promise
													.promise(new F.Function0<Boolean>() {
														@Override
														public Boolean apply() throws Throwable {
															// cancelledNitifications(appliedLeaves);

															return appliedNitifications(appliedLeaves);
														}
													});
											final Promise<Boolean> emailResult1 = Promise
													.promise(new F.Function0<Boolean>() {
														@Override
														public Boolean apply() throws Throwable {
															// cancelledNitifications(appliedLeaves);

															return leaveAppliedEmail(appliedLeaves, maildiff);
														}
													});
											return "successfully applied";
										} else {
											return "They are holidays";
										}
									} else {
										return "Not enough leaves";
									}
								}
								if (PartialDaysEnum.valueOf(partialEnum).equals(PartialDaysEnum.START_AND_END_DAY)) {
									Float diff2 = 0f;
									Holidays holidayss = Holidays.find.where().eq("holidayDate", comingDate).findUnique();
									Holidays holidays1 = Holidays.find.where().eq("holidayDate", todates).findUnique();
								
									if (holidayss != null && holidays1 != null) {
										diff2 = ((float) diffDates - (float) count);
									} else if (holidayss == null && holidays1 != null && (comingDate.getDay() == 6 || comingDate.getDay() == 0)) {
										
										Holidays holiday1 = Holidays.find.where().eq("correspondingWorkingDay", comingDate).findUnique();
										if (holiday1 == null) {
											diff2 = ((float) diffDates - (float) count);
										} else {
											diff2 = ((float) diffDates - (float) 0.5 - (float) count);
										}
										
									} else if (holidayss != null && holidays1 == null && (todates.getDay() == 6 || todates.getDay() == 0)) {
										
										Holidays holiday1 = Holidays.find.where().eq("correspondingWorkingDay", todates).findUnique();
										if (holiday1 == null) {
											diff2 = ((float) diffDates - (float) count);
										} else {
											diff2 = ((float) diffDates - (float) 0.5 - (float) count);
										}
										
									} else if ((comingDate.getDay() == 6 || comingDate.getDay() == 0) && (todates.getDay() == 6 || todates.getDay() == 0)) {
										Holidays holiday1 = Holidays.find.where().eq("correspondingWorkingDay", comingDate).findUnique();
										Holidays holiday2 = Holidays.find.where().eq("correspondingWorkingDay", todates).findUnique();
										
										if (holiday1 == null && holiday2 == null) {
											diff2 = ((float) diffDates - (float) count);
										} else if (holiday1 == null && holiday2 != null) {
											diff2 = ((float) diffDates - (float) 0.5 - (float) count);
										} else if (holiday1 != null && holiday2 == null) {
											diff2 = ((float) diffDates - (float) 0.5 - (float) count);
										} else {
											if (holiday1 != null && holiday2 != null) {
												diff2 = ((float) diffDates - 1 - (float) count);
											}
										}
										
									} else if ((comingDate.getDay() == 6 || comingDate.getDay() == 0)) {
										
										Holidays holiday1 = Holidays.find.where().eq("correspondingWorkingDay", comingDate).findUnique();
										if (holiday1 != null) {
											diff2 = ((float) diffDates - 1 - (float) count);
										} else {
											diff2 = ((float) diffDates - (float) 0.5 - (float) count);
										}
										
									} else if ((todates.getDay() == 6 || todates.getDay() == 0)) {
										
										Holidays holiday1 = Holidays.find.where().eq("correspondingWorkingDay", todates).findUnique();
										if (holiday1 != null) {
											diff2 = ((float) diffDates - 1 - (float) count);
										} else {
											diff2 = ((float) diffDates - (float) 0.5 - (float) count);
										}
										
									} else {
										diff2 = ((float) diffDates - 1 - (float) count);
									}

									if (diff2 <= leave.remainingLeaves) {
										if (diff2 > (float) 0) {
											AppliedLeaves appliedLeaves = new AppliedLeaves();
											appliedLeaves.leaveStatus = LeaveStatus.PENDING_APPROVAL;
											appliedLeaves.startDate = comingDate;
											appliedLeaves.endDate = todates;
											appliedLeaves.reason = reason;
											appliedLeaves.leaveType = leaveType;
											appliedLeaves.appUser = Application.getLoggedInUser();
											appliedLeaves.totalLeaves = (float) diff2;
											for (int j = 0; j <= realDiff; j++) {
												DateWiseAppliedLeaves dateLeaves = new DateWiseAppliedLeaves();
												Calendar cal = Calendar.getInstance();
												cal.setTime(comingDate);
												cal.add(Calendar.DATE, j);
												Holidays holidays = Holidays.find.where().eq("holidayDate", cal.getTime()).findUnique();
												Holidays holiday1 = Holidays.find.where().eq("correspondingWorkingDay", cal.getTime()).findUnique();
												if (holidays != null) {
													count++;
												} else if (holidays == null && (cal.get(Calendar.DAY_OF_WEEK) == 1 || cal.get(Calendar.DAY_OF_WEEK) == 7) && holiday1 == null) {
												} else {
													dateLeaves.leaveDate = cal.getTime();
													if (j == 0 || j == realDiff) {
														dateLeaves.duEnum = DurationEnum.valueOf(duration);
													} else {
														dateLeaves.duEnum = DurationEnum.FULL_DAY;
													}
													Date todayDate  = new Date(); 
													long diff1 = dateLeaves.leaveDate.getTime() - todayDate.getTime();
													Long days = TimeUnit.DAYS.convert(diff1, TimeUnit.MILLISECONDS);
													if(days < 15){
														dateLeaves.appliedLeaveType = AppliedLeaveType.Unplanned;
													}else{
														dateLeaves.appliedLeaveType = AppliedLeaveType.Planned;
													}
													appliedLeaves.dateLeaves.add(dateLeaves);
												}
											}
											appliedLeaves.save();
											leave.usedLeaves += diff2;
											leave.remainingLeaves = leave.remainingLeaves - diff2;
											leave.update();
											Float maildiff2 = diff2;
											final Promise<Boolean> emailResult = Promise
													.promise(new F.Function0<Boolean>() {
														@Override
														public Boolean apply() throws Throwable {
															// cancelledNitifications(appliedLeaves);

															return appliedNitifications(appliedLeaves);
														}
													});
											final Promise<Boolean> emailResult1 = Promise
													.promise(new F.Function0<Boolean>() {
														@Override
														public Boolean apply() throws Throwable {
															// cancelledNitifications(appliedLeaves);

															return leaveAppliedEmail(appliedLeaves, maildiff2);
														}
													});
											return "Successfully Applied";
										} else {
											return "They are holidays";
										}
									} else {
										return "Not enough leaves";
									}
								}

							}

						} catch (final Exception e) {
							Logger.error("date is not parsing", e);
						}
					}
				}
			} catch (ParseException e1) {
				e1.printStackTrace();
			}

		}
		return duration;

	}

	public Boolean appliedNitifications(AppliedLeaves appliedLeaves) {
		List<AppUser> appUsersList = new ArrayList<AppUser>();
		List<Role> rolesList = new ArrayList<>();
		// Logger.debug("inside method call");
		Role roleAdmin = Role.find.where().eq("role", "Admin").findUnique();

		rolesList.add(roleAdmin);
		Role roleHr = Role.find.where().eq("role", "HR").findUnique();
		Role roleRepManger = Role.find.where().eq("role", "Manager").findUnique();
		rolesList.add(roleHr);
		appUsersList.addAll(AppUser.find.where().in("role", rolesList).findList());
		if (appliedLeaves.appUser.getReptManager(appliedLeaves.appUser.getReportMangerId()) != null && !appUsersList
				.contains(appliedLeaves.appUser.getReptManager(appliedLeaves.appUser.getReportMangerId()))) {
			appUsersList.add(appliedLeaves.appUser.getReptManager(appliedLeaves.appUser.getReportMangerId()));
		}
		for (AppUser appUser : appUsersList) {
			if (!Application.getLoggedInUser().equals(appUser)) {
				String rejMessage1 = appliedLeaves.appUser.getAppUserFullName() + " " + "has applied leave from "
						+ fromDate + " to " + toDate + ".";
				if (appUser.role.contains(roleAdmin)) {
					if (appliedLeaves.appUser.getReptManager(appliedLeaves.appUser.getReportMangerId()) != null) {
						if (!appUser.equals(
								appliedLeaves.appUser.getReptManager(appliedLeaves.appUser.getReportMangerId()))) {
							AdminLeaveController.sendNotification(appUser, Application.getLoggedInUser(), rejMessage1,
									"/leave-tracker", roleAdmin);
						} else {
							AdminLeaveController.sendNotification(appUser, Application.getLoggedInUser(), rejMessage1,
									"/team/leave-tracker", roleRepManger);
						}
					} else {
						AdminLeaveController.sendNotification(appUser, Application.getLoggedInUser(), rejMessage1,
								"/leave-tracker", roleAdmin);
					}
				} else if (appUser.role.contains(roleHr)) {
					if (appliedLeaves.appUser.getReptManager(appliedLeaves.appUser.getReportMangerId()) != null) {
						if (!appUser.equals(
								appliedLeaves.appUser.getReptManager(appliedLeaves.appUser.getReportMangerId()))) {
							AdminLeaveController.sendNotification(appUser, Application.getLoggedInUser(), rejMessage1,
									"/leave-tracker", roleHr);
						} else {
							AdminLeaveController.sendNotification(appUser, Application.getLoggedInUser(), rejMessage1,
									"/team/leave-tracker", roleRepManger);
						}
					} else {
						AdminLeaveController.sendNotification(appUser, Application.getLoggedInUser(), rejMessage1,
								"/leave-tracker", roleHr);
					}
				} else {
					AdminLeaveController.sendNotification(appUser, Application.getLoggedInUser(), rejMessage1,
							"/team/leave-tracker", roleRepManger);
				}
			}
		}
		return true;
	}

	public Boolean leaveAppliedEmail(AppliedLeaves appliedLeaves, Float differrr) {
		AppUser appUser = Application.getLoggedInUser();
		List<AppUser> appUsersList = new ArrayList<AppUser>();
		List<Role> rolesList = new ArrayList<>();
		Role roleAdmin = Role.find.where().eq("role", "Admin").findUnique();

		rolesList.add(roleAdmin);
		Role roleHr = Role.find.where().eq("role", "HR").findUnique();
		rolesList.add(roleHr);
		appUsersList.addAll(AppUser.find.where().in("role", rolesList).findList());
		if (!appUsersList.contains(appliedLeaves.appUser.getReptManager(appliedLeaves.appUser.getReportMangerId()))) {
			appUsersList.add(appliedLeaves.appUser.getReptManager(appliedLeaves.appUser.getReportMangerId()));
		}
		String content = "This mail is to intimate you that" + " " + Application.getLoggedInUser().getAppUserFullName()
				+ " " + "has requested for leave for the below dates " + " \n\n Start date:- " + fromDate
				+ "\n\n End date:- " + toDate + "\n\n No.of Days :" + differrr;
		for (AppUser appUser2 : appUsersList) {
			if (!appUser.equals(appUser2)) {
				EmailService.sendVerificationMail(appUser2.getEmail(), content, "Request For Leave Approval !!");
			}
		}

		return true;
	}
	
	//New
	@SuppressWarnings("deprecation")
	public String toNewApplyLeavenew() {
			Anchor anchor = new Anchor("https://developer.thrymr.net/team/leave-tracker");
			anchor.setReference("https://developer.thrymr.net/team/leave-tracker");

			Integer count = 0;
			try {
					if (!fromDate.isEmpty() && !toDate.isEmpty()) {
						String thisSYear = new SimpleDateFormat("yyyy").format(new Date());
						Date thisYear = new SimpleDateFormat("yyyy").parse(thisSYear);
						try {
							final Date comingDate = new SimpleDateFormat("dd-MM-yyyy").parse(fromDate.trim());
							final Date todates = new SimpleDateFormat("dd-MM-yyyy").parse(toDate.trim());
							int realDiff = (int) ((todates.getTime() - comingDate.getTime()) / (1000 * 60 * 60 * 24));
							int diffDates = (int) ((todates.getTime() - comingDate.getTime()) / (1000 * 60 * 60 * 24))+ 1;
							
							// This for loop checking his/her already applied or not between leaves dates
							for (int j = 0; j <= realDiff; j++) {
								Calendar cal = Calendar.getInstance();
								cal.setTime(comingDate);
								cal.add(Calendar.DATE, j);
								//Logger.debug("date "+cal.getTime());
								DateWiseAppliedLeaves checkAppliedLeaves = DateWiseAppliedLeaves.find.where().eq("applyUser", Application.getLoggedInUser()).eq("leaveDate", cal.getTime()).findUnique();
								if(checkAppliedLeaves != null){
									return "You've already applied between these dates,  please check and apply again.";
								}
							}
							
							
							
							
							for (int s = 0; s <= realDiff; s++) {
								Calendar cal = Calendar.getInstance();
								cal.setTime(comingDate);
								cal.add(Calendar.DATE, s);
								Holidays holidays = Holidays.find.where().eq("holidayDate", cal.getTime()).findUnique();
								Holidays holiday1 = Holidays.find.where().eq("correspondingWorkingDay", cal.getTime()).findUnique();
								if (holidays != null) {
									count++;
								} else if (holidays == null && (cal.get(Calendar.DAY_OF_WEEK) == 1 || cal.get(Calendar.DAY_OF_WEEK) == 7 ) && holiday1 == null) {
										count++;
								}

							}
							if (DurationEnum.valueOf(duration).equals(DurationEnum.FULL_DAY)) {
								Float differrr = (float) diffDates - (float) count;
									if (differrr > (float) 0) {
										AppliedLeaves appliedLeaves = new AppliedLeaves();
										appliedLeaves.leaveStatus = LeaveStatus.PENDING_APPROVAL;
										appliedLeaves.startDate = comingDate;
										appliedLeaves.endDate = todates;
										appliedLeaves.reason = reason;
										appliedLeaves.appUser = Application.getLoggedInUser();
										appliedLeaves.totalLeaves = differrr;
										appliedLeaves.year = thisYear;
										for (int j = 0; j <= realDiff; j++) {
											DateWiseAppliedLeaves dateLeaves = new DateWiseAppliedLeaves();
											Calendar cal = Calendar.getInstance();
											cal.setTime(comingDate);
											cal.add(Calendar.DATE, j);
											Holidays holidays = Holidays.find.where().eq("holidayDate", cal.getTime()).findUnique();
											Holidays holiday1 = Holidays.find.where().eq("correspondingWorkingDay", cal.getTime()).findUnique();
											if (holidays != null) {
											} else if (holidays == null && (cal.get(Calendar.DAY_OF_WEEK) == 1 || cal.get(Calendar.DAY_OF_WEEK) == 7 ) && holiday1 == null) {
											} else {
												dateLeaves.leaveDate = cal.getTime();
												dateLeaves.duEnum = DurationEnum.valueOf(duration);
												dateLeaves.applyUser = Application.getLoggedInUser();
												
												Date todayDate  = new Date(); 
												long diff1 = dateLeaves.leaveDate.getTime() - todayDate.getTime();
												Long days = TimeUnit.DAYS.convert(diff1, TimeUnit.MILLISECONDS);
												if(days < 15){
													dateLeaves.appliedLeaveType = AppliedLeaveType.Unplanned;
												}else{
													dateLeaves.appliedLeaveType = AppliedLeaveType.Planned;
												}
												
												appliedLeaves.dateLeaves.add(dateLeaves);
											}
										}
										appliedLeaves.save();
										final Promise<Boolean> emailResult = Promise
												.promise(new F.Function0<Boolean>() {
													@Override
													public Boolean apply() throws Throwable {
														// cancelledNitifications(appliedLeaves);

														return appliedNitifications(appliedLeaves);
													}
												});

										final Promise<Boolean> emailResult1 = Promise
												.promise(new F.Function0<Boolean>() {
													@Override
													public Boolean apply() throws Throwable {
														// cancelledNitifications(appliedLeaves);

														return leaveAppliedEmail(appliedLeaves, differrr);
													}
												});
										return "Successfully Applied";
									} else {
										return "They are holidays";
									}
							}
							if (DurationEnum.valueOf(duration).equals(DurationEnum.HALF_DAY)) {
								if (PartialDaysEnum.valueOf(partialEnum).equals(PartialDaysEnum.ALL_DAYS)) {
									Float diff = (((float) diffDates - (float) count) / (float) 2);
										if (diff > (float) 0) {
											AppliedLeaves appliedLeaves = new AppliedLeaves();
											appliedLeaves.leaveStatus = LeaveStatus.PENDING_APPROVAL;
											appliedLeaves.startDate = comingDate;
											appliedLeaves.endDate = todates;
											appliedLeaves.reason = reason;
											appliedLeaves.appUser = Application.getLoggedInUser();
											appliedLeaves.totalLeaves = diff;
											appliedLeaves.year = thisYear;
											for (int j = 0; j <= realDiff; j++) {
												DateWiseAppliedLeaves dateLeaves = new DateWiseAppliedLeaves();
												Calendar cal = Calendar.getInstance();
												cal.setTime(comingDate);
												cal.add(Calendar.DATE, j);
												Holidays holidays = Holidays.find.where().eq("holidayDate", cal.getTime()).findUnique();
												Holidays holiday1 = Holidays.find.where().eq("correspondingWorkingDay", cal.getTime()).findUnique();
												if (holidays != null) {
												} else if (holidays == null && (cal.get(Calendar.DAY_OF_WEEK) == 1 || cal.get(Calendar.DAY_OF_WEEK) == 7 ) && holiday1 == null) {
												} else {
													dateLeaves.leaveDate = cal.getTime();
													dateLeaves.duEnum = DurationEnum.valueOf(duration);
													dateLeaves.applyUser = Application.getLoggedInUser();
													
													Date todayDate  = new Date(); 
													long diff1 = dateLeaves.leaveDate.getTime() - todayDate.getTime();
													Long days = TimeUnit.DAYS.convert(diff1, TimeUnit.MILLISECONDS);
													if(days < 15){
														dateLeaves.appliedLeaveType = AppliedLeaveType.Unplanned;
													}else{
														dateLeaves.appliedLeaveType = AppliedLeaveType.Planned;
													}
													appliedLeaves.dateLeaves.add(dateLeaves);
												}
											}
											appliedLeaves.save();
											final Promise<Boolean> emailResult = Promise
													.promise(new F.Function0<Boolean>() {
														@Override
														public Boolean apply() throws Throwable {
															// cancelledNitifications(appliedLeaves);

															return appliedNitifications(appliedLeaves);
														}
													});
											final Promise<Boolean> emailResult1 = Promise
													.promise(new F.Function0<Boolean>() {
														@Override
														public Boolean apply() throws Throwable {
															// cancelledNitifications(appliedLeaves);

															return leaveAppliedEmail(appliedLeaves, diff);
														}
													});
											return "successfully applied";
										} else {
											return "They are holidays";
										}
								}

								if (PartialDaysEnum.valueOf(partialEnum).equals(PartialDaysEnum.START_DAY)
										|| PartialDaysEnum.valueOf(partialEnum).equals(PartialDaysEnum.END_DAY)) {
									Logger.debug("start day" + (diffDates - 1 + 0.5));
									Float diff1 = 0f;
									if (PartialDaysEnum.valueOf(partialEnum).equals(PartialDaysEnum.START_DAY)) {
										Holidays holidays = Holidays.find.where().eq("holidayDate", comingDate)
												.findUnique();
										if (holidays != null) {
											diff1 = ((float) diffDates - (float) count);
										} else if (comingDate.getDay() == 6 || comingDate.getDay() == 0) {
											Holidays holiday1 = Holidays.find.where()
													.eq("correspondingWorkingDay", comingDate).findUnique();
											if (holiday1 == null) {
												diff1 = ((float) diffDates - (float) count);
											} else {
												diff1 = ((float) diffDates - (float) 1 + (float) 0.5 - (float) count);
											}

										} else {
											diff1 = ((float) diffDates - (float) 1 + (float) 0.5 - (float) count);
										}
									} else if (PartialDaysEnum.valueOf(partialEnum).equals(PartialDaysEnum.END_DAY)) {
										Logger.debug("dayyyy" + todates.getDay());
										Holidays holidays = Holidays.find.where().eq("holidayDate", todates)
												.findUnique();
										if (holidays != null) {
											diff1 = ((float) diffDates - (float) count);
										} else if (todates.getDay() == 6 || todates.getDay() == 0) {
											Holidays holiday1 = Holidays.find.where()
													.eq("correspondingWorkingDay", todates).findUnique();
											if (holiday1 == null) {
												diff1 = ((float) diffDates - (float) count);
											} else {
												diff1 = ((float) diffDates - (float) 1 + (float) 0.5 - (float) count);
											}

										} else {
											diff1 = ((float) diffDates - (float) 1 + (float) 0.5 - (float) count);
										}

									} else {
										diff1 = ((float) diffDates - (float) 1 + (float) 0.5 - (float) count);
									}
										if (diff1 > (float) 0) {
											AppliedLeaves appliedLeaves = new AppliedLeaves();
											appliedLeaves.leaveStatus = LeaveStatus.PENDING_APPROVAL;
											appliedLeaves.startDate = comingDate;
											appliedLeaves.endDate = todates;
											appliedLeaves.reason = reason;
											appliedLeaves.appUser = Application.getLoggedInUser();
											appliedLeaves.totalLeaves = diff1;
											appliedLeaves.year = thisYear;
											for (int j = 0; j <= realDiff; j++) {
												DateWiseAppliedLeaves dateLeaves = new DateWiseAppliedLeaves();
												Calendar cal = Calendar.getInstance();
												cal.setTime(comingDate);
												cal.add(Calendar.DATE, j);
												Holidays holidays = Holidays.find.where()
														.eq("holidayDate", cal.getTime()).findUnique();
												Holidays holiday1 = Holidays.find.where()
														.eq("correspondingWorkingDay", cal.getTime()).findUnique();
												if (holidays != null) {
												} else if (holidays == null && (cal.get(Calendar.DAY_OF_WEEK) == 1
														|| cal.get(Calendar.DAY_OF_WEEK) == 7) && holiday1 == null) {
												} else {
													dateLeaves.leaveDate = cal.getTime();
													if (PartialDaysEnum.valueOf(partialEnum)
															.equals(PartialDaysEnum.START_DAY) && j == 0) {
														dateLeaves.duEnum = DurationEnum.valueOf(duration);
													} else if (PartialDaysEnum.valueOf(partialEnum)
															.equals(PartialDaysEnum.END_DAY) && j == realDiff) {
														dateLeaves.duEnum = DurationEnum.valueOf(duration);
													} else {
														dateLeaves.duEnum = DurationEnum.FULL_DAY;
													}
													Date todayDate  = new Date(); 
													long diff11 = dateLeaves.leaveDate.getTime() - todayDate.getTime();
													Long days = TimeUnit.DAYS.convert(diff11, TimeUnit.MILLISECONDS);
													if(days < 15){
														dateLeaves.appliedLeaveType = AppliedLeaveType.Unplanned;
													}else{
														dateLeaves.appliedLeaveType = AppliedLeaveType.Planned;
													}
													
													dateLeaves.applyUser = Application.getLoggedInUser();
													appliedLeaves.dateLeaves.add(dateLeaves);
												}
											}
											appliedLeaves.save();
											Float maildiff = diff1;
											final Promise<Boolean> emailResult = Promise
													.promise(new F.Function0<Boolean>() {
														@Override
														public Boolean apply() throws Throwable {
															// cancelledNitifications(appliedLeaves);

															return appliedNitifications(appliedLeaves);
														}
													});
											final Promise<Boolean> emailResult1 = Promise
													.promise(new F.Function0<Boolean>() {
														@Override
														public Boolean apply() throws Throwable {
															// cancelledNitifications(appliedLeaves);

															return leaveAppliedEmail(appliedLeaves, maildiff);
														}
													});
											return "successfully applied";
										} else {
											return "They are holidays";
										}
								}
								if (PartialDaysEnum.valueOf(partialEnum).equals(PartialDaysEnum.START_AND_END_DAY)) {
									Float diff2 = 0f;
									Holidays holidayss = Holidays.find.where().eq("holidayDate", comingDate)
											.findUnique();
									Holidays holidays1 = Holidays.find.where().eq("holidayDate", todates).findUnique();
									if (holidayss != null && holidays1 != null) {
										diff2 = ((float) diffDates - (float) count);
									} else if (holidayss == null && holidays1 != null
											&& (comingDate.getDay() == 6 || comingDate.getDay() == 0)) {
										Holidays holiday1 = Holidays.find.where()
												.eq("correspondingWorkingDay", comingDate).findUnique();
										if (holiday1 == null) {
											diff2 = ((float) diffDates - (float) count);
										} else {
											diff2 = ((float) diffDates - (float) 0.5 - (float) count);
										}
									} else if (holidayss != null && holidays1 == null
											&& (todates.getDay() == 6 || todates.getDay() == 0)) {
										Holidays holiday1 = Holidays.find.where().eq("correspondingWorkingDay", todates)
												.findUnique();
										if (holiday1 == null) {
											diff2 = ((float) diffDates - (float) count);

										} else {
											diff2 = ((float) diffDates - (float) 0.5 - (float) count);
										}
									} else if ((comingDate.getDay() == 6 || comingDate.getDay() == 0)
											&& (todates.getDay() == 6 || todates.getDay() == 0)) {
										Holidays holiday1 = Holidays.find.where()
												.eq("correspondingWorkingDay", comingDate).findUnique();
										Holidays holiday2 = Holidays.find.where().eq("correspondingWorkingDay", todates)
												.findUnique();
										if (holiday1 == null && holiday2 == null) {
											diff2 = ((float) diffDates - (float) count);
										} else if (holiday1 == null && holiday2 != null) {
											diff2 = ((float) diffDates - (float) 0.5 - (float) count);
										} else if (holiday1 != null && holiday2 == null) {
											diff2 = ((float) diffDates - (float) 0.5 - (float) count);
										} else {
											if (holiday1 != null && holiday2 != null) {
												diff2 = ((float) diffDates - 1 - (float) count);
											}
										}
									} else if ((comingDate.getDay() == 6 || comingDate.getDay() == 0)) {
										Holidays holiday1 = Holidays.find.where()
												.eq("correspondingWorkingDay", comingDate).findUnique();
										if (holiday1 != null) {
											diff2 = ((float) diffDates - 1 - (float) count);
										} else {
											diff2 = ((float) diffDates - (float) 0.5 - (float) count);
										}
									} else if ((todates.getDay() == 6 || todates.getDay() == 0)) {
										Holidays holiday1 = Holidays.find.where().eq("correspondingWorkingDay", todates)
												.findUnique();
										if (holiday1 != null) {
											diff2 = ((float) diffDates - 1 - (float) count);
										} else {
											diff2 = ((float) diffDates - (float) 0.5 - (float) count);
										}
									} else {
										diff2 = ((float) diffDates - 1 - (float) count);
									}


										if (diff2 > (float) 0) {
											AppliedLeaves appliedLeaves = new AppliedLeaves();
											appliedLeaves.leaveStatus = LeaveStatus.PENDING_APPROVAL;
											appliedLeaves.startDate = comingDate;
											appliedLeaves.endDate = todates;
											appliedLeaves.reason = reason;
											appliedLeaves.appUser = Application.getLoggedInUser();
											appliedLeaves.totalLeaves = (float) diff2;
											appliedLeaves.year = thisYear;
											for (int j = 0; j <= realDiff; j++) {
												DateWiseAppliedLeaves dateLeaves = new DateWiseAppliedLeaves();
												Calendar cal = Calendar.getInstance();
												cal.setTime(comingDate);
												cal.add(Calendar.DATE, j);
												Holidays holidays = Holidays.find.where().eq("holidayDate", cal.getTime()).findUnique();
												Holidays holiday1 = Holidays.find.where().eq("correspondingWorkingDay", cal.getTime()).findUnique();
												if (holidays != null) {
													count++;
												} else if (holidays == null && (cal.get(Calendar.DAY_OF_WEEK) == 1 || cal.get(Calendar.DAY_OF_WEEK) == 7) && holiday1 == null) {
												} else {
													dateLeaves.leaveDate = cal.getTime();
													if (j == 0 || j == realDiff) {
														dateLeaves.duEnum = DurationEnum.valueOf(duration);
													} else {
														dateLeaves.duEnum = DurationEnum.FULL_DAY;
													}
													Date todayDate  = new Date(); 
													long diff1 = dateLeaves.leaveDate.getTime() - todayDate.getTime();
													Long days = TimeUnit.DAYS.convert(diff1, TimeUnit.MILLISECONDS);
													if(days < 15){
														dateLeaves.appliedLeaveType = AppliedLeaveType.Unplanned;
													}else{
														dateLeaves.appliedLeaveType = AppliedLeaveType.Planned;
													}
													
													dateLeaves.applyUser = Application.getLoggedInUser();
													appliedLeaves.dateLeaves.add(dateLeaves);
												}
											}
											appliedLeaves.save();
											Float maildiff2 = diff2;
											final Promise<Boolean> emailResult = Promise
													.promise(new F.Function0<Boolean>() {
														@Override
														public Boolean apply() throws Throwable {
															// cancelledNitifications(appliedLeaves);

															return appliedNitifications(appliedLeaves);
														}
													});
											final Promise<Boolean> emailResult1 = Promise
													.promise(new F.Function0<Boolean>() {
														@Override
														public Boolean apply() throws Throwable {
															// cancelledNitifications(appliedLeaves);

															return leaveAppliedEmail(appliedLeaves, maildiff2);
														}
													});
											return "Successfully Applied";
										} else {
											return "They are holidays";
										}
								}

							}

						} catch (final Exception e) {
							Logger.error("date is not parsing", e);
						}
					}
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		return duration;

	}
}
