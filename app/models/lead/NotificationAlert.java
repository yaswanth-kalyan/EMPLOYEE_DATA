package models.lead;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import models.AppUser;
import models.Role;

import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Model;

import controllers.Application;

@Entity
public class NotificationAlert extends Model implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
			
	//Message
	public String notification;
	public Boolean alert = false;
	
	//Not Using
	public Long leadId;
	//Not Using
	public Long appUserId;
	
	//MessageDate
	public Date notificationDate;

	public String url;
	
	@ManyToOne
	public AppUser notifiedBy;

	@ManyToOne
	public AppUser notifiedTo;
	
	@ManyToOne
	public Role role;

	public static Model.Finder<Long, NotificationAlert> find = new Model.Finder<Long, NotificationAlert>(
			NotificationAlert.class);

	@Override
	public String toString() {
		return "NotificationAlert [id=" + id + ", notification=" + notification
				+ ", alert=" + alert + ", leadId=" + leadId + ", appUserId="
				+ appUserId + ", notificationDate=" + notificationDate
				+ ", url=" + url + ", notifiedBy=" + notifiedBy
				+ ", notifiedTo=" + notifiedTo + ", role=" + role + "]";
	}

	public static List<NotificationAlert> getListNotifications() {
		final List<NotificationAlert> listNotificationAlert = NotificationAlert.find.where().eq("alert", false).eq("notifiedTo", Application.getLoggedInUser()).or(Expr.eq("role", Application.getLoggedInUserRoleId() ),Expr.isNull("role")).orderBy("notificationDate DESC").findList();
		//Collections.reverse(listNotificationAlert.findList());
		//Logger.debug("order "+listNotificationAlert.findList());
		return listNotificationAlert;
	}
	public static List<NotificationAlert> getListNotificationsAll() {
			final List<NotificationAlert> listNotificationAlert = NotificationAlert.find.where().eq("notifiedTo", Application.getLoggedInUser()).or(Expr.eq("role", Application.getLoggedInUserRoleId() ),Expr.isNull("role")).orderBy("notificationDate DESC").findList();
			return listNotificationAlert;
		}

	public static Boolean getTrue(Long id) {
		Boolean flag = false;
		NotificationAlert notificationAlert = NotificationAlert.find.byId(id);
		if (notificationAlert != null) {
			flag = notificationAlert.alert;
		}
		return flag;
	}
	public static Long getNotificationCount() {
		//List<NotificationAlert> listNotificationAlert = new ArrayList<NotificationAlert>();
		final ExpressionList<NotificationAlert> listNotificationAlert = NotificationAlert.find.where().eq("alert", false).eq("notifiedTo", Application.getLoggedInUser()).or(Expr.eq("role", Application.getLoggedInUserRoleId() ),Expr.isNull("role"));
		/*listNotificationAlert = NotificationAlert.find.where()
				.eq("alert", false).eq("notifiedTo", Application.getLoggedInUser()).eq("role", Application.getLoggedInUserRoleId()).findList();*/
		return (long) listNotificationAlert.findList().size();
	}
	
}
