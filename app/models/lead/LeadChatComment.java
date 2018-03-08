package models.lead;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;

@Entity
public class LeadChatComment extends Model implements Comparable<LeadChatComment>{
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	public Long leadId;
	public Long appUserId;
	
	@ManyToOne
	public LeadStatus leadStatus = null;
	
	@Column(columnDefinition="TEXT")
	public String comment;
	public Date commentDate;

	@ManyToMany(cascade=CascadeType.ALL)
	@JoinTable(name="Lead_Comment")
	public List<Lead> leads =new ArrayList<Lead>();

	@ManyToMany(cascade=CascadeType.ALL)
	@JoinTable(name="Comment_File")
	public List<StoreFile> listFIles =new ArrayList<StoreFile>();
	
	public static Model.Finder<Long, LeadChatComment> find = new Model.Finder<Long, LeadChatComment>(LeadChatComment.class);
	
	@Override
	public String toString() {
		return "LeadChatComment [id=" + id + ", leadId=" + leadId
				+ ", appUserId=" + appUserId + ", comment=" + comment
				+ ", commentDate=" + commentDate + ", leads=" + leads
				+ ", listFIles=" + listFIles + "]";
	}
	
	public static String getLatestStatus(Long leadId,Long appUserId){
	//	status.status.equals(models.lead.LeadStatus.getStatus123())
		/*List<LeadChatComment> listLeadChatComment = new ArrayList<LeadChatComment>();
		listLeadChatComment = LeadChatComment.find.where().eq("leadId", leadId).eq("appUserId", appUserId).findList();*/
		String status = "";
		/*if(listLeadChatComment != null && !listLeadChatComment.isEmpty()){
		status = listLeadChatComment.get(listLeadChatComment.size()-1).leadStatus.status;
		}else{
			Lead lead = Lead.find.byId(leadId);
			status = lead.leadStatus.status;
		}*/
		if(leadId != null){
			Lead lead = Lead.find.byId(leadId);
			status = lead.leadStatus.status;
		}
		return status;
	}

	public int compareTo(LeadChatComment o) {
		// TODO Auto-generated method stub
		return (int)(this.id-o.id);
	}
	
	public static int getTotalComments(Long leadId) {
		int size = 0;
		List<LeadChatComment> leadChatComments = new ArrayList<LeadChatComment>();
		leadChatComments = LeadChatComment.find.where().eq("lead_id",leadId).findList();
		if(!leadChatComments.isEmpty()){
			size = leadChatComments.size();
		}
		return size;
	}
}
