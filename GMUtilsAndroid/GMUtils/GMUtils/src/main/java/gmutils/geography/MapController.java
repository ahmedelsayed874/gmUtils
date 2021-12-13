package gmutils.geography;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import gmutils.images.ImageUtils;
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

/**
 * dependencies:
 * implementation 'com.google.android.gms:play-services-maps:17.0.0'
 */

public class MapController {
    public static int DEFAULT_ZOOM = 10;

    public static class MapPin implements Parcelable, Serializable {
        private static final int ICON_SOURCE_RESOURCES = 0;
        private static final int ICON_SOURCE_ASSETS = 1;
        private static final int ICON_SOURCE_FILE = 2;

        private final String id;
        private String name;
        private double lat;
        private double lng;
        private String icon;
        private int iconSource; //0: resources | 1: assets | 2: file
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
            this.icon = icon == 0 ? null : icon + "";
            this.iconSource = ICON_SOURCE_RESOURCES;
            return this;
        }

        public MapPin setIcon(String assetPath) {
            this.icon = assetPath;
            this.iconSource = ICON_SOURCE_ASSETS;
            return this;
        }

        public MapPin setIcon(File file) {
            if (file == null) {
                this.icon = null;
                this.iconSource = ICON_SOURCE_RESOURCES;
            } else {
                this.icon = file.getAbsolutePath();
                this.iconSource = ICON_SOURCE_FILE;
            }

            return this;
        }

        public MapPin setExtraData(Object extraData) {
            this.extraData = extraData;
            return this;
        }

        //----------------------------------------------------------------------------------------------

