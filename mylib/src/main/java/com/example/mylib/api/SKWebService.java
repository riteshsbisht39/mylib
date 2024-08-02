package com.example.mylib.api;

import com.example.mylib.api.event.IncidentEndStatus;
import com.example.mylib.api.event.IncidentRequest;
import com.example.mylib.api.event.IncidentResponse;
import com.example.mylib.api.event.LocationRequest;
import com.example.mylib.api.event.StreamStartRequest;
import com.example.mylib.api.event.StreamStartResponse;
import com.example.mylib.api.login.OtpRequest;
import com.example.mylib.api.login.OtpResponse;
import com.example.mylib.api.map.MapsListResponse;
import com.example.mylib.api.plan.ERPlanInfoResponse;
import com.example.mylib.api.plan.ERPlanInfoVersion;
import com.example.mylib.api.plan.ERPlansInfoUpdateResponse;
import com.example.mylib.api.plan.ERPlansResponse;
import com.example.mylib.api.userDevice.DeviceRequest;
import com.example.mylib.api.userDevice.DeviceResponse;
import com.example.mylib.session.SessionInitResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface SKWebService {

    @GET("session_init")
    Call<SessionInitResponse> getCsrfToken();

    @POST("otp/verify/")
    Call<OtpResponse> verifyOtp(
            @Header("X-CSRFToken") String csrf,
            @Header("Cookie") String cookie,
            @Header("Referer") String ref,
            @Body OtpRequest content
    );

    @POST("user_device/")
    Call<DeviceResponse> registerDevice(
            @Header("X-CSRFToken") String csrf,
            @Header("Cookie") String cookie,
            @Header("Referer") String ref,
            @Body DeviceRequest content
    );

    @POST("incident/")
    Call<IncidentResponse> sendEvent(
            @Header("X-CSRFToken") String csrf,
            @Header("Cookie") String cookie,
            @Header("Referer") String ref,
            @Body IncidentRequest request
    );

    @POST("media_incident/start/")
    Call<StreamStartResponse> startEventStream(
            @Header("X-CSRFToken") String csrf,
            @Header("Cookie") String cookie,
            @Header("Referer") String ref,
            @Body StreamStartRequest content
    );

    @POST("incident_location/")
    Call<Void> sendLocation(
            @Header("X-CSRFToken") String csrf,
            @Header("Cookie") String cookie,
            @Header("Referer") String ref,
            @Body LocationRequest content
    );

    @GET("incident_media/stop/{incidentUuid}")
    Call<Void> stopEventStream(
            @Header("X-CSRFToken") String csrf,
            @Header("Cookie") String cookie,
            @Header("Referer") String ref,
            @Path("incidentUuid") String incidentUuid
    );

    @PUT("incident/{incidentUuid}/")
    Call<IncidentResponse> endEvent(
            @Header("X-CSRFToken") String csrf,
            @Header("Cookie") String cookie,
            @Header("Referer") String ref,
            @Body IncidentEndStatus content,
            @Path("incidentUuid") String incidentUuid
    );

    @POST("erp/?plans_only=1")
    Call<ArrayList<ERPlansResponse>> getERPans(
            @Header("X-CSRFToken") String csrf,
            @Header("Cookie") String cookie,
            @Header("Referer") String ref,
            @Body UserLocationRequest content
    );

    @GET("erp/{ERPId}")
    Call<ERPlanInfoResponse> getERPInfo(
            @Header("X-CSRFToken") String csrf,
            @Header("Cookie") String cookie,
            @Header("Referer") String ref,
            @Path("ERPId") String ERPId
    );

    @PUT("erp/{ERPId}/")
    Call<ERPlansInfoUpdateResponse> updateERPInfo(
            @Header("X-CSRFToken") String csrf,
            @Header("Cookie") String cookie,
            @Header("Referer") String ref,
            @Body ERPlanInfoVersion content,
            @Path("ERPId") String ERPId
    );

    @POST("map/")
    Call<MapsListResponse> getMaps(
            @Header("X-CSRFToken") String csrf,
            @Header("Cookie") String cookie,
            @Header("Referer") String ref,
            @Body UserLocationRequest content
    );
}