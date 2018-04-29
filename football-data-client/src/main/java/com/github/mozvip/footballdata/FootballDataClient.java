package com.github.mozvip.footballdata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.mozvip.footballdata.model.Competition;
import com.github.mozvip.footballdata.model.Fixtures;
import com.github.mozvip.footballdata.model.Teams;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;

import java.io.IOException;

public class FootballDataClient {

    public static final class Builder {

        private String apiKey;

        private Builder(String apiKey) {
            this.apiKey = apiKey;
        }

        public FootballDataClient build() {
            return new FootballDataClient( apiKey );
        }

    }

    public static Builder Builder(String apiKey) {
        return new Builder(apiKey);
    }

    private String apiKey;
    private FootballDataService service = null;

    public FootballDataClient(String apiKey) {
        this.apiKey = apiKey;

        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule()); // new module, NOT JSR310Module

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.football-data.org")
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .build();
        service = retrofit.create(FootballDataService.class);
    }

    public Competition competition(int competitionId) throws IOException {
        Response<Competition> response = service.competition(competitionId).execute();
        if (response.isSuccessful()) {
            return response.body();
        }
        throw new IOException(response.message());
    }

    public Teams teams(int competitionId) throws IOException {
        Response<Teams> response = service.teams(competitionId).execute();
        if (response.isSuccessful()) {
            return response.body();
        }
        throw new IOException(response.message());
    }

    public Fixtures fixtures(int competitionId) throws IOException {
        Response<Fixtures> response = service.fixtures(competitionId).execute();
        if (response.isSuccessful()) {
            return response.body();
        }
        throw new IOException(response.message());
    }
}

