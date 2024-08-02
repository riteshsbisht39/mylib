package com.example.mylib.api.login;

import com.google.gson.annotations.SerializedName;

public class OtpResponse {

    @SerializedName("user_uuid")
    private String userUuid;

    @SerializedName("cookie")
    public String cookie;

    @SerializedName("has_org")
    private Boolean isOrg;

    @SerializedName("phone_info")
    private Phone phone;

    @SerializedName("ble_permission")
    private Boolean defaultBlePerm;

    public class Phone {

        private Boolean verified;
        private String message;
        private String phone;

        @SerializedName("country_code")
        private String countryCode;

        public Boolean getVerified() {
            return verified;
        }

        public String getMessage() {
            return message;
        }

        public String getPhone() {
            return phone;
        }

        public String getCountryCode() {
            return countryCode;
        }
    }

    public String getUserUuid() {
        return userUuid;
    }

    public String getCookie() {
        return cookie;
    }

    public Boolean isOrg() {
        return isOrg;
    }

    public Phone getPhone() {
        return phone;
    }

    public Boolean getDefaultBlePerm() {
        return defaultBlePerm;
    }
}
