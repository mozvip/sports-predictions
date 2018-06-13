package predictions.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Game {

	private int matchNum;
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm", timezone="GMT+2")
	private Date dateTime;
	private boolean done = false;
	private String group;
	private String round;
	private String stadium;
	private String homeTeamName;
	private int homeTeamWinnerFrom = -1;
	private int homeTeamLoserFrom = -1;
	private String awayTeamName;
	private int awayTeamWinnerFrom = -1;
	private int awayTeamLoserFrom = -1;
	private int homeScore;
	private int awayScore;
	private String winner;
	private String loser;

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

	public String getHomeTeamName() {
		return homeTeamName;
	}

	public void setHomeTeamName(String homeTeamName) {
		this.homeTeamName = homeTeamName;
	}

	public String getAwayTeamName() {
		return awayTeamName;
	}

	public void setAwayTeamName(String awayTeamName) {
		this.awayTeamName = awayTeamName;
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

	public String getWinner() {
		return winner;
	}

	public void setWinner(String winner) {
		this.winner = winner;
	}

	public int getHomeTeamWinnerFrom() {
		return homeTeamWinnerFrom;
	}

	public void setHomeTeamWinnerFrom(int homeTeamWinnerFrom) {
		this.homeTeamWinnerFrom = homeTeamWinnerFrom;
	}

	public int getAwayTeamWinnerFrom() {
		return awayTeamWinnerFrom;
	}

	public void setAwayTeamWinnerFrom(int awayTeamWinnerFrom) {
		this.awayTeamWinnerFrom = awayTeamWinnerFrom;
	}

	public String getRound() {
		return round;
	}

	public void setRound(String round) {
		this.round = round;
	}

	public int getHomeTeamLoserFrom() {
		return homeTeamLoserFrom;
	}

	public void setHomeTeamLoserFrom(int homeTeamLoserFrom) {
		this.homeTeamLoserFrom = homeTeamLoserFrom;
	}

	public int getAwayTeamLoserFrom() {
		return awayTeamLoserFrom;
	}

	public void setAwayTeamLoserFrom(int awayTeamLoserFrom) {
		this.awayTeamLoserFrom = awayTeamLoserFrom;
	}

	public String getLoser() {
		return loser;
	}

	public void setLoser(String loser) {
		this.loser = loser;
	}
}
