package com.example.fishdemo

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val iv = findViewById<ImageView>(R.id.iv)
        iv.setImageDrawable(FishDrawable())
    }
}