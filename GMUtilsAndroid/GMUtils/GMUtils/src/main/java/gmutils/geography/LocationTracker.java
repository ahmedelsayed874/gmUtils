package gmutils.geography;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.Context.LOCATION_SERVICE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.fragment.app.Fragment;

import java.util.List;

import gmutils.ui.dialogs.MessageDialog;
import gmutils.utils.UIUtils;

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer (Java/Kotlin, Swift) also Flutter (Dart)
 * Have precedent experience with:
 * - (C/C++, C#) languages
 * - .NET environment
 * - Java swing
 * - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */
public class LocationTracker implements LocationListener {
    // The minimum distance to change Updates in meters
    public static long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    // The minimum time between updates in milliseconds
    public static long MIN_TIME_BW_UPDATES = 1000;

    private LocationManager locationManager; // Declaring a Location Manager
    private Location location;
    private Listener mListener;


    @RequiresPermission(anyOf = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
    public LocationTracker(Fragment fragment, Listener callback) {
        this(fragment.getContext(), callback);
        UIUtils.createInstance().addOnFragmentDestroyedObserver(fragment, this::destroy);
    }

    @RequiresPermission(anyOf = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
    public LocationTracker(Activity activity, Listener callback) {
        this((Context) activity, callback);
        UIUtils.createInstance().addOnActivityDestroyed(activity, this::destroy);
    }

    @RequiresPermission(anyOf = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
    public LocationTracker(Context context, Listener callback) {
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

                if (mListener != null && location != null) {
                    mListener.onLocationFounded(this, location);
                }

            } else {
                if (mListener != null) {
                    mListener.onLocationProviderStatusChanged(this, LocationManager.GPS_PROVIDER, true);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (mListener != null) {
                mListener.onErrorOccurred(this, e.getMessage());
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
            locationManager.removeUpdates(LocationTracker.this);
        }
    }

    //----------------------------------------------------------------------------------------------

    public Location getLocation() {
        return location;
    }

    public Address getLocationDescription(Context context) {
        Geocoder geocoder = new Geocoder(context);

        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            return addresses.get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
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
                "Location Settings",
                "Cancel",
                null
        );
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will launch Settings Options
     */
    public void showGPSDisabledAlert(Context context, String title, String message, String mainButtonTitle, String cancelButtonTitle, Runnable cancel) {
        MessageDialog.create(context)
                .setMessage(message)
                .setTitle(title)
                .setButton1(mainButtonTitle, () -> {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(intent);

                    new Handler(Looper.getMainLooper())
                            .postDelayed(
                                    LocationTracker.this::startLocationUpdating,
                                    10_000
                            );
                })
                .setButton2(cancelButtonTitle, () -> {
                    if (cancel != null) cancel.run();
                })
                .setCancelable(false)
                .show();
    }
    //----------------------------------------------------------------------------------------------

    public void destroy() {
        stopLocationUpdating();
        mListener = null;
        locationManager = null;
    }

    public boolean isDestroyed() {
        return locationManager == null;
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

        void onLocationFounded(LocationTracker obj, @NonNull Location location);

        /**
         * @param provider {@link LocationManager#GPS_PROVIDER} or {@link LocationManager#NETWORK_PROVIDER}
         */
        void onLocationProviderStatusChanged(LocationTracker obj, String provider, boolean disabled);

        void onErrorOccurred(LocationTracker obj, String error);
    }
}
