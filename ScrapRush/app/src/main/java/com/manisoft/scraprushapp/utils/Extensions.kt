package com.manisoft.scraprushapp.utils

import android.app.Activity
import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StyleRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.manisoft.scraprushapp.R
import com.manisoft.scraprushapp.databinding.OrderSuccessDialogBinding
import com.manisoft.scraprushapp.ui.customer.login.LoginActivity
import java.io.File
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*


fun Fragment.black(): Int {
//   return ContextCompat.getColor(requireContext(), com.google.android.libraries.places.R.color.quantum_yellow)
    return ContextCompat.getColor(requireContext(), R.color.primary)
}

fun Fragment.white(): Int {
    return ContextCompat.getColor(requireContext(), R.color.white)
}

fun Activity.changeStatusBarColor() {
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor = ContextCompat.getColor(this, R.color.primary)
    WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
}

fun Activity.changeNavBarColor() {
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.navigationBarColor = ContextCompat.getColor(this, R.color.primary)
    WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightNavigationBars = false
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.inVisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun Activity.showToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Fragment.showToast(text: String) {
    Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
}

fun Context.showToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}


fun Uri.getMimeType(context: Context): String? {
    return when (scheme) {
        ContentResolver.SCHEME_CONTENT -> context.contentResolver.getType(this)
        ContentResolver.SCHEME_FILE -> MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(toString()).toLowerCase(Locale.US))
        else -> null
    }
}

fun Uri.getName(context: Context): String? {
    val returnCursor = context.contentResolver.query(this, null, null, null, null)
    val nameIndex = returnCursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
    returnCursor?.moveToFirst()
    val fileName = returnCursor?.getString(nameIndex!!)
    returnCursor?.close()
    return fileName
}

fun Double.roundOffDecimal(): Double {
    val df = DecimalFormat("#.#####")
    df.roundingMode = RoundingMode.CEILING
    return df.format(this).toDouble()
}

fun Context.shareDeepLink(deepLink: String) {
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_SUBJECT, "You have been shared an amazing meme, check it out ->")
    intent.putExtra(Intent.EXTRA_TEXT, deepLink)
    startActivity(intent)
}

/*fun Fragment.logout() {
    KeyValues.sharedPreferencesClear()
    startActivity(Intent(requireActivity(), LoginActivity::class.java))
    requireActivity().finish()
}*/

//alert dialog extension
fun Activity.showUnAuthAlert(@StyleRes style: Int = 0) {
    MaterialAlertDialogBuilder(this, style).apply {
        setCancelable(false)
        setTitle("Un-Authentication")
        setMessage("Please login or sign-up to continue this app.")
        positiveButton { logout() }
        negativeButton { d -> d.dismiss() }
        create()
        show()
    }
}

fun Fragment.showUnAuthAlert(@StyleRes style: Int = 0) {
    MaterialAlertDialogBuilder(requireContext(), style).apply {
        setCancelable(false)
        setTitle("Un-Authentication")
        setMessage("Please login or sign-up to continue this app.")
        positiveButton { logout() }
        negativeButton { d -> d.dismiss() }
        create()
        show()
    }
}

fun String.isPhoneNumberValid(): Boolean {
    // Create a regex pattern to match a phone number with exactly 10 digits
    val regex = """^\d{10}$""".toRegex()

    // Check if the string matches the regex pattern and if it's a valid phone number according to Android Patterns
    return matches(regex) && Patterns.PHONE.matcher(this).matches()
}

fun Fragment.logout() {
    KeyValues.sharedPreferencesClear()
    startActivity(Intent(requireActivity(), LoginActivity::class.java))
    requireActivity().finish()
}

fun Activity.logout() {
    KeyValues.sharedPreferencesClear()
    startActivity(Intent(this, LoginActivity::class.java))
    finish()
}

fun MaterialAlertDialogBuilder.negativeButton(text: String = "No", handleClick: (dialogInterface: DialogInterface) -> Unit = { it.dismiss() }) {
    this.setNegativeButton(text) { dialogInterface, _ -> handleClick(dialogInterface) }
}

