package com.manisoft.scraprushapp.ui.admin.orderdetails

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.manisoft.scraprushapp.R
import com.manisoft.scraprushapp.databinding.UpdateStatusBottomSheetBinding
import com.manisoft.scraprushapp.utils.Constants
import com.manisoft.scraprushapp.utils.showToast


class UpdateStatusBottomSheet(private val currentOrderStatus: Int, private val listener: StatusUpdateListener) : BottomSheetDialogFragment() {
    private lateinit var binding: UpdateStatusBottomSheetBinding

    interface StatusUpdateListener {
        fun onStatusUpdated(statusCode: Int, remarks: String = "No remarks")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = UpdateStatusBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        updateUI()
        setClickListeners()
    }

    private fun updateUI() {
        when (currentOrderStatus) {
            Constants.ORDER_CREATED -> {
                binding.btnConfirmed.isEnabled = true
                binding.btnPickup.isEnabled = false
                binding.btnCompleted.isEnabled = false
                binding.btnRejected.isEnabled = true
            }

            Constants.ORDER_CONFIRMED -> {
                binding.btnConfirmed.isEnabled = false
                binding.btnPickup.isEnabled = true
                binding.btnCompleted.isEnabled = false
                binding.btnRejected.isEnabled = true
            }

            Constants.WAITING_FOR_PICKUP -> {
                binding.btnConfirmed.isEnabled = false
                binding.btnPickup.isEnabled = false
                binding.btnCompleted.isEnabled = true
                binding.btnRejected.isEnabled = true
            }

            Constants.ORDER_COMPLETED, Constants.ORDER_REJECTED, Constants.ORDER_CANCELLED -> {
                binding.btnConfirmed.isEnabled = false
                binding.btnPickup.isEnabled = false
                binding.btnCompleted.isEnabled = false
                binding.btnRejected.isEnabled = false
            }
        }
        updateDisabledButtonsTint()
    }

    private fun updateDisabledButtonsTint() {
        val buttons = arrayOf(binding.btnConfirmed, binding.btnPickup, binding.btnCompleted, binding.btnRejected)
        val color = ContextCompat.getColor(requireContext(), R.color.grey)

        for (button in buttons) {
            if (!button.isEnabled) {
                button.backgroundTintList = ColorStateList.valueOf(color)
            }
        }
    }

    private fun setClickListeners() {
        binding.ivClose.setOnClickListener {
            dismiss()
        }
        binding.btnCompleted.setOnClickListener {
            listener.onStatusUpdated(Constants.ORDER_COMPLETED)
            dismiss()
        }
        binding.btnPickup.setOnClickListener {
            listener.onStatusUpdated(Constants.WAITING_FOR_PICKUP)
            dismiss()
        }
        binding.btnRejected.setOnClickListener {
            if (binding.etRemark.text.toString().isEmpty()) {
                showToast("Please enter the remarks")
            } else {
                listener.onStatusUpdated(Constants.ORDER_REJECTED, binding.etRemark.text.toString().trim())
                dismiss()
            }
        }
        binding.btnConfirmed.setOnClickListener {
            listener.onStatusUpdated(Constants.ORDER_CONFIRMED)
            dismiss()
        }
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
}