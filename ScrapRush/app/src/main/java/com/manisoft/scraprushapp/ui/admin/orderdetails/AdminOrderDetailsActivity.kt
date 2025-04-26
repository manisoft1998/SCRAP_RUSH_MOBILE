package com.manisoft.scraprushapp.ui.admin.orderdetails

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.manisoft.scraprushapp.databinding.ActivityAdminOrderDetailsBinding
import com.manisoft.scraprushapp.models.AdminUpdateOrderRequest
import com.manisoft.scraprushapp.models.OrderLists
import com.manisoft.scraprushapp.mvvm.viewmodel.AdminViewModel
import com.manisoft.scraprushapp.network.Resource
import com.manisoft.scraprushapp.ui.customer.requestpickup.AttachmentViewActivity
import com.manisoft.scraprushapp.utils.ActivityStatus
import com.manisoft.scraprushapp.utils.DialogUtils
import com.manisoft.scraprushapp.utils.KeyValues
import com.manisoft.scraprushapp.utils.Utils
import com.manisoft.scraprushapp.utils.displayDate
import com.manisoft.scraprushapp.utils.gone
import com.manisoft.scraprushapp.utils.showToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class AdminOrderDetailsActivity : AppCompatActivity(), UpdateStatusBottomSheet.StatusUpdateListener {
    private lateinit var binding: ActivityAdminOrderDetailsBinding

    private var adminOrderData = OrderLists()
    private lateinit var progressDialog: DialogUtils
    private val viewModel: AdminViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getInstanceOfClasses()
        setClickListeners()
        updateUI()
        subscribe()
    }

    private fun getInstanceOfClasses() {
        adminOrderData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("orderDetails", OrderLists::class.java) ?: OrderLists()
        } else {
            intent.getParcelableExtra("orderDetails") ?: OrderLists()
        }
        progressDialog = DialogUtils(this@AdminOrderDetailsActivity)
    }

    private fun setClickListeners() {
        binding.ivBacKIcon.setOnClickListener {
            onBackPressed()
        }
        binding.ivAttachment.setOnClickListener {
            if (adminOrderData.images?.isNotEmpty() == true) {
                Intent(this@AdminOrderDetailsActivity, AttachmentViewActivity::class.java).apply {
                    putExtra("attachment", adminOrderData.images?.first() ?: "")
                    putExtra("viewOnly", true)
                    startActivity(this)
                }
            } else {
                showToast("No Image")
            }
        }

        binding.btnUpdateStatus.setOnClickListener {
            UpdateStatusBottomSheet(adminOrderData.status ?: 0, this@AdminOrderDetailsActivity).show(supportFragmentManager, "UpdateStatusBottomSheet")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        binding.orderDetailsInclude.apply {
            tvOrderedDate.text = adminOrderData.ordered_at?.displayDate() ?: "--"
            tvPickupDate.text = adminOrderData.pick_up_date?.displayDate() ?: "--"
            tvSelectedScraps.text = Utils.showScrapLists(adminOrderData.ordered_items)
            tvOrderId.text = adminOrderData.order_id ?: "--"
            tvCustomerName.text = adminOrderData.address?.name ?: "--"
            tvOrderStatus.text = Utils.getCurrentStatus(adminOrderData.status ?: 0)

            val location = Location("Location 1")
            location.latitude = adminOrderData.address?.latitude?.toDouble() ?: 0.0
            location.longitude = adminOrderData.address?.longitude?.toDouble() ?: 0.0
            tvTotalDistance.text = Utils.calculateDistance(location)

            if (adminOrderData.address?.mobile.isNullOrEmpty() || adminOrderData.address?.mobile == "0") {
                tvPhone.text = "Mobile number not available"
                ivPhone.gone()
            } else {
                tvPhone.text = "+91 ${adminOrderData.address?.mobile ?: "--"}"

            }
            tvDirection.setOnClickListener {
                Utils.navigateToDirection(this@AdminOrderDetailsActivity, location)
            }
        }

        binding.tvDeliveryNote.text = adminOrderData.notes?.ifEmpty { "No Instructions" }

        if (adminOrderData.images?.isNotEmpty() == true) {
            Utils.loadImage(this@AdminOrderDetailsActivity, binding.ivAttachment, adminOrderData.images?.first() ?: "")
        }

        //show address info
        binding.cvDeliveryAddress.apply {
            tvChange.gone()
            tvAddressType.text = "Pickup from : ${adminOrderData.address?.address_type}"
            tvAddress.text = adminOrderData.address?.formatted_address
        }

        Utils.showTrackTimeLine(adminOrderData.status_history ?: arrayListOf(), binding.includeTimeLine, this)
    }

    private fun subscribe() {
        viewModel.updateOrderResponse.observe(this) {
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
                    showToast(it.value.message ?: "")
                    if (it.value.status == true) {
                        ActivityStatus.isAdminUpdatedStatus = true
                        onBackPressed()
                    }
                }
            }
        }
    }

    override fun onStatusUpdated(statusCode: Int, remarks: String) {
        viewModel.updateOrder(token = KeyValues.authToken(), request = AdminUpdateOrderRequest(notess = remarks, order_id = adminOrderData.id ?: 0, order_status = statusCode))
    }
}