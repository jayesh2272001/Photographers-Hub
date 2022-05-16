package com.jayesh.finalyearproject.activity
/*
* date: 03 may
* owner: jayesh Shinde */

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.jayesh.finalyearproject.R
import java.util.concurrent.TimeUnit


class OtpVerifyActivity : AppCompatActivity() {

    val database = Firebase.database
    private lateinit var auth: FirebaseAuth
    lateinit var etOtp: EditText
    lateinit var btnVerifyContinue: Button
    lateinit var txtEnterDesc: TextView
    lateinit var txtDontReceive: TextView

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verify)

        etOtp = findViewById(R.id.etOtp)
        btnVerifyContinue = findViewById(R.id.btnVerifyContinue)
        txtDontReceive = findViewById(R.id.txtDontReceive)
        txtEnterDesc = findViewById(R.id.txtEnterDesc)
        auth = FirebaseAuth.getInstance()
        val myRef = database.getReference("users")

        //getting extra string from welcomeActivity
        val code = intent.getStringExtra("code")
        val mono = intent.getStringExtra("mono")
        val mresendToken: ForceResendingToken? = intent.getParcelableExtra("token")
        txtEnterDesc.text = mono

        etOtp.addTextChangedListener { value ->
            btnVerifyContinue.isEnabled = !(value.isNullOrEmpty() || value.length < 6)
        }
        //setting click listener on button verify and continue
        btnVerifyContinue.setOnClickListener {
            verifyCode(code.toString(), etOtp.text.toString(), myRef)
        }

        txtDontReceive.setOnClickListener {
            resendVerificationCode(mono.toString(), mresendToken!!)
        }
        /*
        * date: 03 may 2022*/

        //setting spannable text colour as yellow
        val spannable = SpannableString(txtEnterDesc.text)
        spannable.setSpan(
            ForegroundColorSpan(getColor(R.color.yellow)),
            0,
            13,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        txtEnterDesc.text = spannable

    }


    private fun verifyCode(authCode: String, etOtp: String, myRef: DatabaseReference) {
        val credentials = PhoneAuthProvider.getCredential(authCode, etOtp)
        signInWithCredentials(credentials, myRef)
    }

    private fun signInWithCredentials(credentials: PhoneAuthCredential, myRef: DatabaseReference) {
        auth.signInWithCredential(credentials)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    //checking if the user already fill up profile
                    // Read from the database
                    // [START read_message]
                    myRef.child(auth.currentUser?.uid!!).get()
                        .addOnSuccessListener {
                            Log.i("firebase", "Got value ${it.value}")
                            //intent user towards the main activity
                            if (it.value != null) {
                                Toast.makeText(this, "login successfully", Toast.LENGTH_SHORT)
                                    .show()
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            } else {
                                Log.e("firebase", "Error getting data")
                                startActivity(Intent(this, GetProfileActivity::class.java))
                                finish()
                            }
                        }.addOnFailureListener {

                        }
                    // [END read_message]

                } else {
                    Toast.makeText(
                        this,
                        "You have entered wrong OTP, please re-enter correct OTP",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    // [START resend_verification]
    private fun resendVerificationCode(
        phoneNumber: String,
        token: ForceResendingToken
    ) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(mCallBack) // OnVerificationStateChangedCallbacks
            .setForceResendingToken(token) // ForceResendingToken from callbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val mCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onCodeSent(p0: String, p1: ForceResendingToken) {
                Toast.makeText(
                    this@OtpVerifyActivity,
                    "OTP Resent on your mobile number",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onVerificationCompleted(p0: PhoneAuthCredential) {

            }

            override fun onVerificationFailed(p0: FirebaseException) {

            }

        }


/*    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            val intent = Intent(this, GetProfileActivity::class.java)
            startActivity(intent)
            finish()
        }

    }*/
}