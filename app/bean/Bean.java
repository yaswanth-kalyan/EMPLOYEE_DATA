package bean;

import java.util.List;

public class Bean {


	public String role;
	public long id;
	public String password;
	public List roles;
	public String email;
	public String password1;
	public String jDate;
	public String dobirth = null;
	public String socialEmail;
	public String socialId;
	
	public String getjDate() {
		return jDate;
	}
	public void setjDate(String jDate) {
		this.jDate = jDate;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public List getRoles() {
		return roles;
	}
	public void setRoles(List roles) {
		this.roles = roles;
	}
	public String getPassword1() {
		return password1;
	}
	public void setPassword1(String password1) {
		this.password1 = password1;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getDobirth() {
		return dobirth;
	}
	public void setDobirth(String dobirth) {
		this.dobirth = dobirth;
	}
	public String getSocialEmail() {
		return socialEmail;
	}
	public void setSocialEmail(String socialEmail) {
		this.socialEmail = socialEmail;
	}
	public String getSocialId() {
		return socialId;
	}
	public void setSocialId(String socialId) {
		this.socialId = socialId;
	}
	
	
}
