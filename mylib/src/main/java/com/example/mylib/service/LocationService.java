package com.example.mylib.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.mylib.KuvrrSDK;
import com.example.mylib.prefs.Datastore;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

public final class LocationService extends Service {
    private static final String TAG = "LocationService";
    private static final int ONE_SECOND = 1000;
    private static final int UPDATE_FREQUENCY = ONE_SECOND * 10; // 10 seconds
    private static final float MIN_DISPLACEMENT_METERS = 0; // No minimum
    private final Object LOCK = new Object();

    private FusedLocationProviderClient mFusedLocationClient;
    private Location currentLocation;
    private LocationServiceBinder mBinder;
    private List<LocationSubscriber> mSubscribers = new LinkedList<>();
    public static boolean oneTimeLoc = false;
    public static String title = "";
    public static String text = "";
    public static int orgId = 0;
    public static String messageId = "";
    private Datastore mDatastore;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        mBinder = new LocationServiceBinder();
        mDatastore = new Datastore(getApplicationContext());
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Request location updates. If permissions are not available, stop service
        if (!startTracking()) {
            Log.i(TAG, "Permissions unavailable to request location updates. Stopping service.");
            stopSelf();
        }
    }

    private boolean startTracking() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest
                .setInterval(UPDATE_FREQUENCY)                          // Request an update every x seconds
                .setFastestInterval(UPDATE_FREQUENCY)                   // Don't process an update more than once every x seconds
                .setSmallestDisplacement(MIN_DISPLACEMENT_METERS)       // Minimum distance - in meters - between location updates
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        Log.i(TAG, "Requesting new location updates (" + UPDATE_FREQUENCY + " ms, " + MIN_DISPLACEMENT_METERS + " meters)");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, null);
        return true;
    }

    @Override
    public void onDestroy() {
        try {
            Log.d(TAG, "onDestroy: ");
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            stop();
        } catch (Exception e) {
            Log.e(TAG, "Error in onDestroy", e);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY_COMPATIBILITY;
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            /*for (Location location : locationResult.getLocations()) {
                updateWithLocation(location);
            }*/
            updateWithLocation(locationResult.getLastLocation());
        };
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void updateWithLocation(Location newLocation) {
        if (currentLocation == null) {
            Log.d(TAG, "Location initialized");
        } else {
            Log.d(TAG, "Location updated");
            Log.d(TAG, "updateWithLocation: oneTimeLoc " + oneTimeLoc);
        }
        currentLocation = newLocation;
        Log.d(TAG, "updateWithLocation: getLatitude " + currentLocation.getLatitude());
        KuvrrSDK.setCurrentLocation(currentLocation);
        postLocationToSubscribers(currentLocation);
    }

    private void postLocationToSubscribers(Location location) {
        synchronized (LOCK) {
            for (LocationSubscriber subscriber : mSubscribers) {
                try {
                    subscriber.onReceiveLocation(location);
                }catch (Exception e) {
                    Log.e(TAG, "Error in postLocationToSubscribers!", e);
                }
            }
        }
    }

    private void subscribe(LocationSubscriber locationSubscriber) {
        synchronized (LOCK) {
            mSubscribers.add(locationSubscriber);
        }
    }
    private void unsubscribe(LocationSubscriber locationSubscriber) {
        synchronized (LOCK) {
            mSubscribers.remove(locationSubscriber);
        }
    }

    private void stop() {
        synchronized (LOCK) {
            for (LocationSubscriber subscriber : mSubscribers) {
                try {
                    subscriber.onStop();
                }catch (Exception e) {
                    Log.e(TAG, "Error in onStop!", e);
                }
            }
            mSubscribers.clear();
        }
    }

    /* Allows communication from activities to service */
    public class LocationServiceBinder extends Binder {
        private LocationSubscriber mSubscriber;

        @Override
        public void finalize() throws Throwable {
            super.finalize();
            unsubscribe();
        }

        public LocationService getService() {
            return LocationService.this;
        }

        public void subscribe(LocationSubscriber locationSubscriber) throws IllegalArgumentException {
            if (locationSubscriber == null) {
                throw new IllegalArgumentException("locationSubscriber cannot be null.");
            }

            mSubscriber = locationSubscriber;
            LocationService.this.subscribe(mSubscriber);
        }

        public void unsubscribe() {
            if (mSubscriber != null) {
                LocationService.this.unsubscribe(mSubscriber);
                mSubscriber = null;
            }
        }
    }
}
