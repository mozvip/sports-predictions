package com.github.mozvip.footballdata.model;

import java.util.List;
import java.util.Map;

public class Teams {

    private Map<String, Link> _links;
    private int count;

    private List<Team> teams;

    public Map<String, Link> get_links() {
        return _links;
    }

    public void set_links(Map<String, Link> _links) {
        this._links = _links;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }
}
