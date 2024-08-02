package com.example.mylib.api.map;

import android.util.Log;

import com.example.mylib.api.SKApiClient;
import com.example.mylib.api.SKWebService;
import com.example.mylib.api.UserLocationRequest;
import com.example.mylib.session.SessionInitResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Maps {

    public interface OnResponseListener {
        void onResponse(MapsListResponse result);
        void onErrorResponse(ResponseBody errBody);
        void onFailure();
    }

    public void getMaps(UserLocationRequest requestContent, OnResponseListener responseListener) {
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
        Call<MapsListResponse> apiCall = service.getMaps(token, csrfCookieString, SKApiClient.BASE_URL, content);

        apiCall.enqueue(new Callback<MapsListResponse>() {
            @Override
            public void onResponse(Call<MapsListResponse> call, Response<MapsListResponse>response) {
                try {
                    Log.d( "Maps","onResponse Code - " + response.code());
                    if (response.isSuccessful()) {
                        MapsListResponse result = response.body();
                        listener.onResponse(result);
                    } else {
                        Log.d( "Maps","onResponse Error - " + response.errorBody().string());
                        listener.onFailure();
                    }
                } catch (Exception e) {
                    Log.d( "Maps"," : onResponse Exception - " + e.getLocalizedMessage());
                    listener.onFailure();
                }
            }

            @Override
            public void onFailure(Call<MapsListResponse> call, Throwable t) {
                Log.d( "Maps"," : onFailure - " + t.getLocalizedMessage());
                listener.onFailure();
            }
        });
    }
}
