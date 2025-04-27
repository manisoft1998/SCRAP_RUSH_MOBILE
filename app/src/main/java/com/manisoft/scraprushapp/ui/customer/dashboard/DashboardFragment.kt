package com.manisoft.scraprushapp.ui.customer.dashboard

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.recyclerview.widget.LinearLayoutManager
import com.manisoft.scraprushapp.adapters.DashboardAddressListAdapter
import com.manisoft.scraprushapp.databinding.FragmentDashboardBinding
import com.manisoft.scraprushapp.models.AddressData
import com.manisoft.scraprushapp.mvvm.viewmodel.AddressViewModel
import com.manisoft.scraprushapp.network.Resource
import com.manisoft.scraprushapp.ui.customer.locationpicker.LocationPickerActivity
import com.manisoft.scraprushapp.ui.customer.recyclestore.RecycleStoreActivity
import com.manisoft.scraprushapp.ui.customer.requestpickup.SelectScrapItemActivity
import com.manisoft.scraprushapp.ui.customer.scraprate.ScrapRateActivity
import com.manisoft.scraprushapp.utils.Constants
import com.manisoft.scraprushapp.utils.KeyValues
import com.manisoft.scraprushapp.utils.ModelPreferenceManager
import com.manisoft.scraprushapp.utils.Utils
import com.manisoft.scraprushapp.utils.gone
import com.manisoft.scraprushapp.utils.showToast
import com.manisoft.scraprushapp.utils.showUnAuthAlert
import com.manisoft.scraprushapp.utils.visible
import org.koin.androidx.viewmodel.ext.android.viewModel


class DashboardFragment : Fragment(), DashboardAddressListAdapter.OnAddressClickListener {
    private lateinit var binding: FragmentDashboardBinding

    private val viewModel: AddressViewModel by viewModel()
    private lateinit var addressAdapter: DashboardAddressListAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        updateActionBarAddressUI()
        setClickListeners()
        subscribe()
    }

    @SuppressLint("SetTextI18n")
    private fun updateActionBarAddressUI() {
        if (KeyValues.readBoolean(Constants.IS_ADDRESS_SELECTED, false) == true) {
            val addressItem = ModelPreferenceManager.get(Constants.SELECTED_ADDRESS) ?: AddressData()
            binding.appBar.tvTitle.text = "${addressItem.address_type} - ${addressItem.formatted_address}"
        } else {
            binding.appBar.tvTitle.text = "Add Your Address"
        }

        binding.tvGreetings.text = "${Utils.showGreetings()}, ${KeyValues.readString(Constants.FULL_NAME, "") ?: "--"} !"
    }

    private fun setClickListeners() {
        binding.dashboardMenu.cvScrapRate.setOnClickListener {
            startActivity(Intent(requireActivity(), ScrapRateActivity::class.java))
        }
        binding.dashboardMenu.cvRequestPickup.setOnClickListener {
            startActivity(Intent(requireActivity(), SelectScrapItemActivity::class.java))
        }
        binding.dashboardMenu.cvShopLocation.setOnClickListener {
            startActivity(Intent(requireActivity(), RecycleStoreActivity::class.java))
        }

        var isExpandable = true
        binding.appBar.linearLayout.setOnClickListener {
            if (isExpandable) {
                viewModel.addressList(KeyValues.authToken())
                binding.appBar.llExpandable.visible()
            } else {
                binding.appBar.llExpandable.gone()
                binding.appBar.rvAddress.gone()
            }
            val rotateAnimation = RotateAnimation(90.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
            binding.appBar.ivArrow.startAnimation(rotateAnimation)
            isExpandable = !isExpandable
        }

        binding.appBar.rlAddNewAddress.setOnClickListener {
            binding.appBar.llExpandable.gone()
            binding.appBar.rvAddress.gone()
            startActivity(Intent(requireActivity(), LocationPickerActivity::class.java))
        }
    }

    @SuppressLint("SetTextI18n")
    private fun subscribe() {
        viewModel.addressListResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Failure -> {
                    binding.appBar.addressProgressbar.gone()
                    if (it.errorCode == 401) showUnAuthAlert() else showToast(it.errorBody.toString())
                }

                is Resource.Loading -> {
                    binding.appBar.addressProgressbar.visible()
                }

                is Resource.Success -> {
                    binding.appBar.addressProgressbar.gone()
                    if (it.value.status) {
                        if (it.value.data.isEmpty()) {
                            binding.appBar.rvAddress.gone()
                            binding.appBar.tvAddressDataInfo.text = "No Address data"
                        } else {
                            binding.appBar.rvAddress.visible()
                            binding.appBar.tvAddressDataInfo.gone()
                            showAddressList(it.value.data)
                        }
                    } else {
                        binding.appBar.tvAddressDataInfo.visible()
                        binding.appBar.rvAddress.gone()
                        binding.appBar.tvAddressDataInfo.text = "No Address data"
                        showToast(it.value.message ?: "")
                    }
                }
            }
        }
    }

    private fun showAddressList(data: List<AddressData>) {
        addressAdapter = DashboardAddressListAdapter(data as ArrayList<AddressData>, this@DashboardFragment, true)
        binding.appBar.rvAddress.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = addressAdapter
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onAddressClicked(item: AddressData) {
        binding.appBar.llExpandable.gone()
        binding.appBar.rvAddress.gone()
        binding.appBar.tvTitle.text = "${item.address_type} - ${item.formatted_address}"
        ModelPreferenceManager.put(item, Constants.SELECTED_ADDRESS)
        KeyValues.writeBoolean(Constants.IS_ADDRESS_SELECTED, true)
    }

    override fun onDeleteClicked(item: AddressData, position: Int) {}

}