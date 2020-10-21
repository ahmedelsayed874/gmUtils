package com.blogspot.gm4s1.gmutils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer with (Java/Kotlin, Swift)
 * Have experience with:
 *      - (C/C++, C#) languages
 *      - .NET environment
 *      - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */
public class GPSTracker implements LocationListener {
    // The minimum distance to change Updates in meters
    public static long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; //10; // 10 meters
    // The minimum time between updates in milliseconds
    public static long MIN_TIME_BW_UPDATES = 1000;//1000 * 60; // 1 minute

    private LocationManager locationManager; // Declaring a Location Manager
    private Location location;
    private Listener mListener;

    @RequiresPermission(anyOf = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
    public GPSTracker(Listener callback) {
        this.mListener = callback;

        //findLocation();
    }

    @SuppressLint("MissingPermission")
    public Location findLocation(Activity activity) {
        try {
            locationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);

            // getting GPS status
            // flag for GPS status
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled) {
                if (requestLocationFinder(LocationManager.NETWORK_PROVIDER) == null) {
                    requestLocationFinder(LocationManager.GPS_PROVIDER);
                }

            } else {
                if (mListener != null && !mListener.onErrorHappened(this, Listener.ERR_GPS_CLOSED)) {
                    showSettingsAlert(activity);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    @SuppressLint("MissingPermission")
    private Location requestLocationFinder(String provider) {
        if (locationManager != null) {
            location = locationManager.getLastKnownLocation(provider);

            if (location == null) {
                locationManager.requestLocationUpdates(
                        provider,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES,
                        this
                );
            } else {
                if (mListener != null) {
                    mListener.onLocationFounded(this, location);
                }
            }
        }

        return location;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will launch Settings Options
     */
    private void showSettingsAlert(Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle("GPS")
                .setMessage("Enable GPS to complete")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = createShowLocationSettingsIntent();
                        activity.startActivity(intent);

                        new Handler(Looper.getMainLooper())
                                .postDelayed(
                                        () -> findLocation(activity),
                                        20_000
                                );
                    }
                })
                .show();
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */
    @SuppressLint("MissingPermission")
    private void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    //--------------------------------------------------------------------------------------------//

    public Intent createShowLocationSettingsIntent() {
        return new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
    }

    public Location get_Location() {
        return location;

    }

    /**
     * Function to get latitude
     */
    public double getLatitude() {
        if (location != null) {
            return location.getLatitude();
        }

        // return latitude
        return 0;
    }

    /**
     * Function to get longitude
     */
    public double getLongitude() {
        if (location != null) {
            return location.getLongitude();
        }

        // return longitude
        return 0;
    }


    //--------------------------------------------------------------------------------------------//

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            this.location = location;

            if (mListener != null) {
                mListener.onLocationFounded(this, location);
            }

            stopUsingGPS();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }


    //--------------------------------------------------------------------------------------------//


    public interface Listener {
        int ERR_GPS_CLOSED = 1;

        void onLocationFounded(GPSTracker obj, Location location);

        /**
         *
         * @param errorCode
         * @return handled
         */
        boolean onErrorHappened(GPSTracker obj, int errorCode);
    }
}
