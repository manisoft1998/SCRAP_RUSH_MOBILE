package com.manisoft.scraprushapp.ui.admin.orderlist

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.clss.serviceticket.utils.paginationlistener.PaginationScrollListener
import com.google.android.material.button.MaterialButtonToggleGroup
import com.manisoft.scraprushapp.R
import com.manisoft.scraprushapp.adapters.adminadapters.AdminOrdersAdapter
import com.manisoft.scraprushapp.databinding.ActivityAdminOrderListBinding
import com.manisoft.scraprushapp.models.AdminOrderListRequest
import com.manisoft.scraprushapp.models.OrderCount
import com.manisoft.scraprushapp.models.OrderLists
import com.manisoft.scraprushapp.mvvm.viewmodel.AdminViewModel
import com.manisoft.scraprushapp.network.Resource
import com.manisoft.scraprushapp.ui.admin.orderdetails.AdminOrderDetailsActivity
import com.manisoft.scraprushapp.utils.ActivityStatus
import com.manisoft.scraprushapp.utils.Constants
import com.manisoft.scraprushapp.utils.DialogUtils
import com.manisoft.scraprushapp.utils.KeyValues
import com.manisoft.scraprushapp.utils.showToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class AdminOrderListActivity : AppCompatActivity(), AdminOrdersAdapter.OrderClickListener {
    private lateinit var binding: ActivityAdminOrderListBinding

    private val viewModel: AdminViewModel by viewModel()
    private lateinit var progressDialog: DialogUtils
    private lateinit var adminOrdersAdapter: AdminOrdersAdapter

    //pagination ref
    private var pageNumber: Int = 1
    var isLoading: Boolean = false
    var isLastPage: Boolean = false
    private val pageSizeLimit = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminOrderListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getInstanceOfClasses()
        initMyOrdersRv()
        subscribe()
        viewModel.getAdminOrderList(KeyValues.authToken(), AdminOrderListRequest(page = 1, page_limit = 20, status = Constants.ORDER_CREATED))
    }

    private fun getInstanceOfClasses() {
        progressDialog = DialogUtils(this)
    }

    private fun subscribe() {
        viewModel.adminOrdersResponse.observe(this) {
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
                        if (it.value.data?.order_lists?.isNotEmpty() == true) {
                            hideAnimation()
                            isLoading = false

                            setOrderStatusCount(it.value.data?.order_count)
                            isLastPage = it.value.data?.is_last_page ?: false

                            adminOrdersAdapter.setOrderList(it.value.data?.order_lists ?: arrayListOf())

                        } else {
                            showEmptyAnimation()
                        }
                    } else {
                        showEmptyAnimation()
                        showToast(it.value.message ?: "")
                    }
                }
            }
        }

        viewModel.adminOrdersPaginationResponse.observe(this) {
            when (it) {
                is Resource.Failure -> {
                    adminOrdersAdapter.removeLoadingView()
                    showToast(it.errorBody?.message ?: "")
                }

                is Resource.Loading -> {
                    adminOrdersAdapter.addLoadingView()
                }

                is Resource.Success -> {
                    if (it.value.status == true) {
                        if (it.value.data?.order_lists?.isNotEmpty() == true) {
                            hideAnimation()
                            isLoading = false

                            setOrderStatusCount(it.value.data?.order_count)
                            if (it.value.data?.is_last_page == false) {
                                isLastPage = false
                            } else if (it.value.data?.is_last_page == true) {
                                isLastPage = true
                                showToast("No more items..!")
                            }
                            if (it.value.data?.order_lists != null) {
                                adminOrdersAdapter.removeLoadingView()
                                adminOrdersAdapter.setOrderList(it.value.data?.order_lists ?: arrayListOf())
                            }

                        } else {
                            showEmptyAnimation()
                        }
                    } else {
                        showEmptyAnimation()
                        showToast(it.value.message ?: "")
                    }
                }
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun setOrderStatusCount(orderCount: OrderCount?) {
        binding.tvTitle.text = "Order List - ${orderCount?.total_orders}"
        orderCount?.status_wise?.forEach {
            when (it?.status_code ?: 0) {
                Constants.ORDER_CREATED -> binding.btnPending.text = "Pending (${it?.total_orders})"
                Constants.ORDER_CONFIRMED -> binding.btnConfirmed.text = "Confirmed (${it?.total_orders})"
                Constants.WAITING_FOR_PICKUP -> binding.btnPickup.text = "Pickup (${it?.total_orders})"
                Constants.ORDER_REJECTED -> binding.btnRejected.text = "Completed (${it?.total_orders})"
                Constants.ORDER_CANCELLED -> binding.btnCancelled.text = "Cancelled (${it?.total_orders})"
                Constants.ORDER_COMPLETED -> binding.btnCompleted.text = "Rejected (${it?.total_orders})"
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
                R.id.btn_pending -> filterMyOrderList(Constants.ORDER_CONFIRMED)
                R.id.btn_confirmed -> filterMyOrderList(Constants.ORDER_CONFIRMED)
                R.id.btn_pickup -> filterMyOrderList(Constants.WAITING_FOR_PICKUP)
                R.id.btn_rejected -> filterMyOrderList(Constants.ORDER_REJECTED)
                R.id.btn_completed -> filterMyOrderList(Constants.ORDER_COMPLETED)
                R.id.btn_cancelled -> filterMyOrderList(Constants.ORDER_CANCELLED)
            }
        }
    }

    private fun filterMyOrderList(status: Int) {
        pageNumber = 1
        adminOrdersAdapter.clearList()
        viewModel.getAdminOrderList(KeyValues.authToken(), AdminOrderListRequest(page = pageNumber, page_limit = pageSizeLimit, status = status))
    }


    private fun initMyOrdersRv() {
        adminOrdersAdapter = AdminOrdersAdapter(this, this)
        val linearLayoutManager = LinearLayoutManager(this@AdminOrderListActivity)
        binding.rvMyBookings.apply {
            layoutManager = linearLayoutManager
            itemAnimator = DefaultItemAnimator()
            setHasFixedSize(true)
            adapter = adminOrdersAdapter
        }

        binding.rvMyBookings.addOnScrollListener(object : PaginationScrollListener(linearLayoutManager) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                isLoading = true
                loadMore(++pageNumber)
            }
        })
    }

    private fun loadMore(pageNumber: Int) {
        viewModel.getAdminOrderPaginationList(KeyValues.authToken(), AdminOrderListRequest(page = pageNumber, page_limit = pageSizeLimit, status = Constants.ORDER_CREATED))
    }

    override fun onResume() {
        super.onResume()
        if (ActivityStatus.isAdminUpdatedStatus) {
            ActivityStatus.isAdminUpdatedStatus = false
            adminOrdersAdapter.clearList()
            viewModel.getAdminOrderList(KeyValues.authToken(), AdminOrderListRequest(page = 1, page_limit = 20, status = Constants.ORDER_CREATED))
        }
        binding.toggleGroup.addOnButtonCheckedListener(toggleBtnGrpListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.toggleGroup.removeOnButtonCheckedListener(toggleBtnGrpListener)
    }


    override fun onOrderClicked(data: OrderLists) {
        Intent(this@AdminOrderListActivity, AdminOrderDetailsActivity::class.java).apply {
            putExtra("orderDetails", data)
            startActivity(this)
        }
    }
}