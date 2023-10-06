package com.manisoft.scraprushapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.GsonBuilder

object ModelPreferenceManager {
    lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        preferences = context.getSharedPreferences(
            context.packageName, Context.MODE_PRIVATE
        )
    }

    fun <T> put(`object`: T, key: String) {
        val jsonString = GsonBuilder().create().toJson(`object`)
        preferences.edit().putString(key, jsonString).apply()
    }

    inline fun <reified T> get(key: String): T? {
        val value = preferences.getString(key, null)
        return GsonBuilder().create().fromJson(value, T::class.java)
    }
}