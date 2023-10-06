package com.manisoft.scraprushapp.utils

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.manisoft.scraprushapp.R
import java.math.RoundingMode
import java.text.DecimalFormat
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
fun Fragment.showUnAuthAlert(@StyleRes style: Int = 0) {
    MaterialAlertDialogBuilder(requireContext(), style).apply {
        setCancelable(false)
        setTitle("Un-Authentication")
        setMessage("Please login or sign-up to continue this app.")
        positiveButton { }
        negativeButton { d -> d.dismiss() }
        create()
        show()
    }
}

fun MaterialAlertDialogBuilder.negativeButton(text: String = "No", handleClick: (dialogInterface: DialogInterface) -> Unit = { it.dismiss() }) {
    this.setNegativeButton(text) { dialogInterface, _ -> handleClick(dialogInterface) }
}

fun MaterialAlertDialogBuilder.positiveButton(text: String = "Yes", handleClick: (dialogInterface: DialogInterface) -> Unit = { it.dismiss() }) {
    this.setPositiveButton(text) { dialogInterface, _ -> handleClick(dialogInterface) }
}

fun Fragment.showGreetings(): String {
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

fun String.lastMinProductName(): String {
    return if (this.length >= 12) "${this.take(9)}..." else this
}

fun String.priceFormat(): String {
    return "₹$this"
}

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
    return "Download Scrap Rush Today!\n" + "\n" + "Join the Scrap Rush community and be part of the solution to reduce waste and promote sustainable living. Download the app now and start turning your scrap into treasures while making a difference in the world.\n" + "\n" + "Get ready to rush toward a more sustainable future with Scrap Rush!\n" + "\n" + "Install Now: Scrap Rush on Google Play\n" + "https://play.google.com/store/apps/details?id=com.brave.browser&pcampaignid=web_share"
}