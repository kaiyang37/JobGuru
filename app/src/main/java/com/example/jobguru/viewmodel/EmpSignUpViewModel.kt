package com.example.jobguru.viewmodel

import android.net.Uri
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jobguru.model.EmployerModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class EmpSignUpViewModel : ViewModel() {
    private val dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Employers")
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

    private val _registrationSuccess = MutableLiveData<Boolean>()
    val registrationSuccess: LiveData<Boolean> = _registrationSuccess

    private val _dbErrorText = MutableLiveData<String>()
    val dbErrorText: LiveData<String> = _dbErrorText

    private val _empId = MutableLiveData<String>()
    val empId: LiveData<String> = _empId

    // LiveData for error messages
    private val _imageError = MutableLiveData<String>()
    val imageError: LiveData<String> = _imageError

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

    private val _personInChargeEmailError = MutableLiveData<String>()
    val personInChargeEmailError: LiveData<String> = _personInChargeEmailError

    private val _personInChargePasswordError = MutableLiveData<String>()
    val  personInChargePasswordError: LiveData<String> = _personInChargePasswordError

    fun validateData(
        ImageUri: Uri?,
        empName: String,
        empIndustry: String,
        empAddress: String,
        empPostcode: String,
        personInChargeName: String,
        personInChargeContact: String,
        personInChargeDesignation: String,
        personInChargeEmail: String,
        personInChargePassword: String
    ): Boolean {

        // Clear previous error messages
        _imageError.value = null
        _empNameError.value = null
        _empIndustryError.value = null
        _empAddressError.value = null
        _empPostcodeError.value = null
        _personInChargeNameError.value = null
        _personInChargeContactError.value = null
        _personInChargeDesignationError.value = null
        _personInChargeEmailError.value = null
        _personInChargePasswordError.value = null


        var isValid = true

        if(ImageUri == null){
            _imageError.value = "Company logo is required"
            isValid = false
        }

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

        if (personInChargeEmail.isBlank()) {
            _personInChargeEmailError.value = "Email address is required"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(personInChargeEmail).matches()) {
            _personInChargeEmailError.value = "Invalid email address"
            isValid = false
        }

        if (personInChargePassword.isBlank()) {
            _personInChargePasswordError.value = "Password is required"
            isValid = false
        } else if (personInChargePassword.length < 8) {
            _personInChargePasswordError.value = "Password must be at least 8 characters long"
            isValid = false
        } else if (!Regex("[A-Z]").containsMatchIn(personInChargePassword)) {
            _personInChargePasswordError.value = "Password must contain at least one uppercase letter"
            isValid = false
        } else if (!Regex("[a-z]").containsMatchIn(personInChargePassword)) {
            _personInChargePasswordError.value = "Password must contain at least one lowercase letter"
            isValid = false
        } else if (!Regex("\\d").containsMatchIn(personInChargePassword)) {
            _personInChargePasswordError.value = "Password must contain at least one digit"
            isValid = false
        } else if (!Regex("[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]+").containsMatchIn(personInChargePassword)) {
            _personInChargePasswordError.value = "Password must contain at least one special character"
            isValid = false
        }

        return isValid
    }


    private val auth = FirebaseAuth.getInstance()

    fun empSignUp(email: String, password: String) {
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

    fun saveEmployerData(
        empName: String,
        empIndustry: String,
        empAddress: String,
        empPostcode: String,
        personInChargeName: String,
        personInChargeContact: String,
        personInChargeDesignation: String,
        personInChargeEmail: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        generateNextEmployerId { nextEmployerId ->
            val emp = EmployerModel(
                nextEmployerId,
                empName,
                empIndustry,
                empAddress,
                empPostcode,
                selectedState,
                personInChargeName,
                personInChargeContact,
                personInChargeDesignation,
                selectedGender,
                personInChargeEmail
            )
            _empId.value = nextEmployerId

            dbRef.child(nextEmployerId).setValue(emp).addOnCompleteListener {
                onSuccess()
            }.addOnFailureListener {
                onError("Error inserting the data")
            }
        }

    }

    fun uploadImage(empEmail: String, imageUri: Uri?) {
        var fileName = empEmail
        val storageReference = FirebaseStorage.getInstance().getReference("employersLogo/$fileName")

        if(imageUri != null){
            storageReference.putFile(imageUri)
        }
    }

    private fun generateNextEmployerId(callback: (String) -> Unit) {
        val query = dbRef.orderByKey().limitToLast(1)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (childSnapshot in dataSnapshot.children) {
                        val lastEmployerId = childSnapshot.key
                        if (lastEmployerId != null) {
                            val numericPart = lastEmployerId.substring(1).toInt()
                            val nextNumericPart = numericPart + 1
                            val nextEmployerId = "E$nextNumericPart"

                            callback(nextEmployerId)
                        }
                    }
                } else {
                    callback("E1")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback("E1")
            }
        })
    }
}