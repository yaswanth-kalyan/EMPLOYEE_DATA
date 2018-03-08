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

import controllers.PEController;
import models.AppUser;
import models.BaseEntity;
import models.Projects;
import play.Logger;

@Entity
public class PeSelfAppraisal extends BaseEntity implements Comparable<PeSelfAppraisal>{
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;
	
	@ManyToOne
	public AppUser appUser;
	
	public Date monthDate;//MM-YYYY
	public Double Saar;
	public Double War;
	@Column(columnDefinition="TEXT")
	public String issue;
	@OneToMany(cascade=CascadeType.ALL)
	public List<PeSelfAppraisalAnswer> answerList = new ArrayList<PeSelfAppraisalAnswer>();
	
	
	public Double getSaar() {
		return Saar;
	}

	public void setSaar(Double saar) {
		Saar = saar;
	}

	public Double getWar() {
		return War;
	}

	public void setWar(Double war) {
		War = war;
	}

	public static final Model.Finder<Long,PeSelfAppraisal> find=new Model.Finder<>(PeSelfAppraisal.class);
	
	public static Boolean checkSubmit(Long Id) throws ParseException{
		Boolean flag = false;
		SimpleDateFormat sf =new SimpleDateFormat("MM-yyyy");
		PeSelfAppraisal peSelfAppraisal = PeSelfAppraisal.find.where().eq("app_user_id", Id).eq("monthDate", sf.parse(sf.format(PEController.getMonth()))).findUnique();
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

	@Override
	public int compareTo(PeSelfAppraisal o) {
		// TODO Auto-generated method stub
		return (int)(this.War-o.War);
	}
}