        public Bitmap getIcon(Context context) {
            if (ICON_SOURCE_RESOURCES == iconSource) {
                try {
                    int resId = Integer.parseInt(icon);
                    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
                    return bitmap;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (ICON_SOURCE_ASSETS == iconSource) {
                try {
                    Bitmap bitmap = ImageUtils.createInstance().openBitmapFromAssets(context, icon);
                    return bitmap;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (ICON_SOURCE_FILE == iconSource) {
                try {
                    File file = new File(icon);
                    Bitmap bitmap = ImageUtils.createInstance().openBitmapFile(file);
                    return bitmap;
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                return this.lng == otherObj.lng;
                //if (!TextUtils.equals(this.icon, otherObj.icon)) return false;
                //if (!Utils.createInstance().checkEquality(this.extraData, otherObj.extraData)) return false;
            }

            return false;
        }

        @Override
        public int hashCode() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                return Objects.hash(id, lat, lng);
            }

            return ("" + id + "," + lat + "," + lng).hashCode();
        }

        //----------------------------------------------------------------------------------------------

        protected MapPin(Parcel in) {
            id = in.readString();
            name = in.readString();
            lat = in.readDouble();
            lng = in.readDouble();
            icon = in.readString();
            iconSource = in.readInt();
            extraData = in.readValue(getClass().getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(name);
            dest.writeDouble(lat);
            dest.writeDouble(lng);
            dest.writeString(icon);
            dest.writeInt(iconSource);
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

    private static class MapPin2 {
        MapPin mapPin;
        Bitmap icon;

        public MapPin2(MapPin mapPin) {
            this.mapPin = mapPin;
        }

        public MapPin2(MapPin mapPin, Bitmap icon) {
            this.mapPin = mapPin;
            this.icon = icon;
        }

    }

    //----------------------------------------------------------------------------------------------

    public interface InitListener {
        void onInitializeComplete(MapController obj);
    }

    public interface PinClickListener {
        void onPinClicked(Marker marker);
    }

    //----------------------------------------------------------------------------------------------

    private Context mAppContext;
    private GoogleMap mGoogleMap;
    private GoogleMap.InfoWindowAdapter mInfoWindowAdapter;
    private GoogleMap.OnInfoWindowClickListener mOnInfoWindowClickListener;
    private LatLng latLng = new LatLng(29.977344, 31.132493);//giza piramids
    private List<MapPin2> tempPinDataList;
    private float zoom = DEFAULT_ZOOM;
    private Integer mapType = null;
    private boolean buildingsEnabled = false;
    private boolean showMyLocationEnabled = false;
    private boolean toolbarControl = false;
    private boolean zoomControls = false;
    private boolean allGesturesEnabled = true;
    private Map<String, Marker> mMarkers = new HashMap<>();
    private Bitmap defaultMarkerIcon;
    private PinClickListener listener;


    public MapController(SupportMapFragment fragment) {
        this(fragment, null);
    }

    public MapController(SupportMapFragment fragment, InitListener initListener) {
        checkRequiredClasses();
        fragment.getMapAsync(new OnMapReadyCallbackImp(initListener));
        mAppContext = fragment.getContext().getApplicationContext();

        UIUtils.createInstance().addOnFragmentDestroyedObserver(fragment, this::destroy);
    }

    public MapController(Context context, @NonNull GoogleMap map) {
        this(context, map, null);
    }

    public MapController(Context context, @NonNull GoogleMap map, InitListener initListener) {
        checkRequiredClasses();
        if (map == null) throw new NullPointerException("MapController.GoogleMap");
        mAppContext = context.getApplicationContext();

        new OnMapReadyCallbackImp(initListener).onMapReady(map);
    }

    private void checkRequiredClasses() {
        try {
            Class.forName("com.google.android.gms.maps.GoogleMap");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalStateException("add this line to gradle script file:\n" +
                    "implementation 'com.google.android.gms:play-services-maps:17.0.0'");
        }
    }

    private class OnMapReadyCallbackImp implements OnMapReadyCallback {
        private InitListener mInitListener;

        public OnMapReadyCallbackImp(InitListener mInitListener) {
            this.mInitListener = mInitListener;
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onMapReady(com.google.android.gms.maps.GoogleMap map) {
            mGoogleMap = map;

            setMapType(mapType != null ? mapType : GoogleMap.MAP_TYPE_NORMAL);
            map.setIndoorEnabled(true);

            map.setOnMarkerClickListener(new OnMarkerClickListener());

            setInfoWindowAdapter(mInfoWindowAdapter);
            setOnInfoWindowClickListener(mOnInfoWindowClickListener);

            if (tempPinDataList != null && tempPinDataList.size() > 0) {
                for (MapPin2 pinData : tempPinDataList) {
                    Marker marker = addMarker(pinData.mapPin);
                    if (marker != null && pinData.icon != null) {
                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(pinData.icon));
                    }
                }
                tempPinDataList.clear();
                tempPinDataList = null;
            } else {
                moveMapCamera();
            }

            zoom(zoom);

            try {
                map.setMyLocationEnabled(showMyLocationEnabled);
            } catch (Exception e) {
            }
            map.setBuildingsEnabled(buildingsEnabled);
            map.getUiSettings().setMapToolbarEnabled(toolbarControl);
            map.getUiSettings().setZoomControlsEnabled(zoomControls);
            map.getUiSettings().setCompassEnabled(true);
            map.getUiSettings().setAllGesturesEnabled(allGesturesEnabled);

            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

            if (mInitListener != null)
                mInitListener.onInitializeComplete(MapController.this);
            mInitListener = null;
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

    public MapController enableToolbarControl() {
        toolbarControl = true;
        if (mGoogleMap != null) {
            mGoogleMap.getUiSettings().setMapToolbarEnabled(true);
        }
        return this;
    }

    public MapController enableZoomControl() {
        zoomControls = true;
        if (mGoogleMap != null) {
            mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        }
        return this;
    }

    public MapController enableBuildings() {
        buildingsEnabled = true;
        if (mGoogleMap != null) {
            mGoogleMap.setBuildingsEnabled(true);
        }
        return this;
    }

    public MapController disableAllGestures() {
        allGesturesEnabled = false;
        if (mGoogleMap != null) {
            mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
        }
        return this;
    }

    @RequiresPermission(
            anyOf = {"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}
    )
    public MapController showMyLocationButton() {
        showMyLocationEnabled = true;
        if (mGoogleMap != null) {
            mGoogleMap.setMyLocationEnabled(true);
        }
        return this;
    }

    /**
     * @param type GoogleMap
     */
    public MapController setMapType(int type) {
        mapType = type;
        if (mGoogleMap != null) mGoogleMap.setMapType(type);
        return this;
    }

    public MapController setDefaultMarkerIcon(Bitmap defaultMarkerIcon) {
        this.defaultMarkerIcon = defaultMarkerIcon;
        return this;
    }

    public MapController setDefaultMarkerIcon(File defaultMarkerIconFile) throws IOException {
        this.defaultMarkerIcon = ImageUtils.createInstance().openBitmapFile(defaultMarkerIconFile);
        return this;
    }

    //----------------------------------------------------------------------------------------------

    public MapController setPinClickListener(PinClickListener listener) {
        this.listener = listener;
        return this;
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

    /**
     * 1: World
     * 5: Landmass/continent
     * 10: City
     * 15: Streets
     * 20: Buildings
     * <p>
     * https://developers.google.com/maps/documentation/android-sdk/views#zoom
     */
    public MapController zoom(float zoom) {
        this.zoom = zoom;
        if (mGoogleMap != null) {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        }
        return this;
    }

    public void moveMapCamera(double lat, double lng) {
        latLng = new LatLng(lat, lng);
        moveMapCamera();
    }

    public void moveMapCamera(String id) {
        Marker marker = getMarker(id);
        if (marker != null) {
            latLng = marker.getPosition();
            moveMapCamera();
        }
    }

    public void moveMapCamera() {
        if (mGoogleMap != null) {
            //mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            zoom(zoom);
        }
    }

    //----------------------------------------------------------------------------------------------

    public GoogleMap getGoogleMap() {
        return mGoogleMap;
    }

    public Collection<Marker> getMarkers() {
        return mMarkers.values();
    }

    public void removeMarker(String id) {
        try {
            mMarkers.get(id).remove();
            mMarkers.remove(id);
        } catch (Exception e) {
        }
    }

    public void removeAllMarkers() {
        if (mMarkers == null) return;

        for (Map.Entry<String, Marker> entry : mMarkers.entrySet()) {
            try {
                entry.getValue().remove();
            } catch (Exception e) {
            }
        }

        mMarkers.clear();
    }

    public Marker getMarker(String id) {
        return mMarkers.get(id);
    }

    public void destroy() {
        mAppContext = null;
        mGoogleMap = null;
        mInfoWindowAdapter = null;
        mOnInfoWindowClickListener = null;
        tempPinDataList = null;
        listener = null;

        if (mMarkers != null) mMarkers.clear();
        mMarkers = null;

        if (defaultMarkerIcon != null) defaultMarkerIcon.recycle();
        defaultMarkerIcon = null;
    }

    //----------------------------------------------------------------------------------------------

    public Marker addMarker(double lat, double lng, String title, Object extraData) {
        return addMarker(lat, lng, title, extraData, true);
    }

    public Marker addMarker(double lat, double lng, String title, Object extraData, boolean moveToPin) {
        MapPin pinData = new MapPin(lat, lng)
                .setName(title)
                .setExtraData(extraData);

        return addMarker(pinData, moveToPin);
    }

    public Marker addMarker(String id, double lat, double lng, String title, Object extraData) {
        return addMarker(id, lat, lng, title, extraData, true);
    }

    public Marker addMarker(String id, double lat, double lng, String title, Object extraData, boolean moveToPin) {
        MapPin pinData = new MapPin(id, lat, lng)
                .setName(title)
                .setExtraData(extraData);

        return addMarker(pinData, moveToPin);
    }

    public void addMarkers(List<MapPin> mapPinList) {
        for (MapPin mapPin : mapPinList) {
            addMarker(mapPin, false);
        }

        moveMapCamera();
    }

    public Marker addMarker(MapPin mapPin) {
        return addMarker(mapPin, true);
    }

    public Marker addMarker(MapPin mapPin, boolean moveToPin) {
        if (mGoogleMap == null) {
            if (tempPinDataList == null)
                tempPinDataList = new ArrayList<>();

            if (!tempPinDataList.contains(mapPin)) {
                tempPinDataList.add(new MapPin2(mapPin));
            }
            return null;
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
            if (defaultMarkerIcon != null) {
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(defaultMarkerIcon));
            } else {
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            }
        } else {
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
        }
        marker.setZIndex(0);

        if (moveToPin) moveMapCamera();

        mMarkers.put(mapPin.id, marker);

        return marker;
    }

    //----------------------------------------------------------------------------------------------

    public void setMarkerIcon(String id, Bitmap icon) {
        boolean f = false;
        if (mMarkers != null) {
            Marker marker = mMarkers.get(id);
            if (marker != null) {
                f = true;
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
            }
        }
        if (!f) {
            if (tempPinDataList != null)
                for (MapPin2 mapPin : tempPinDataList) {
                    if (("" + id).equals(mapPin.mapPin.id)) {
                        mapPin.icon = icon;
                        break;
                    }
                }
        }
    }

    public void setMarkerIcon(String id, File icon) {
        try {
            Bitmap bitmap = ImageUtils.createInstance().openBitmapFile(icon);
            setMarkerIcon(id, bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
