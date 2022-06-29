package com.jayesh.finalyearproject.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.activity.GetPaymentDetails
import com.jayesh.finalyearproject.activity.GetRecentWorkActivity
import com.jayesh.finalyearproject.activity.MainActivity
import com.jayesh.finalyearproject.activity.WelcomeAuthActivity
import com.jayesh.finalyearproject.data.SavedData


class SettingFragment : Fragment() {

    private lateinit var tvMultipleImages: TextView
    private lateinit var ivLogout: ImageView
    private lateinit var cvPaymentDetails: CardView
    private lateinit var cvTerms: CardView
    private lateinit var cvAboutUs: CardView
    private lateinit var cvHelp: CardView
    private lateinit var cvShare: CardView
    private lateinit var cvRateUs: CardView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        tvMultipleImages = view.findViewById(R.id.tvMultipleImages)
        ivLogout = view.findViewById(R.id.ivLogout)
        cvPaymentDetails = view.findViewById(R.id.cvPaymentDetails)
        cvTerms = view.findViewById(R.id.cvTerms)
        cvAboutUs = view.findViewById(R.id.cvAboutUs)
        cvHelp = view.findViewById(R.id.cvHelp)
        cvShare = view.findViewById(R.id.cvShare)
        cvRateUs = view.findViewById(R.id.cvRateUs)

        cvPaymentDetails.setOnClickListener {
            val intent = Intent(activity, GetPaymentDetails::class.java)
            startActivity(intent)
        }

        tvMultipleImages.setOnClickListener {
            startActivity(Intent(activity, GetRecentWorkActivity::class.java))
        }


        ivLogout.setOnClickListener {
            val alertDialog = AlertDialog.Builder(requireContext())
            alertDialog.setTitle("Log Out")
            alertDialog.setMessage("Do you want to log out?")
            alertDialog.setPositiveButton("Accept") { _, _ ->
                Firebase.auth.signOut()
                val intent = Intent(activity, WelcomeAuthActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
            alertDialog.setNegativeButton("Deny") { _, _ ->

            }
            alertDialog.create()
            alertDialog.show()
        }
        return view
    }

}