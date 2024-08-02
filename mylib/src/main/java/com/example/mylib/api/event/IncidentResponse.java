package com.example.mylib.api.event;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class IncidentResponse implements Serializable {

    public String observing_pro_org;
    public String observing_geofence;
    public String geofence;
    public ArrayList<Observer> observers_watching;
    public ArrayList<MessageResponse> incident_chat;
    public String uuid;
    public String created;
    public String modified;
    public String status;
    public String status_message;
    public String event_type;
    public String close_date;
    public String legacy_short_code;
    public String browsable_url;
    public String type;
    public String message;
    public String token;
    public String id;
    public Boolean is_read;
    public SPMap map;

    public Boolean two_way_live_stream;
    public String channel_name;
    public Boolean portrait_only;
    public String token_stream;

    public class Observer implements Serializable {

        public String organization_name;
        public String first_name;
        public String last_name;
        public String email;
    }

    public class MessageResponse implements Serializable {

        public String author_first_name;
        public String author_last_name;
        public String author_role;
        public String author_org;
        public String uuid;
        public String created;
        public String message;
    }

    public class SPMap implements Serializable {

        @SerializedName("api_key")
        public String apiKey;
    }
}
