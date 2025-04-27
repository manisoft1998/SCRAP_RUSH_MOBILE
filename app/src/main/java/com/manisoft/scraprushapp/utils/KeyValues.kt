package com.manisoft.scraprushapp.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log


object KeyValues {
    private var mSharedPreferences: SharedPreferences? = null
    private var mEditor: SharedPreferences.Editor? = null

    fun init(context: Context) {
        if (mSharedPreferences == null) mSharedPreferences = context.getSharedPreferences(context.packageName, Activity.MODE_PRIVATE)
    }

    @SuppressLint("CommitPrefEdits")
    fun writeString(key: String, value: String) {
        mEditor = mSharedPreferences?.edit()
        mEditor?.putString(key, value)
        mEditor?.apply()
    }


    @SuppressLint("CommitPrefEdits")
    fun writeInt(key: String, value: Int) {
        mEditor = mSharedPreferences?.edit()
        mEditor?.putInt(key, value)
        mEditor?.apply()
    }

    @SuppressLint("CommitPrefEdits")
    fun writeLong(key: String, value: Long) {
        mEditor = mSharedPreferences?.edit()
        mEditor?.putLong(key, value)
        mEditor?.apply()
    }


    @SuppressLint("CommitPrefEdits")
    fun writeBoolean(key: String, value: Boolean) {
        mEditor = mSharedPreferences?.edit()
        mEditor?.putBoolean(key, value)
        mEditor?.apply()
    }

    fun readString(key: String, defValue: String): String? {
        return mSharedPreferences?.getString(key, defValue)
    }

    fun readInt(key: String, defValue: Int): Int? {
        return mSharedPreferences?.getInt(key, defValue)
    }

    fun readLong(key: String, defValue: Long): Long? {
        return mSharedPreferences?.getLong(key, defValue)
    }


    fun readBoolean(key: String, defValue: Boolean): Boolean? {
        return mSharedPreferences?.getBoolean(key, defValue)
    }

    fun sharedPreferencesClear() {
        mSharedPreferences?.edit()?.clear()?.apply()
    }


    fun saveFetchedLocation(lat: Double, lng: Double) {
        mEditor = mSharedPreferences?.edit()
        mEditor?.apply {
            putLong("lat_value", lat.toBits())
            putLong("lng_val", lng.toBits())
            Log.i("Tag", "LONG_BITS: " + " Lat: " + lat.toBits() + " Lng: " + lng.toBits())
        }?.apply()
    }

    fun getLatitude(): Double {
        return Double.fromBits(mSharedPreferences!!.getLong("lat_value", 1))

    }

    fun getLongitude(): Double {
        return Double.fromBits(mSharedPreferences!!.getLong("lng_val", 1))
    }

    fun authToken(): String {
        return KeyValues.readString(Constants.ACCESS_TOKEN, "") ?: ""
    }
}