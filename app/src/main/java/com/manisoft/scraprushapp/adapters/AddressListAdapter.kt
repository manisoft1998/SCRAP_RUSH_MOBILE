package com.manisoft.scraprushapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.manisoft.scraprushapp.databinding.AddressRowItemBinding
import com.manisoft.scraprushapp.models.AddressData

class AddressListAdapter(private var addressList: ArrayList<AddressData>, private val listener: OnAddressClickListener, private val showMenu: Boolean) :
    RecyclerView.Adapter<AddressListAdapter.MyViewHolder>() {

    interface OnAddressClickListener {
        fun onAddressClicked(item: AddressData)
        fun onDeleteClicked(item: AddressData, position: Int)
    }

    inner class MyViewHolder(var binding: AddressRowItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = AddressRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = addressList[position]
        with(holder) {
            when {
                showMenu -> binding.ivMenuIcon.visibility = View.VISIBLE
                else -> binding.ivMenuIcon.visibility = View.GONE
            }
//            binding.tvAddressTitle.text = item.address ?: ""
            if (item.address_type?.lowercase() != "other") {
                binding.tvAddressTitle.text = item.address_type
            } else {
                binding.tvAddressTitle.text = item.address_type + "-" + item.address_type_label
            }
            val subTitleAddress = item.city + "," + item.zipcode + "," + item.state
            binding.tvAddressSubtitle.text = item.formatted_address

            binding.ivMenuIcon.setOnClickListener {
                listener.onDeleteClicked(item, position)
            }
            itemView.setOnClickListener {
                listener.onAddressClicked(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return addressList.size
    }

    fun deleteAddressFromList(position: Int) {
        addressList.removeAt(position)
        notifyDataSetChanged()
    }
}