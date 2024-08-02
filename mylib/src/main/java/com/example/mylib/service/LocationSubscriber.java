package com.example.mylib.service;

import android.location.Location;

public abstract class LocationSubscriber {
    public abstract void onReceiveLocation(Location location);
    public void onStop() {}
}
