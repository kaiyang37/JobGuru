package com.example.jobguru.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EmpInterviewDetailsViewModel : ViewModel() {

    private val _intvwId = MutableLiveData<String>()
    val intvwId: LiveData<String>
        get() = _intvwId

    private val _applName = MutableLiveData<String>()
    val applName: LiveData<String>
        get() = _applName

    private val _jobTitle = MutableLiveData<String>()
    val jobTitle: LiveData<String>
        get() = _jobTitle

    private val _intvwrName = MutableLiveData<String>()
    val intvwrName: LiveData<String>
        get() = _intvwrName

    private val _intvwDate = MutableLiveData<String>()
    val intvwDate: LiveData<String>
        get() = _intvwDate

    private val _intvwTime = MutableLiveData<String>()
    val intvwTime: LiveData<String>
        get() = _intvwTime

    private val _intvwPlatform = MutableLiveData<String>()
    val intvwPlatform: LiveData<String>
        get() = _intvwPlatform

    private val _intvwReason = MutableLiveData<String>()
    val intvwReason: LiveData<String>
        get() = _intvwReason

    private val _intvwStatus = MutableLiveData<String>()
    val intvwStatus: LiveData<String>
        get() = _intvwStatus

    fun initializeWithData(
        intvwId: String,
        applName: String,
        jobTitle: String,
        intvwrName: String,
        intvwDate: String,
        intvwTime: String,
        intvwPlatform: String,
        intvwReason: String,
        intvwStatus: String,
    ) {
        _intvwId.value = intvwId
        _applName.value = applName
        _jobTitle.value = jobTitle
        _intvwrName.value = intvwrName
        _intvwDate.value = intvwDate
        _intvwTime.value = intvwTime
        _intvwPlatform.value = intvwPlatform
        _intvwReason.value = intvwReason
        _intvwStatus.value = intvwStatus
    }
}