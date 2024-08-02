package com.example.mylib.api.plan;

import android.util.Log;

import com.example.mylib.api.SKApiClient;
import com.example.mylib.api.SKWebService;
import com.example.mylib.session.SessionInitResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ERPInfo {

    public interface OnResponseListener {
        void onResponse(ERPlanInfoResponse result);
        void onErrorResponse(ResponseBody errBody);
        void onFailure();
    }

    public void getERPInfo(String id, OnResponseListener responseListener) {
        SKWebService service = SKApiClient.getClient().create(SKWebService.class);;
        Call<SessionInitResponse> apiCall = service.getCsrfToken();

        apiCall.enqueue(new Callback<SessionInitResponse>() {
            @Override
            public void onResponse(Call<SessionInitResponse> call, Response<SessionInitResponse> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                SessionInitResponse result = response.body();
                if (result != null) {
                    makeCall(result.getCsrfToken(), id, responseListener);
                }
            }

            @Override
            public void onFailure(Call<SessionInitResponse> call, Throwable t) {
            }
        });
    }

    public void makeCall(String token, String id, OnResponseListener listener) {
        String csrfCookieString = SKApiClient.getCookieString(token);
        SKWebService service = SKApiClient.getClient().create(SKWebService.class);
        Call<ERPlanInfoResponse> apiCall = service.getERPInfo(token, csrfCookieString, SKApiClient.BASE_URL, id);

        apiCall.enqueue(new Callback<ERPlanInfoResponse>() {
            @Override
            public void onResponse(Call<ERPlanInfoResponse> call, Response<ERPlanInfoResponse>response) {
                try {
                    Log.d( "ERPInfo","onResponse Code - " + response.code());
                    if (response.isSuccessful()) {
                        ERPlanInfoResponse result = response.body();
                        listener.onResponse(result);
                    } else {
                        Log.d( "ERPInfo","onResponse Error - " + response.errorBody().string());
                        listener.onFailure();
                    }
                } catch (Exception e) {
                    Log.d( "ERPInfo"," : onResponse Exception - " + e.getLocalizedMessage());
                    listener.onFailure();
                }
            }

            @Override
            public void onFailure(Call<ERPlanInfoResponse> call, Throwable t) {
                Log.d( "ERPInfo"," : onFailure - " + t.getLocalizedMessage());
                listener.onFailure();
            }
        });
    }
}
