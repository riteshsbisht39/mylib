package com.example.mylib.api.plan;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ERPlanInfoResponse extends ERPlansResponse {

    public String description;

    @SerializedName("ack_date")
    public String acknowledgementDate;

    public ERPlanInfoResponse(String description, String acknowledgementDate, String organization_uuid, String uuid, String title, String icon, int version, int sortkey, String banner, ArrayList<String> json_data) {
        super(organization_uuid, uuid, title, icon, version, sortkey, banner, json_data);
        this.description = description;
        this.acknowledgementDate = acknowledgementDate;
    }
}
