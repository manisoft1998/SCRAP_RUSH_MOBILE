package com.manisoft.scraprushapp.ui.admin.scraplist

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.bumptech.glide.Glide
import com.manisoft.scraprushapp.R
import com.manisoft.scraprushapp.databinding.ProductUpdateDialogBinding
import com.manisoft.scraprushapp.models.NewProductData
import com.manisoft.scraprushapp.models.ScrapRateItems
import com.manisoft.scraprushapp.utils.Constants
import com.manisoft.scraprushapp.utils.DialogUtils
import com.manisoft.scraprushapp.utils.FileSelectedListener
import com.manisoft.scraprushapp.utils.Utils
import com.manisoft.scraprushapp.utils.amazonutils.AaWaaSaaaaOaaauatahaaaaa
import com.manisoft.scraprushapp.utils.attachmentutils.FileChooser
import com.manisoft.scraprushapp.utils.gone
import com.manisoft.scraprushapp.utils.showToast
import com.manisoft.scraprushapp.utils.visible
import java.io.File

class ProductUpdateDialog(private val context: Context, appCompatActivity: AppCompatActivity, private val listResponse: (NewProductData, Int) -> Unit) {
    private lateinit var binding: ProductUpdateDialogBinding

    private var s3Client: AmazonS3Client
    private var imageFilePath = ""
    private var fileChooser: FileChooser
    private var progressDialog: DialogUtils
    private var selectedUnitId = 0
    private var selectedProductId = 0

    init {
        val awsCredentials = BasicAWSCredentials(AaWaaSaaaaOaaauatahaaaaa.AAAAAAAAAaACCESSAAAAAAAAA_IAAAAAD, AaWaaSaaaaOaaauatahaaaaa.ssssSdEdCdRdsfssffsfssfEfTf_fKffEYasdfghjkl)
        s3Client = AmazonS3Client(awsCredentials, Region.getRegion(Regions.AP_SOUTHEAST_2))
        fileChooser = FileChooser(context, appCompatActivity)
        progressDialog = DialogUtils(appCompatActivity)
    }

    fun showProductUpdateDialog(items: ScrapRateItems) {
        selectedProductId = items.id
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val inflater = LayoutInflater.from(context)
        binding = ProductUpdateDialogBinding.inflate(inflater)
        dialog.setContentView(binding.root)

        updateUI(items)
        setClickListeners(dialog)
        dialog.show()
    }

    private fun updateUI(items: ScrapRateItems) {
        binding.etScrapName.setText(items.name)
        binding.etScrapTamilName.setText(items.name_tamil)
        binding.etPrice.setText(items.price)
        binding.etVariantName.setText(Utils.extractNumber(items.variant_name).toString())

        val variantName = items.variant_name.lowercase()
        when {
            variantName.contains("grams") -> binding.chipGrams.isChecked = true
            variantName.contains("kg") -> binding.chipKg.isChecked = true
            variantName.contains("pc") -> binding.chipPc.isChecked = true
            variantName.contains("ltr") -> binding.chipLtr.isChecked = true
        }
        selectedUnitId = when (binding.unitChipGrp.checkedChipId) {
            R.id.chip_grams -> Constants.GRAMS
            R.id.chip_kg -> Constants.KILO_GRAMS
            R.id.chip_pc -> Constants.PIECE
            R.id.chip_ltr -> Constants.LITTER
            else -> 0
        }
        binding.tvAwsName.text = items.image ?: ""
        imageFilePath = items.image ?: ""
        binding.attachmentInclude.rlBeforeAttachment.gone()
        binding.attachmentInclude.cvAfterAttachment.visible()
        Utils.loadImage(context, binding.attachmentInclude.ivAttachment, items.image ?: "")
    }

