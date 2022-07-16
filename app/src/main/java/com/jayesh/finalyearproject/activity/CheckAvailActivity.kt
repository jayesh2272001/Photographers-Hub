package com.jayesh.finalyearproject.activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.model.Dates
import com.jayesh.finalyearproject.model.UPI
import java.util.*
import kotlin.collections.ArrayList

class CheckAvailActivity : AppCompatActivity() {
    private lateinit var rlProgressBar: RelativeLayout
    private lateinit var tbCheckAvail: Toolbar
    private lateinit var layout01: RelativeLayout
    private lateinit var photographersUid: String
    private lateinit var photographersName: String
    private lateinit var senderName: String
    private lateinit var etDate: EditText
    private lateinit var btnCheckDates: Button
    private lateinit var mdbRef: DatabaseReference
    private lateinit var dateList: ArrayList<Dates>
    private lateinit var upiList: ArrayList<UPI>
    private var dateId: String? = null
    lateinit var list: ArrayList<String>


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_availl)

        photographersUid = intent.getStringExtra("uid").toString()
        photographersName = intent.getStringExtra("name").toString()
        senderName = intent.getStringExtra("CurUserName").toString()

        rlProgressBar = findViewById(R.id.rlProgressBar)
        layout01 = findViewById(R.id.layout01)
        tbCheckAvail = findViewById(R.id.tbCheckAvail)
        etDate = findViewById(R.id.etDate)
        btnCheckDates = findViewById(R.id.btnCheckDates)
        mdbRef = FirebaseDatabase.getInstance().reference
        dateList = ArrayList()
        upiList = ArrayList()
        list = ArrayList()
        setUpToolBar(tbCheckAvail)
        //getting name of user


        dateId = (FirebaseAuth.getInstance().currentUser?.uid) + photographersUid

        //accessing calender
        etDate.setOnClickListener {
            val cal = Calendar.getInstance()
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH)
            val day = cal.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, myear, mmonth, mdayOfMonth ->
                    etDate.setText("$mdayOfMonth/$mmonth/$myear")
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }

        etDate.addTextChangedListener { value ->
            btnCheckDates.isEnabled = !(value.isNullOrEmpty() || value.length < 7)
        }

        btnCheckDates.setOnClickListener {
            rlProgressBar.visibility = View.VISIBLE
            var conditionVar = false
            val date = etDate.text.toString()
            val availability = false

            val dateObject = Dates(date, availability, photographersName, senderName)

            if (!etDate.text.isNullOrBlank()) {
                //add data to the photographers database

                mdbRef.child("users").child(photographersUid).child("dates").child(dateId!!)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (postSnapshot in snapshot.children) {
                                val allVials = postSnapshot.getValue(Dates::class.java)
                                val date2 = allVials?.date
                                if (date2.toString() == date) {
                                    conditionVar = true
                                    break
                                } else {
                                    conditionVar = false
                                }
                            }

                            if (!conditionVar) {
                                Log.i("Condition", conditionVar.toString())
                                mdbRef.child("users").child(photographersUid).child("dates")
                                    .child(dateId!!)
                                    .push()
                                    .setValue(dateObject).addOnSuccessListener {
                                        Toast.makeText(
                                            this@CheckAvailActivity,
                                            "We will let you know once the $photographersName confirm $date is available",
                                            Toast.LENGTH_LONG
                                        ).show()


                                        //adding values in current user for later access in [date check request sent fragment]
                                        mdbRef.child("users")
                                            .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                                            .child("date-chq-req-sent")
                                            .child(dateId!!)
                                            .push()
                                            .setValue(dateObject).addOnSuccessListener {
                                                Log.i("Data uploaded", "to current users table ")
                                            }
                                    }
                                //clearing the edittext after pushing data to firebase
                                rlProgressBar.visibility = View.GONE
                                etDate.text.clear()

                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("DB error",error.toString())
                        }
                    })
            } else {
                Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show()
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