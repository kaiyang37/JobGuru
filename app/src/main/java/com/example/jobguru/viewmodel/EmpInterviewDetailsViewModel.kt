package com.example.jobguru.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jobguru.model.InterviewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EmpInterviewDetailsViewModel : ViewModel() {
    private val _jobTitle = MutableLiveData<String>()
    val jobTitle: LiveData<String> = _jobTitle

    private val _applName = MutableLiveData<String>()
    val applName: LiveData<String> = _applName

    private val _intvwrName = MutableLiveData<String>()
    val intvwrName: LiveData<String> = _intvwrName

    private val _intvwDate = MutableLiveData<String>()
    val intvwDate: LiveData<String> = _intvwDate

    private val _intvwTime = MutableLiveData<String>()
    val intvwTime: LiveData<String> = _intvwTime

    private val _intvwPlatform = MutableLiveData<String>()
    val intvwPlatform: LiveData<String> = _intvwPlatform

    private val _intvwStatus = MutableLiveData<String>()
    val intvwStatus: LiveData<String> = _intvwStatus

    private val _intvwReason = MutableLiveData<String>()
    val intvwReason: LiveData<String> = _intvwReason

    private val _intvwIsResend = MutableLiveData<Boolean>()
    val intvwIsResend: LiveData<Boolean> = _intvwIsResend

    fun getInterviewData(intvwId: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Interviews")

        val query = dbRef.orderByChild("intvwId").equalTo(intvwId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val interviewData =
                        snapshot.children.first().getValue(InterviewModel::class.java)
                    _jobTitle.value = interviewData?.jobTitle
                    _applName.value = interviewData?.applName
                    _intvwrName.value = interviewData?.intvwrName
                    _intvwDate.value = interviewData?.intvwDate
                    _intvwTime.value = interviewData?.intvwTime
                    _intvwPlatform.value = interviewData?.intvwPlatform
                    _intvwStatus.value = interviewData?.intvwStatus
                    _intvwReason.value = interviewData?.intvwReason
                    _intvwIsResend.value = interviewData?.resend
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error if needed
                Log.e("Firebase", "Error: ${error.message}")
            }
        })
    }

}