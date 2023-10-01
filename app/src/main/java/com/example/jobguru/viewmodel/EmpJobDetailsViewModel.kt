package com.example.jobguru.viewmodel

import android.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jobguru.model.JobModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EmpJobDetailsViewModel : ViewModel() {

    private val _jobTitle = MutableLiveData<String>()
    val jobTitle: LiveData<String>
        get() = _jobTitle

    private val _jobRole = MutableLiveData<String>()
    val jobRole: LiveData<String>
        get() = _jobRole

    private val _jobSpecialization = MutableLiveData<String>()
    val jobSpecialization: LiveData<String>
        get() = _jobSpecialization

    private val _jobYearOfExp = MutableLiveData<String>()
    val jobYearOfExp: LiveData<String>
        get() = _jobYearOfExp

    private val _jobDesc = MutableLiveData<String>()
    val jobDesc: LiveData<String>
        get() = _jobDesc

    private val _jobWorkState = MutableLiveData<String>()
    val jobWorkState: LiveData<String>
        get() = _jobWorkState

    private val _jobMinSalary = MutableLiveData<Double>()
    val jobMinSalary: LiveData<Double>
        get() = _jobMinSalary

    private val _jobMaxSalary = MutableLiveData<Double>()
    val jobMaxSalary: LiveData<Double>
        get() = _jobMaxSalary

    fun getJobData(jobId: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Jobs")

        val query = dbRef.orderByChild("jobId").equalTo(jobId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val jobData = snapshot.children.first().getValue(JobModel::class.java)
                    _jobTitle.value = jobData?.jobTitle
                    _jobRole.value = jobData?.jobRole
                    _jobSpecialization.value = jobData?.jobSpecialization
                    _jobYearOfExp.value = jobData?.jobYearOfExp
                    _jobDesc.value = jobData?.jobDesc
                    _jobWorkState.value = jobData?.jobWorkState
                    _jobMinSalary.value = jobData?.jobMinSalary
                    _jobMaxSalary.value = jobData?.jobMaxSalary
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error if needed
            }
        })
    }

    fun removeRecord(
        id: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Jobs").child(id)
        val task = dbRef.removeValue()

        task.addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener { error ->
            onError("Removing Err ${error.message}")
        }
    }

}