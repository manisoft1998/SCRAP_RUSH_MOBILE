package com.manisoft.scraprushapp.utils

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.widget.ImageView
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.MaterialDatePicker
import com.manisoft.scraprushapp.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Utils {
    fun showDatePicker(fm: FragmentManager, onDateSelected: (String) -> Unit = {}) {
        val datePicker = MaterialDatePicker.Builder.datePicker().build()
        datePicker.show(fm, "DatePicker")

        datePicker.addOnPositiveButtonClickListener {
            val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val date = dateFormatter.format(Date(it))
            onDateSelected.invoke(date)
        }
    }

    fun loadAssetImage(context: Context, imageView: ImageView, url: Int) {
        Glide.with(context).load(url).placeholder(R.drawable.product_placeholder).error(R.drawable.product_placeholder).into(imageView)
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun openWhatsAppChat(context: Context) {
        try {
            val packageManager = context.packageManager
            val whatsappIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$8148979742"))
            whatsappIntent.setPackage("com.whatsapp")

            if (whatsappIntent.resolveActivity(packageManager) != null) {
                // Add the greeting message to the WhatsApp intent
                context.startActivity(whatsappIntent)
            } else {
                // WhatsApp is not installed on the device, handle accordingly
                context.showToast("WhatsApp is not installed on the device")
            }
        } catch (e: Exception) {
            // Handle any exceptions that may occur
            context.showToast(e.message ?: "")
        }
    }

}