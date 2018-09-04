package com.matchmore.event

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.gun0912.tedpermission.PermissionListener
import io.matchmore.sdk.api.models.Publication
import java.util.*
import io.matchmore.sdk.Matchmore
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.createcluster.*

class generateClusterId : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_cluster_id)

        val API_KEY = "eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NiJ9.eyJpc3MiOiJhbHBzIiwic3ViIjoiMGM3NDgzNDMtZWQ1Mi00NjYzLWE1NjgtZjJkYTQ2ZWVjNmEyIiwiYXVkIjpbIlB1YmxpYyJdLCJuYmYiOjE1MzMwMjk2MjIsImlhdCI6MTUzMzAyOTYyMiwianRpIjoiMSJ9.L1myrQR795WcAC8k8PZ-7cX-Jx3IhJnYVgpOmx9IQrd3yjWQPJkkUfFMe73-zBUfDXrqlcno4J-aGjRyq2LN_w"
        if (!Matchmore.isConfigured())
        {
            Matchmore.config(this,API_KEY,false)
        }
        checkLocationPermission()

        val clusterId = findViewById(R.id.clusterId) as TextView

        val id = generateRandom()

        val bundle = intent.extras

        var name:String = bundle.get("name").toString()


        Log.d("debug", "Name = "+name)
        clusterId.setText(id.toString())
        createPub(id, name)
        getMatches(name)


    }

    fun generateRandom(): Int {
        //This function will generate a random number between 1000 and 9999
        return Random().nextInt(9999 + 1 - 1000) + 1000
    }

    private fun createPub(i: Int, n: String)
    {
        Matchmore.instance.apply {
            startUsingMainDevice ({ device ->
                Log.d("debug","Currently using Device "+device.name)
                val pub = Publication("cluster", 500.0, 180.0)
                pub.properties = hashMapOf("created_by" to title.toString(), "room_number" to i.toString())
                createPublicationForMainDevice(pub,
                        { result ->
                            Log.d("debug","Publication made successfully with topic "+pub.topic.toString())
                        }, Throwable::printStackTrace)
            }, Throwable::printStackTrace)
        }
    }

    private fun getMatches (name:String)
    {
        val listView = findViewById (R.id.list_members) as ListView

        // Empty Array that will used to store the properties of the publications
        var rsl: ArrayList<String> = ArrayList()

        Log.d("debug", "Check matches")
        rsl.add(name)

        Matchmore.instance.apply {

            //Start fetching matches
            matchMonitor.addOnMatchListener { matches, _ ->
                //We should get there every time a match occur

                Log.d("debug", "We got ${matches.size} matches")

                val first = matches.first()

                //Let's fill our Array with the properties of the publication
                rsl.add(first.publication!!.properties["content"].toString())
            }
            matchMonitor.startPollingMatches(1000)
        }
        val adapter = ArrayAdapter(this@generateClusterId, android.R.layout.simple_list_item_1, rsl)
        listView.adapter = adapter
    }

    private fun fullscreen() {
        window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
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
                Toast.makeText(this@generateClusterId, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setDeniedMessage("Permission Denied")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check()
    }
}