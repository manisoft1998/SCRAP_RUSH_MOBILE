package com.manisoft.scraprushapp.utils

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.IntentSender
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.manisoft.scraprushapp.R

class CheckAppUpdate(private val activity: AppCompatActivity, private val callback: UpdateCheckCallback) {
    private var appUpdateManager: AppUpdateManager? = null
    private var appUpdateInfoTask: Task<AppUpdateInfo>? = null

    interface UpdateCheckCallback {
        fun onUpdateAvailable()
        fun onUpdateNotAvailable()

    }

    // Declare an ActivityResultLauncher for the in-app update flow
    private val activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>

    init {
        appUpdateManager = AppUpdateManagerFactory.create(activity)
        appUpdateInfoTask = appUpdateManager?.appUpdateInfo

        activityResultLauncher = activity.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
            // handle callback
            if (result.resultCode != RESULT_OK) {
                Log.d("Update flow failed! Result code: ", result.resultCode.toString())
                // If the update is canceled or fails,
                // you can request to start the update again.
            }
            if (result.resultCode == RESULT_OK) {
                appUpdateManager!!.completeUpdate()
                activity.showToast("App updated successfully")
            } else if (result.resultCode == Activity.RESULT_CANCELED) {
                activity.finish()
            } else if (com.google.android.play.core.install.model.ActivityResult.RESULT_IN_APP_UPDATE_FAILED == result.resultCode) {
                callSnackBar("Error occurred please try again")
            }
        }

    }

    fun checkUpdate() {
        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask!!.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
            Log.d("Splash", "checkUpdate: " + appUpdateInfo.updateAvailability())
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {
                    callback.onUpdateAvailable()
                    appUpdateManager?.startUpdateFlowForResult(appUpdateInfo, activityResultLauncher, AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build())
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                }
            } else {
                callback.onUpdateNotAvailable()
            }
        }.addOnFailureListener { e ->
            // Handle failure here
            Log.e("Splash", "Error checking for app update: ${e.message}")
            callback.onUpdateNotAvailable()
        }

    }

    private fun callSnackBar(message: String?) {
        val snackBar = Snackbar.make(activity.findViewById(android.R.id.content), message!!, Snackbar.LENGTH_INDEFINITE).setAction("Retry") { _: View? ->
            checkUpdate()
        }
        snackBar.setActionTextColor(ContextCompat.getColor(activity, R.color.app_green))
        snackBar.show()
    }

}