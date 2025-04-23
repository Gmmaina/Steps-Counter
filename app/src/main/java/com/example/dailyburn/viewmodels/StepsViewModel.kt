package com.example.dailyburn.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyburn.models.StepData
import com.example.dailyburn.repositories.StepRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

class StepsViewModel : ViewModel() {
    private val repository = StepRepository()
    private val userId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val _todaySteps = MutableLiveData<StepData>()
    val todaySteps: LiveData<StepData> = _todaySteps

    private val _weeklySteps = MutableLiveData<List<StepData>>()
    val weeklySteps: LiveData<List<StepData>> = _weeklySteps

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        if (userId.isNotEmpty()) {
            loadTodaySteps()
            loadWeeklySteps()

            // Refresh data periodically
            startPeriodicRefresh()
        }
    }

    fun loadTodaySteps() {
        if (userId.isEmpty()) return

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val stepData = repository.getTodaySteps(userId)
                _todaySteps.value = stepData ?: createEmptyStepData()
            } catch (e: Exception) {
                _todaySteps.value = createEmptyStepData()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadWeeklySteps() {
        if (userId.isEmpty()) return

        viewModelScope.launch {
            try {
                val stepsList = repository.getWeeklySteps(userId)
                _weeklySteps.value = stepsList
            } catch (e: Exception) {
                _weeklySteps.value = emptyList()
            }
        }
    }

    fun updateGoal(goal: Int) {
        if (userId.isEmpty() || goal <= 0) return

        viewModelScope.launch {
            try {
                repository.updateGoal(userId, goal)
                loadTodaySteps() // Refresh data
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun startPeriodicRefresh() {
        viewModelScope.launch {
            while (true) {
                delay(60000) // Refresh every minute
                loadTodaySteps()
            }
        }
    }

    private fun createEmptyStepData(): StepData {
        return StepData(
            userId = userId,
            date = Calendar.getInstance().timeInMillis
        )
    }
}

