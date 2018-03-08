package bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.AppUser;
import models.performance.PeEmployeeAppraisal;
import models.performance.PeSelfAppraisal;

public class PEMonthEmployeesDataBean {
	
	public AppUser appUser;
	public PeSelfAppraisal peSelfAppraisal;
	public Date month;
	public List<AppUser> pmList = new ArrayList<AppUser>();
	public List<PeEmployeeAppraisal> pmPeEmployeeAppraisalList = new ArrayList<PeEmployeeAppraisal>();

}
