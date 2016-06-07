package predictions.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GoogleReCaptchaResponse {
	
	private boolean success;
	@JsonProperty(value="challenge_ts")
	private Date challengeTs;
	private String hostname;
	@JsonProperty(value="error_codes")
	private List<String> errorCodes;

	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public Date getChallengeTs() {
		return challengeTs;
	}
	public void setChallengeTs(Date challengeTs) {
		this.challengeTs = challengeTs;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public List<String> getErrorCodes() {
		return errorCodes;
	}
	public void setErrorCodes(List<String> errorCodes) {
		this.errorCodes = errorCodes;
	}
	
	

}
