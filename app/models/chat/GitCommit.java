package models.chat;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class GitCommit extends Model {
	
	
	@Id
	@JsonIgnore
	public Long   id;
	public String commitId;
	
	@Column(columnDefinition="TEXT")
	public String message;
	
	@Column(columnDefinition="TEXT")
	public String commitUrl;
	@JsonIgnore
	public Date   committedAt;
	
	public String committerName;
	
	@JsonIgnore
	public String committerEmail;
	
	public String userName;
	
	public static Model.Finder<Long, GitCommit> find = new Model.Finder<Long, GitCommit>(GitCommit.class);

}