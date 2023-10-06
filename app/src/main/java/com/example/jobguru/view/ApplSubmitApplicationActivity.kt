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

        // Obtain data from previous activity
        val jobTitle = intent.getStringExtra("jobTitle").toString()
        val applName = intent.getStringExtra("applName").toString()
        val applEmail = intent.getStringExtra("applEmail").toString()
        val applEducationLevel = intent.getStringExtra("applEducationLevel").toString()
        val applMinimumMonthlySalary = intent.getStringExtra("applMinimumMonthlySalary").toString()
        var applMinMonthlySalary = 0.0
        if (applMinimumMonthlySalary != null && applMinimumMonthlySalary.isNotEmpty() && !applMinimumMonthlySalary.equals(
                "null",
                ignoreCase = true
            )
        ) {
            applMinMonthlySalary = applMinimumMonthlySalary.toDouble()
        }
        val applLiveIn = intent.getStringExtra("applLiveIn").toString()
        val applGender = intent.getStringExtra("applGender").toString()
        val applPhoneNum = intent.getStringExtra("applPhoneNum").toString()
        val jobCompanyName = intent.getStringExtra("jobCompanyName").toString()
        val jobWorkState = intent.getStringExtra("jobWorkState").toString()
        val jobCompanyEmail = intent.getStringExtra("jobCompanyEmail").toString()

        if (applGender.equals("Male")) {
            binding.maleAvatar.visibility = View.VISIBLE
        } else {
            binding.femaleAvatar.visibility = View.VISIBLE
        }

        binding.applName.text = applName

        binding.applEmail.text = applEmail

        binding.applPhoneNum.text = applPhoneNum

        binding.applyJobTitle.text = jobTitle

        binding.manageProfileBtn.setOnClickListener {
            startActivity(Intent(this, ApplManageProfileActivity::class.java))
        }

        binding.submitApplicationBtn.setOnClickListener {
            val appStatus = "Pending"

            if (applId != null) {
                if (jobId != null) {
                    viewModel.submitApplication(
                        applId,
                        jobId,
                        jobTitle,
                        jobCompanyName,
                        jobCompanyEmail,
                        jobWorkState,
                        applName,
                        applEducationLevel,
                        applMinMonthlySalary,
                        applLiveIn,
                        appStatus,
                        onSuccess = {
                            val intent = Intent(this, ApplSentApplicationActivity::class.java)
                            startActivity(intent)
                            finish()
                        },
                        onError = { errorMessage ->
                            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                        })
                }
            }

        }

    }

}