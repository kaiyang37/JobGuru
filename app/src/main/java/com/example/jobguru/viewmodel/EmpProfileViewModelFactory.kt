package com.example.jobguru.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class EmpProfileViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EmpProfileViewModel::class.java)) {
            return EmpProfileViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}