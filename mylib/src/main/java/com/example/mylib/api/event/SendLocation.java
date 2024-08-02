package com.example.mylib.api.event;

import android.util.Log;

import com.example.mylib.api.SKApiClient;
import com.example.mylib.api.SKWebService;
import com.example.mylib.session.SessionInitResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendLocation {

    public void sendLocation(LocationRequest requestContent) {
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
                    makeCall(result.getCsrfToken(), requestContent);
                }
            }

            @Override
            public void onFailure(Call<SessionInitResponse> call, Throwable t) {
            }
        });
    }

    public void makeCall(String token, LocationRequest content) {
        String csrfCookieString = SKApiClient.getCookieString(token);
        SKWebService service = SKApiClient.getClient().create(SKWebService.class);
        Call<Void> apiCall = service.sendLocation(token, csrfCookieString, SKApiClient.BASE_URL, content);

        apiCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void>response) {
                try {
                    Log.d( "SendLocation","onResponse Code - " + response.code());
                    if (response.isSuccessful()) {
                        Log.d( "SendLocation","onSuccess - ");
                    } else {
                        Log.d( "SendLocation","onResponse Error - " + response.errorBody().string());
                    }
                } catch (Exception e) {
                    Log.d( "SendLocation"," : onResponse Exception - " + e.getLocalizedMessage());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d( "SendLocation"," : onFailure - " + t.getLocalizedMessage());
            }
        });
    }
}
