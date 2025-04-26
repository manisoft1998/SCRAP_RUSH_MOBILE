package com.manisoft.scraprushapp.adapters.adminadapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.manisoft.scraprushapp.databinding.NewScrapRowItemBinding
import com.manisoft.scraprushapp.models.NewProductData
import com.manisoft.scraprushapp.utils.Constants
import com.manisoft.scraprushapp.utils.Utils
import com.manisoft.scraprushapp.utils.priceFormat

class NewScrapAdapter(private val listener: NewScrapClickListeners) : RecyclerView.Adapter<NewScrapAdapter.MyViewHolder>() {
    private var context: Context? = null
    var newProductList: ArrayList<NewProductData> = arrayListOf()

    interface NewScrapClickListeners {
        fun onDeleteClicked(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context
        return MyViewHolder(NewScrapRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = newProductList[position]
        with(holder) {
            binding.tvScrapPrice.text = item.price.toString().priceFormat()
            binding.tvScrapName.text = item.name
            binding.tvTamilScrapName.text = item.name_tamil
            val qty = "Qty :${item.variant_name} ${getUnitName(item.unit_id ?: 0)}"
            binding.tvScrapQty.text = qty
            Utils.loadImage(context!!, binding.ivScrap, item.image_path ?: "")

            binding.ivDelete.setOnClickListener {
                listener.onDeleteClicked(position)
            }
        }
    }

    inner class MyViewHolder(var binding: NewScrapRowItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int = newProductList.size

    private fun getUnitName(unitId: Int): String {
        return when (unitId) {
            Constants.GRAMS -> "Grams"
            Constants.KILO_GRAMS -> "Kg"
            Constants.PIECE -> "Piece"
            Constants.LITTER -> "Ltr"
            else -> "Unknown"
        }
    }

    fun addNewProduct(item: NewProductData) {
        newProductList.add(item)
        notifyItemInserted(newProductList.size)
    }

    fun deleteProduct(position: Int) {
        newProductList.removeAt(position)
        notifyItemRemoved(position)
    }
}