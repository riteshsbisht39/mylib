package com.example.mylib.api.map;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MapResponse implements Serializable {

    private String uuid;

    private String name;

    private String description;

    @SerializedName("map_id")
    private String mapId;

    @SerializedName("map_key")
    private String mapKey;

    @SerializedName("map_live_key")
    private String mapLiveKey;

    @SerializedName("map_type")
    private String mapType;

    @SerializedName("map_url")
    private String mapUrl;

    @SerializedName("map_region")
    private String mapRegion;

    @SerializedName("map_zoom")
    private Integer mapZoom;

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMapId() {
        return mapId;
    }

    public String getMapKey() {
        return mapKey;
    }

    public String getMapLiveKey() {
        return mapLiveKey;
    }

    public String getMapType() {
        return mapType;
    }

    public String getMapUrl() {
        return mapUrl;
    }

    public String getMapRegion() {
        return mapRegion;
    }

    public Integer getMapZoom() {
        return mapZoom;
    }
}
