package bean.pmo;

public class RoadMapBean {
	
	public Long roadmapId;
	
	public String title;
	
	public String description;
	
	public Long projectId;

	@Override
	public String toString() {
		return "RoadMapBean [id=" + roadmapId + ", title=" + title + ", description="
				+ description + ", projectId=" + projectId + "]";
	}
	
	
}
