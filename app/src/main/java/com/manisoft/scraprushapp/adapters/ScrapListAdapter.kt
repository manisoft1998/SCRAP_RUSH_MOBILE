package com.manisoft.scraprushapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.manisoft.scraprushapp.databinding.ScrapRateRowItemBinding
import com.manisoft.scraprushapp.models.ScrapRateItems
import com.manisoft.scraprushapp.utils.Utils
import com.manisoft.scraprushapp.utils.priceFormat

class ScrapListAdapter(private var scrapList: ArrayList<ScrapRateItems>, private val listener: ScrapClickListener) : RecyclerView.Adapter<ScrapListAdapter.MyViewHolder>() {
    private var context: Context? = null

    interface ScrapClickListener {
        fun onScrapClicked(items: ScrapRateItems, position: Int)
    }

    inner class MyViewHolder(var binding: ScrapRateRowItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context
        return MyViewHolder(ScrapRateRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = scrapList[position]
        with(holder) {
            binding.tvScrapPrice.text = item.price.priceFormat()
            binding.tvScrapName.text = item.name
            binding.tvTamilScrapName.text = item.name_tamil
            val qty = "Qty :${item.variant_name}"
            binding.tvScrapQty.text = qty
            Utils.loadImage(context!!, binding.ivScrap, item.image ?: "")

            itemView.setOnClickListener {
                listener.onScrapClicked(item, position)
            }
        }
    }

    override fun getItemCount(): Int = scrapList.size

    @SuppressLint("NotifyDataSetChanged")
    fun clearScrapList() {
        scrapList.clear()
        notifyDataSetChanged()
    }

    fun deleteProduct(position: Int) {
        scrapList.removeAt(position)
        notifyItemRemoved(position)
    }
}