package com.example.jobguru.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jobguru.model.JobModel
import com.example.jobguru.model.SaveJobModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ApplSavedJobsViewModel : ViewModel() {
    private val _saveJobList = MutableLiveData<List<SaveJobModel>>()
    val saveJobList: LiveData<List<SaveJobModel>>
        get() = _saveJobList

    private val _filteredJobList = MutableLiveData<List<SaveJobModel>>()
    val filteredJobList: LiveData<List<SaveJobModel>>
        get() = _filteredJobList

    init {
        getSavedJobsData()
    }

    private fun getSavedJobsData() {
        val dbRef = FirebaseDatabase.getInstance().getReference("Saved Jobs")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val saveJobList = mutableListOf<SaveJobModel>()
                if (snapshot.exists()) {
                    for (saveJobSnap in snapshot.children) {
                        val jobData = saveJobSnap.getValue(SaveJobModel::class.java)
                        jobData?.let {
                            saveJobList.add(it)
                        }
                    }
                }
                _saveJobList.postValue(saveJobList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error if needed
            }
        })
    }

    fun filterSavedJobsBasedOnAppl(applId: String?) {

        val filteredList = _saveJobList.value?.filter{ job ->


            applId == job.applId

        } ?: emptyList()

        _filteredJobList.postValue(filteredList)
    }
}