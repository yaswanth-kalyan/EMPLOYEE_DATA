package controllers.pmo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Projects;
import models.pmo.Epic;
import models.pmo.RoadMap;
import bean.pmo.EpicBean;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class EpicController extends Controller {

	public static final Form<EpicBean> epicForm = Form.form(EpicBean.class);
	// epic
	public Result getEpicPage(Long roadmapId) {
		return ok(views.html.pmo.epic.epicPage.render(roadmapId));
	}

	public Result saveEpics() {
		final Form<EpicBean> filledForm = epicForm.bindFromRequest();
		if (filledForm.hasErrors()) {
			return ok(views.html.pmo.epic.epicPage.render(null));
		} else {
			EpicBean epicBean = filledForm.get();
			Epic epic = new Epic();
			epic.setName(epicBean.name);
			epic.setRoadMap(RoadMap.find.byId(epicBean.roadmapId));
			epic.save();										
			Logger.info("roadMapBean >> " + epicBean);
			return redirect(routes.EpicController.getEpicPage(epicBean.roadmapId));
		}
	}

	public Result updateEpic() {
		final Form<EpicBean> filledForm = epicForm.bindFromRequest();
		if (filledForm.hasErrors()) {
			return ok(views.html.pmo.epic.epicPage.render(null));
		} else {
				EpicBean epicBean = filledForm.get();
				Epic epic = Epic.find.byId(epicBean.epicId);
				epic.setName(epicBean.name);
				epic.setDescription(epicBean.description);
				epic.update();
				return redirect(routes.EpicController.getEpicPage(epicBean.roadmapId));
			}
	}
	public Result daleteEpic(Long epicId) {
		Map<String, String> resultMap = new HashMap<String, String>();
		if (epicId != null) {
			try{
				Epic epic = Epic.find.byId(epicId);
				epic.delete();
				resultMap.put("message", epic.name +" deleted successfully !!");
				resultMap.put("status", "true");
			}catch(Exception  e){
				e.printStackTrace();
				resultMap.put("page"," something went wrong !! " );
				resultMap.put("status", "false");
			}
		}
		return ok(Json.toJson(resultMap));
	}

	public Result epicsList() {
		Map<String, String> resultMap = new HashMap<String, String>();
		List<Epic> epicList = Epic.find.orderBy("id").findList();
		String page = views.html.pmo.epic.allEpic.render(epicList).toString();
		resultMap.put("page", page);
		resultMap.put("status", "true");
		return ok(Json.toJson(resultMap));
	}
	
	public Result showEditEpicPage(Long epicId) {
		Map<String, String> resultMap = new HashMap<String, String>();
		Epic epic = Epic.find.byId(epicId);
		System.out.println(epic.toString());
		String page = views.html.pmo.epic.editEpic.render(epic,epic.roadMap.id).toString();
		resultMap.put("page", page);
		resultMap.put("status", "true");
		return ok(Json.toJson(resultMap));
	}
	
	public Result epicListByRoadmap(Long roadmapId){
		Map<String, String> resultMap = new HashMap<String, String>();
		List<Epic> epicList = Epic.find.where().eq("road_map_id",roadmapId).orderBy("id").findList();
		String page = views.html.pmo.epic.allEpic.render(epicList).toString();
		resultMap.put("page", page);
		resultMap.put("status", "true");
		return ok(Json.toJson(resultMap));
	}

}
