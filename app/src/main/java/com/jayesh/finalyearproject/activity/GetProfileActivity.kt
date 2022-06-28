package com.jayesh.finalyearproject.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.data.User
import com.jayesh.finalyearproject.fragment.HomeFragment
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*


class GetProfileActivity() : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    lateinit var databaseReference: FirebaseDatabase
    lateinit var etName: EditText
    lateinit var etEmail: EditText
    lateinit var etAge: EditText
    lateinit var etExperience: EditText
    lateinit var etLocation: EditText
    lateinit var btnSubmit: Button
    lateinit var rlProgressBar: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var circleImageView: CircleImageView
    lateinit var selectedImage: Uri
    lateinit var mono: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_profile)

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference
        storage = FirebaseStorage.getInstance()
        databaseReference = FirebaseDatabase.getInstance()
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etAge = findViewById(R.id.etAge)
        etExperience = findViewById(R.id.etExperience)
        etLocation = findViewById(R.id.etLocation)
        progressBar = findViewById(R.id.progressBar)
        rlProgressBar = findViewById(R.id.rlProgressBar)
        btnSubmit = findViewById(R.id.btnSubmit)
        circleImageView = findViewById(R.id.civProfileImage)
        mono = intent.getStringExtra("mono").toString()


        circleImageView.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }

        btnSubmit.setOnClickListener {
            rlProgressBar.visibility = View.VISIBLE
            val reference = storage.reference.child(Date().time.toString())
            reference.putFile(selectedImage).addOnCompleteListener {
                if (it.isSuccessful) {
                    reference.downloadUrl.addOnSuccessListener { task ->
                        writeNewProfile(
                            etName.text.toString(),
                            etEmail.text.toString(),
                            etAge.text.toString(),
                            etExperience.text.toString(),
                            etLocation.text.toString(),
                            task.toString(),
                            auth.currentUser?.uid.toString(),
                            mono
                        )
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (data.data != null) {
                selectedImage = data.data!!
                circleImageView.setImageURI(selectedImage)
            }
        }
    }

    private fun writeNewProfile(
        name: String,
        email: String,
        age: String,
        experience: String,
        location: String,
        profileImage: String,
        uid: String,
        mono: String
    ) {
        val user = User(name, email, age, experience, location, profileImage, uid, mono)
        database.child("users").child(auth.currentUser?.uid!!).setValue(user)
            .addOnSuccessListener {
                rlProgressBar.visibility = View.GONE
                Toast.makeText(this, "Profile created successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomeFragment::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error creating profile", Toast.LENGTH_SHORT).show()
            }
    }


}