package com.example.jobguru.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    val platforms =
        arrayOf("Google Meet", "Zoom", "Phone Calls", "Company Office", "Conference Room")

    var selectedPlatform: String = ""
    var selectedDate: String = ""
    var selectedTime: String = ""

    private val _interviewDateError = MutableLiveData<String>()
    val interviewDateError: LiveData<String> = _interviewDateError

    private val _interviewTimeError = MutableLiveData<String>()
    val interviewTimeError: LiveData<String> = _interviewTimeError

    private val _interviewerNameError = MutableLiveData<String>()
    val interviewerNameError: LiveData<String> = _interviewerNameError

    fun validateData(
        interviewerName: String,
        selectedDate: String,
        selectedTime: String
    ): Boolean {
        _interviewDateError.value = null
        _interviewTimeError.value = null
        _interviewerNameError.value = null
        var isValid = true

        val selectedDateTime = parseDateTime(selectedDate, selectedTime)
        val currentDateTime = Calendar.getInstance().time


        if (interviewerName.isBlank()) {
            _interviewerNameError.value = "Interviewer name is required"
            isValid = false
        }

        if (selectedDateTime == null) {
            _interviewDateError.value = "Interview date is required"
            _interviewTimeError.value = "Interview time is required"
            isValid = false
        }else {
            if (selectedDateTime.before(currentDateTime)) {
                _interviewDateError.value = "Selected interview date cannot be in the past"
                _interviewTimeError.value = "Selected interview time cannot be in the past"
                isValid = false
            }
        }

        return isValid
    }

    fun saveInterviewData(
        jobId: String,
        applId: String,
        applName: String,
        empName: String,
        jobTitle: String,
        interviewerName: String,
        interviewDate: String,
        interviewTime: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        generateNextInterviewId { nextInterviewId ->
            val interview = InterviewModel(
                nextInterviewId,
                jobId,
                applId,
                applName,
                empName,
                jobTitle,
                interviewerName,
                interviewDate,
                interviewTime,
                selectedPlatform, "Pending", "", false
            )

            dbRef.child(nextInterviewId).setValue(interview).addOnCompleteListener {
                onSuccess()
            }.addOnFailureListener {
                onError("Error setting the interview session")
            }
        }
    }

    fun acceptApplicant(
        appId: String
    ) {
        val applyRef = FirebaseDatabase.getInstance().getReference("Apply")
        val query = applyRef.orderByChild("appId").equalTo(appId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (applicantSnapshot in dataSnapshot.children) {
                        val updateData = HashMap<String, Any?>()
                        updateData["appStatus"] = "Accepted"
                        val specificRecordRef = applicantSnapshot.ref
                        specificRecordRef.updateChildren(updateData)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
                Log.e("Firebase", "Error: ${databaseError.message}")
            }
        })
    }

    fun updateResendStatus(
        intvwId: String,
        isResend: Boolean
    ) {
        val debRef = FirebaseDatabase.getInstance().getReference("Interviews")
        val query = debRef.orderByChild("intvwId").equalTo(intvwId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (interviewSnapshot in dataSnapshot.children) {
                        val updateData = HashMap<String, Any?>()
                        updateData["resend"] = isResend
                        val specificRecordRef = interviewSnapshot.ref
                        specificRecordRef.updateChildren(updateData)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
                Log.e("Firebase", "Error: ${databaseError.message}")
            }
        })
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
}