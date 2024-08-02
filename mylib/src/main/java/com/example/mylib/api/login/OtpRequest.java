package com.example.mylib.api.login;

import java.io.Serializable;

public class OtpRequest implements Serializable {

    private String email;
    private String otp;

    public OtpRequest(String email, String otp) {
        this.email = email;
        this.otp = otp;
    }
}
