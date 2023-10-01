package com.example.jobguru.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jobguru.model.InterviewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EmpInterviewViewModel : ViewModel()  {

    private val _interviewList = MutableLiveData<List<InterviewModel>>()
    val interviewList: LiveData<List<InterviewModel>>
        get() = _interviewList

    private val _searchedInterviewList = MutableLiveData<List<InterviewModel>>()
    val searchedInterviewList: LiveData<List<InterviewModel>>
        get() = _searchedInterviewList

    init {
        getInterviewsData()
    }

    private fun getInterviewsData() {
        val dbRef = FirebaseDatabase.getInstance().getReference("Interviews")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val interviewList = mutableListOf<InterviewModel>()
                if (snapshot.exists()) {
                    for (interviewSnap in snapshot.children) {
                        val interviewData = interviewSnap.getValue(InterviewModel::class.java)
                        interviewData?.let {
                            interviewList.add(it)
                        }
                    }
                }
                _interviewList.postValue(interviewList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error if needed
            }
        })
    }

    fun searchInterviews(query: String) {
        val searchedList = _interviewList.value?.filter { interview ->
            (interview.jobTitle?.contains(query, ignoreCase = true) == true) ||
                    (interview.applName?.contains(query, ignoreCase = true) == true) ||
                    (interview.intvwDate?.contains(query, ignoreCase = true) == true)||
                    (interview.intvwTime?.contains(query, ignoreCase = true) == true)||
                    (interview.intvwStatus?.contains(query, ignoreCase = true) == true)

            // Add more fields to search if needed
        } ?: emptyList()
        _searchedInterviewList.postValue(searchedList)
    }
}