package com.example.mylib;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.mylib.api.event.IncidentRequest;
import com.example.mylib.api.event.IncidentResponse;
import com.example.mylib.api.event.SendEvent;
import com.example.mylib.api.login.OtpRequest;
import com.example.mylib.api.login.OtpResponse;
import com.example.mylib.api.login.SubmitOtp;
import com.example.mylib.api.userDevice.DeviceRequest;
import com.example.mylib.api.userDevice.DeviceResponse;
import com.example.mylib.api.userDevice.RegisterDevice;
import com.example.mylib.prefs.Datastore;
import com.example.mylib.service.LocationService;
import com.example.mylib.ui.HomeActivity;
import com.example.mylib.ui.map.MapsActivity;
import com.example.mylib.ui.plan.PlansActivity;

import okhttp3.ResponseBody;

public class KuvrrSDK {
    public static Context context;
    public static Activity activity;
    private static Location mCurrentLocation = null;

    public static void showToast(String string) {
        Toast mytoast = Toast.makeText(context, string, Toast.LENGTH_LONG);
        mytoast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        mytoast.show();
    }

    public static void startDial911(Context c, Activity a, String email, String otp) {
        context = c;
        activity = a;
        Datastore mDatastore = new Datastore(context);

        handleLocationPerm();

        if (mDatastore.getUuid() != null) {
            ToastMessage.toastMessage(context, "User Already Logged In");
            sendEvent(mDatastore);
        } else {
            login(mDatastore, email, otp);
        }
    }

    public static void startSos(Context c, Activity a, String email, String otp) {
        context = c;
        activity = a;
        Datastore mDatastore = new Datastore(context);

        handleLocationPerm();
        handleCamPerm();
        handleMicPerm();

        if (mDatastore.getUuid() != null) {
            ToastMessage.toastMessage(context, "User Already Logged In");
            startSosEvent();
        } else {
            login(mDatastore, email, otp);
        }
    }

    private static void login(Datastore ds, String email, String otp) {
        OtpRequest requestContent = new OtpRequest(email, otp);
        SubmitOtp request = new SubmitOtp();
        request.submitOtp(requestContent, new SubmitOtp.OnResponseListener() {
            @Override
            public void onResponse(OtpResponse result) {
                ToastMessage.toastMessage(context, "User Login Success");
                ds.saveUuid(result.getUserUuid());
                ds.saveSessionId(result.getCookie());
                ds.setHasOrgCode(result.isOrg());
                ds.saveMobileNumberVerified(result.getPhone().getVerified());
                sendDeviceDetail(ds);
            }

            @Override
            public void onErrorResponse(ResponseBody errBody) {

            }

            @Override
            public void onFailure() {

            }
        });
    }

    private static void sendDeviceDetail(Datastore ds) {
        String nativeDeviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        ds.saveNativeDeviceId(nativeDeviceId);

        DeviceRequest requestContent = new DeviceRequest(ds.getUuid(), getAppVersionName(),
                nativeDeviceId);

        RegisterDevice request = new RegisterDevice();
        request.registerDevice(requestContent, new RegisterDevice.OnResponseListener() {
            @Override
            public void onResponse(DeviceResponse result) {
                ToastMessage.toastMessage(context, "User device Register Success");
                ds.saveDeviceUuid(result.uuid);
                sendEvent(ds);
            }

            @Override
            public void onErrorResponse(ResponseBody errBody) {

            }

            @Override
            public void onFailure() {

            }
        });

    }

    public static String getAppVersionName() {
        String versionName = "";
        try {
            versionName = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    private static void sendEvent(Datastore ds) {
        IncidentRequest incidentRequest = new IncidentRequest(ds.getDeviceUuid());

        Location currentLocation = getCurrentLocation();
        if (currentLocation != null) {
            incidentRequest.lat = (float) currentLocation.getLatitude();
            incidentRequest.lng = (float) currentLocation.getLongitude();
            incidentRequest.horizontal_accuracy = currentLocation.getAccuracy();
        } else { //currentLocation may be null if the device has never acquired a location. give fake values.
            incidentRequest.lat = 1;
            incidentRequest.lng = 1;
            incidentRequest.horizontal_accuracy = 100;
        }

//        incidentRequest.lat = (float) 28.6361364;
//        incidentRequest.lng = (float) 77.36056;
//        incidentRequest.horizontal_accuracy = 100;

        incidentRequest.responder_type = "911";

        incidentRequest.pb_trigger = false;

        incidentRequest.app_location_only = false;
        incidentRequest.ems = true;
        incidentRequest.media_type = "";

        SendEvent request = new SendEvent();
        request.sendEvent(incidentRequest, new SendEvent.OnResponseListener() {
            @Override
            public void onResponse(IncidentResponse result) {
                ToastMessage.toastMessage(context, "Sent Dial 911 Event  Success");
                call911();
            }

            @Override
            public void onErrorResponse(ResponseBody errBody) {

            }

            @Override
            public void onFailure() {

            }
        });
    }

    private static void call911() {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("tel:" + "911"));
            context.startActivity(intent);
        } catch (Exception e) {
            Log.d("call911", e.getLocalizedMessage());
        }
    }

    private static void startSosEvent() {
        Intent i = new Intent(context, HomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    public static Location getCurrentLocation() {
        return mCurrentLocation;
    }
    public static void setCurrentLocation(Location location) {
        mCurrentLocation = location;
    }

    private static void handleLocationPerm() {
        if (ContextCompat.checkSelfPermission( context, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, 200);
        } else {
            Intent locationService = new Intent(context, LocationService.class);
            context.startService(locationService);
        }
    }

    private static void handleCamPerm() {
        if (ContextCompat.checkSelfPermission( context, CAMERA) != PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{CAMERA}, 300);
        } else {

        }
    }

    private static void handleMicPerm() {
        if (ContextCompat.checkSelfPermission( context, RECORD_AUDIO) != PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{RECORD_AUDIO}, 400);
        } else {

        }
    }

    public static void openPlans(Context c, Activity a) {
        context = c;
        activity = a;
        PlansActivity.start(context);
    }

    public static void openMaps(Context c, Activity a) {
        context = c;
        activity = a;
        MapsActivity.start(context);
    }
}
