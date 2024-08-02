package com.example.mylib.api.event;

import android.util.Log;

import com.example.mylib.api.SKApiClient;
import com.example.mylib.api.SKWebService;
import com.example.mylib.session.SessionInitResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EndEvent {

    public interface OnResponseListener {
        void onResponse(IncidentResponse result);
        void onErrorResponse(ResponseBody errBody);
        void onFailure();
    }

    public void endEventWithStatus(String incidentId, IncidentEndStatus requestContent, OnResponseListener responseListener) {
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
                    makeCall(result.getCsrfToken(), incidentId, requestContent, responseListener);
                }
            }

            @Override
            public void onFailure(Call<SessionInitResponse> call, Throwable t) {
            }
        });
    }

    public void makeCall(String token, String incidentId, IncidentEndStatus content, OnResponseListener listener) {
        String csrfCookieString = SKApiClient.getCookieString(token);
        SKWebService service = SKApiClient.getClient().create(SKWebService.class);
        Call<IncidentResponse> apiCall = service.endEvent(token, csrfCookieString, SKApiClient.BASE_URL, content, incidentId);

        apiCall.enqueue(new Callback<IncidentResponse>() {
            @Override
            public void onResponse(Call<IncidentResponse> call, Response<IncidentResponse>response) {
                try {
                    Log.d("EndEvent", "onResponse Code - " + response.code());
                    if (response.isSuccessful()) {
                        IncidentResponse result = response.body();
                        listener.onResponse(result);
                    } else {
                        Log.d( "EndEvent","onResponse Error - " + response.errorBody().string());
                        listener.onFailure();
                    }
                } catch (Exception e) {
                    Log.d( "EndEvent"," : onResponse Exception - " + e.getLocalizedMessage());
                    listener.onFailure();
                }
            }

            @Override
            public void onFailure(Call<IncidentResponse> call, Throwable t) {
                Log.d( "EndEvent"," : onFailure - " + t.getLocalizedMessage());
                listener.onFailure();
            }
        });
    }
}
