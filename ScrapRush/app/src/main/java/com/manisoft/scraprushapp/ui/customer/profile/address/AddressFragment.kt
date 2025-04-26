package com.manisoft.scraprushapp.ui.customer.profile.address

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.manisoft.scraprushapp.adapters.AddressListAdapter
import com.manisoft.scraprushapp.databinding.FragmentAddressBinding
import com.manisoft.scraprushapp.models.AddressData
import com.manisoft.scraprushapp.models.DeleteAddressRequest
import com.manisoft.scraprushapp.mvvm.viewmodel.AddressViewModel
import com.manisoft.scraprushapp.network.Resource
import com.manisoft.scraprushapp.ui.customer.locationpicker.LocationPickerActivity
import com.manisoft.scraprushapp.utils.DialogUtils
import com.manisoft.scraprushapp.utils.DropDownUtil
import com.manisoft.scraprushapp.utils.KeyValues
import com.manisoft.scraprushapp.utils.negativeButton
import com.manisoft.scraprushapp.utils.positiveButton
import com.manisoft.scraprushapp.utils.showAlertDialog
import com.manisoft.scraprushapp.utils.showToast
import com.manisoft.scraprushapp.utils.showUnAuthAlert
import org.koin.androidx.viewmodel.ext.android.viewModel


class AddressFragment : Fragment(), AddressListAdapter.OnAddressClickListener {
    private lateinit var binding: FragmentAddressBinding
    private val viewModel: AddressViewModel by viewModel()
    private lateinit var progressDialog: DialogUtils
    private lateinit var dropDownUtil: DropDownUtil
    private var selectedAddressPosition = 0
    private lateinit var addressAdapter: AddressListAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getInstanceOfClasses()
        setClickListeners()
        subscribe()
    }

    override fun onResume() {
        super.onResume()
        viewModel.addressList(KeyValues.authToken())
    }

    private fun getInstanceOfClasses() {
        progressDialog = DialogUtils(requireActivity())
        dropDownUtil = DropDownUtil(requireContext())
    }

    private fun setClickListeners() {
        binding.ivAddAddress.setOnClickListener {
            startActivity(Intent(requireActivity(), LocationPickerActivity::class.java))
        }

        binding.ivBacKIcon.setOnClickListener {
            requireActivity().onBackPressed()
        }

    }

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
                        showAddressList(it.value.data)
                    } else {
                        showToast(it.value.message ?: "")
                    }
                }
            }
        }
        viewModel.deleteAddressResponse.observe(viewLifecycleOwner) {
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
                        addressAdapter.deleteAddressFromList(selectedAddressPosition)
                    }
                }
            }
        }
    }

    private fun showAddressList(data: List<AddressData>) {
        addressAdapter = AddressListAdapter(data as ArrayList<AddressData>, this@AddressFragment, true)
        binding.rvAddress.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = addressAdapter
        }
    }

    override fun onAddressClicked(item: AddressData) {

    }

    override fun onDeleteClicked(item: AddressData, position: Int) {
        selectedAddressPosition = position
        showAlertDialog {
            setTitle("Delete Address")
            setMessage("Are you sure want to delete ?")
            positiveButton("Delete") {
                viewModel.deleteAddress(KeyValues.authToken(), DeleteAddressRequest(address_id = item.id ?: 0))
                it.dismiss()
            }
            negativeButton("No") {
                it.dismiss()
            }
        }
    }

}