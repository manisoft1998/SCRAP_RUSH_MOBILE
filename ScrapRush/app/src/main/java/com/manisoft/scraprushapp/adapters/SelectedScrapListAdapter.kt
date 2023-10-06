package com.manisoft.scraprushapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.manisoft.scraprushapp.R
import com.manisoft.scraprushapp.databinding.SelectScrapRowItemBinding
import com.manisoft.scraprushapp.models.ScrapRateItems
import com.manisoft.scraprushapp.utils.Utils

class SelectedScrapListAdapter(private var addressList: ArrayList<ScrapRateItems>) : RecyclerView.Adapter<SelectedScrapListAdapter.MyViewHolder>() {
    private var context: Context? = null

    inner class MyViewHolder(var binding: SelectScrapRowItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context
        return MyViewHolder(SelectScrapRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = addressList[position]
        with(holder) {
            binding.tvScrapPrice.text = item.price
            binding.tvScrapName.text = item.name
            Utils.loadAssetImage(context!!, binding.ivScrap, R.drawable.plastic)
        }
    }

    override fun getItemCount(): Int = addressList.size
}