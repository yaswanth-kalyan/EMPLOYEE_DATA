package bean;

import models.UserProjectStatus;



public class ProjectBean {

	public long pid;
	public String projectName;
	public String description;
	public String startedDate;
	public String endedDate;
	public String client;
	public long projectManagerId;
	public UserProjectStatus status;
	
	public long getPid() {
		return pid;
	}
	public void setPid(long pid) {
		this.pid = pid;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getStartedDate() {
		return startedDate;
	}
	public void setStartedDate(String startedDate) {
		this.startedDate = startedDate;
	}
	public String getEndedDate() {
		return endedDate;
	}
	public void setEndedDate(String endedDate) {
		this.endedDate = endedDate;
	}
	public String getClient() {
		return client;
	}
	public void setClient(String client) {
		this.client = client;
	}
	
	public long getProjectManagerId() {
		return projectManagerId;
	}
	public void setProjectManagerId(long projectManagerId) {
		this.projectManagerId = projectManagerId;
	}
	public UserProjectStatus getStatus() {
		return status;
	}
	public void setStatus(UserProjectStatus status) {
		this.status = status;
	}
	
	

}
