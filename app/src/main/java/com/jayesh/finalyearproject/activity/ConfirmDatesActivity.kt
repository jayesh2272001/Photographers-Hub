package com.jayesh.finalyearproject.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.adapter.NotificationAdapter
import com.jayesh.finalyearproject.model.Dates
import java.util.*

class ConfirmDatesActivity : AppCompatActivity() {
    private lateinit var tbNotify: Toolbar
    private lateinit var rvNotifyActivity: RecyclerView
    private lateinit var mdbRef: DatabaseReference
    private lateinit var dateList: ArrayList<Dates>
    private lateinit var notificationAdapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_dates)


        tbNotify = findViewById(R.id.tbNotify)
        rvNotifyActivity = findViewById(R.id.rvNotifyActivity)
        mdbRef = FirebaseDatabase.getInstance().reference
        dateList = ArrayList()
        val currId = FirebaseAuth.getInstance().currentUser?.uid.toString()

        mdbRef.child("users").child(currId).child("dates")
            .addValueEventListener(object : ValueEventListener {

                @SuppressLint("NotifyDataSetChanged")
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
                                    notificationAdapter.notifyDataSetChanged()
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }
                            })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        notificationAdapter = NotificationAdapter(this, dateList)
        rvNotifyActivity.layoutManager = LinearLayoutManager(this)
        rvNotifyActivity.adapter = notificationAdapter
    }
}