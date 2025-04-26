package com.manisoft.scraprushapp.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.manisoft.scraprushapp.R
import com.manisoft.scraprushapp.databinding.OrderTimeLineRowItemBinding
import com.manisoft.scraprushapp.models.OrderedItem
import com.manisoft.scraprushapp.models.StatusHistory
import java.net.URLEncoder
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object Utils {
    fun showDatePicker(fm: FragmentManager, onDateSelected: (String) -> Unit = {}) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = Date()
        val stringCurrentDate = dateFormat.format(currentDate)
        val enableDate = dateFormat.parse(stringCurrentDate)

        val constraintsBuilder: CalendarConstraints.Builder = CalendarConstraints.Builder().setValidator(DateValidatorPointForward.from(enableDate!!.time))
        val datePicker = MaterialDatePicker.Builder.datePicker().setCalendarConstraints(constraintsBuilder.build()).setSelection(Calendar.getInstance().timeInMillis).build()
        datePicker.show(fm, "DatePicker")

        datePicker.addOnPositiveButtonClickListener {
            val dateFormatter = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
            val date = dateFormatter.format(Date(it))
            onDateSelected.invoke(date)
        }
    }
    fun showTimePicker(fm: FragmentManager, onTimeSelected: (String) -> Unit = {}) {
        val materialTimePicker = MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_12H).build()
        materialTimePicker.show(fm, "TimePicker")

        materialTimePicker.addOnPositiveButtonClickListener {
            val newHour: Int = materialTimePicker.hour
            val newMinute: Int = materialTimePicker.minute
            var str = "$newHour:$newMinute"

            val input = SimpleDateFormat("hh:mm", Locale.getDefault())
            val output = SimpleDateFormat("hh:mm a", Locale.getDefault())

            try {
                val inputDate = input.parse(str) // parse input
                str = output.format(inputDate ?: "00:00") // format output
                onTimeSelected.invoke(str)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
    }

    fun getTimeStamp(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }



    fun serverDate(displayDate: String): String {
        val inputFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val input = inputFormat.parse(displayDate)
        return outputFormat.format(input)
    }

    fun serverTime(displayTime: String): String {
        val inputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val input = inputFormat.parse(displayTime)
        return outputFormat.format(input)
    }

    fun defaultPickupDate(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val dateFormatter = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        return dateFormatter.format(calendar.time)
    }

    fun defaultPickupTime(): String {
        val date = Date()
        val cal = Calendar.getInstance()
        cal.time = date
        return when (cal.get(Calendar.HOUR_OF_DAY)) {
            in 0..11 -> "11:00 am"
            in 12..15 -> "03:00 pm"
            in 16..23 -> "10:00 am"
            else -> "11:00 am"
        }
    }

    fun loadImage(context: Context, imageView: ImageView, url: Int) {
        Glide.with(context).load(url).placeholder(R.drawable.img_placeholder).error(R.drawable.img_placeholder).into(imageView)
    }

    fun loadImage(context: Context, imageView: ImageView, url: String) {
        Glide.with(context).load(url).placeholder(R.drawable.img_placeholder).error(R.drawable.img_placeholder).into(imageView)
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun openWhatsAppChat(context: Context) {
        try {
            val packageManager = context.packageManager
            val businessNumber="9489472423"
            val whatsappIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$businessNumber/?text=${URLEncoder.encode("Hello there! \uD83D\uDC4B", "UTF-8")}"))

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
    fun sendMessageToBusiness(context: Context, businessNumber: String, message: String) {
        try {
            val packageManager = context.packageManager
            val whatsappIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$businessNumber/?text=${URLEncoder.encode(message, "UTF-8")}"))
            if (whatsappIntent.resolveActivity(packageManager) != null) {
                context.startActivity(whatsappIntent)
            } else {
                // WhatsApp is not installed on the device, handle accordingly
                Toast.makeText(context, "WhatsApp is not installed on the device", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Handle any exceptions that may occur
            Toast.makeText(context, e.message ?: "Error occurred", Toast.LENGTH_SHORT).show()
        }
    }


    //check file size
    fun getFileSize(fileUri: Uri, context: Context): Float {
        var size: Long = 0
        fileUri.let { returnUri ->
            context.contentResolver.query(returnUri, null, null, null, null)
        }?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            cursor.moveToFirst()
            cursor.getString(nameIndex)
            size = cursor.getLong(sizeIndex)
        }
        return (size.toFloat() / 1000000)
    }

    fun extractNumber(input: String): Int {
        val regex = Regex("\\d+")
        val matchResult = regex.find(input)
        return matchResult?.value?.toInt() ?: 0
    }

    const val APP_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE

    fun checkStoragePermission(requireActivity: Context): Boolean {
        return ContextCompat.checkSelfPermission(requireActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun checkStoragePermission13(requireActivity: Context): Boolean {
        return ContextCompat.checkSelfPermission(requireActivity, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(requireActivity,
            Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED
    }

    fun showGreetings(): String {
        val date = Date()
        val cal = Calendar.getInstance()
        cal.time = date
        return when (cal.get(Calendar.HOUR_OF_DAY)) {
            in 0..11 -> "Good Morning"
            in 12..15 -> "Good Afternoon"
            in 16..23 -> "Good Evening"
            else -> "Good Morning"
        }
    }

    fun getCurrentStatus(status: Int): String {
        return when (status) {
            Constants.ORDER_CREATED -> "Order created"
            Constants.ORDER_CONFIRMED -> "Order confirmed"
            Constants.WAITING_FOR_PICKUP -> "Waiting for pickup"
            Constants.ORDER_CANCELLED -> "Order cancelled"
            Constants.ORDER_REJECTED -> "Order rejected"
            Constants.ORDER_COMPLETED -> "Order completed"
            else -> "Unknown order status"
        }
    }

    fun calculateDistance(location1: Location): String {
        val result = FloatArray(1)
        Location.distanceBetween(location1.latitude, location1.longitude, Constants.STORE_LATITUDE, Constants.STORE_LONGITUDE, result)
        val distanceInMeters = result[0]
        return if (distanceInMeters < 1000) {
            "$distanceInMeters M"
        } else {
            val distanceInKm = distanceInMeters / 1000
            "$distanceInKm KM"
        }
    }


    fun navigateToDirection(context: Context, location: Location) {
        val uri = "http://maps.google.com/maps?saddr=${location.latitude},${location.longitude}&daddr=${Constants.STORE_LATITUDE},${Constants.STORE_LONGITUDE}"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.setPackage("com.google.android.apps.maps")
        context.startActivity(intent)
    }

    fun showTrackTimeLine(statusHistory: List<StatusHistory?>, binding: OrderTimeLineRowItemBinding, context: Context) {
        statusHistory.forEach { history ->
            history?.let {
                when (it.status_code ?: 0) {
                    Constants.ORDER_CREATED -> {
                        if (it.is_active == true) {
                            binding.cvOrderCreated.visible()
                            binding.textStatusDesc1.text = it.last_updated?.displayDate() ?: "--"
                            binding.viewCircle1.background.setTint(ContextCompat.getColor(context, R.color.green_tick))
                        }
                    }

                    Constants.ORDER_CONFIRMED -> {
                        if (it.is_active == true) {
                            binding.cvOrderConfirmed.visible()
                            binding.textStatusDesc2.text = it.last_updated?.displayDate() ?: "--"
                            binding.viewCircle2.background.setTint(ContextCompat.getColor(context, R.color.pink_tick))
                        }
                    }

                    Constants.WAITING_FOR_PICKUP -> {
                        if (it.is_active == true) {
                            binding.cvWaitingForPickup.visible()
                            binding.textStatusDesc3.text = it.last_updated?.displayDate() ?: "--"
                            binding.viewCircle3.background.setTint(ContextCompat.getColor(context, R.color.blue_tick))
                        }
                    }

                    Constants.ORDER_COMPLETED -> {
                        if (it.is_active == true) {
                            binding.cvOrderCompleted.visible()
                            binding.cvOrderCancelled.gone()
                            binding.cvOrderRejected.gone()
                            binding.textStatusDesc4.text = it.last_updated?.displayDate() ?: "--"
                            binding.viewCircle4.background.setTint(ContextCompat.getColor(context, R.color.orange_tick))
                        }
                    }

                    Constants.ORDER_CANCELLED -> {
                        binding.cvOrderCompleted.gone()
                        binding.cvOrderCancelled.visible()
                        binding.cvOrderRejected.gone()
                        binding.textStatusDesc5.text = it.last_updated?.displayDate() ?: "--"
                    }

                    Constants.ORDER_REJECTED -> {
                        binding.cvOrderCompleted.gone()
                        binding.cvOrderCancelled.gone()
                        binding.cvOrderRejected.visible()
                        binding.textStatusDesc6.text = it.last_updated?.displayDate() ?: "--"
                    }
                }

            }
        }
    }

    fun showScrapLists(orderedItems: List<OrderedItem?>?): String {
        val maxItemsToShow = 1

        val remainingItemCount = orderedItems?.size?.minus(maxItemsToShow)
        val selectedScraps = when {
            orderedItems.isNullOrEmpty() -> {
                "No items"
            }

            orderedItems.size <= maxItemsToShow -> {
                orderedItems.mapNotNull { it?.item }.joinToString(separator = ", ")
            }

            else -> {
                orderedItems.take(maxItemsToShow).mapNotNull { it?.item }.joinToString(separator = ", ").let {
                    if (remainingItemCount != null && remainingItemCount > 0) {
                        "$it and $remainingItemCount more items"
                    } else {
                        it
                    }
                }
            }
        }

        return selectedScraps
    }
}