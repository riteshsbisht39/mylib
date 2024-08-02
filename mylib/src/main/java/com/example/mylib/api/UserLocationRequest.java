package com.example.mylib.api;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserLocationRequest implements Serializable {

    @SerializedName("lat")
    private Double lat;

    @SerializedName("lng")
    private Double lng;

    public UserLocationRequest(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }
}
