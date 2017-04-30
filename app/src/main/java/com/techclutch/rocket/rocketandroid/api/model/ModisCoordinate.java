package com.techclutch.rocket.rocketandroid.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Arman on 4/30/2017.
 */

public class ModisCoordinate {
    @Expose
    @SerializedName("Latitude")
    double latitude;
    @Expose
    @SerializedName("Longitude")
    double longitude;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
