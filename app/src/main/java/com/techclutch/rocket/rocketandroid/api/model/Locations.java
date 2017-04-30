package com.techclutch.rocket.rocketandroid.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arman on 4/30/2017.
 */

public class Locations {
    @Expose
    @SerializedName("Locations")
    List<Location> locations;

    public Locations() {
        locations = new ArrayList<>();
    }

    public void clear() {
        locations.clear();
    }

    public List<Location> getLocations() {
        return locations;
    }
}
