package com.example.mylib.api.plan;

import android.util.Log;

import com.example.mylib.api.SKApiClient;
import com.example.mylib.api.SKWebService;
import com.example.mylib.api.UserLocationRequest;
import com.example.mylib.session.SessionInitResponse;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ERPlans {

    public interface OnResponseListener {
        void onResponse(ArrayList<ERPlansResponse> result);
        void onErrorResponse(ResponseBody errBody);
        void onFailure();
    }

    public void getERPlans(UserLocationRequest requestContent, OnResponseListener responseListener) {
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
                    makeCall(result.getCsrfToken(), requestContent, responseListener);
                }
            }

            @Override
            public void onFailure(Call<SessionInitResponse> call, Throwable t) {
            }
        });
    }

    public void makeCall(String token, UserLocationRequest content, OnResponseListener listener) {
        String csrfCookieString = SKApiClient.getCookieString(token);
        SKWebService service = SKApiClient.getClient().create(SKWebService.class);
        Call<ArrayList<ERPlansResponse>> apiCall = service.getERPans(token, csrfCookieString, SKApiClient.BASE_URL, content);

        apiCall.enqueue(new Callback<ArrayList<ERPlansResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<ERPlansResponse>> call, Response<ArrayList<ERPlansResponse>>response) {
                try {
                    Log.d( "ERPlans","onResponse Code - " + response.code());
                    if (response.isSuccessful()) {
                        ArrayList<ERPlansResponse> result = response.body();
                        listener.onResponse(result);
                    } else {
                        Log.d( "ERPlans","onResponse Error - " + response.errorBody().string());
                        listener.onFailure();
                    }
                } catch (Exception e) {
                    Log.d( "ERPlans"," : onResponse Exception - " + e.getLocalizedMessage());
                    listener.onFailure();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<ERPlansResponse>> call, Throwable t) {
                Log.d( "ERPlans"," : onFailure - " + t.getLocalizedMessage());
                listener.onFailure();
            }
        });
    }
}
