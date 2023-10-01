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

class EmpApplicantDetailsViewModel: ViewModel() {

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

    private val _applLiveIn = MutableLiveData<String>()
    val applLiveIn: LiveData<String>
        get() = _applLiveIn

    private val _applAreaCode = MutableLiveData<String>()
    val applAreaCode: LiveData<String>
        get() = _applAreaCode

    private val _applPhoneNumber = MutableLiveData<String>()
    val applPhoneNumber: LiveData<String>
        get() = _applPhoneNumber

    private val _applNationality = MutableLiveData<String>()
    val applNationality: LiveData<String>
        get() = _applNationality

    private val _applMinimumMonthlySalary = MutableLiveData<Double>()
    val applMinimumMonthlySalary: LiveData<Double>
        get() = _applMinimumMonthlySalary

    private val _applEducationLevel = MutableLiveData<String>()
    val applEducationLevel: LiveData<String>
        get() = _applEducationLevel

    private val _applInstitute = MutableLiveData<String>()
    val applInstitute: LiveData<String>
        get() = _applInstitute

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

    fun getApplicantData(applId: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Applicants")

        val query = dbRef.orderByChild("applId").equalTo(applId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val applicantData = snapshot.children.first().getValue(ApplicantModel::class.java)
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
            }
        })
    }

    fun rejectApplicant(applId: String, delimitedJobIds: String){
            val applyRef = FirebaseDatabase.getInstance().getReference("Apply")
            val jobIdList = delimitedJobIds.split(",")

            for (jobId in jobIdList) {
                val query = applyRef.orderByChild("jobId").equalTo(jobId)

                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (applySnap in dataSnapshot.children) {
                                val applyData = applySnap.getValue(ApplyModel::class.java)

                                if (applyData != null && applyData.applId.equals(applId) && applyData.appStatus == "Pending") {
                                    updateApplyStatus(applId, jobId)
                                    Log.d("My Tag", "Yes")
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
    fun updateApplyStatus(applId: String, jobId: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Apply")
        val query = dbRef.orderByChild("applId").equalTo(applId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Loop through the matching records (there may be multiple with the same applId)
                    for (applySnapshot in dataSnapshot.children) {
                        val applyData = applySnapshot.getValue(ApplyModel::class.java)
                        if (applyData != null && applyData.jobId == jobId) {
                            // Get a reference to the specific record and update appStatus
                            val specificRecordRef = dbRef.child(applySnapshot.key!!)
                            val updateData = HashMap<String, Any?>()
                            updateData["appStatus"] = "Rejected"

                            specificRecordRef.updateChildren(updateData)
                                .addOnSuccessListener {
                                    // Update was successful
                                    Log.d("My Tag", "Update appStatus success")
                                }
                                .addOnFailureListener { e ->
                                    // Handle the error
                                    Log.e("Firebase", "Error updating appStatus: ${e.message}")
                                }
                        }
                    }
                } else {
                    Log.d("My Tag", "Does not exist")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
                Log.e("Firebase", "Error: ${databaseError.message}")
            }
        })
    }
}