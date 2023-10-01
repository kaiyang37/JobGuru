package com.example.jobguru.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jobguru.model.ApplyModel
import com.example.jobguru.model.JobModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ApplApplicationHistoryViewModel : ViewModel() {
    private val _jobList = MutableLiveData<List<JobModel>>()
    val jobList: LiveData<List<JobModel>>
        get() = _jobList

    private val _jobTitle = MutableLiveData<String>()
    val jobTitle: LiveData<String>
        get() = _jobTitle

    private val _searchJobList = MutableLiveData<List<JobModel>>()
    val searchJobList: LiveData<List<JobModel>>
        get() = _searchJobList

    init {
        getJobsData()
    }

    private fun getJobsData() {
        val dbRef = FirebaseDatabase.getInstance().getReference("Jobs")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val jobList = mutableListOf<JobModel>()
                if (snapshot.exists()) {
                    for (jobSnap in snapshot.children) {
                        val jobData = jobSnap.getValue(JobModel::class.java)
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
                if (snapshot.exists()) {
                    for (applySnap in snapshot.children) {
                        val applyData = applySnap.getValue(ApplyModel::class.java)
                        if (applyData != null) {
                            val jobId = applyData.jobId
                            val dbRef = FirebaseDatabase.getInstance().getReference("Jobs")
                            val query = dbRef.orderByChild("jobId").equalTo(jobId)
                            query.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val jobList = mutableListOf<JobModel>()
                                    if (snapshot.exists()) {
                                        for (jobSnap in snapshot.children) {
                                            val jobData = jobSnap.getValue(JobModel::class.java)
                                            jobData?.let {
                                                jobList.add(it)
                                            }
                                        }
                                    }
                                    _searchJobList.postValue(jobList)
                                }
                                override fun onCancelled(error: DatabaseError) {
                                    // Handle the error if needed
                                }
                            })
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error if needed
            }
        })
    }
}