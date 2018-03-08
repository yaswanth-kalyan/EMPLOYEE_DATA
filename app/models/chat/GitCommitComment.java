package models.chat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import com.avaje.ebean.Model;

@Entity
public class GitCommitComment extends Model {
	@Id
	public Long id;
	public String full_name;
	public String commitId;
	public String commentBy;
	public String commitUrl;
	@Column(columnDefinition="TEXT")
	public String comment;
	@OneToOne
	public GitNotification gitNotificationId;
	public static Model.Finder<Long, GitCommitComment> find = new Model.Finder<Long, GitCommitComment>(GitCommitComment.class);


}
