package com.manisoft.scraprushapp.ui.customer.requestpickup

import android.content.ContentResolver
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.manisoft.scraprushapp.BuildConfig
import com.manisoft.scraprushapp.databinding.ActivityAttachmentViewBinding
import com.manisoft.scraprushapp.utils.ActivityStatus
import com.manisoft.scraprushapp.utils.gone
import com.manisoft.scraprushapp.utils.negativeButton
import com.manisoft.scraprushapp.utils.positiveButton
import com.manisoft.scraprushapp.utils.showAlertDialog
import com.manisoft.scraprushapp.utils.showToast
import java.io.File

class AttachmentViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAttachmentViewBinding
    private var imageFilePath = ""
    private var isFileProvider = false
    private var isViewOnly = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttachmentViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showImage()
        setClickListeners()
    }

    private fun setClickListeners() {
        binding.tvDelete.setOnClickListener {
            showAlertDialog {
                setTitle("Delete Image")
                setMessage("Are you sure want to delete the Image ?")
                positiveButton("Delete") {
                    if (isFileProvider) {
                        val imageFile = File(imageFilePath)
                        if (imageFile.exists()) {
                            if (imageFile.delete()) {
                                showToast("Image deleted successfully")
                                ActivityStatus.isImageDeleted = true
                                finish()
                            } else {
                                showToast("Failed to delete the image")
                            }
                        } else {
                            showToast("Image does not exist at path: $imageFilePath")
                        }
                    } else {
                        showToast("Image deleted successfully")
                        ActivityStatus.isImageDeleted = true
                        finish()
                    }
                    it.dismiss()
                }
                negativeButton("No") {
                    it.dismiss()
                }
            }
        }
        binding.ivBacKIcon.setOnClickListener {
            onBackPressed()
        }
    }

    private fun showImage() {
        isFileProvider = intent.getBooleanExtra("isFileProvider", false)
        isViewOnly = intent.getBooleanExtra("viewOnly", false)
        imageFilePath = intent.getStringExtra("attachment") ?: "unknown file path"

        if (isViewOnly) {
            binding.tvDelete.gone()
        }
        Glide.with(this@AttachmentViewActivity).load(imageFilePath).into(binding.ivAttachmentImage)
    }
}