package com.example.jobguru.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jobguru.model.JobModel
import com.example.jobguru.model.ApplicantModel
import com.example.jobguru.model.ApplyModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class EmpApplicantDetailsViewModel : ViewModel() {
    private val _applFirstName = MutableLiveData<String>()
    val applFirstName: LiveData<String> = _applFirstName

    private val _applLastName = MutableLiveData<String>()
    val applLastName: LiveData<String> = _applLastName

    private val _applGender = MutableLiveData<String>()
    val applGender: LiveData<String> = _applGender

    private val _applEmail = MutableLiveData<String>()
    val applEmail: LiveData<String> = _applEmail

    private val _applLiveIn = MutableLiveData<String>()
    val applLiveIn: LiveData<String> = _applLiveIn

    private val _applAreaCode = MutableLiveData<String>()
    val applAreaCode: LiveData<String> = _applAreaCode

    private val _applPhoneNumber = MutableLiveData<String>()
    val applPhoneNumber: LiveData<String> = _applPhoneNumber

    private val _applNationality = MutableLiveData<String>()
    val applNationality: LiveData<String> = _applNationality

    private val _applMinimumMonthlySalary = MutableLiveData<Double>()
    val applMinimumMonthlySalary: LiveData<Double> = _applMinimumMonthlySalary

    private val _applEducationLevel = MutableLiveData<String>()
    val applEducationLevel: LiveData<String> = _applEducationLevel

    private val _applInstitute = MutableLiveData<String>()
    val applInstitute: LiveData<String> = _applInstitute

    private val _applFieldOfStudies = MutableLiveData<String>()
    val applFieldOfStudies: LiveData<String> = _applFieldOfStudies

    private val _applLocation = MutableLiveData<String>()
    val applLocation: LiveData<String> = _applLocation

    private val _applYearOfGraduation = MutableLiveData<String>()
    val applYearOfGraduation: LiveData<String> = _applYearOfGraduation

    private val _applMonthOfGraduation = MutableLiveData<String>()
    val applMonthOfGraduation: LiveData<String> = _applMonthOfGraduation

    private val _applJobTitle = MutableLiveData<String>()
    val applJobTitle: LiveData<String> = _applJobTitle

    private val _applCompanyName = MutableLiveData<String>()
    val applCompanyName: LiveData<String> = _applCompanyName

    private val _applStartDate = MutableLiveData<String>()
    val applStartDate: LiveData<String> = _applStartDate

    private val _applEndDate = MutableLiveData<String>()
    val applEndDate: LiveData<String> = _applEndDate

    private val _applCompanyIndustry = MutableLiveData<String>()
    val applCompanyIndustry: LiveData<String> = _applCompanyIndustry

    fun getApplicantData(applId: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Applicants")

        val query = dbRef.orderByChild("applId").equalTo(applId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val applicantData =
                        snapshot.children.first().getValue(ApplicantModel::class.java)
                    _applFirstName.value = applicantData?.applFirstName
                    _applLastName.value = applicantData?.applLastName
                    _applGender.value = applicantData?.applGender
                    _applEmail.value = applicantData?.applEmail
                    _applLiveIn.value = applicantData?.applLiveIn
                    _applAreaCode.value = applicantData?.applAreaCode
                    _applPhoneNumber.value = applicantData?.applPhoneNumber
                    _applNationality.value = applicantData?.applNationality
                    _applMinimumMonthlySalary.value = applicantData?.applMinimumMonthlySalary
                    _applEducationLevel.value = applicantData?.applEducationLevel
                    _applInstitute.value = applicantData?.applInstitute
                    _applFieldOfStudies.value = applicantData?.applFieldOfStudies
                    _applLocation.value = applicantData?.applLocation
                    _applYearOfGraduation.value = applicantData?.applYearOfGraduation
                    _applMonthOfGraduation.value = applicantData?.applMonthOfGraduation
                    _applJobTitle.value = applicantData?.applJobTitle
                    _applCompanyName.value = applicantData?.applCompanyName
                    _applStartDate.value = applicantData?.applStartDate
                    _applEndDate.value = applicantData?.applEndDate
                    _applCompanyIndustry.value = applicantData?.applCompanyIndustry
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error if needed
                Log.e("Firebase", "Error: ${error.message}")
            }
        })
    }

    fun rejectApplicant(
        appId: String, onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val applyRef = FirebaseDatabase.getInstance().getReference("Apply")
        val query = applyRef.orderByChild("appId").equalTo(appId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (applicantSnapshot in dataSnapshot.children) {
                        val updateData = HashMap<String, Any?>()
                        updateData["appStatus"] = "Rejected"
                        val specificRecordRef = applicantSnapshot.ref
                        specificRecordRef.updateChildren(updateData).addOnCompleteListener{
                            onSuccess()
                        }.addOnFailureListener{
                            onError("Error rejecting the applicant")
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
}