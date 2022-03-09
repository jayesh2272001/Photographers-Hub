package com.jayesh.finalyearproject.data

import android.content.Context
import android.content.SharedPreferences

class SavedData(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("file", Context.MODE_PRIVATE)

    fun setDarkModeState(state: Boolean?) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("Dark", state!!)
        editor.apply()
    }

    fun loadDarkModeState(): Boolean? {
        val state = sharedPreferences.getBoolean("Dark", false)
        return (state)
    }
}