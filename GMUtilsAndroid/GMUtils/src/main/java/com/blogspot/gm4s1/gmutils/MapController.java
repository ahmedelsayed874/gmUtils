package com.blogspot.gm4s1.gmutils;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.blogspot.gm4s1.gmutils.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

/**
 * dependencies:
 *     implementation 'com.google.android.gms:play-services-maps:17.0.0'
 */

public class MapController {
    private Context mAppContext;
    private GoogleMap mGoogleMap;
    private GoogleMap.InfoWindowAdapter mInfoWindowAdapter;
    private GoogleMap.OnInfoWindowClickListener mOnInfoWindowClickListener;
    private final List<MapPin> pinDataList = new ArrayList<>();
    private LatLng latLng = new LatLng(29.977344, 31.132493);//giza piramids
    private Float zoom = null;
    private boolean buildingsEnabled = false;
    private boolean toolbarControl = false;
    private boolean zoomControls = false;
    private boolean allGesturesEnabled = true;
    private Listener listener;

    public MapController(SupportMapFragment fragment) {
        fragment.getMapAsync(new OnMapReadyCallbackImp());
        mAppContext = fragment.getContext().getApplicationContext();
    }

    public MapController(Context context, @NonNull GoogleMap map) {
        if (map == null) throw new NullPointerException("MapController.GoogleMap");
        mAppContext = context.getApplicationContext();
        new OnMapReadyCallbackImp().onMapReady(map);
    }

    private class OnMapReadyCallbackImp implements OnMapReadyCallback {
        @Override
        public void onMapReady(com.google.android.gms.maps.GoogleMap map) {
            mGoogleMap = map;

            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            map.setIndoorEnabled(true);

            map.setOnMarkerClickListener(new OnMarkerClickListener());

            setInfoWindowAdapter(mInfoWindowAdapter);
            setOnInfoWindowClickListener(mOnInfoWindowClickListener);

            if (pinDataList.size() > 0) {
                for (MapPin pinData : pinDataList) {
                    addMarker(pinData);
                }
            } else {
                moveMapCamera();
            }

            zoom(zoom != null ? zoom : 10);

            map.setBuildingsEnabled(buildingsEnabled);
            map.getUiSettings().setMapToolbarEnabled(toolbarControl);
            map.getUiSettings().setZoomControlsEnabled(zoomControls);
            map.getUiSettings().setCompassEnabled(true);
            map.getUiSettings().setAllGesturesEnabled(allGesturesEnabled);

            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }

    private class OnMarkerClickListener implements GoogleMap.OnMarkerClickListener {
        @Override
        public boolean onMarkerClick(Marker marker) {
            marker.setZIndex(0);
            if (listener != null) listener.onPinClicked(marker);
            return false;
        }
    }

    //----------------------------------------------------------------------------------------------

    public void dispose() {
        mAppContext = null;
        mGoogleMap = null;
        mInfoWindowAdapter = null;
        mOnInfoWindowClickListener = null;
        pinDataList.clear();
        listener = null;
    }

    public GoogleMap getGoogleMap() {
        return mGoogleMap;
    }

    public MapController setInfoWindowAdapter(GoogleMap.InfoWindowAdapter infoWindowAdapter) {
        this.mInfoWindowAdapter = infoWindowAdapter;
        if (mGoogleMap != null) {
            mGoogleMap.setInfoWindowAdapter(mInfoWindowAdapter);
        }
        return this;
    }

    public MapController setOnInfoWindowClickListener(GoogleMap.OnInfoWindowClickListener onInfoWindowClickListener) {
        mOnInfoWindowClickListener = onInfoWindowClickListener;
        if (mGoogleMap != null) {
            mGoogleMap.setOnInfoWindowClickListener(onInfoWindowClickListener);
        }
        return this;
    }

    //----------------------------------------------------------------------------------------------

    public void addMarker(double lat, double lng, String title, Object tag) {
        addMarker(lat, lng, title, tag, true);
    }

    public void addMarker(double lat, double lng, String title, Object tag, boolean moveToPin) {
        MapPin pinData = new MapPin()
                .setLat(lat)
                .setLng(lng)
                .setName(title)
                .setExtraData(tag);

        addMarker(pinData, moveToPin);
    }

    public void addMarkers(List<MapPin> mapPinList) {
        for (MapPin mapPin : mapPinList) {
            addMarker(mapPin, false);
        }

        moveMapCamera();
    }

    public void addMarker(MapPin mapPin) {
        addMarker(mapPin, true);
    }

    public void addMarker(MapPin mapPin, boolean moveToPin) {
        if (mGoogleMap == null) {
            if (!pinDataList.contains(mapPin)) {
                pinDataList.add(mapPin);
            }
            return;
        }

        this.latLng = new LatLng(mapPin.getLat(), mapPin.getLng());

        Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(mapPin.getName()));

