package com.example.mylib.api.event;

import android.util.Log;

import com.example.mylib.api.SKApiClient;
import com.example.mylib.api.SKWebService;
import com.example.mylib.session.SessionInitResponse;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StopStream {

    public interface OnResponseListener {
        void onResponse();
        void onErrorResponse(ResponseBody errBody);
        void onFailure();
    }

    public void stopEventStream(String incidentUuid, OnResponseListener responseListener) {
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
                    makeCall(result.getCsrfToken(), incidentUuid, responseListener);
                }
            }

            @Override
            public void onFailure(Call<SessionInitResponse> call, Throwable t) {
            }
        });
    }

    public void makeCall(String token, String incidentUuid, OnResponseListener listener) {
        String csrfCookieString = SKApiClient.getCookieString(token);
        SKWebService service = SKApiClient.getClient().create(SKWebService.class);
        Call<Void> apiCall = service.stopEventStream(token, csrfCookieString, SKApiClient.BASE_URL, incidentUuid);

        apiCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void>response) {
                try {
                    Log.d( "StopStream","onResponse Code - " + response.code());
                    if (response.isSuccessful()) {
                        listener.onResponse();
                    } else {
                        Log.d( "StopStream","onResponse Error - " + response.errorBody().string());
                        listener.onFailure();
                    }
                } catch (Exception e) {
                    Log.d( "StopStream"," : onResponse Exception - " + e.getLocalizedMessage());
                    listener.onFailure();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d( "StopStream"," : onFailure - " + t.getLocalizedMessage());
                listener.onFailure();
            }
        });
    }
}
