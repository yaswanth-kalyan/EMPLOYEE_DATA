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
public class RecruitmentCategory extends Model implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7460346541190119575L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;
	public String jobCategoryName;
	@Column(columnDefinition="TEXT")
	public String description;
	public static final Finder<Long, RecruitmentCategory> find=new Model.Finder<Long, RecruitmentCategory>(RecruitmentCategory.class);
	
	public static String getjobCategoryName(Long id){
		return null;
	}

	public static List<RecruitmentCategory> getCategoryList() {
		List<RecruitmentCategory> recruitmentCategoriesList = RecruitmentCategory.find.all();
		return recruitmentCategoriesList;
	}
}
