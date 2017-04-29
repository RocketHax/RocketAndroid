package com.techclutch.rocket.rocketandroid.api;

import com.techclutch.rocket.rocketandroid.api.model.Location;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Arman on 4/29/2017.
 */

public interface FireService {

    @GET("/evacuation?latitude={latitude}&longitude={longtitude}")
    Call<List<Location>> getEvacuationList(@Path("latitude") double latitude, @Path("longitude") double longitude);

    @GET("/reportfire?latitude1={latitude1}&latitude2={latitude2}&longitude1={longitude1}&longitude2={longitude2}")
    Call<List<Location>> getFireList(@Path("latitude1") double latitude1, @Path("latitude2") double latitude2, @Path("longitude1") double longitude1, @Path("longitude2") double longitude2);

    @POST("/evacuation")
    Call<Location> requestEvacuation(@Body Location location);

    @POST("/reportfire")
    Call<Location> reportFire(@Body Location location);
}
