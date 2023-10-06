package com.example.jobguru.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.jobguru.R
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
            } else{
                binding.textInputLayoutEmail.error = "This email does not belong to an applicant"
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}