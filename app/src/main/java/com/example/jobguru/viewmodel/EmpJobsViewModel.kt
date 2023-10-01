package com.example.jobguru

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.jobguru.model.JobModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class EmpJobsViewModel (private val empEmail: String) : ViewModel() {
    private val _jobList = MutableLiveData<List<JobModel>>()
    val jobList: LiveData<List<JobModel>>
        get() = _jobList

    private val _searchedJobList = MutableLiveData<List<JobModel>>()
    val searchedJobList: LiveData<List<JobModel>>
        get() = _searchedJobList

    init {
        getJobsData(empEmail)
    }

    private fun getJobsData(empEmail:String) {
        val employersRef = FirebaseDatabase.getInstance().getReference("Employers")
        val dbRef = FirebaseDatabase.getInstance().getReference("Jobs")

        val employerQuery = employersRef.orderByChild("personInChargeEmail").equalTo(empEmail)

        employerQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(employerDataSnapshot: DataSnapshot) {
                if (employerDataSnapshot.exists()) {
                    // Step 2: Retrieve the empId
                    val employerSnapshot = employerDataSnapshot.children.first()
                    val empId = employerSnapshot.key

                    // Step 3: Fetch jobs associated with the empId
                    val jobQuery = dbRef.orderByChild("empId").equalTo(empId)

                    jobQuery.addValueEventListener(object : ValueEventListener {
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
                } else {
                    // Handle the case where no matching employer is found for empEmail
                }
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
                    (job.jobWorkState?.contains(query, ignoreCase = true) == true)||
                    (job.jobMinSalary.toString()?.contains(query, ignoreCase = true) == true)||
                    (job.jobMaxSalary.toString()?.contains(query, ignoreCase = true) == true)

            // Add more fields to search if needed
        } ?: emptyList()
        _searchedJobList.postValue(searchedList)
    }
}