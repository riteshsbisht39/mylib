package com.example.mylib.api.plan;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ERPlansResponse {

    @SerializedName("uuid")
    @Expose
    public String uuid;
    @SerializedName("organization_uuid")
    @Expose
    public String organizationUuid;
    @SerializedName("sortkey")
    @Expose
    public int sortkey;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("banner")
    @Expose
    public String banner;
    @SerializedName("icon")
    @Expose
    public String icon;
    @SerializedName("version")
    @Expose
    public int version;
    @SerializedName("json_data")
    @Expose
    public ArrayList<String> jsonData = null;

    public ERPlansResponse(String organization_uuid, String uuid, String title, String icon, int version, int sortkey, String banner, ArrayList<String> details) {
        this.organizationUuid = organization_uuid;
        this.uuid = uuid;
        this.title = title;
        this.icon = icon;
        this.version = version;
        this.sortkey = sortkey;
        this.banner = banner;
        this.jsonData = details;
    }
}
