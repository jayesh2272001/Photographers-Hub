package com.jayesh.finalyearproject.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.data.User
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class CurrentUserProfileActivity : AppCompatActivity() {
    lateinit var dbref: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var etName: EditText
    private lateinit var etExperience: EditText
    private lateinit var etLocation: EditText
    private lateinit var etAge: EditText
    private lateinit var etDes: EditText
    private lateinit var etEmail: EditText
    lateinit var profileImage: CircleImageView
    private lateinit var btUpdateProfile: Button
    lateinit var selectedImage: Uri
    private lateinit var storage: FirebaseStorage

    private var name: String? = null
    private var age: String? = null
    private var location: String? = null
    private var des: String? = null
    private var email: String? = null
    private var experience: String? = null
    private var profilepc: String? = null

    lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_user_profile)
        /*owner: jayesh shinde
        * date: 20 may*/
        etName = findViewById(R.id.et_first_name)
        etExperience = findViewById(R.id.et_experience)
        etLocation = findViewById(R.id.et_location)
        etAge = findViewById(R.id.et_age)
        etDes = findViewById(R.id.et_des)
        etEmail = findViewById(R.id.et_email)
        profileImage = findViewById(R.id.civProfileImage)
        btUpdateProfile = findViewById(R.id.bt_update_profile)
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()

        getProfileInfo()
        btUpdateProfile.setOnClickListener {
            //checkProfileChanges()
            val reference = storage.reference.child(Date().time.toString())
            reference.putFile(selectedImage).addOnCompleteListener() {
                if (it.isSuccessful) {
                    reference.downloadUrl.addOnSuccessListener { task ->
                        updateProfileEntirely(
                            etAge.text.toString(),
                            etEmail.text.toString(),
                            etExperience.text.toString(),
                            etLocation.text.toString(),
                            etName.text.toString(),
                            task.toString()
                        )
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                }
            }


        }

        profileImage.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {

            if (data.data != null) {
                selectedImage = data.data!!
                profileImage.setImageURI(selectedImage)
            }

        }
    }

    // Checking if the input in form is valid
    fun validateInput(): Boolean {
        if (etName.text.toString() == "") {
            etName.error = "Please Enter Name"
            return false
        }
        if (etExperience.text.toString() == "") {
            etExperience.error = "Please Enter Last Name"
            return false
        }
        if (etEmail.text.toString() == "") {
            etEmail.error = "Please Enter Email"
            return false
        }

        if (etDes.text.toString() == "") {
            etDes.error = "Please Enter Designation"
            return false
        }
        if (etLocation.text.toString() == "") {
            etLocation.error = "Please Enter Designation"
            return false
        }
        if (profileImage.toString() == "") {
            etDes.error = "Please Enter Designation"
            return false
        }
        // checking the proper email format
        if (!isEmailValid(etEmail.text.toString())) {
            etEmail.error = "Please Enter Valid Email"
            return false
        }

        return true
    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun getProfileInfo() {
        dbref = FirebaseDatabase.getInstance().getReference("users")
            .child(auth.currentUser?.uid.toString())
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    user = snapshot.getValue(User::class.java)!!
                    etName.setText(user.name)
                    etEmail.setText(user.email)
                    etLocation.setText(user.location)
                    etAge.setText(user.age)
                    etExperience.setText(user.experience)
                    Glide.with(this@CurrentUserProfileActivity).load(user?.profileImage)
                        .into(profileImage).view
                    //etDes.hint =user?.desc
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun checkProfileChanges(): Boolean {

        if (user.name.equals(etName.text)) {

        } else {
            name = etName.text.toString()
            return false
        }

        if (user.email.equals(etEmail.text)) {
            email = etEmail.text.toString()
            return false
        }

        if (user.age.equals(etAge.text)) {
            age = etAge.text.toString()
            return false
        }

        if (user.location.equals(etLocation.text)) {
            location = etLocation.text.toString()
            return false
        }

        /* if (user.des.equals(etDes.text)) {
             des = etDes.text.toString()
             return false
         }*/

        if (user.experience.equals(etExperience.text)) {
            experience = etExperience.text.toString()
            return false
        }

        /*   updateProfileEntirely(
               name!!,
               mono!!,
               location!!,
               age!!,
               experience!!,
               email!!,
               profileImage,
               des!!
           )*/
        return true
    }

    private fun updateProfileEntirely(
        age: String,
        email: String,
        experience: String,
        location: String,
        name: String,
        profileImage: String,
        //des: String
    ) {
        dbref = FirebaseDatabase.getInstance().getReference("users")
            .child(auth.currentUser?.uid.toString())


        val user = mapOf<String, String>(
            "age" to age,
            "email" to email,
            "experience" to experience,
            "location" to location,
            "name" to name,
            "profileImage" to profileImage,
        )
        dbref.updateChildren(user).addOnSuccessListener {
            Toast.makeText(this, "profile updated successfully", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Error while updating profile. Try again! ", Toast.LENGTH_SHORT)
                .show()
        }
    }
}