package bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.AppUser;
import models.TaskList;
import controllers.TaskController;


public class TaskListBean {
	public Long taskListId;
	public String title;
	public List<Long> assignFromIds;
	public String description;
	public Long getTaskListId() {
		return taskListId;
	}
	public void setTaskListId(Long taskListId) {
		this.taskListId = taskListId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<Long> getAssignFromIds() {
		return assignFromIds;
	}
	public void setAssignFromIds(List<Long> assignFromIds) {
		this.assignFromIds = assignFromIds;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public static TaskList toTaskList(TaskListBean taskListBean,AppUser appUser)
	{
		TaskList taskList=new TaskList();
		List<AppUser> appUsers=new ArrayList<AppUser>();
		taskList.setTaskListName(taskListBean.getTitle());
		if(taskListBean.getDescription()!=null){
		taskList.setDescription(taskListBean.getDescription());
		}
		if(taskListBean.getAssignFromIds()!=null&& !taskListBean.getAssignFromIds().isEmpty())
		{
			for(Long id:taskListBean.getAssignFromIds())
			{
				if(appUser.getId()==id);
				else{
				appUsers.add(AppUser.find.byId(id));
				TaskController.createNotification(AppUser.find.byId(id),appUser,appUser.getAppUserFullName()+" has added you to the TaskList "+taskListBean.getTitle(),"/taskRender");
				}
			}
			
			
		}
		appUsers.add(appUser);
		taskList.setAppUsers(appUsers);
		taskList.setCreatedBy(appUser);
		taskList.setCreationDate(new Date());
		taskList.save();
		
		return taskList;
	}
	public static TaskList updateTaskList(TaskListBean taskListBean,AppUser appUser)
	{
		TaskList taskList=TaskList.find.byId(taskListBean.getTaskListId());
		List<AppUser> appUsers=new ArrayList<AppUser>();
		taskList.setTaskListName(taskListBean.getTitle());
		if(taskListBean.getDescription()!=null){
		taskList.setDescription(taskListBean.getDescription());
		}
		if(taskListBean.getAssignFromIds()!=null&& !taskListBean.getAssignFromIds().isEmpty())
		{
			for(Long id:taskListBean.getAssignFromIds())
			{
				if(appUser.getId()==id);
				else{
				appUsers.add(AppUser.find.byId(id));
				}
			}
			
			
		}
		List<AppUser> remainingAppUser=new ArrayList<>(appUsers);
		remainingAppUser.removeAll(taskList.getAppUsers());
		for(AppUser appUserRemain:remainingAppUser)
		{
			
			TaskController.createNotification(appUserRemain,appUser,appUser.getAppUserFullName()+" has added you to the TaskList "+taskListBean.getTitle(),"/taskRender");
			
		}
		appUsers.add(appUser);
		taskList.setAppUsers(appUsers);
		taskList.update();
		
		return taskList;
	}

}
