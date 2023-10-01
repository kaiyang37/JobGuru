package com.example.jobguru.viewmodel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jobguru.model.ApplicantModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ApplSignUpViewModel : ViewModel() {

    private val _registrationSuccess = MutableLiveData<Boolean>()
    val registrationSuccess: LiveData<Boolean> = _registrationSuccess

    private val _dbErrorText = MutableLiveData<String>()
    val dbErrorText: LiveData<String> = _dbErrorText

    // LiveData for error messages
    private val _firstNameError = MutableLiveData<String>()
    val firstNameError: LiveData<String> = _firstNameError

    private val _lastNameError = MutableLiveData<String>()
    val lastNameError: LiveData<String> = _lastNameError

    private val _emailError = MutableLiveData<String>()
    val emailError: LiveData<String> = _emailError

    private val _passwordError = MutableLiveData<String>()
    val passwordError: LiveData<String> = _passwordError


    fun validateData(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Boolean {
        // Clear previous error messages
        _firstNameError.value = null
        _lastNameError.value = null
        _emailError.value = null
        _passwordError.value = null

        var isValid = true

        if (firstName.isBlank()) {
            _firstNameError.value = "First name is required"
            isValid = false
        }

        if (lastName.isBlank()) {
            _lastNameError.value = "Last name is required"
            isValid = false
        }

        if (email.isBlank()) {
            _emailError.value = "Email address is required"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailError.value = "Invalid email address"
            isValid = false
        }

        if (password.isBlank()) {
            _passwordError.value = "Password is required"
            isValid = false
        } else if (password.length < 8) {
            _passwordError.value = "Password must be at least 8 characters long"
            isValid = false
        } else if (!Regex("[A-Z]").containsMatchIn(password)) {
            _passwordError.value = "Password must contain at least one uppercase letter"
            isValid = false
        } else if (!Regex("[a-z]").containsMatchIn(password)) {
            _passwordError.value = "Password must contain at least one lowercase letter"
            isValid = false
        } else if (!Regex("\\d").containsMatchIn(password)) {
            _passwordError.value = "Password must contain at least one digit"
            isValid = false
        } else if (!Regex("[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]+").containsMatchIn(password)) {
            _passwordError.value = "Password must contain at least one special character"
            isValid = false
        }

        return isValid
    }

    private val auth = FirebaseAuth.getInstance()

    fun applSignUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _registrationSuccess.value = true
                } else {
                    handleRegistrationError(task.exception)
                }
            }
    }

    private fun handleRegistrationError(exception: Exception?) {
        when (exception) {
            is FirebaseAuthUserCollisionException -> {
                _dbErrorText.value = "The email address is already in use"
            }
            else -> {
                _dbErrorText.value = "An error occurred: ${exception?.message}"
            }
        }
    }

    private val dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Applicants")

    fun saveApplicant(
        applFirstName: String,
        applLastName: String,
        applGender: String,
        applEmail: String,
        applLiveIn: String,
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
        applResume: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        generateNextApplicantId { nextApplicantId ->
            val applicant = ApplicantModel(
                nextApplicantId,
                applFirstName,
                applLastName,
                applGender,
                applEmail,
                applLiveIn,
                applAreaCode,
                applPhoneNumber,
                applNationality,
                applMinimumMonthlySalary,
                applEducationLevel,
                applInstitute,
                applFieldOfStudies,
                applLocation,
                applYearOfGraduation,
                applMonthOfGraduation,
                applJobTitle,
                applCompanyName,
                applStartDate,
                applEndDate,
                applCompanyIndustry,
                applResume
            )

            dbRef.child(nextApplicantId).setValue(applicant)
                .addOnCompleteListener {
                    onSuccess()
                }.addOnFailureListener { err ->
                    onError("Error inserting the data")
                }
        }
    }

    // Function to generate the next job ID
    private fun generateNextApplicantId(callback: (String) -> Unit) {
        // Query the database to find the current maximum applicant ID
        val query = dbRef.orderByKey().limitToLast(1)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (childSnapshot in dataSnapshot.children) {
                        val lastApplicantId = childSnapshot.key
                        if (lastApplicantId != null) {
                            // Extract the numeric part of the ID and increment it
                            val numericPart = lastApplicantId.substring(1).toInt()
                            val nextNumericPart = numericPart + 1
                            val nextApplicantId = "A$nextNumericPart"

                            // Now you have the next available custom ID
                            // Call the callback function with the ID
                            callback(nextApplicantId)
                        }
                    }
                } else {
                    // If no employees exist, start with A1
                    callback("A1")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors if any
                // You can provide a default ID or an error message here
                callback("A1")
            }
        })
    }
}
