package predictions.model;

import java.security.Principal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
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
	
	private DateTime lastLoginDate;

	@Min(value = 0)
	private int currentScore = 0;
	
	@Min(value = 1)
	private int currentRanking = 1;
	
	@Min(value = 1)
	private int previousRanking = 1;
	
	private boolean admin = false;
	
	private boolean active = true;
	
	public User() {
	}

	public User(String community, String name, String email, String password, String changePasswordToken,
			DateTime lastLoginDate, int currentScore, int currentRanking, int previousRanking, boolean admin, boolean active) {
		super();
		this.community = community;
		this.name = name;
		this.email = email;
		this.password = password;
		this.changePasswordToken = changePasswordToken;
		this.lastLoginDate = lastLoginDate;
		this.currentScore = currentScore;
		this.admin = admin;
		this.active = active;
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
	@JsonFormat(locale = "fr", shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+2")
	public DateTime getLastLoginDate() {
		return lastLoginDate;
	}
	
	@JsonProperty
	public boolean isActive() {
		return active;
	}
	
	@JsonProperty
	public int getCurrentRanking() {
		return currentRanking;
	}
	
	public int getPreviousRanking() {
		return previousRanking;
	}

	public String getChangePasswordToken() {
		return changePasswordToken;
	}
}
