package com.example.jobguru.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jobguru.model.JobModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ApplSearchViewModel : ViewModel() {
    private val _jobList = MutableLiveData<List<JobModel>>()
    val jobList: LiveData<List<JobModel>>
        get() = _jobList

    private val _searchedJobList = MutableLiveData<List<JobModel>>()
    val searchedJobList: LiveData<List<JobModel>>
        get() = _searchedJobList

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

    fun searchJobs(query: String) {
        val searchedList = _jobList.value?.filter { job ->
            (job.jobTitle?.contains(query, ignoreCase = true) == true) ||
                    (job.jobRole?.contains(query, ignoreCase = true) == true) ||
                    (job.jobWorkState?.contains(query, ignoreCase = true) == true) ||
                    (job.jobMinSalary.toString()?.contains(query, ignoreCase = true) == true) ||
                    (job.jobMaxSalary.toString()?.contains(query, ignoreCase = true) == true)
        } ?: emptyList()
        _searchedJobList.postValue(searchedList)
    }


    fun filterJobsBasedOnSpec(selectedSpec: String) {
        val filteredList = _jobList.value?.filter { job ->
            (job.jobSpecialization?.equals(selectedSpec, ignoreCase = true) == true)
        } ?: emptyList()
        _searchedJobList.postValue(filteredList)
    }

}