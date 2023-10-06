package com.manisoft.scraprushapp.utils.locationpickerutils

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.manisoft.scraprushapp.databinding.PlacePredictionItemBinding
import java.util.*

class PlacePredictionAdapter : RecyclerView.Adapter<PlacePredictionAdapter.PlacePredictionViewHolder>() {
    private val predictions: MutableList<AutocompletePrediction> = ArrayList()
    var onPlaceClickListener: ((AutocompletePrediction) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacePredictionViewHolder {

        val binding = PlacePredictionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlacePredictionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlacePredictionViewHolder, position: Int) {
        val rnd = Random()
        val currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        holder.binding.fabLocation.backgroundTintList = ColorStateList.valueOf(currentColor)

        val place = predictions[position]
        holder.binding.textViewTitle.text = place.getPrimaryText(null)
        holder.binding.textViewAddress.text = place.getSecondaryText(null)
        holder.itemView.setOnClickListener {
            onPlaceClickListener?.invoke(place)
        }
    }

    override fun getItemCount(): Int {
        return predictions.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setPredictions(predictions: List<AutocompletePrediction>?) {
        this.predictions.clear()
        this.predictions.addAll(predictions!!)
        notifyDataSetChanged()
    }

    inner class PlacePredictionViewHolder(var binding: PlacePredictionItemBinding) : ViewHolder(binding.root)

}