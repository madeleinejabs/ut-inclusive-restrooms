package com.example.utinclusiverestrooms

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // get reference to button
        val findRestroomButton = findViewById<Button>(R.id.button)
        // set on-click listener
        findRestroomButton.setOnClickListener {
            val intent = Intent(this, RestroomActivity::class.java)
            startActivity(intent)
        }
    }
}