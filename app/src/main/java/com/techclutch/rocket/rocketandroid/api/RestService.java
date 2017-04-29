package com.techclutch.rocket.rocketandroid.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Arman on 4/29/2017.
 */

public class RestService {

    public static final String BASE_URL = "http://localhost:60131/api//";
    private Retrofit retrofit;
    private FireService fireService;

    public RestService() {
         retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        fireService = retrofit.create(FireService.class);
    }

    public FireService getFireService() {
        return fireService;
    }
}
