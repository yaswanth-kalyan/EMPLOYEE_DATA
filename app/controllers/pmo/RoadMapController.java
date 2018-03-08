package controllers.pmo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Projects;
import models.pmo.RoadMap;

import org.json.simple.JSONObject;

import bean.pmo.RoadMapBean;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class RoadMapController extends Controller {

	public static final Form<RoadMapBean> roadMapForm = Form.form(RoadMapBean.class);

	public Result getRoadMapPage(Long projectId) {
		return ok(views.html.pmo.roadmap.roadMapPage.render(null,projectId));
	}

	public Result saveRoadMap() {
		final Form<RoadMapBean> filledForm = roadMapForm.bindFromRequest();
		if (filledForm.hasErrors()) {
			return ok(views.html.pmo.roadmap.roadMapPage.render(null,null));
		} else {
			RoadMapBean roadMapBean = filledForm.get();
			RoadMap roadMap = new RoadMap();
			roadMap.setTitle(roadMapBean.title);
			roadMap.setProject(Projects.find.byId(roadMapBean.projectId));
			roadMap.save();
			Logger.info("roadMapBean >> " + roadMapBean);
			return redirect(routes.RoadMapController.getRoadMapPage(roadMapBean.projectId));
		}
	}

	public Result updateRoadMap() {
		final Form<RoadMapBean> filledForm = roadMapForm.bindFromRequest();
		RoadMapBean roadMapBean = filledForm.get();
		RoadMap roadMap = RoadMap.find.byId(roadMapBean.roadmapId);
		roadMap.setTitle(roadMapBean.title);
		roadMap.setDescription(roadMapBean.description);
		roadMap.update();
		//Logger.info("roadMapBean.title" + roadMapBean.title + "id=====> "+ roadMap.title);
		return redirect(routes.RoadMapController.getRoadMapPage(roadMapBean.projectId));
	}

	public Result daleteRoadMap(Long roadmapId) {
		JSONObject jsonObject = new JSONObject();
		try {
			RoadMap roadMap = RoadMap.find.where().eq("id", roadmapId).findUnique();
			Logger.debug("roadMap====" + roadMap);
			if (roadMap != null) {
				roadMap.delete();
				jsonObject.put("status", true);
				jsonObject.put("message", roadMap.getTitle()
						+ " deleted successfully");
				return ok(Json.parse(jsonObject.toString()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put("status", false);
			jsonObject.put("message", " Oops something went wrong");
		}
		return ok(Json.parse(jsonObject.toString()));
	}

	public Result roadMapList() {
		Map<String, String> resultMap = new HashMap<String, String>();
		List<RoadMap> roadMapList = RoadMap.find.order("id").findList();
		String page = views.html.pmo.roadmap.allRoadMap.render(roadMapList).toString();
		resultMap.put("page", page);
		resultMap.put("status", "true");
		return ok(Json.toJson(resultMap));
	}

	public Result showEditRoadMapPage(Long roadMapId) {
		Map<String, String> resultMap = new HashMap<String, String>();
		RoadMap roadMap = RoadMap.find.byId(roadMapId);
		System.out.println(roadMap.toString());
		String page = views.html.pmo.roadmap.editRoadmap.render(roadMap,roadMap.getProject().id).toString();
		resultMap.put("page", page);
		resultMap.put("status", "true");
		return ok(Json.toJson(resultMap));
	}
	
	public Result roadmapListByProject(Long projectId){
		Map<String, String> resultMap = new HashMap<String, String>();
		List<RoadMap> roadMapList = RoadMap.find.where().eq("project_id", projectId).order("id").findList();
		String page = views.html.pmo.roadmap.allRoadMap.render(roadMapList).toString();
		resultMap.put("page", page);
		resultMap.put("status", "true");
		return ok(Json.toJson(resultMap));
	}

}
