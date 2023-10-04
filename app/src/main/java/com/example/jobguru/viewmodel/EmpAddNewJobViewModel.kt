package com.example.jobguru.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jobguru.model.JobModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EmpAddNewJobViewModel : ViewModel() {
    private val dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Jobs")

    val roles = arrayOf("Full-Time", "Part-Time")
    val specializations = arrayOf(
        "Accounting/Finance",
        "Admin/HR",
        "Arts/Media",
        "Building/Construction",
        "Computer/IT",
        "Education/Training",
        "Engineering",
        "Healthcare",
        "Hotel/Dormitory",
        "Manufacturing",
        "Restaurant",
        "Sales/Marketing",
        "Sciences",
        "Services",
    )
    val yearsOfExperience =
        arrayOf("1 year", "2 years", "3 years", "4 years", "More than 5 years")
    val states = arrayOf(
        "Johor",
        "Kedah",
        "Kelantan",
        "Kuala Lumpur",
        "Melaka",
        "Negeri Sembilan",
        "Pahang",
        "Penang",
        "Perak",
        "Perlis",
        "Sabah",
        "Sarawak",
        "Selangor",
        "Terengganu"
    )

    // Properties to hold selected values
    var selectedRole: String = ""
    var selectedSpecialization: String = ""
    var selectedYearOfExp: String = ""
    var selectedState: String = ""
    var empId: String = ""
    var empName: String = ""

    // LiveData for error messages
    private val _jobTitleError = MutableLiveData<String>()
    val jobTitleError: LiveData<String> = _jobTitleError

    private val _jobDescError = MutableLiveData<String>()
    val jobDescError: LiveData<String> = _jobDescError

    private val _minSalaryError = MutableLiveData<String>()
    val minSalaryError: LiveData<String> = _minSalaryError

    private val _maxSalaryError = MutableLiveData<String>()
    val maxSalaryError: LiveData<String> = _maxSalaryError

    fun validateData(
        jobTitle: String,
        jobDesc: String,
        jobMinSalary: Double,
        jobMaxSalary: Double
    ): Boolean {

        // Clear previous error messages
        _jobTitleError.value = null
        _jobDescError.value = null
        _minSalaryError.value = null
        _maxSalaryError.value = null

        var isValid = true

        if (jobTitle.isBlank()) {
            _jobTitleError.value = "Job title is required"
            isValid = false
        }

        if (jobDesc.isBlank()) {
            _jobDescError.value = "Job description is required"
            isValid = false
        }

        if (jobMinSalary == 0.0 && jobMaxSalary == 0.0) {
            _minSalaryError.value = "Job minimum salary is required"
            _maxSalaryError.value = "Job maximum salary is required"
            isValid = false
        }else {

            if (jobMinSalary < 0.0) {
                _minSalaryError.value = "Job minimum salary cannot be less than 0"
                isValid = false
            } else if (jobMinSalary > jobMaxSalary) {
                _minSalaryError.value = "Job minimum salary cannot be more than job maximum salary"
                isValid = false
            }

            if (jobMaxSalary == 0.0) {
                _maxSalaryError.value = "Job maximum salary cannot be 0"
                isValid = false
            } else if (jobMinSalary > jobMaxSalary) {
                _maxSalaryError.value = "Job maximum salary cannot be less than job minimum salary"
                isValid = false
            }
        }

        return isValid
    }

    // Function to save job data
    fun saveJobData(
        empEmail: String,
        jobTitle: String,
        jobMinSalary: Double,
        jobMaxSalary: Double,
        jobDesc: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        getEmpInfo(empEmail)
        generateNextJobId { nextJobId ->
            val job = JobModel(
                nextJobId,
                empId,
                empName,
                empEmail,
                jobTitle,
                selectedRole,
                selectedSpecialization,
                selectedYearOfExp,
                selectedState,
                jobMinSalary,
                jobMaxSalary,
                jobDesc
            )

            dbRef.child(nextJobId).setValue(job).addOnCompleteListener {
                onSuccess()
            }.addOnFailureListener {
                onError("Error inserting the job")
            }
        }
    }

    private fun getEmpInfo(email: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Employers")
        val query = databaseReference.orderByChild("personInChargeEmail").equalTo(email)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val employerSnapshot = dataSnapshot.children.first()
                    empId = employerSnapshot.key.toString()
                    empName =
                        employerSnapshot.child("empName").getValue(String::class.java).toString()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                //Handle error
            }
        })
    }

    // Function to generate the next job ID
    private fun generateNextJobId(callback: (String) -> Unit) {
        val query = dbRef.orderByKey().limitToLast(1)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (childSnapshot in dataSnapshot.children) {
                        val lastJobId = childSnapshot.key
                        if (lastJobId != null) {
                            val numericPart = lastJobId.substring(1).toInt()
                            val nextNumericPart = numericPart + 1
                            val nextJobId = "J$nextNumericPart"

                            callback(nextJobId)
                        }
                    }
                } else {
                    // If no jobs exist, start with J1
                    callback("J1")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors if any
                // You can provide a default ID or an error message here
                callback("J1")
            }
        })
    }
}