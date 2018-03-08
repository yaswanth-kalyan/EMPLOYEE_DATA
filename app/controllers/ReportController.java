package controllers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;

import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import utils.ConstructReportUrl;

public class ReportController extends Controller {
	private String reportURL = null;
	private String reportName = null;
	String sDate =null;
	String eDate =null;
	
/**
 * 	Employee Daily Status (All Date wise&id input Reports)..EMP:32
 */
public Result getEmpDailyStatusByDate(String startDate,Long id) {
	if(id==0){
		reportName = "bb8_adminreport_bydate";
		if(startDate!=null){
			reportURL = ConstructReportUrl.constructFilterURL(reportName,startDate.replace("-","/"));
		}
	}else{
		reportName = "Employee_Daily_Status_Report";
		if(startDate!=null){
			String date = startDate.split("-")[2]+"/"+startDate.split("-")[1]+"/"+startDate.split("-")[0];
			reportURL = ConstructReportUrl.constructFilterURL(reportName,date,id);
			
		}
	}
	reportURL = reportURL.replaceAll("/", "%2f");
	reportURL = reportURL.replace("?","%3f");
	return ok(reportURL);
}
/**
 * 	All  Weekly Reports ....EMP:32
 */
public Result getEmpWeeklyAttendance(String startDate,String endDate,Long id,String reportType){
	if(startDate!=null&&endDate!=null){
		 sDate = startDate.split("-")[2]+"-"+startDate.split("-")[1]+"-"+startDate.split("-")[0];
		 eDate =endDate.split("-")[2]+"-"+endDate.split("-")[1]+"-"+endDate.split("-")[0];
				if(reportType .equalsIgnoreCase("attendance") || reportType .equalsIgnoreCase("empweek-Wise-Report")){
					reportName = "Average_Main_Report_Datewise";
				}
				else{
					 //Logger.info(reportType);
					 sDate = startDate.split("-")[2]+"/"+startDate.split("-")[1]+"/"+startDate.split("-")[0];
					 eDate =endDate.split("-")[2]+"/"+endDate.split("-")[1]+"/"+endDate.split("-")[0];
					if(reportType.equalsIgnoreCase("attendanceComapany")){
					reportName = "CompanyWeeklyAttendanceReport";
				   }
				   else if(reportType.equalsIgnoreCase("sumarry-Week-Wise-Report")||reportType.equalsIgnoreCase("sumarry-Week-Wiseemp-Report") ){
						reportName = "Employee_Weekly_Status_Report";
					 }
				   else if(reportType.equalsIgnoreCase("Employee_Monthly_Status_Report") || reportType.equalsIgnoreCase("anytime_report")){
						reportName = "Employee_Monthly_Status_Report";
					 }
				}
	        reportURL = ConstructReportUrl.constructFilterURL(reportName,sDate,eDate,id);
	        reportURL = reportURL.replaceAll("/", "%2f");
	    	reportURL = reportURL.replace("?","%3f");
	    	//Logger.info("ghxg"+reportURL);
	}else{
		//Logger.info("Dates are wrong");
	}
	return ok(reportURL);
}
/**
 * Startdate ,end date seperate Method ....EMP:32
 */
public Result getprojectDetails(String startDate,String endDate,String reportType){
	
	if(startDate != null && endDate != null){
		
		if (reportType.equalsIgnoreCase("Employee_Project_Details")) {
			 sDate = startDate.split("-")[2]+"-"+startDate.split("-")[1]+"-"+startDate.split("-")[0]+"%2000:00:00";
			 eDate =endDate.split("-")[2]+"-"+endDate.split("-")[1]+"-"+endDate.split("-")[0]+"%2023:59:59";
		reportName = "Employee_Project_Details";
		} else if (reportType.equalsIgnoreCase("All_Employee_Status")) {
			 sDate = startDate.split("-")[2]+"/"+startDate.split("-")[1]+"/"+startDate.split("-")[0];
					 eDate =endDate.split("-")[2]+"/"+endDate.split("-")[1]+"/"+endDate.split("-")[0];
			reportName = "All_Employee_Status";
		} else if (reportType.equalsIgnoreCase("all-employee-biometric-report.xls")) {
			sDate = startDate.split("-")[2]+"-"+startDate.split("-")[1]+"-"+startDate.split("-")[0];
			 eDate =endDate.split("-")[2]+"-"+endDate.split("-")[1]+"-"+endDate.split("-")[0];
			 reportName = "AllEmployeeBiometricAttendanceReport";
		}
		reportURL = ConstructReportUrl.constructFilterURL(reportName,sDate,eDate);
		reportURL = reportURL.replaceAll("/", "%2f");
    	reportURL = reportURL.replace("?","%3f");
	}else{
		//Logger.info("Dates are wrong");
	}
    return ok(reportURL);
}
public Result getYearlyReport(String year,Long id,String reportType){
	if(reportType.equalsIgnoreCase("yearly_report")){
		reportName = "Daily_Status_History";
		reportURL = ConstructReportUrl.constructFilterURL(reportName,year,id);
	}else if(reportType.equalsIgnoreCase("biometric-attendance-yearly")){
		reportName = "BiometricAttendanceHistoryReport";
		reportURL = ConstructReportUrl.constructFilterURL(reportName,year,id);
	}else if(reportType.equalsIgnoreCase("leave_report")){
		reportName = "Leave_History_Report";
		reportURL = ConstructReportUrl.constructFilterURL(reportName,year,id);
	}else{
		reportName = "Leave_Balance_Report";
		reportURL = ConstructReportUrl.constructFilterURL(reportName,null);
	}
		reportURL = reportURL.replaceAll("/", "%2f");
    	reportURL = reportURL.replace("?","%3f");
    	
    	Logger.debug("report url -------------- > " + reportURL);

    return ok(reportURL);
}


}
