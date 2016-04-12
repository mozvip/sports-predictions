package predictions.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthenticationResult {
	
	@JsonProperty
	private String message;
	
	@JsonProperty
	private String authToken;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}
	
	

}
