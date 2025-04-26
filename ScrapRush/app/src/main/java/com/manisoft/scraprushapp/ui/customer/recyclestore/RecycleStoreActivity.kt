package com.manisoft.scraprushapp.ui.customer.recyclestore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.manisoft.scraprushapp.R
import com.manisoft.scraprushapp.databinding.ActivityRecycleStoreBinding
import com.manisoft.scraprushapp.ui.customer.locationpicker.LocationPickerActivity
import com.manisoft.scraprushapp.utils.Constants
import com.manisoft.scraprushapp.utils.locationpickerutils.LocationUtils

class RecycleStoreActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityRecycleStoreBinding

    private var myGoogleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecycleStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initMap()
        setClickListeners()
    }

    private fun initMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.myMap) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }


    private fun setClickListeners() {
        binding.ivBacKIcon.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        myGoogleMap = googleMap

        LocationUtils.checkLocationPermission(this)
        myGoogleMap?.isMyLocationEnabled = false
        myGoogleMap?.uiSettings?.isMyLocationButtonEnabled = false
        myGoogleMap?.uiSettings?.isCompassEnabled = false
        myGoogleMap?.uiSettings?.isZoomControlsEnabled = true

        myGoogleMap?.animateCamera(CameraUpdateFactory.zoomTo(LocationPickerActivity.DEFAULT_ZOOM), 1000, null)

        /*        // Create a MarkerOptions object
                val markerOptions = MarkerOptions().position(LatLng(11.214037, 78.191530)).title("Scrap Rush Recycle Store")

                // Add the marker to the map and get the Marker object
                val marker = myGoogleMap?.addMarker(markerOptions)

                // Optionally, move the camera to the marker
                marker?.let {
                    myGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(it.position, LocationPickerActivity.DEFAULT_ZOOM))
                }*/

        // Add marker with location name
        val location = LatLng(Constants.STORE_LATITUDE, Constants.STORE_LONGITUDE)
        val markerOptions = MarkerOptions().position(location).title("Scrap Rush").snippet("Recycle Store")
        val marker = myGoogleMap?.addMarker(markerOptions)

        // Set the info window to be displayed automatically
        marker?.showInfoWindow()
        marker?.let {
            myGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(it.position, 17f))
        }
    }

}