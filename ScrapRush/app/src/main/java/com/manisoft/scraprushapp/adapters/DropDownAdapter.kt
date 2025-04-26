package com.manisoft.scraprushapp.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.manisoft.scraprushapp.R
import com.manisoft.scraprushapp.databinding.DropDownRowItemBinding

class DropDownAdapter(private val onItemClick: DropDownItemClickListener, private val type: String, private val stringList: ArrayList<String>, private var selection: String = "") :
    RecyclerView.Adapter<DropDownAdapter.MyViewHolder>() {


    interface DropDownItemClickListener {
        fun onDropDownItemClicked(selectedItem: String, position: Int, type: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(DropDownRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        with(holder) {
            if (selection.lowercase().trim() == stringList[position].lowercase().trim()) {
                binding.tvName.text = stringList[position]
                binding.tvName.setTextColor(Color.parseColor("#4D6EAE"))
                binding.tvName.setBackgroundResource(R.drawable.selected_bg_border)
            } else {
                binding.tvName.text = stringList[position]
                binding.tvName.setTextColor(Color.parseColor("#FF000000"))
                binding.tvName.setBackgroundResource(R.drawable.dd_bg_border)
            }

            binding.tvName.setOnClickListener {
                onItemClick.onDropDownItemClicked(stringList[position], position, type)
            }
        }
    }

    override fun getItemCount(): Int = stringList.size

    inner class MyViewHolder(var binding: DropDownRowItemBinding) : RecyclerView.ViewHolder(binding.root)

}