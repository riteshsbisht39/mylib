package com.example.mylib.api.event;

public class IncidentRequest {

    public String device_uuid;
    public String timer_uuid = null;
    public float lat = 1;
    public float lng = 1;
    public float horizontal_accuracy = 100;
    public String responder_type = null;
    public String old_api;
    public Boolean pb_trigger;
    public Boolean app_location_only;
    public Boolean ems;
    public Boolean is_event;
    public Boolean is_checkout_system_date;
    public String checkout_date;
    public String comments;

    public String media_type;
//    public String signature_url;

    public IncidentRequest(String device_uuid) {
        this.device_uuid = device_uuid;
    }
}
