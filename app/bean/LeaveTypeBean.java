package bean;

import java.io.Serializable;

import models.leave.LeaveType;

public class LeaveTypeBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Long id;
	
	public String leaveType;
	
	public Boolean carryForward;
	
	public LeaveType toEnLeaveType(){
		
		LeaveType leaveType = null;
		if(id != null && !id.toString().isEmpty()){
			leaveType = LeaveType.find.byId(id);
			if(!this.leaveType.isEmpty()){
				leaveType.leaveType = this.leaveType.trim();
				/*if(carryForward != null){
					leaveType.carryForward = carryForward;
				}*/
				leaveType.update();
			}
		}else{
		if(!this.leaveType.isEmpty()){
			 leaveType = new LeaveType();
			leaveType.leaveType = this.leaveType.trim();
			/*if(carryForward != null){
				leaveType.carryForward = carryForward;
			}*/
			leaveType.save();
		}
		}
		return leaveType;
		
		
	}

}
