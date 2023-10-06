package com.manisoft.scraprushapp.network

import android.content.Context
import com.manisoft.scraprushapp.utils.Utils
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class NetworkConnectionInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!Utils.isNetworkAvailable(context)) {
            throw NoConnectivityException()
        }
        return chain.proceed(chain.request())
    }

    class NoConnectivityException : IOException() {
        override val message: String
            get() = "Network connection not available."
    }

}