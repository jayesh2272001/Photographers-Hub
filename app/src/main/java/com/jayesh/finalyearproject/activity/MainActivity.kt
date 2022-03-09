package com.jayesh.finalyearproject.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.data.SavedData

class MainActivity : AppCompatActivity() {
    private lateinit var smMode: SwitchMaterial
    lateinit var savedData: SavedData
    lateinit var btnSignOut: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        //checking saved data when app is loading and setting theme
        savedData = SavedData(this)
        if (savedData.loadDarkModeState() == true) {
            setTheme(R.style.Dark_FinalYearProject)
        } else {
            setTheme(R.style.Theme_FinalYearProject)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        smMode = findViewById(R.id.smMode)
        btnSignOut = findViewById(R.id.btnSignOut)

        if (savedData.loadDarkModeState() == true) {
            smMode.isChecked = true
        }

        //adding on change listener on switch
        smMode.setOnCheckedChangeListener { _, ischecked ->
            if (ischecked) {
                savedData.setDarkModeState(true)
                restartApp()
            } else {
                savedData.setDarkModeState(false)
                restartApp()
            }

        }

        btnSignOut.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(this, WelcomeAuthActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    private fun restartApp() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


}