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
import com.google.firebase.database.ktx.getValue
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.adapter.MessageAdapter
import com.jayesh.finalyearproject.adapter.NotificationAdapter
import com.jayesh.finalyearproject.model.Dates
import java.util.ArrayList

class NotificationActivity : AppCompatActivity() {
    private lateinit var tbNotify: Toolbar
    private lateinit var rvNotifyActivity: RecyclerView
    private lateinit var mdbRef: DatabaseReference
    private lateinit var dateList: ArrayList<Dates>
    private lateinit var notificationAdapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        tbNotify = findViewById(R.id.tbNotify)
        rvNotifyActivity = findViewById(R.id.rvNotifyActivity)
        mdbRef = FirebaseDatabase.getInstance().reference
        dateList = ArrayList()

        val senderUid = FirebaseAuth.getInstance().currentUser?.uid.toString()


        mdbRef.child("users").child(senderUid).child("dates")
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
                        val date = postSnapshot.getValue(Dates::class.java)
                        val test =
                            Dates(
                                date?.date!!,
                                date.availability!!,
                                date.name!!,
                                date.senderName!!
                            )
                        dateList.add(test)

                    }
                    notificationAdapter.notifyDataSetChanged()
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