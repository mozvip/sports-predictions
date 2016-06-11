package predictions.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Game {

	private int matchNum;
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm", timezone="CEST")
	private Date dateTime;
	private boolean done = false;
	private String group;
	private String stadium;
	private String homeTeam;
	private String awayTeam;
	private int homeScore;
	private int awayScore;
	private String winningTeam;

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getStadium() {
		return stadium;
	}

	public void setStadium(String stadium) {
		this.stadium = stadium;
	}

	public String getHomeTeam() {
		return homeTeam;
	}

	public void setHomeTeam(String homeTeam) {
		this.homeTeam = homeTeam;
	}

	public String getAwayTeam() {
		return awayTeam;
	}

	public void setAwayTeam(String awayTeam) {
		this.awayTeam = awayTeam;
	}

	public int getMatchNum() {
		return matchNum;
	}

	public void setMatchNum(int matchNum) {
		this.matchNum = matchNum;
	}

	public int getHomeScore() {
		return homeScore;
	}

	public void setHomeScore(int homeScore) {
		this.homeScore = homeScore;
	}

	public int getAwayScore() {
		return awayScore;
	}

	public void setAwayScore(int awayScore) {
		this.awayScore = awayScore;
	}

	public String getWinningTeam() {
		return winningTeam;
	}

	public void setWinningTeam(String winningTeam) {
		this.winningTeam = winningTeam;
	}

}
