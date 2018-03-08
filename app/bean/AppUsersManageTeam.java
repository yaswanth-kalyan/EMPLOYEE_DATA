package bean;

import java.util.List;

public class AppUsersManageTeam {
	
	private List<Long>  appUsersAll,appUsersSome;;
    private String projectName;
    
	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public List<Long> getAppUsersAll() {
		return appUsersAll;
	}

	public void setAppUsersAll(List<Long> appUsersAll) {
		this.appUsersAll = appUsersAll;
	}

	public List<Long> getAppUsersSome() {
		return appUsersSome;
	}

	public void setAppUsersSome(List<Long> appUsersSome) {
		this.appUsersSome = appUsersSome;
	}

	
	
	
	

}
