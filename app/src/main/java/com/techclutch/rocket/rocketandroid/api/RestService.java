package com.techclutch.rocket.rocketandroid.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Arman on 4/29/2017.
 */

public class RestService {

    //public static final String BASE_URL = "http://api.geonames.org";
    //public static final String BASE_URL = "http://10.30.41.87:60131";
    //public static final String BASE_URL = "http://172.20.10.2:60131";
    public static final String BASE_URL = "http://fireescape.azurewebsites.net";
    private Retrofit retrofit;
    private FireService fireService;
    private OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
            .readTimeout(5, TimeUnit.MINUTES)
            .connectTimeout(5, TimeUnit.MINUTES)
            .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));


    public RestService() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(clientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                //.addConverterFactory(MoshiConverterFactory.create())
                .build();

        fireService = retrofit.create(FireService.class);
    }

    public FireService getFireService() {
        return fireService;
    }
}
