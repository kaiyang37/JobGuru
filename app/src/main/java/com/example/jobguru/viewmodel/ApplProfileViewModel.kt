package com.example.jobguru.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jobguru.model.ApplicantModel
import com.example.jobguru.model.ApplyModel
import com.example.jobguru.model.JobModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ApplProfileViewModel : ViewModel(){

    private val _applProfileStatus = MutableLiveData<Boolean>()
    val applProfileStatus: LiveData<Boolean>
        get() = _applProfileStatus

    private val _jobTitle = MutableLiveData<String>()
    val jobTitle: LiveData<String>
        get() = _jobTitle

    private val _applName = MutableLiveData<String>()
    val applName: LiveData<String>
        get() = _applName

    private val _applGender = MutableLiveData<String>()
    val applGender: LiveData<String>
        get() = _applGender

    private val _applEmail = MutableLiveData<String>()
    val applEmail: LiveData<String>
        get() = _applEmail

    private val _applPhoneNum = MutableLiveData<String>()
    val applPhoneNum: LiveData<String>
        get() = _applPhoneNum

    fun getJobTitle(jobId: String){
        val dbRef = FirebaseDatabase.getInstance().getReference("Jobs")
        val query = dbRef.orderByChild("jobId").equalTo(jobId)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val jobData = snapshot.children.first().getValue(JobModel::class.java)
                    _jobTitle.value = jobData?.jobTitle
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle the error if needed
            }
        })
    }

    fun checkApplProfileStatus(applId: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Applicants")

        val query = dbRef.orderByChild("applId").equalTo(applId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (applSnap in snapshot.children) {
                        val applData = applSnap.getValue(ApplicantModel::class.java)
                        if (applData != null && applData.applLiveIn.isNullOrEmpty()) {
                            _applProfileStatus.postValue(true)
                        }else {
                            _applProfileStatus.postValue(false) // Profile status is not empty
                        }
                    }
                } else {
                    // No data found for the specified applId
                    // Handle this case as needed
                    _applProfileStatus.postValue(false)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error if needed
                _applProfileStatus.postValue(false)
            }
        })
    }

    fun getApplicantData(applId: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Applicants")

        val query = dbRef.orderByChild("applId").equalTo(applId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val applData = snapshot.children.first().getValue(ApplicantModel::class.java)
                    _applGender.value = applData?.applGender
                    _applName.value = applData?.applFirstName + " " + applData?.applLastName
                    _applEmail.value = applData?.applEmail
                    _applPhoneNum.value = "+${applData?.applAreaCode?.replace(Regex("[^\\d]"), "")}"
                    _applPhoneNum.value = _applPhoneNum.value + " " + applData?.applPhoneNumber
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error if needed
            }
        })
    }
}