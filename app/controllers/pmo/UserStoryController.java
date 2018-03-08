package controllers.pmo;

import java.util.List;

import models.pmo.UserStory;
import models.pmo.Epic;
import play.mvc.Controller;
import play.mvc.Result;

public class UserStoryController extends Controller {

	public Result list() {

		List<UserStory> userStories = UserStory.find.all();
		return ok(views.html.pmo.userstory.list.render(userStories));
	}

	public Result listByEpic(Long epicId) {

		List<UserStory> userStories = UserStory.find.where()
				.eq("epic.id", epicId).findList();
		return ok(views.html.pmo.userstory.list.render(userStories));
	}

	public Result create() {

		return redirect(routes.UserStoryController.list());
	}

	public Result edit(Long id) {

		return redirect(routes.UserStoryController.list());
	}

	public Result delete(Long id) {

		if (id != null) {
			UserStory userStory = UserStory.find.byId(id);
			if (userStory != null) {
				userStory.delete();
			}
		}

		return redirect(routes.UserStoryController.list());
	}

	public Result details(Long id) {

		return redirect(routes.UserStoryController.list());
	}

	public Result loadPage(Long epicId) {
		Epic epic = Epic.find.byId(epicId);
		UserStory userStory = new UserStory();
		return ok(views.html.pmo.userstory.page.render(epic, userStory));

	}
}
