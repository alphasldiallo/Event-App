package com.matchmore.event

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import io.matchmore.sdk.Matchmore
import io.matchmore.sdk.api.models.Match
import io.matchmore.sdk.api.models.Publication
import io.matchmore.sdk.api.models.Subscription
import io.matchmore.sdk.managers.MatchMoreBeaconManager

class beacon : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beacon)




        val API_KEY = "eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NiJ9.eyJpc3MiOiJhbHBzIiwic3ViIjoiMGM3NDgzNDMtZWQ1Mi00NjYzLWE1NjgtZjJkYTQ2ZWVjNmEyIiwiYXVkIjpbIlB1YmxpYyJdLCJuYmYiOjE1MzMwMjk2MjIsImlhdCI6MTUzMzAyOTYyMiwianRpIjoiMSJ9.L1myrQR795WcAC8k8PZ-7cX-Jx3IhJnYVgpOmx9IQrd3yjWQPJkkUfFMe73-zBUfDXrqlcno4J-aGjRyq2LN_w"
        if (!Matchmore.isConfigured())
        {
            Matchmore.config(this,API_KEY,false)

        }


        checkLocationPermission()

        //createPub()
        createSub()
        getMatches()

        val btn = findViewById(R.id.publish) as Button
        btn.setOnClickListener()
        {

            //createPub()
        }
        //
    }

    private fun createSub()
    {
        Matchmore.instance.apply {
            startUsingMainDevice ({ device ->
                val subscription = Subscription("beacon101", 500.0, 30.0)
                createSubscriptionForMainDevice(subscription, { result ->
                    val status = findViewById(R.id.status) as TextView
                    status.setText(Matchmore.instance.locationManager.lastLocation.toString())

                }, Throwable::printStackTrace)
            }, Throwable::printStackTrace)
        }
    }

    private fun createPub()
    {
        Matchmore.instance.apply {
            startUsingMainDevice ({ device ->

                val pub = Publication("beacon101", 500.0, 30.0)

                createPublicationForMainDevice(pub,
                        { result ->
                            Log.d("debug","Publication made successfully with properties "+pub.properties.toString())
                        }, Throwable::printStackTrace)
            }, Throwable::printStackTrace)
        }
    }


    private fun getMatches ()
    {

        Matchmore.instance.apply {

            //Start fetching matches
            matchMonitor.addOnMatchListener { matches, _ ->
                //We should get there every time a match occur

                Log.d("debug", "We got ${matches.size} matches")

                val status = findViewById(R.id.status) as TextView
                status.setText("There's a match! with this device ${matches.map {d ->
                    matches.first().publication!!.deviceId
                }}")


            }
            matchMonitor.startPollingMatches()
        }

    }

    private fun checkLocationPermission() {
        val permissionListener = object : PermissionListener {
            @SuppressLint("MissingPermission")
            override fun onPermissionGranted() {
                Matchmore.instance.apply {
                    startUpdatingLocation()
                    startRanging()

                    Log.d("debug", "start ranging")
                }

            }

            override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {
                Toast.makeText(this@beacon, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setDeniedMessage("Permission Denied")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN)
                .check()
    }
}
