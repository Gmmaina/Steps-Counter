package com.example.dailyburn.ui


import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.dailyburn.R
import com.example.dailyburn.models.StepData
import com.example.dailyburn.services.StepCounterService
import com.example.dailyburn.viewmodels.StepsViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StepsFragment : Fragment() {

    private lateinit var viewModel: StepsViewModel
    private lateinit var progressBar: ProgressBar
    private lateinit var tvStepCount: TextView
    private lateinit var tvGoal: TextView
    private lateinit var tvCalories: TextView
    private lateinit var tvDistance: TextView
    private lateinit var barChart: BarChart
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private val stepUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == StepCounterService.ACTION_STEPS_UPDATED) {
                // Force refresh when we receive a broadcast
                refreshStepData()
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_ACTIVITY_RECOGNITION = 100
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_steps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[StepsViewModel::class.java]

        // Initialize views
        progressBar = view.findViewById(R.id.progressSteps)
        tvStepCount = view.findViewById(R.id.tvStepCount)
        tvGoal = view.findViewById(R.id.tvGoal)
        tvCalories = view.findViewById(R.id.tvCalories)
        tvDistance = view.findViewById(R.id.tvDistance)
        barChart = view.findViewById(R.id.barChartWeekly)
        loadingIndicator = view.findViewById(R.id.loadingIndicator)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh)

        // Setup SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            refreshStepData()
        }

        // Setup chart
        setupBarChart()

        // Set up goal button
        view.findViewById<Button>(R.id.btnSetGoal).setOnClickListener {
            showGoalDialog()
        }

        // Observe today's step data
        viewModel.todaySteps.observe(viewLifecycleOwner) { stepData ->
            tvStepCount.text = stepData.steps.toString()
            tvGoal.text = "Goal: ${stepData.goal} steps"
            tvCalories.text = "${stepData.calculateCalories()} cal"
            tvDistance.text = String.format("%.2f km", stepData.calculateDistance())

            // Update progress bar
            progressBar.max = stepData.goal
            progressBar.progress = stepData.steps
        }

        // Observe weekly step data for chart
        viewModel.weeklySteps.observe(viewLifecycleOwner) { weeklySteps ->
            updateBarChart(weeklySteps)
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
            // Also update SwipeRefreshLayout
            if (!isLoading) {
                swipeRefreshLayout.isRefreshing = false
            }
        }

        // Check permission and start service
        checkPermissionAndStartService()
    }

    override fun onStart() {
        super.onStart()
        // Register broadcast receiver
        val filter = IntentFilter(StepCounterService.ACTION_STEPS_UPDATED)
        ContextCompat.registerReceiver(
            requireActivity(),
            stepUpdateReceiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    override fun onResume() {
        super.onResume()

        // Force refresh data when fragment becomes visible
        refreshStepData()
    }

    override fun onStop() {
        super.onStop()
        // Unregister broadcast receiver
        try {
            requireActivity().unregisterReceiver(stepUpdateReceiver)
        } catch (e: Exception) {
            // Receiver might not be registered
        }
    }

    private fun refreshStepData() {
        // Only proceed if user is authenticated
        if (FirebaseAuth.getInstance().currentUser != null) {
            // Force reload steps data from Firebase
            viewModel.loadTodaySteps()
            viewModel.loadWeeklySteps()
        }
    }

    private fun checkPermissionAndStartService() {
        // For Android 10 and above we need ACTIVITY_RECOGNITION permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                    PERMISSION_REQUEST_ACTIVITY_RECOGNITION
                )
            } else {
                startStepCounterService()
            }
        } else {
            startStepCounterService()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_ACTIVITY_RECOGNITION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startStepCounterService()
            }
        }
    }

    private fun startStepCounterService() {
        val serviceIntent = Intent(requireContext(), StepCounterService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().startForegroundService(serviceIntent)
        } else {
            requireContext().startService(serviceIntent)
        }
    }

    private fun setupBarChart() {
        barChart.description.isEnabled = false
        barChart.setDrawGridBackground(false)
        barChart.setDrawBarShadow(false)
        barChart.setDrawValueAboveBar(true)
        barChart.setPinchZoom(false)
        barChart.setScaleEnabled(false)

        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f

        barChart.axisLeft.setDrawGridLines(false)
        barChart.axisRight.isEnabled = false
        barChart.legend.isEnabled = false
    }

    private fun updateBarChart(stepDataList: List<StepData>) {
        if (stepDataList.isEmpty()) return

        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()
        val dateFormat = SimpleDateFormat("EEE", Locale.getDefault())

        // Sort by date
        val sortedList = stepDataList.sortedBy { it.date }

        // Create entries for chart
        sortedList.forEachIndexed { index, data ->
            entries.add(BarEntry(index.toFloat(), data.steps.toFloat()))
            labels.add(dateFormat.format(Date(data.date)))
        }

        val dataSet = BarDataSet(entries, "Steps")
        dataSet.color = ContextCompat.getColor(requireContext(), R.color.colorPrimary)

        val barData = BarData(dataSet)
        barData.setValueTextSize(12f)

        barChart.data = barData
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        barChart.animateY(1000)
        barChart.invalidate()
    }

    private fun showGoalDialog() {
        val editText = EditText(context)
        editText.inputType = InputType.TYPE_CLASS_NUMBER
        editText.setText(viewModel.todaySteps.value?.goal?.toString() ?: "10000")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Set Daily Step Goal")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val goal = editText.text.toString().toIntOrNull() ?: 10000
                viewModel.updateGoal(goal)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}