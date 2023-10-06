package com.example.jobguru.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jobguru.model.ApplicantModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.DecimalFormat

class ApplManageProfileViewModel : ViewModel() {
    private val _applFirstName = MutableLiveData<String>()
    val applFirstName: LiveData<String>
        get() = _applFirstName

    private val _applLastName = MutableLiveData<String>()
    val applLastName: LiveData<String>
        get() = _applLastName

    private val _applGender = MutableLiveData<String>()
    val applGender: LiveData<String>
        get() = _applGender

    private val _applEmail = MutableLiveData<String>()
    val applEmail: LiveData<String>
        get() = _applEmail

    private val _applAreaCode = MutableLiveData<String>()
    val applAreaCode: LiveData<String>
        get() = _applAreaCode

    private val _applPhoneNum = MutableLiveData<String>()
    val applPhoneNum: LiveData<String>
        get() = _applPhoneNum

    private val _applNationality = MutableLiveData<String>()
    val applNationality: LiveData<String>
        get() = _applNationality

    private val _applExpectedSalary = MutableLiveData<String>()
    val applExpectedSalary: LiveData<String>
        get() = _applExpectedSalary

    private val _applEducationLevel = MutableLiveData<String>()
    val applEducationLevel: LiveData<String>
        get() = _applEducationLevel

    private val _applFieldOfStudies = MutableLiveData<String>()
    val applFieldOfStudies: LiveData<String>
        get() = _applFieldOfStudies

    private val _applLocation = MutableLiveData<String>()
    val applLocation: LiveData<String>
        get() = _applLocation

    private val _applYearOfGraduation = MutableLiveData<String>()
    val applYearOfGraduation: LiveData<String>
        get() = _applYearOfGraduation

    private val _applMonthOfGraduation = MutableLiveData<String>()
    val applMonthOfGraduation: LiveData<String>
        get() = _applMonthOfGraduation

    private val _applJobTitle = MutableLiveData<String>()
    val applJobTitle: LiveData<String>
        get() = _applJobTitle

    private val _applCompanyName = MutableLiveData<String>()
    val applCompanyName: LiveData<String>
        get() = _applCompanyName

    private val _applStartDate = MutableLiveData<String>()
    val applStartDate: LiveData<String>
        get() = _applStartDate

    private val _applEndDate = MutableLiveData<String>()
    val applEndDate: LiveData<String>
        get() = _applEndDate

    private val _applCompanyIndustry = MutableLiveData<String>()
    val applCompanyIndustry: LiveData<String>
        get() = _applCompanyIndustry

    private val _applResume = MutableLiveData<String>()
    val applResume: LiveData<String>
        get() = _applResume

    private val _applInstitute = MutableLiveData<String>()
    val applInstitute: LiveData<String>
        get() = _applInstitute

    private val _applLiveIn = MutableLiveData<String>()
    val applLiveIn: LiveData<String>
        get() = _applLiveIn

    fun getApplicantData(applId: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Applicants")

        val query = dbRef.orderByChild("applId").equalTo(applId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val applData = snapshot.children.first().getValue(ApplicantModel::class.java)
                    _applGender.value = applData?.applGender
                    _applFirstName.value = applData?.applFirstName
                    _applLastName.value = applData?.applLastName
                    _applEmail.value = applData?.applEmail
                    _applAreaCode.value = applData?.applAreaCode
                    _applPhoneNum.value = applData?.applPhoneNumber
                    _applJobTitle.value = applData?.applJobTitle
                    _applCompanyName.value = applData?.applCompanyName
                    _applStartDate.value = applData?.applStartDate
                    _applEndDate.value = applData?.applEndDate
                    _applCompanyIndustry.value = applData?.applCompanyIndustry
                    _applInstitute.value = applData?.applInstitute
                    _applLocation.value = applData?.applLocation
                    _applYearOfGraduation.value = applData?.applYearOfGraduation
                    _applMonthOfGraduation.value = applData?.applMonthOfGraduation
                    _applEducationLevel.value = applData?.applEducationLevel
                    _applFieldOfStudies.value = applData?.applFieldOfStudies
                    _applLiveIn.value = applData?.applLiveIn
                    _applNationality.value = applData?.applNationality
                    val salary = applData?.applMinimumMonthlySalary ?: 0.0 // Replace 0.0 with the default value you want
                    val decimalFormat = DecimalFormat("#") // Format to display with no decimal places
                    val formattedSalary = decimalFormat.format(salary)
                    _applExpectedSalary.value = "MYR $formattedSalary"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error if needed
            }
        })
    }
}