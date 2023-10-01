package com.example.jobguru.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ApplCompleteCurrentStatusViewModel : ViewModel() {

    // LiveData for error messages
    private val _phoneNumError = MutableLiveData<String>()
    val phoneNumError: LiveData<String> = _phoneNumError

    fun validatePhoneNum(phoneNum : String):Boolean{
        _phoneNumError.value = null

        var isValid = true

        val phonePattern = "^[+]?[0-9]{9,13}$"
        val regex = Regex(phonePattern)

        if (!regex.matches(phoneNum)) {
            _phoneNumError.value = "Invalid phone number"
            isValid = false
        }
        return isValid
    }
}