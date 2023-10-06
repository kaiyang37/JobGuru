package com.example.jobguru.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ApplEditEducationViewModel : ViewModel() {
    val educationLvls = arrayOf(
        "Bachelor Degree",
        "Diploma",
        "Doctorate (PhD)",
        "SPM",
        "STPM"
    )
    val fieldOfStudies1 = arrayOf(
        "Accounting/Finance",
        "Arts/Media",
        "Architecture",
        "Computer/IT",
        "Education/Training",
        "Engineering",
        "HR Management",
        "History",
        "Hospitality",
        "Logistic",
        "Personal Services",
        "Pharmacy",
        "Sales/Marketing",
        "Sciences"
    )

    val fieldOfStudies2 = arrayOf(
        "Sciences",
        "Arts"
    )

    val locations = arrayOf(
        "Malaysia",
        "Singapore",
        "Indonesia",
        "Thailand",
        "Philippines",
        "Vietnam",
        "Myanmar"
    )

    val years = arrayOf(
        "2023",
        "2022",
        "2021",
        "2020",
        "2019",
        "2018",
        "2017",
        "2016",
        "2015",
        "2014",
        "2013",
        "2012",
        "2011",
        "2010",
        "2009",
        "2008",
        "2007",
        "2006",
        "2005",
        "2004",
        "2003"
    )

    val months = arrayOf(
        "January", "February", "March", "April", "May", "June", "July", "August",
        "September", "October", "November", "December"
    )

    // Properties to hold selected values
    var selectedEducationLvl: String = ""
    var selectedFieldOfStudies: String = ""
    var selectedLocation: String = ""
    var selectedYearOfGraduation: String = ""
    var selectedMonthOfGraduation: String = ""

    // LiveData for error messages
    private val _instituteError = MutableLiveData<String>()
    val instituteError: LiveData<String> = _instituteError

    fun validateData(institute: String): Boolean {
        // Clear previous error messages
        _instituteError.value = null

        var isValid = true

        if (institute.isBlank()) {
            _instituteError.value = "Institute is required"
            isValid = false
        }

        return isValid
    }

    fun updateEducationData(
        applId: String, institute: String, onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Applicants").child(applId)
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val updatedFields = mutableMapOf<String, Any?>()

                    updatedFields["applEducationLevel"] = selectedEducationLvl
                    updatedFields["applInstitute"] = institute
                    updatedFields["applFieldOfStudies"] = selectedFieldOfStudies
                    updatedFields["applLocation"] = selectedLocation
                    updatedFields["applYearOfGraduation"] = selectedYearOfGraduation
                    updatedFields["applMonthOfGraduation"] = selectedMonthOfGraduation

                    dbRef.updateChildren(updatedFields)
                    onSuccess()
                } else {
                    onError("Update Applicant Education Error")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.message)
            }
        })

    }
}