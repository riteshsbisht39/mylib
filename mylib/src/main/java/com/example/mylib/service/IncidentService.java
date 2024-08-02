package com.example.mylib.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.mylib.R;
import com.example.mylib.api.event.LocationRequest;
import com.example.mylib.api.event.SendLocation;
import com.example.mylib.prefs.Datastore;
import com.example.mylib.ui.HomeActivity;
import com.example.mylib.utils.NotificationUtils;

import java.util.Date;

public final class IncidentService extends Service {
    public static final String TAG = "IncidentService";
    public static final int SERVICE_ID = 10011;

    private static IncidentService sInstance = null;

    String sessionId;
    String incidentUuid;
    IncidentServiceBinder mBinder;
    NotificationCompat.Builder mNotificationBuilder;

    ServiceConnection mLocationServiceConn;
    LocationService.LocationServiceBinder mLocationServiceBinder;

    Datastore mDatastore;

    @Override
    public void onCreate() {
        super.onCreate();
        if (sInstance == null) {
            sInstance = this;
        }
        mDatastore = new Datastore(getApplicationContext());
        mBinder = new IncidentServiceBinder();

        Intent locationServiceIntent = new Intent(this, LocationService.class);
        startService(locationServiceIntent);
        mLocationServiceConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.d(TAG, "LocationService connected");
                mLocationServiceBinder = (LocationService.LocationServiceBinder) iBinder;
                mLocationServiceBinder.subscribe(mLocationSubscriber);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.d(TAG, "LocationService disconnected");
                mLocationServiceBinder = null;
            }
        };
        bindService(locationServiceIntent, mLocationServiceConn, Context.BIND_AUTO_CREATE);

        PendingIntent pi;
        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pi = PendingIntent.getActivity(getApplicationContext(), 0, i, PendingIntent.FLAG_IMMUTABLE | 0);
        } else {
            pi = PendingIntent.getActivity(getApplicationContext(), 0, i, 0);
        }

        mNotificationBuilder = new NotificationCompat.Builder(getApplicationContext(), new NotificationUtils(getApplicationContext()).createActiveIncidentChannel())
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(Color.argb(0,34, 186, 255))
                .setContentTitle(getString(R.string.notification_title))
                .setOngoing(true);

//        if (mDatastore.isLocationOnly()) {
//            mNotificationBuilder.setContentIntent(pi);
//        }

        updateNotificationText("Safety Kuvrr is monitoring");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            stopForeground(true);

            if (mLocationServiceBinder != null) {
                mLocationServiceBinder.unsubscribe();
            }
            if (mLocationServiceConn != null) {
                unbindService(mLocationServiceConn);
            }
            mLocationServiceConn = null;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        sInstance = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY_COMPATIBILITY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public static IncidentService getInstance() {
        return sInstance;
    }

    private LocationSubscriber mLocationSubscriber = new LocationSubscriber() {
        @Override
        public void onReceiveLocation(Location location) {
            if (location != null) {
                postLocation(location);
            }
        }
    };

    private void postLocation(Location currentLocation) {

//        if (mDatastore.isLocationOnly()) {
//            sessionId = mDatastore.getSessionId();
//            incidentUuid = mDatastore.getIncidentUuid();
//        }
        Log.w(TAG, "Attempting to post location");
        if (sessionId == null)
            return;
        if (incidentUuid != null) {
            Log.d(TAG, "postLocation: incidentUuid SendLocation" + incidentUuid);
            LocationRequest locationRequest = new LocationRequest(incidentUuid,
                    (float) currentLocation.getLatitude(), (float) currentLocation.getLongitude());
            locationRequest.location_age = currentLocation.getTime() - new Date().getTime();
            locationRequest.altitude = currentLocation.getAltitude();
            locationRequest.direction_degrees = currentLocation.getBearing();
            locationRequest.horizontal_accuracy = currentLocation.getAccuracy();

            SendLocation request = new SendLocation();
            request.sendLocation(locationRequest);
        }
    }

    public class IncidentServiceBinder extends Binder {
        public IncidentService getService() {
            return IncidentService.this;
        }

        public void clearIncidentUuid() {
            IncidentService.this.incidentUuid = null;
        }

        public void startIncident(String sessionId, String Uuid) {
            IncidentService.this.sessionId = sessionId;
            IncidentService.this.incidentUuid = Uuid;
            updateNotificationText(getString(R.string.notification_incident_running));
        }
    }

    private void updateNotificationText(String text) {
        mNotificationBuilder.setContentText(text);
        startForeground(SERVICE_ID, mNotificationBuilder.build());
    }
}
