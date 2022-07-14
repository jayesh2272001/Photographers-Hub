package com.jayesh.finalyearproject.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.hbb20.CountryCodePicker
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.adapter.OnBoardingItemAdapter
import com.jayesh.finalyearproject.model.OnBoardingItem
import java.util.concurrent.TimeUnit

class WelcomeAuthActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var btnContinue: Button
    lateinit var etMoNo: EditText
    private lateinit var onBoardingItemAdapter: OnBoardingItemAdapter
    lateinit var llItemStateContainer: LinearLayout
    lateinit var rlProgressBar: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var linearLayout3: LinearLayout
    lateinit var countryCode: String
    lateinit var phoneNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_auth)

        auth = FirebaseAuth.getInstance()
        btnContinue = findViewById(R.id.btnContinue)
        etMoNo = findViewById(R.id.etMoNo)
        rlProgressBar = findViewById(R.id.rlProgressBar)
        progressBar = findViewById(R.id.progressBar)
        linearLayout3 = findViewById(R.id.linearLayout3)

        setOnBoardingItems()
        setUpIndicators()
        setUpCurrentIndicator(0)

        etMoNo.addTextChangedListener { value ->
            btnContinue.isEnabled = !(value.isNullOrEmpty() || value.length < 10)
        }

        btnContinue.setOnClickListener {
            countryCode = findViewById<CountryCodePicker>(R.id.ccp).selectedCountryCodeWithPlus
            //extracting phone number with country code
            phoneNumber = countryCode + etMoNo.text.toString()
            sendVerificationCode(phoneNumber)
            //process bar enable
            rlProgressBar.visibility = View.VISIBLE

        }
    }

    private fun setOnBoardingItems() {
        onBoardingItemAdapter = OnBoardingItemAdapter(
            listOf(
                OnBoardingItem(
                    onBoardingImage = R.drawable.on_boarding_01,
                    title = "Find Photographer",
                    description = "Explore Photographer of your choice"
                ),
                OnBoardingItem(
                    onBoardingImage = R.drawable.on_boarding_02,
                    title = "Hire Photographers",
                    description = "Hire the professionals who can work efficiently"
                ), OnBoardingItem(
                    onBoardingImage = R.drawable.on_boarding_03,
                    title = "Join Community",
                    description = "Join the large growing photographers community"
                )
            )
        )
        val vpOnBoarding = findViewById<ViewPager2>(R.id.vpOnBoarding)
        vpOnBoarding.adapter = onBoardingItemAdapter
        vpOnBoarding.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setUpCurrentIndicator(position)
            }
        })
        (vpOnBoarding.getChildAt(0) as RecyclerView).overScrollMode =
            RecyclerView.OVER_SCROLL_NEVER
    }

    private fun setUpIndicators() {
        llItemStateContainer = findViewById(R.id.llItemStateContainer)
        val indicator = arrayOfNulls<ImageView>(onBoardingItemAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicator.indices) {
            indicator[i] = ImageView(applicationContext)
            indicator[i]?.let {
                it.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext, R.drawable.indicators_inactive_background
                    )
                )
                it.layoutParams = layoutParams
                llItemStateContainer.addView(it)
            }
        }
    }

    private fun setUpCurrentIndicator(position: Int) {
        val childCount = llItemStateContainer.childCount
        for (i in 0 until childCount) {
            val imageView = llItemStateContainer.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicators_active_background
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicators_inactive_background
                    )
                )
            }
        }
    }


    /*date:  01 may 2022
    owner: jayesh shinde
    * */
    private fun sendVerificationCode(moNo: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(moNo)
            .setActivity(this)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setCallbacks(mCallBack)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val mCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                //process bar disable
                rlProgressBar.visibility = View.GONE
                val intent = Intent(this@WelcomeAuthActivity, OtpVerifyActivity::class.java)
                intent.putExtra("code", p0)
                intent.putExtra("mono", phoneNumber)
                intent.putExtra("token", p1)
                startActivity(intent)
                finish()
            }

            override fun onVerificationCompleted(p0: PhoneAuthCredential) {

            }

            override fun onVerificationFailed(p0: FirebaseException) {

            }

        }
}


