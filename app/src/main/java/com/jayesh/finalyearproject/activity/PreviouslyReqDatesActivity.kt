package com.jayesh.finalyearproject.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
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
import com.jayesh.finalyearproject.adapter.NotificationAdapter
import com.jayesh.finalyearproject.adapter.PreviousDatesAdapter
import com.jayesh.finalyearproject.model.Dates
import com.jayesh.finalyearproject.model.PreviousDates
import java.util.ArrayList

class PreviouslyReqDatesActivity : AppCompatActivity() {
    private lateinit var tbNotify: Toolbar
    private lateinit var rlProgressBar: RelativeLayout
    private lateinit var rvNotifyActivity: RecyclerView
    private lateinit var mdbRef: DatabaseReference
    private lateinit var dateList: ArrayList<PreviousDates>
    private lateinit var notificationAdapter: PreviousDatesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_previously_req_dates)

        tbNotify = findViewById(R.id.tbNotify)
        rlProgressBar = findViewById(R.id.rlProgressBar)
        rvNotifyActivity = findViewById(R.id.rvNotifyActivity)
        mdbRef = FirebaseDatabase.getInstance().reference
        dateList = ArrayList()
        val currId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        setUpToolBar(tbNotify)

        mdbRef.child("users").child(currId).child("date-chq-req-sent")
            .addValueEventListener(object : ValueEventListener {

                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {

                        val userToken = postSnapshot.key

                        Log.i("req Dates", userToken.toString())

                        val newId = userToken?.replace(currId, "")
                        Log.i("req Dates", newId.toString())

                        mdbRef.child("users")
                            .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                            .child("date-chq-req-sent").child(userToken.toString())
                            .addValueEventListener(object : ValueEventListener {

                                override fun onDataChange(snapshot: DataSnapshot) {

                                    for (postSnapshot2 in snapshot.children) {
                                        val allVials = postSnapshot2.getValue(Dates::class.java)
                                        val date2 = allVials?.date
                                        val avail = allVials?.availability
                                        val name = allVials?.name
                                        val test = PreviousDates(date2!!, name.toString(), avail!!)
                                        dateList.add(test)
                                    }
                                    notificationAdapter.notifyDataSetChanged()
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.e("error", error.toString())
                                }
                            })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("error", error.toString())
                }

            })
        rlProgressBar.visibility = View.VISIBLE
        notificationAdapter = PreviousDatesAdapter(this, dateList)
        rvNotifyActivity.layoutManager = LinearLayoutManager(this)
        rvNotifyActivity.adapter = notificationAdapter
        rlProgressBar.visibility = View.GONE
    }

    fun getName(newId: String): String? {
        var name: String? = null
        mdbRef.child("users").child(newId)
            .child("name")
            .get()
            .addOnSuccessListener {
                name = it.value.toString()
                Log.i("req name", name.toString())

            }
        return name
    }

    private fun setUpToolBar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Previously Requested Dates"
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