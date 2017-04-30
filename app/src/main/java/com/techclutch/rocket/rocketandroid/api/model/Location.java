package com.techclutch.rocket.rocketandroid.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Arman on 4/29/2017.
 */

public class Location {
    @SerializedName("Id")
    @Expose(serialize = false, deserialize = false)
    String id;
    @SerializedName("Latitude")
    @Expose
    double latitude;
    @SerializedName("Longitude")
    @Expose
    double longitude;
    @SerializedName("Bearing")
    @Expose
    double bearing;
    @SerializedName("Name")
    @Expose()
    String name;
    @SerializedName("Type")
    @Expose()
    String type;

    public Location(String id, double latitude, double longitude, double bearing, String name, String type) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.bearing = bearing;
        this.name = name;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getBearing() {
        return bearing;
    }

    public void setBearing(double bearing) {
        this.bearing = bearing;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
