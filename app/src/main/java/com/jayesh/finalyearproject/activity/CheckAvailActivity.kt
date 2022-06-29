package com.jayesh.finalyearproject.activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.addTextChangedListener
import androidx.navigation.NavOptions
import com.google.firebase.database.*
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.model.Dates
import com.jayesh.finalyearproject.model.UPI
import java.util.*

class CheckAvailActivity : AppCompatActivity() {
    private lateinit var tbCheckAvail: Toolbar
    private lateinit var layout01: RelativeLayout
    private lateinit var photographersUid: String
    private lateinit var photographersName: String
    private lateinit var etDate: EditText
    private lateinit var btnCheckDates: Button
    private lateinit var mdbRef: DatabaseReference
    private lateinit var dateList: ArrayList<Dates>
    private lateinit var upiList: ArrayList<UPI>

    /*layout 02*/
    private lateinit var layout02: RelativeLayout
    private lateinit var btnPayment: Button
    private lateinit var tvDetails: TextView

    var conditionVariable: String? = null


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_availl)

        photographersUid = intent.getStringExtra("uid").toString()
        photographersName = intent.getStringExtra("name").toString()
        layout01 = findViewById(R.id.layout01)
        tbCheckAvail = findViewById(R.id.tbCheckAvail)
        etDate = findViewById(R.id.etDate)
        btnCheckDates = findViewById(R.id.btnCheckDates)
        mdbRef = FirebaseDatabase.getInstance().reference
        dateList = ArrayList()
        upiList = ArrayList()
        setUpToolBar(tbCheckAvail)

        //declaration for layout 02
        layout02 = findViewById(R.id.layout02)
        btnPayment = findViewById(R.id.btnPayment)
        tvDetails = findViewById(R.id.tvDetails)


        //check for date if it is extended by yes if yes then proceed for payment
        val id = photographersUid.toString()
        mdbRef.child("users").child(id).child("dates")
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
                        val date = postSnapshot.getValue(Dates::class.java)
                        dateList.add(date!!)
                        Log.i("condition variable ", date.date.toString())
                        conditionVariable = date.date.toString()
                        if (date.date.toString().contains("yes", true)) {
                            layout01.visibility = View.GONE
                            layout02.visibility = View.VISIBLE
                        } else {
                            layout01.visibility = View.VISIBLE
                            layout02.visibility = View.GONE
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })


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
            val date = etDate.text.toString()
            val dateObject = Dates(date)

            if (!etDate.text.isNullOrBlank()) {
                //add data to the photographers database
                mdbRef.child("users").child(photographersUid).child("dates").push()
                    .setValue(dateObject).addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "We will let you know once the $photographersName confirm date is available",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                etDate.text.clear()

            } else {
                Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show()
            }
        }


        /*Layout 02
        * starting actual payment gateway
        * */

        tvDetails.text =
            "$photographersName is available on your requested date. To make payment, Click the button below"

        btnPayment.setOnClickListener {
            mdbRef.child("users").child(id).child("payment_details")
                .addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (postSnapshot in snapshot.children) {
                            val upi = postSnapshot.getValue(UPI::class.java)
                            upiList.add(upi!!)
                            Log.i("required Upi variable ", upi.upiId.toString())

                            /*google pay integration starts*/
                            val GOOGLE_PAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user"
                            val GOOGLE_PAY_REQUEST_CODE = 123

                            val uri: Uri = Uri.Builder()
                                .scheme("upi")
                                .authority("pay")
                                .appendQueryParameter("pa", upi.upiId.toString())
                                .appendQueryParameter("pn", photographersName)
                                //.appendQueryParameter("mc", "your-merchant-code")
                                //.appendQueryParameter("tr", "your-transaction-ref-id")
                                .appendQueryParameter("tn", "Payment to $photographersName")
                                .appendQueryParameter("am", "01")
                                .appendQueryParameter("cu", "INR")
                                //.appendQueryParameter("url", "your-transaction-url")
                                .build()
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data = uri
                            intent.setPackage(GOOGLE_PAY_PACKAGE_NAME)
                            startActivityForResult(intent, GOOGLE_PAY_REQUEST_CODE)
                            /*google pay integration ends*/
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
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