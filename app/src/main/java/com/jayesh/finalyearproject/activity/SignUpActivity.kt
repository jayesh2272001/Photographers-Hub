package com.jayesh.finalyearproject.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Patterns
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.data.SavedData
import com.jayesh.finalyearproject.data.User


class SignUpActivity : AppCompatActivity() {
    lateinit var savedData: SavedData
    lateinit var mtbMain: MaterialToolbar
    lateinit var tvHaveAccount: TextView
    lateinit var etName: TextInputEditText
    lateinit var etMono: TextInputEditText
    lateinit var etEmail: TextInputEditText
    lateinit var etPass: TextInputEditText
    lateinit var etCPass: TextInputEditText
    lateinit var btnSignup: AppCompatButton
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
        setContentView(R.layout.activity_sign_up)

        //binding view
        mtbMain = findViewById(R.id.mtbMain)
        tvHaveAccount = findViewById(R.id.tvHaveAccount)
        etEmail = findViewById(R.id.etEmail)
        etName = findViewById(R.id.etName)
        etMono = findViewById(R.id.etMoNo)
        etPass = findViewById(R.id.etPass)
        etCPass = findViewById(R.id.etCPass)
        btnSignup = findViewById(R.id.btnSignUp)
        auth = FirebaseAuth.getInstance()


        //click listener on back button on appbar(toolbar)
        mtbMain.setNavigationOnClickListener {
            val intent = Intent(this, WelcomeAuthActivity::class.java)
            startActivity(intent)
            finish()
        }


        etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString())
                        .matches()
                ) {
                    btnSignup.isEnabled = true
                } else {
                    btnSignup.isEnabled = false
                    etEmail.error = "Invalid Email"
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        //setting spannable text colour as yellow
        val spannable = SpannableString(tvHaveAccount.text)
        spannable.setSpan(
            ForegroundColorSpan(getColor(R.color.yellow)),
            17,
            24,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tvHaveAccount.text = spannable

        //setting on click listener on sign in text
        tvHaveAccount.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        btnSignup.setOnClickListener {
            val name: String = etName.text.toString()
            val email: String = etEmail.text.toString()
            val password: String = etCPass.text.toString()
            val phone: String = etMono.text.toString()
            registerUser(name, email, password, phone)
        }

    }


    private fun registerUser(name: String, email: String, password: String, phone: String) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(OnCompleteListener<AuthResult?> { task ->
                if (task.isSuccessful) {
                    val user = User(
                        name,
                        email,
                        phone
                    )
                    FirebaseDatabase.getInstance().getReference("Users")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .setValue(user).addOnCompleteListener(OnCompleteListener<Void?> { task ->
                            if (task.isSuccessful) {
                                startActivity(Intent(this, MainActivity::class.java))

                            } else {
                                Toast.makeText(this, "fails", Toast.LENGTH_SHORT).show()
                            }
                        })
                } else {

                }
            })
    }

}