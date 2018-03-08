package bean;

import java.util.ArrayList;
import java.util.List;

import models.AppUser;
import models.Projects;
import models.Task;
import models.chat.ChatGroup;
public class AddTaskBean {
	
	public Long taskId;
	public String title;
	public String description;
	public Long assignToId;
	public List<Long> assignFromIds;
	public Long projectId;
	public Long chatGroupId;
	public Long statusId;
	
	public Long getStatusId() {
		return statusId;
	}
	public void setStatusId(Long statusId) {
		this.statusId = statusId;
	}
	
	
	public Long getTaskId() {
		return taskId;
	}
	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Long getAssignToId() {
		return assignToId;
	}
	public void setAssignToId(Long assignToId) {
		this.assignToId = assignToId;
	}
	public List<Long> getAssignFromIds() {
		return assignFromIds;
	}
	public void setAssignFromIds(List<Long> assignFromIds) {
		this.assignFromIds = assignFromIds;
	}
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	public Long getChatGroupId() {
		return chatGroupId;
	}
	public void setChatGroupId(Long chatGroupId) {
		this.chatGroupId = chatGroupId;
	}
	
	public static Task toTask(AddTaskBean addBeanTask)
	{ 
		List<AppUser> allUsers=new ArrayList<AppUser>(); 
		List<AppUser> assignFromList=new ArrayList<AppUser>(); 
		Task task=Task.find.byId(addBeanTask.getTaskId());
		if(addBeanTask.getAssignToId()!=null)
		{
		task.setAssignTo(AppUser.find.byId(addBeanTask.getAssignToId()));
		allUsers.add(AppUser.find.byId(addBeanTask.getAssignToId()));
		}
		if(addBeanTask.getChatGroupId()!=null)
		{
		task.setChatGroup(ChatGroup.find.byId(addBeanTask.getChatGroupId()));
		}
		task.setTitle(addBeanTask.getTitle());
		if(addBeanTask.getDescription()!=null)
		{
		task.setDescription(addBeanTask.getDescription());
		}
		if(addBeanTask.getProjectId()!=null)
		{
		task.setProject(Projects.find.byId(addBeanTask.getProjectId()));
		}
		//task.setCreationDate(new Date());
		
		if(addBeanTask.getAssignFromIds()!=null&& !addBeanTask.getAssignFromIds().isEmpty())
		{
		for(Long id:addBeanTask.getAssignFromIds())
		{
			AppUser appUser=AppUser.find.byId(id);
			assignFromList.add(appUser);
		}
		task.setAssignFrom(assignFromList);
		allUsers.addAll(assignFromList);
		task.setAppUsers(allUsers);
		}
		
		return task;
	}
 public static boolean compare(List<Task> allTask , List<Task> someTask)
 {
	 boolean flag=false;
	 for(Task some : someTask)
	 {
		 if(allTask.contains(some)){
			 flag=true;
			 break;
		 }
		 
	 }
	 return flag;
 }

}
