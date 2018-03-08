package utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;

import models.recruitment.RecruitmentSelectionRound;
import play.Logger;

public class GoogleDriveEvent {

	/** Project Location. */
	public static String  workingDir = System.getProperty("user.dir");
	
	/** Application name. */
    private static final String APPLICATION_NAME =
        "Drive API Java Quickstart";

    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(workingDir, ".credentials/drive-java-quickstart");

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
     * at ~/.credentials/drive-java-quickstart
     */
    private static final List<String> SCOPES =
        Arrays.asList(DriveScopes.DRIVE_METADATA,DriveScopes.DRIVE_FILE,DriveScopes.DRIVE);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in =
        		GoogleDriveEvent.class.getResourceAsStream("/public/client_secret_drive.json");
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
        Logger.info(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Drive client service.
     * @return an authorized Drive client service
     * @throws IOException
     */
    public static Drive getDriveService() throws IOException {
        Credential credential = authorize();
        return new Drive.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
    
    public static File uploadFileDrive(RecruitmentSelectionRound recSRound){
	    try {
	    	
	    	java.io.File outputFileResume = null;
			byte[] resume = recSRound.recruitmentApplicant.resume;
			String fileName = recSRound.recruitmentApplicant.fileName;
			
			if(resume != null){
				outputFileResume = new java.io.File("conf/excel/"+recSRound.recruitmentApplicant.fileName);
				FileOutputStream outputStream = new FileOutputStream(outputFileResume);
				outputStream.write(resume);
				outputStream.close();
				
				Drive driveService = getDriveService();
				File fileMetadata = new File();
//				java.io.File filePath = new java.io.File(resumePath);
				fileMetadata.setName(outputFileResume.getName());
				fileMetadata.setMimeType("application/vnd.google-apps.spreadsheet");
				
				fileName = fileName.substring(fileName.indexOf("."),fileName.length());
				String fileContent = "";
				if (fileName.equalsIgnoreCase(".pdf")) {
					fileContent = "application/pdf";
				}else {
					fileContent = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
				}
				
				FileContent mediaContent = new FileContent(fileContent, outputFileResume);
				File file = driveService.files().create(fileMetadata, mediaContent)
						.execute();
				Logger.info("File ID: " + file.getId());
				return file;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
    }
    
    public static void deleteFileOnDrive(String fileId){
    	try {
    		Drive driveService = getDriveService();
    		driveService.files().delete(fileId).execute();
    		Logger.info("Deleted File on Google Drive");
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public static void getFiles() throws IOException {
        // Build a new authorized API client service.
        Drive service = getDriveService();

        // Print the names and IDs for up to 10 files.
        FileList result = service.files().list()
             .setPageSize(10)
             .setFields("nextPageToken, files(id, name)")
             .execute();
        List<File> files = result.getFiles();
        if (files == null || files.size() == 0) {
            Logger.info("No files found.");
        } else {
            Logger.info("Files:");
            for (File file : files) {
                System.out.printf("%s (%s)\n", file.getName(), file.getId());
            }
        }
    }
    
    
    public static void shareFileDrive(String fileId,Set<String> toMailingList){
    	try {
    		Drive service = getDriveService();
			JsonBatchCallback<Permission> callback = new JsonBatchCallback<Permission>() {
			    @Override
			    public void onFailure(GoogleJsonError e,
			                          HttpHeaders responseHeaders)
			            throws IOException {
			        // Handle error
			        System.err.println(e.getMessage());
			    }

			    @Override
			    public void onSuccess(Permission permission,
			                          HttpHeaders responseHeaders)
			            throws IOException {
			        Logger.info("Permission ID: " + permission.getId());
			    }
			};
			
			List<String> mailingList = new ArrayList<String>(toMailingList);
			EventAttendee[] attendees = new EventAttendee[(mailingList.size())] ;
			for(int i=0;i<mailingList.size();i++){
				attendees[i] = new EventAttendee().setEmail(mailingList.get(i));
			}
			
			BatchRequest batch = service.batch();
			for(String mail : toMailingList){
				Permission userPermission = new Permission()
				        .setType("user")
				        .setRole("writer")
				        .setEmailAddress(mail);
				
				service.permissions().create(fileId, userPermission).setSendNotificationEmail(false)
				        .queue(batch, callback);
			}

			Permission domainPermission = new Permission()
			        .setType("domain")
			        .setRole("reader")
			        .setDomain("thrymr.net");
			service.permissions().create(fileId, domainPermission).setSendNotificationEmail(false)
			        .queue(batch, callback);

			batch.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

}
