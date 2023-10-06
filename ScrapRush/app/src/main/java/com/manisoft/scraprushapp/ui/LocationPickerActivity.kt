package com.manisoft.scraprushapp.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.LocationBias
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.manisoft.scraprushapp.utils.locationpickerutils.locationsmodel.GeocodingResult
import com.manisoft.scraprushapp.R
import com.manisoft.scraprushapp.databinding.ActivityLocationPickerBinding
import com.manisoft.scraprushapp.utils.Constants
import com.manisoft.scraprushapp.utils.ModelPreferenceManager
import com.manisoft.scraprushapp.utils.MultiPermissionCallback
import com.manisoft.scraprushapp.utils.PermissionUtil
import com.manisoft.scraprushapp.utils.hideKeyboard
import com.manisoft.scraprushapp.utils.locationpickerutils.LocationUtils
import com.manisoft.scraprushapp.utils.locationpickerutils.LocationUtils.checkGpsStatus
import com.manisoft.scraprushapp.utils.locationpickerutils.LocationUtils.locationPermissions
import com.manisoft.scraprushapp.utils.locationpickerutils.PlacePredictionAdapter
import com.manisoft.scraprushapp.utils.negativeButton
import com.manisoft.scraprushapp.utils.onTextChanged
import com.manisoft.scraprushapp.utils.positiveButton
import com.manisoft.scraprushapp.utils.showAlertDialog
import com.manisoft.scraprushapp.utils.showToast

class LocationPickerActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityLocationPickerBinding

    private lateinit var selectedGeoResult: GeocodingResult

    //map and search address reference
    private lateinit var placesClient: PlacesClient
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private var myGoogleMap: GoogleMap? = null

    //variables
    private val myAdapter = PlacePredictionAdapter()
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        PermissionUtil.getInstance(this)
        initMap()
        initPickUpSearchView()
        initRecyclerView()
        setClickListeners()
    }

    override fun onResume() {
        super.onResume()
        checkLocationPermission()
    }

    private fun setClickListeners() {
        binding.btnConfirm.setOnClickListener {
            ModelPreferenceManager.put(selectedGeoResult, Constants.GEO_LOCATION)
            finish()
        }
        binding.btnLocateMe.setOnClickListener {
            getDeviceLocation()
        }
    }

    private fun initMap() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.myMap) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        placesClient = Places.createClient(this)
    }

    @SuppressLint("SetTextI18n")
    override fun onMapReady(googleMap: GoogleMap) {
        myGoogleMap = googleMap

        LocationUtils.checkLocationPermission(this)
        myGoogleMap?.isMyLocationEnabled = false
        myGoogleMap?.uiSettings?.isMyLocationButtonEnabled = false
        myGoogleMap?.uiSettings?.isCompassEnabled = false

        myGoogleMap?.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM), 1000, null)

        myGoogleMap?.setOnCameraIdleListener {
            binding.mapProgressBar.visibility = View.VISIBLE
            binding.mapProgressBar.isIndeterminate = true
            LocationUtils.getLocationFromGeoCoder(myGoogleMap!!.cameraPosition.target, this) {
                selectedGeoResult = it
                binding.mapProgressBar.visibility = View.GONE
                binding.mapProgressBar.isIndeterminate = false

                val addressList = it.formatted_address?.split(",")
                try {
                    when {
                        addressList?.isNotEmpty() == true && addressList.size == 1 -> {
                            binding.tvTitleAddress.text = addressList[0].trim()
                        }

                        addressList?.isNotEmpty() == true && addressList.size >= 2 -> {
                            binding.tvTitleAddress.text = addressList[1].trim()
                        }

                        else -> binding.tvTitleAddress.text = "Please select address"
                    }
                } catch (e: ArrayIndexOutOfBoundsException) {
                    e.printStackTrace()
                }
                binding.tvConfirmAddress.text = it.formatted_address
                binding.btnConfirm.isEnabled = true
            }
        }
        getDeviceLocation()
    }

    private fun getDeviceLocation() {
        try {
            mFusedLocationProviderClient?.getCurrentLocation(CurrentLocationRequest.Builder().setPriority(Priority.PRIORITY_HIGH_ACCURACY).build(), null)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val location = task.result
                    if (location != null) {
                        moveCamera(LatLng(task.result.latitude, task.result.longitude), DEFAULT_ZOOM)
                        Log.d("TAG", "getDeviceLocation: " + "Current Location" + "Latitude : ${location.latitude}" + "Longitude : ${location.longitude}")
                    } else {
                        getDeviceLocation()
                    }
                } else {
                    showToast(task.exception?.message ?: "")
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun moveCamera(latLng: LatLng, defaultZoom: Float) {
        myGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, defaultZoom))
        myGoogleMap?.clear()
        hideKeyboard()
    }

    companion object {
        const val DEFAULT_ZOOM = 18f
    }

    /* search address*/
    @SuppressLint("SetTextI18n")
    private fun initRecyclerView() {
        val mLayoutManager = LinearLayoutManager(this)
        binding.recyclerView.apply {
            adapter = myAdapter
            layoutManager = mLayoutManager
            addItemDecoration(DividerItemDecoration(this@LocationPickerActivity, mLayoutManager.orientation))
        }
        myAdapter.onPlaceClickListener = {
            LocationUtils.geocodePlaceAndDisplay(it, this) { result ->
                val addressList = result.formatted_address?.split(",")
                try {
                    when {
                        addressList?.isNotEmpty() == true && addressList.size == 1 -> {
                            binding.tvTitleAddress.text = addressList[0].trim()
                        }

                        addressList?.isNotEmpty() == true && addressList.size >= 2 -> {
                            binding.tvTitleAddress.text = addressList[1].trim()
                        }

                        else -> binding.tvTitleAddress.text = "Please select address"
                    }
                } catch (e: ArrayIndexOutOfBoundsException) {
                    e.printStackTrace()
                }
                binding.tvConfirmAddress.text = result.formatted_address
                binding.icPin.visibility = View.VISIBLE
                binding.cvAddress.visibility = View.VISIBLE
                binding.btnLocateMe.visibility = View.VISIBLE
                moveCamera(LatLng(result.geometry?.location!!.latitude, result.geometry?.location!!.longitude), DEFAULT_ZOOM)
                binding.recyclerView.visibility = View.GONE

                binding.btnConfirm.isEnabled = true
                hideKeyboard()
            }
        }
    }

    private fun initPickUpSearchView() {
        binding.inputSearch.isFocusable = true
        binding.inputSearch.requestFocusFromTouch()

        binding.inputSearch.onTextChanged {
            binding.progressBar.isIndeterminate = true
            binding.icPin.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
            binding.cvAddress.visibility = View.GONE
            binding.btnLocateMe.visibility = View.GONE
            // Cancel any previous place prediction requests
            handler.removeCallbacksAndMessages(null)
            // Start a new place prediction request in 300 ms
            handler.postDelayed({ getPlacePredictions(it) }, 300)
        }
    }

    private fun getPlacePredictions(query: String) {

        val bias: LocationBias = RectangularBounds.newInstance(LatLng(22.458744, 88.208162),  // SW lat, lng
            LatLng(22.730671, 88.524896) // NE lat, lng
        )

        // Create a new programmatic Place Autocomplete request in Places SDK for Android
        val newRequest = FindAutocompletePredictionsRequest.builder().setLocationBias(bias).setTypeFilter(TypeFilter.ESTABLISHMENT).setQuery(query).setCountries("IN").build()

        // Perform autocomplete predictions request
        placesClient.findAutocompletePredictions(newRequest).addOnSuccessListener { response ->
            val predictions = response.autocompletePredictions
            myAdapter.setPredictions(predictions)
            binding.progressBar.isIndeterminate = false
//            binding.viewAnimator.displayedChild = if (predictions.isEmpty()) 0 else 1
        }.addOnFailureListener { exception: Exception? ->
            binding.progressBar.isIndeterminate = false
            if (exception is ApiException) {
                Log.e("TAG", "Place not found: " + exception.statusCode)
            }
        }
    }

    private fun checkLocationPermission() {
        if (LocationUtils.checkLocationPermission(this)) {
            checkLocationIsOn()
        } else {
            PermissionUtil.askMultiplePermissions(locationPermissions, permissionCallback)
        }
    }

    private val permissionCallback = object : MultiPermissionCallback {
        override fun onAccepted() {
            checkLocationIsOn()
        }

        override fun onDenied() {
            showToast("Please grant location permission to continue this app")
        }
    }

    private fun checkLocationIsOn() {
        val locationRequest = LocationRequest.create().apply {
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        val locationSettingsRequest = LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()
        val settingsClient = LocationServices.getSettingsClient(this@LocationPickerActivity)

        settingsClient.checkLocationSettings(locationSettingsRequest).addOnSuccessListener {
            if (it.locationSettingsStates?.isLocationPresent == true) {
                getDeviceLocation()
            }
        }.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                    launcher.launch(intentSenderRequest)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d("LocationPicker", sendEx.message ?: "")

                    sendEx.printStackTrace()
                }
            }
        }
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            getDeviceLocation()
        } else {
            Log.d("TAG", "Cancelled: ")
            showAlertDialog {
                setTitle("Location Service")
                setMessage("To continue, turn on device location, which uses Google\'s location service")
                positiveButton {
                    checkLocationIsOn()
                    it.dismiss()
                }
                negativeButton {
                    it.dismiss()
                }
            }
        }
    }
}