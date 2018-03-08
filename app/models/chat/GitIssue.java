package models.chat;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class GitIssue extends Model {
	@Id
	public Long id;
	@Column(columnDefinition="TEXT")
	public String title;
	@Column(columnDefinition="TEXT")
	public String comment;
	@Column(columnDefinition="TEXT")
	public String issueUrl;
	
	@JsonIgnore
	@OneToOne
	public GitNotification gitNotificationId;
	
	public GitIssueType gitIssueType;
	public String IssueRaisedBy;
	@Column(columnDefinition="TEXT")
	public String IssueRaisedByUrl;
	public String fullName;
	public String issueNumber;
	public String commentedBy;
	public String assignedTo;
	@Column(columnDefinition="TEXT")
	public String assigneeUrl;
	
	@javax.persistence.Transient
	public List<UploadFileInfo>  uploadFileInfoList;
	
	public static Model.Finder<Long,GitIssue> find = new Model.Finder<Long,GitIssue>(GitIssue.class);
}
