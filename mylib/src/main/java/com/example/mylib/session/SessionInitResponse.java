package com.example.mylib.session;

import com.google.gson.annotations.SerializedName;

public class SessionInitResponse {

    @SerializedName("csrf_token")
    private String csrfToken;

    private String message;

    public String getCsrfToken() {
        return csrfToken;
    }

    public String getMessage() {
        return message;
    }
}
