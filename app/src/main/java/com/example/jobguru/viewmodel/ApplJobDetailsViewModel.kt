package com.example.jobguru.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jobguru.model.ApplicantModel
import com.example.jobguru.model.ApplyModel
import com.example.jobguru.model.EmployerModel
import com.example.jobguru.model.JobModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ApplJobDetailsViewModel : ViewModel() {

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

    private val _jobCompanyName = MutableLiveData<String>()
    val jobCompanyName: LiveData<String>
        get() = _jobCompanyName

    private val _applProfileStatus = MutableLiveData<Boolean>()
    val applProfileStatus: LiveData<Boolean>
        get() = _applProfileStatus

    private val _applyJobStatus = MutableLiveData<Boolean>()
    val applyJobStatus: LiveData<Boolean>
        get() = _applyJobStatus

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
                    getEmpData(jobData?.empId)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error if needed
            }
        })
    }

    private fun getEmpData(empId: String?){
        val dbRef = FirebaseDatabase.getInstance().getReference("Employers")
        val query = dbRef.orderByChild("empId").equalTo(empId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val empData = snapshot.children.first().getValue(EmployerModel::class.java)

                    _jobCompanyName.value = empData?.empName
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


    fun checkApplyJobStatus(applId: String, jobId: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Apply")

        val query = dbRef.orderByChild("jobId").equalTo(jobId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (applySnap in snapshot.children) {
                        val applyData = applySnap.getValue(ApplyModel::class.java)
                        if (applyData != null && applyData.applId.equals(applId)) {
                            _applyJobStatus.postValue(true)
                        }else {
                            _applyJobStatus.postValue(false) // Profile status is not empty
                        }
                    }
                } else {
                    // No data found for the specified applId
                    // Handle this case as needed
                    _applyJobStatus.postValue(false)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error if needed
                _applyJobStatus.postValue(false)
            }
        })
    }


    fun saveJob(
        id: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Jobs").child(id)
        val task = dbRef.removeValue()

        task.addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener { error ->
            onError("Deleting Err ${error.message}")
        }

        fun deleteRecord(
            id: String,
            onSuccess: () -> Unit,
            onError: (String) -> Unit
        ) {
            val dbRef = FirebaseDatabase.getInstance().getReference("Jobs").child(id)
            val task = dbRef.removeValue()

            task.addOnSuccessListener {
                onSuccess()
            }.addOnFailureListener { error ->
                onError("Deleting Err ${error.message}")
            }
        }

    }
}