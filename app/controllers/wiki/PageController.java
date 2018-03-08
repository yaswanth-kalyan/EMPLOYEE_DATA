package controllers.wiki;

import java.util.List;
import java.util.stream.Collectors;

import controllers.Application;
import models.wiki.Page;
import models.wiki.PageHistory;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

public class PageController extends Controller {

	final Form<Page> pageForm = Form.form(Page.class);

	public Result list() {

		List<Page> pages = Page.find.all().stream().filter(t-> t.isActive).collect(Collectors.toList());

		return ok(views.html.wiki.list.render(pages));
	}

	public Result view(Long id) {

		Page page = Page.find.byId(id);

		if (page != null) {
			return ok(views.html.wiki.page.render(page));
		} else {

			return redirect(controllers.wiki.routes.PageController.list());
		}

	}

	public Result save() {

		DynamicForm requestData = Form.form().bindFromRequest();
		String title = requestData.get("title");
		String content = requestData.get("content");
		String idStr = requestData.get("id");
		Page page = new Page();

		if (idStr != null && !idStr.isEmpty()) {
			Long id = Long.parseLong(idStr);
			page = Page.find.byId(id);
			page.title = title;
			page.update();
			
			PageHistory history = new PageHistory();
			history.content = content;
			history.page = page;
			history.version = page.getLastHistory().version+1;
			history.appUser = Application.getLoggedInUser();
			history.save();

		} else {
			page.title = title;
			page.save();
			
			//first time create the page history
			
			PageHistory history = new PageHistory();
			history.content = content;
			history.page = page;
			history.version = history.version + 1;
			history.appUser = Application.getLoggedInUser();
			history.save();
		}

		

		return redirect(controllers.wiki.routes.PageController.list());
	}

	public Result renderForm(Long id) {

		Page page = new Page();
		if (id != null) {
			page = Page.find.byId(id);
		}

		if (page == null) {
			page = new Page();
		}

		return ok(views.html.wiki.form.render(page));

	}

	public Result delete(Long id) {
		if (id != null) {
			Page page = Page.find.byId(id);
			if (page != null) {
				page.isActive = Boolean.FALSE;
				page.update();
			}
		}

		return redirect(controllers.wiki.routes.PageController.list());
	}

}
