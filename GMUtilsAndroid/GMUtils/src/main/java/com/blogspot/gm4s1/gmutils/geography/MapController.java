package com.blogspot.gm4s1.gmutils.geography;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

/**
 * dependencies:
 * implementation 'com.google.android.gms:play-services-maps:17.0.0'
 */

public class MapController {
    private Context mAppContext;
    private GoogleMap mGoogleMap;
    private GoogleMap.InfoWindowAdapter mInfoWindowAdapter;
    private GoogleMap.OnInfoWindowClickListener mOnInfoWindowClickListener;
    private LatLng latLng = new LatLng(29.977344, 31.132493);//giza piramids
    private List<MapPin> tempPinDataList;
    private Float zoom = null;
    private boolean buildingsEnabled = false;
    private boolean toolbarControl = false;
    private boolean zoomControls = false;
    private boolean allGesturesEnabled = true;
    private PinClickListener listener;
    private Map<String, Marker> mMarkers = new HashMap<>();

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

            setMapType(GoogleMap.MAP_TYPE_NORMAL);
            map.setIndoorEnabled(true);

            map.setOnMarkerClickListener(new OnMarkerClickListener());

            setInfoWindowAdapter(mInfoWindowAdapter);
            setOnInfoWindowClickListener(mOnInfoWindowClickListener);

            if (tempPinDataList != null && tempPinDataList.size() > 0) {
                for (MapPin pinData : tempPinDataList) {
                    addMarker(pinData);
                }
                tempPinDataList.clear();
                tempPinDataList = null;
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

    /**
     * @param type GoogleMap
     */
    public void setMapType(int type) {
        mGoogleMap.setMapType(type);
    }

    //----------------------------------------------------------------------------------------------

    public void setPinClickListener(PinClickListener listener) {
        this.listener = listener;
    }

    public void setInfoWindowAdapter(GoogleMap.InfoWindowAdapter infoWindowAdapter) {
        this.mInfoWindowAdapter = infoWindowAdapter;
        if (mGoogleMap != null) {
            mGoogleMap.setInfoWindowAdapter(mInfoWindowAdapter);
        }
    }

    public void setOnInfoWindowClickListener(GoogleMap.OnInfoWindowClickListener onInfoWindowClickListener) {
        mOnInfoWindowClickListener = onInfoWindowClickListener;
        if (mGoogleMap != null) {
            mGoogleMap.setOnInfoWindowClickListener(onInfoWindowClickListener);
        }
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

    /**
     * 1: World
     * 5: Landmass/continent
     * 10: City
     * 15: Streets
     * 20: Buildings
     * <p>
     * https://developers.google.com/maps/documentation/android-sdk/views#zoom
     */
    public void zoom(float zoom) {
        this.zoom = zoom;
        if (mGoogleMap != null) {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        }
    }

    //----------------------------------------------------------------------------------------------

    public GoogleMap getGoogleMap() {
        return mGoogleMap;
    }

    public Collection<Marker> getMarkers() {
        return mMarkers.values();
    }

    public void destroy() {
        mAppContext = null;
        mGoogleMap = null;
        mInfoWindowAdapter = null;
        mOnInfoWindowClickListener = null;
        tempPinDataList = null;
        listener = null;
        mMarkers.clear();
        mMarkers = null;
    }

    //----------------------------------------------------------------------------------------------

    public void addMarker(double lat, double lng, String title, Object extraData) {
        addMarker(lat, lng, title, extraData, true);
    }

    public void addMarker(double lat, double lng, String title, Object extraData, boolean moveToPin) {
        MapPin pinData = new MapPin(lat, lng)
                .setName(title)
                .setExtraData(extraData);

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
            if (tempPinDataList == null)
                tempPinDataList = new ArrayList<>();

            if (!tempPinDataList.contains(mapPin)) {
                tempPinDataList.add(mapPin);
            }
            return;
        }

        if (mMarkers.containsKey(mapPin.id)) {
            mMarkers.get(mapPin.id).remove();
            mMarkers.remove(mapPin.id);
        }

        this.latLng = new LatLng(mapPin.lat, mapPin.lng);

        Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(mapPin.name));

        marker.setTag(mapPin.extraData);

        Bitmap icon = mapPin.getIcon(mAppContext);
        if (icon == null) {
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        } else {
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
        }
        marker.setZIndex(0);

        if (moveToPin) moveMapCamera();

        mMarkers.put(mapPin.id, marker);
    }

    //----------------------------------------------------------------------------------------------

    public static class MapPin implements Parcelable, Serializable {
        private String id;
        private String name;
        private double lat;
        private double lng;
        private String icon;
        private Object extraData;


        public MapPin(double lat, double lng) {
            this(lat + "," + lng, lat, lng);
        }

        public MapPin(String id, double lat, double lng) {
            this.id = id;
            this.lat = lat;
            this.lng = lng;
        }

        public MapPin(String latLng) {
            setLatLng(latLng);
            this.id = latLng;
        }

        public MapPin(String id, String latLng) {
            setLatLng(latLng);
            this.id = id;
        }

        private void setLatLng(String latLng) {
            String[] coodrs = null;
            try {
                coodrs = latLng.split(",");
            } catch (Exception e) {
                coodrs = new String[]{"0", "0"};
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
        }

        public MapPin setName(String name) {
            this.name = name;
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
                //if (!TextUtils.equals(this.name, otherObj.name)) return false;
                if (this.lat != otherObj.lat) return false;
                if (this.lng != otherObj.lng) return false;
                //if (!TextUtils.equals(this.icon, otherObj.icon)) return false;
                //if (!Utils.createInstance().checkEquality(this.extraData, otherObj.extraData)) return false;

                return true;
            }

            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, lat, lng);
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

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(name);
            dest.writeDouble(lat);
            dest.writeDouble(lng);
            dest.writeString(icon);
            dest.writeValue(extraData);
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
        public String toString() {
            return lat + ",\n" + lng + "\n[" + name + "]";
        }
    }

    //----------------------------------------------------------------------------------------------

    public interface PinClickListener {
        void onPinClicked(Marker marker);
    }

}
