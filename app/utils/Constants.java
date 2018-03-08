package utils;

import play.Play;

public class Constants {
	   

    public static final String EMAIL_HOST = Play.application().configuration().getString("mail.smtp.host");
    public static final String EMAIL_PORT = Play.application().configuration().getString("mail.smtp.port");
    public static final String EMAIL_AUTH = Play.application().configuration().getString("mail.smtp.auth");
    public static final String EMAIL_STARTTLS_ENABLE = Play.application().configuration().getString("mail.smtp.starttls.enable");
    public static final String EMAIL_USERNAME = Play.application().configuration().getString("mail.smtp.user");
    public static final String EMAIL_PASSWORD = Play.application().configuration().getString("mail.smtp.pass");
    public static final String EMAIL_HR_USERNAME = Play.application().configuration().getString("mail.smtp.hrUser");
    public static final String EMAIL_HR_PASSWORD = Play.application().configuration().getString("mail.smtp.hrPass");
    public static final String EMAIL_CHANNEL = Play.application().configuration().getString("mail.smtp.channel");
    public static final String EMAIL_FROM = Play.application().configuration().getString("mail.from");
    public static final String SOCKET_ADSRESS = Play.application().configuration().getString("socket-address");
    public static final String FORM_DISPLAY = Play.application().configuration().getString("login-form-display");
    public static final String JASPER_SERVER_LINK = Play.application().configuration().getString("jasper.serverlink");
    public static final String IS_LOGOUT = Play.application().configuration().getString("is_Logout");
    
}