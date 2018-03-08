package bean;

import java.util.ArrayList;
import java.util.List;

public class SelfAppraisalBean {

	public Long id;
	public List<Long> questionId = new ArrayList<Long>();
	public List<Long> rate = new ArrayList<Long>();
	public List<String> answer = new ArrayList<String>();
	public String issue;
	
}
