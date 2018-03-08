package models.recruitment;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.avaje.ebean.Model;

@Entity
public class RecruitmentRole extends Model implements Serializable{
	private static final long serialVersionUID = -111798510844886149L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;
	public String jobRoleName;
	@Column(columnDefinition="TEXT")
	public String description;
	public static final Finder<Long, RecruitmentRole> find= new Model.Finder<Long, RecruitmentRole>(RecruitmentRole.class);
	
	public static List<RecruitmentRole> getRoleList() {
		List<RecruitmentRole> recruitmentRolesList = RecruitmentRole.find.all();
		return recruitmentRolesList;
	}

}
