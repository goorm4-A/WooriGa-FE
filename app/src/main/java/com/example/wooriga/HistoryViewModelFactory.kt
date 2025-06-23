package com.example.wooriga

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HistoryViewModelFactory(
    private val repository: HistoryRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            return HistoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class ViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val api = RetrofitClient.apiService
        val repo = HistoryRepository(api)
        return HistoryViewModel(repo) as T
    }
}
