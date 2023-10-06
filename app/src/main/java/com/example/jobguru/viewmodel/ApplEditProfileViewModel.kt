package com.example.jobguru.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ApplEditProfileViewModel : ViewModel() {
    val areaCodes = arrayOf(
        "Malaysia +60",
        "Singapore +65",
        "Indonesia +62",
        "Thailand +66",
        "Philippines +63",
        "Vietnam +84",
        "Myanmar +95"
    )
    val gender = arrayOf(
        "Male",
        "Female"
    )

    // Properties to hold selected values
    var selectedAreaCode: String = ""
    var selectedGender: String = ""

    // LiveData for error messages
    private val _firstNameError = MutableLiveData<String>()
    val firstNameError: LiveData<String> = _firstNameError

    private val _lastNameError = MutableLiveData<String>()
    val lastNameError: LiveData<String> = _lastNameError

    private val _phoneNumError = MutableLiveData<String>()
    val phoneNumError: LiveData<String> = _phoneNumError

    fun validateData(
        firstName: String,
        lastName: String,
        phoneNum: String
    ): Boolean {

        // Clear previous error messages
        _firstNameError.value = null
        _lastNameError.value = null
        _phoneNumError.value = null

        var isValid = true

        if (firstName.isBlank()) {
            _firstNameError.value = "First name is required"
            isValid = false
        }

        if (lastName.isBlank()) {
            _lastNameError.value = "Last name is required"
            isValid = false
        }

        val phonePattern = "^[+]?[0-9]{9,13}$"
        val regex = Regex(phonePattern)

        if (!regex.matches(phoneNum)) {
            _phoneNumError.value = "Invalid phone number"
            isValid = false
        }

        return isValid
    }

    fun updateProfileData(
        applId: String,
        firstName: String,
        lastName: String,
        phoneNum: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Applicants").child(applId)
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val updatedFields = mutableMapOf<String, Any?>()

                    updatedFields["applFirstName"] = firstName
                    updatedFields["applLastName"] = lastName
                    updatedFields["applPhoneNumber"] = phoneNum
                    updatedFields["applAreaCode"] = selectedAreaCode
                    updatedFields["applGender"] = selectedGender

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
}