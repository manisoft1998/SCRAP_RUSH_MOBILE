package com.manisoft.scraprushapp.ui.customer.profile.address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButtonToggleGroup
import com.manisoft.scraprushapp.R
import com.manisoft.scraprushapp.databinding.ConfirmAddressDialogBinding
import com.manisoft.scraprushapp.models.SaveAddress
import com.manisoft.scraprushapp.models.SaveAddressRequest
import com.manisoft.scraprushapp.mvvm.viewmodel.AddressViewModel
import com.manisoft.scraprushapp.network.Resource
import com.manisoft.scraprushapp.utils.Constants
import com.manisoft.scraprushapp.utils.DialogUtils
import com.manisoft.scraprushapp.utils.DropDownUtil
import com.manisoft.scraprushapp.utils.KeyValues
import com.manisoft.scraprushapp.utils.gone
import com.manisoft.scraprushapp.utils.isValidMobileNumber
import com.manisoft.scraprushapp.utils.locationpickerutils.locationsmodel.GeocodingResult
import com.manisoft.scraprushapp.utils.showToast
import com.manisoft.scraprushapp.utils.showUnAuthAlert
import com.manisoft.scraprushapp.utils.visible
import org.koin.androidx.viewmodel.ext.android.viewModel


class ConfirmAddressBottomSheet(private val geocodingResult: GeocodingResult) : BottomSheetDialogFragment() {
    private lateinit var binding: ConfirmAddressDialogBinding

    private val viewModel: AddressViewModel by viewModel()
    private lateinit var progressDialog: DialogUtils
    private lateinit var dropDownUtil: DropDownUtil
    private var addressType = "Home"
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = ConfirmAddressDialogBinding.inflate(inflater, container, false)

        getInstanceOfClasses()
        updateUI()
        subscribe()
        setClickListeners()
        return binding.root
    }

    private fun updateUI() {
        val mobile = KeyValues.readLong(Constants.MOBILE_NUMBER, 0L) ?: 0L
        binding.etMobileNumber.setText(mobile.toString())
    }

    private fun getInstanceOfClasses() {
        progressDialog = DialogUtils(requireActivity())
        dropDownUtil = DropDownUtil(requireContext())
        binding.etCompleteAddress.setText(geocodingResult.formatted_address)
    }

    private fun subscribe() {
        viewModel.saveAddressResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Failure -> {
                    progressDialog.dismiss()
                    if (it.errorCode == 401) showUnAuthAlert() else showToast(it.errorBody.toString())
                }

                is Resource.Loading -> {
                    progressDialog.show()
                }

                is Resource.Success -> {
                    progressDialog.dismiss()
                    showToast(it.value.message)
                    if (it.value.status) {
                        dismiss()
                        requireActivity().finish()
                    }
                }
            }
        }
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    private val onButtonCheckedListener = MaterialButtonToggleGroup.OnButtonCheckedListener { _, checkedId, isChecked ->
        if (isChecked) {
            when (checkedId) {
                R.id.btn_home -> {
                    addressType = "Home"
                    binding.etOtherType.gone()
                    binding.tvOtherTitle.gone()
                }

                R.id.btn_work -> {
                    addressType = "Work"
                    binding.etOtherType.gone()
                    binding.tvOtherTitle.gone()
                }

                R.id.btn_other -> {
                    addressType = "Other"
                    binding.etOtherType.visible()
                    binding.tvOtherTitle.visible()
                }
            }
        }
    }

    private fun setClickListeners() {

        binding.ivClose.setOnClickListener {
            dismiss()
        }
        binding.btnSubmit.setOnClickListener {
            when {
                addressType.isEmpty() -> showToast("Choose address type")
                addressType.lowercase() == "other" && binding.etOtherType.text.toString().trim().isEmpty() -> showToast("Enter other type")
                binding.etMobileNumber.text.toString().trim().isEmpty() -> binding.etMobileNumber.error = "Enter mobile number"
                !binding.etMobileNumber.text.toString().trim().isValidMobileNumber() -> binding.etMobileNumber.error = "Enter valid mobile number"
                binding.etCompleteAddress.text.toString().trim().isEmpty() -> binding.etCompleteAddress.error = "Enter complete address"
                else -> saveOrUpdateAddress()
            }
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }


    private fun saveOrUpdateAddress() {
        val mobileNumber = binding.etMobileNumber.text.toString().trim()
        val otherType = if (addressType.lowercase() == "other") binding.etOtherType.text.toString().trim() else ""
        val addressInfo = SaveAddress(address_type = addressType,
            address_type_label = otherType,
            country_id = 91,
            mobile = mobileNumber,
            name = KeyValues.readString(Constants.FULL_NAME, "") ?: "",
            formatted_address = binding.etCompleteAddress.text.toString().trim(),
            place_id = geocodingResult.place_id ?: "",
            latitude = geocodingResult.geometry?.location?.latitude ?: 0.0,
            longitude = geocodingResult.geometry?.location?.longitude ?: 0.0)
        val reqBody = SaveAddressRequest("create", addressInfo, 0)
        viewModel.saveAddress(KeyValues.authToken(), reqBody)
    }

    override fun onResume() {
        super.onResume()
        binding.switchButton.addOnButtonCheckedListener(onButtonCheckedListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.switchButton.removeOnButtonCheckedListener(onButtonCheckedListener)
    }
}