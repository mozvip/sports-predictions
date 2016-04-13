package predictions.model;

import java.security.Principal;
import java.util.Date;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User implements Principal {
	
	@NotNull
	private String community;
	
	@NotNull
	private String name;

	@NotNull
	private String email;
	
	@NotNull
	private String password;
	
	private String changePasswordToken;
	
	private Date lastLoginDate;

	@Min(value = 0)
	private int currentScore = 0;
	
	private boolean admin;
	
	public User() {
	}

	public User(String community, String name, String email, String password, String changePasswordToken,
			Date lastLoginDate, int currentScore, boolean admin) {
		super();
		this.community = community;
		this.name = name;
		this.email = email;
		this.password = password;
		this.changePasswordToken = changePasswordToken;
		this.lastLoginDate = lastLoginDate;
		this.currentScore = currentScore;
		this.admin = admin;
	}

	@JsonProperty
	public String getCommunity() {
		return community;
	}

	@JsonProperty
	public String getEmail() {
		return email.toLowerCase();
	}
	
	@JsonProperty
	public String getName() {
		return name;
	}

	@JsonProperty
	public int getCurrentScore() {
		return currentScore;
	}

	@JsonProperty
	public boolean isAdmin() {
		return admin;
	}

	@JsonProperty
	public Date getLastLoginDate() {
		return lastLoginDate;
	}
	
	public String getChangePasswordToken() {
		return changePasswordToken;
	}
}
