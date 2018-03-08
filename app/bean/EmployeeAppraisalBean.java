package bean;

import java.util.ArrayList;
import java.util.List;

public class EmployeeAppraisalBean {

	public Long id;
	public Long teamMemberId;
	public List<Long> questionId = new ArrayList<Long>();
	public List<Long> rate = new ArrayList<Long>();
	public List<String> answer = new ArrayList<String>();
	public String issue;
	
}
