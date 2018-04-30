package com.github.mozvip.footballdata.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonAppend;

public class Result {

    private int goalsHomeTeam;
    private int goalsAwayTeam;

    Score halfTime;

    Score extraTime;

    Score penaltyShootout;

    public int getGoalsHomeTeam() {
        return goalsHomeTeam;
    }

    public void setGoalsHomeTeam(int goalsHomeTeam) {
        this.goalsHomeTeam = goalsHomeTeam;
    }

    public int getGoalsAwayTeam() {
        return goalsAwayTeam;
    }

    public void setGoalsAwayTeam(int goalsAwayTeam) {
        this.goalsAwayTeam = goalsAwayTeam;
    }

    public Score getHalfTime() {
        return halfTime;
    }

    public void setHalfTime(Score halfTime) {
        this.halfTime = halfTime;
    }

    public Score getExtraTime() {
        return extraTime;
    }

    public void setExtraTime(Score extraTime) {
        this.extraTime = extraTime;
    }

    public Score getPenaltyShootout() {
        return penaltyShootout;
    }

    public void setPenaltyShootout(Score penaltyShootout) {
        this.penaltyShootout = penaltyShootout;
    }
}
