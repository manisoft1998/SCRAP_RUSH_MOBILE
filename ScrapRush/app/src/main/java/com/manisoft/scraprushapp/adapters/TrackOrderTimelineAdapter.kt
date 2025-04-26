package com.manisoft.scraprushapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.manisoft.scraprushapp.R
import com.manisoft.scraprushapp.databinding.TrackOrderTimelineRowItemBinding
import com.manisoft.scraprushapp.models.StatusHistory
import com.manisoft.scraprushapp.utils.VectorDrawableUtils
import com.manisoft.scraprushapp.utils.displayDate

class TrackOrderTimelineAdapter(private val statusHistoryList: List<StatusHistory?>) : RecyclerView.Adapter<TrackOrderTimelineAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackOrderTimelineAdapter.MyViewHolder {
        return MyViewHolder(TrackOrderTimelineRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TrackOrderTimelineAdapter.MyViewHolder, position: Int) {
        val item = statusHistoryList[position]
        with(holder) {
            binding.tvStatus.text = item?.name ?: "--"
            binding.tvDate.text = item?.last_updated?.displayDate() ?: "--"
            binding.timeline.marker = VectorDrawableUtils.getDrawable(holder.itemView.context, R.drawable.ic_marker_inactive, ContextCompat.getColor(holder.itemView.context, R.color.grey))
            binding.timeline.initLine(holder.itemViewType)
        }
    }

    override fun getItemCount(): Int = statusHistoryList.size

    inner class MyViewHolder(var binding: TrackOrderTimelineRowItemBinding) : RecyclerView.ViewHolder(binding.root)

}