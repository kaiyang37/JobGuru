package com.example.jobguru.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jobguru.model.InterviewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ApplInterviewViewModel: ViewModel() {
    private val _intId = MutableLiveData<String>()
    val intId: LiveData<String>
        get() = _intId

    private val _companyName = MutableLiveData<String>()
    val companyName: LiveData<String>
        get() = _companyName

    private val _jobTitle = MutableLiveData<String>()
    val jobTitle: LiveData<String>
        get() = _jobTitle

    private val _intDate = MutableLiveData<String>()
    val intDate: LiveData<String>
        get() = _intDate

    private val _intTime = MutableLiveData<String>()
    val intTime: LiveData<String>
        get() = _intTime

    private val _intPlatform = MutableLiveData<String>()
    val intPlatform: LiveData<String>
        get() = _intPlatform

    private val _interviewer = MutableLiveData<String>()
    val interviewer: LiveData<String>
        get() = _interviewer

    private val _intStatus = MutableLiveData<String>()
    val intStatus: LiveData<String>
        get() = _intStatus

    // LiveData for error messages
    private val _reasonError = MutableLiveData<String>()
    val reasonError: LiveData<String> = _reasonError

    fun getInterviewData(applId: String, jobId: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Interviews")

        val query = dbRef.orderByChild("jobId").equalTo(jobId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (intSnap in snapshot.children) {
                        val intData = intSnap.getValue(InterviewModel::class.java)
                        if (intData != null && intData.applId.equals(applId)) {
                            _intId.value = intData?.intvwId
                            _companyName.value = intData?.empName
                            _jobTitle.value = intData?.jobTitle
                            _intDate.value = intData?.intvwDate
                            _intTime.value = intData?.intvwTime
                            _intPlatform.value = intData?.intvwPlatform
                            _interviewer.value = intData?.intvwrName
                            _intStatus.value = intData?.intvwStatus
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error if needed
            }
        })
    }

    fun updateInterviewStatus(intId: String, onSuccess: () -> Unit,
                              onError: (String) -> Unit){
        val dbRef = FirebaseDatabase.getInstance().getReference("Interviews").child(intId)
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val updatedFields = mutableMapOf<String, Any?>()

                    updatedFields["intvwStatus"] = "Accepted"

                    dbRef.updateChildren(updatedFields)
                    onSuccess()
                } else {
                    onError("Update Interview Status Error")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.message)
            }
        })

    }

    fun validateDate(reason: String): Boolean{
        // Clear previous error messages
        _reasonError.value = null

        var isValid = true

        if (reason.isBlank()) {
            _reasonError.value = "Reject Reason is required"
            isValid = false
        }
        return isValid
    }

    fun updateRejectInterviewStatus(intId: String, intReason: String, onSuccess: () -> Unit,
                                    onError: (String) -> Unit){
        val dbRef = FirebaseDatabase.getInstance().getReference("Interviews").child(intId)
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val updatedFields = mutableMapOf<String, Any?>()

                    updatedFields["intvwStatus"] = "Rejected"
                    updatedFields["intvwReason"] = intReason

                    dbRef.updateChildren(updatedFields)
                    onSuccess()
                } else {
                    onError("Update Reject Interview Invitation Error")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.message)
            }
        })

    }
}