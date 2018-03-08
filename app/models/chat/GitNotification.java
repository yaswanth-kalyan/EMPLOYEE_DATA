package models.chat;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
public class GitNotification extends Model{
	@Id
	public Long id;
	public String notificationTitle;
	public String repository;
	public String repositoryBranch;
	public Integer noOfCommits;
	public String committedBy;
	
	@JsonIgnore
	public String pusherEmail;
	
	@Column(columnDefinition="TEXT")
	@JsonIgnore
	public String originJson;
	
	@OneToOne
	@JsonIgnore
	public Message message;
	
	public String branchedFrom;
	public String repositoryUrl;
	public GitNotificationType gitNotificationType;
	
	@javax.persistence.Transient
	public GitIssue  gitIssue;
	
	@javax.persistence.Transient
	public GitCommitComment gitCommitComment;
	
	@OneToMany(cascade=CascadeType.ALL)
	public List<GitCommit> commitsList = new ArrayList<GitCommit>();
	public static Model.Finder<Long, GitNotification> find = new Model.Finder<Long, GitNotification>(GitNotification.class);
	
	public String gitMessage(){
	
		 String message = "<b>["+this.repository+":"+this.repositoryBranch+"] ";
		 if(this.noOfCommits==1){
		 message = message+this.noOfCommits+" new commit by "+this.committedBy+"</b><br><div class='git-href'>";
		 }
		 if(this.noOfCommits>1){
			 message = message+this.noOfCommits+" new commits by "+this.committedBy+"</b></br><div class='git-href'>";
		 }
		 if(this.commitsList!=null){
			 for(GitCommit commit:commitsList){
				 String commitMessage = commit.message;
				 if(commit.message.length()>80 ){
					 String cm = commit.message.substring(0, 80);
						String c = cm.substring(0, cm.lastIndexOf(" "));
						commitMessage=c;
				 }
				 String commitId = commit.commitId.substring(Math.max(0, commit.commitId.length() - 7));
                		message = message+"&nbsp;&nbsp;&nbsp;<a target='_blank' href='"+commit.commitUrl+"'>"+commitId+"</a> : "+commitMessage+" - "+committedBy+"</br>";		 
			 }
			 message=message+"</div>";
		 }
		
		return message;
	}
	
}