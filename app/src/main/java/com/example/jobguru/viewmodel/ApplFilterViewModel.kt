package com.example.jobguru.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jobguru.model.EmployerModel
import com.example.jobguru.model.JobModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Job

class ApplFilterViewModel : ViewModel() {
    private val _jobList = MutableLiveData<List<JobModel>>()
    val jobList: LiveData<List<JobModel>>
        get() = _jobList

    private val _filteredJobList = MutableLiveData<List<JobModel>>()
    val filteredJobList: LiveData<List<JobModel>>
        get() = _filteredJobList

    init {
        getJobsData()
    }

    private var empId: String? = ""

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

    fun filterJobsBasedOnPreferences(locationQuery: String?, specQuery: String?, salaryQuery: String?) {

        val filteredList = _jobList.value?.filter{ job ->

            val locationValues = locationQuery?.split(",")?.map { it.trim() }
            val specValues = specQuery?.split(",")?.map { it.trim() }
            val numSalaryQuery = salaryQuery?.replace(Regex("[^0-9.]"), "")?.trim()

            val locationMatched = locationValues?.any { job.jobWorkState?.contains(it, ignoreCase = true) == true } ?: false
            val specMatched = specValues?.any { job.jobSpecialization?.contains(it, ignoreCase = true) == true } ?: false

            (locationMatched && specMatched) || numSalaryQuery?.let { job.jobMinSalary!! >= it.toDouble() } == true

        } ?: emptyList()

        val sortedJobList = filteredList.sortedBy { job ->
            job.jobWorkState
        }

        _filteredJobList.postValue(sortedJobList)
    }

}