package actor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import models.leave.Holidays;
import play.Logger;
import utils.EmailService;
import akka.actor.UntypedActor;
import controllers.AdminLeaveController;
import controllers.EngineerController;
import controllers.PEController;
import controllers.SampleDataController;


public class SchedulerClass extends UntypedActor{
    
    @Override
    public void onReceive(final Object message)  {
         Calendar cal = Calendar.getInstance();
            int day = cal.get(Calendar.DAY_OF_WEEK);
            int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
             Holidays holiday = Holidays.find.where().eq("holidayDate", EngineerController.getTodayDate(new Date())).findUnique();
            Holidays workingDay = Holidays.find.where().eq("correspondingWorkingDay", EngineerController.getTodayDate(new Date())).findUnique();
        
                if (message.equals("companyDailyStatus")) {
                    try {
                    	if((day ==1 || day ==7 || holiday != null) && workingDay == null){
                    	}else{
                    		EmailService.engineerSummaryReportPdf("companyDailyStatus");
                    	}
                    } catch (Exception e) {
                    	e.printStackTrace();
                    }     

                }else if(message.equals("companyweeklyReport")){
                    try {
                            if(day == 2 ){
                            	EmailService.engineerSummaryReportPdf("companyweeklyReport");  // Sending Company Weekly Daily Status treport pdf to Admin
                            }
                            else{
                               Logger.debug("today is not monday");
                            }
                        } catch (Exception e) {
                        e.printStackTrace();
                    }     

                }else if(message.equals("employeedailystatus")){
                    try {
                    	if((day ==1 || day ==7 || holiday != null) && workingDay == null){
                        Logger.info("Today is saturday or sunday");
                    }else{
                    	EmailService.engineerSummaryReportPdf("employeedailystatus");  //Dont Send Engineer Daily Status Report on saturday and sunday.
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(message.equals("employeeWeeklySummary")){
                    try {
                    if(day ==2){
                    	EmailService.engineerSummaryReportPdf("employeeWeeklySummary");  // Only Send Engineer Summary Report on Sunday
                     }else{
                    	Logger.info("Today is not sunday");  
                    		}
                    }catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(message.equals("notfilled")) {
            	try {
                    if((day ==1 || day ==7 || holiday != null) && workingDay == null){
                    	Logger.info("Today Holiday");
                    }else{
                       SampleDataController.sendMailNotFilled();
                    }
                }
            	catch (Exception e) {
                    e.printStackTrace();
                }
            	
           }
            else if(message.equals("employeeMonthlySummary")){
                try {
					String lastDay = new SimpleDateFormat("yyyy/MM/dd").format(EmailService.getFirstDateOfCurrentMonth());
					String currentDay = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
					if(lastDay.equalsIgnoreCase(currentDay)){
						EmailService.engineerSummaryReportPdf("employeeMonthlySummary");  // Only Send Engineer Summary Report on Sunday
					}else{
						Logger.info("Today is not first day  of Month");
					}
                	
                }catch (Exception e) {
                e.printStackTrace();
            }
        }else if (message.equals("CalculateWAR")){
	        	
	        	if( dayOfMonth == 4){
	        		try {
						PEController.CalculateWAR();
					} catch (ParseException e) {
						e.printStackTrace();
					}
	        	}
        } else if (message.equals("sendRedFlagNotification")){
        	if( dayOfMonth == 4){
	        	try{
	    			PEController.sendRedFlagNotificationHRAdmin();
	    		}catch(Exception e){
	    			e.printStackTrace();
	    		}
        	}
        } else if (message.equals("sendMailRemindersPE")){
        	if( dayOfMonth  >= 27 || dayOfMonth  <= 3){
	        	try{
	    			PEController.sendMailRemindersSelf();
	    			PEController.sendMailRemindersEmployee();
	    		}catch(Exception e){
	    			e.printStackTrace();
	    		}
        	}
        } else if (message.equals("tick")){
        	if((day ==1 || day ==7 || holiday != null) && workingDay == null){
                this.unhandled(message);
            }else{
	   	        new EngineerController().missingDailyStatusEmail();
            }
        } else if (message.equals("deductLeaves")){
	   	        new AdminLeaveController();
				try {
					AdminLeaveController.deductLeavesbyDate();
				} catch (ParseException e) {
					e.printStackTrace();
				}
        } else if (message.equals("leavesCarryForward")){
   	        new AdminLeaveController();
			try {
				String date = new SimpleDateFormat("dd-MM").format(new Date());
				String newYearFirst = "01-01";
				if(newYearFirst.equalsIgnoreCase(date)){
					AdminLeaveController.leavesCarryForward();
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
        } else if (message.equals("addLeavesMonth")){
   	        new AdminLeaveController();
			try {
				String date = new SimpleDateFormat("dd").format(new Date());
				String newMonthFirst = "02";
				if(newMonthFirst.equalsIgnoreCase(date)){
					AdminLeaveController.addLeavesMonth();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
        } else if (message.equals("ProbationPeriod6Month")){
			try {
				SampleDataController.sendMailProbationPeriodCompletion6MonthsEmps();
			} catch (Exception e) {
				e.printStackTrace();
			}
        } else {
            this.unhandled(message);
        }
      }
}
