package controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Alert;
import models.AppUser;
import models.Projects;
import models.Task;
import models.TaskComment;
import models.TaskList;
import models.TaskStatus;
import models.UserProjectStatus;
import models.chat.ChatGroup;
import models.lead.NotificationAlert;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import bean.AddTaskBean;
import bean.TaskCommentBean;
import bean.TaskListBean;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;

public class TaskController extends Controller {
	public static final Form<TaskListBean> taskListForm = Form
			.form(TaskListBean.class);
	public static final Form<AddTaskBean> form = Form.form(AddTaskBean.class);
	public static final Form<TaskComment> form1 = Form.form(TaskComment.class);
	public static final Form<TaskStatus> taskStatusForm = Form
			.form(TaskStatus.class);

	public static void createNotification(final AppUser notifiedTo,
			final AppUser notifiedBy, final String message, final String url) {

		System.out.println("create notifications");
		try {
			final NotificationAlert notification = new NotificationAlert();
			// Logger.debug("notifiedTo>>>>>"+notifiedTo);
			notification.notifiedBy = notifiedBy;
			notification.notifiedTo = notifiedTo;
			notification.url = url;
			notification.notification = message;
			notification.notificationDate = new Date();
			notification.save();

		} catch (final Exception e) {
			e.printStackTrace();
			

		}

	}

	public Result taskRender1() {

		List<AppUser> appUsers = AppUser.find.where()
				.eq("status", UserProjectStatus.Active).findList();
		appUsers.remove(AppUser.find.byId(Long.parseLong(session("AppUserId"))));
		List<TaskList> taskList = AppUser.find.byId(
				Long.parseLong(session("AppUserId"))).getTaskList();
		List<Task> listOfTask = getTasks();
		Integer starCount = listOfTask.size();
		return ok(views.html.task.task.render(appUsers, taskList, null, null,
				starCount));
	}

	public Result taskRenderMarkUnMark() {
		session("fetchStarTask", "false");
		session("allTask", "false");
		session("assignMe", "false");
		List<AppUser> appUsers = AppUser.find.where()
				.eq("status", UserProjectStatus.Active).findList();
		appUsers.remove(AppUser.find.byId(Long.parseLong(session("AppUserId"))));
		List<TaskList> taskList = AppUser.find.byId(
				Long.parseLong(session("AppUserId"))).getTaskList();
		List<Task> listOfTask = getTasks();
		Integer starCount = listOfTask.size();
		return ok(views.html.task.task.render(appUsers, taskList, null, null,
				starCount));
	}

	public Result fetchAllTask() {
		session("fetchStarTask", "false");
		session("taskName", "empty");
		session("assignMe", "false"); 
		List<AppUser> appUsers = AppUser.find.where()
				.eq("status", UserProjectStatus.Active).findList();
		appUsers.remove(AppUser.find.byId(Long.parseLong(session("AppUserId"))));
		List<TaskList> taskList = AppUser.find.byId(
				Long.parseLong(session("AppUserId"))).getTaskList();
		List<Task> listOfTask = getTasks();
		Integer starCount = listOfTask.size();
		return ok(views.html.task.task.render(appUsers, taskList, null, null,
				starCount));

	}

	public Result fetchAssignToMeTask() {
		session("fetchStarTask", "false");
		session("taskName", "empty");
		session("allTask", "false");

		List<AppUser> appUsers = AppUser.find.where()
				.eq("status", UserProjectStatus.Active).findList();
		appUsers.remove(AppUser.find.byId(Long.parseLong(session("AppUserId"))));
		List<TaskList> taskList = AppUser.find.byId(
				Long.parseLong(session("AppUserId"))).getTaskList();
		List<Task> listOfTask = getTasks();
		Integer starCount = listOfTask.size();
		return ok(views.html.task.task.render(appUsers, taskList, null, null,
				starCount));

	}

