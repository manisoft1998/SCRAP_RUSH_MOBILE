package com.manisoft.scraprushapp.adapters.adminadapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.location.Location
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clss.serviceticket.utils.paginationlistener.RecylerViewType
import com.manisoft.scraprushapp.databinding.AdminOrderListRowItemBinding
import com.manisoft.scraprushapp.databinding.RvProgressBarBinding
import com.manisoft.scraprushapp.models.OrderLists
import com.manisoft.scraprushapp.utils.Utils
import com.manisoft.scraprushapp.utils.displayDate
import com.manisoft.scraprushapp.utils.gone

class AdminOrdersAdapter(private var listener: OrderClickListener, private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var myOrderList: ArrayList<OrderLists?> = arrayListOf()
    private var isLoadingAdded: Boolean = false
    private lateinit var mContext: Context

    interface OrderClickListener {
        fun onOrderClicked(data: OrderLists)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        mContext = parent.context
        return if (viewType == RecylerViewType.VIEW_TYPE_ITEM) {
            MyViewHolder(AdminOrderListRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            val binding = RvProgressBarBinding.inflate(LayoutInflater.from(mContext), parent, false)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                binding.progressBar.indeterminateDrawable.colorFilter = BlendModeColorFilter(Color.BLACK, BlendMode.SRC_ATOP)
            } else {
                binding.progressBar.indeterminateDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY)
            }
            LoadingViewHolder(binding)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == RecylerViewType.VIEW_TYPE_ITEM) {

            val item = myOrderList[position]
            with(holder as MyViewHolder) {
                binding.tvOrderedDate.text = item?.ordered_at?.displayDate() ?: "--"
                binding.tvPickupDate.text = item?.pick_up_date?.displayDate() ?: "--"

                binding.tvSelectedScraps.text = Utils.showScrapLists(item?.ordered_items)

                binding.tvOrderId.text = item?.order_id ?: "--"
                binding.tvCustomerName.text = item?.address?.name ?: "--"
                binding.tvOrderStatus.text = Utils.getCurrentStatus(item?.status ?: 0)

                val location = Location("Location 1")
                location.latitude = item?.address?.latitude?.toDouble() ?: 0.0
                location.longitude = item?.address?.longitude?.toDouble() ?: 0.0
                binding.tvTotalDistance.text = Utils.calculateDistance(location)
//            binding.tvTotalDistance.text = LocationDistanceUtils.calculateDistanceInKm(location)

                if (item?.address?.mobile.isNullOrEmpty() || item?.address?.mobile == "0") {
                    binding.tvPhone.text = "Mobile number not available"
                    binding.ivPhone.gone()
                } else {
                    binding.tvPhone.text = "+91 ${item?.address?.mobile ?: "--"}"

                }

                itemView.setOnClickListener {
                    item?.let { it1 -> listener.onOrderClicked(it1) }
                }
                binding.tvDirection.setOnClickListener {
                    Utils.navigateToDirection(context, location)
                }
            }
        }
    }

    override fun getItemCount(): Int = myOrderList.size

    override fun getItemViewType(position: Int): Int {
        return if (myOrderList.size - 1 == position && isLoadingAdded) {
            RecylerViewType.VIEW_TYPE_LOADING
        } else {
            RecylerViewType.VIEW_TYPE_ITEM
        }
    }


    inner class MyViewHolder(var binding: AdminOrderListRowItemBinding) : RecyclerView.ViewHolder(binding.root)

    inner class LoadingViewHolder(binding: RvProgressBarBinding) : RecyclerView.ViewHolder(binding.root)

    @SuppressLint("NotifyDataSetChanged")
    fun setOrderList(item: ArrayList<OrderLists?>) {
        myOrderList.addAll(item)
        notifyDataSetChanged()
    }

    fun removeLoadingView() {
        isLoadingAdded = false
        myOrderList.removeAt(myOrderList.size - 1)
        notifyItemRemoved(myOrderList.size)
    }

    fun addLoadingView() {
        isLoadingAdded = true
        myOrderList.add(OrderLists())
        notifyItemInserted(myOrderList.size - 1)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearList() {
        myOrderList.clear()
        notifyDataSetChanged()
    }
}


