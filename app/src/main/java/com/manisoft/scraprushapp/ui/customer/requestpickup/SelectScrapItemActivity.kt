package com.manisoft.scraprushapp.ui.customer.requestpickup

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.manisoft.scraprushapp.adapters.SelectScrapListAdapter
import com.manisoft.scraprushapp.databinding.ActivitySelectScrapItemBinding
import com.manisoft.scraprushapp.models.AddressData
import com.manisoft.scraprushapp.models.ScrapRateItems
import com.manisoft.scraprushapp.mvvm.viewmodel.ScrapViewModel
import com.manisoft.scraprushapp.network.Resource
import com.manisoft.scraprushapp.ui.customer.profile.address.SelectAddressBottomSheet
import com.manisoft.scraprushapp.utils.Constants
import com.manisoft.scraprushapp.utils.DialogUtils
import com.manisoft.scraprushapp.utils.KeyValues
import com.manisoft.scraprushapp.utils.ModelPreferenceManager
import com.manisoft.scraprushapp.utils.gone
import com.manisoft.scraprushapp.utils.showToast
import com.manisoft.scraprushapp.utils.showUnAuthAlert
import com.manisoft.scraprushapp.utils.visible
import org.koin.androidx.viewmodel.ext.android.viewModel

class SelectScrapItemActivity : AppCompatActivity(), SelectAddressBottomSheet.AddressSelectListener, SelectScrapListAdapter.OnItemSelectedListener {
    private lateinit var binding: ActivitySelectScrapItemBinding
    private val viewModel: ScrapViewModel by viewModel()
    private lateinit var progressDialog: DialogUtils
    private lateinit var scrapAdapter: SelectScrapListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectScrapItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getInstanceOfClasses()
        updateUI()
        subscribe()
        setClickListeners()
    }

    private fun updateUI() {
        if (KeyValues.readBoolean(Constants.IS_ADDRESS_SELECTED, false) == true) {
            binding.cvDeliveryAddress.root.visible()
            showAddressUI(addressData = ModelPreferenceManager.get(Constants.SELECTED_ADDRESS) ?: AddressData())
        } else {
            binding.cvDeliveryAddress.root.gone()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showAddressUI(addressData: AddressData) {
        if (addressData.address_type?.lowercase() != "other") {
            binding.cvDeliveryAddress.tvAddressType.text = "Pickup from : " + addressData.address_type
        } else {
            binding.cvDeliveryAddress.tvAddressType.text = "Pickup from : " + addressData.address_type + "-" + addressData.address_type_label
        }
        binding.cvDeliveryAddress.tvAddress.text = addressData.formatted_address
    }

    private fun getInstanceOfClasses() {
        progressDialog = DialogUtils(this)
    }

    private fun subscribe() {
        viewModel.getScrapRateItems(KeyValues.authToken())
        viewModel.scrapRateResponse.observe(this) {
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
                        loadScrapItems(it.value.data)
                    } else {
                        showToast(it.value.message ?: "")
                    }
                }
            }
        }
    }

    private fun setClickListeners() {
        binding.ivBacKIcon.setOnClickListener {
            onBackPressed()
        }
        binding.btnProceed.setOnClickListener {
            if (KeyValues.readBoolean(Constants.IS_ADDRESS_SELECTED, false) == true) {
                Intent(this@SelectScrapItemActivity, RequestPickupActivity::class.java).apply {
                    putParcelableArrayListExtra("scraps", scrapAdapter.selectedScrapList())
                    startActivity(this)
                }
            } else {
                SelectAddressBottomSheet(this).show(supportFragmentManager, "SelectAddressBottomSheet")

            }
        }
        binding.cvDeliveryAddress.tvChange.setOnClickListener {
            SelectAddressBottomSheet(this).show(supportFragmentManager, "SelectAddressBottomSheet")
        }
    }

    private fun loadScrapItems(data: List<ScrapRateItems>) {
        scrapAdapter = SelectScrapListAdapter(data, this)
        binding.rvScraps.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@SelectScrapItemActivity)
            adapter = scrapAdapter
        }
    }

    override fun onAddressSelected(item: AddressData) {
        ModelPreferenceManager.put(item, Constants.SELECTED_ADDRESS)
        KeyValues.writeBoolean(Constants.IS_ADDRESS_SELECTED, true)
        updateAddressUI(item)
    }

    @SuppressLint("SetTextI18n")
    private fun updateAddressUI(item: AddressData) {
        if (KeyValues.readBoolean(Constants.IS_ADDRESS_SELECTED, false) == true) {
            binding.cvDeliveryAddress.root.visible()
//            selectedAddressId = item.id ?: 0
            showAddressUI(addressData = item)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onItemSelected(count: Int) {
        when (count) {
            0, 1 -> binding.tvItemCount.text = "$count item selected"
            else -> binding.tvItemCount.text = "$count items selected"
        }
    }
}