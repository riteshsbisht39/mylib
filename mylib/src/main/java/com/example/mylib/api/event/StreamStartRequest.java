package com.example.mylib.api.event;

import com.google.gson.annotations.SerializedName;


public class StreamStartRequest {
    @SerializedName("incident_id")
    public String incidentId;
    @SerializedName("media_type")
    public String mediaType;

    public StreamStartRequest(String incidentId, String mediaType) {
        this.incidentId = incidentId;
        this.mediaType = mediaType;
    }
}
