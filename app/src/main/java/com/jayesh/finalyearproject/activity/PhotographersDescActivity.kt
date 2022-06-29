package com.jayesh.finalyearproject.activity

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.compose.ui.window.Dialog
import androidx.room.Room
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.data.User
import com.jayesh.finalyearproject.database.PhotographersDatabase
import com.jayesh.finalyearproject.database.PhotographersEntity
import de.hdodenhof.circleimageview.CircleImageView

class PhotographersDescActivity : AppCompatActivity() {
    lateinit var rlProgressBar: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var tbPDesc: Toolbar
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
        cvRecentWork = findViewById(R.id.cvRecentWork)
        btnBookNow = findViewById(R.id.btnBookNow)
        auth = FirebaseAuth.getInstance()
        usersArrayList = arrayListOf<User>()

        getUserData(userForCheck)
        setUpToolBar(tbPDesc)

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
            val intent = Intent(this, CheckAvailActivity::class.java)
            val id: String = userForCheck
            val name: String = photographerName
            intent.putExtra("uid", id)
            intent.putExtra("name", name)
            startActivity(intent)
        }


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