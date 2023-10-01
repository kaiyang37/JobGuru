package com.example.jobguru.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.jobguru.EmpJobsViewModel

class EmpJobsViewModelFactory(private val empEmail: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EmpJobsViewModel::class.java)) {
            return EmpJobsViewModel(empEmail) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

