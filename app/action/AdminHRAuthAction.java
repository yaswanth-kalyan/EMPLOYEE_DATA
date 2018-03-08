package action;

import models.Alert;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import controllers.Application;

public class AdminHRAuthAction extends BaseAction{

	@Override
	public Promise<Result> call(Http.Context ctx) throws java.lang.Throwable {
	//	super.logRequest(ctx);
		if (Application.isAdminHR().equals(false)) {
			Controller.flash().put("alert",	new Alert("alert-info",	"You don't have permision to access this Page...!").toString());
			return Promise.pure((Result) new Application().error());
		}
		return this.delegate.call(ctx);
	}
}