fun MaterialAlertDialogBuilder.neutralButton(text: String = "Cancel", handleClick: (dialogInterface: DialogInterface) -> Unit = { it.dismiss() }) {
    this.setNeutralButton(text) { dialogInterface, _ -> handleClick(dialogInterface) }
}

fun MaterialAlertDialogBuilder.positiveButton(text: String = "Yes", handleClick: (dialogInterface: DialogInterface) -> Unit = { it.dismiss() }) {
    this.setPositiveButton(text) { dialogInterface, _ -> handleClick(dialogInterface) }
}

fun String.lastMinProductName(): String {
    return if (this.length >= 12) "${this.take(9)}..." else this
}

fun String.priceFormat(): String = "₹$this"


// text watcher
fun EditText.onTextChanged(onTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged.invoke(s.toString())
        }

        override fun afterTextChanged(editable: Editable?) {
        }
    })
}

fun AutoCompleteTextView.onTextChanged(onTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged.invoke(s.toString())
        }

        override fun afterTextChanged(editable: Editable?) {
        }
    })
}

fun String.isValidMobileNumber(): Boolean {
    // Define a regular expression pattern for a valid mobile number with a maximum length of 10
    val mobilePattern = "^[0-9]{1,10}\$"

    // Use the regular expression pattern to validate the mobile number
    return Patterns.PHONE.matcher(this).matches() && this.matches(mobilePattern.toRegex())
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Activity.showAlertDialog(dialogBuilder: MaterialAlertDialogBuilder.() -> Unit) {
    MaterialAlertDialogBuilder(this, 0).apply {
        setCancelable(false)
        dialogBuilder()
        create()
        show()
    }
}

fun Fragment.showAlertDialog(dialogBuilder: MaterialAlertDialogBuilder.() -> Unit) {
    MaterialAlertDialogBuilder(requireContext(), 0).apply {
        setCancelable(false)
        dialogBuilder()
        create()
        show()
    }
}

fun Fragment.shareDeepLink() {
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_TEXT, sharingContent())
    startActivity(intent)
}

fun sharingContent(): String {
    return "Download Scrap Rush Today!\n" + "\n" + "Join the Scrap Rush community and be part of the solution to reduce waste and promote sustainable living. Download the app now and start turning your scrap into treasures while making a difference in the world.\n" + "\n" + "Get ready to rush toward a more sustainable future with Scrap Rush!\n" + "\n" + "Install Now: Scrap Rush on Google Play\n" + "https://play.google.com/store/apps/details?id=com.manisoft.scraprushapp&pcampaignid=web_share"
}

fun String.displayDate(): String {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        val dateTime = LocalDateTime.parse(this, formatter)

        val ordinalDay = dateTime.dayOfMonth.toString() + when {
            dateTime.dayOfMonth in 11..13 -> "th"
            dateTime.dayOfMonth % 10 == 1 -> "st"
            dateTime.dayOfMonth % 10 == 2 -> "nd"
            dateTime.dayOfMonth % 10 == 3 -> "rd"
            else -> "th"
        }

        val monthName = dateTime.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
        val year = dateTime.year
        val hourMinute = dateTime.format(DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH))

        return "$ordinalDay $monthName $year $hourMinute"
    } else {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.getDefault())
        val date = inputFormat.parse(this)
        return outputFormat.format(date)
    }
}

fun Activity.showConfirmationDialogue(message: String, onHomeClicked: (Dialog) -> Unit, onMyOrdersClicked: (Dialog) -> Unit) {
    val factory = LayoutInflater.from(this)
    val binding = OrderSuccessDialogBinding.inflate(factory)
    val dialog = AlertDialog.Builder(this).create()
    dialog.setCancelable(false)
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.setView(binding.root)

    binding.animationView.setAnimation(R.raw.success_animation)
    binding.animationView.playAnimation()

    binding.tvPodContent.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(message, Html.FROM_HTML_MODE_COMPACT)
    } else {
        HtmlCompat.fromHtml(message, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    binding.tvHome.setOnClickListener {
        onHomeClicked(dialog)
    }
    binding.tvMyOrders.setOnClickListener {
        onMyOrdersClicked(dialog)
    }
    dialog.show()
}

fun String.filePathToUri(): Uri {
    return Uri.fromFile(File(this))
}
