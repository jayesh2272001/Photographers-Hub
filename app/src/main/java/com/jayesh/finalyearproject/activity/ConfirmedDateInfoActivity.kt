package com.jayesh.finalyearproject.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.model.UPI
import dev.shreyaspatil.easyupipayment.EasyUpiPayment
import dev.shreyaspatil.easyupipayment.listener.PaymentStatusListener
import dev.shreyaspatil.easyupipayment.model.PaymentApp
import dev.shreyaspatil.easyupipayment.model.TransactionDetails
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random
import kotlin.random.Random.Default.nextInt


class ConfirmedDateInfoActivity : AppCompatActivity() {
    private lateinit var pId: String
    private lateinit var pName: String
    private lateinit var cDate: String
    private lateinit var uDate: String
    private var payableAmount: String? = null
    private lateinit var mdbRef: DatabaseReference

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

        tvDate = findViewById(R.id.tvDate)
        tvUserName = findViewById(R.id.tvUserName)
        tvChangedOn = findViewById(R.id.tvChangedOn)
        btnCheckProfile = findViewById(R.id.btnCheckProfile)
        btnProceedToPay = findViewById(R.id.btnProceedToPay)
        tvPaymentInfo = findViewById(R.id.tvPaymentInfo)
        mdbRef = FirebaseDatabase.getInstance().reference

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


                        //starts
                        val transId = (0..1000000000000).random()
                        Log.i("UPI", transId.toString())
                        val am = payableAmount?.toFloat()
                        Log.i("UPI", "amount $am")

                        //upi starts

                        if (am.toString().length > 0) {
                            var uri = Uri.parse("upi://pay").buildUpon()
                                .appendQueryParameter("pa", "9763570430@postbank")
                                .appendQueryParameter("mc", "")
                                .appendQueryParameter("tid", "t$transId")
                                .appendQueryParameter("tr", "tr$transId")
//                                .appendQueryParameter("pa", upi)
                                .appendQueryParameter("pn", pName)
                                .appendQueryParameter("tn", "pay to $pName")
                                .appendQueryParameter("am", "1.0")
                                .appendQueryParameter("cu", "INR")
                                .build();

                            var intent = Intent(Intent.ACTION_VIEW);
                            intent.data = uri
                            var intentChooser = Intent.createChooser(intent, "Pay with")

                            if (null != intentChooser.resolveActivity(getPackageManager())) {
                                startActivityForResult(intentChooser, UPI_PAYMENT);
                            } else {
                                Toast.makeText(
                                    this@ConfirmedDateInfoActivity,
                                    "No UPI app found, please install one to continue",
                                    Toast.LENGTH_SHORT
                                ).show();
                            }

                        } else {
                            Toast.makeText(
                                this@ConfirmedDateInfoActivity,
                                "Please enter amount",
                                Toast.LENGTH_SHORT
                            ).show();

                        }
                        //upi ends


                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.i("error", error.toString())
                    }
                })
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            UPI_PAYMENT -> {
                if (resultCode == Activity.RESULT_OK || resultCode == 1) {
                    var list: java.util.ArrayList<String>
                    if (data != null) {
                        list = ArrayList()
                        data.getStringExtra("resposne")?.let { list.add(it) }

                    } else {
                        list = ArrayList()
                        list.add("No Reponse")
                    }
                } else {
                    var list: java.util.ArrayList<String>
                    list = ArrayList()
                    list.add("Error")

                    checkPaymentStatus(list)
                }
            }
        }
    }

    private fun checkPaymentStatus(list: java.util.ArrayList<String>) {

        if (isConnectionAvailable(applicationContext)) {
            var str = list.get(0);
            Log.d("UPIPAY", "upiPaymentDataOperation: " + str);
            if (str == null)
                str = "discard"
            var status: String? = null;
            var txtnNo: String? = null;
            var paymentCancel: String? = null;

            var response = str.split("&")
            for (i in 0 until response.size) {
                val equalStr = response[i].split("=")
                if (equalStr.size >= 2) {
                    if (equalStr[0].toLowerCase() == "Status".toLowerCase()) {
                        status = equalStr[1].toLowerCase()
                    } else if (equalStr[0].toLowerCase() == "ApprovalRefNo".toLowerCase() || equalStr[0].toLowerCase() == "txnRef".toLowerCase()) {
                        txtnNo = equalStr[1]
                    }
                } else {
                    paymentCancel = "Payment cancelled by user."
                }
            }
            if (status.equals("success")) {
                //Code to handle successful transaction here.
                Toast.makeText(this, "Transaction successful.", Toast.LENGTH_SHORT).show();
                Log.d("UPI", "responseStr: " + txtnNo);
            } else if ("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Transaction failed.Please try again", Toast.LENGTH_SHORT)
                    .show();
            }

        } else {
            Toast.makeText(this, "Please check your network", Toast.LENGTH_SHORT).show();

        }

    }

    fun isConnectionAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val netInfo = connectivityManager.activeNetworkInfo
            if (netInfo != null && netInfo.isConnected
                && netInfo.isConnectedOrConnecting
                && netInfo.isAvailable
            ) {
                return true
            }
        }
        return false
    }
}