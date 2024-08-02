package com.example.mylib.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

public class Datastore {

    private static final String USER_UUID = "user_uuid";
    private static final String SESSION_ID = "session_id";
    private static final String HAS_ORG_CODE = "has_org_code";
    private static final String MOBILE_NUMBER_VERIFIED = "is_mobile_verified";
    private static final String NATIVE_DEVICE_ID = "native_device_id";
    private static final String DEVICE_UUID = "device_uuid";
    private SharedPreferences mSharedPrefs;
    private Context ctx;

    public Datastore(Context ctx) {
        this.ctx = ctx;
        mSharedPrefs = ctx.getSharedPreferences("preferences", Context.MODE_PRIVATE);
    }

    private SharedPreferences.Editor getEditor() {
        return getPrefs().edit();
    }

    private SharedPreferences getPrefs() {
        return mSharedPrefs;
    }

    public String getUuid() {
        return getPrefs().getString(USER_UUID, null);
    }
    public void saveUuid(String newUuid) {
        getEditor().putString(USER_UUID, newUuid).commit();
    }

    public String getSessionId() {
        return getPrefs().getString(SESSION_ID, null);
    }

    public void saveSessionId(String newSessionId) {
        getEditor().putString(SESSION_ID, newSessionId).commit();
    }

    public boolean hasOrgCode() {
        return getPrefs().getBoolean(HAS_ORG_CODE, false);
    }

    public void setHasOrgCode(boolean hasOrgCode) {
        getEditor().putBoolean(HAS_ORG_CODE, hasOrgCode).commit();
    }

    public boolean isMobileNumberVerified() {
        return getPrefs().getBoolean(MOBILE_NUMBER_VERIFIED, false);
    }

    public void saveMobileNumberVerified(boolean status) {
        getEditor().putBoolean(MOBILE_NUMBER_VERIFIED, status).commit();
    }

    public String getNativeDeviceId() {
        return getPrefs().getString(NATIVE_DEVICE_ID, "");
    }

    public void saveNativeDeviceId(String id) {
        getEditor().putString(NATIVE_DEVICE_ID, id).commit();
    }

    public String getDeviceUuid() {
        return getPrefs().getString(DEVICE_UUID, null);
    }

    public void saveDeviceUuid(String id) {
        getEditor().putString(DEVICE_UUID, id).commit();
    }
}
