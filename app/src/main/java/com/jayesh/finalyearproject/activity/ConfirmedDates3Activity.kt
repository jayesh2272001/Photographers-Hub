package com.jayesh.finalyearproject.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.adapter.ConfirmedDates3Adapter
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmed_dates3)

        toolbar = findViewById(R.id.toolbar)
        rvConfirmedDates = findViewById(R.id.rvConfirmedDates)
        mdbRef = FirebaseDatabase.getInstance().reference
        dataList = ArrayList()
        val currId = FirebaseAuth.getInstance().currentUser?.uid.toString()

        //getting data from firebase realtime database
        mdbRef.child("users").child(currId).child("confirmed-dates")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
                        val data = postSnapshot.getValue(ConfirmedDate::class.java)
                        dataList.add(data!!)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i("Error", error.toString())
                }

            })

        confirmedDates3Adapter = ConfirmedDates3Adapter(this, dataList)
        rvConfirmedDates.layoutManager = LinearLayoutManager(this)
        rvConfirmedDates.adapter = confirmedDates3Adapter
    }
}