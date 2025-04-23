package com.example.dailyburn.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dailyburn.R
import com.example.dailyburn.adapters.TipsAdapter
import com.example.dailyburn.models.FitnessTip
import com.example.dailyburn.viewmodels.TipsViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class TipsFragment : Fragment() {
    private lateinit var viewModel: TipsViewModel
    private lateinit var tipsAdapter: TipsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tips, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[TipsViewModel::class.java]

        // Setup RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvTips)
        tipsAdapter = TipsAdapter { tip ->
            showTipDetailsDialog(tip)
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = tipsAdapter
        }

        // Setup category filter chips
        val chipGroup = view.findViewById<ChipGroup>(R.id.chipGroupCategories)

        // Observe categories
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            chipGroup.removeAllViews()

            categories.forEach { category ->
                val chip = Chip(context).apply {
                    text = category
                    isCheckable = true
                    chipBackgroundColor =
                        resources.getColorStateList(R.color.chip_background_color, null)
                }

                chip.setOnClickListener {
                    viewModel.setCategory(category)
                }

                chipGroup.addView(chip)
            }

            // Set the "All" chip as selected by default
            (chipGroup.getChildAt(0) as? Chip)?.isChecked = true
        }

        // Observe tips
        viewModel.tips.observe(viewLifecycleOwner) { tips ->
            tipsAdapter.submitList(tips)
        }

        // Observe selected category to update UI
        viewModel.selectedCategory.observe(viewLifecycleOwner) { category ->
            view.findViewById<TextView>(R.id.tvCategoryTitle).text =
                if (category == "All") "All Fitness Tips" else "$category Tips"

            // Update chip selection
            for (i in 0 until chipGroup.childCount) {
                val chip = chipGroup.getChildAt(i) as Chip
                chip.isChecked = chip.text == category
            }
        }
    }

    private fun showTipDetailsDialog(tip: FitnessTip) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_tip_details, null)

        dialogView.findViewById<TextView>(R.id.tvDialogTitle).text = tip.title
        dialogView.findViewById<TextView>(R.id.tvDialogCategory).text = tip.category
        dialogView.findViewById<TextView>(R.id.tvDialogContent).text = tip.content

        tip.iconResId?.let { iconResId ->
            dialogView.findViewById<ImageView>(R.id.ivDialogIcon).apply {
                setImageResource(iconResId)
                visibility = View.VISIBLE
            }
        }

        MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Got it") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}