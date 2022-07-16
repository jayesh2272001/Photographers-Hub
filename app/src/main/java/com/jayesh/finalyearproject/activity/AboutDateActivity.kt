package com.jayesh.finalyearproject.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.jayesh.finalyearproject.R

class AboutDateActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var cvDateToConfirm: CardView
    private lateinit var cvPreviouslyRequestedDates: CardView
    private lateinit var cvConfirmedDates: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_date)

        toolbar = findViewById(R.id.toolbar)
        cvDateToConfirm = findViewById(R.id.cvDateToConfirm)
        cvPreviouslyRequestedDates = findViewById(R.id.cvPreviouslyRequestedDates)
        cvConfirmedDates = findViewById(R.id.cvConfirmedDates)
        setUpToolBar(toolbar)

        cvDateToConfirm.setOnClickListener {
            startActivity(Intent(this, ConfirmDatesActivity::class.java))
        }

        cvPreviouslyRequestedDates.setOnClickListener {
            startActivity(Intent(this, PreviouslyReqDatesActivity::class.java))
        }

        cvConfirmedDates.setOnClickListener {
            startActivity(Intent(this, ConfirmedDates3Activity::class.java))
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

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}