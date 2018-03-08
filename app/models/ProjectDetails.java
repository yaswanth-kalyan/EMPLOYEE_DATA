package models;

import java.util.Date;
import java.util.List;

public class ProjectDetails {
	
	public int sNo;
	public String projectName;
	

	public Date startedDate;
	
	public Date endedDate;
	
	public String client;
	public AppUser projectLeader;
	public String PmContactNo;
	public String PmMobNo;
	public List<Role> myRole;
	public UserProjectStatus status;
	
	
	
	
	public UserProjectStatus getStatus() {
		return status;
	}
	public void setStatus(UserProjectStatus status) {
		this.status = status;
	}
	public int getsNo() {
		return sNo;
	}
	public void setsNo(int sNo) {
		this.sNo = sNo;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	public Date getStartedDate() {
		return startedDate;
	}
	public void setStartedDate(Date startedDate) {
		this.startedDate = startedDate;
	}
	public Date getEndedDate() {
		return endedDate;
	}
	public void setEndedDate(Date endedDate) {
		this.endedDate = endedDate;
	}
	public String getClient() {
		return client;
	}
	public void setClient(String client) {
		this.client = client;
	}
	public AppUser getProjectLeader() {
		return projectLeader;
	}
	public void setProjectLeader(AppUser projectLeader) {
		this.projectLeader = projectLeader;
	}
	public String getPmContactNo() {
		return PmContactNo;
	}
	public void setPmContactNo(String pmContactNo) {
		PmContactNo = pmContactNo;
	}
	public String getPmMobNo() {
		return PmMobNo;
	}
	public void setPmMobNo(String pmMobNo) {
		PmMobNo = pmMobNo;
	}
	public List<Role> getMyRole() {
		return myRole;
	}
	public void setMyRole(List<Role> myRole) {
		this.myRole = myRole;
	}
	
	
	
	

}
