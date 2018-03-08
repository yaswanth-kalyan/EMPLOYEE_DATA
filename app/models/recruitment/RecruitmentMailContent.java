package models.recruitment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.avaje.ebean.Model;

import models.BaseEntity;

@Entity
public class RecruitmentMailContent extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	public MailType mailType;
	@Column(columnDefinition="TEXT")
	public String mailContent;
	
	public static final Model.Finder<Long,RecruitmentMailContent> find=new Model.Finder<>(RecruitmentMailContent.class);
}
