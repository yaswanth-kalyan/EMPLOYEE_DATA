package utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttachment;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.api.services.drive.model.File;

import models.recruitment.RecruitmentSelectionRound;
import play.Logger;

public class CalenderInviteOfInterviews {

	
		
		/** Project Location. */
		public static String  workingDir = System.getProperty("user.dir");
		
		/** Application name. */
	    private static final String APPLICATION_NAME =
	        "Google Calendar API Java Quickstart";

	    /** Directory to store user credentials for this application. */
	    private static final java.io.File DATA_STORE_DIR = new java.io.File(workingDir, ".credentials/calendar-java-quickstart");

	    /** Global instance of the {@link FileDataStoreFactory}. */
	    private static FileDataStoreFactory DATA_STORE_FACTORY;

	    /** Global instance of the JSON factory. */
	    private static final JsonFactory JSON_FACTORY =
	        JacksonFactory.getDefaultInstance();

	    /** Global instance of the HTTP transport. */
	    private static HttpTransport HTTP_TRANSPORT;

	    /** Global instance of the scopes required by this quickstart.
	     *
	     * If modifying these scopes, delete your previously saved credentials
	     * at ~/.credentials/calendar-java-quickstart
	     */
	    private static final List<String> SCOPES =
	        Arrays.asList(CalendarScopes.CALENDAR);
	    
	    static {
	        try {
	            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
	            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
	        } catch (Throwable t) {
	            t.printStackTrace();
	            System.exit(1);
	        }
	    }
		
