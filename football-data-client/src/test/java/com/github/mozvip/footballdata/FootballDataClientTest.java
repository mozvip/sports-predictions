package com.github.mozvip.footballdata;

import com.github.mozvip.footballdata.model.Competition;
import com.github.mozvip.footballdata.model.Fixtures;
import com.github.mozvip.footballdata.model.Teams;
import org.junit.Before;
import retrofit2.Call;

import java.io.IOException;

import static org.junit.Assert.*;

public class FootballDataClientTest {

    FootballDataClient client;

    @Before
    public void setUp() {
        client = FootballDataClient.Builder("24d157912c1742a48feff025e0172b38").build();
    }

    @org.junit.Test
    public void competition() throws IOException {
        Competition competition = client.competition(467);
        assertEquals("World Cup 2018 Russia", competition.getCaption());
    }

    @org.junit.Test
    public void competition424() throws IOException {
        Competition competition = client.competition(424);
        assertEquals("European Championships France 2016", competition.getCaption());
        assertEquals(51, competition.getNumberOfGames());

        Fixtures fixtures = client.fixtures(competition.getId());
        assertNotNull(fixtures);
    }

    @org.junit.Test
    public void teams() throws IOException {
        Teams teams = client.teams(467);
        assertEquals(32, teams.getCount());
    }

    @org.junit.Test
    public void fixtures() throws IOException {
        Fixtures fixtures = client.fixtures(467);
        assertEquals(64, fixtures.getCount());
    }
}