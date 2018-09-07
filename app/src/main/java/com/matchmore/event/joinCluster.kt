package com.matchmore.event

import android.Manifest
import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import io.matchmore.sdk.Matchmore
import io.matchmore.sdk.api.models.Publication
import io.matchmore.sdk.api.models.Subscription
import java.util.ArrayList

class joinCluster : AppCompatActivity() {

    private var is_match:Boolean ?= false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.join_cluster)

        val API_KEY = "eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NiJ9.eyJpc3MiOiJhbHBzIiwic3ViIjoiNjQyZDJmOTktZTcxYS00NzA5LWJmMWYtZjhkZWFkY2Q3OGE1IiwiYXVkIjpbIlB1YmxpYyJdLCJuYmYiOjE1MzIwMDQwMjgsImlhdCI6MTUzMjAwNDAyOCwianRpIjoiMSJ9.EWvR_yTClTvc7nT-avTitW7doImVE0Il7zjM5gEf1LIjKxyAi4ADj0bDpt2XZvUAH9lfAkVEtXiAYjCjc9wApA"
        if (!Matchmore.isConfigured())
        {
            Matchmore.config(this,API_KEY,false)
        }
        checkLocationPermission()


        val idT = findViewById(R.id.idJoin) as EditText
        val joinBtn = findViewById(R.id.joinClusterBtn) as Button
        val find = findViewById(R.id.find) as Button
        find.setVisibility(View.GONE)


        joinBtn.setOnClickListener()
        {
            val id:String = idT.text.toString()
            createSub("cluster", id, 180.0)
            val match = findViewById(R.id.match) as TextView
            match.setText("No crew found with this ID\n Please wait...")
        }

        checkMatches()


    }

    private fun createSub(topic:String, room_number:String, duration:Double)
    {
        Log.d ("debug", "createSub")
        Matchmore.instance.apply {
            startUsingMainDevice ({ d ->
                Log.d("debug", "location_sub"+d.location.toString())
                Matchmore.instance.locationManager.lastLocation
                val subscription = Subscription(topic, 500.0, duration)
                subscription.selector = "room_number = '${room_number}'"

                Log.d("debug", subscription.selector.toString())
                createSubscriptionForMainDevice(subscription, { result ->
                    Log.d("debug", "Subscription made successfully with topic ${result.topic}")

                }, Throwable::printStackTrace)
            }, Throwable::printStackTrace)
        }
    }

    private fun createPub(topic:String, name: String, room_name: String, duration: Double)
    {
        Matchmore.instance.apply {
            startUsingMainDevice ({ device ->
                val pub = Publication(topic, 500.0, duration)
                pub.properties = hashMapOf("created_by" to name,
                        "room_name" to room_name,
                        "location" to Matchmore.instance.locationManager.lastLocation!!.toString()
                        )
                createPublicationForMainDevice(pub,
                        { result ->
                            Log.d("debug","Publication made successfully on this topic "+pub.topic)
                        }, Throwable::printStackTrace)
            }, Throwable::printStackTrace)
        }
    }

    private fun checkMatches() {

        val match = findViewById(R.id.match) as TextView
        Matchmore.instance.apply {

            //Start fetching matches
            matchMonitor.addOnMatchListener { matches, _ ->
                //We should get there every time a match occur

                Log.d("debug", "We got ${matches.size} matches")
                Log.d("debug", "Matches =  ${matches.toString()}")
                match.setText("You've successfully joined the crew of ${matches.first().publication!!.properties["created_by"]}!")
                val joinBtn = findViewById(R.id.joinClusterBtn) as Button
                joinBtn.visibility = View.GONE

                val bundle = intent.extras
                var name:String = bundle.get("name").toString()
                val idT = findViewById(R.id.idJoin) as EditText
                val room_number:String = idT.text.toString()
                val findBtn = findViewById(R.id.find) as Button
                findBtn.setVisibility(View.VISIBLE)

                matches.map { map ->
                    Log.d("debug", "topic = "+map.publication!!.topic.toString())
                }
                if (is_match != true)
                {
                    is_match = true
                    createPub("cluster_joined_"+room_number, name, room_number, 180.0)
                    Log.d("debug", "cluster_joined_"+room_number)
                }


                findBtn.setOnClickListener()
                {
                    intent.setClass(this@joinCluster, live::class.java)
                    intent.putExtra("name", name).putExtra("room_number", room_number)
                    startActivity(intent)
                }
                //createPub("find", name, room_number, 0.0)
                //createSub("find", room_number, 0.0)


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
                }
            }

            override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {
                Toast.makeText(this@joinCluster, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setDeniedMessage("Permission Denied")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check()
    }

    }
