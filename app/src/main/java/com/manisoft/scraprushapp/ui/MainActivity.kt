package com.manisoft.scraprushapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.manisoft.scraprushapp.R
import com.manisoft.scraprushapp.databinding.ActivityMainBinding
import com.manisoft.scraprushapp.models.UpdateFCMTokenRequest
import com.manisoft.scraprushapp.mvvm.viewmodel.ScrapViewModel
import com.manisoft.scraprushapp.network.Resource
import com.manisoft.scraprushapp.utils.CheckAppUpdate
import com.manisoft.scraprushapp.utils.Constants
import com.manisoft.scraprushapp.utils.KeyValues
import com.manisoft.scraprushapp.utils.showToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(),CheckAppUpdate.UpdateCheckCallback {
    private lateinit var binding: ActivityMainBinding
    private lateinit var checkAppUpdate: CheckAppUpdate
    private val viewModel: ScrapViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkAppUpdate = CheckAppUpdate(this,this)
        setupNavigation()

        getFCMToken()
        subscribe()
    }

    private fun subscribe() {
        viewModel.updateFcmTokenResponse.observe(this) {
            when (it) {
                is Resource.Failure -> {
                    showToast(it.errorBody?.message ?: "")
                }

                is Resource.Loading -> {}

                is Resource.Success -> {
                    Log.d("TAG", "Fcm token status : ${it.value.message}")
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        checkAppUpdate.checkUpdate()

/*        if (intent.getBooleanExtra("isMyOrder", false)) {
            findNavController(R.id.hostFragment).navigate(R.id.action_homeFragment_to_myBookingFragment)
//            findNavController(R.id.hostFragment).navigate(R.id.myBookingFragment)
        }*/
    }

    private fun setupNavigation() {
        val navController = findNavController(R.id.hostFragment)
        binding.bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment, R.id.myBookingFragment, R.id.myAccountFragment -> showBottomNav()
                else -> hideBottomNav()
            }
        }
    }

    private fun showBottomNav() {
        binding.bottomNavigationView.visibility = View.VISIBLE
    }

    private fun hideBottomNav() {
        binding.bottomNavigationView.visibility = View.GONE
    }

    private fun getFCMToken() {
        FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task: Task<String?> ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            // Get new FCM registration token
            if (KeyValues.readString(Constants.FCM_TOKEN, "").isNullOrEmpty()) {
                updateTokenToServer(task.result)
            } else {
                if (KeyValues.readString(Constants.FCM_TOKEN, "") != task.result) {
                    updateTokenToServer(task.result)
                }
            }
            Log.d("TAG", task.result!!)
        }.addOnFailureListener { e: Exception ->
            Log.d("TAG", "onCreate: " + e.message)
        }
    }

    private fun updateTokenToServer(result: String?) {
        viewModel.updateFcmTokenResponse(KeyValues.authToken(), UpdateFCMTokenRequest(device_token = result ?: ""))
    }

    override fun onUpdateAvailable() {

    }

    override fun onUpdateNotAvailable() {

    }

}