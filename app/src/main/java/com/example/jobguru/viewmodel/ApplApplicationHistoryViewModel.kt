package com.example.jobguru.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jobguru.model.ApplyModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ApplApplicationHistoryViewModel : ViewModel() {
    private val _jobTitle = MutableLiveData<String>()
    val jobTitle: LiveData<String>
        get() = _jobTitle

    private val _jobCompanyName = MutableLiveData<String>()
    val jobCompanyName: LiveData<String>
        get() = _jobCompanyName

    private val _jobWorkState = MutableLiveData<String>()
    val jobWorkState: LiveData<String>
        get() = _jobWorkState

    private val _appStatus = MutableLiveData<String>()
    val appStatus: LiveData<String>
        get() = _appStatus

    private val _jobList = MutableLiveData<List<ApplyModel>>()
    val jobList: LiveData<List<ApplyModel>>
        get() = _jobList

    private val _searchApplyList = MutableLiveData<List<ApplyModel>>()
    val searchApplyList: LiveData<List<ApplyModel>>
        get() = _searchApplyList

    init {
        getJobsData()
    }

    private fun getJobsData() {
        val dbRef = FirebaseDatabase.getInstance().getReference("Jobs")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val jobList = mutableListOf<ApplyModel>()
                if (snapshot.exists()) {
                    for (jobSnap in snapshot.children) {
                        val jobData = jobSnap.getValue(ApplyModel::class.java)
                        jobData?.let {
                            jobList.add(it)
                        }
                    }
                }
                _jobList.postValue(jobList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error if needed
            }
        })
    }

    fun getApplyData(applId: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Apply")

        val query = dbRef.orderByChild("applId").equalTo(applId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val applyList = mutableListOf<ApplyModel>()
                if (snapshot.exists()) {
                    for (applySnap in snapshot.children) {
                        val applyData = applySnap.getValue(ApplyModel::class.java)
                        applyData?.let {
                            applyList.add(it)
                        }
//                        _jobTitle.value = applyData?.jobTitle
//                        _jobCompanyName.value = applyData?.empName
//                        _jobWorkState.value = applyData?.jobWorkState
//                        _appStatus.value = applyData?.appStatus


//                            val jobId = applyData.jobId
//                            val dbRef = FirebaseDatabase.getInstance().getReference("Jobs")
//                            val query = dbRef.orderByChild("jobId").equalTo(jobId)
//                            query.addValueEventListener(object : ValueEventListener {
//                                override fun onDataChange(snapshot: DataSnapshot) {
//                                    val jobList = mutableListOf<JobModel>()
//                                    if (snapshot.exists()) {
//                                        for (jobSnap in snapshot.children) {
//                                            val jobData = jobSnap.getValue(JobModel::class.java)
//                                            jobData?.let {
//                                                jobList.add(it)
//                                            }
//                                        }
//                                    }
//                                    _searchJobList.postValue(jobList)
//                                }
//                                override fun onCancelled(error: DatabaseError) {
//                                    // Handle the error if needed
//                                }
//                            })

                    }
                }
                _searchApplyList.postValue(applyList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error if needed
            }
        })
    }
}