        marker.setTag(mapPin.getExtraData());

        Bitmap icon = mapPin.getIcon(mAppContext);
        if (icon == null) {
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        } else {
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
        }
        marker.setZIndex(0);

        if (moveToPin) moveMapCamera();
    }

    //----------------------------------------------------------------------------------------------

    public void moveMapCamera(double lat, double lng) {
        latLng = new LatLng(lat, lng);
        moveMapCamera();
    }

    public void moveMapCamera() {
        if (mGoogleMap != null) {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }

    public void moveMapCamera(float zoom) {
        this.zoom = zoom;
        if (mGoogleMap != null) {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        }
    }

    public void zoom(float zoom) {
        this.zoom = zoom;
        if (mGoogleMap != null) {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        }
    }

    //----------------------------------------------------------------------------------------------

    public void enableToolbarControl() {
        toolbarControl = true;
        if (mGoogleMap != null) {
            mGoogleMap.getUiSettings().setMapToolbarEnabled(true);
        }
    }

    public void enableZoomControl() {
        zoomControls = true;
        if (mGoogleMap != null) {
            mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        }
    }

    public void enableBuildings() {
        buildingsEnabled = true;
        if (mGoogleMap != null) {
            mGoogleMap.setBuildingsEnabled(true);
        }
    }

    public void disableAllGestures() {
        allGesturesEnabled = false;
        if (mGoogleMap != null) {
            mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
        }
    }

    //----------------------------------------------------------------------------------------------

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onPinClicked(Marker marker);
    }


    //----------------------------------------------------------------------------------------------

    public static class MapPin implements Parcelable, Serializable {
        private String id;
        private String name;
        private double lat;
        private double lng;
        private String icon;
        private Object extraData;


        public MapPin() {
        }

        public MapPin setId(String id) {
            this.id = id;
            return this;
        }

        public MapPin setName(String name) {
            this.name = name;
            return this;
        }

        public MapPin setLatLng(String latLng) {
            String[] coodrs = null;
            try {
                coodrs = latLng.split(",");
            } catch (Exception e) {
                coodrs = new String[]{"0" , "0"};
            }
            try {
                this.lat = Double.parseDouble(coodrs[0]);
            } catch (Exception e) {
                this.lat = 0;
            }
            try {
                this.lng = Double.parseDouble(coodrs[1]);
            } catch (Exception e) {
                this.lng = 0;
            }
            return this;
        }

        public MapPin setLat(double lat) {
            this.lat = lat;
            return this;
        }

        public MapPin setLng(double lng) {
            this.lng = lng;
            return this;
        }

        public MapPin setIcon(@DrawableRes int icon) {
            this.icon = icon + "";
            return this;
        }

        public MapPin setIcon(String assetPath) {
            this.icon = assetPath;
            return this;
        }

        public MapPin setExtraData(Object extraData) {
            this.extraData = extraData;
            return this;
        }

        //----------------------------------------------------------------------------------------------

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public double getLat() {
            return lat;
        }

        public double getLng() {
            return lng;
        }

        public Object getExtraData() {
            return extraData;
        }

        public Bitmap getIcon(Context context) {
            try {
                int resId = Integer.parseInt(icon);
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                InputStream inputStream = context.getAssets().open(icon);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        //----------------------------------------------------------------------------------------------

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (obj == this) return true;

            if (obj instanceof MapPin) {
                MapPin otherObj = (MapPin) obj;

                if (!TextUtils.equals(this.id, otherObj.id)) return false;
                if (!TextUtils.equals(this.name, otherObj.name)) return false;
                if (this.lat != otherObj.lat) return false;
                if (this.lng != otherObj.lng) return false;
                if (!TextUtils.equals(this.icon, otherObj.icon)) return false;
                if (!Utils.createInstance().checkEquality(this.extraData, otherObj.extraData)) return false;

                return true;
            }

            return false;
        }

        //----------------------------------------------------------------------------------------------

        protected MapPin(Parcel in) {
            id = in.readString();
            name = in.readString();
            lat = in.readDouble();
            lng = in.readDouble();
            icon = in.readString();
            extraData = in.readValue(getClass().getClassLoader());
        }

        public static final Creator<MapPin> CREATOR = new Creator<MapPin>() {
            @Override
            public MapPin createFromParcel(Parcel in) {
                return new MapPin(in);
            }

            @Override
            public MapPin[] newArray(int size) {
                return new MapPin[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(name);
            dest.writeDouble(lat);
            dest.writeDouble(lng);
            dest.writeString(icon);
            dest.writeValue(extraData);
        }

        @Override
        public String toString() {
            return lat + ",\n" + lng + "\n[" + name + "]";
        }
    }

}
