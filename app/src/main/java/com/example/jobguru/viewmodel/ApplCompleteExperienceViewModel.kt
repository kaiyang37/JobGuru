package com.example.jobguru.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar

class ApplCompleteExperienceViewModel : ViewModel() {

    // LiveData for error messages
    private val _startDateError = MutableLiveData<String>()
    val startDateError: LiveData<String> = _startDateError

    private val _endDateError = MutableLiveData<String>()
    val endDateError: LiveData<String> = _endDateError

    fun validateDate(startDate: String, endDate: String): Boolean {
        // Get the current month and year
        val currentDate = Calendar.getInstance()
        val currentYear = currentDate.get(Calendar.YEAR)
        val currentMonth = currentDate.get(Calendar.MONTH)

        val startDateParts = startDate.split(" ")
        var selectedStartMonth: Int? = 0
        var selectedStartYear: Int? = 0

        if (startDateParts.size >= 2) {
            selectedStartMonth = getMonthNumber(startDateParts[0])
            selectedStartYear = startDateParts[1].toIntOrNull() ?: 0
        }

        val endDateParts = endDate.split(" ")
        var selectedEndMonth: Int? = 0
        var selectedEndYear: Int? = 0

        if (endDateParts.size >= 2) {
            selectedEndMonth = getMonthNumber(endDateParts[0])
            selectedEndYear = endDateParts[1].toIntOrNull() ?: 0
        }
        _startDateError.value = null
        _endDateError.value = null
        var isValid = true

        if (selectedStartYear == currentYear && selectedStartMonth!! > currentMonth) {
            _startDateError.value = "Start date cannot be in the future"
            isValid = false

        } else if ((selectedStartYear!! > selectedEndYear!!) || (selectedStartYear!! == selectedEndYear!! && selectedStartMonth!! > selectedEndMonth!!)) {
            _startDateError.value = "Start date cannot be later than end date"
            _endDateError.value = "End date cannot be earlier than start date"
            isValid = false
        }

        if (selectedEndYear == currentYear && selectedEndMonth!! > currentMonth) {
            _endDateError.value = "End date cannot be in the future"
            isValid = false
        }
        return isValid
    }

    fun updateApplicantProfile(
        applId: String, applLiveIn: String,
        applAreaCode: String,
        applPhoneNumber: String,
        applNationality: String,
        applMinimumMonthlySalary: Double,
        applEducationLevel: String,
        applInstitute: String,
        applFieldOfStudies: String,
        applLocation: String,
        applYearOfGraduation: String,
        applMonthOfGraduation: String,
        applJobTitle: String,
        applCompanyName: String,
        applStartDate: String,
        applEndDate: String,
        applCompanyIndustry: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        val dbRef = FirebaseDatabase.getInstance().getReference("Applicants").child(applId)
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val updatedFields = mutableMapOf<String, Any?>()
                    updatedFields["applLiveIn"] = applLiveIn
                    updatedFields["applAreaCode"] = applAreaCode
                    updatedFields["applPhoneNumber"] = applPhoneNumber
                    updatedFields["applMinimumMonthlySalary"] = applMinimumMonthlySalary
                    updatedFields["applNationality"] = applNationality
                    updatedFields["applEducationLevel"] = applEducationLevel
                    updatedFields["applInstitute"] = applInstitute
                    updatedFields["applFieldOfStudies"] = applFieldOfStudies
                    updatedFields["applLocation"] = applLocation
                    updatedFields["applYearOfGraduation"] = applYearOfGraduation
                    updatedFields["applMonthOfGraduation"] = applMonthOfGraduation
                    updatedFields["applJobTitle"] = applJobTitle
                    updatedFields["applCompanyName"] = applCompanyName
                    updatedFields["applStartDate"] = applStartDate
                    updatedFields["applEndDate"] = applEndDate
                    updatedFields["applCompanyIndustry"] = applCompanyIndustry

                    // Update the specific employer data
                    dbRef.updateChildren(updatedFields)
                    onSuccess()
                } else {
                    onError("Update Applicant Profile Error")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.message)
            }
        })
    }

    private fun getMonthNumber(monthName: String): Int {
        val months = listOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
        return months.indexOf(monthName) + 1 // Adding 1 to make it 1-based (January = 1)
    }
}