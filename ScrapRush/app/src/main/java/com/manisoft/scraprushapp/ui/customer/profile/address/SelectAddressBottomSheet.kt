package com.manisoft.scraprushapp.ui.customer.profile.address

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.manisoft.scraprushapp.R
import com.manisoft.scraprushapp.adapters.AddressListAdapter
import com.manisoft.scraprushapp.databinding.FragmentSelectAddressBottomSheetBinding
import com.manisoft.scraprushapp.models.AddressData
import com.manisoft.scraprushapp.mvvm.viewmodel.AddressViewModel
import com.manisoft.scraprushapp.network.Resource
import com.manisoft.scraprushapp.ui.customer.locationpicker.LocationPickerActivity
import com.manisoft.scraprushapp.utils.DialogUtils
import com.manisoft.scraprushapp.utils.KeyValues
import com.manisoft.scraprushapp.utils.gone
import com.manisoft.scraprushapp.utils.showToast
import com.manisoft.scraprushapp.utils.showUnAuthAlert
import com.manisoft.scraprushapp.utils.visible
import org.koin.androidx.viewmodel.ext.android.viewModel

class SelectAddressBottomSheet(private val addressSelectListener: AddressSelectListener) : BottomSheetDialogFragment(), AddressListAdapter.OnAddressClickListener {
    private lateinit var binding: FragmentSelectAddressBottomSheetBinding
    private lateinit var progressDialog: DialogUtils
    private val viewModel: AddressViewModel by viewModel()
    private var selectedAddressPosition = 0
    private lateinit var addressAdapter: AddressListAdapter

    interface AddressSelectListener {
        fun onAddressSelected(item: AddressData)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSelectAddressBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val bottomSheetView = layoutInflater.inflate(R.layout.fragment_select_address_bottom_sheet, null)
        bottomSheetView.minimumHeight = resources.getDimensionPixelSize(R.dimen.bottom_sheet_min_height)
        getInstanceOfClass()
        setClickListeners()
        subscribe()
    }

    override fun onResume() {
        super.onResume()
        viewModel.addressList(KeyValues.authToken())
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    private fun subscribe() {
        viewModel.addressListResponse.observe(viewLifecycleOwner) {
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
                    if (it.value.status) {
                        if (it.value.data.isEmpty()) {
                            binding.tvNoAddress.visible()
                            binding.rvAddress.gone()
                        } else {
                            binding.tvNoAddress.gone()
                            binding.rvAddress.visible()
                            showAddressList(it.value.data)
                        }
                    } else {
                        binding.tvNoAddress.visible()
                        binding.rvAddress.gone()
                        showToast(it.value.message ?: "")
                    }
                }
            }
        }
    }

    private fun showAddressList(data: List<AddressData>) {
        addressAdapter = AddressListAdapter(data as ArrayList<AddressData>, this, false)
        binding.rvAddress.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = addressAdapter
        }
    }

    private fun setClickListeners() {
        binding.ivAdd.setOnClickListener {
            startActivity(Intent(requireActivity(), LocationPickerActivity::class.java))
        }
        binding.ivClose.setOnClickListener {
            dismiss()
        }
    }

    private fun getInstanceOfClass() {
        progressDialog = DialogUtils(requireActivity())
    }

    override fun onAddressClicked(item: AddressData) {
        addressSelectListener.onAddressSelected(item)
        dismiss()
    }

    override fun onDeleteClicked(item: AddressData, position: Int) {

    }
}