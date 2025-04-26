package com.manisoft.scraprushapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.manisoft.scraprushapp.R
import com.manisoft.scraprushapp.databinding.SelectScrapRowItemBinding
import com.manisoft.scraprushapp.models.ScrapRateItems
import com.manisoft.scraprushapp.utils.Utils
import com.manisoft.scraprushapp.utils.gone
import com.manisoft.scraprushapp.utils.priceFormat
import com.manisoft.scraprushapp.utils.visible

class SelectScrapListAdapter(private var scrapList: List<ScrapRateItems>, private val listener: OnItemSelectedListener) : RecyclerView.Adapter<SelectScrapListAdapter.MyViewHolder>() {
    private var context: Context? = null

    interface OnItemSelectedListener {
        fun onItemSelected(count: Int)
    }

    inner class MyViewHolder(var binding: SelectScrapRowItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context
        return MyViewHolder(SelectScrapRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = scrapList[position]
        with(holder) {
            binding.tvScrapPrice.text = item.price.priceFormat()
            binding.tvScrapName.text = item.name
            binding.tvTamilScrapName.text = item.name_tamil
            Utils.loadImage(context!!, binding.ivScrap, item.image ?: "")

            if (item.isSelected) {
                binding.ivSelected.visible()
                binding.cvScrapItem.setBackgroundColor(Color.parseColor("#99FFB6"))
            } else {
                binding.ivSelected.gone()
                binding.cvScrapItem.setBackgroundColor(Color.parseColor("#FFFFFF"))
            }

            itemView.setOnClickListener {
                updateScrapItem(position, !item.isSelected)
            }
        }
    }

    private fun updateScrapItem(position: Int, status: Boolean) {
        scrapList[position].isSelected = status
        val selectedScrapItems = scrapList.filter { it.isSelected }
        listener.onItemSelected(selectedScrapItems.size)
        notifyItemChanged(position)
    }

    fun selectedScrapList(): ArrayList<ScrapRateItems> = scrapList.filter { it.isSelected } as ArrayList<ScrapRateItems>

    override fun getItemCount(): Int = scrapList.size
}