package com.kazuki.fakecameraclient.repos

import android.content.Context
import android.content.SharedPreferences

class SharedPrefRepository(private val context: Context) {
    val sharedPref: SharedPreferences = context.getSharedPreferences(SP_FILE_KEY, Context.MODE_PRIVATE)

    fun put(key: String,value: String) {
        with(sharedPref.edit()) {
            putString(key, value)
            apply()
        }
    }

    fun getString(key: String): String {
        return sharedPref.getString(key, "")!!
    }

    companion object {
        const val SP_FILE_KEY = "SP_KEY"
    }
}