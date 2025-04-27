package com.manisoft.scraprushapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.manisoft.scraprushapp.databinding.SelectedScrapRowItemBinding
import com.manisoft.scraprushapp.models.ScrapRateItems
import com.manisoft.scraprushapp.utils.priceFormat

class SelectedScrapListAdapter(private var scrapList: ArrayList<ScrapRateItems>) : RecyclerView.Adapter<SelectedScrapListAdapter.MyViewHolder>() {
    private var context: Context? = null

    inner class MyViewHolder(var binding: SelectedScrapRowItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context
        return MyViewHolder(SelectedScrapRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = scrapList[position]
        with(holder) {
            binding.tvScrapPrice.text = "${item.price.priceFormat()}/${item.variant_name}"
            binding.tvScrapName.text = item.name
        }
    }

    override fun getItemCount(): Int = scrapList.size
}