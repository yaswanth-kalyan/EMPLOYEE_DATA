import static play.mvc.Results.internalServerError;

import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Minutes;
import org.joda.time.Seconds;

import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.Play;
import play.libs.Akka;
import play.libs.F.Function0;
import play.libs.F.Promise;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import scala.concurrent.duration.Duration;
import utils.Constants;
import utils.EmailService;
import actor.ChatRoom;
import actor.DailyStatusEmailNotification;
import actor.SchedulerClass;
import akka.actor.ActorRef;
import akka.actor.Props;

public class Global  extends GlobalSettings {

	@Override
	public void onStart(final Application app) {
		//Logger.info("schedular started");
		
		final ActorRef myActor = Akka.system().actorOf(Props.create(SchedulerClass.class));
		TimeZone.setDefault(TimeZone.getTimeZone("GMT+5:30"));
	
		//Reports Schedulars....
		Akka.system().scheduler().schedule(Duration.create(nextExecutionInSeconds(2,0),TimeUnit.SECONDS),Duration.create(24, TimeUnit.HOURS), myActor, "leavesCarryForward", Akka.system().dispatcher(), null);
		Akka.system().scheduler().schedule(Duration.create(nextExecutionInSeconds(3,0),TimeUnit.SECONDS),Duration.create(24, TimeUnit.HOURS), myActor, "CalculateWAR", Akka.system().dispatcher(), null);
		Akka.system().scheduler().schedule(Duration.create(nextExecutionInSeconds(4,0),TimeUnit.SECONDS),Duration.create(24, TimeUnit.HOURS), myActor, "sendRedFlagNotification", Akka.system().dispatcher(), null);
		Akka.system().scheduler().schedule(Duration.create(nextExecutionInSeconds(5,0),TimeUnit.SECONDS),Duration.create(24, TimeUnit.HOURS), myActor, "addLeavesMonth", Akka.system().dispatcher(), null);
		Akka.system().scheduler().schedule(Duration.create(nextExecutionInSeconds(8,0),TimeUnit.SECONDS),Duration.create(24, TimeUnit.HOURS), myActor, "companyweeklyReport", Akka.system().dispatcher(), null);
		Akka.system().scheduler().schedule(Duration.create(nextExecutionInSeconds(8,10),TimeUnit.SECONDS),Duration.create(24, TimeUnit.HOURS), myActor, "employeeWeeklySummary", Akka.system().dispatcher(), null);
		Akka.system().scheduler().schedule(Duration.create(nextExecutionInSeconds(8,30),TimeUnit.SECONDS),Duration.create(24, TimeUnit.HOURS), ChatRoom.defaultRoom, "generalMessages", Akka.system().dispatcher(), null);
		Akka.system().scheduler().schedule(Duration.create(nextExecutionInSeconds(9,0),TimeUnit.SECONDS),Duration.create(24, TimeUnit.HOURS), myActor, "employeeMonthlySummary", Akka.system().dispatcher(), null);
		Akka.system().scheduler().schedule(Duration.create(nextExecutionInSeconds(10,30),TimeUnit.SECONDS),Duration.create(24, TimeUnit.HOURS), myActor, "sendMailRemindersPE", Akka.system().dispatcher(), null);
		Akka.system().scheduler().schedule(Duration.create(nextExecutionInSeconds(11,15),TimeUnit.SECONDS),Duration.create(24, TimeUnit.HOURS), myActor, "tick", Akka.system().dispatcher(), null);
		Akka.system().scheduler().schedule(Duration.create(nextExecutionInSeconds(23,30),TimeUnit.SECONDS),Duration.create(24, TimeUnit.HOURS), myActor, "employeedailystatus", Akka.system().dispatcher(), null);
		Akka.system().scheduler().schedule(Duration.create(nextExecutionInSeconds(23,45),TimeUnit.SECONDS),Duration.create(24, TimeUnit.HOURS), myActor, "companyDailyStatus", Akka.system().dispatcher(), null);
		Akka.system().scheduler().schedule(Duration.create(nextExecutionInSeconds(20,00),TimeUnit.SECONDS),Duration.create(24, TimeUnit.HOURS), myActor, "deductLeaves", Akka.system().dispatcher(), null);
		Akka.system().scheduler().schedule(Duration.create(nextExecutionInSeconds(9,30),TimeUnit.SECONDS),Duration.create(24, TimeUnit.HOURS), myActor, "ProbationPeriod6Month", Akka.system().dispatcher(), null);

		//Akka.system().scheduler().schedule(Duration.create(0,TimeUnit.SECONDS),Duration.create(24, TimeUnit.HOURS), myActor, "ProbationPeriod6Month", Akka.system().dispatcher(), null);
		//Reports Schedulars End
		//Akka.system().scheduler().schedule(Duration.create(0,TimeUnit.SECONDS),Duration.create(24, TimeUnit.HOURS), ChatRoom.defaultRoom, "generalMessages", Akka.system().dispatcher(), null);
		//Akka.system().scheduler().schedule(Duration.create(nextExecutionInSeconds(23,55),TimeUnit.SECONDS),Duration.create(24, TimeUnit.HOURS), myActor, "notfilled", Akka.system().dispatcher(), null);
		//Akka.system().scheduler().schedule(Duration.create(0,TimeUnit.MILLISECONDS),Duration.create(24, TimeUnit.HOURS), myActor, "CalculateWAR", Akka.system().dispatcher(), null);
	        /* Akka.system().scheduler().schedule( Duration.create(0,TimeUnit.MILLISECONDS), Duration.create(24, TimeUnit.HOURS), myActor,"companyDailyStatus", Akka.system().dispatcher(), null );
			 Akka.system().scheduler().schedule( Duration.create(0,TimeUnit.MILLISECONDS), Duration.create(24, TimeUnit.HOURS), myActor,"companyweeklyReport", Akka.system().dispatcher(), null );
			 Akka.system().scheduler().schedule( Duration.create(0,TimeUnit.MILLISECONDS), Duration.create(24, TimeUnit.HOURS), myActor,"employeedailystatus", Akka.system().dispatcher(), null );
			 Akka.system().scheduler().schedule( Duration.create(0,TimeUnit.MILLISECONDS), Duration.create(24, TimeUnit.HOURS), myActor,"employeeWeeklySummary", Akka.system().dispatcher(), null );
			 Akka.system().scheduler().schedule( Duration.create(0,TimeUnit.MILLISECONDS), Duration.create(24, TimeUnit.HOURS), myActor,"employeeMonthlySummary", Akka.system().dispatcher(), null );
			 Akka.system().scheduler().schedule( Duration.create(0,TimeUnit.MILLISECONDS), Duration.create(24, TimeUnit.HOURS), myActor,"notfilled", Akka.system().dispatcher(), null );*/
		
	/*	Akka.system().scheduler().schedule(
				//Duration.create(nextExecutionInSeconds(8,0),TimeUnit.SECONDS),
			//Duration.create(24, TimeUnit.HOURS),
				Duration.create(0, TimeUnit.MILLISECONDS), //Initial delay 0 milliseconds
			Duration.create(2,TimeUnit.SECONDS),     //Frequency seconds
				ChatRoom.defaultRoom, 
				"generalMessages",
				Akka.system().dispatcher(), 
				null);*/
	 
			/* final ActorRef actor1 = Akka.system().actorOf(Props.create(DailyStatusEmailNotification.class));
			 	Akka.system().scheduler().schedule(
				Duration.create(nextExecutionInSeconds(11,13),TimeUnit.SECONDS),
				Duration.create(24, TimeUnit.HOURS),
				actor1,
				"tick",
				Akka.system().dispatcher(),
				null
				);*/
	 }
	
	
 

	


