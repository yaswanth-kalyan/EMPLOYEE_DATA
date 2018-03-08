package bean.pmo;

public class PTaskBean {
	
	public Long taskId;
	
	public String name;
	
	public String description;
	
	public Double estimatedTime;
	
	public Double actualTime;
	
	public String startDate;
	
	public String endDate;
	
	public Long userStId;

	@Override
	public String toString() {
		return "PTaskBean [taskId=" + taskId + ", name=" + name
				+ ", description=" + description + ", estimatedTime="
				+ estimatedTime + ", actualTime=" + actualTime + ", startDate="
				+ startDate + ", endDate=" + endDate + ", userStId=" + userStId
				+ "]";
	}

	

	
	
}
