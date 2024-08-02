package com.example.mylib.api.event;

public class LocationRequest {

    public String incident_uuid;
    public double lat;
    public double lng;
    public double altitude;
    public double horizontal_accuracy;
    public double vertical_accuracy;
    public double direction_degrees;
    public long location_age; //in seconds

    public LocationRequest(String incident_uuid, double lat, double lng) {
        this.incident_uuid = incident_uuid;
        this.lat = lat;
        this.lng = lng;
    }
}
