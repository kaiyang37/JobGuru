package com.example.jobguru.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class EmpInterviewViewModelFactory(private val delimitedJobIds: String, private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EmpInterviewViewModel::class.java)) {
            return EmpInterviewViewModel(delimitedJobIds, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}