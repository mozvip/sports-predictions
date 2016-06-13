package predictions.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GameStats {
	
	@JsonProperty
	private String homeTeam;
	
	@JsonProperty
	private String awayTeam;

	@JsonProperty
	private int total;

	@JsonProperty
	private int perfect;

	@JsonProperty
	private int good;

	@JsonProperty
	private int bad;

	public GameStats(String homeTeam, String awayTeam, int total, int perfect, int good, int bad) {
		this.homeTeam = homeTeam;
		this.awayTeam = awayTeam;
		this.total = total;
		this.perfect = perfect;
		this.good = good;
		this.bad = bad;
	}
	
	public String getHomeTeam() {
		return homeTeam;
	}
	
	public String getAwayTeam() {
		return awayTeam;
	}

	public int getTotal() {
		return total;
	}

	public int getPerfect() {
		return perfect;
	}

	public int getGood() {
		return good;
	}

	public int getBad() {
		return bad;
	}

}
