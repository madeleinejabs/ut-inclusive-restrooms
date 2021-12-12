package com.example.utinclusiverestrooms

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels

class RestroomActivity : AppCompatActivity() {
    private val viewModel: RestroomViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restroom)

        val textView = findViewById<TextView>(R.id.textView).apply {
            text = viewModel.testString
        }
    }
}