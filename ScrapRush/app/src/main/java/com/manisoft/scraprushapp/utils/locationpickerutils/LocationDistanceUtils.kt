package com.manisoft.scraprushapp.utils.locationpickerutils

import android.location.Location
import com.manisoft.scraprushapp.utils.Constants
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

object LocationDistanceUtils {
    private const val KM_TO_METER_CONVERSION_FACTOR = 1000
    private const val METERS_TO_KM_CONVERSION_FACTOR = 60
    private const val MILES_TO_KM_CONVERSION_FACTOR = 1.1515

    fun calculateDistanceInKm(location: Location): String {
        val distanceInMeters = distance(location.latitude, location.longitude, Constants.STORE_LATITUDE, Constants.STORE_LONGITUDE)
        return getFormattedDistanceString(convertToKm(distanceInMeters))
    }

    private fun getFormattedDistanceString(distanceInKm: Double): String {
        return if (distanceInKm < 1) {
            "${convertToMeter(distanceInKm)} M"
        } else {
            "${distanceInKm.roundToInt()} KM"
        }
    }

    private fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = sin(deg2rad(lat1)) * sin(deg2rad(lat2)) + cos(deg2rad(lat1)) * cos(deg2rad(lat2)) * cos(deg2rad(theta))
        dist = acos(dist)
        dist = rad2deg(dist)
        dist *= METERS_TO_KM_CONVERSION_FACTOR * MILES_TO_KM_CONVERSION_FACTOR
        return dist
    }

    private fun deg2rad(deg: Double): Double {
        return deg * PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / PI
    }

    private fun convertToMeter(distanceInKm: Double): Int {
        return (distanceInKm * KM_TO_METER_CONVERSION_FACTOR).roundToInt()
    }

    private fun convertToKm(distanceInMeters: Double): Double {
        return distanceInMeters / KM_TO_METER_CONVERSION_FACTOR
    }
}