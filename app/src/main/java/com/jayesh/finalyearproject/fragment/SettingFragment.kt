package com.jayesh.finalyearproject.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.switchmaterial.SwitchMaterial
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.activity.MainActivity
import com.jayesh.finalyearproject.data.SavedData


class SettingFragment : Fragment() {
    private lateinit var smMode: SwitchMaterial
    lateinit var savedData: SavedData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_setting, container, false)
        smMode = view.findViewById(R.id.smMode)
        savedData = SavedData(requireContext())

        if (savedData.loadDarkModeState() == true) {
            smMode.isChecked = true
        }

        //adding on change listener on switch
        smMode.setOnCheckedChangeListener { _, ischecked ->
            if (ischecked) {
                savedData.setDarkModeState(true)
                restartApp()
            } else {
                savedData.setDarkModeState(false)
                restartApp()
            }

        }
        return view
    }
    private fun restartApp() {
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

}