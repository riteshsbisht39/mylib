package com.example.mylib.api.login;

import android.util.Log;

import com.example.mylib.api.SKApiClient;
import com.example.mylib.api.SKWebService;
import com.example.mylib.session.SessionInitResponse;

import okhttp3.Headers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubmitOtp {

    public interface OnResponseListener {
        void onResponse(OtpResponse result);
        void onErrorResponse(ResponseBody errBody);
        void onFailure();
    }

    public void submitOtp(OtpRequest requestContent, OnResponseListener responseListener) {
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

    public void makeCall(String token, OtpRequest content, OnResponseListener listener) {
        String csrfCookieString = SKApiClient.getCookieString(token);
        SKWebService service = SKApiClient.getClient().create(SKWebService.class);
        Call<OtpResponse> apiCall = service.verifyOtp(token, csrfCookieString, SKApiClient.BASE_URL, content);

        apiCall.enqueue(new Callback<OtpResponse>() {
            @Override
            public void onResponse(Call<OtpResponse> call, Response<OtpResponse>response) {
                try {
                    Log.d("Submit Otp", "onResponse Code: " + response.code());
                    if (response.isSuccessful()) {
                        OtpResponse result = response.body();
                        if (result != null) {
                            result.cookie = response.headers().get("Set-Cookie");
                        }
                        listener.onResponse(result);
                    } else {
                        listener.onErrorResponse(response.errorBody());
                    }
                } catch (Exception e) {
                    Log.d("Submit Otp", "onResponse Exception: " + e.getLocalizedMessage());
                    listener.onFailure();
                }
            }

            @Override
            public void onFailure(Call<OtpResponse> call, Throwable t) {
                Log.d("Submit Otp", "onFailure: " + t.getLocalizedMessage());
                listener.onFailure();
            }
        });
    }
}
