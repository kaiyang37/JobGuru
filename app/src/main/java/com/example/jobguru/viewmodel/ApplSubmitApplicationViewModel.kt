package com.example.jobguru.viewmodel

import android.util.Log
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

class ApplSubmitApplicationViewModel : ViewModel() {
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

    private val _jobTitle = MutableLiveData<String>()
    val jobTitle: LiveData<String>
        get() = _jobTitle

    private val dbRef = FirebaseDatabase.getInstance().getReference("Apply")

    fun submitApplication(
        applId: String,
        jobId: String,
        jobTitle: String,
        empName: String,
        jobCompanyEmail: String,
        jobWorkState: String,
        applName: String,
        applEducationLevel: String,
        applMinimumMonthlySalary: Double,
        applLiveIn: String,
        appStatus: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        generateNextApplyId { nextApplyId ->
            val applyJob = ApplyModel(
                nextApplyId,
                applId,
                jobId,
                jobTitle,
                empName,
                jobCompanyEmail,
                jobWorkState,
                applName,
                applEducationLevel,
                applMinimumMonthlySalary,
                applLiveIn,
                appStatus
            )

            dbRef.child(nextApplyId).setValue(applyJob)
                .addOnCompleteListener {
                    onSuccess()
                }.addOnFailureListener { err ->
                    onError("Error inserting the data")
                }
        }
    }

    // Function to generate the next apply ID
    private fun generateNextApplyId(callback: (String) -> Unit) {
        // Query the database to find the current maximum apply ID
        val query = dbRef.orderByKey().limitToLast(1)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (childSnapshot in dataSnapshot.children) {
                        val lastApplyId = childSnapshot.key
                        if (lastApplyId != null) {
                            Log.d("ApplSubmitApplication", "Last Apply ID: $lastApplyId")
                            // Extract the numeric part of the ID and increment it
                            val numericPart = lastApplyId.substring(2).toInt()
                            val nextNumericPart = numericPart + 1
                            val nextApplyId = "AP$nextNumericPart"

                            callback(nextApplyId)
                        }
                    }
                } else {
                    callback("AP1")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback("AP1")
            }
        })
    }
}