	    // Refer to the Java quickstart on how to setup the environment:
	    // https://developers.google.com/google-apps/calendar/quickstart/java
	    // Change the scope to CalendarScopes.CALENDAR and delete any stored
	    // credentials.
//		public static void sendCalenderInviteOfInterviews(List<MailingList> mailingList,int sequence,Date eventDate) throws IOException{
		public static void sendCalenderInviteOfInterviews(Set<String> toMailingList,RecruitmentSelectionRound recSRound,String resumePath,String subject,String description,Boolean flage) throws IOException{

			
			try {
				com.google.api.services.calendar.Calendar service = getCalendarService();
				
				
				Event event = new Event();
				event.setSummary(subject);
				event.setLocation(""+recSRound.interviewVenue);
				event.setDescription(description);
					
				if(flage && resumePath != null){
					File file  = GoogleDriveEvent.uploadFileDrive(recSRound);//resumePath
					if (file != null) {
						GoogleDriveEvent.shareFileDrive(file.getId(), toMailingList);
						List<EventAttachment> attachments = event.getAttachments();
						if (attachments == null) {
							attachments = new ArrayList<EventAttachment>();
						}
						String fileURL = "https://drive.google.com/open?id=" + file.getId() + "&authuser=0";
						attachments.add(new EventAttachment().setFileId(file.getId()).setIconLink(file.getIconLink())
								.setFileUrl(fileURL).setMimeType(file.getMimeType()).setTitle(file.getName()));
						event.setAttachments(attachments);
						recSRound.googleDriveFileId = file.getId();
					}
				}
				
				Calendar start1 = Calendar.getInstance();
			    start1.setTime(recSRound.conductDate);
			   
			    Calendar end1 = Calendar.getInstance();
			    end1.setTime(recSRound.conductDate);
			    end1.add(Calendar.HOUR_OF_DAY, 1);
				
				DateTime startDateTime = new DateTime(start1.getTime());
				EventDateTime start = new EventDateTime()
				    .setDateTime(startDateTime).setTimeZone("Asia/Kolkata");
				event.setStart(start);
		
				DateTime endDateTime = new DateTime(end1.getTime());
				EventDateTime end = new EventDateTime()
				    .setDateTime(endDateTime)
				    .setTimeZone("Asia/Kolkata");
				event.setEnd(end);
		
//				String[] recurrence = new String[] {"RRULE:FREQ=DAILY;COUNT=2"};
//				event.setRecurrence(Arrays.asList(recurrence));
		
				List<String> mailingList = new ArrayList<String>(toMailingList);
				
				EventAttendee[] attendees = new EventAttendee[(mailingList.size())] ;
				for(int i=0;i<mailingList.size();i++){
					attendees[i] = new EventAttendee().setEmail(mailingList.get(i));
				}
				
				event.setAttendees(Arrays.asList(attendees));
		
				EventReminder[] reminderOverrides = new EventReminder[] {
				    new EventReminder().setMethod("email").setMinutes(24 * 60),
				    new EventReminder().setMethod("popup").setMinutes(10),
				};
				Event.Reminders reminders = new Event.Reminders()
				    .setUseDefault(false)
				    .setOverrides(Arrays.asList(reminderOverrides));
				event.setReminders(reminders);
				
				String calendarId = "primary";
				
				event = service.events().insert(calendarId, event).setSendNotifications(true).setSupportsAttachments(true).execute();
				
				if(flage){
					recSRound.interviewerCalendarEventId = event.getId();
				}else{
					recSRound.candidateCalendarEventId = event.getId();
				}
				recSRound.update();
				System.out.printf("Event created: %s\n", event.getHtmlLink()+", Event Id"+event.getId());
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	/**
     * Build and return an authorized Calendar client service.
     * @return an authorized Calendar client service
     * @throws IOException
     */
	public static com.google.api.services.calendar.Calendar getCalendarService() throws IOException {
	    Credential credential = authorize();
	    return new com.google.api.services.calendar.Calendar.Builder(
	            HTTP_TRANSPORT, JSON_FACTORY, credential)
            .setApplicationName(APPLICATION_NAME)
            .build();
	}

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in = CalenderInviteOfInterviews.class.getResourceAsStream("/public/client_secret.json");
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(
            flow, new LocalServerReceiver()).authorize("user");
        
        
        Logger.info("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }
    
    public static void updateExistingCalendarEvent(Set<String> toMailingList,RecruitmentSelectionRound recSRound,String subject,String description,Boolean flage){
    	   
    	try {
    			
			// Retrieve the service from the API
			com.google.api.services.calendar.Calendar service = getCalendarService();

		
			// Retrieve the event from the API
			Event event = null;
			if (flage) {
				event = service.events().get("primary", recSRound.interviewerCalendarEventId.trim()).execute();
			}else{
				event = service.events().get("primary", recSRound.candidateCalendarEventId.trim()).execute();
			}

			// Make a change
			event.setSummary(subject);
			event.setLocation(""+recSRound.interviewVenue);
			event.setDescription(description);
		

			Calendar start1 = Calendar.getInstance();
		    start1.setTime(recSRound.conductDate);
		   
		    Calendar end1 = Calendar.getInstance();
		    end1.setTime(recSRound.conductDate);
		    end1.add(Calendar.HOUR_OF_DAY, 1);
			
			DateTime startDateTime = new DateTime(start1.getTime());
			EventDateTime start = new EventDateTime()
			    .setDateTime(startDateTime).setTimeZone("Asia/Kolkata");
			event.setStart(start);
	
			DateTime endDateTime = new DateTime(end1.getTime());
			EventDateTime end = new EventDateTime()
			    .setDateTime(endDateTime)
			    .setTimeZone("Asia/Kolkata");
			event.setEnd(end);
			
			List<String> mailingList = new ArrayList<String>(toMailingList);
			
			EventAttendee[] attendees = new EventAttendee[(mailingList.size())] ;
			for(int i=0;i<mailingList.size();i++){
				attendees[i] = new EventAttendee().setEmail(mailingList.get(i));
			}
			
			event.setAttendees(Arrays.asList(attendees));
			
			EventReminder[] reminderOverrides = new EventReminder[] {
				    new EventReminder().setMethod("email").setMinutes(24 * 60),
				    new EventReminder().setMethod("popup").setMinutes(10),
				};
				Event.Reminders reminders = new Event.Reminders()
				    .setUseDefault(false)
				    .setOverrides(Arrays.asList(reminderOverrides));
				event.setReminders(reminders);
				
			// Update the event
			Event updatedEvent = service.events().update("primary", event.getId(), event).setSendNotifications(true).execute();

			Logger.info("updatedEvent : "+updatedEvent.getUpdated());
				
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static void deleteCalendarEvent(String calendarEventId,String fileId){
    	try {
			com.google.api.services.calendar.Calendar service = getCalendarService();
			service.events().delete("primary", calendarEventId).setSendNotifications(true).execute();
			Logger.info("Deleted Event on Google Calendar");
			
			if (fileId != null) {
				GoogleDriveEvent.deleteFileOnDrive(fileId);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
