package com.github.mozvip.footballdata.model;

import java.time.ZonedDateTime;
import java.util.Map;

public class Fixture {

    private Map<String, Link> _links;
    private ZonedDateTime date;
    private FixtureStatus status;
    private int matchday;
    private String homeTeamName;
    private String awayTeamName;
    private Result result;
    private String odds;

    public Map<String, Link> get_links() {
        return _links;
    }

    public void set_links(Map<String, Link> _links) {
        this._links = _links;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public FixtureStatus getStatus() {
        return status;
    }

    public void setStatus(FixtureStatus status) {
        this.status = status;
    }

    public int getMatchday() {
        return matchday;
    }

    public void setMatchday(int matchday) {
        this.matchday = matchday;
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

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getOdds() {
        return odds;
    }

    public void setOdds(String odds) {
        this.odds = odds;
    }
}
