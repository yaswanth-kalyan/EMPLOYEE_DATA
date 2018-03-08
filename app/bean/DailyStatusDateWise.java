package bean;

import models.AppUser;
import models.UsersDailyReport;

public class DailyStatusDateWise {

	public UsersDailyReport usersDailyReport;
    public AppUser appUser;
    
    public UsersDailyReport getUsersDailyReport() {
        return usersDailyReport;
    }
    public void setUsersDailyReport(UsersDailyReport usersDailyReport) {
        this.usersDailyReport = usersDailyReport;
    }
    public AppUser getAppUser() {
        return appUser;
    }
    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }
}
