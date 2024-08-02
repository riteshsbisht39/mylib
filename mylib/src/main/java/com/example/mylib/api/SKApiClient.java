package com.example.mylib.api;

import android.util.Log;

import com.example.mylib.KuvrrSDK;
import com.example.mylib.prefs.Datastore;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SKApiClient {

    public static String BASE_URL = "https://safety-red5.kuvrr.com/api/v1/";
    public static String CSRF_COOKIE = "csrftoken";

    private static Retrofit retrofit = null;
    private static Retrofit retrofitBaseUrl = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);
            httpClient.readTimeout(60, TimeUnit.SECONDS);
            httpClient.connectTimeout(60, TimeUnit.SECONDS);

            retrofit = new Retrofit.Builder()
                    .baseUrl("https://safety-red5.kuvrr.com/api/v1/")
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getBaseUrlClient() {
        if (retrofitBaseUrl == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);

            retrofitBaseUrl = new Retrofit.Builder()
                    .baseUrl("https://safety.kuvrr.com/api/v1/")
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitBaseUrl;
    }

    public static String getCookieString(String token) {
        Datastore mDatastore = new Datastore(KuvrrSDK.context);
        String csrfCookieString = CSRF_COOKIE + "=" + token + ";";
        if (mDatastore.getSessionId() != null) {
            csrfCookieString = csrfCookieString + mDatastore.getSessionId();
        }
        return csrfCookieString;
    }
}
