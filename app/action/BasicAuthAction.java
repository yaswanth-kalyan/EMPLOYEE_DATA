package action;

import models.Alert;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import controllers.Application;

public class BasicAuthAction extends BaseAction {

	@Override
	public Promise<Result> call(Http.Context ctx) throws java.lang.Throwable {
	//	super.logRequest(ctx);
		if (!Application.isLoggedIn()) {
			Controller.flash().put("alert",	new Alert("alert-info",	"You have been logged out. Please login to continue.").toString());
			return Promise.pure((Result) new Application().loginError());
		}
		return this.delegate.call(ctx);
	}
}
