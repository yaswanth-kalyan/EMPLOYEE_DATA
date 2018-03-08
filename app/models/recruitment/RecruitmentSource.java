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
public class RecruitmentSource extends Model implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6726842700865263754L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;
	public String sourceName;
	@Column(columnDefinition="TEXT")
	public String description;
	
	public static final Finder<Long,RecruitmentSource> find=new Model.Finder<>(RecruitmentSource.class);

	public static List<RecruitmentSource> getSourceList() {
		List<RecruitmentSource> recruitmentSourceList = RecruitmentSource.find.all();
		return recruitmentSourceList;
	}
}