	public Result taskRender() {
		session("fetchStarTask", "false");
		session("taskName", "empty");
		session("allTask", "false");
		session("assignMe", "false");
		AppUser appUser = AppUser.find.byId(Long
				.parseLong(session("AppUserId")));
		TaskList taskList1 = TaskList.find.where()
				.eq("taskListName", appUser.getId() + "inbox").findUnique();
		if (taskList1 == null) {
			taskList1 = new TaskList();
			taskList1.setCreatedBy(appUser);
			taskList1.setCreationDate(new Date());
			taskList1.setTaskListName(appUser.getId() + "inbox");
			taskList1.save();
		}
		List<AppUser> appUsers = AppUser.find.where()
				.eq("status", UserProjectStatus.Active).findList();
		appUsers.remove(AppUser.find.byId(Long.parseLong(session("AppUserId"))));
		List<TaskList> taskList = AppUser.find.byId(
				Long.parseLong(session("AppUserId"))).getTaskList();
		List<Task> listOfTask = getTasks();
		Integer starCount = listOfTask.size();
		return ok(views.html.task.task.render(appUsers, taskList, null, null,
				starCount));
	}

	public Result showNotificationTask(Long taskListId) {
                session("fetchStarTask", "false");
		session("taskName", "empty");
		session("allTask", "false");
		session("assignMe", "false");
		List<AppUser> appUsers = AppUser.find.where()
				.eq("status", UserProjectStatus.Active).findList();
		appUsers.remove(AppUser.find.byId(Long.parseLong(session("AppUserId"))));
		List<TaskList> taskList = AppUser.find.byId(
				Long.parseLong(session("AppUserId"))).getTaskList();
		TaskList taskListObj = TaskList.find.byId(taskListId);
		List<Task> listOfTask = getTasks();
		Integer starCount = listOfTask.size();
		return ok(views.html.task.task.render(appUsers, taskList, taskListObj,
				null, starCount));
	}

	public Result showNotificationTaskEdit(Long taskId) {

                session("fetchStarTask", "false");
		session("taskName", "empty");
		session("allTask", "false");
		session("assignMe", "false");
		Task task = Task.find.byId(taskId);
		List<AppUser> appUsers = AppUser.find.where()
				.eq("status", UserProjectStatus.Active).findList();
		appUsers.remove(AppUser.find.byId(Long.parseLong(session("AppUserId"))));
		List<TaskList> taskList = AppUser.find.byId(
				Long.parseLong(session("AppUserId"))).getTaskList();
		TaskList taskListObj = null;
		if (task != null) {
			taskListObj = TaskList.find.byId(task.getTaskList().getId());
		}
		;
		List<Task> listOfTask = getTasks();
		Integer starCount = listOfTask.size();
		return ok(views.html.task.task.render(appUsers, taskList, taskListObj,
				task, starCount));
	}

	public Result addTaskList() {
		try {
			TaskListBean taskListBean = taskListForm.bindFromRequest().get();
			AppUser appUser = AppUser.find.byId(Long
					.parseLong(session("AppUserId")));
			TaskListBean.toTaskList(taskListBean, appUser);
			List<TaskList> taskList = AppUser.find.byId(
					Long.parseLong(session("AppUserId"))).getTaskList();
			List<AppUser> appUsers = AppUser.find.where()
					.eq("status", UserProjectStatus.Active).findList();
		} catch (Exception e) {
			flash().put(
					"alert",
					new Alert("alert-danger",
							"Duplicate Task List not allowed try some different Name ! ")
							.toString());
		}
		return redirect(routes.TaskController.taskRender());
	}

	public Result editTaskList() {

		TaskListBean taskListBean = taskListForm.bindFromRequest().get();
		AppUser appUser = AppUser.find.byId(Long
				.parseLong(session("AppUserId")));
		TaskListBean.updateTaskList(taskListBean, appUser);
		List<TaskList> taskList = AppUser.find.byId(
				Long.parseLong(session("AppUserId"))).getTaskList();
		List<AppUser> appUsers = AppUser.find.where()
				.eq("status", UserProjectStatus.Active).findList();
		return redirect(routes.TaskController.taskRender());

	}

	public Result fetchTaskList(Long taskListId) {
		// List<AppUser> appUsers=AppUser.find.where().eq("status",
		// UserProjectStatus.Active).findList();
		// List<TaskList>
		// taskList=AppUser.find.byId(Long.parseLong(session("AppUserId"))).getTaskList();
		session("fetchStarTask", "false");
		session("taskName", "empty");
		session("allTask", "false");
		session("assignMe", "false");
		TaskList taskList1 = TaskList.find.byId(taskListId);
		List<Task> tasks = Ebean.createQuery(Task.class).where()
				.eq("taskList", taskList1).orderBy("creationDate").findList();
		Collections.reverse(tasks);
		return ok(views.html.task.allTasks.render(tasks, null));
	}

