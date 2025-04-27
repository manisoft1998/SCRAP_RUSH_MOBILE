package com.manisoft.scraprushapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.manisoft.scraprushapp.R
import com.manisoft.scraprushapp.databinding.ScrapRateRowItemBinding
import com.manisoft.scraprushapp.models.ScrapRateItems
import com.manisoft.scraprushapp.utils.Utils
import com.manisoft.scraprushapp.utils.priceFormat

class ScrapRateListAdapter(private var addressList: List<ScrapRateItems>) : RecyclerView.Adapter<ScrapRateListAdapter.MyViewHolder>() {
    private var context: Context? = null

    inner class MyViewHolder(var binding: ScrapRateRowItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context
        return MyViewHolder(ScrapRateRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = addressList[position]
        with(holder) {
            binding.tvScrapPrice.text = item.price.priceFormat()
            binding.tvScrapName.text = item.name
            val qty = "Qty :${item.variant}"
            binding.tvScrapQty.text = qty
            Utils.loadAssetImage(context!!, binding.ivScrap, R.drawable.plastic)
        }
    }

    override fun getItemCount(): Int = addressList.size
}