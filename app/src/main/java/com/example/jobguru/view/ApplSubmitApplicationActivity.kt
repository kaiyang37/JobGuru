package com.example.jobguru.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.jobguru.R
import com.example.jobguru.databinding.ActivityApplSubmitApplicationBinding
import com.example.jobguru.viewmodel.ApplSubmitApplicationViewModel

class ApplSubmitApplicationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityApplSubmitApplicationBinding
    private lateinit var viewModel: ApplSubmitApplicationViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApplSubmitApplicationBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(ApplSubmitApplicationViewModel::class.java)
        setContentView(binding.root)

        binding.upBtn.setOnClickListener {
            finish()
        }

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val applId = sharedPreferences.getString("applId", "")
        val jobId = sharedPreferences.getString("applyJobId", "")

        if (applId != null) {
            viewModel.getApplicantData(applId)
        }

        if (jobId != null) {
            viewModel.getJobTitle(jobId)
        }

        viewModel.applGender.observe(this) { applGender ->
            if (applGender.equals("Male")) {
                binding.maleAvatar.visibility = View.VISIBLE
            } else {
                binding.femaleAvatar.visibility = View.VISIBLE
            }
        }

        viewModel.applName.observe(this) { applName ->
            binding.applName.text = applName
        }

        viewModel.applEmail.observe(this) { applEmail ->
            binding.applEmail.text = applEmail
        }

        viewModel.applPhoneNum.observe(this) { applPhoneNum ->
            binding.applPhoneNum.text = applPhoneNum
        }

        viewModel.jobTitle.observe(this) { jobTitle ->
            binding.applyJobTitle.text = jobTitle
        }

        binding.manageProfileBtn.setOnClickListener {
            startActivity(Intent(this, ApplManageProfileActivity::class.java))
        }

        binding.submitApplicationBtn.setOnClickListener {
            val appStatus = "Pending"

            if (applId != null) {
                if (jobId != null) {
                    viewModel.submitApplication(applId, jobId, appStatus, onSuccess = {
//                        Toast.makeText(this, "Application Submitted", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, ApplSentApplicationActivity::class.java)
                        startActivity(intent)
                        finish()
                    }, onError = { errorMessage ->
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                    })
                }
            }

        }

    }

}