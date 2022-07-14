package com.jayesh.finalyearproject.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.jayesh.finalyearproject.R

class DisplayImageActivity : AppCompatActivity() {
    private lateinit var ivDialog: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_image)
        ivDialog = findViewById(R.id.ivDialog)

        val url = intent.getStringExtra("image-url")
        Glide.with(this).load(url).into(ivDialog).view
    }
}