package model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Match {

	@JsonProperty
	private int matchNum;

	@JsonProperty
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm", timezone="GMT+2")
	private ZonedDateTime dateTime;

	@JsonProperty
	private String group;

	@JsonProperty
	private String stadium;

	@JsonProperty
	private String homeTeamName;

	@JsonProperty
	private Integer homeTeamWinnerFrom;

	@JsonProperty
	private String awayTeamName;

	@JsonProperty
	private Integer awayTeamWinnerFrom;

	public Match(int matchNum, ZonedDateTime dateTime, String group, String stadium, String homeTeamName, String awayTeamName) {
		this.matchNum = matchNum;
		this.dateTime = dateTime;
		this.group = group;
		this.stadium = stadium.trim();
		this.homeTeamName = homeTeamName.trim();
		this.awayTeamName = awayTeamName.trim();
	}

	public Match(int matchNum, ZonedDateTime dateTime, String group, String stadium, int homeTeamWinnerFrom, int awayTeamWinnerFrom) {
		this.matchNum = matchNum;
		this.dateTime = dateTime;
		this.group = group;
		this.stadium = stadium;
		this.homeTeamWinnerFrom = homeTeamWinnerFrom;
		this.awayTeamWinnerFrom = awayTeamWinnerFrom;
	}

	public int getMatchNum() {
		return matchNum;
	}

	public void setMatchNum(int matchNum) {
		this.matchNum = matchNum;
	}

	public ZonedDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(ZonedDateTime dateTime) {
		this.dateTime = dateTime;
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

	public Integer getHomeTeamWinnerFrom() {
		return homeTeamWinnerFrom;
	}

	public void setHomeTeamWinnerFrom(Integer homeTeamWinnerFrom) {
		this.homeTeamWinnerFrom = homeTeamWinnerFrom;
	}

	public String getAwayTeamName() {
		return awayTeamName;
	}

	public void setAwayTeamName(String awayTeamName) {
		this.awayTeamName = awayTeamName;
	}

	public Integer getAwayTeamWinnerFrom() {
		return awayTeamWinnerFrom;
	}

	public void setAwayTeamWinnerFrom(Integer awayTeamWinnerFrom) {
		this.awayTeamWinnerFrom = awayTeamWinnerFrom;
	}

	@Override
	public String toString() {
		return "Match{" +
				"matchNum=" + matchNum +
				", dateTime=" + dateTime +
				", group='" + group + '\'' +
				", stadium='" + stadium + '\'' +
				", homeTeamName='" + homeTeamName + '\'' +
				", homeTeamWinnerFrom=" + homeTeamWinnerFrom +
				", awayTeamName='" + awayTeamName + '\'' +
				", awayTeamWinnerFrom=" + awayTeamWinnerFrom +
				'}';
	}
}
