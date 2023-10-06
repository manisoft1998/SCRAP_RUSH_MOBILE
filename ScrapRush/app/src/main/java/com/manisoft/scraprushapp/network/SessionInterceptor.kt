package com.manisoft.scraprushapp.network

import android.app.Activity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response

class SessionInterceptor(private val context: Activity) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val response = chain.proceed(originalRequest)

        // Check if the response indicates a session expiration
        if (response.code == 401) {
            // Perform logout or any other desired action
            CoroutineScope(Dispatchers.Main).launch {
                showSessionExpiredDialog()
            }
        }

        return response
    }

    private fun showSessionExpiredDialog() {
//        context.showAlertDialog {
//            setTitle("Session Expired")
//            setMessage("Your session has expired. Please log in to continue using this app.")
//            negativeButton("OK") { logout() }
//        }
    }

    private fun logout() {
        // Implement your logout logic here
//        KeyValues.sharedPreferencesClear()
//        context.googleSignOut()
//        val intent = Intent(context, LoginActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//        context.startActivity(intent)
//        context.finish()
    }
}
