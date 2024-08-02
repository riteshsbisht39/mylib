package com.example.mylib.api.userDevice;


import android.os.Build;

public class DeviceRequest {

    public String user_uuid;
    public String phone_number = "";
    public String device_type = "Phone";
    public String os_type = "Android";
    public int os_version_major = Build.VERSION.SDK_INT;
    public int os_version_minor = 0;
    public int os_version_point = 0;
    public int app_version_major = 0;
    public int app_version_minor = 0;
    public int app_version_point = 0;
    public boolean push_enabled = true;
    public String push_id = "XXXX_XXXX";
    public boolean is_active = true;
    public String phone_model = "Brnd:" + Build.BRAND + ", Mdl:" + Build.MODEL + ", Dvc:"
            + Build.DEVICE + ", vrsn:" + Build.VERSION.RELEASE;
    public String current_version;
    public String native_device_id;
    public String device_uuid;

//    public DeviceRequest(String user_uuid) {
//        this.user_uuid = user_uuid;
//    }


    public DeviceRequest(String user_uuid, String current_version, String native_device_id ) {
        this.user_uuid = user_uuid;
        this.current_version = current_version;
        this.native_device_id = native_device_id;
    }

    public DeviceRequest(String user_uuid, String push_id, String current_version, String native_device_id) {
        this.user_uuid = user_uuid;
        this.push_id = push_id;
        this.current_version = current_version;
        this.native_device_id = native_device_id;
    }

    public DeviceRequest(String user_uuid, String native_device_id) {
        this.user_uuid = user_uuid;
        this.native_device_id = native_device_id;
    }

    public DeviceRequest(String device_uuid) {
        this.device_uuid = device_uuid;
    }
}
