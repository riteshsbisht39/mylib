package com.example.mylib.api.plan;

import android.util.Log;

import com.example.mylib.api.SKApiClient;
import com.example.mylib.api.SKWebService;
import com.example.mylib.session.SessionInitResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ERPInfoUpdate {

    public interface OnResponseListener {
        void onResponse(ERPlansInfoUpdateResponse result);
        void onErrorResponse(ResponseBody errBody);
        void onFailure();
    }

    public void updateERPInfo(String id, ERPlanInfoVersion requestContent, OnResponseListener responseListener) {
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
                    makeCall(result.getCsrfToken(), id, requestContent, responseListener);
                }
            }

            @Override
            public void onFailure(Call<SessionInitResponse> call, Throwable t) {
            }
        });
    }

    public void makeCall(String token, String id, ERPlanInfoVersion content, OnResponseListener listener) {
        String csrfCookieString = SKApiClient.getCookieString(token);
        SKWebService service = SKApiClient.getClient().create(SKWebService.class);
        Call<ERPlansInfoUpdateResponse> apiCall = service.updateERPInfo(token, csrfCookieString, SKApiClient.BASE_URL, content, id);

        apiCall.enqueue(new Callback<ERPlansInfoUpdateResponse>() {
            @Override
            public void onResponse(Call<ERPlansInfoUpdateResponse> call, Response<ERPlansInfoUpdateResponse>response) {
                try {
                    Log.d( "ERPInfoUpdate","onResponse Code - " + response.code());
                    if (response.isSuccessful()) {
                        ERPlansInfoUpdateResponse result = response.body();
                        listener.onResponse(result);
                    } else {
                        listener.onErrorResponse(response.errorBody());
                    }
                } catch (Exception e) {
                    Log.d( "ERPInfoUpdate"," : onResponse Exception - " + e.getLocalizedMessage());
                    listener.onFailure();
                }
            }

            @Override
            public void onFailure(Call<ERPlansInfoUpdateResponse> call, Throwable t) {
                Log.d( "ERPInfoUpdate"," : onFailure - " + t.getLocalizedMessage());
                listener.onFailure();
            }
        });
    }
}
