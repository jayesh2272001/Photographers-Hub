package com.jayesh.finalyearproject.activity

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.data.SavedData

class WelcomeAuthActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    lateinit var savedData: SavedData
    lateinit var ivLogo: ImageView
    lateinit var tvHeading: TextView
    lateinit var mbEmailSignUp: Button
    lateinit var tvSignIn: TextView

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        //checking saved data when app is loading and setting theme
        savedData = SavedData(this)
        if (savedData.loadDarkModeState() == true) {
            setTheme(R.style.Dark_FinalYearProject)
        } else {
            setTheme(R.style.Theme_FinalYearProject)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_auth)
        auth = Firebase.auth

        //binding view by id
        ivLogo = findViewById(R.id.ivLogo)
        tvHeading = findViewById(R.id.tvHeading)
        mbEmailSignUp = findViewById(R.id.mbEmailSignUp)
        tvSignIn = findViewById(R.id.tvSignIn)


        //setting on click listener to continue with email and password
        mbEmailSignUp.setOnClickListener {
            //continue to sign up  screen
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)

        }

        //setting spannable text colour as yellow
        val spannable = SpannableString(tvSignIn.text)
        spannable.setSpan(
            ForegroundColorSpan(getColor(R.color.yellow)),
            25,
            32,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tvSignIn.text = spannable


        tvSignIn.setOnClickListener {
            //continue to login / sign up screen
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }

}