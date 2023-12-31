package com.example.jobguru.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.jobguru.EmpJobsViewModel

class EmpJobsViewModelFactory(private val empEmail: String, private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EmpJobsViewModel::class.java)) {
            return EmpJobsViewModel(empEmail, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

