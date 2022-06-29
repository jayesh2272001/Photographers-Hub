package com.jayesh.finalyearproject.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.model.Message
import com.jayesh.finalyearproject.model.UPI

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

        setUpToolBar(toolbar)
        etUpiId.addTextChangedListener { value ->
            btnSubmitUpi.isEnabled = !(value.isNullOrEmpty() || value.length < 6)
        }

        btnSubmitUpi.setOnClickListener {
            val uip = etUpiId.text.toString()
            val upiObject = UPI(uip)
            val curUser = FirebaseAuth.getInstance().currentUser?.uid.toString()
            mdbRef.child("users").child(curUser).child("payment_details").push()
                .setValue(upiObject).addOnSuccessListener {
                    etUpiId.text.clear()
                    Toast.makeText(this, "UPI Id updated successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
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