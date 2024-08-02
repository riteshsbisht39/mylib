package com.example.mylib.api.map;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class MapsListResponse implements Serializable {

    @SerializedName("user_uuid")
    public String userUuid;

    @SerializedName("user_name")
    public String userName;

    @SerializedName("maps")
    public List<MapResponse> maps;

}
