package com.example.shiv.locationcoverage;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by shivu on 3/11/2015.
 */
public class LocationListenerTask implements LocationListener {

    private static final String TAG = LocationListenerTask.class.getSimpleName();

    public void onLocationChanged(Location location) {
        // Called when a new location is found by the network location provider.
        Log.d(TAG, "Lattitude" + location.getLatitude());
        Log.d(TAG, "Longitude" + location.getLongitude());
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {}

    public void onProviderEnabled(String provider) {}

    public void onProviderDisabled(String provider) {}
}
