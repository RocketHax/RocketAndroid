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
    Call<List<Location>> getEvacuationList(@Path("latitude") String latitude, @Path("longitude") String longitude);

    @GET("/reportfire?latitude1={latitude1}&latitude2={latitude2}&longitude1={longitude1}&longitude2={longitude2}")
    Call<List<Location>> getFireList(@Path("latitude1") String latitude1, @Path("latitude2") String latitude2, @Path("longitude1") String longitude1, @Path("longitude2") String longitude2);

    @POST("/evacuation")
    Call<Location> requestEvacuation(@Body String latitude, @Body String longitude);

    @POST("/reportfire")
    Call<Location> reportFire(@Body String latitude, @Body String longitude, @Body double bearing);
}
