package com.manisoft.scraprushapp.utils

import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

object PermissionUtil {
    private lateinit var mPermissions: Array<String>
    private lateinit var callback: MultiPermissionCallback
    private lateinit var activity: AppCompatActivity
    private lateinit var singlePermissionLauncher: ActivityResultLauncher<String>
    private lateinit var multiplePermissionLauncher: ActivityResultLauncher<Array<String>>

    fun getInstance(activity: AppCompatActivity) {
        this.activity = activity
        initPermissions()
    }

    private fun initPermissions() {
        //Ask multiple permissions
        multiplePermissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (mPermissions.size == 2 && permissions.size == 2)
                if (permissions.getValue(mPermissions[0]) && permissions.getValue(mPermissions[1])) callback.onAccepted() else callback.onDenied()


            /*         permissions.entries.forEach {
                         Log.d("DEBUG", "${it.key} = ${it.value}")
                         Toast.makeText(activity, "${it.key} = ${it.value}", Toast.LENGTH_SHORT).show()
         //                MapActivity().getDeviceLocation()
                     }*/
        }

        //Ask single permission
        singlePermissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                Toast.makeText(activity, "Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "Not granted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun askMultiplePermissions(permissions: Array<String>, callback: MultiPermissionCallback) {
        mPermissions = permissions
        multiplePermissionLauncher.launch(permissions)
        this.callback = callback
    }

    fun askSinglePermission(permissions: String) {
        singlePermissionLauncher.launch(permissions)
    }

}