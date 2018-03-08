package models;

public class UserDetails {
	
	private String projectName;
	private String role;
	private UserProjectStatus status;
	
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public UserProjectStatus getStatus() {
		return status;
	}
	public void setStatus(UserProjectStatus status) {
		this.status = status;
	}
	
	

}
