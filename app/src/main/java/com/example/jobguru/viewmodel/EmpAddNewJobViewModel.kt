package com.example.jobguru.viewmodel

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

    // Function to save job data
    fun saveJobData(
        empEmail: String,
        jobTitle: String,
        jobDesc: String,
        jobMinSalary: Double,
        jobMaxSalary: Double,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        getEmpId(empEmail)
        if (jobTitle.isEmpty()) {
            onError("Job title cannot be empty. Please provide a valid job title.")
            return
        } else if (jobDesc.isEmpty()) {
            onError("Job Description cannot be empty. Please provide a valid job description.")
            return
        }

        try {
            if (jobMinSalary < 0.0 || jobMaxSalary == 0.0 || jobMinSalary > jobMaxSalary) {
                onError("Invalid job minimum or maximum salary. Please provide valid job monthly salary.")
                return
            }

            generateNextJobId { nextJobId ->
                val job = JobModel(
                    nextJobId,
                    empId,
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
                    onError("Error inserting the data")
                }
            }
        } catch (e: NumberFormatException) {
            onError("Invalid salary format. Please enter a valid number.")
        }
    }

    private fun getEmpId(email: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Employers")
        val query = databaseReference.orderByChild("personInChargeEmail").equalTo(email)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    empId = dataSnapshot.children.first().key.toString()
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