	public Result addTask() {
		AddTaskBean bean = form.bindFromRequest().get();
		TaskList taskList1 = TaskList.find.byId(bean.getTaskId());
		Task task = new Task();
		TaskStatus taskStatus = TaskStatus.find.where().eq("status", "open")
				.findUnique();
		if (taskStatus == null) {
			taskStatus = new TaskStatus();
			taskStatus.setStatus("open");
			taskStatus.save();
		}
		task.setStatus(taskStatus);
		task.setAppUsers(taskList1.getAppUsers());
		task.setTitle(bean.getTitle());
		task.setCreatedBy(AppUser.find.byId(Long
				.parseLong(session("AppUserId"))));
		task.setCreationDate(new Date());
		task.setTaskList(taskList1);
		task.save();
		for (AppUser appUser : taskList1.getAppUsers()) {
			if (appUser.equals(AppUser.find.byId(Long
					.parseLong(session("AppUserId")))))
				;
			else {
				createNotification(appUser, AppUser.find.byId(Long
						.parseLong(session("AppUserId"))),
						AppUser.find.byId(Long.parseLong(session("AppUserId")))
								.getAppUserFullName()
								+ " has added a Task: '"
								+ bean.getTitle()
								+ "' in TaskList: '"
								+ taskList1.getTaskListName() + "'",
						"/showNotificationTask/" + task.getTaskList().getId());
			}
		}
		List<Task> tasks = Ebean.createQuery(Task.class).where()
				.eq("taskList", taskList1).orderBy("creationDate").findList();
		Collections.reverse(tasks);
	//	Logger.info("task saved");
		return ok(views.html.task.allTasks.render(tasks, null));

	}

	public Result editTask(Long id) {
		Task task = Task.find.byId(id);
		session("isTaskUpdate", "No");
		System.out.println("edit task");
		return ok(views.html.task.editTask.render(task));
		
	}

	public Result editTaskAction() {
		session("isTaskUpdate", "Yes");
		AddTaskBean addTaskBean = form.bindFromRequest().get();
		Task task = AddTaskBean.toTask(addTaskBean);
		task.update();
		for (AppUser appUser : task.getTaskList().getAppUsers()) {
			if (appUser.equals(AppUser.find.byId(Long
					.parseLong(session("AppUserId")))))
				;
			else {
				if (addTaskBean.assignToId != null
						&& appUser.equals(AppUser.find
								.byId(addTaskBean.assignToId))) {
					createNotification(appUser, AppUser.find.byId(Long
							.parseLong(session("AppUserId"))), AppUser.find
							.byId(Long.parseLong(session("AppUserId")))
							.getAppUserFullName()
							+ " has assigned Task: '"
							+ task.getTitle()
							+ "' to you in TaskList: '"
							+ task.getTaskList().getTaskListName() + "'",
							"/showNotificationTaskEdit/" + task.getId());
				} else {
					createNotification(appUser, AppUser.find.byId(Long
							.parseLong(session("AppUserId"))), AppUser.find
							.byId(Long.parseLong(session("AppUserId")))
							.getAppUserFullName()
							+ " has modified Task: '"
							+ task.getTitle()
							+ "' in TaskList: '"
							+ task.getTaskList().getTaskListName() + "'",
							"/showNotificationTaskEdit/" + task.getId());
				}
			}
		}

		return ok(views.html.task.editTask.render(task));
	}

	public Result taskComment() {
		TaskCommentBean taskCommentBean = Form.form(TaskCommentBean.class)
				.bindFromRequest().get();
		Task task = Task.find.byId(taskCommentBean.getTaskId());
		TaskComment taskComment = new TaskComment();
		taskComment.setAppUser(AppUser.find.byId(Long
				.parseLong(session("AppUserId"))));
		taskComment.setCommentDate(new Date());
		taskComment.setComment(taskCommentBean.getComment());
		taskComment.setTask(task);
		taskComment.save();
		List<TaskComment> taskComments;
		taskComments = task.getComments();
		taskComments.add(taskComment);
		task.setComments(taskComments);
		task.update();
		for (AppUser appUser : task.getTaskList().getAppUsers()) {
			if (appUser.equals(AppUser.find.byId(Long
					.parseLong(session("AppUserId")))))
				;
			else {
				createNotification(appUser, AppUser.find.byId(Long
						.parseLong(session("AppUserId"))),
						AppUser.find.byId(Long.parseLong(session("AppUserId")))
								.getAppUserFullName()
								+ " has commented on Task: '"
								+ task.getTitle()
								+ "' in TaskList: '"
								+ task.getTaskList().getTaskListName() + "'",
						"/showNotificationTaskEdit/" + task.getId());
			}

		}
		return redirect(routes.TaskController.editTask(taskCommentBean
				.getTaskId()));

	}

