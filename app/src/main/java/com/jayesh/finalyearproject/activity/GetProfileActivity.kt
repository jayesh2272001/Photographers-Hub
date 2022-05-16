package com.jayesh.finalyearproject.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.data.User
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class GetProfileActivity : AppCompatActivity() {
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
    lateinit var circleImageView: CircleImageView
    lateinit var selectedImage: Uri
    private lateinit var dialog: AlertDialog.Builder


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
        btnSubmit = findViewById(R.id.btnSubmit)
        circleImageView = findViewById(R.id.civProfileImage)


        dialog = AlertDialog.Builder(this)
            .setMessage("updating profile...")
            .setCancelable(false)


        circleImageView.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }
//        var ref = databaseReference.reference.child("profile")

        btnSubmit.setOnClickListener {
            if (circleImageView == null) {
                Toast.makeText(this, "please select profile image", Toast.LENGTH_SHORT).show()
            }
            val reference = storage.reference.child(Date().time.toString())
            reference.putFile(selectedImage).addOnCompleteListener() {
                if (it.isSuccessful) {
                    reference.downloadUrl.addOnSuccessListener { task ->
                        WriteNewProfile(
                            etName.text.toString(),
                            etEmail.text.toString(),
                            etAge.text.toString(),
                            etExperience.text.toString(),
                            etLocation.text.toString(),
                            task.toString()
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

    private fun WriteNewProfile(
        name: String,
        email: String,
        age: String,
        experience: String,
        location: String,
        profileImage: String

    ) {
        val user = User(name, email, age, experience, location, profileImage)
        database.child("users").child(auth.currentUser?.uid!!).setValue(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile created successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error creating profile", Toast.LENGTH_SHORT).show()
            }


    }


}