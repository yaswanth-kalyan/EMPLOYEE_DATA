package actor;

import java.util.Date;

import models.leave.AppliedLeaves;
import akka.actor.UntypedActor;

import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;

public class LeaveStatusTaken extends UntypedActor{
	@Override
    public void onReceive(final Object message) throws Exception {
		if (message.equals("taken")) {
			ExpressionList<AppliedLeaves> expeExpressionList= AppliedLeaves.find.where().lt("endDate", new Date()).or(Expr.eq("leaveStatus", models.leave.LeaveStatus.PENDING_APPROVAL ),Expr.eq("leaveStatus", models.leave.LeaveStatus.APPROVED ));
			for(AppliedLeaves appliedLeaves : expeExpressionList.findList()){
				appliedLeaves.leaveStatus = models.leave.LeaveStatus.TAKEN;
				appliedLeaves.update();
			}
		}else{
			this.unhandled(message);
		}
	}

}
