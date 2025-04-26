package com.manisoft.scraprushapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.manisoft.scraprushapp.databinding.MyOrdersRowItemBinding
import com.manisoft.scraprushapp.models.MyOrderData
import com.manisoft.scraprushapp.utils.Utils
import com.manisoft.scraprushapp.utils.displayDate

class MyPickupsAdapter(private var listener: OrderClickListener) : RecyclerView.Adapter<MyPickupsAdapter.MyViewHolder>() {
    private var myOrderList: ArrayList<MyOrderData?> = arrayListOf()

    interface OrderClickListener {
        fun onOrderClicked(data: MyOrderData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = MyOrdersRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = myOrderList[position]
        with(holder) {
            binding.tvOrderedDate.text = item?.ordered_at?.displayDate() ?: "--"
            binding.tvPickupDate.text = item?.pick_up_date?.displayDate() ?: "--"

            binding.tvSelected.text = Utils.showScrapLists(item?.ordered_items)
            binding.tvOrderId.text = item?.order_id ?: "--"

            /*         when (item?.status) {
        Constants.ORDER_COMPLETED -> {
            binding.flEvent.setBackgroundColor(ContextCompat.getColor(context, R.color.booking_completed))
        }

        Constants.ORDER_CREATED, Constants.ORDER_CONFIRMED, Constants.WAITING_FOR_PICKUP -> {
            binding.flEvent.setBackgroundColor(ContextCompat.getColor(context, R.color.booking_pending))
        }

        Constants.ORDER_CANCELLED, Constants.ORDER_REJECTED -> {
            binding.flEvent.setBackgroundColor(ContextCompat.getColor(context, R.color.booking_cancelled))
        }
    }
    binding.tvEvent.text = "Order id ${item?.order_id ?: "--"}"*/

            itemView.setOnClickListener {
                item?.let { it1 -> listener.onOrderClicked(it1) }
            }
        }
    }

    override fun getItemCount(): Int = myOrderList.size


    inner class MyViewHolder(var binding: MyOrdersRowItemBinding) : RecyclerView.ViewHolder(binding.root)

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(orderList: List<MyOrderData?>) {
        myOrderList.clear()
        myOrderList.addAll(orderList)
        notifyDataSetChanged()
    }
}