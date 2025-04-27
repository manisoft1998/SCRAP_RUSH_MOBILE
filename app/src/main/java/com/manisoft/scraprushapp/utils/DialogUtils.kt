package com.manisoft.scraprushapp.utils

import android.app.Activity
import android.app.Dialog
import android.view.LayoutInflater
import android.view.Window
import com.manisoft.scraprushapp.R
import com.manisoft.scraprushapp.databinding.ProgressBarBinding


class DialogUtils(private val activity: Activity) {
    private var dialog: Dialog? = null

    fun show() {
        dialog = Dialog(activity)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.rounded_corner_dialog)
        dialog?.setCancelable(false)
        val layoutInflater = LayoutInflater.from(activity)
        val bind = ProgressBarBinding.inflate(layoutInflater)
        dialog?.setContentView(bind.root)
        bind.progressView.isAnimating = true
        dialog?.show()
    }

    fun dismiss() {
        if (dialog != null) {
            dialog?.dismiss()
            dialog = null
        }
    }
}