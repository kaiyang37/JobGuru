package com.example.jobguru.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.jobguru.model.ApplyModel
import com.example.jobguru.model.InterviewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EmpSetInterviewViewModel : ViewModel() {
    private val dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Interviews")

    var selectedDate: String = ""
    var selectedTime: String = ""

    val platforms =
        arrayOf("Google Meet", "Zoom", "Phone Calls", "Company Office", "Conference Room")

    var selectedPlatform: String = ""
    // Function to save job data
    fun saveInterviewData(
        intvwId:String,
        applicantName: String,
        jobTitle: String,
        interviewerName: String,
        interviewDate: String,
        interviewTime: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val selectedDateTime = parseDateTime(interviewDate, interviewTime)
        val currentDateTime = Calendar.getInstance().time

        if (interviewerName.isEmpty()) {
            onError("Interviewer Name cannot be empty. Please provide a valid interviewer name.")
            return
        } else if (selectedDateTime == null) {
            onError("Invalid date or time format.")
            return
        } else if (selectedDateTime.before(currentDateTime)) {
            onError("Interview date and time cannot be in the past. Please select a valid date and time.")
            return
        } else {

            generateNextInterviewId { nextInterviewId ->
                val interview = InterviewModel(
                    nextInterviewId,
                    applicantName,
                    jobTitle,
                    interviewerName,
                    interviewDate,
                    interviewTime,
                    selectedPlatform, "Pending", ""
                )

                dbRef.child(nextInterviewId).setValue(interview).addOnCompleteListener {
                    if(intvwId.isNotEmpty()) {
                        deleteRejectedInterview(intvwId)
                    }
                    onSuccess()
                }.addOnFailureListener {
                    onError("Error inserting the data")
                }
            }
        }
    }

    private fun deleteRejectedInterview(
        id: String
    ) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Interviews").child(id)
        dbRef.removeValue()
    }

    private fun parseDateTime(dateString: String, timeString: String): Date? {
        val datePattern = "yyyy-MM-dd"
        val timePattern = "HH:mm"

        val dateFormat = SimpleDateFormat("$datePattern $timePattern", Locale.US)

        try {
            val combinedDateTimeString = "$dateString $timeString"
            return dateFormat.parse(combinedDateTimeString)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    // Function to generate the next interview ID
    private fun generateNextInterviewId(callback: (String) -> Unit) {
        val query = dbRef.orderByKey().limitToLast(1)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (childSnapshot in dataSnapshot.children) {
                        val lastInterviewId = childSnapshot.key
                        if (lastInterviewId != null) {
                            val numericPart = lastInterviewId.substring(1).toInt()
                            val nextNumericPart = numericPart + 1
                            val nextInterviewId = "I$nextNumericPart"

                            callback(nextInterviewId)
                        }
                    }
                } else {
                    // If no jobs exist, start with I1
                    callback("I1")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors if any
                // You can provide a default ID or an error message here
                callback("I1")
            }
        })
    }


    fun getJobId(applId: String, delimitedJobIds: String): String {
        val applyRef = FirebaseDatabase.getInstance().getReference("Apply")
        val jobIdList = delimitedJobIds.split(",")
        var jTitle = ""

        for (jobId in jobIdList) {
            val query = applyRef.orderByChild("jobId").equalTo(jobId)

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (applySnap in dataSnapshot.children) {
                            val applyData = applySnap.getValue(ApplyModel::class.java)

                            if (applyData != null && applyData.applId.equals(applId) && applyData.appStatus == "Pending") {
                                //holder.tvJobTitle.text = jobId
                                jTitle = getJobTitle(jobId)
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors
                    Log.e("Firebase", "Error: ${databaseError.message}")
                }

            })
        }

        return jTitle
    }

    fun getJobTitle(jobId: String): String {
        val jobRef = FirebaseDatabase.getInstance().getReference("Jobs")
        val jobQuery = jobRef.orderByChild("jobId").equalTo(jobId)
        var jTitle = ""

        jobQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Assuming jobTitle is stored as a child under the jobId node
                    val jobTitle = dataSnapshot.child(jobId).child("jobTitle").value.toString()
                    jTitle = jobTitle
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
                Log.e("Firebase", "Error: ${databaseError.message}")
            }
        })

        return jTitle
    }
}