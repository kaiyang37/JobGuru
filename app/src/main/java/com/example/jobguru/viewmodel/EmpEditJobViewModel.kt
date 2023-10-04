package com.example.jobguru.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase

class EmpEditJobViewModel : ViewModel() {
    val roles = arrayOf("Full-Time", "Part-Time")
    val specializations = arrayOf(
        "Accounting/Finance",
        "Admin/Human Resources",
        "Arts/Media/Communications",
        "Building/Construction",
        "Computer/IT",
        "Services",
        "Sciences",
        "Sales/Marketing",
        "Restaurant",
        "Manufacturing",
        "Hotel/Dormitory",
        "Healthcare",
        "Engineering",
        "Education/Training"
    )
    val yearsOfExperience =
        arrayOf("1 year", "2 years", "3 years", "4 years", "More than 5 years")
    val states = arrayOf(
        " Johor",
        "Kedah",
        "Kelantan",
        "Kuala Lumpur",
        "Labuan",
        "Melaka",
        "Negeri Sembilan",
        "Pahang",
        "Perak",
        "Perlis",
        "Pulau Pinang",
        "Putrajaya",
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

    fun updateJobData(
        jobId: String,
        jobTitle: String,
        jobDesc: String,
        jobMinSalary: Double,
        jobMaxSalary: Double,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        if (jobTitle.isEmpty()) {
            onError("Job title cannot be empty. Please provide a valid job title.")
        } else if (jobDesc.isEmpty()) {
            onError("Job Description cannot be empty. Please provide a valid job description.")
        } else {
            try {
                if (jobMinSalary < 0.0 || jobMaxSalary == 0.0 || jobMinSalary > jobMaxSalary) {
                    onError("Invalid job minimum or maximum salary. Please provide valid job monthly salary.")
                } else {
                    val dbRef = FirebaseDatabase.getInstance().getReference("Jobs").child(jobId)
                    val updatedFields = mutableMapOf<String, Any?>()

                    updatedFields["jobTitle"] = jobTitle
                    updatedFields["jobDesc"] = jobDesc
                    updatedFields["jobMinSalary"] = jobMinSalary
                    updatedFields["jobMaxSalary"] = jobMaxSalary
                    updatedFields["jobRole"] = selectedRole
                    updatedFields["jobSpecialization"] = selectedSpecialization
                    updatedFields["jobYearOfExp"] = selectedYearOfExp
                    updatedFields["jobWorkState"] = selectedState

                    dbRef.updateChildren(updatedFields)
                    onSuccess()

                }
            } catch (e: NumberFormatException) {
                // Handle cases where salary values cannot be parsed to double
                onError("Invalid salary format. Please enter a valid number.")
            }
        }
    }

}