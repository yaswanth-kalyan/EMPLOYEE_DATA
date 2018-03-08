package models.recruitment;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import models.AppUser;

import com.avaje.ebean.Model;

@Entity
public class InterviewerAppUser extends Model{
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;
	
	@ManyToOne
	public AppUser interviewer;
	
	@ManyToMany(cascade=CascadeType.ALL)
	@JoinTable(name="recruitment_selection_round_interviewer")
	public List<RecruitmentSelectionRound> recruitmentSelectionRounds=new ArrayList<RecruitmentSelectionRound>();
	
	
	public static final Model.Finder<Long, InterviewerAppUser> find=new Model.Finder<Long, InterviewerAppUser>(InterviewerAppUser.class);
	
	public static List<AppUser> getAppUserListThoseAreInterviewer()
	{
		List<AppUser> appUsers=new ArrayList<AppUser>();
		List<InterviewerAppUser> interviewerAppUsers=InterviewerAppUser.find.all();
		for(InterviewerAppUser interviewerAppUser:interviewerAppUsers)
		{
			appUsers.add(interviewerAppUser.interviewer);
		}
		return appUsers;
	}
	
	public static Boolean checkAppUser(Long id){
		Boolean flag = false;
		InterviewerAppUser interviewerAppUser = InterviewerAppUser.find.where().eq("interviewer_id", id).findUnique();
		if(interviewerAppUser != null){
			flag = true;
		}
		return flag;
		
	}
	

}