	public Result editTaskStatus(String status, Long taskId) {
		Task task = Task.find.byId(taskId);
		if (status.equals("open")) {
			TaskStatus taskStatus = TaskStatus.find.where()
					.eq("status", "open").findUnique();
			task.setStatus(taskStatus);
			task.update();
			for (AppUser appUser : task.getTaskList().getAppUsers()) {
				if (appUser.equals(AppUser.find.byId(Long
						.parseLong(session("AppUserId")))))
					;
				else {
					createNotification(
							appUser,
							AppUser.find.byId(Long
									.parseLong(session("AppUserId"))),
							"Task: '"
									+ task.getTitle()
									+ "' is Reopened by "
									+ AppUser.find
											.byId(Long
													.parseLong(session("AppUserId")))
											.getAppUserFullName()
									+ " in TaskList: '"
									+ task.getTaskList().getTaskListName()
									+ "'", "/showNotificationTask/"
									+ task.getTaskList().getId());
				}

			}

		} else {
			TaskStatus taskStatus = TaskStatus.find.where()
					.eq("status", "close").findUnique();
			if (taskStatus == null) {
				taskStatus = new TaskStatus();
				taskStatus.setStatus("close");
				taskStatus.save();
			}
			task.setStatus(taskStatus);
			task.update();
			for (AppUser appUser : task.getTaskList().getAppUsers()) {
				if (appUser.equals(AppUser.find.byId(Long
						.parseLong(session("AppUserId")))))
					;
				else {
					createNotification(
							appUser,
							AppUser.find.byId(Long
									.parseLong(session("AppUserId"))),
							"Task: '"
									+ task.getTitle()
									+ "' is closed by "
									+ AppUser.find
											.byId(Long
													.parseLong(session("AppUserId")))
											.getAppUserFullName()
									+ " in TaskList: '"
									+ task.getTaskList().getTaskListName()
									+ "'", "/showNotificationTask/"
									+ task.getTaskList().getId());
				}

			}
		}

		List<Task> tasks = Ebean.createQuery(Task.class).where()
				.eq("taskList", task.getTaskList()).orderBy("creationDate")
				.findList();
		Collections.reverse(tasks);
		if (session("fetchStarTask").equals("true")) {
			List<Task> listOfTask = searchTasks();
			AppUser appUser = AppUser.find.byId(Long
					.parseLong(session("AppUserId")));
			return redirect(routes.TaskController.fetchStarTask());
		} else if (!session("taskName").equals("empty")) {
			AppUser appUser = AppUser.find.byId(Long
					.parseLong(session("AppUserId")));
			List<TaskList> taskLists = appUser.getTaskList();
			List<Task> listOfTask = new ArrayList<>();
			for (TaskList taskList : taskLists) {
				List<Task> tasks1 = Ebean
						.createQuery(Task.class)
						.where(Expr.and(
								Expr.eq("taskList", taskList),
								Expr.like("title", "%" + session("taskName")
										+ "%"))).findList();
				listOfTask.addAll(tasks1);
			}
                      
			return ok(views.html.task.allTasks.render(listOfTask,
					appUser.getTaskList()));

		} else if (session("allTask").equals("true")) {
			return redirect(routes.TaskController.getAllTasks());
		} else if (session("assignMe").equals("true")) {
			return redirect(routes.TaskController.assignMe());
		}

		else {
			return ok(views.html.task.allTasks.render(tasks, null));
		}

	}

