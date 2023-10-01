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