package com.jayesh.finalyearproject.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.data.SavedData
import java.util.*

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        //main begins


        //creating auth instance
        auth = FirebaseAuth.getInstance()
        Handler(Looper.myLooper()!!).postDelayed({
            if (auth.currentUser == null) {
                val intent = Intent(this, WelcomeAuthActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 3000) // 3000 is the delayed time in milliseconds.

    }
}