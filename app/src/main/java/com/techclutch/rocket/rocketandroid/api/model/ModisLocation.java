package com.techclutch.rocket.rocketandroid.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Arman on 4/30/2017.
 */

public class ModisLocation {
    @Expose
    @SerializedName("Coordinate")
    ModisCoordinate coordinate;

    public double getLatitude() {
        return coordinate.getLatitude();
    }

    public double getLongitude() {
        return coordinate.getLongitude();
    }
}
