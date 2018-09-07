package com.matchmore.event


import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.constants.Style
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode.COMPASS
import io.matchmore.sdk.Matchmore
import java.util.ArrayList
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode
import com.matchmore.event.R.id.mapView






class live : AppCompatActivity() {

    private var mapView: MapView? = null

    private var map: MapboxMap? = null
    private var permissionsManager: PermissionsManager? = null
    private var locationPlugin: LocationLayerPlugin? = null
    private val locationEngine: LocationEngine? = null
    private var originLocation: Location? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, "pk.eyJ1IjoiYWxwaGFkaWFsbG8iLCJhIjoiY2prdjhkZDJpMDZmajNrbzhkYmhkdHBwNSJ9.kHR0rIoTbFXOKmH5QG2Evw")
        setContentView(R.layout.live_activity)

        val API_KEY = "eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NiJ9.eyJpc3MiOiJhbHBzIiwic3ViIjoiNjQyZDJmOTktZTcxYS00NzA5LWJmMWYtZjhkZWFkY2Q3OGE1IiwiYXVkIjpbIlB1YmxpYyJdLCJuYmYiOjE1MzIwMDQwMjgsImlhdCI6MTUzMjAwNDAyOCwianRpIjoiMSJ9.EWvR_yTClTvc7nT-avTitW7doImVE0Il7zjM5gEf1LIjKxyAi4ADj0bDpt2XZvUAH9lfAkVEtXiAYjCjc9wApA"
        if (!Matchmore.isConfigured())
        {
            Matchmore.config(this,API_KEY,false)
        }
        checkLocationPermission()



        mapView = findViewById(R.id.mapView)
        val camera = CameraPosition.Builder()
                .target(LatLng(46.522128099999996, 6.583489999999999))
                .zoom(10.0)
                .tilt(20.0)
                .build()

        mapView!!.onCreate(savedInstanceState)

        mapView!!.getMapAsync{
            it.setStyle(Style.OUTDOORS)
            it.cameraPosition = camera
            it.addMarker(MarkerOptions()
                    .position(LatLng(48.13863, 11.57603))
                    .title("Asia")
                    .snippet("Booya")

            )
        }
    }





    private fun checkLocationPermission() {
        val permissionListener = object : PermissionListener {
            @SuppressLint("MissingPermission")
            override fun onPermissionGranted() {
                Matchmore.instance.apply {
                    startUpdatingLocation()
                    startRanging()
                }
            }

            override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {
                Toast.makeText(this@live, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setDeniedMessage("Permission Denied")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check()
    }

}
