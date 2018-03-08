package actor;

import java.util.Calendar;

import play.Logger;
import akka.actor.UntypedActor;
import controllers.EngineerController;

public class DailyStatusEmailNotification extends UntypedActor{

	@Override
    public void onReceive(final Object message) throws Exception {
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_WEEK);
        
         if(day == 7 || day == 1){
             this.unhandled(message);
         }else{
	        if (message.equals("tick")) {
	            new EngineerController().missingDailyStatusEmail();
	        }else{
	            this.unhandled(message);
	        }
         }
    }

}
