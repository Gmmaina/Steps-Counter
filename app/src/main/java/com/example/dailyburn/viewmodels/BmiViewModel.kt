package com.example.dailyburn.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyburn.models.BmiRecord
import com.example.dailyburn.repositories.BmiRepository
import com.example.dailyburn.utils.BmiCalculator
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.Date

class BmiViewModel : ViewModel() {
    private val repository = BmiRepository()
    private val userId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val _currentBmi = MutableLiveData<
            BmiRecord>()
    val currentBmi: LiveData<BmiRecord> = _currentBmi

    private val _bmiHistory = MutableLiveData<List<BmiRecord>>()
    val bmiHistory: LiveData<List<BmiRecord>> = _bmiHistory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _saveSuccess = MutableLiveData<Boolean>()
    val saveSuccess: LiveData<Boolean> = _saveSuccess

    init {
        if (userId.isNotEmpty()) {
            loadLatestBmi()
            loadBmiHistory()
        }
    }

    fun calculateBmi(weight: Float, height: Float) {
        if (userId.isEmpty() || weight <= 0 || height <= 0) return

        val bmi = BmiCalculator.calculateBmi(weight, height)
        val category = BmiCalculator.getBmiCategory(bmi)

        val bmiRecord = BmiRecord(
            userId = userId,
            timestamp = Date().time,
            height = height,
            weight = weight,
            bmi = bmi,
            category = category
        )

        _currentBmi.value = bmiRecord
    }

    fun saveBmiRecord() {
        val current = _currentBmi.value ?: return

        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.saveBmiRecord(current)
            _saveSuccess.value = result
            _isLoading.value = false

            if (result) {
                // Refresh history after saving
                loadBmiHistory()
            }
        }
    }

    fun loadLatestBmi() {
        if (userId.isEmpty()) return

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val latest = repository.getLatestBmi(userId)
                if (latest != null) {
                    _currentBmi.value = latest
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadBmiHistory() {
        if (userId.isEmpty()) return

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val history = repository.getBmiHistory(userId)
                _bmiHistory.value = history
            } catch (e: Exception) {
                _bmiHistory.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}