package action;

import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;

public class BaseAction extends Action.Simple{

	@Override
	public Promise<Result> call(Context ctx) throws Throwable {
		// TODO Auto-generated method stub
		return this.delegate.call(ctx);
	}

	/**
	 * log all incoming request which are secured and keep it in database
	 *
	 * a basic balance between keeping all the requests logged versus only the
	 * authenticated ones ! for enabling/disabling for individual roles, comment
	 * out call for this method in specific subclass
	 *
	 * @param ctx
	 */
	/*public void logRequest(Context ctx) {
		final Request request = ctx.request();
		final UserActivity activity = new UserActivity();
		final AppUser appUser = LoginController.getLoggedInUser();

		if (appUser != null) {
			activity.appUser = LoginController.getLoggedInUser().email;
			activity.user = appUser;
		}

		activity.url = request.path();
		activity.method = request.method();
		activity.ipAddress = request.remoteAddress();
		//TODO : try to save in an async manner, probably either via a message interface or via async jdbc,
		// further calls can proceeed without blocking this
		//activity.user.save();
		activity.save();
		// Logger.debug(" activity" + activity.toString());
	}*/
}