	public Result deleteTaskList(Long id) {
		TaskList taskList = TaskList.find.byId(id);
                if(AppUser.find.byId(Long.parseLong(session("AppUserId"))).equals(taskList.getCreatedBy())){
		for (AppUser appUser : taskList.getAppUsers()) {
			if (appUser.equals(AppUser.find.byId(Long
					.parseLong(session("AppUserId")))))
				;
			else {
				createNotification(
						appUser,
						AppUser.find.byId(Long.parseLong(session("AppUserId"))),
						"TaskList : '"
								+ taskList.getTaskListName()
								+ "' is deleted by "
								+ AppUser.find.byId(
										Long.parseLong(session("AppUserId")))
										.getAppUserFullName(), "/taskRender");
				System.out.println("delete task List");
			}

		}
		taskList.setAppUsers(new ArrayList<>());
		;
		taskList.update();
		taskList.delete();
		flash().put(
				"alert",
				new Alert("alert-danger", "TaskList deleted ! "
						+ taskList.getTaskListName()).toString());
}else{
   flash().put(
				"alert",
				new Alert("alert-danger", "You dont have Permission to Delete TaskList! "
						+ taskList.getTaskListName()).toString());
}
		return redirect(routes.TaskController.taskRender());

	}

	public Result deleteTask(Long id) {
		// AddTaskBean addTaskBean = Form.form(AddTaskBean.class)
		// .bindFromRequest().get();
		// Logger.info(addTaskBean.getTaskId() + "");
                boolean flag=false;
		Task task = Task.find.byId(id);
                 if(AppUser.find.byId(Long.parseLong(session("AppUserId"))).equals(task.getCreatedBy())){
                
		task.setAppUsers(new ArrayList<>());
		task.update();
		task.delete();
                flag=true;
		for (AppUser appUser : task.getTaskList().getAppUsers()) {
			if (appUser.equals(AppUser.find.byId(Long
					.parseLong(session("AppUserId")))))
				;
			else {
				createNotification(
						appUser,
						AppUser.find.byId(Long.parseLong(session("AppUserId"))),
						"Task: '"
								+ task.getTitle()
								+ "' is deleted by "
								+ AppUser.find.byId(
										Long.parseLong(session("AppUserId")))
										.getAppUserFullName()
								+ " in TaskList: '"
								+ task.getTaskList().getTaskListName() + "'",
						"/showNotificationTask/" + task.getTaskList().getId());
			}

		}
		flash().put(
				"alert",
				new Alert("alert-danger", "Task deleted ! " + task.getTitle())
						.toString());
                }
		TaskList taskList = task.getTaskList();
		List<Task> tasks = Ebean.createQuery(Task.class).where()
				.eq("taskList", taskList).orderBy("creationDate").findList();
		Collections.reverse(tasks);
                if(flag==false)
{
flash().put(
				"alert",
				new Alert("alert-danger", "You dont have permission to delete Task! " + task.getTitle())
						.toString());

}

		if (session("fetchStarTask").equals("true")) {
			List<Task> listOfTask = searchTasks();
			AppUser appUser = AppUser.find.byId(Long
					.parseLong(session("AppUserId")));

			System.out.println("mark Un marks");
			return redirect(routes.TaskController.taskRender1());
		} else if (!session("taskName").equals("empty")) {
		//	Logger.info("search task");
			return redirect(routes.TaskController.taskRenderMarkUnMark());

		} else if (session("allTask").equals("true")) {
			return redirect(routes.TaskController.fetchAllTask());
		} else if (session("assignMe").equals("true")) {
			return redirect(routes.TaskController.fetchAssignToMeTask());
		}

		else {
			return redirect(routes.TaskController.showNotificationTask(taskList
					.getId()));
			// return ok(views.html.task.allTasks.render(tasks, null));
		}
	}

