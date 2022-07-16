package com.jayesh.finalyearproject.activity

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
import com.jayesh.finalyearproject.adapter.ConfirmedDates3Adapter
import com.jayesh.finalyearproject.adapter.MyHiresAdapter
import com.jayesh.finalyearproject.adapter.NotificationAdapter
import com.jayesh.finalyearproject.model.ConfirmedDate
import com.jayesh.finalyearproject.model.Dates
import java.util.*

class ConfirmedDates3Activity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var rvConfirmedDates: RecyclerView
    private lateinit var mdbRef: DatabaseReference
    private lateinit var dataList: ArrayList<ConfirmedDate>
    private lateinit var confirmedDates3Adapter: ConfirmedDates3Adapter
    private lateinit var loteeProgressBar: RelativeLayout
    private lateinit var rlNoDataFound: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmed_dates3)

        toolbar = findViewById(R.id.toolbar)
        rvConfirmedDates = findViewById(R.id.rvConfirmedDates)
        mdbRef = FirebaseDatabase.getInstance().reference
        dataList = ArrayList()
        loteeProgressBar = findViewById(R.id.rlProgressBar)
        rlNoDataFound = findViewById(R.id.rlNoDataFound)
        val currId = FirebaseAuth.getInstance().currentUser?.uid.toString()

        setUpToolBar(toolbar)

        //getting data from firebase realtime database
        loteeProgressBar.visibility = View.VISIBLE
        mdbRef.child("users").child(currId).child("confirmed-dates")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
                        val data = postSnapshot.getValue(ConfirmedDate::class.java)
                        dataList.add(data!!)
                    }
                    if (dataList.isNotEmpty()) {
                        confirmedDates3Adapter = ConfirmedDates3Adapter(this@ConfirmedDates3Activity, dataList)
                        rvConfirmedDates.layoutManager = LinearLayoutManager(this@ConfirmedDates3Activity)
                        rvConfirmedDates.adapter = confirmedDates3Adapter
                        loteeProgressBar.visibility = View.GONE
                    } else {
                        loteeProgressBar.visibility = View.GONE
                        rlNoDataFound.visibility = View.VISIBLE
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i("Error", error.toString())
                }

            })


    }

    private fun setUpToolBar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Confirmed Dates by Photographers"
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