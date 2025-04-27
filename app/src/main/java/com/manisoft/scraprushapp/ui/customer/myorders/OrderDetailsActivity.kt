package com.manisoft.scraprushapp.ui.customer.myorders

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.manisoft.scraprushapp.databinding.ActivityOrderDetailsBinding
import com.manisoft.scraprushapp.models.MyOrderData
import com.manisoft.scraprushapp.models.UpdateOrderRequest
import com.manisoft.scraprushapp.mvvm.viewmodel.ScrapViewModel
import com.manisoft.scraprushapp.network.Resource
import com.manisoft.scraprushapp.ui.customer.requestpickup.AttachmentViewActivity
import com.manisoft.scraprushapp.utils.ActivityStatus
import com.manisoft.scraprushapp.utils.Constants
import com.manisoft.scraprushapp.utils.DialogUtils
import com.manisoft.scraprushapp.utils.KeyValues
import com.manisoft.scraprushapp.utils.Utils
import com.manisoft.scraprushapp.utils.displayDate
import com.manisoft.scraprushapp.utils.gone
import com.manisoft.scraprushapp.utils.negativeButton
import com.manisoft.scraprushapp.utils.positiveButton
import com.manisoft.scraprushapp.utils.showAlertDialog
import com.manisoft.scraprushapp.utils.showToast
import com.manisoft.scraprushapp.utils.visible
import org.koin.androidx.viewmodel.ext.android.viewModel

class OrderDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderDetailsBinding
    private var myOrderData = MyOrderData()
    private lateinit var progressDialog: DialogUtils
    private val viewModel: ScrapViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getInstanceOfClasses()
        setClickListeners()
        updateUI()
        subscribe()
    }

    private fun getInstanceOfClasses() {
        myOrderData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("orderDetails", MyOrderData::class.java) ?: MyOrderData()
        } else {
            intent.getParcelableExtra("orderDetails") ?: MyOrderData()
        }
        progressDialog = DialogUtils(this@OrderDetailsActivity)
    }

    private fun setClickListeners() {
        binding.ivBacKIcon.setOnClickListener {
            onBackPressed()
        }
        binding.btnCancel.setOnClickListener {
            showAlertDialog {
                setTitle("Cancel Order")
                setMessage("Are you sure want to cancel the order?")
                positiveButton("Yes") {
                    viewModel.updateOrder(token = KeyValues.authToken(), orderRequest = UpdateOrderRequest(order_id = myOrderData.id, status_code = Constants.ORDER_CANCELLED))
                    it.dismiss()
                }
                negativeButton("No") { it.dismiss() }
            }
        }
        binding.ivAttachment.setOnClickListener {
            if (myOrderData.images?.isNotEmpty() == true) {
                Intent(this@OrderDetailsActivity, AttachmentViewActivity::class.java).apply {
                    putExtra("attachment", myOrderData.images?.first() ?: "")
                    putExtra("viewOnly", true)
                    startActivity(this)
                }
            } else {
                showToast("No Image")
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        binding.tvOrderId.text = myOrderData.order_id ?: "--"
        binding.tvOrderedDate.text = myOrderData.ordered_at?.displayDate() ?: "--"
        binding.tvPickupDate.text = myOrderData.pick_up_date?.displayDate() ?: "--"

        val selectedScraps = myOrderData.ordered_items?.mapNotNull { it?.item }?.joinToString(separator = ", ")
        binding.tvSelectedScraps.text = selectedScraps?.ifEmpty { "No items selected" }

        binding.tvDeliveryNote.text = myOrderData.notes?.ifEmpty { "No Instructions" }

        if (myOrderData.images?.isNotEmpty() == true) {
            Utils.loadImage(this@OrderDetailsActivity, binding.ivAttachment, myOrderData.images?.first() ?: "")
        }

        binding.tvTitle.text = when (myOrderData.status) {
            Constants.ORDER_COMPLETED -> "Completed Order Detail"
            Constants.ORDER_CREATED, Constants.ORDER_CONFIRMED, Constants.WAITING_FOR_PICKUP -> "Upcoming Order Detail"
            Constants.ORDER_CANCELLED, Constants.ORDER_REJECTED -> "Cancelled Order Detail"
            else -> "Order Detail"
        }
        //show address info
        binding.cvDeliveryAddress.apply {
            tvChange.gone()
            tvAddressType.text = "Pickup from : ${myOrderData.address?.address_type}"
            tvAddress.text = myOrderData.address?.formatted_address
        }

        //check cancel btn status
        when (myOrderData.status) {
            Constants.ORDER_REJECTED, Constants.ORDER_CANCELLED, Constants.ORDER_COMPLETED -> binding.btnCancel.gone()
            else -> binding.btnCancel.visible()
        }
        Utils.showTrackTimeLine(myOrderData.status_history ?: arrayListOf(), binding.includeTimeLine, this)
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
                        ActivityStatus.isChangesMade = true
                        onBackPressed()
                    }
                }
            }
        }
    }
}

/*
private fun loadTrackOrder(statusHistory: List<StatusHistory?>?) {
    val trackAdapter = TrackOrderTimelineAdapter(statusHistory ?: arrayListOf())*//*  binding.rvTimeline.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@OrderDetailsActivity)
            adapter = trackAdapter
        }*//*

}
*/

/*    private fun showTrackTimeLine(statusHistory: List<StatusHistory?>) {
        // Define a mapping of status names to UI elements and colors
        val statusMap = mapOf("order created" to Triple(binding.includeTimeLine.textStatusDesc1, binding.includeTimeLine.viewCircle1, R.color.green_tick),
            "order confirmed" to Triple(binding.includeTimeLine.textStatusDesc2, binding.includeTimeLine.viewCircle2, R.color.pink_tick),
            "waiting for pickup" to Triple(binding.includeTimeLine.textStatusDesc3, binding.includeTimeLine.viewCircle3, R.color.blue_tick),
            "order completed" to Triple(binding.includeTimeLine.textStatusDesc4, binding.includeTimeLine.viewCircle4, R.color.orange_tick))

        // Iterate through the status history
        statusHistory.forEach { status ->
            val statusName = status?.name?.lowercase()
            statusMap[statusName]?.let { (textStatusDesc, viewCircle, colorResId) ->
                if (status?.is_active == true) {
                    textStatusDesc.text = status.last_updated?.displayDate() ?: "--"
                    viewCircle.background.setTint(ContextCompat.getColor(this, colorResId))
                } else {
                    textStatusDesc.inVisible()
                    viewCircle.inVisible()
                }
            }
        }
    }*/