    private fun setClickListeners(dialog: Dialog) {
        binding.attachmentInclude.rlBeforeAttachment.setOnClickListener {
            fileChooser.showFileChooser(mediaChooserCallback, isFileOnly = true)
        }
        binding.btnUpdate.setOnClickListener {
            when {
                binding.etScrapName.text.toString().isEmpty() -> context.showToast("Enter scrap name")
                binding.etScrapTamilName.text.toString().isEmpty() -> context.showToast("Enter tamil scrap name")
                binding.etPrice.text.toString().isEmpty() -> context.showToast("Enter price")
                binding.etVariantName.text.toString().isEmpty() -> context.showToast("Enter variant name")
                selectedUnitId == 0 -> context.showToast("Select Unit")
                imageFilePath.isEmpty() -> context.showToast("Attach the scrap photo")
                binding.tvAwsName.text.toString().isEmpty() -> context.showToast("Upload image to AWS S3")
                else -> {
                    val newProduct = NewProductData(image_path = binding.tvAwsName.text.toString().trim(),
                        name = binding.etScrapName.text.toString().trim(),
                        name_tamil = binding.etScrapTamilName.text.toString().trim(),
                        price = binding.etPrice.text.toString().trim().toDouble(),
                        unit_id = selectedUnitId,
                        variant_name = binding.etVariantName.text.toString().trim())
                    listResponse.invoke(newProduct, selectedProductId)
                    dialog.dismiss()
                }
            }
        }


        binding.ivDelete.setOnClickListener {
            if (imageFilePath.isEmpty()) {
                context.showToast("No image")
            } else {
                binding.attachmentInclude.rlBeforeAttachment.visible()
                binding.attachmentInclude.cvAfterAttachment.gone()
                context.showToast("Image deleted")
                imageFilePath = ""
            }
        }
        binding.btnUploadToAws.setOnClickListener {
            if (imageFilePath.isEmpty()) {
                context.showToast("Please select the file")
            } else {
                uploadImage()
            }
        }
        binding.unitChipGrp.setOnCheckedStateChangeListener { _, _ ->
            selectedUnitId = when (binding.unitChipGrp.checkedChipId) {
                R.id.chip_grams -> Constants.GRAMS
                R.id.chip_kg -> Constants.KILO_GRAMS
                R.id.chip_pc -> Constants.PIECE
                R.id.chip_ltr -> Constants.LITTER
                else -> 0
            }
        }
        binding.ivClose.setOnClickListener {
            dialog.dismiss()
        }
    }

    private val mediaChooserCallback = object : FileSelectedListener {
        override fun onFileSelected(isImageType: Boolean, filePath: String, fileName: String, isFileProvider: Boolean) {
            binding.attachmentInclude.rlBeforeAttachment.gone()
            binding.attachmentInclude.cvAfterAttachment.visible()
            imageFilePath = filePath
            binding.tvAwsName.text = ""
            Glide.with(context).load(filePath).into(binding.attachmentInclude.ivAttachment)
        }
    }

    private fun uploadImage() {
        progressDialog.show()
        val imageFile = File(imageFilePath)
        val imageFileName = "${Utils.getTimeStamp()}_${imageFile.name}"

        val trans = TransferUtility.builder().context(context).s3Client(s3Client).build()
        val observer: TransferObserver = trans.upload(AaWaaSaaaaOaaauatahaaaaa.ssssssssssssssssssssBUsCsssKsEsTs_sNsAsMEss, imageFileName, imageFile)//manual storage permission
        observer.setTransferListener(object : TransferListener {
            @SuppressLint("SetTextI18n")
            override fun onStateChanged(id: Int, state: TransferState) {
                if (state == TransferState.COMPLETED) {
                    val url = s3Client.getUrl(AaWaaSaaaaOaaauatahaaaaa.ssssssssssssssssssssBUsCsssKsEsTs_sNsAsMEss, imageFileName)

                    binding.ivDelete.gone()
                    binding.tvAwsName.visible()
                    binding.tvAwsName.text = url.file
                    progressDialog.dismiss()
                    context.showToast("Image uploaded to S3 server")
                    Log.d("msg", "success")
                } else if (state == TransferState.FAILED) {
                    Log.d("msg", "fail")
                }
            }

            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
//                if (bytesCurrent == bytesTotal)
            }

            override fun onError(id: Int, ex: Exception) {
                progressDialog.dismiss()
                Log.d("error", ex.toString())
            }
        })
    }

    /*
        private fun deleteImage() {
            val trans = TransferUtility.builder().context(context).s3Client(s3Client).build()
            val observer: TransferObserver = trans.deleteTransferRecord(AWSOauth.BUCKET_NAME, filePath!!.lastPathSegment)//manual storage permission
            observer.setTransferListener(object : TransferListener {
                override fun onStateChanged(id: Int, state: TransferState) {
                    if (state == TransferState.COMPLETED) {
                        Log.d("msg", "success")
                    } else if (state == TransferState.FAILED) {
                        Log.d("msg", "fail")
                    }
                }

                override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {}

                override fun onError(id: Int, ex: Exception) {
                    Log.d("error", ex.toString())
                }
            })
        }
    */

}