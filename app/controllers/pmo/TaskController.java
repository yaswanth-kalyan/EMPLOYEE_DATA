package controllers.pmo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Task;
import models.pmo.PTask;
import models.pmo.UserStory;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.DateUtil;
import bean.pmo.PTaskBean;

public class TaskController  extends Controller{
	
	
	public static final Form<PTaskBean> taskForm = Form.form(PTaskBean.class);
	
	public Result createTaskByUserStory(Long userStId){
		 return ok(views.html.pmo.task.createTask.render());
	}
	
	public Result createTask(){
		 return ok(views.html.pmo.task.createTask.render());
	}
	
	public Result saveTask(){
		 final Form<PTaskBean> taskBeanForm = taskForm.bindFromRequest();
		 if(taskBeanForm.hasErrors()){
			 return ok(views.html.pmo.task.createTask.render());
		 }else{
			 PTaskBean  taskBean = taskBeanForm.get();
			 if(taskBean.taskId != null){
				 try{
					 PTask pTask = PTask.find.byId(taskBean.taskId);
					 pTask.setName(taskBean.name);
					 pTask.setDescription(taskBean.description);
					 pTask.setEstimatedTime(taskBean.estimatedTime);
					 pTask.setActualTime(taskBean.actualTime);
					 pTask.setActualStartDate(DateUtil.convertToDate(taskBean.startDate));
					 pTask.setActualEndDate(DateUtil.convertToDate(taskBean.endDate));
					 pTask.update();
					 
				 }catch(Exception e){
					 e.printStackTrace();
				 }
			 }else{
				 try{
					 PTask  pTask = new PTask();
					 pTask.setName(taskBean.name); 
					 pTask.setEstimatedTime(taskBean.estimatedTime);
					 if(taskBean.userStId != null){
						 pTask.setUserStory(UserStory.find.byId(taskBean.userStId));
					 }
					 pTask.save();
				 }catch(Exception e){
					 e.printStackTrace();
				 }
			 }
		 }
		return redirect(routes.TaskController.createTask());
	}
	
	public Result editTask(Long taskId){
		Map<String,Object> map = new HashMap<String,Object>();
		PTask pTask = PTask.find.byId(taskId);
		String page = views.html.pmo.task.editTask.render(pTask).toString();
		map.put("page", page);
		return ok(Json.toJson(map));
	}
	public Result deleteTask(Long taskId){
		Map<String,Object> map = new HashMap<String,Object>();
		if(taskId != null){
			PTask pTask = PTask.find.byId(taskId);
			try{
				pTask.delete();
				map.put("status", true);
				map.put("message", pTask.name + " Deleted SuccessFully !!!");
			}catch(Exception e){
				map.put("status", false);
				map.put("message", pTask.name + "Something Went Wrong !!!");
				e.printStackTrace();
			}
		}
		return ok(Json.toJson(map));
	}
	
	public Result taskListbyUserStory(Long userStId){
		Map<String,Object> map = new HashMap<String,Object>();
		List<PTask> pTaskList = PTask.find.where().eq("user_story_id", userStId).orderBy("id").findList();
		String page = views.html.pmo.task.allTask.render(pTaskList).toString();
		map.put("page",page);
		map.put("status", true);
		return ok(Json.toJson(map));
	}
	
	public Result taskList(){
		Map<String,Object> map = new HashMap<String,Object>();
		List<PTask> pTaskList = PTask.find.orderBy("id").findList();
		String page = views.html.pmo.task.allTask.render(pTaskList).toString();
		map.put("page",page);
		map.put("status", true);
		return ok(Json.toJson(map));
	}
}
