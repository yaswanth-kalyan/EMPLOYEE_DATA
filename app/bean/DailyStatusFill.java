package bean;


import java.util.List;

public class DailyStatusFill {

	private List<String> today;
	private List<String> tomorrow;
	private List<String> problem;
	private int rate;

	public List<String> getToday() {
		return today;
	}
	public void setToday(List<String> today) {
		this.today = today;
	}
	public List<String> getTomorrow() {
		return tomorrow;
	}
	public void setTomorrow(List<String> tomorrow) {
		this.tomorrow = tomorrow;
	}
	public List<String> getProblem() {
		return problem;
	}
	public void setProblem(List<String> problem) {
		this.problem = problem;
	}
	public int getRate() {
		return rate;
	}
	public void setRate(int rate) {
		this.rate = rate;
	}
	

}
