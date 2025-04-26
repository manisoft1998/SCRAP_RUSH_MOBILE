package com.manisoft.scraprushapp.utils.locationpickerutils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.manisoft.scraprushapp.utils.locationpickerutils.locationsmodel.GeocodingResult
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.gson.GsonBuilder
import com.manisoft.scraprushapp.R
import org.json.JSONArray
import org.json.JSONException

object LocationUtils {
    val locationPermissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

    fun geocodePlaceAndDisplay(placePrediction: AutocompletePrediction, context: Context, onLocationSelected: (GeocodingResult) -> Unit) {
        val queue: RequestQueue = Volley.newRequestQueue(context)

        // Construct the request URL
        val apiKey =  context.resources.getString(R.string.google_api_key)
        val requestURL = "https://maps.googleapis.com/maps/api/geocode/json?place_id=${placePrediction.placeId}&key=$apiKey"

        // Use the HTTP request URL for Geocoding API to get geographic coordinates for the place
        val request = JsonObjectRequest(Request.Method.GET, requestURL, null, { response ->
            try {
                // Inspect the value of "results" and make sure it's not empty
                val results: JSONArray = response.getJSONArray("results")
                if (results.length() == 0) {
                    Log.w("TAG", "No results from geocoding request.")
                    return@JsonObjectRequest
                }
                val gson = GsonBuilder().registerTypeAdapter(LatLng::class.java, LatLngAdapter()).create()

                // Use Gson to convert the response JSON object to a POJO
                val result: GeocodingResult = gson.fromJson(results.getString(0), GeocodingResult::class.java)
                onLocationSelected.invoke(result)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, { error ->
            Log.d("TAG", "Request failed", error)
        })

        // Add the request to the Request queue.
        queue.add(request)
    }

    fun getLocationFromGeoCoder(target: LatLng, context: Context, onLocationSelected: (GeocodingResult) -> Unit) {
        val queue: RequestQueue = Volley.newRequestQueue(context)

        // Construct the request URL
//        val apiKey =  context.resources.getString(R.string.google_api_key)
        val apiKey =  "AIzaSyCA9ZR1itYfZLbC0_GrmNhiOLgHA5UeRnI"
        val requestURL = "https://maps.googleapis.com/maps/api/geocode/json?latlng=${target.latitude},${target.longitude}&key=$apiKey"

        // Use the HTTP request URL for Geocoding API to get geographic coordinates for the place
        val request = JsonObjectRequest(Request.Method.GET, requestURL, null, { response ->
            try {
                // Inspect the value of "results" and make sure it's not empty
                val results: JSONArray = response.getJSONArray("results")
                if (results.length() == 0) {
                    Log.w("TAG", "No results from geocoding request.")
                    return@JsonObjectRequest
                }
                val gson = GsonBuilder().registerTypeAdapter(LatLng::class.java, LatLngAdapter()).create()

                // Use Gson to convert the response JSON object to a POJO
                val result: GeocodingResult = gson.fromJson(results.getString(0), GeocodingResult::class.java)
                onLocationSelected.invoke(result)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, { error ->
            Log.d("TAG", "Request failed", error)
        })

        // Add the request to the Request queue.
        queue.add(request)
    }


    fun checkLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context,
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun checkGpsStatus(context: Context, gpsStatus: (Boolean) -> Unit) {
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            checkGPSEnable(context)
        } else {
            gpsStatus.invoke(true)
        }
    }

    private fun checkGPSEnable(context: Context) {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setMessage("Your GPS seems to be disabled, do you want to enable it?").setCancelable(false).setPositiveButton("Yes") { _, _ ->
                    context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }.setNegativeButton("No") { dialog, _ ->
                    dialog.cancel()
                }
        val alert = dialogBuilder.create()
        alert.show()
    }

    /*
        fun getLocationFromGeoCoder(context: Context, target: LatLng) {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addressList = geocoder.getFromLocation(target.latitude, target.longitude, 1)
            if (addressList?.size > 0) {
    //            binding.progressBarTxt.visibility = View.GONE
                val address = addressList[0]
                val addressLine = address.getAddressLine(0)
                val city = address.locality
                val state = address.adminArea
                val country = address.countryName
                val postalCode = address.postalCode
                val knownName = address.featureName

                selectedAddress =
                    "Lat : " + nowLocation.latitude + "\n" +
                            "Lng : " + nowLocation.longitude + "\n" + addressLine

                binding.tvConfirmAddress.text = selectedAddress

            }
        }
    */
}