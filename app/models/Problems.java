package models;

import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.avaje.ebean.Model;

@Entity
public class Problems extends Model implements Comparable<Problems>{
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public int id;
	
	@Column(columnDefinition="TEXT")
	public String problem;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getProblem() {
		return problem;
	}
	public void setProblem(String problem) {
		this.problem = problem;
	}
	
	@Override
	public String toString() {
		return ""+problem;
	}
	
	public int compareTo(Problems o) {
		return (int)(this.id-o.id);
	}

	public static List<Problems> getList(List<Problems> problems) {
		Collections.sort(problems);
		return problems;
	}
}

