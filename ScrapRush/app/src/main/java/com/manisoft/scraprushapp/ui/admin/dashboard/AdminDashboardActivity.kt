package com.manisoft.scraprushapp.ui.admin.dashboard

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.manisoft.scraprushapp.databinding.ActivityAdminDashboardBinding
import com.manisoft.scraprushapp.models.UpdateFCMTokenRequest
import com.manisoft.scraprushapp.mvvm.viewmodel.ScrapViewModel
import com.manisoft.scraprushapp.network.Resource
import com.manisoft.scraprushapp.ui.MainActivity
import com.manisoft.scraprushapp.ui.admin.orderlist.AdminOrderListActivity
import com.manisoft.scraprushapp.ui.admin.scraplist.ScrapListActivity
import com.manisoft.scraprushapp.utils.Constants
import com.manisoft.scraprushapp.utils.KeyValues
import com.manisoft.scraprushapp.utils.Utils
import com.manisoft.scraprushapp.utils.showToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class AdminDashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminDashboardBinding
    private val viewModel: ScrapViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getInstanceOfClasses()
        updateUI()
        setClickListeners()
        getFCMToken()
        subscribe()
    }

    private fun getInstanceOfClasses() {

    }

    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        binding.tvGreetings.text = "${Utils.showGreetings()}, ${KeyValues.readString(Constants.FULL_NAME, "") ?: "--"} !"
    }

    private fun setClickListeners() {
        binding.cvOrderList.setOnClickListener {
            Intent(this@AdminDashboardActivity, AdminOrderListActivity::class.java).apply {
                startActivity(this)
            }
        }

        binding.cvAddNewProduct.setOnClickListener {
            Intent(this@AdminDashboardActivity, ScrapListActivity::class.java).apply {
                startActivity(this)
            }
        }

        binding.cvShopLocation.setOnClickListener {
            Intent(this@AdminDashboardActivity, MainActivity::class.java).apply {
                startActivity(this)
            }
        }
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

    private fun subscribe() {
        viewModel.updateFcmTokenResponse.observe(this) {
            when (it) {
                is Resource.Failure -> {
                    showToast(it.errorBody?.message ?: "")
                }

                is Resource.Loading -> {

                }

                is Resource.Success -> {
                    Log.d("TAG", "Fcm token status : ${it.value.message}")
                }
            }
        }

    }

//    #fcc64d
}