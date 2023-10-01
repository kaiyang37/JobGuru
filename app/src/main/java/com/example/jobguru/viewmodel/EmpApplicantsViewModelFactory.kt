package com.example.jobguru.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class EmpApplicantsViewModelFactory (private val delimitedJobIds: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EmpApplicantsViewModel::class.java)) {
            return EmpApplicantsViewModel(delimitedJobIds) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}