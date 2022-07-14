package com.jayesh.finalyearproject.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.adapter.ShowRecentWorkAdapter
import com.jayesh.finalyearproject.data.User
import com.jayesh.finalyearproject.database.PhotographersDatabase
import com.jayesh.finalyearproject.database.PhotographersEntity
import de.hdodenhof.circleimageview.CircleImageView

class PhotographersDescActivity : AppCompatActivity() {

    private lateinit var recyclerViewDesc: RecyclerView
    private lateinit var imageUrls: java.util.ArrayList<String>
    private lateinit var mdbRef: DatabaseReference

    private lateinit var rlProgressBar: RelativeLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var tbPDesc: Toolbar
    private lateinit var civProfileImage: CircleImageView
    private lateinit var tvUserName: TextView
    private lateinit var tvUserAddress: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var fabCallUser: ImageView
    private lateinit var fabMessageUser: ImageView
    private lateinit var fabAddBookmark: ImageView
    private lateinit var btnBookNow: Button
    private lateinit var dbref: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var usersArrayList: ArrayList<User>
    private var profileImage: String? = null
    private lateinit var userForCheck: String
    private lateinit var photographerName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photographers_desc)
        //extracting extra from FetchUserAdapter
        userForCheck = intent.getStringExtra("userVal").toString()
        photographerName = intent.getStringExtra("userName").toString()


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
        recyclerViewDesc = findViewById(R.id.recyclerViewDesc)
        mdbRef = FirebaseDatabase.getInstance().reference
        btnBookNow = findViewById(R.id.btnBookNow)
        auth = FirebaseAuth.getInstance()
        usersArrayList = arrayListOf<User>()
        imageUrls = ArrayList()

        getUserData(userForCheck)
        setUpToolBar(tbPDesc)

        selectDefaultImages()

        //check for already bookmarked or not
        val photographersEntity = PhotographersEntity(
            userForCheck,
            photographerName
        )
        val checkBookmarked = DBAsyncTask(this, photographersEntity, 1).execute()
        val isBookmarked = checkBookmarked.get()
        if (isBookmarked) {
            fabAddBookmark.setImageDrawable(getDrawable(R.drawable.ic_is_bookmarked))
        } else {
            fabAddBookmark.setImageDrawable(getDrawable(R.drawable.ic_not_bookmarked))
        }

        //bookmark photographer
        fabAddBookmark.setOnClickListener {

            if (!DBAsyncTask(this, photographersEntity, 1).execute().get()) {
                val async = DBAsyncTask(this, photographersEntity, 2).execute()
                val result = async.get()

                if (result) {
                    Toast.makeText(
                        this,
                        "${photographerName?.capitalize()} added to Bookmarked!",
                        Toast.LENGTH_SHORT
                    ).show()

                    fabAddBookmark.setImageDrawable(getDrawable(R.drawable.ic_is_bookmarked))
                } else {
                    Toast.makeText(this, "Error occurred, try again.", Toast.LENGTH_SHORT).show()
                }
            } else {
                val async = DBAsyncTask(this, photographersEntity, 3).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(
                        this,
                        "${photographerName?.capitalize()} removed from Bookmarked",
                        Toast.LENGTH_SHORT
                    ).show()
                    fabAddBookmark.setImageDrawable(getDrawable(R.drawable.ic_not_bookmarked))
                }
            }
        }


        //calling photographer
        fabCallUser.setOnClickListener {
            val u = Uri.parse("tel:" + usersArrayList[0].mono)
            val i = Intent(Intent.ACTION_DIAL, u)
            startActivity(i)
        }

        //chat with photographer
        fabMessageUser.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            val id: String = userForCheck
            val name: String = photographerName
            intent.putExtra("uid", id)
            intent.putExtra("name", name)
            intent.putExtra("profileImage", profileImage)
            startActivity(intent)
        }

        //booking photographer
        btnBookNow.setOnClickListener {
            var name: String? = null
            mdbRef.child("users").child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .child("name")
                .get()
                .addOnSuccessListener {
                    name = it.value.toString()
                    Log.i("req name", name.toString())

                    val intent = Intent(this, CheckAvailActivity::class.java)
                    val id: String = userForCheck
                    val pName: String = photographerName
                    intent.putExtra("uid", id)
                    intent.putExtra("CurUserName",name)
                    intent.putExtra("name", pName)
                    startActivity(intent)
                }



        }


    }

    private fun selectDefaultImages() {
        mdbRef.child("users").child(userForCheck).child("recent_work")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
//                        Log.i("Recent work", "Existing work :$postSnapshot")

//                        val image = postSnapshot.getValue(ImageUrl::class.java)
                        imageUrls.add(postSnapshot.value.toString())
                    }
                    /* for (item in imageUrls) {
                         Log.i("Recent work", "Existing work :$item")
                     }*/

                    recyclerViewDesc.adapter =
                        ShowRecentWorkAdapter(this@PhotographersDescActivity, imageUrls)
                    recyclerViewDesc.layoutManager =
                        GridLayoutManager(this@PhotographersDescActivity, 3)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        //setting default images to recycler view


    }

    private fun getUserData(userForCheck: String) {
        dbref = FirebaseDatabase.getInstance().getReference("users").child(userForCheck)
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(User::class.java)
                    usersArrayList.add(user!!)
                    tvUserName.text = user.name
                    tvUserAddress.text = user.location
                    tvUserEmail.text = user.email
                    Glide.with(this@PhotographersDescActivity).load(user.profileImage)
                        .into(civProfileImage).view
                    profileImage = user.profileImage
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    /*Async Database*/
    class DBAsyncTask(
        var context: Context,
        val photographersEntity: PhotographersEntity,
        val mode: Int
    ) :
        AsyncTask<Void, Void, Boolean>() {

        val db =
            Room.databaseBuilder(context, PhotographersDatabase::class.java, "bookmark-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {
            /* Mode 1->check if photographer is in Bookmarks
            * Mode 2->Save the Photographer into DB as Bookmarked
            * Mode 3-> Remove the Photographer form Bookmark*/
            when (mode) {
                1 -> {
                    val photographer: PhotographersEntity? = db.photographersDao()
                        .getPhotographerById(photographersEntity.photographersId)
                    db.close()
                    return photographer != null
                }
                2 -> {
                    db.photographersDao().insertPhotographer(photographersEntity)
                    db.close()
                    return true
                }
                3 -> {
                    db.photographersDao().deletePhotographer(photographersEntity)
                    db.close()
                    return true
                }
                else -> return false
            }
        }
    }

    private fun setUpToolBar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}