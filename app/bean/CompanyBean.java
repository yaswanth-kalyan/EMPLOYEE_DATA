package bean;

import java.util.ArrayList;
import java.util.List;


public class CompanyBean {
	public List<Long> cid = new ArrayList<Long>();	;
	public List<String> jobTitle = new ArrayList<String>();
	public long leadId;
	public List<Long> getCid() {
		return cid;
	}
	public void setCid(List<Long> cid) {
		this.cid = cid;
	}
	public List<String> getJobTitle() {
		return jobTitle;
	}
	public void setJobTitle(List<String> jobTitle) {
		this.jobTitle = jobTitle;
	}
	@Override
	public String toString() {
		return "CompanyBean [cid=" + cid + ", jobTitle=" + jobTitle + "]";
	}
}
