package com.manisoft.scraprushapp.ui.customer.myorders

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButtonToggleGroup
import com.manisoft.scraprushapp.R
import com.manisoft.scraprushapp.adapters.MyPickupsAdapter
import com.manisoft.scraprushapp.databinding.FragmentMyPickupsBinding
import com.manisoft.scraprushapp.models.MyOrderData
import com.manisoft.scraprushapp.mvvm.viewmodel.ScrapViewModel
import com.manisoft.scraprushapp.network.Resource
import com.manisoft.scraprushapp.utils.ActivityStatus
import com.manisoft.scraprushapp.utils.Constants
import com.manisoft.scraprushapp.utils.DialogUtils
import com.manisoft.scraprushapp.utils.KeyValues
import com.manisoft.scraprushapp.utils.OrderStatus
import com.manisoft.scraprushapp.utils.showToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.collections.ArrayList

class MyPickupsFragment : Fragment(), MyPickupsAdapter.OrderClickListener {
    private lateinit var binding: FragmentMyPickupsBinding
    private val viewModel: ScrapViewModel by viewModel()
    private lateinit var progressDialog: DialogUtils
    private lateinit var myOrdersAdapter: MyPickupsAdapter
    private var myOrderList: ArrayList<MyOrderData?> = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMyPickupsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getInstanceOfClasses()
        initMyOrdersRv()
        subscribe()
        viewModel.getMyOrder(KeyValues.authToken())
    }

    override fun onResume() {
        super.onResume()
        if (ActivityStatus.isChangesMade) {
            ActivityStatus.isChangesMade = false
            viewModel.getMyOrder(KeyValues.authToken())
        }
        binding.toggleGroup.addOnButtonCheckedListener(toggleBtnGrpListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.toggleGroup.removeOnButtonCheckedListener(toggleBtnGrpListener)
    }

    private fun subscribe() {
        viewModel.myOrderResponse.observe(viewLifecycleOwner) {
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
                        if (it.value.data?.isNotEmpty() == true) {
                            myOrderList.clear()
                            myOrderList.addAll(it.value.data ?: arrayListOf())
                            val pendingList =
                                myOrderList.filter { item -> item?.status == Constants.ORDER_CREATED || item?.status == Constants.ORDER_CONFIRMED || item?.status == Constants.WAITING_FOR_PICKUP }
                            if (pendingList.isEmpty()) {
                                showEmptyAnimation()
                            } else {
                                hideAnimation()
                                myOrdersAdapter.updateList(pendingList)
                            }
                        } else {
                            showEmptyAnimation()
                        }
                    } else {
                        showToast(it.value.message ?: "")
                    }
                }
            }
        }
    }

    private fun showEmptyAnimation() {
        binding.rvMyBookings.visibility = View.GONE
        binding.llAnimationView.visibility = View.VISIBLE
        binding.animationView.setAnimation(R.raw.empty_cart_no_text)
        binding.animationView.playAnimation()
    }

    private fun hideAnimation() {
        binding.rvMyBookings.visibility = View.VISIBLE
        binding.llAnimationView.visibility = View.GONE
    }

    private val toggleBtnGrpListener = MaterialButtonToggleGroup.OnButtonCheckedListener { _, checkedId, isChecked ->
        if (isChecked) {
            when (checkedId) {
                R.id.btn_pending -> filterMyOrderList(OrderStatus.PENDING)
                R.id.btn_completed -> filterMyOrderList(OrderStatus.COMPLETED)
                R.id.btn_cancelled -> filterMyOrderList(OrderStatus.CANCELLED)
            }
        }
    }

    private fun filterMyOrderList(status: OrderStatus) {
        val filteredList = when (status) {
            OrderStatus.PENDING -> myOrderList.filter { it?.status == Constants.ORDER_CREATED || it?.status == Constants.ORDER_CONFIRMED || it?.status == Constants.WAITING_FOR_PICKUP }
            OrderStatus.COMPLETED -> myOrderList.filter { it?.status == Constants.ORDER_COMPLETED }
            OrderStatus.CANCELLED -> myOrderList.filter { it?.status == Constants.ORDER_CANCELLED || it?.status == Constants.ORDER_REJECTED }
            else -> myOrderList.filter { it?.status == Constants.ORDER_CREATED }
        }
        if (filteredList.isEmpty()) showEmptyAnimation() else hideAnimation()
        myOrdersAdapter.updateList(filteredList)
    }

    private fun initMyOrdersRv() {
        myOrdersAdapter = MyPickupsAdapter(this)
        binding.rvMyBookings.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = myOrdersAdapter
        }
    }

    private fun getInstanceOfClasses() {
        progressDialog = DialogUtils(requireActivity())
    }

    override fun onOrderClicked(data: MyOrderData) {
        Intent(requireActivity(), OrderDetailsActivity::class.java).apply {
            putExtra("orderDetails", data)
            startActivity(this)
        }
    }
}