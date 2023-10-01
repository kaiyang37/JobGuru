package com.example.jobguru.viewmodel

import androidx.lifecycle.ViewModel
import com.example.jobguru.model.SaveJobModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SaveJobInsertionViewModel : ViewModel(){
    private val dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Saved Jobs")

    fun saveJob(
        jobId: String,
        applId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        generateNextSaveJobId { nextSaveJobId ->
            val saveJob = SaveJobModel(
                nextSaveJobId,
                jobId,
                applId
            )

            dbRef.child(nextSaveJobId).setValue(saveJob)
                .addOnCompleteListener {
                    onSuccess()
                }.addOnFailureListener { err ->
                    onError("Error inserting the data")
                }
        }
    }

    // Function to generate the next job ID
    private fun generateNextSaveJobId(callback: (String) -> Unit) {
        // Query the database to find the current maximum save job ID
        val query = dbRef.orderByKey().limitToLast(1)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (childSnapshot in dataSnapshot.children) {
                        val lastApplicantId = childSnapshot.key
                        if (lastApplicantId != null) {
                            // Extract the numeric part of the ID and increment it
                            val numericPart = lastApplicantId.substring(1).toInt()
                            val nextNumericPart = numericPart + 1
                            val nextSaveJobId = "SJ$nextNumericPart"

                            // Now you have the next available custom ID
                            // Call the callback function with the ID
                            callback(nextSaveJobId)
                        }
                    }
                } else {
                    // If no employees exist, start with SJ1
                    callback("SJ1")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors if any
                // You can provide a default ID or an error message here
                callback("SJ1")
            }
        })
    }
}