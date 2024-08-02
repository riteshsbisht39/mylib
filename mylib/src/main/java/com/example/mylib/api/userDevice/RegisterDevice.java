package com.example.mylib.api.userDevice;

import android.util.Log;

import com.example.mylib.api.SKApiClient;
import com.example.mylib.api.SKWebService;
import com.example.mylib.session.SessionInitResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterDevice {

    public interface OnResponseListener {
        void onResponse(DeviceResponse result);
        void onErrorResponse(ResponseBody errBody);
        void onFailure();
    }

    public void registerDevice(DeviceRequest requestContent, OnResponseListener responseListener) {
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

    public void makeCall(String token, DeviceRequest content, OnResponseListener listener) {
        String csrfCookieString = SKApiClient.getCookieString(token);
        SKWebService service = SKApiClient.getClient().create(SKWebService.class);
        Call<DeviceResponse> apiCall = service.registerDevice(token, csrfCookieString, SKApiClient.BASE_URL, content);

        apiCall.enqueue(new Callback<DeviceResponse>() {
            @Override
            public void onResponse(Call<DeviceResponse> call, Response<DeviceResponse>response) {
                try {
                    Log.d( "Device Register","onResponse Code - " + response.code());
                    if (response.isSuccessful()) {
                        DeviceResponse result = response.body();
                        listener.onResponse(result);
                    } else {
                        Log.d( "Device Register","onResponse Error - " + response.errorBody().string());
                        listener.onFailure();
                    }
                } catch (Exception e) {
                    Log.d( "Device Register"," : onResponse Exception - " + e.getLocalizedMessage());
                    listener.onFailure();
                }
            }

            @Override
            public void onFailure(Call<DeviceResponse> call, Throwable t) {
                Log.d( "Device Register"," : onFailure - " + t.getLocalizedMessage());
                listener.onFailure();
            }
        });
    }
}
