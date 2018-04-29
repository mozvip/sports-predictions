package predictions.model.db;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ActualResult {
	
	@JsonProperty
	private int match_id;
	@JsonProperty
	private int away_score;
	@JsonProperty
	private String away_team_id;
	@JsonProperty
	private int home_score;
	@JsonProperty
	private String home_team_id;
	@JsonProperty
	private boolean home_winner;
	@JsonProperty
	private boolean validated;

	public ActualResult() {
	}

	public ActualResult(int match_id, int away_score, String away_team_id,
			int home_score, String home_team_id, boolean home_winner,
			boolean validated) {
		super();
		this.match_id = match_id;
		this.away_score = away_score;
		this.away_team_id = away_team_id;
		this.home_score = home_score;
		this.home_team_id = home_team_id;
		this.home_winner = home_winner;
		this.validated = validated;
	}


	public boolean isHome_winner() {
		return home_winner;
	}

	public void setHome_winner(boolean home_winner) {
		this.home_winner = home_winner;
	}

	public int getMatch_id() {
		return match_id;
	}
	public void setMatch_id(int match_id) {
		this.match_id = match_id;
	}
	public int getAway_score() {
		return away_score;
	}
	public void setAway_score(int away_score) {
		this.away_score = away_score;
	}
	public String getAway_team_id() {
		return away_team_id;
	}
	public void setAway_team_id(String away_team_id) {
		this.away_team_id = away_team_id;
	}
	public int getHome_score() {
		return home_score;
	}
	public void setHome_score(int home_score) {
		this.home_score = home_score;
	}
	public String getHome_team_id() {
		return home_team_id;
	}
	public void setHome_team_id(String home_team_id) {
		this.home_team_id = home_team_id;
	}

	public boolean isValidated() {
		return validated;
	}

}
