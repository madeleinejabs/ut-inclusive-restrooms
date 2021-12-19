package com.example.utinclusiverestrooms

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun changeActivity(view: View) {
        val intent = Intent(this, RestroomActivity::class.java)
        startActivity(intent)
    }
}