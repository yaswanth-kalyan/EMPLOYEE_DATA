package models.recruitment;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import com.avaje.ebean.Model;

@Entity
public class RecruitmentSkill extends Model implements Serializable {
private static final long serialVersionUID = 5459924231875737879L;
@Id
@GeneratedValue(strategy=GenerationType.AUTO)
public Long id;
public String skillName;
@Column(columnDefinition="TEXT")
public String description;
@ManyToMany
@JoinTable(name="recruitment_mandatoryskills")
public List<RecruitmentJob> mandatoryJobs;
@ManyToMany
@JoinTable(name="recruitment_desiredskills")
public List<RecruitmentJob> desiredJobs;
public static final Finder<Long, RecruitmentSkill> find=new Model.Finder<Long, RecruitmentSkill>(RecruitmentSkill.class);
}
