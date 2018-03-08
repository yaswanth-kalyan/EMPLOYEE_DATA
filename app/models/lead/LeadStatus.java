package models.lead;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.avaje.ebean.Model;

@Entity
public class LeadStatus extends Model{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	public String status;
	@Column(columnDefinition="TEXT")
	public String description;
	
	public static Model.Finder<Long, LeadStatus> find = new Model.Finder<Long, LeadStatus>(LeadStatus.class);
	@Override
	public String toString() {
		return "LeadStatus [id=" + id + ", status=" + status + ", description="
				+ description + "]";
	}
	
	public static List<LeadStatus> getAllLeadStatus() {
		List<LeadStatus> listLeadStatus = new ArrayList<LeadStatus>();
		listLeadStatus = LeadStatus.find.all();
		return listLeadStatus;
	}
	
	public static Boolean getStatusFlag(Long commentId,Long statusid,Long leadId){
		Boolean flag = false;
		int count = 0;
		List<LeadChatComment> listLeadChatComment = new ArrayList<LeadChatComment>();
		listLeadChatComment = LeadChatComment.find.where().eq("leadId", leadId).findList();
		for(LeadChatComment leadChatComment : listLeadChatComment){
			if(commentId >= leadChatComment.id && statusid == leadChatComment.leadStatus.id) {
				count++;
			}
		}
		if(count == 1){
			flag = true;
		}
		return flag;
	}
	
	public static String getDefaultStatus(){
		String defaultstatus = "";
		LeadStatus status = LeadStatus.find.byId(1l);
			if(status != null){
				defaultstatus = status.status;
			}
		return defaultstatus;
	}
}
