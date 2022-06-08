package com.jayesh.finalyearproject.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.adapter.FetchUserAdapter
import com.jayesh.finalyearproject.data.User
import de.hdodenhof.circleimageview.CircleImageView
import java.util.ArrayList
import kotlin.collections.contains

class PhotographersDescActivity : AppCompatActivity() {
    lateinit var rlProgressBar: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var tbPDesc: androidx.appcompat.widget.Toolbar
    lateinit var civProfileImage: CircleImageView
    lateinit var tvUserName: TextView
    lateinit var tvUserAddress: TextView
    lateinit var tvUserEmail: TextView
    lateinit var fabCallUser: FloatingActionButton
    lateinit var fabMessageUser: FloatingActionButton
    lateinit var fabAddBookmark: FloatingActionButton
    lateinit var cvRecentWork: CardView
    lateinit var btnBookNow: Button
    lateinit var dbref: DatabaseReference
    private lateinit var auth: FirebaseAuth
    lateinit var usersArrayList: ArrayList<User>
    var photographerName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photographers_desc)
        //extracting extra from FetchUserAdapter
        val userForCheck = intent.getStringExtra("userVal")

        rlProgressBar = findViewById(R.id.rlProgressBar)
        progressBar = findViewById(R.id.progressBar)
        tbPDesc = findViewById(R.id.tbPDesc)
        civProfileImage = findViewById(R.id.civProfileImage)
        tvUserName = findViewById(R.id.tvUserName)
        tvUserAddress = findViewById(R.id.tvUserAddress)
        tvUserEmail = findViewById(R.id.tvUserEmail)
        fabCallUser = findViewById(R.id.fabCallUser)
        fabMessageUser = findViewById(R.id.fabMessageUser)
        fabAddBookmark = findViewById(R.id.fabAddBookmark)
        cvRecentWork = findViewById(R.id.cvRecentWork)
        btnBookNow = findViewById(R.id.btnBookNow)
        auth = FirebaseAuth.getInstance()

        usersArrayList = arrayListOf<User>()
        getUserData(userForCheck.toString())

        //calling photographer
        fabCallUser.setOnClickListener {
            val u = Uri.parse("tel:" + usersArrayList[0].mono)
            val i = Intent(Intent.ACTION_DIAL, u)
            startActivity(i)
        }

        //booking photographer
        btnBookNow.setOnClickListener {
            bookPhotographerWithId(userForCheck, photographerName)
        }
    }

    private fun getUserData(userForCheck: String) {
        dbref = FirebaseDatabase.getInstance().getReference("users").child(userForCheck)
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Log.i("firebase reset", "Got required user ${snapshot}")

                    val user = snapshot.getValue(User::class.java)
                    usersArrayList.add(user!!)
                    tvUserName.text = user.name
                    photographerName = user.name
                    tvUserAddress.text = user.location
                    tvUserEmail.text = user.email
                    Glide.with(this@PhotographersDescActivity).load(user.profileImage)
                        .into(civProfileImage).view
                    //Toast.makeText(activity, "$snapshot", Toast.LENGTH_SHORT).show()


                    //rvMain.adapter = FetchUserAdapter(requireContext(), usersArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun bookPhotographerWithId(photographersId: String?, photographersName: String?) {
        Toast.makeText(this, "$photographersName is hired ", Toast.LENGTH_SHORT).show()
    }
}