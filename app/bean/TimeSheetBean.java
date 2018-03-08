package bean;

import java.util.List;

public class TimeSheetBean {
	
	private long id;

	private List<Long> projectId;
	private List<Float> hours;
	private String remark;
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public List<Long> getProjectId() {
		return projectId;
	}
	public void setProjectId(List<Long> projectId) {
		this.projectId = projectId;
	}
	public List<Float> getHours() {
		return hours;
	}
	public void setHours(List<Float> hours) {
		this.hours = hours;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	

}
