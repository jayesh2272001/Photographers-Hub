package com.jayesh.finalyearproject.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.compose.ui.input.key.Key.Companion.I
import androidx.core.widget.addTextChangedListener
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.model.Message
import com.jayesh.finalyearproject.model.UPI
import java.time.LocalDate

class GetPaymentDetails : AppCompatActivity() {
    private lateinit var etUpiId: EditText
    private lateinit var btnSubmitUpi: Button
    private lateinit var mdbRef: DatabaseReference
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_payment_details)

        etUpiId = findViewById(R.id.etUpiId)
        btnSubmitUpi = findViewById(R.id.btnSubmitUpi)
        mdbRef = FirebaseDatabase.getInstance().reference
        toolbar = findViewById(R.id.toolbar)

//        checkUPIPresent(FirebaseAuth.getInstance().currentUser?.uid.toString())

        setUpToolBar(toolbar)
        etUpiId.addTextChangedListener { value ->
            btnSubmitUpi.isEnabled = !(value.isNullOrEmpty() || value.length < 6)
        }




        btnSubmitUpi.setOnClickListener {
            val uip = etUpiId.text.toString()
            val upiObject = UPI(uip)
            val curUser = FirebaseAuth.getInstance().currentUser?.uid.toString()
            //setUPI(uip, curUser)
            mdbRef.child("users").child(curUser).child("payment_details").push()
                .setValue(upiObject).addOnSuccessListener {
                    etUpiId.text.clear()
                    Toast.makeText(
                        this@GetPaymentDetails,
                        "UPI Id updated successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this@GetPaymentDetails, MainActivity::class.java))
                    finish()
                }


        }
    }

    private fun setUPI(upi: String, curUser: String) {
        FirebaseDatabase.getInstance().getReference("users")
            .child(curUser)
            .child("payment_details")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (postSnapshot in snapshot.children) {
                            val test = postSnapshot.getValue(UPI::class.java)
                            Log.i("payment already", test?.upiId.toString())
                            Log.i("payment already upi", etUpiId.hint.toString())

                            if (test?.upiId.toString() == etUpiId.hint.toString()) {
                                Log.i("Payments", "Upi needs to remove and update the node")


                                //logic to remove the current upi id
                                FirebaseDatabase.getInstance().getReference("users")
                                    .child(curUser)
                                    .child("payment_details")
                                    .removeValue()
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            this@GetPaymentDetails,
                                            "removed",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        setNewUpi(upi)
                                    }


                            } else {
                                Log.i("Payments", "no need to remove and update the node")
                            }
                        }
                    } else {
                        //insert the upi for first time
                        val newUpi = etUpiId.text.toString()
                        val upiObject = UPI(newUpi)
                        mdbRef.child("users").child(curUser).child("payment_details").push()
                            .setValue(upiObject).addOnSuccessListener {
                                etUpiId.text.clear()
                                Toast.makeText(
                                    this@GetPaymentDetails,
                                    "UPI Id updated successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(
                                    Intent(
                                        this@GetPaymentDetails,
                                        MainActivity::class.java
                                    )
                                )
                                finish()
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i("Snapshot error ", error.toString())
                }

            })

    }

    private fun setNewUpi(upi: String) {
        val upiObject = UPI(upi)
        val curUser = FirebaseAuth.getInstance().currentUser?.uid.toString()


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

    private fun checkUPIPresent(uid: String) {
        FirebaseDatabase.getInstance().getReference("users")
            .child(uid)
            .child("payment_details")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (postSnapshot in snapshot.children) {
                            val upiObj = postSnapshot.getValue(UPI::class.java)
                            etUpiId.hint = upiObj?.upiId.toString()
                        }
                    } else {
                        Toast.makeText(
                            this@GetPaymentDetails,
                            "Enter your UPI ID.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i("Snapshot error ", error.toString())
                }

            })
    }

}