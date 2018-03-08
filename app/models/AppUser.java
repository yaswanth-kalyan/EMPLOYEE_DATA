package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import models.leave.Entitlement;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class AppUser extends Model {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	

	@Column(unique = true)
	@JsonIgnore
	public String FullName;
	
	@Column(unique = true)
	@JsonIgnore
	public String email;
	
	@JsonIgnore
	public String password;
	
	@Column(unique = true)
	@JsonIgnore
	public long mobileNo;
	
	@JsonIgnore
	public Gender gender;
	
	@JsonIgnore
	public String organisation;
	
	@JsonIgnore
	public String jobTitle;
	
	@JsonIgnore
	public String reMgnr;
	
	@JsonIgnore
	public long reportMangerId;
	
	@JsonIgnore
	@Temporal(TemporalType.DATE)
	public Date joinedDate;
	
	@JsonIgnore
	@Temporal(TemporalType.DATE)
	public Date dob;
	
	@Lob
	@JsonIgnore
	public byte[] image;
	
	@Lob
	@JsonIgnore
	public byte[] thumbnail;
	
	@JsonIgnore
	public UserProjectStatus status;
	
	@JsonIgnore
	public Boolean isPasswordChange = Boolean.TRUE;
	
	@JsonIgnore
	public Boolean loginCheck;
	
	@JsonIgnore
	public String gitId;
	@JsonIgnore
	public String employeeId;
	
	@Column(unique = true)
	@JsonIgnore
	public Long esslId;
	
	@JsonIgnore
	public ProbationPeriod experience = ProbationPeriod.No;
	
	@JsonIgnore
	@ManyToMany
	@JoinTable(name = "AppUsers_Role")
	public List<Role> role = new ArrayList<Role>();
	
	@JsonIgnore
	@ManyToMany
	@JoinTable(name = "AppUsers_Projects")
	public List<Projects> projects = new ArrayList<Projects>();
	
	@Column(columnDefinition="TEXT")
	@JsonIgnore
	public String socialId;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JsonIgnore
	public List<Attendance> attendences=new ArrayList<Attendance>();
	
	@JsonIgnore
	@ManyToMany
	@JoinTable(name="appUser_Task_AllUser")
	public List<Task> tasks;
	
	@JsonIgnore
	@ManyToMany
	@JoinTable(name="taskList_appUsers")
	public List<TaskList> taskList;
	
	@Column(unique = true)
	public String userName;
    
	@JsonIgnore
	@ManyToMany
	@JoinTable(name="entitlement_app_user")
	public List<Entitlement> entitlement;

	public List<TaskList> getTaskList() {
		return taskList;
	}

	public void setTaskList(List<TaskList> taskList) {
		this.taskList = taskList;
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@JsonIgnore
	public String getFullName() {
		return FullName;
	}

	public void setFullName(String fullName) {
		FullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public long getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(long mobileNo) {
		this.mobileNo = mobileNo;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getOrganisation() {
		return organisation;
	}

	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getReMgnr() {
		return reMgnr;
	}

	public void setReMgnr(String reMgnr) {
		this.reMgnr = reMgnr;
	}

	public long getReportMangerId() {
		return reportMangerId;
	}

	public void setReportMangerId(long reportMangerId) {
		this.reportMangerId = reportMangerId;
	}

	public Date getJoinedDate() {
		return joinedDate;
	}

	public void setJoinedDate(Date joinedDate) {
		this.joinedDate = joinedDate;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public byte[] getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(byte[] thumbnail) {
		this.thumbnail = thumbnail;
	}

	public UserProjectStatus getStatus() {
		return status;
	}

	public void setStatus(UserProjectStatus status) {
		this.status = status;
	}

	public Boolean getIsPasswordChange() {
		return isPasswordChange;
	}

	public void setIsPasswordChange(Boolean isPasswordChange) {
		this.isPasswordChange = isPasswordChange;
	}

	public Boolean getLoginCheck() {
		return loginCheck;
	}

	public void setLoginCheck(Boolean loginCheck) {
		this.loginCheck = loginCheck;
	}

	public String getGitId() {
		return gitId;
	}

	public void setGitId(String gitId) {
		this.gitId = gitId;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public Long getEsslId() {
		return esslId;
	}

	public void setEsslId(Long esslId) {
		this.esslId = esslId;
	}

	public List<Role> getRole() {
		return role;
	}

	public void setRole(List<Role> role) {
		this.role = role;
	}

	public List<Projects> getProjects() {
		return projects;
	}

	public void setProjects(List<Projects> projects) {
		this.projects = projects;
	}

	public String getSocialId() {
		return socialId;
	}

	public void setSocialId(String socialId) {
		this.socialId = socialId;
	}

	public List<Attendance> getAttendences() {
		return attendences;
	}

	public void setAttendences(List<Attendance> attendences) {
		this.attendences = attendences;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Date getDob() {
		return dob;
	}
	public void setDob(Date dob) {
		this.dob = dob;
	}
	public List<Entitlement> getEntitlement() {
		return entitlement;
	}
	public void setEntitlement(List<Entitlement> entitlement) {
		this.entitlement = entitlement;
	}

	@Override
	public String toString() {
		return "AppUser [id=" + id + ", FullName=" + FullName + ", email="
				+ email + ", password=" + password + ", mobileNo=" + mobileNo
				+ ", gender=" + gender + ", organisation=" + organisation
				+ ", jobTitle=" + jobTitle + ", reMgnr=" + reMgnr
				+ ", reportMangerId=" + reportMangerId + ", joinedDate="
				+ joinedDate + ", image=" + Arrays.toString(image)
				+ ", thumbnail="+ Arrays.toString(thumbnail)+", status="
				+ status + ", isPasswordChange=" + isPasswordChange
				+ ", loginCheck=" + loginCheck + ", gitId=" + gitId
				+ ", employeeId=" + employeeId + ", esslId=" + esslId
				+ ", role=" + role + ", projects=" + projects + ", socialId="
				+ socialId + ", attendences=" + attendences + ", tasks="
				+ tasks + ", userName=" + userName + "]";
	}

	public static Model.Finder<Long, AppUser> find = new Model.Finder<Long, AppUser>(AppUser.class);

	public static List<String> getUsernameList(){
		List<String> userNameList=new ArrayList<String>();
		for (AppUser appUser : AppUser.find.all()) {
			userNameList.add(appUser.userName);
		}
		return userNameList;
		
	}
	
	public List<String> getAppUserRoleList() {
		List<String> roleList = new ArrayList<String>();
		for (Role role : this.getRole()) {
			roleList.add(role.getRole());
		}
		return roleList;
	}

	public static List<AppUser> getAppManager() {
		List<AppUser> managerUsers = new ArrayList<AppUser>();
		Role role = null;
		role = Role.find.where().eq("role", "Manager").findUnique();
		if (role != null && !role.getAppUsers().isEmpty()) {
			for (AppUser appUs : role.getAppUsers()) {
				if (appUs.getStatus().equals(UserProjectStatus.Active)) {
					managerUsers.add(appUs);
				}
			}
		} else {
			role = Role.find.where().eq("role", "Admin").findUnique();
			for (AppUser appUs : role.getAppUsers()) {
				if (appUs.getStatus().equals(UserProjectStatus.Active)) {
					managerUsers.add(appUs);
				}
			}
		}
		return managerUsers;
	}

	public static String getAppUserName(String id) {
		String name = "";
		AppUser appUserName = AppUser.find.byId(Long.parseLong(id));
		if (appUserName != null) {
			return appUserName.getFullName();
		} else {
			return name;
		}
	}

	public static String getAppUserName(Long id) {
		String name = "";
		AppUser user = AppUser.find.byId(id);
		if (user != null) {
			return user.FullName;
		} else {
			return name;
		}

	}
	
	@JsonIgnore
	public String getAppUserFullName(){
		return FullName;
		
	}
	
	public static AppUser getReptManager(Long id){
		AppUser appUser = AppUser.find.byId(id);
		return appUser;
	}
	
	public static List<AppUser> getActiveUserList() {
		List<AppUser> appUsersList = AppUser.find.where().eq("status", UserProjectStatus.Active).findList();
		return appUsersList;
	}
}
