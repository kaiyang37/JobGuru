package com.example.jobguru.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.jobguru.R
import com.example.jobguru.databinding.ActivityApplSignUpBinding
import com.example.jobguru.viewmodel.ApplSignUpViewModel

class ApplSignUpActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: ActivityApplSignUpBinding
    private lateinit var viewModel: ApplSignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApplSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.upBtn.setOnClickListener {
            this.onBackPressed()
        }

        binding.genderSpinner.onItemSelectedListener = this
        ArrayAdapter.createFromResource(
            this, R.array.gender_spinner, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.genderSpinner.adapter = adapter
        }

        viewModel = ViewModelProvider(this).get(ApplSignUpViewModel::class.java)

        binding.signUpBtn.setOnClickListener {
            val firstName = binding.firstNameTextBox.text.toString()
            val lastName = binding.lastNameTextBox.text.toString()
            val email = binding.emailTextBox.text.toString()
            val password = binding.passwordTextBox.text.toString()

            if (viewModel.validateData(firstName, lastName, email, password)) {
                viewModel.applSignUp(email, password)
            }
        }

        // Observe error messages and update UI accordingly
        viewModel.firstNameError.observe(this) { errorMessage ->
            binding.textInputLayoutFirstName.error = errorMessage
        }

        viewModel.lastNameError.observe(this) { errorMessage ->
            binding.textInputLayoutLastName.error = errorMessage
        }

        viewModel.emailError.observe(this) { errorMessage ->
            binding.textInputLayoutEmail.error = errorMessage
        }

        viewModel.passwordError.observe(this) { errorMessage ->
            binding.textInputLayoutPassword.error = errorMessage
        }

        // Observe registration success
        viewModel.registrationSuccess.observe(this) { isSuccess ->
            if (isSuccess) {
                val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("loginEmail", binding.emailTextBox.text.toString())
                editor.apply()
                saveApplicantData()
            }
        }

        // Observe registration error
        viewModel.dbErrorText.observe(this) { errorMessage ->
            if (errorMessage.contains("An error occurred:")) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            } else {
                // Handle the error message, show it to the user, update UI, etc.
                binding.textInputLayoutEmail.error = errorMessage
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedValue = parent?.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    private fun saveApplicantData() {
        val firstName = binding.firstNameTextBox.text.toString()
        val lastName = binding.lastNameTextBox.text.toString()
        val gender = binding.genderSpinner.selectedItem.toString()
        val email = binding.emailTextBox.text.toString()

        viewModel.saveApplicant(
            firstName,
            lastName,
            gender,
            email,
            "",
            "",
            "",
            "",
            0.0,
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            onSuccess = {
                replaceFragment(ApplLocationFragment())
            }, onError = { errorMessage ->
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}
