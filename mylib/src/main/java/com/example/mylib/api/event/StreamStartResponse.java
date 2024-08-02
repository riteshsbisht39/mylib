package com.example.mylib.api.event;

import com.google.gson.annotations.SerializedName;


public class StreamStartResponse {
    @SerializedName("RED5_SERVER")
    public String red5Server;
    @SerializedName("RED5_APP_NAME")
    public String red5AppName;
    @SerializedName("STREAM_NAME")
    public String streamName;
    @SerializedName("URL")
    public String url;
    @SerializedName("LOCATION")
    public String location;

    @SerializedName("max_live_stream_allow")
    public Integer maxTimeLimit;

    @SerializedName("message")
    public String message;

}
