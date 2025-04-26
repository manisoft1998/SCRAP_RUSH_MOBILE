package com.manisoft.scraprushapp.ui.customer.requestpickup

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.manisoft.scraprushapp.R
import com.manisoft.scraprushapp.adapters.SelectedScrapListAdapter
import com.manisoft.scraprushapp.databinding.ActivityRequestPickupBinding
import com.manisoft.scraprushapp.models.AddressData
import com.manisoft.scraprushapp.models.ScrapRateItems
import com.manisoft.scraprushapp.mvvm.viewmodel.ScrapViewModel
import com.manisoft.scraprushapp.network.Resource
import com.manisoft.scraprushapp.ui.MainActivity
import com.manisoft.scraprushapp.ui.customer.profile.address.SelectAddressBottomSheet
import com.manisoft.scraprushapp.utils.ActivityStatus
import com.manisoft.scraprushapp.utils.Constants
import com.manisoft.scraprushapp.utils.DialogUtils
import com.manisoft.scraprushapp.utils.FileSelectedListener
import com.manisoft.scraprushapp.utils.KeyValues
import com.manisoft.scraprushapp.utils.ModelPreferenceManager
import com.manisoft.scraprushapp.utils.Utils
import com.manisoft.scraprushapp.utils.attachmentutils.FileChooser
import com.manisoft.scraprushapp.utils.gone
import com.manisoft.scraprushapp.utils.inVisible
import com.manisoft.scraprushapp.utils.negativeButton
import com.manisoft.scraprushapp.utils.positiveButton
import com.manisoft.scraprushapp.utils.showAlertDialog
import com.manisoft.scraprushapp.utils.showConfirmationDialogue
import com.manisoft.scraprushapp.utils.showToast
import com.manisoft.scraprushapp.utils.visible
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class RequestPickupActivity : AppCompatActivity(), SelectAddressBottomSheet.AddressSelectListener {
    private lateinit var binding: ActivityRequestPickupBinding

    private var selectedAddressId = 0
    private var selectedScrapList: ArrayList<ScrapRateItems> = arrayListOf()

    private lateinit var fileChooser: FileChooser
    private val viewModel: ScrapViewModel by viewModel()
    private var imageFilePath = ""
    private var isFileProvider = false
    private lateinit var progressDialog: DialogUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestPickupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getInstanceOfClasses()
        setClickListeners()
        updateUI()
        subscribe()
    }

    private fun subscribe() {
        viewModel.createOrderResponse.observe(this) {
            when (it) {
                is Resource.Failure -> {
                    progressDialog.dismiss()
                    showToast(it.errorBody?.message ?: "")
                }

                is Resource.Loading -> {
                    progressDialog.show()
                }

                is Resource.Success -> {
                    progressDialog.dismiss()
                    if (it.value.status == true) {
                        showOrderSuccessAnimation()
                    } else {
                        showToast(it.value.message ?: "")
                    }
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        if (ActivityStatus.isImageDeleted) {
            ActivityStatus.isImageDeleted = false
            updateAttachmentUI()
        }
    }

    private fun updateAttachmentUI() {
        imageFilePath = ""
        isFileProvider = false
        binding.attachmentInclude.rlBeforeAttachment.visible()
        binding.attachmentInclude.cvAfterAttachment.gone()
    }

    private fun navigateToMyOrders() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("isMyOrder", true)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }


    private fun showOrderSuccessAnimation() {/*       showConfirmationDialogue("Your order has been placed successfully", onHomeClicked = {
            navigateToHome()
            it.dismiss()
        }, onMyOrdersClicked = {
            navigateToMyOrders()
            it.dismiss()
        })
*/
        binding.rlProceedParent.gone()
        binding.nsvMainContent.gone()
        binding.rlAnimation.visible()
        binding.animationView.setAnimation(R.raw.success_animation)
        binding.animationView.playAnimation()
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToMyOrders()
        }, 3000)
    }

    private fun setClickListeners() {
        binding.ivBacKIcon.setOnClickListener {
            onBackPressed()
        }

        binding.cvDeliveryAddress.tvChange.setOnClickListener {
            SelectAddressBottomSheet(this).show(supportFragmentManager, "SelectAddressBottomSheet")
        }

        // Set up a ViewTreeObserver to listen for layout changes
        binding.etDeliveryNote.viewTreeObserver.addOnGlobalLayoutListener {
            val rec = Rect()
            binding.rlParent.getWindowVisibleDisplayFrame(rec)

            //finding screen height
            val screenHeight = binding.rlParent.rootView.height

            //finding keyboard height
            val keypadHeight = screenHeight - rec.bottom

            if (keypadHeight > screenHeight * 0.15) {
                binding.rlProceed.gone()
            } else {
                binding.rlProceed.visible()
            }
        }
        binding.attachmentInclude.rlBeforeAttachment.setOnClickListener {
            fileChooser.showFileChooser(mediaChooserCallback)
        }
        binding.attachmentInclude.cvAfterAttachment.setOnClickListener {
            Intent(this@RequestPickupActivity, AttachmentViewActivity::class.java).apply {
                putExtra("attachment", imageFilePath)
                putExtra("isFileProvider", isFileProvider)
                startActivity(this)
            }
        }
        binding.btnConfirmPickup.setOnClickListener {
            when (selectedAddressId) {
                0 -> showAddressAlert()
                else -> createOrder()
            }
        }
        binding.cvPickupDate.root.setOnClickListener {
            Utils.showDatePicker(supportFragmentManager) { date ->
                Utils.showTimePicker(supportFragmentManager) { time ->
                    binding.cvPickupDate.tvPickupDate.text = date
                    binding.cvPickupDate.tvPickupTime.text = time
                }
            }
        }
    }

    private fun createOrder() {
        val productIds = getProductIds().toRequestBody("text/plain".toMediaTypeOrNull())
        val estimatedAmount = estimatedAmount().toRequestBody("text/plain".toMediaTypeOrNull())
        val addressId = selectedAddressId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val pickupDate = (Utils.serverDate(binding.cvPickupDate.tvPickupDate.text.toString())).toRequestBody("text/plain".toMediaTypeOrNull())
        val pickupTime = (Utils.serverTime(binding.cvPickupDate.tvPickupTime.text.toString())).toRequestBody("text/plain".toMediaTypeOrNull())
        val imagePart = if (imageFilePath.isEmpty()) null else createMultipart()
        val notes = if (binding.etDeliveryNote.text.toString().isEmpty()) {
            null
        } else {
            binding.etDeliveryNote.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        }

        viewModel.createOrder(token = KeyValues.authToken(),
            items = productIds,
            estimationAmount = estimatedAmount,
            addressId = addressId,
            pickupDate = pickupDate,
            pickupTime = pickupTime,
            notes = notes,
            image = imagePart)
    }

    private fun showAddressAlert() {
        showAlertDialog {
            setTitle("Select Address")
            setMessage("Please select the address")
            positiveButton("Select") {
                SelectAddressBottomSheet(this@RequestPickupActivity).show(supportFragmentManager, "SelectAddressBottomSheet")
                it.dismiss()
            }
            negativeButton("Cancel") {
                it.dismiss()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        when (val count = selectedScrapList.size) {
            0, 1 -> binding.tvItemCount.text = "$count item selected"
            else -> binding.tvItemCount.text = "$count items selected"
        }

        if (KeyValues.readBoolean(Constants.IS_ADDRESS_SELECTED, false) == true) {
            updateAddressUI(ModelPreferenceManager.get(Constants.SELECTED_ADDRESS) ?: AddressData())
        }

        if (selectedScrapList.isEmpty()) {
            binding.rvScraps.gone()
            binding.tvNoItems.visible()
        } else {
            binding.rvScraps.visible()
            binding.tvNoItems.gone()
        }

        //set pickup date and time by default next day date and time
        binding.cvPickupDate.tvPickupDate.text = Utils.defaultPickupDate()
        binding.cvPickupDate.tvPickupTime.text = Utils.defaultPickupTime()
    }

    @SuppressLint("SetTextI18n")
    private fun updateAddressUI(item: AddressData) {
        if (KeyValues.readBoolean(Constants.IS_ADDRESS_SELECTED, false) == true) {
            selectedAddressId = item.id ?: 0
            if (item.address_type?.lowercase() != "other") {
                binding.cvDeliveryAddress.tvAddressType.text = "Pickup from : " + item.address_type
            } else {
                binding.cvDeliveryAddress.tvAddressType.text = "Pickup from : " + item.address_type + "-" + item.address_type_label
            }
            binding.cvDeliveryAddress.tvAddress.text = item.formatted_address
        }
    }

    private fun loadSelectedScrapItems() {
        binding.llScrapRecycler.visible()
        binding.rvScraps.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@RequestPickupActivity)
            adapter = SelectedScrapListAdapter(selectedScrapList)
        }
    }

    private fun getInstanceOfClasses() {
        selectedScrapList = intent.getParcelableArrayListExtra("scraps") ?: arrayListOf()
        if (selectedScrapList.isNotEmpty()) loadSelectedScrapItems()
        fileChooser = FileChooser(this, this)
        progressDialog = DialogUtils(this)
    }

    private val mediaChooserCallback = object : FileSelectedListener {
        override fun onFileSelected(isImageType: Boolean, filePath: String, fileName: String, isFileProvider: Boolean) {
            binding.attachmentInclude.rlBeforeAttachment.gone()
            binding.attachmentInclude.cvAfterAttachment.visible()
            imageFilePath = filePath
            this@RequestPickupActivity.isFileProvider = isFileProvider
            Glide.with(this@RequestPickupActivity).load(filePath).into(binding.attachmentInclude.ivAttachment)
        }
    }

    override fun onAddressSelected(item: AddressData) {
        ModelPreferenceManager.put(item, Constants.SELECTED_ADDRESS)
        KeyValues.writeBoolean(Constants.IS_ADDRESS_SELECTED, true)
        updateAddressUI(item)
    }

    private fun getProductIds(): String {
        var productIds = ""
        selectedScrapList.forEach { productIds += ",${it.id}" }
        return productIds
    }

    private fun createMultipart(): MultipartBody.Part {
        val fileName = File(imageFilePath)
        val requestFile = fileName.asRequestBody("/".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("images", fileName.name, requestFile)
    }

    private fun estimatedAmount(): String {
        var amount = 0.0
        selectedScrapList.forEach {
            amount += it.price.toDouble()
        }
        return amount.toString()
    }
}
