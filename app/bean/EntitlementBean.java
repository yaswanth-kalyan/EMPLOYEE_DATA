package bean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import models.AppUser;
import models.UserProjectStatus;
import models.leave.Entitlement;
import models.leave.LeaveStatus;
import models.leave.LeaveType;
import models.leave.Leaves;
import play.Logger;

public class EntitlementBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Long id;
	public List<Long> employee;
	public Long leaveTypee;
	public String workedDate;
	public String leavePeriod;
	public Float numDays;

	public Entitlement toEntitlement() {
		Entitlement entitlement = null;

		if (id != null && !id.toString().isEmpty()) {

		} else {
			//if (numDays > 0.0) {
				entitlement = new Entitlement();
				if (leaveTypee != null && !leaveTypee.toString().isEmpty()) {
					LeaveType leaveTypes = LeaveType.find.byId(this.leaveTypee);
					if (leaveTypes != null) {
						entitlement.leaveType = leaveTypes;
						entitlement.workedDate = workedDate;
					}
					// Logger.debug(employee+"<<<< employees");
					try {
						final Date comDay = new SimpleDateFormat("yyyy").parse(leavePeriod.trim());
						//String xx = new SimpleDateFormat("yyyy").format(new Date());
						entitlement.leavePeriod = comDay;
						if (!employee.contains(0l)) {
							for (int i = 0; i < employee.size(); i++) {
								AppUser appUser = AppUser.find.byId(employee.get(i));
								if (appUser != null) {
									entitlement.appUserList.add(appUser);

									Leaves leavenew = Leaves.find.where().eq("appUser", appUser).eq("leaveType", leaveTypes).eq("year", comDay).findUnique();
									if (leavenew != null) {
										leavenew.addedLeaves += numDays;
										leavenew.remainingLeaves += numDays;
										leavenew.year = comDay;
										leavenew.update();
									} else {
										Leaves leaves = new Leaves();
										leaves.leaveType = leaveTypes;
										leaves.leaveStatus = LeaveStatus.NOT_APPLIED;
										leaves.appUser = appUser;
										leaves.year = comDay;
										leaves.addedLeaves = numDays;
										leaves.remainingLeaves = numDays;
										leaves.save();

									}

								}
							}
						} else {
							//Logger.debug(employee + "<<<< employees");
							entitlement.appUserList.addAll(AppUser.find.where().eq("status", UserProjectStatus.Active).findList());
							for (AppUser appUser : AppUser.find.where().eq("status", UserProjectStatus.Active)
									.findList()) {
								Leaves leavenew = Leaves.find.where().eq("appUser", appUser).eq("leaveType", leaveTypes).eq("year", comDay).findUnique();
								if (leavenew != null) {
									leavenew.addedLeaves += numDays;
									leavenew.remainingLeaves += numDays;
									leavenew.year = comDay;
									leavenew.update();
								} else {
									Leaves leaves = new Leaves();
									leaves.leaveType = leaveTypes;
									leaves.leaveStatus = LeaveStatus.NOT_APPLIED;
									leaves.appUser = appUser;
									leaves.year = comDay;
									//Logger.debug(numDays + "leavesssssss");
									leaves.addedLeaves = numDays;
									leaves.remainingLeaves = numDays;
									leaves.save();
								}

							}
						}
					} catch (final Exception e) {
						e.printStackTrace();
						//Logger.error("date is not parsing", e);
					}

					entitlement.noOfDays = numDays;
					entitlement.save();
				}
			}
		//}
		return entitlement;

	}

}
