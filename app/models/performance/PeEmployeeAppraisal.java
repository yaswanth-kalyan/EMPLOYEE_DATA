package models.performance;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.avaje.ebean.Model;

import controllers.Application;
import controllers.PEController;
import models.AppUser;
import models.BaseEntity;
import play.Logger;

@Entity
public class PeEmployeeAppraisal extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;
	
	@ManyToOne
	public AppUser projectManager;
	
	@ManyToOne
	public AppUser projectTeamMember;
	
	public Date monthDate;//MM-YYYY
	public Double Pr;
	public Double War;
	
	public Double getPr() {
		return Pr;
	}

	public void setPr(Double pr) {
		Pr = pr;
	}

	@Column(columnDefinition="TEXT")
	public String issue;
	@OneToMany(cascade=CascadeType.ALL)
	public List<PeEmployeeAppraisalAnswer> answerList = new ArrayList<PeEmployeeAppraisalAnswer>();
	
	public static final Model.Finder<Long,PeEmployeeAppraisal> find=new Model.Finder<>(PeEmployeeAppraisal.class);
	
	public static Boolean checkSubmit(Long Id) throws ParseException{
		Boolean flag = false;
		SimpleDateFormat sf =new SimpleDateFormat("MM-yyyy");
		PeEmployeeAppraisal peSelfAppraisal = PeEmployeeAppraisal.find.where().eq("projectManager", Application.getLoggedInUser()).
				eq("project_team_member_id", Id).eq("monthDate", sf.parse(sf.format(PEController.getMonth()))).findUnique();
		if(peSelfAppraisal != null){
			flag = true;
		}
		return flag;
	}
	
	/*public static Boolean getCondition(){
		Boolean flag = false;
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int month = cal.get(Calendar.MONTH);
		if(month == 1){
			if(day >= 25){
				flag = true;
			}
		}else{
			if(day >= 27){
				flag = true;
			}
		}
		
		if(day <= 3){
			flag = true;
		}
		return flag;
	}*/
}
