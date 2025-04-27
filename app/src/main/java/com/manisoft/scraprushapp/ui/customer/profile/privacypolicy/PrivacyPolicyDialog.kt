package com.manisoft.scraprushapp.ui.customer.profile.privacypolicy

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.webkit.WebView
import android.webkit.WebViewClient
import com.manisoft.scraprushapp.databinding.PrivacyPolicyDialogBinding

class PrivacyPolicyDialog(private val context: Context) {
    private lateinit var binding: PrivacyPolicyDialogBinding

    fun showPrivacyPolicyDialog() {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val inflater = LayoutInflater.from(context)
        binding = PrivacyPolicyDialogBinding.inflate(inflater)
        dialog.setContentView(binding.root)

        setWebView()
        setClickListeners(dialog)
        dialog.show()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebView() {
        binding.webView.settings.javaScriptEnabled = true

        binding.webView.webViewClient = object : WebViewClient() {
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url ?: "")
                return true
            }
        }
        binding.webView.loadUrl("https://scrapappweb.s3.ap-southeast-2.amazonaws.com/privacy_policy.html")
    }

    private fun setClickListeners(dialog: Dialog) {

        binding.ivClose.setOnClickListener {
            dialog.dismiss()
        }
    }

}