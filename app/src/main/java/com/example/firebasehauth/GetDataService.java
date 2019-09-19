package com.example.firebasehauth;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GetDataService {

    @GET("api/location/3534/")
    Call<Montreal> getMontrealWeather();

    @GET("api/location/3534/")
    Call<ConsolidatedWeather> getWeather();


}
