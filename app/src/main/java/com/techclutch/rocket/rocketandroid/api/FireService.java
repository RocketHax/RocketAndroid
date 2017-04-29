package com.techclutch.rocket.rocketandroid.api;

import com.techclutch.rocket.rocketandroid.api.model.Location;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Arman on 4/29/2017.
 */

public interface FireService {
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("/api/evacuation")
    Call<List<Location>> getEvacuationList(@Query("latitude") double latitude, @Query("longitude") double longitude);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("/api/reportfire")
    Call<List<Location>> getFireList(@Query("latitude1") double latitude1, @Query("latitude2") double latitude2, @Query("longitude1") double longitude1, @Query("longitude2") double longitude2);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("/api/evacuation")
    Call<Location> requestEvacuation(@Body Location location);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("/api/reportfire")
    Call<Location> reportFire(@Body Location location);

    ////citiesJSON?north=44.1&south=-9.9&east=-22.4&west=55.2&lang=de&username=demo
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("/citiesJSON")
    Call<Void> getCities(@Query("north") double north, @Query("south") double south, @Query("east") double east,
                             @Query("west") double west, @Query("lang") String lang, @Query("username") String username);
}
