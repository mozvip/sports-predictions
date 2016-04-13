package predictions.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MatchPredictions {
	
	@JsonProperty
	private String email;
	
	@JsonProperty
	private String password;
	
	@JsonProperty
	private String name;
	
	@JsonProperty
	private String authToken;
	
	@JsonProperty
	private List<MatchPrediction> match_predictions_attributes;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<MatchPrediction> getMatch_predictions_attributes() {
		return match_predictions_attributes;
	}

	public void setMatch_predictions_attributes(
			List<MatchPrediction> match_predictions_attributes) {
		this.match_predictions_attributes = match_predictions_attributes;
	}
	
	public String getEmail() {
		return email != null ? email.toLowerCase() : null;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getAuthToken() {
		return authToken;
	}
	
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public MatchPredictions() {
	}

}
