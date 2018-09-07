package com.matchmore.event

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class createCluster : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.createcluster)

        val name = findViewById(R.id.name) as EditText
        val next = findViewById(R.id.next) as Button
        val join = findViewById(R.id.joinBtn) as Button


        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        next.setOnClickListener()
        {
            val nameVal: String = name.text.toString()
            Log.d("debug", nameVal)
            if (!nameVal.equals("")) {
                intent.setClass(this, generateClusterId::class.java)
                intent.putExtra("name", nameVal)
                startActivity(intent)
            }
            else
            {
                Toast.makeText(this@createCluster, "The name should be filed", Toast.LENGTH_SHORT).show()
            }
        }

        join.setOnClickListener()
        {
            val nameVal:String = name.text.toString()

            if (!nameVal.equals("")) {
                intent.setClass(this, joinCluster::class.java)
                intent.putExtra("name", nameVal)
                startActivity(intent)
            }
            else
            {
                Toast.makeText(this@createCluster, "The name should be filed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)


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

}
