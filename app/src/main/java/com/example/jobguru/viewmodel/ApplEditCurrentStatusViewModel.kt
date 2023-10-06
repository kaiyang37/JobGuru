package com.example.jobguru.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ApplEditCurrentStatusViewModel : ViewModel() {
    val liveIns = arrayOf(
        "Malaysia, Johor",
        "Malaysia, Kedah",
        "Malaysia, Kelantan",
        "Malaysia, Kuala Lumpur",
        "Malaysia, Melaka",
        "Malaysia, Negeri Sembilan",
        "Malaysia, Pahang",
        "Malaysia, Perak",
        "Malaysia, Perlis",
        "Malaysia, Penang",
        "Malaysia, Sabah",
        "Malaysia, Sarawak",
        "Malaysia, Selangor",
        "Malaysia, Terengganu"
    )

    val nationalities = arrayOf(
        "Malaysia",
        "Singapore",
        "Indonesia",
        "Thailand",
        "Philippines",
        "Vietnam",
        "Myanmar"
    )

    val salaries = arrayOf(
        "MYR 0",
        "MYR 500",
        "MYR 1000",
        "MYR 1500",
        "MYR 2000",
        "MYR 2500",
        "MYR 3000",
        "MYR 3500",
        "MYR 4000",
        "MYR 4500",
        "MYR 5000",
        "MYR 5500",
        "MYR 6000",
        "MYR 6500",
        "MYR 7000",
        "MYR 7500",
        "MYR 8000",
        "MYR 8500",
        "MYR 9000",
        "MYR 9500",
        "MYR 10000"
    )

    // Properties to hold selected values
    var selectedLiveIn: String = ""
    var selectedNationality: String = ""
    var selectedSalaries: String = ""

    fun updateCurrentStatusData(
        applId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Applicants").child(applId)
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val updatedFields = mutableMapOf<String, Any?>()

                    updatedFields["applLiveIn"] = selectedLiveIn
                    updatedFields["applNationality"] = selectedNationality
                    val salaryString = selectedSalaries.substring(4).replace(",", "").trim() // Remove "MYR " and commas
                    val salaryDouble = salaryString.toDoubleOrNull() ?: 0.0 // Parse as double or default to 0.0
                    updatedFields["applMinimumMonthlySalary"] = salaryDouble

                    dbRef.updateChildren(updatedFields)
                    onSuccess()
                } else {
                    onError("Update Applicant Current Status Error")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.message)
            }
        })

    }
}