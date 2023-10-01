package com.example.jobguru.viewmodel

import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jobguru.model.ApplicantModel
import com.example.jobguru.model.EmployerModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ApplLoginViewModel : ViewModel() {

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> = _loginSuccess

    // LiveData for error messages
    private val _emailError = MutableLiveData<String>()
    val emailError: LiveData<String> = _emailError

    private val _passwordError = MutableLiveData<String>()
    val passwordError: LiveData<String> = _passwordError

    fun validateData(
        email: String,
        password: String
    ): Boolean {
        // Clear previous error messages
        _emailError.value = null
        _passwordError.value = null

        var isValid = true

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

    fun applLogin(email: String, password: String, context: Context) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _loginSuccess.value = true
                } else {
                    handleLoginError(task.exception, context)
                }
            }
    }

    private fun handleLoginError(exception: Exception?, context: Context) {
        val errorMessage = exception?.message ?: "An error occurred."

        if (errorMessage.contains("INVALID_LOGIN_CREDENTIALS")) {
            // Show a dialog for the specific internal error
            val alertDialog = AlertDialog.Builder(context)
                .setTitle("Invalid Credentials")
                .setMessage("Please check your email address and password and retry.")
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()

            alertDialog.show()
        } else {
            Toast.makeText(
                context,
                errorMessage,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
