package com.example.jobguru.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ApplJobsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ApplFilterViewModel::class.java)) {
            return ApplFilterViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}