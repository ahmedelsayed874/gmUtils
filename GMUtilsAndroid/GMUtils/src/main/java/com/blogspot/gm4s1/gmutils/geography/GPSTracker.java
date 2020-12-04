package com.blogspot.gm4s1.gmutils.geography;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AlertDialog;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer with (Java/Kotlin, Swift)
 * Have experience with:
 * - (C/C++, C#) languages
 * - .NET environment
 * - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */
public class GPSTracker implements LocationListener {
    // The minimum distance to change Updates in meters
    public static long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    // The minimum time between updates in milliseconds
    public static long MIN_TIME_BW_UPDATES = 1000;


    private LocationManager locationManager; // Declaring a Location Manager
    private Location location;
    private Listener mListener;

    @RequiresPermission(anyOf = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
    public GPSTracker(Context context, Listener callback) {
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        this.mListener = callback;
    }

    //----------------------------------------------------------------------------------------------

    public Boolean isGPSEnabled() {
        if (locationManager != null) {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        return null;
    }

    //----------------------------------------------------------------------------------------------

    @SuppressLint("MissingPermission")
    public void startLocationUpdating() {
        try {
            // if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled()) {
                stopLocationUpdating();

                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES,
                        this
                );

                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES,
                        this
                );

                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (location == null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }

                if (mListener != null) {
                    mListener.onLocationFounded(this, location);
                }

            } else {
                if (mListener != null) {
                    mListener.onErrorHappened(this, Listener.ERR_GPS_CLOSED, "");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (mListener != null) {
                mListener.onErrorHappened(this, Listener.ERR_EXCEPTION, e.getMessage());
            }
        }
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */
    @SuppressLint("MissingPermission")
    public void stopLocationUpdating() {
        if (locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    //----------------------------------------------------------------------------------------------

    public Location getLocation() {
        return location;
    }

    //----------------------------------------------------------------------------------------------

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will launch Settings Options
     */
    public void showGPSDisabledAlert(Context context) {
        showGPSDisabledAlert(
                context,
                "GPS",
                "Enable GPS to complete",
                "Location Settings"
        );
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will launch Settings Options
     */
    public void showGPSDisabledAlert(Context context, String title, String message, String buttonTitle) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(buttonTitle, (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(intent);

                    new Handler(Looper.getMainLooper())
                            .postDelayed(
                                    this::startLocationUpdating,
                                    20_000
                            );
                })
                .show();
    }

    //----------------------------------------------------------------------------------------------

    public void destroy() {
        stopLocationUpdating();
        mListener = null;
        locationManager = null;
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            this.location = location;

            if (mListener != null) {
                mListener.onLocationFounded(this, location);
            }
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        if (mListener != null)
            mListener.onLocationProviderStatusChanged(this, provider, true);
    }

    @Override
    public void onProviderEnabled(String provider) {
        if (mListener != null)
            mListener.onLocationProviderStatusChanged(this, provider, false);
    }

    @Deprecated
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    //----------------------------------------------------------------------------------------------

    public interface Listener {
        int ERR_GPS_CLOSED = 1;
        int ERR_EXCEPTION = 2;

        void onLocationFounded(GPSTracker obj, Location location);

        /**
         * @param provider {@link LocationManager#GPS_PROVIDER} or {@link LocationManager#NETWORK_PROVIDER}
         */
        void onLocationProviderStatusChanged(GPSTracker obj, String provider, boolean disabled);

        /**
         * @param errorCode {@link #ERR_GPS_CLOSED} or {@link #ERR_EXCEPTION}
         */
        void onErrorHappened(GPSTracker obj, int errorCode, String msg);
    }
}