	public Result markTask(Long id) {
		// AddTaskBean addTaskBean = Form.form(AddTaskBean.class)
		// .bindFromRequest().get();
		Task task = Task.find.byId(id);
		if (task.isTaskMark() == false) {
			task.setTaskMark(true);
			for (AppUser appUser : task.getTaskList().getAppUsers()) {
				if (appUser.equals(AppUser.find.byId(Long
						.parseLong(session("AppUserId")))))
					;
				else {
					
					
					createNotification(
							appUser,
							AppUser.find.byId(Long
									.parseLong(session("AppUserId"))),
							"Task: '"
									+ task.getTitle()
									+ "' is Marked by "
									+ AppUser.find
											.byId(Long
													.parseLong(session("AppUserId")))
											.getAppUserFullName()
									+ " in TaskList: '"
									+ task.getTaskList().getTaskListName()
									+ "'", "/showNotificationTask/"
									+ task.getTaskList().getId());
				}

			}
		} else {
			task.setTaskMark(false);
			for (AppUser appUser : task.getTaskList().getAppUsers()) {
				if (appUser.equals(AppUser.find.byId(Long
						.parseLong(session("AppUserId")))))
					;
				else {
					createNotification(
							appUser,
							AppUser.find.byId(Long
									.parseLong(session("AppUserId"))),
							"Task: '"
									+ task.getTitle()
									+ "' is Unmarked by "
									+ AppUser.find
											.byId(Long
													.parseLong(session("AppUserId")))
											.getAppUserFullName()
									+ " in TaskList: '"
									+ task.getTaskList().getTaskListName()
									+ "'", "/showNotificationTask/"
									+ task.getTaskList().getId());
					
				}

			}
		}
		task.update();
		TaskList taskList = task.getTaskList();
		List<Task> tasks = Ebean.createQuery(Task.class).where()
				.eq("taskList", taskList).orderBy("creationDate").findList();
		Collections.reverse(tasks);
		if (session("fetchStarTask").equals("true")) {
			//return redirect(routes.TaskController.taskRender1());
                      AppUser appUser = AppUser.find.byId(Long
				.parseLong(session("AppUserId")));
		List<Task> listOfTask = getTasks();
		return ok(views.html.task.allTasks.render(listOfTask,
				appUser.getTaskList()));
		} else if (!session("taskName").equals("empty")) {
			//Logger.info("search task");
			//return redirect(routes.TaskController.taskRenderMarkUnMark());
                        AppUser appUser = AppUser.find.byId(Long
				.parseLong(session("AppUserId")));
		List<TaskList> taskLists = appUser.getTaskList();
		List<Task> listOfTask = new ArrayList<>();
		for (TaskList taskList1 : taskLists) {
			List<Task> tasks1 = Ebean
					.createQuery(Task.class)
					.where(Expr.and(Expr.eq("taskList", taskList1),
							Expr.like("title", "%" + session("taskName") + "%")))
					.findList();
			listOfTask.addAll(tasks1);
		}

		return ok(views.html.task.allTasks.render(listOfTask,
				appUser.getTaskList()));
                       

		} else if (session("allTask").equals("true")) {
			//return redirect(routes.TaskController.fetchAllTask());
                      AppUser appUser = AppUser.find.byId(Long
				.parseLong(session("AppUserId")));
		List<TaskList> taskList1 = appUser.getTaskList();
		List<Task> allTask = new ArrayList<>();
		for (TaskList taskListObj : taskList1) {
			allTask.addAll(Task.find
					.where()
					.and(Expr.eq("taskList", taskListObj),
							Expr.eq("status",
									TaskStatus.find.where()
											.eq("status", "open").findUnique()))
					.findList());
		}
		return ok(views.html.task.allTasks.render(allTask, taskList1));
		} else if (session("assignMe").equals("true")) {
			//return redirect(routes.TaskController.fetchAssignToMeTask());
			//return ok(views.html.task.allTasks.render(tasks, null));
                    AppUser appUser = AppUser.find.byId(Long
				.parseLong(session("AppUserId")));
		List<TaskList> taskList2 = appUser.getTaskList();
		List<Task> allTask = new ArrayList<>();
		for (TaskList taskListObj : taskList2) {
			allTask.addAll(Task.find
					.where()
					.and(Expr.eq("taskList", taskListObj),
							Expr.eq("assignTo", appUser)).findList());
		}
		return ok(views.html.task.allTasks.render(allTask, taskList2));
		}

		else {
			//return redirect(routes.TaskController.showNotificationTask(taskList
				//	.getId()));
			 return ok(views.html.task.allTasks.render(tasks, null));
		}

	}

	public static List<Task> searchTasks() {
		AppUser appUser = AppUser.find.byId(Long
				.parseLong(session("AppUserId")));
		List<TaskList> taskLists = appUser.getTaskList();
		List<Task> listOfTask = new ArrayList<>();
		for (TaskList taskList : taskLists) {
			List<Task> tasks = Ebean
					.createQuery(Task.class)
					.where(Expr.and(Expr.eq("taskList", taskList),
							Expr.like("title", "%" + session("taskName") + "%")))
					.findList();
			listOfTask.addAll(tasks);
		}
		return listOfTask;
	}

