package bean.pmo;

public class EpicBean {
	
	public Long epicId;
	
	public String name;
	
	public String description;
	
	public Long roadmapId;

	@Override
	public String toString() {
		return "EpicBean [id=" + epicId + ", name=" + name + ", description="
				+ description + ", roadmapId=" + roadmapId + "]";
	}

	
	

}
