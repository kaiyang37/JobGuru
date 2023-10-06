package com.example.jobguru.view

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.jobguru.R
import com.example.jobguru.databinding.ActivityEmpLoginBinding
import com.example.jobguru.viewmodel.EmpLoginViewModel

class EmpLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmpLoginBinding
    private lateinit var viewModel: EmpLoginViewModel
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmpLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.upBtn.setOnClickListener {
            this.onBackPressed()
        }

        viewModel = ViewModelProvider(this).get(EmpLoginViewModel::class.java)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Logging in")

        binding.loginButton.setOnClickListener {
            val email = binding.emailTextBox.text.toString()
            val password = binding.passwordTextBox.text.toString()
            if (viewModel.validateData(email, password)) {
                progressDialog.show()
                viewModel.empLogin(email, password, this)
            }
        }

        // Observe error messages and update UI accordingly
        viewModel.emailError.observe(this) { errorMessage ->
            binding.emailTextBoxErrorMessage.text = errorMessage
            binding.emailTextBoxErrorMessage.visibility = View.VISIBLE
            progressDialog.dismiss()
        }

        viewModel.passwordError.observe(this) { errorMessage ->
            binding.passwordTextBoxErrorMessage.text = errorMessage
            binding.passwordTextBoxErrorMessage.visibility = View.VISIBLE
            progressDialog.dismiss()
        }

        viewModel.loginSuccess.observe(this) { isSuccess ->
            if (isSuccess) {
                val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("personInChargeEmail", binding.emailTextBox.text.toString())
                editor.apply()
                progressDialog.dismiss()
                this.finish()
                val intent = Intent(this, EmpJobsActivity::class.java)
                startActivity(intent)
            }else{
                progressDialog.dismiss()
            }
        }

        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }
}