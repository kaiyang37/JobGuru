package com.example.jobguru.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.jobguru.databinding.ActivityApplLoginBinding
import com.example.jobguru.viewmodel.ApplLoginViewModel

class ApplLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityApplLoginBinding
    private lateinit var viewModel: ApplLoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApplLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.upBtn.setOnClickListener {
            this.onBackPressed()
        }

        viewModel = ViewModelProvider(this).get(ApplLoginViewModel::class.java)

        binding.loginBtn.setOnClickListener {
            val email = binding.emailTextBox.text.toString()
            val password = binding.passwordTextBox.text.toString()

            if (viewModel.validateData(email, password)) {
                viewModel.applLogin(email, password, this)
            }

        }

        // Observe error messages and update UI accordingly
        viewModel.emailError.observe(this) { errorMessage ->
            binding.textInputLayoutEmail.error = errorMessage
        }

        viewModel.passwordError.observe(this) { errorMessage ->
            binding.textInputLayoutPassword.error = errorMessage
        }

        viewModel.loginSuccess.observe(this) { isSuccess ->
            if (isSuccess) {
                val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("loginEmail", binding.emailTextBox.text.toString())
                editor.apply()
                val intent = Intent(this, ApplMainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}