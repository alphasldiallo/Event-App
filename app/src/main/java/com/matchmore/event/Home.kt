package com.matchmore.event

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class Home : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)
        val findFriend = findViewById(R.id.findFriends) as Button
        val touring = findViewById(R.id.touringGuide) as Button
        val touringGuide = findViewById(R.id.touringGuide) as? Button

        hideSystemUI()
        var intent = Intent ()

        findFriend.setOnClickListener()
        {
            intent.setClass(this, createCluster::class.java)
            startActivity(intent)
        }

        touring.setOnClickListener()
        {
            intent.setClass(this, beacon::class.java)
            startActivity(intent)
        }


    }


    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }
}
