package com.example.dailyburn.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dailyburn.R
import com.example.dailyburn.models.BmiRecord
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BmiHistoryAdapter :
    ListAdapter<BmiRecord, BmiHistoryAdapter.BmiViewHolder>(BmiDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BmiViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bmi_history, parent, false)
        return BmiViewHolder(view)
    }

    override fun onBindViewHolder(holder: BmiViewHolder, position: Int) {
        val record = getItem(position)
        holder.bind(record)
    }

    class BmiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate = itemView.findViewById<TextView>(R.id.tvDate)
        private val tvBmiValue = itemView.findViewById<TextView>(R.id.tvBmiValue)
        private val tvBmiCategory = itemView.findViewById<TextView>(R.id.tvBmiCategory)
        private val tvWeight = itemView.findViewById<TextView>(R.id.tvWeight)
        private val tvHeight = itemView.findViewById<TextView>(R.id.tvHeight)

        fun bind(record: BmiRecord) {
            // Format date
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val date = Date(record.timestamp)
            tvDate.text = dateFormat.format(date)

            // Format BMI
            tvBmiValue.text = String.format("%.1f", record.bmi)

            // Set category
            tvBmiCategory.text = record.category

            // Set weight and height
            tvWeight.text = "${record.weight} kg"
            tvHeight.text = "${record.height} cm"
        }
    }

    class BmiDiffCallback : DiffUtil.ItemCallback<BmiRecord>() {
        override fun areItemsTheSame(oldItem: BmiRecord, newItem: BmiRecord): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BmiRecord, newItem: BmiRecord): Boolean {
            return oldItem == newItem
        }
    }
}