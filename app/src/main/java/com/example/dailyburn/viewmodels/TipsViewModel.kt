package com.example.dailyburn.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dailyburn.models.FitnessTip
import com.example.dailyburn.repositories.TipsRepository

class TipsViewModel : ViewModel() {
    private val repository = TipsRepository()

    private val _tips = MutableLiveData<List<FitnessTip>>()
    val tips: LiveData<List<FitnessTip>> = _tips

    private val _categories = MutableLiveData<List<String>>()
    val categories: LiveData<List<String>> = _categories

    private val _selectedCategory = MutableLiveData<String>()
    val selectedCategory: LiveData<String> = _selectedCategory

    init {
        _categories.value = repository.categories
        setCategory("All")
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
        _tips.value = if (category == "All") {
            repository.getFitnessTips()
        } else {
            repository.getFitnessTips(category)
        }
    }
}