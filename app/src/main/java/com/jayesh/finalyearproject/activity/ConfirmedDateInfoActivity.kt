package com.jayesh.finalyearproject.activity

import android.annotation.SuppressLint
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
import dev.shreyaspatil.easyupipayment.model.TransactionDetails
import java.util.*
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

                        val easyUpiPayment = EasyUpiPayment(this@ConfirmedDateInfoActivity) {
                            this.payeeVpa = "ksaiee263@okhdfcbank"
//                            this.payeeVpa = upi.toString()
                            this.payeeName = pName
                            this.payeeMerchantCode = "1823"
                            this.transactionId = "T$transId"
                            this.transactionRefId = "T$transId"
                            this.description = "Payment to $pName for Photographers hub"
                            this.amount = "1.00"
//                            this.amount = am.toString()
                        }
                        easyUpiPayment.startPayment()
                        easyUpiPayment.setPaymentStatusListener(
                            object : PaymentStatusListener {
                                override fun onTransactionCancelled() {
                                    Toast.makeText(
                                        this@ConfirmedDateInfoActivity,
                                        "Transaction is cancelled",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                override fun onTransactionCompleted(transactionDetails: TransactionDetails) {
                                    Log.i("Transaction details", transactionDetails.toString())
                                    Toast.makeText(
                                        this@ConfirmedDateInfoActivity,
                                        "$transactionDetails",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }


                            }
                        )


                        /*google pay integration starts*/

                        /* val uri: Uri = Uri.Builder()
                             .scheme("upi")
                             .authority("pay")
                             .appendQueryParameter("pa", upi.toString())
                             .appendQueryParameter("pn", pName)
                             //.appendQueryParameter("mc", "your-merchant-code")
                             //.appendQueryParameter("tr", "your-transaction-ref-id")
                             .appendQueryParameter("tn", "Payment to $pName")
                             .appendQueryParameter("am", "01")//change amount later
                             .appendQueryParameter("cu", "INR")
                             //.appendQueryParameter("url", "your-transaction-url")
                             .build()
                         val intent = Intent(Intent.ACTION_VIEW)
                         intent.data = uri
                         intent.setPackage(GOOGLE_PAY_PACKAGE_NAME)
                         startActivityForResult(intent, GOOGLE_PAY_REQUEST_CODE)*/
                        /*google pay integration ends*/


                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.i("error", error.toString())
                    }

                })


        }

    }


/*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("main ", "response $resultCode")
        if (resultCode == GOOGLE_PAY_REQUEST_CODE) {
            if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                if (data != null) {
                    val trxt: String = data.getStringExtra("response")!!
                    Log.e("UPI", "onActivityResult: $trxt")
                    val dataList: ArrayList<String> = ArrayList()
                    dataList.add(trxt)
                    upiPaymentDataOperation(dataList)
                } else {
                    Log.e("UPI", "onActivityResult: " + "Return data is null")
                    val dataList: ArrayList<String> = ArrayList()
                    dataList.add("nothing")
                    upiPaymentDataOperation(dataList)
                }
            } else {
                //when user simply back without payment

                //when user simply back without payment
                Log.e("UPI", "onActivityResult: " + "Return data is null")
                val dataList: ArrayList<String> = ArrayList()
                dataList.add("nothing")
                upiPaymentDataOperation(dataList)
            }
        }

    }
*/

/*
    private fun upiPaymentDataOperation(data: ArrayList<String>) {

        if (isConnectionAvailable(this)) {
            var str: String = data[0]
            Log.e("UPIPAY", "upiPaymentDataOperation: $str")
            var paymentCancel = ""
            if (str == null) str = "discard"
            var status = ""
            var approvalRefNo = ""
            val response = str.split("&").toTypedArray()
            for (i in response.indices) {
                val equalStr = response[i].split("=").toTypedArray()
                if (equalStr.size >= 2) {
                    if (equalStr[0].lowercase(Locale.getDefault()) == "Status".lowercase(Locale.getDefault())) {
                        status = equalStr[1].lowercase(Locale.getDefault())
                    } else if (equalStr[0].lowercase(Locale.getDefault()) == "ApprovalRefNo".lowercase(
                            Locale.getDefault()
                        ) || equalStr[0].lowercase(Locale.getDefault()) == "txnRef".lowercase(
                            Locale.getDefault()
                        )
                    ) {
                        approvalRefNo = equalStr[1]
                    }
                } else {
                    paymentCancel = "Payment cancelled by user."
                }
            }
            if (status == "success") {
                //Code to handle successful transaction here.
                Toast.makeText(this, "Transaction successful.", Toast.LENGTH_SHORT)
                    .show()
                Log.e("UPI", "payment successful: $approvalRefNo")
            } else if ("Payment cancelled by user." == paymentCancel) {
                Toast.makeText(this, "Payment cancelled by user.", Toast.LENGTH_SHORT)
                    .show()
                Log.e("UPI", "Cancelled by user: $approvalRefNo")
            } else {
                Toast.makeText(
                    this,
                    "Transaction failed.Please try again",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("UPI", "failed payment: $approvalRefNo")
            }
        } else {
            Log.e("UPI", "Internet issue: ")
            Toast.makeText(
                this,
                "Internet connection is not available. Please check and try again",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
*/

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

/*
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
*/
}