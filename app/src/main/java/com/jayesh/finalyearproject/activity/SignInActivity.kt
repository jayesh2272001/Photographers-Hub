package com.jayesh.finalyearproject.activity

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.ButtonBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.data.SavedData

class SignInActivity : AppCompatActivity() {
    lateinit var savedData: SavedData
    lateinit var mtbMain: MaterialToolbar
    lateinit var tvDonHave: TextView
    lateinit var btnSignIn: Button
    lateinit var tetEmail: TextInputEditText
    lateinit var tetPass: TextInputEditText
    lateinit var auth: FirebaseAuth

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
        setContentView(R.layout.activity_sign_in)

        mtbMain = findViewById(R.id.mtbMain)
        tvDonHave = findViewById(R.id.tvDonHave)
        btnSignIn = findViewById(R.id.btnSignIn)
        tetEmail = findViewById(R.id.tetEmail)
        tetPass = findViewById(R.id.tetPass)
        auth = FirebaseAuth.getInstance()


        mtbMain.setNavigationOnClickListener {
            val intent = Intent(this, WelcomeAuthActivity::class.java)
            startActivity(intent)
            finish()
        }

        //todo:login work starts here
        btnSignIn.setOnClickListener {
            login(tetEmail.text.toString(), tetPass.text.toString())
        }

        //checking email for validity
        tetEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(tetEmail.text.toString())
                        .matches()
                ) {
                    btnSignIn.isEnabled = true
                } else {
                    btnSignIn.isEnabled = false
                    btnSignIn.error = "Invalid Email"
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })


        //setting spannable text colour as yellow
        val spannable = SpannableString(tvDonHave.text)
        spannable.setSpan(
            ForegroundColorSpan(getColor(R.color.yellow)),
            23,
            30,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tvDonHave.text = spannable

        tvDonHave.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun login(email: String, password: String) {
        val intent = Intent(this, MainActivity::class.java)
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    startActivity(intent)
                    this?.finish()
                    val user = auth.currentUser
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this, "login failed.", Toast.LENGTH_SHORT).show()

                }
            }
    }
}