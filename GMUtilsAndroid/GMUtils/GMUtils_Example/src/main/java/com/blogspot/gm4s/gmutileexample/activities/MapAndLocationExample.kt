package com.blogspot.gm4s.gmutileexample.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.blogspot.gm4s.gmutileexample.R
import gmutils.Activities
import gmutils.Logger
import gmutils.geography.LocationTracker
import gmutils.geography.MapController
import gmutils.storage.AccountStorage
import gmutils.ui.toast.MyToast
import gmutils.ui.activities.BaseActivity
import gmutils.ui.dialogs.MessageDialog
import gmutils.ui.utils.ViewSource
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker

class MapAndLocationExample : BaseActivity() {

    companion object {
        fun start(context: Context) {
            Activities.start(MapAndLocationExample::class.java, context, null)
        }

        const val REQUEST_CODE_LOCATION = 1
    }

    private var requiredPermissionGranted = false
    private var mMapController: MapController? = null
    private var mLocationTracker: LocationTracker? = null

    private var mapPinId = 1 //important for tracking

    override fun getViewSource(inflater: LayoutInflater) = ViewSource.LayoutResource(R.layout.activity_map_and_location)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkRequiredPermissions()

        initUI()

    }

    private fun checkRequiredPermissions() {
        val notGranted1 = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
        val notGranted2 = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED

        if (notGranted1 && notGranted2) {
            requiredPermissionGranted = false
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), REQUEST_CODE_LOCATION
            )
        } else {
            requiredPermissionGranted = true
        }
    }

    //----------------------------------------------------------------------------------------------

    override fun onStart() {
        super.onStart()

        //region check required permissions
        checkRequiredPermissions()
        //endregion

    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requiredPermissionGranted = true
                setMyLocationButtonEnabled()
                setupLocationTracker()

            } else {
                showEnableGPSAlert()
            }
        }
    }

    private fun showEnableGPSAlert() {
        MessageDialog.create(this)
            .setMessage(/*R.string.*/"we need you to enable gps to enable us get your physical location")
            .setButton1(R.string.ok) {
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setButton2(R.string.cancel, null)
            .show()
    }

    //----------------------------------------------------------------------------------------------

    //region UI aspect

    private fun initUI() {
        setupMap()

        setupLocationTracker()
    }

    private fun setupMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mMapController = MapController(mapFragment)
        mMapController?.zoom(15f)
        mMapController?.enableZoomControl()
        //mMapController?.enableToolbarControl()

        mMapController?.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            override fun getInfoWindow(p0: Marker): View? {
                return null
            }

            override fun getInfoContents(p0: Marker): View? {
                val info = p0.tag as? String
                return createMapInfoWindowView(info)
            }

        })
        mMapController?.setOnInfoWindowClickListener {
            val info = it.tag as String
            MyToast.show(thisActivity(), info)
        }

        setMyLocationButtonEnabled()
    }

    @SuppressLint("MissingPermission")
    private fun setMyLocationButtonEnabled() {
        /*if (requiredPermissionGranted) {
//            mMapController?.showMyLocationButton()
        }*/
    }

    private fun createMapInfoWindowView(info: String?): View? {
        if (info != null) {
            val view = TextView(this)
            view.setText(info)
            view.setBackgroundResource(R.drawable.shape_solid_round_accent)
            view.setPadding(15, 15, 15, 15)

            return view
        }
        return null
    }

    //----------------------------------------------------------------------------------------------

    @SuppressLint("MissingPermission")
    private fun setupLocationTracker() {
        if (!requiredPermissionGranted) return
        mLocationTracker = LocationTracker(this, object : LocationTracker.Listener {
            override fun onLocationFounded(obj: LocationTracker?, location: Location) {
                val pin = MapController.MapPin(
                    mapPinId.toString(), //important for tracking
                    location.latitude,
                    location.longitude
                ).also {
                    it.setName(AccountStorage.ACCOUNT._id())
                    it.setExtraData(AccountStorage.ACCOUNT)
                    //it.setIcon()
                }

                mMapController?.addMarker(pin)

            }

            override fun onLocationProviderStatusChanged(
                p0: LocationTracker?,
                p1: String?,
                disabled: Boolean
            ) {
                if (disabled) {
                    if (LocationManager.GPS_PROVIDER == p1) {
                        showEnableGPSAlert()
                    }
                }
            }

            override fun onErrorOccurred(obj: LocationTracker?, error: String?) {
                Logger.print(Logger.Callbacks.PrintSingle { error ?: "" })
            }
        })
        mLocationTracker?.startLocationUpdating()
    }

//endregion

//----------------------------------------------------------------------------------------------

    override fun onDestroy() {
        super.onDestroy()

        mMapController?.destroy()
        mMapController = null

    }


}