	public static int nextExecutionInSeconds(final int hour, final int minute) {
		// Logger.info(DateTime.now()+""); 
//		Logger.info(Minutes.minutesBetween(new DateTime(),
//				nextExecution(hour, minute)).getMinutes()
//				+ "");
	
		return Seconds.secondsBetween(
				new DateTime(DateTimeZone.forID("+05:30")),
				nextExecution(hour, minute)).getSeconds();
	}
	
	public static DateTime nextExecution(final int hour, final int minute) {
		final DateTime next = new DateTime(DateTimeZone.forID("+05:30"))
		.withHourOfDay(hour).withMinuteOfHour(minute)
		.withSecondOfMinute(0).withMillisOfSecond(0);
		
		
		
			return (next.isBeforeNow()) ? next.plusHours(24) : next;
		
		
		
	}

	/**
	 *	Rendering 500 error page on error
	 */
	@Override
	public Promise<Result> onError(final RequestHeader request, final Throwable t) {

		//Logger.debug("step1 email erropr");;
		final StringBuilder sb = new StringBuilder("");
		sb.append("<html>");
		sb.append("<body>");
		sb.append("<p>");
		sb.append("URL: ");
		sb.append(request.uri());
		sb.append("</p>");
		sb.append("<p>");
		sb.append("LoggedInAppUserId: ");
		if(controllers.Application.isLoggedIn()){
			sb.append(controllers.Application.getLoggedInUser().id+" : Name:"+controllers.Application.getLoggedInUser().getAppUserFullName());
		}
		else{
			sb.append("Public User");
		}
		sb.append("</p>");
		sb.append("<p>");
		sb.append("IP: ");sb.append(request.remoteAddress());
		sb.append("</p>");
		sb.append("<p>");sb.append(t.getMessage());sb.append("</p>");
		sb.append("<p>");
		for (final StackTraceElement e : t.getStackTrace()) {
			sb.append(e.toString());sb.append("<br>");
		}
		sb.append("</p>");
		sb.append("</body>");
		sb.append("</html>");

		if(Play.isProd()){
			// Async Execution
			Promise.promise(new Function0<Integer>() {
				@Override
				public Integer apply() {
					if(!EmailService.sendIndividualMail(Constants.EMAIL_USERNAME, "BB8 : developer.thrymr.net :  Error: "+t.getMessage(), sb.toString())){
						return 1;
					}
					return 0;
				}
			});
			// End of async
		}else{
			t.printStackTrace();
		}

		return Promise.<Result>pure(internalServerError("Error"));
		//return Promise.<Result>pure(internalServerError(views.html.error.render("Exception", 404)));
	}
	/**
	 *	Rendering 404 page on not found
	 /*

	@Override

	public Promise<Result> onHandlerNotFound(final RequestHeader request) {

		return Promise.<Result>pure(notFound("fddfdf"));
	}
	/**
	 *	Rendering 500 on bad request too
	 */

	@Override
	public Promise<Result> onBadRequest(final RequestHeader request, final String error) {
		
		return Promise.<Result>pure(internalServerError("Error"));

	}
	
}
