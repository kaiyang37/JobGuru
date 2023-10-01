package com.example.jobguru.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jobguru.model.ApplicantModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EmpApplicantsViewModel(private val delimitedJobIds: String) : ViewModel() {
    private val _applList = MutableLiveData<List<ApplicantModel>>()
    val applList: LiveData<List<ApplicantModel>>
        get() = _applList

    private val _searchedApplList = MutableLiveData<List<ApplicantModel>>()
    val searchedApplList: LiveData<List<ApplicantModel>>
        get() = _searchedApplList

    init {
        getApplicantsData(delimitedJobIds)
    }

    private fun getApplicantsData(delimitedJobIds: String) {
        val applyRef = FirebaseDatabase.getInstance().getReference("Apply")
        val jobIdList = delimitedJobIds.split(",")

        val applicantsList = mutableListOf<String>() // List to store applicant IDs

        // Function to retrieve applicants for a specific job
        fun getApplicantsForJob(jobId: String, callback: (List<String>) -> Unit) {
            val applyQuery = applyRef.orderByChild("jobId").equalTo(jobId)
            applyQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val applicantsForJob = mutableListOf<String>()

                    for (applySnapshot in dataSnapshot.children) {
                        val applyStatus =
                            applySnapshot.child("appStatus").getValue(String::class.java)
                        val applicantId = applySnapshot.child("applId").getValue(String::class.java)

                        if (applyStatus != null && applyStatus == "Pending" && applicantId != null) {
                            applicantsForJob.add(applicantId)
                        }
                    }

                    callback(applicantsForJob)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle database error
                }
            })
        }

        // Retrieve applicants for each job in jobIdList
        val totalJobs = jobIdList.size
        var jobsProcessed = 0

        for (jobId in jobIdList) {
            getApplicantsForJob(jobId) { applicantsForJob ->
                applicantsList.addAll(applicantsForJob)
                jobsProcessed++

                // Check if all jobs have been processed
                if (jobsProcessed == totalJobs) {
                    retrieveApplicantData(applicantsList)
                }
            }
        }
    }

    private fun retrieveApplicantData(applicantsList: List<String>) {
        val applicantsRef = FirebaseDatabase.getInstance().getReference("Applicants")
        val applList = mutableListOf<ApplicantModel>()

        for (applicantId in applicantsList) {
            val applicantQuery = applicantsRef.orderByChild("applId").equalTo(applicantId)
            applicantQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (applSnap in dataSnapshot.children) {
                            val applData = applSnap.getValue(ApplicantModel::class.java)
                            applData?.let {
                                applList.add(it)
                            }
                        }
                    }

                    if (applList.size == applicantsList.size) {
                        _applList.postValue(applList)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle database error
                }
            })
        }
    }


    fun searchApplicants(query: String) {
        val searchedList = _applList.value?.filter { appl ->
            (appl.applFirstName?.contains(
                query,
                ignoreCase = true
            ) == true) || (appl.applLastName?.contains(query, ignoreCase = true) == true) ||
                    (appl.applEducationLevel?.contains(
                        query,
                        ignoreCase = true
                    ) == true) || (appl.applMinimumMonthlySalary.toString()
                ?.contains(query, ignoreCase = true) == true) ||
                    (appl.applLiveIn?.contains(query, ignoreCase = true) == true)

            // Add more fields to search if needed
        } ?: emptyList()
        _searchedApplList.postValue(searchedList)
    }
}