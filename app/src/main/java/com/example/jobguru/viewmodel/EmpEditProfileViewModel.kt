package com.example.jobguru.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EmpEditProfileViewModel : ViewModel() {
    val genders = arrayOf("Male", "Female")
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

    var selectedGender: String = ""
    var selectedState: String = ""


    private val _empNameError = MutableLiveData<String>()
    val empNameError: LiveData<String> = _empNameError

    private val _empIndustryError = MutableLiveData<String>()
    val empIndustryError: LiveData<String> = _empIndustryError

    private val _empAddressError = MutableLiveData<String>()
    val empAddressError: LiveData<String> = _empAddressError

    private val _empPostcodeError = MutableLiveData<String>()
    val empPostcodeError: LiveData<String> = _empPostcodeError

    private val _personInChargeNameError = MutableLiveData<String>()
    val personInChargeNameError: LiveData<String> = _personInChargeNameError

    private val _personInChargeContactError = MutableLiveData<String>()
    val personInChargeContactError: LiveData<String> = _personInChargeContactError

    private val _personInChargeDesignationError = MutableLiveData<String>()
    val personInChargeDesignationError: LiveData<String> = _personInChargeDesignationError

    fun validateData(
        empName: String,
        empIndustry: String,
        empAddress: String,
        empPostcode: String,
        personInChargeName: String,
        personInChargeContact: String,
        personInChargeDesignation: String
    ): Boolean {

        // Clear previous error messages
        _empNameError.value = null
        _empIndustryError.value = null
        _empAddressError.value = null
        _empPostcodeError.value = null
        _personInChargeNameError.value = null
        _personInChargeContactError.value = null
        _personInChargeDesignationError.value = null

        var isValid = true

        if (empName.isBlank()) {
            _empNameError.value = "Company name is required"
            isValid = false
        }

        if (empIndustry.isBlank()) {
            _empIndustryError.value = "Company industry involve is required"
            isValid = false
        }

        if (empAddress.isBlank()) {
            _empAddressError.value = "Company address is required"
            isValid = false
        }
        if (empPostcode.isBlank()) {
            _empPostcodeError.value = "Company postcode is required"
            isValid = false
        }
        if (personInChargeName.isBlank()) {
            _personInChargeNameError.value = "Person in charge name is required"
            isValid = false
        }
        if (personInChargeContact.isBlank()) {
            _personInChargeContactError.value = "Person in charge contact number is required"
            isValid = false
        }
        if (personInChargeDesignation.isBlank()) {
            _personInChargeDesignationError.value = "Person in charge designation is required"
            isValid = false
        }

        return isValid
    }

    fun updateProfileData(
        empEmail: String,
        empName: String,
        empIndustry: String,
        empAddress: String,
        empPostcode: String,
        personInChargeName: String,
        personInChargeContact: String,
        personInChargeDesignation: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Employers")

        // Query for the specific employer using empEmail
        val query = dbRef.orderByChild("personInChargeEmail").equalTo(empEmail)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // This assumes there is only one match, but you should handle multiple matches if necessary
                    val empId = dataSnapshot.children.first().key

                    val updatedFields = mutableMapOf<String, Any?>()
                    updatedFields["empName"] = empName
                    updatedFields["empIndustry"] = empIndustry
                    updatedFields["empAddress"] = empAddress
                    updatedFields["empPostcode"] = empPostcode
                    updatedFields["empState"] = selectedState
                    updatedFields["personInChargeName"] = personInChargeName
                    updatedFields["personInChargeContact"] = personInChargeContact
                    updatedFields["personInChargeDesignation"] = personInChargeDesignation
                    updatedFields["personInChargeGender"] = selectedGender

                    // Update the specific employer data
                    dbRef.child(empId?:"").updateChildren(updatedFields)
                    onSuccess()
                } else {
                    onError("Employer with email $empEmail not found.")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onError(databaseError.message)
            }
        })
    }
}