package com.jayesh.finalyearproject.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
}