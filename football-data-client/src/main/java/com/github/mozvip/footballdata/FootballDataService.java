package com.github.mozvip.footballdata;

import com.github.mozvip.footballdata.model.Competition;
import com.github.mozvip.footballdata.model.Fixtures;
import com.github.mozvip.footballdata.model.Teams;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface FootballDataService {

    @GET("v1/competitions/{id}")
    Call<Competition> competition(@Path("id") int competitionId);

    @GET("v1/competitions/{id}/teams")
    Call<Teams> teams(@Path("id") int competitionId);

    @GET("v1/competitions/{id}/fixtures")
    Call<Fixtures> fixtures(@Path("id") int competitionId);

}
