package com.digitalcranberry.gainsl;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by yo on 01/04/15.
 */
public class GeoLocator {
    private LocationManager locationManager;
    private LocationListener locationListener;

    public void startListening(Context mContext) {
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    public Location getCurrentLocation() {
        //google suggests using network locations for better performance; we assume we don't have this available.
        String locationProvider = LocationManager.GPS_PROVIDER;
        return locationManager.getLastKnownLocation(locationProvider);
    }

    public void stopListening() {
        locationManager.removeUpdates(locationListener);
    }
}
