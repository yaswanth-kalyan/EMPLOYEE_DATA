package utils;

import play.Play;


/**
 * This class is used for opening Reports
 * @author thrmyr
 *
 */

public class ConstructReportUrl {    
    public static final String BACKSLASH = "/";
    public static final String COLON = ":";
    public static final String AMPERSAND = "&";
    public static final String PARENT_FOLDER_KEY = "ParentFolderUri";
    public static final String REPORTS =  Play.application().configuration().getString("reportsUrl");
    public static final String REPORT_NAME_KEY = "reportUnit";
    public static final String EQUALTO = "=";
    public static final String NO_DECORATION = "decorate=no";
    public static final String USERNAME = "j_username=jasperadmin";
    public static final String PASSWORD = "j_password=jasperadmin"; 

    /**
     * Action to constructing URL for particular report with all records with reportname
     */
    
    public static String constructFilterURL(String reportname,String from,String to) {        
        
        // TODO Auto-generated method stub
        final StringBuilder url = new StringBuilder();
      
      //Dynamic part - Parent Folder
        url.append(AMPERSAND);
        url.append(PARENT_FOLDER_KEY);
        url.append(EQUALTO);
        url.append(BACKSLASH);
        url.append(REPORTS);
        url.append(AMPERSAND);
       
        
        //Dynamic part - Report Name
        url.append(REPORT_NAME_KEY);
        url.append(EQUALTO);
        url.append(BACKSLASH);
        url.append(REPORTS);
        url.append(BACKSLASH);
        url.append(reportname);

        //Login credentials
        url.append(AMPERSAND);
        url.append("from");
        url.append(EQUALTO);
        url.append(from);
        url.append(AMPERSAND);
        url.append("to");
        url.append(EQUALTO);
        url.append(to);
        url.append(AMPERSAND);
        url.append(NO_DECORATION);
        return url.toString();
    }
    /**
     * Action to constructing URL for particular report with all records with reportname
     */
    
    public static String constructFilterURL(String reportname,String from,String to,Long id) {        
        
        // TODO Auto-generated method stub
        final StringBuilder url = new StringBuilder();

      //Dynamic part - Parent Folder
        url.append(AMPERSAND);
        url.append(PARENT_FOLDER_KEY);
        url.append(EQUALTO);
        url.append(BACKSLASH);
        url.append(REPORTS);
        url.append(AMPERSAND);
       
        
        //Dynamic part - Report Name
        url.append(REPORT_NAME_KEY);
        url.append(EQUALTO);
        url.append(BACKSLASH);
        url.append(REPORTS);
        url.append(BACKSLASH);
        url.append(reportname);

        //Login credentials
        url.append(AMPERSAND);
        url.append("from");
        url.append(EQUALTO);
        url.append(from);
        url.append(AMPERSAND);
        url.append("to");
        url.append(EQUALTO);
        url.append(to);
        url.append(AMPERSAND);
        url.append("id");
        url.append(EQUALTO);
        url.append(id);
        url.append(AMPERSAND);
        url.append(NO_DECORATION);
        return url.toString();
    }
    /**
     * Action to constructing URL for particular report and filter records using user id
     */
    public static String constructFilterURL(String reportname,String start) {
        // TODO Auto-generated method stub
        final StringBuilder url = new StringBuilder();

      //Dynamic part - Parent Folder
        url.append(AMPERSAND);
        url.append(PARENT_FOLDER_KEY);
        url.append(EQUALTO);
        url.append(BACKSLASH);
        url.append(REPORTS);
        url.append(AMPERSAND);
       
        //Dynamic part - Report Name
        url.append(REPORT_NAME_KEY);
        url.append(EQUALTO);
        url.append(BACKSLASH);
        url.append(REPORTS);
        url.append(BACKSLASH);
        url.append(reportname);
       
        //Login credentials
        
        
        if(start!=null){
        url.append(AMPERSAND);
        url.append("date");
        url.append(EQUALTO);
        url.append(start);
        }
        url.append(AMPERSAND);
        url.append(NO_DECORATION);
        return url.toString();
    }
    /**
     * Action to constructing URL for particular report and filter records using user id
     */
    public static String constructFilterURL(String reportname,String start,Long id) {
        // TODO Auto-generated method stub
        final StringBuilder url = new StringBuilder();

      //Dynamic part - Parent Folder
        url.append(AMPERSAND);
        url.append(PARENT_FOLDER_KEY);
        url.append(EQUALTO);
        url.append(BACKSLASH);
        url.append(REPORTS);
        url.append(AMPERSAND);
       
        //Dynamic part - Report Name
        url.append(REPORT_NAME_KEY);
        url.append(EQUALTO);
        url.append(BACKSLASH);
        url.append(REPORTS);
        url.append(BACKSLASH);
        url.append(reportname);
       
        //Login credentials
        url.append(AMPERSAND);
        if(reportname=="Daily_Status_History" || reportname=="Leave_History_Report" || reportname == "BiometricAttendanceHistoryReport"){
        	url.append("year");
        }else{
        	url.append("Date");
        }
        url.append(EQUALTO);
        url.append(start);
        url.append(AMPERSAND);
        url.append("id");
        url.append(EQUALTO);
        url.append(id);
        url.append(AMPERSAND);
        url.append(NO_DECORATION);
        return url.toString();
    }
    public static String urlCommonPart(){ 
		StringBuilder url = new StringBuilder();
	url.append(Play.application().configuration().getString("jasper.server.protocol"));//http
	url.append(COLON); //http:
	url.append(BACKSLASH);//http:/
	url.append(BACKSLASH);//http://
	url.append(Play.application().configuration().getString("jasper.server.host"));//http://locahost
	url.append(COLON);//http://locahost:
	url.append(Play.application().configuration().getString("jasper.server.port"));//http://locahost:8080
	url.append(BACKSLASH);//http://locahost:8080/
	url.append(Play.application().configuration().getString("jasper.server.context"));//http://locahost:8080/jasperserver/flow.html?_flowId=viewReportFlow&standAlone=true&_flowId=viewReportFlow
	url.append(AMPERSAND);
	return url.toString();

	}
    public static String urlLatestCommonPart(){ 
		StringBuilder url = new StringBuilder();
	url.append(Play.application().configuration().getString("jasper.server.protocol"));//http
	url.append(COLON); //http:
	url.append(BACKSLASH);//http:/
	url.append(BACKSLASH);//http://
	url.append(Play.application().configuration().getString("jasper.server.host"));//http://locahost
	url.append(COLON);//http://locahost:
	url.append(Play.application().configuration().getString("jasper.server.port"));//http://locahost:8080
	url.append(BACKSLASH);//http://locahost:8080/
	url.append(Play.application().configuration().getString("jasper.server.context"));//http://locahost:8080/jasperserver/flow.html?_flowId=viewReportFlow&standAlone=true&_flowId=viewReportFlow
	url.append(AMPERSAND);
	url.append(BACKSLASH);
	return url.toString();

	}
}

