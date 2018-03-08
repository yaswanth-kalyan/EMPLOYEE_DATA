package bean;

import java.io.Serializable;

import models.leave.Days;
import models.leave.DaysSection;
import models.leave.WorkingDays;

public class WorkingDaysBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Long id;
	
	public String monday;
	
	public String tuesday;
	
	public String wednesday;
	
	public String thursday;
	
	public String friday;
	
	public String saturday;
	
	public String sunday;
	
	public WorkingDays toDays(){
		WorkingDays workingDays = null;
		play.Logger.debug(monday+" inside today");
		
			if(!monday.isEmpty()){
				 workingDays = WorkingDays.find.where().eq("day", Days.MONDAY).findUnique();
				 if(workingDays != null){
					 workingDays.daysSection = DaysSection.valueOf(monday);
					 workingDays.update();
				 }else{
					 workingDays = new WorkingDays();
					 workingDays.day = Days.MONDAY;
					 workingDays.daysSection = DaysSection.valueOf(monday);
					 workingDays.save();
				 }
			}
			if(!tuesday.isEmpty()){
				 workingDays = WorkingDays.find.where().eq("day", Days.TUESDAY).findUnique();
				 if(workingDays != null){
					 workingDays.daysSection = DaysSection.valueOf(tuesday);
					 workingDays.update();
				 }else{
					 workingDays = new WorkingDays();
					 workingDays.day = Days.TUESDAY;
					 workingDays.daysSection = DaysSection.valueOf(tuesday);
					 workingDays.save();
				 }
			}
			if(!wednesday.isEmpty()){
				 workingDays = WorkingDays.find.where().eq("day", Days.WEDNESDAY).findUnique();
				 if(workingDays != null){
					 workingDays.daysSection = DaysSection.valueOf(monday);
					 workingDays.update();
				 }else{
					 workingDays = new WorkingDays();
					 workingDays.day = Days.WEDNESDAY;
						 workingDays.daysSection = DaysSection.valueOf(wednesday);
						 workingDays.save();
				 }
			}
			if(!thursday.isEmpty()){
				 workingDays = WorkingDays.find.where().eq("day", Days.THURSDAY).findUnique();
				 if(workingDays != null){
					 workingDays.daysSection = DaysSection.valueOf(thursday);
					 workingDays.update();
				 }else{
					 workingDays = new WorkingDays();
					 workingDays.day = Days.THURSDAY;
						 workingDays.daysSection = DaysSection.valueOf(thursday);
						 workingDays.save();
				 }
			}
			if(!friday.isEmpty()){
				 workingDays = WorkingDays.find.where().eq("day", Days.FRIDAY).findUnique();
				 if(workingDays != null){
					 workingDays.daysSection = DaysSection.valueOf(friday);
					 workingDays.update();
				 }else{
					 workingDays = new WorkingDays();
					 workingDays.day = Days.FRIDAY;
						 workingDays.daysSection = DaysSection.valueOf(friday);
						 workingDays.save();
				 }
			}
			if(!saturday.isEmpty()){
				 workingDays = WorkingDays.find.where().eq("day", Days.SATURDAY).findUnique();
				 if(workingDays != null){
					 workingDays.daysSection = DaysSection.valueOf(saturday);
					 workingDays.update();
				 }else{
					 workingDays = new WorkingDays();
					 workingDays.day = Days.SATURDAY;
						 workingDays.daysSection = DaysSection.valueOf(saturday);
						 workingDays.save();
				 }
			}
			if(!sunday.isEmpty()){
				 workingDays = WorkingDays.find.where().eq("day", Days.SUNDAY).findUnique();
				 if(workingDays != null){
					 workingDays.daysSection = DaysSection.valueOf(sunday);
					 workingDays.update();
				 }else{
					 workingDays = new WorkingDays();
					 workingDays.day = Days.SUNDAY;
						 workingDays.daysSection = DaysSection.valueOf(sunday);
						 workingDays.save();
				 }
			}
		
			
		
		return workingDays;
		
	}

}
