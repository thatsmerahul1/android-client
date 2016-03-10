package com.ecarezone.android.patient.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by L&T Technology Services  on 3/1/2016.
 */
public class LocationFinder extends Service implements LocationListener {
    private final Context mContext;

    // flag for GPS Status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS Tracking is enabled
    boolean isGPSTrackingEnabled = false;

    Location location;
    double latitude = -1;
    double longitude = -1;

    // Declaring a Location Manager
    protected LocationManager locationManager;

    // Store LocationManager.GPS_PROVIDER or LocationManager.NETWORK_PROVIDER information
    private String provider_info;

    public LocationFinder(Context mContext) {
        this.mContext = mContext;
        findLocationCordinates();
    }

    private void findLocationCordinates() {
        locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        if (locationManager == null) {
            return;
        }
        //getting GPS status
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        //getting network status
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        // Try to get location if you GPS Service is enabled
        if (isGPSEnabled) {
            this.isGPSTrackingEnabled = true;
            provider_info = LocationManager.GPS_PROVIDER;
        } else if (isNetworkEnabled) { // Try to get location if you Network Service is enabled
            this.isGPSTrackingEnabled = true;
            provider_info = LocationManager.NETWORK_PROVIDER;
        }
        try {
            if (locationManager != null && provider_info != null) {

                location = locationManager.getLastKnownLocation(provider_info);
                updateGPSCoordinates();
            } else {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateGPSCoordinates() {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    /**
     * GPSTracker latitude getter and setter
     *
     * @return latitude
     */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        return latitude;
    }

    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        return longitude;
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
