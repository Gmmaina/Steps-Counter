package com.example.dailyburn.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dailyburn.R
import com.example.dailyburn.models.FitnessTip

class TipsAdapter(private val onTipClick: (FitnessTip) -> Unit) :
    ListAdapter<FitnessTip, TipsAdapter.TipViewHolder>(TipDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_fitness_tip, parent, false)
        return TipViewHolder(view)
    }

    override fun onBindViewHolder(holder: TipViewHolder, position: Int) {
        val tip = getItem(position)
        holder.bind(tip, onTipClick)
    }

    class TipViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle = itemView.findViewById<TextView>(R.id.tvTipTitle)
        private val tvCategory = itemView.findViewById<TextView>(R.id.tvTipCategory)
        private val ivIcon = itemView.findViewById<ImageView>(R.id.ivTipIcon)

        fun bind(tip: FitnessTip, onTipClick: (FitnessTip) -> Unit) {
            tvTitle.text = tip.title
            tvCategory.text = tip.category

            // Set icon if available
            tip.iconResId?.let { iconResId ->
                ivIcon.setImageResource(iconResId)
                ivIcon.visibility = View.VISIBLE
            } ?: run {
                ivIcon.visibility = View.GONE
            }

            // Set click listener
            itemView.setOnClickListener { onTipClick(tip) }
        }
    }

    class TipDiffCallback : DiffUtil.ItemCallback<FitnessTip>() {
        override fun areItemsTheSame(oldItem: FitnessTip, newItem: FitnessTip): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FitnessTip, newItem: FitnessTip): Boolean {
            return oldItem == newItem
        }
    }
}