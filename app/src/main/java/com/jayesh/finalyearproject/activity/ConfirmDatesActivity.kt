package com.jayesh.finalyearproject.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.adapter.MyHiresAdapter
import com.jayesh.finalyearproject.adapter.NotificationAdapter
import com.jayesh.finalyearproject.model.Dates
import java.util.*

class ConfirmDatesActivity : AppCompatActivity() {
    private lateinit var tbNotify: Toolbar
    private lateinit var rlProgressBar: RelativeLayout
    private lateinit var rlNoDataFound: RelativeLayout
    private lateinit var rvNotifyActivity: RecyclerView
    private lateinit var mdbRef: DatabaseReference
    private lateinit var dateList: ArrayList<Dates>
    private lateinit var notificationAdapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_dates)

        tbNotify = findViewById(R.id.tbNotify)
        rlProgressBar = findViewById(R.id.rlProgressBar)
        rlNoDataFound = findViewById(R.id.rlNoDataFound)
        rvNotifyActivity = findViewById(R.id.rvNotifyActivity)
        mdbRef = FirebaseDatabase.getInstance().reference
        dateList = ArrayList()
        val currId = FirebaseAuth.getInstance().currentUser?.uid.toString()

        setUpToolBar(tbNotify)
        rlProgressBar.visibility = View.VISIBLE
        mdbRef.child("users").child(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .child("dates")
            .addValueEventListener(object : ValueEventListener {


                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
                        val userToken = postSnapshot.key
                        Log.i("Confirm Dates ", userToken.toString())

                        mdbRef.child("users")
                            .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                            .child("dates").child(userToken.toString())

                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for (postSnapshot2 in snapshot.children) {
                                        val allVials = postSnapshot2.getValue(Dates::class.java)
                                        val date2 = allVials?.date
                                        val test = Dates(
                                            date2!!,
                                            allVials.availability!!,
                                            allVials.name!!,
                                            allVials.senderName!!
                                        )
                                        dateList.add(test)
                                    }

                                    if (dateList.isNotEmpty()) {
                                        notificationAdapter = NotificationAdapter(this@ConfirmDatesActivity, dateList)
                                        rvNotifyActivity.layoutManager = LinearLayoutManager(this@ConfirmDatesActivity)
                                        rvNotifyActivity.adapter = notificationAdapter
                                        rlProgressBar.visibility = View.GONE
                                    } else {
                                        rlProgressBar.visibility = View.GONE
                                        rlNoDataFound.visibility = View.VISIBLE
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.e("DB Error", error.toString())
                                }
                            })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("DB Error", error.toString())
                }

            })


    }

    private fun setUpToolBar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Select date to confirm"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            startActivity(Intent(this, AboutDateActivity::class.java))
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}