	public Result searchTask() {
		session("fetchStarTask", "false");
		session("allTask", "false");
		session("assignMe", "false");
		Task task = Form.form(Task.class).bindFromRequest().get();

		session("taskName", task.getTitle());

		AppUser appUser = AppUser.find.byId(Long
				.parseLong(session("AppUserId")));
		List<TaskList> taskLists = appUser.getTaskList();
		List<Task> listOfTask = new ArrayList<>();
		for (TaskList taskList : taskLists) {
			List<Task> tasks = Ebean
					.createQuery(Task.class)
					.where(Expr.and(Expr.eq("taskList", taskList),
							Expr.like("title", "%" + task.getTitle() + "%")))
					.findList();
			listOfTask.addAll(tasks);
			
		}

		return ok(views.html.task.allTasks.render(listOfTask,
				appUser.getTaskList()));
	}

	public static List<Task> getTasks() {
		AppUser appUser = AppUser.find.byId(Long
				.parseLong(session("AppUserId")));
		List<TaskList> taskLists = appUser.getTaskList();
		List<Task> listOfTask = new ArrayList<>();
		for (TaskList taskList : taskLists) {
			List<Task> tasks = Ebean
					.createQuery(Task.class)
					.where(Expr.and(Expr.eq("taskList", taskList),
							Expr.eq("taskMark", true))).findList();
			listOfTask.addAll(tasks);
		}
		return listOfTask;
	}

	public Result fetchStarTask() {
		session("fetchStarTask", "true");
		session("taskName", "empty");
		session("allTask", "false");
		session("assignMe", "false");
		AppUser appUser = AppUser.find.byId(Long
				.parseLong(session("AppUserId")));
		List<Task> listOfTask = getTasks();
		return ok(views.html.task.allTasks.render(listOfTask,
				appUser.getTaskList()));
	}

	public Result getAllTasks() {
		session("fetchStarTask", "false");
		session("taskName", "empty");
		session("assignMe", "false");
		session("allTask", "true");
		AppUser appUser = AppUser.find.byId(Long
				.parseLong(session("AppUserId")));
		List<TaskList> taskList = appUser.getTaskList();
		List<Task> allTask = new ArrayList<>();
		for (TaskList taskListObj : taskList) {
			allTask.addAll(Task.find
					.where()
					.and(Expr.eq("taskList", taskListObj),
							Expr.eq("status",
									TaskStatus.find.where()
											.eq("status", "open").findUnique()))
					.findList());
		}
		return ok(views.html.task.allTasks.render(allTask, taskList));
	}

	public Result assignMe() {
		session("fetchStarTask", "false");
		session("taskName", "empty");
		session("allTask", "false");
		session("assignMe", "true");
		AppUser appUser = AppUser.find.byId(Long
				.parseLong(session("AppUserId")));
		List<TaskList> taskList = appUser.getTaskList();
		List<Task> allTask = new ArrayList<>();
		for (TaskList taskListObj : taskList) {
			allTask.addAll(Task.find
					.where()
					.and(Expr.eq("taskList", taskListObj),
							Expr.eq("assignTo", appUser)).findList());
		}
		return ok(views.html.task.allTasks.render(allTask, taskList));
	}

	public Result taskInbox() {
		AppUser appUser = AppUser.find.byId(Long
				.parseLong(session("AppUserId")));
		TaskList taskList = TaskList.find.where()
				.eq("taskListName", appUser.getId() + "inbox").findUnique();
		if (taskList.getTasks() != null) {
			Collections.reverse(taskList.getTasks());
		}
		return ok(views.html.task.allTasks.render(taskList.getTasks(), null));
	}

	public Result createTaskList() {

		List<Task> allTask = Task.find.all();
		return ok(views.html.task.allTasks.render(allTask, null));
	}

	public Result taskTracker() {
		List<TaskStatus> allTaskstatus = TaskStatus.find.all();
		return ok(views.html.task.configuretask.render(allTaskstatus));
	}

	public Result getAddTask() {
		List<AppUser> appUsers = AppUser.find.where()
				.eq("status", UserProjectStatus.Active).findList();
		List<Projects> projects = Projects.find.where()
				.eq("status", UserProjectStatus.Active).findList();
		List<ChatGroup> chatGroups = ChatGroup.find.where()
				.eq("isDisabled", false).findList();
		return ok(views.html.task.addTask
				.render(appUsers, projects, chatGroups));
	}

	public Result myTasks() {
		AppUser appUser = AppUser.find.byId(Long
				.parseLong(session("AppUserId")));
		List<Task> taskList = appUser.getTasks();
		return ok(views.html.task.allTasks.render(taskList, null));
	}

