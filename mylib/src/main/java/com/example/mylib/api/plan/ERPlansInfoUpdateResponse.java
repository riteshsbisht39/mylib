package com.example.mylib.api.plan;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ERPlansInfoUpdateResponse {

    @SerializedName("uuid")
    @Expose
    public String uuid;
    @SerializedName("er_plan")
    @Expose
    public String erPlan;
    @SerializedName("user")
    @Expose
    public String user;
    @SerializedName("created")
    @Expose
    public String createdDate;
    @SerializedName("version")
    @Expose
    public int version;


    public ERPlansInfoUpdateResponse(String uuid, String erPlan, String user, String createdDate, int version) {
        this.uuid = uuid;
        this.erPlan = erPlan;
        this.user = user;
        this.createdDate = createdDate;
        this.version = version;
    }

}
