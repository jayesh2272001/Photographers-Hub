package com.jayesh.finalyearproject.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.data.User
import com.jayesh.finalyearproject.model.Hires
import com.jayesh.finalyearproject.model.UPI
import com.jayesh.finalyearproject.model.Users
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import org.json.JSONObject
import java.time.LocalDateTime
import java.util.*


class ConfirmedDateInfoActivity : AppCompatActivity(), PaymentResultWithDataListener {
    private lateinit var pId: String
    private lateinit var pName: String
    private lateinit var cDate: String
    private lateinit var uDate: String
    private var upiId: String? = null
    private var payableAmount: String? = null
    private lateinit var mdbRef: DatabaseReference

    private lateinit var toolbar: Toolbar
    private lateinit var tvDate: TextView
    private lateinit var tvUserName: TextView
    private lateinit var tvChangedOn: TextView
    private lateinit var btnCheckProfile: Button
    private lateinit var btnProceedToPay: Button
    private lateinit var tvPaymentInfo: TextView
    val UPI_PAYMENT = 100

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmed_date_info)

        toolbar = findViewById(R.id.toolbar)
        tvDate = findViewById(R.id.tvDate)
        tvUserName = findViewById(R.id.tvUserName)
        tvChangedOn = findViewById(R.id.tvChangedOn)
        btnCheckProfile = findViewById(R.id.btnCheckProfile)
        btnProceedToPay = findViewById(R.id.btnProceedToPay)
        tvPaymentInfo = findViewById(R.id.tvPaymentInfo)
        mdbRef = FirebaseDatabase.getInstance().reference

        setUpToolBar(toolbar)
        pId = intent.getStringExtra("pid").toString()
        pName = intent.getStringExtra("p-name").toString()
        cDate = intent.getStringExtra("c-date").toString()
        uDate = intent.getStringExtra("update-date").toString()

        getWages(pId)

        tvDate.text = cDate
        tvChangedOn.text = "on: $uDate"
        tvUserName.text = "Confirmed by : $pName"
        btnCheckProfile.text = "View $pName's Profile"


        //visit profile
        btnCheckProfile.setOnClickListener {
            val intent = Intent(this, PhotographersDescActivity::class.java)
            intent.putExtra("userVal", pId)
            intent.putExtra("userName", pName)
            startActivity(intent)
        }

        btnProceedToPay.setOnClickListener {
            var upi: String? = null
            mdbRef.child("users").child(pId).child("payment_details")
                .addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (i in snapshot.children) {
                            val test = i.getValue(UPI::class.java)
                            upi = test?.upiId.toString()
                        }
                        Log.i("UPI", upi.toString())
                        upiId = upi

                        //starts
                        val transId = (0..1000000000000).random()
                        Log.i("UPI", transId.toString())
                        val am = payableAmount
                        Log.i("UPI", "amount $am")


                        /*Razor-pay integration*/
                        paynow(upi, am, pName, cDate)


                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.i("error", error.toString())
                    }
                })
        }

    }

    private fun paynow(upi: String?, amount: String?, pName: String, cDate: String) {
        val co = Checkout()
        val am = amount?.toInt()
        val com = (am!! * 0.05)
        val finalAmount = (am + com)
        Log.i("payment info", finalAmount.toInt().toString())
        Log.i("payment info", com.toString())


        try {
            val option = JSONObject()
            option.put("name", "Photographers Hub")
            option.put("Description", "Money will be transferred to $pName shortly")
            option.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
            option.put("currency", "INR")
            option.put("amount", "${finalAmount.toInt()}00")


            val prefill = JSONObject()
            prefill.put("email", "")
            prefill.put("contact", "")
            option.put("prefill", prefill)

            co.open(this, option)


        } catch (e: Exception) {
            Toast.makeText(this, "Error in payment", Toast.LENGTH_SHORT).show()
            Log.e("Exception", e.toString())
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPaymentSuccess(transactionId: String?, paymentData: PaymentData) {
        Toast.makeText(this, "Payment successful", Toast.LENGTH_SHORT).show()

        var status = "SUCCESS"
        Toast.makeText(this, transactionId.toString(), Toast.LENGTH_SHORT).show()
        Log.i("payment name", pName)
        Log.i("payment pid", pId)
        Log.i("payment date", cDate)
        Log.i("payment upi", upiId.toString())
        Log.i("payment status", status)
        Log.i("payment tid", transactionId.toString())
        Log.i("payment paymentId", paymentData.paymentId.toString())
        Log.i("payment userContact", FirebaseAuth.getInstance().currentUser?.phoneNumber.toString())
        Log.i("payment amount", payableAmount.toString())
        Log.i("payment email", paymentData.userEmail.toString())



        pushInfo(
            pName,
            pId,
            cDate,
            upiId.toString(),
            status,
            transactionId.toString(),
            paymentData.paymentId,
            payableAmount.toString(),
            FirebaseAuth.getInstance().currentUser?.phoneNumber.toString(),
        )
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        Log.e("Payment Error", p1.toString())
        Toast.makeText(this, "Error in payment", Toast.LENGTH_SHORT).show()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun pushInfo(
        pName: String,
        pId: String,
        cDate: String,
        upiId: String,
        status: String,
        transactionId: String,
        paymentId: String?,
        payableAmount: String,
        userContact: String?,
    ) {
        FirebaseDatabase.getInstance().getReference("users")
            .child(pId)
            .get()
            .addOnSuccessListener {
                FirebaseDatabase.getInstance().getReference("users")
                    .child(pId)
                    .child("payment_details")
                    .get()
                    .addOnSuccessListener { dataForManasisUpi ->
                        val upiData = dataForManasisUpi.getValue(UPI::class.java)
                        val manasisData = it.getValue(User::class.java)
                        val hiresObj = Hires(
                            manasisData?.name.toString(),
                            manasisData?.uid.toString(),
                            cDate,
                            upiData?.upiId.toString(),
                            status,
                            transactionId,
                            paymentId.toString(),
                            payableAmount,
                            manasisData?.mono.toString(),
                            LocalDateTime.now().toString()
                        )
                        //adding values to firebase
                        FirebaseDatabase.getInstance().getReference("users")
                            .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                            .child("my-hires")
                            .push()
                            .setValue(hiresObj)
                            .addOnSuccessListener {
                                //code to remove date from current users database (confirmed-dates)
                                Log.i("payment Value", "added successfully")

                                FirebaseDatabase.getInstance().getReference("users")
                                    .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                                    .child("confirmed-dates")
                                    .orderByChild("date")
                                    .equalTo(cDate)
                                    .addListenerForSingleValueEvent(
                                        object : ValueEventListener {
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                for (dataSnapshot in snapshot.children) {
                                                    dataSnapshot.ref.removeValue()
                                                }
                                                createMyBookedDates(
                                                    pName,
                                                    pId,
                                                    cDate,
                                                    upiId,
                                                    status,
                                                    transactionId,
                                                    paymentId.toString(),
                                                    payableAmount,
                                                    userContact.toString(),
                                                    LocalDateTime.now().toString()
                                                )
                                            }


                                            override fun onCancelled(error: DatabaseError) {
                                                Log.e("DB Error", error.toString())
                                            }
                                        })
                            }
                    }
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createMyBookedDates(
        pName: String,
        pId: String,
        cDate: String,
        upiId: String,
        status: String,
        transactionId: String,
        paymentId: String,
        payableAmount: String,
        userContact: String,
        toString2: String
    ) {


        FirebaseDatabase.getInstance().getReference("users")
            .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .get()
            .addOnSuccessListener {
                FirebaseDatabase.getInstance().getReference("users")
                    .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .child("payment_details")
                    .get().addOnSuccessListener { forUpiInfo ->
                        val dataForUpiOnly = forUpiInfo.getValue(UPI::class.java)
                        val currUserData = it.getValue(User::class.java)
                        val hiresObj = Hires(
                            currUserData?.name.toString(),
                            currUserData?.uid.toString(),
                            cDate,
                            dataForUpiOnly?.upiId.toString(),
                            status,
                            transactionId,
                            paymentId,
                            payableAmount,
                            currUserData?.mono.toString(),
                            LocalDateTime.now().toString()
                        )
                        FirebaseDatabase.getInstance().getReference("users")
                            .child(this.pId)
                            .child("my-booked-dates")
                            .push()
                            .setValue(hiresObj)
                            .addOnSuccessListener {
                                startActivity(
                                    Intent(
                                        this@ConfirmedDateInfoActivity,
                                        AboutDateActivity::class.java
                                    )
                                )
                                finish()
                            }
                    }


            }

    }


    private fun getWages(id: String) {
        FirebaseDatabase.getInstance().getReference("users")
            .child(id)
            .child("wages")
            .get()
            .addOnSuccessListener {
                btnProceedToPay.text = "Proceed to pay of ${it.value}"
                tvPaymentInfo.text = "Book now by paying the amount of ${it.value}"
                payableAmount = it.value.toString()
            }
    }

    private fun setUpToolBar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Date Info"
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