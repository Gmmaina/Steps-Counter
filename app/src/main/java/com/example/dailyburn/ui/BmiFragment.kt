package com.example.dailyburn.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dailyburn.R
import com.example.dailyburn.adapters.BmiHistoryAdapter
import com.example.dailyburn.utils.BmiCalculator
import com.example.dailyburn.viewmodels.BmiViewModel
import com.google.android.material.snackbar.Snackbar

class BmiFragment : Fragment() {

    private lateinit var viewModel: BmiViewModel
    private lateinit var adapter: BmiHistoryAdapter

    private lateinit var etWeight: EditText
    private lateinit var etHeight: EditText
    private lateinit var btnCalculate: Button
    private lateinit var btnSave: Button
    private lateinit var tvBmiResult: TextView
    private lateinit var tvBmiCategory: TextView
    private lateinit var tvHealthRisk: TextView
    private lateinit var cardResult: CardView
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bmi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[BmiViewModel::class.java]

        // Initialize views
        etWeight = view.findViewById(R.id.etWeight)
        etHeight = view.findViewById(R.id.etHeight)
        btnCalculate = view.findViewById(R.id.btnCalculate)
        btnSave = view.findViewById(R.id.btnSave)
        tvBmiResult = view.findViewById(R.id.tvBmiResult)
        tvBmiCategory = view.findViewById(R.id.tvBmiCategory)
        tvHealthRisk = view.findViewById(R.id.tvHealthRisk)
        cardResult = view.findViewById(R.id.cardResult)
        progressBar = view.findViewById(R.id.progressBar)
        recyclerView = view.findViewById(R.id.recyclerHistory)

        // Setup recycler view
        adapter = BmiHistoryAdapter()
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        val params = recyclerView.layoutParams
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        recyclerView.layoutParams = params

        // Setup calculate button
        btnCalculate.setOnClickListener {
            calculateBmi()
        }

        // Setup save button
        btnSave.setOnClickListener {
            viewModel.saveBmiRecord()
        }

        // Observe current BMI
        viewModel.currentBmi.observe(viewLifecycleOwner) { bmiRecord ->
            if (bmiRecord != null) {
                // Show the result card
                cardResult.visibility = View.VISIBLE

                // Update the UI
                tvBmiResult.text = String.format("%.1f", bmiRecord.bmi)
                tvBmiCategory.text = bmiRecord.category

                // Show health risk
                val healthRisk = BmiCalculator.getHealthRisk(bmiRecord.category)
                tvHealthRisk.text = healthRisk

                // Set color based on category
                val color = BmiCalculator.getCategoryColor(bmiRecord.category)
                tvBmiCategory.setTextColor(color)

                // Prefill the input fields
                if (etWeight.text.toString().isEmpty()) {
                    etWeight.setText(bmiRecord.weight.toString())
                }
                if (etHeight.text.toString().isEmpty()) {
                    etHeight.setText(bmiRecord.height.toString())
                }

                // Enable the save button
                btnSave.isEnabled = true
            } else {
                // Hide the result card
                cardResult.visibility = View.GONE
                btnSave.isEnabled = false
            }
        }

        // Observe BMI history
        viewModel.bmiHistory.observe(viewLifecycleOwner) { history ->
            adapter.submitList(history)

            recyclerView.visibility = View.VISIBLE

            if (history.isEmpty()) {
                showEmptyHistoryMessage()
            }
        }

        viewModel.loadBmiHistory()

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            btnCalculate.isEnabled = !isLoading
            btnSave.isEnabled = !isLoading && viewModel.currentBmi.value != null
        }

        // Observe save success
        viewModel.saveSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Snackbar.make(view, "BMI record saved successfully", Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(view, "Failed to save BMI record", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun showEmptyHistoryMessage() {
        val emptyView = view?.findViewById<TextView>(R.id.tvEmptyHistory)
        emptyView?.visibility = View.VISIBLE
    }

    private fun calculateBmi() {
        val weightStr = etWeight.text.toString()
        val heightStr = etHeight.text.toString()

        if (weightStr.isEmpty() || heightStr.isEmpty()) {
            Snackbar.make(requireView(), "Please enter weight and height", Snackbar.LENGTH_SHORT)
                .show()
            return
        }

        try {
            val weight = weightStr.toFloat()
            val height = heightStr.toFloat()

            if (weight <= 0 || height <= 0) {
                Snackbar.make(
                    requireView(),
                    "Weight and height must be positive",
                    Snackbar.LENGTH_SHORT
                ).show()
                return
            }

            viewModel.calculateBmi(weight, height)
        } catch (e: NumberFormatException) {
            Snackbar.make(requireView(), "Please enter valid numbers", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when fragment is resumed
        viewModel.loadLatestBmi()
        viewModel.loadBmiHistory()
    }
}