	public Result taskConfigure(Long id) {
		Task task = Task.find.byId(id);
		for (TaskComment com : task.getComments()) {
			// System.out.println("<<<<<<<<<<<<<<<<<>>>>>>"+com.getComment());
		}
		List<TaskStatus> taskStatusList = TaskStatus.find.all();
		List<AppUser> appUsers = AppUser.find.where()
				.eq("status", UserProjectStatus.Active).findList();
		return ok(views.html.task.taskConfigure.render(task, appUsers,
				taskStatusList));

	}

	public Result storetaskComment() {
		AddTaskBean addTaskBean = form.bindFromRequest().get();
		TaskComment comment = form1.bindFromRequest().get();
		Task task = Task.find.byId(addTaskBean.getTaskId());
		List<AppUser> appUsers = new ArrayList<>();
		List<AppUser> allUsers = new ArrayList<>();
		if (addTaskBean.getAssignToId() != null) {
			task.setAssignTo(AppUser.find.byId(addTaskBean.getAssignToId()));
			allUsers.add(AppUser.find.byId(addTaskBean.getAssignToId()));
		}

		if (addTaskBean.getAssignFromIds() != null
				&& !addTaskBean.getAssignFromIds().isEmpty()) {
			for (Long id : addTaskBean.getAssignFromIds()) {
				if (id == addTaskBean.getAssignToId())
					;
				else {
					appUsers.add(AppUser.find.byId(id));
				}
			}
			task.setAssignFrom(appUsers);
		}
		if (addTaskBean.getStatusId() != null) {
			task.setStatus(TaskStatus.find.byId(addTaskBean.getStatusId()));
			comment.taskStatus = TaskStatus.find
					.byId(addTaskBean.getStatusId());
		}
		comment.setAppUser(AppUser.find.byId(Long
				.parseLong(session("AppUserId"))));
		comment.setCommentDate(new Date());
		comment.task = task;
		comment.save();
		List<TaskComment> taskCommentList = task.getComments();
		taskCommentList.add(comment);
		task.setComments(taskCommentList);
		;
		allUsers.addAll(appUsers);
		task.setAppUsers(allUsers);
		task.update();
		return redirect(routes.TaskController.taskConfigure(task.id));
	
	}

	public Result addTaskStatus() {
		TaskStatus addTaskStatus = taskStatusForm.bindFromRequest().get();
		addTaskStatus.save();
		flash().put(
				"alert",
				new Alert("alert-success", "Task status save successfully!")
						.toString());

		return redirect(routes.TaskController.taskTracker());
	}

	public Result editConfigureTaskStaus(Long id) {
		TaskStatus taskStatus = TaskStatus.find.byId(id);
		return ok(views.html.task.editConfigureTask.render(taskStatus));

	}

	public Result updateTaskStatus() {
		TaskStatus updateTaskStatus = taskStatusForm.bindFromRequest().get();
		updateTaskStatus.update();
		flash().put(
				"alert",
				new Alert("alert-success", "Task status update successfully!")
						.toString());

		return redirect(routes.TaskController.taskTracker());
	}

	public Result isExistTaskStatus() {
		DynamicForm requestForm = Form.form().bindFromRequest();
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		String id = requestForm.get("id");
		try {
			if (id != null && !id.isEmpty()) {
				map.put("valid",
						!(TaskStatus.find
								.where()
								.ieq("status", requestForm.get("status").trim())
								.ne("id", Long.parseLong(id)).findRowCount() > 0));
			} else {
				map.put("valid",
						!((TaskStatus.find
								.where()
								.ieq("status", requestForm.get("status").trim())
								.findList().size()) > 0));
			}
		} catch (Exception e) {
			map.put("valid", false);
		}
		return ok(Json.toJson(map));
	}

	public Result isExistTaskTitle() {
		DynamicForm requestForm = Form.form().bindFromRequest();
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		String id = requestForm.get("id");
		try {
			if (id != null && !id.isEmpty()) {
				map.put("valid",
						!(TaskList.find.where()
								.ieq("taskListName", requestForm.get("title").trim())
								.ne("id", Long.parseLong(id)).findRowCount() > 0));
			} else {
				map.put("valid",
						!((TaskList.find.where()
								.ieq("taskListName", requestForm.get("title").trim())
								.findList().size()) > 0));
			}
		} catch (Exception e) {
			map.put("valid", false);
		}
		return ok(Json.toJson(map));
	}

}
