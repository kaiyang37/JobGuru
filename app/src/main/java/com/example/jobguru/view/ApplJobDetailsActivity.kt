package com.example.jobguru.view

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.jobguru.R
import com.example.jobguru.databinding.ActivityApplJobDetailsBinding
import com.example.jobguru.viewmodel.ApplJobDetailsViewModel

class ApplJobDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityApplJobDetailsBinding
    private lateinit var viewModel: ApplJobDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApplJobDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        viewModel = ViewModelProvider(this).get(ApplJobDetailsViewModel::class.java)

        binding.upButton.setOnClickListener {
            finish()
        }

        val jobId = intent.getStringExtra("jobId").toString()
        val applId = sharedPreferences.getString("applId", "")
        val jobTitle = intent.getStringExtra("jobTitle").toString()
        var jTitle: String? = null
        var saveJobId: String? = null
        var empName: String? = null
        var personInChargeEmail: String? = null
        var workState: String? = null
        var minSalary: Double? = null
        var maxSalary: Double? = null

        viewModel.getJobData(jobId)

        if (applId != null) {
            viewModel.getApplicantData(applId)
        }

        viewModel.jobTitle.observe(this) { jobTitle ->
            binding.tvMainJobTitle.text = jobTitle
            binding.tvJobTitle.text = jobTitle
            jTitle = jobTitle
        }

        viewModel.jobRole.observe(this) { jobRole ->
            binding.tvJobRole.text = jobRole
        }

        viewModel.jobSpecialization.observe(this) { jobSpecialization ->
            binding.tvJobSpecialization.text = jobSpecialization
        }

        viewModel.jobYearOfExp.observe(this) { jobYearOfExp ->
            binding.tvJobYearOfExp.text = jobYearOfExp
        }

        viewModel.jobDesc.observe(this) { jobDesc ->
            binding.tvJobDesc.text = jobDesc
        }

        viewModel.jobWorkState.observe(this) { jobWorkState ->
            binding.tvJobWorkState.text = jobWorkState
            workState = jobWorkState
        }

        viewModel.jobMinSalary.observe(this) { jobMinSalary ->
            binding.tvJobMinSalary.text =
                String.format("%.2f", jobMinSalary)

            minSalary = jobMinSalary
        }

        viewModel.jobMaxSalary.observe(this) { jobMaxSalary ->
            binding.tvJobMaxSalary.text =
                String.format("%.2f", jobMaxSalary)

            maxSalary = jobMaxSalary
        }

        viewModel.jobCompanyName.observe(this) { jobCompanyName ->
            binding.tvMainJobCompanyName.text = jobCompanyName
            binding.tvJobCompanyName.text = jobCompanyName
            empName = jobCompanyName
        }

        viewModel.jobCompanyLogo.observe(
            this
        ) { jobCompanyLogo -> binding.tvJobCompanyLogoImage.setImageBitmap(jobCompanyLogo) }

        viewModel.jobCompanyEmail.observe(this) { jobCompanyEmail ->
            binding.tvJobCompanyEmail.text = jobCompanyEmail
            personInChargeEmail = jobCompanyEmail
        }

        if (applId != null) {
            viewModel.checkApplyJobStatus(applId, jobId)
        }

        viewModel.applyJobStatus.observe(this, Observer { isApplied ->
            if (isApplied) {
                binding.applyNowBtn.visibility = View.GONE
                binding.appliedBtn.visibility = View.VISIBLE
            } else {
                binding.applyNowBtn.visibility = View.VISIBLE
                binding.appliedBtn.visibility = View.GONE
            }
        })

        viewModel.saveJobId.observe(this) { Id ->
            saveJobId = Id

        }

        binding.saveBtn.setOnClickListener {
            if (!applId.isNullOrEmpty()) {
                binding.saveBtn.visibility = View.GONE
                binding.savedBtn.visibility = View.VISIBLE

                if (applId != null) {
                    if (jobId != null) {
                        empName?.let { it1 ->
                            personInChargeEmail?.let { it2 ->
                                minSalary?.let { it3 ->
                                    maxSalary?.let { it4 ->
                                        workState?.let { it5 ->
                                            jTitle?.let { it6 ->
                                                viewModel.saveJob(
                                                    jobId,
                                                    it1,
                                                    it2,
                                                    it6,
                                                    it5,
                                                    it3,
                                                    it4,
                                                    applId,
                                                    onSuccess = {
                                                        Log.d("Test", applId)
                                                        //                                                val intent = Intent(this, ApplJobDetailsActivity::class.java)
                                                        //                                                startActivity(intent)
                                                        //                                                finish()
                                                    }
                                                ) { errorMessage ->
                                                    Toast.makeText(
                                                        this,
                                                        errorMessage,
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            } else {
                showLoginSignUpDialog()
            }
        }

        binding.savedBtn.setOnClickListener {
            if (!applId.isNullOrEmpty()) {
                binding.saveBtn.visibility = View.VISIBLE
                binding.savedBtn.visibility = View.GONE
                if (saveJobId == null) {
                    saveJobId = "SJ1"
                }
                saveJobId?.let { it1 ->
                    viewModel.removeRecord(
                        it1,
                        onSuccess = {
                            Toast.makeText(this, "Save Jobs has been removed", Toast.LENGTH_LONG)
                                .show()
                            finish()
                        },
                        onError = { errorMessage ->
                            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                        })
                }
            } else {
                showLoginSignUpDialog()
            }
        }

        binding.applyNowBtn.setOnClickListener {
            if (!applId.isNullOrEmpty()) {
                val editor = sharedPreferences.edit()
                editor.putString("applyJobId", jobId)
                editor.apply()

                if (applId != null) {
                    viewModel.checkApplProfileStatus(applId)
                }

                viewModel.applProfileStatus.observe(this) { isProfileEmpty ->
                    if (isProfileEmpty) {
                        showCompleteProfileDialog()
                    } else {
                        val intent = Intent(this, ApplSubmitApplicationActivity::class.java)
                        intent.putExtra("jobTitle", jobTitle)
                        viewModel.applName.observe(this) { applName ->
                            intent.putExtra("applName", applName)
                        }
                        viewModel.applEmail.observe(this) { applEmail ->
                            intent.putExtra("applEmail", applEmail)
                        }
                        viewModel.applEducationLevel.observe(this) { applEducationLevel ->
                            intent.putExtra("applEducationLevel", applEducationLevel)
                        }
                        viewModel.applMinimumMonthlySalary.observe(this) { applMinimumMonthlySalary ->
                            intent.putExtra("applMinimumMonthlySalary", applMinimumMonthlySalary)
                        }
                        viewModel.applLiveIn.observe(this) { applLiveIn ->
                            intent.putExtra("applLiveIn", applLiveIn)
                        }
                        viewModel.applGender.observe(this) { applGender ->
                            intent.putExtra("applGender", applGender)
                        }
                        viewModel.applPhoneNum.observe(this) { applPhoneNum ->
                            intent.putExtra("applPhoneNum", applPhoneNum)
                        }
                        viewModel.jobCompanyName.observe(this) { jobCompanyName ->
                            intent.putExtra("jobCompanyName", jobCompanyName)
                        }
                        viewModel.jobWorkState.observe(this) { jobWorkState ->
                            intent.putExtra("jobWorkState", jobWorkState)
                        }
                        viewModel.jobCompanyEmail.observe(this) { jobCompanyEmail ->
                            intent.putExtra("jobCompanyEmail", jobCompanyEmail)
                        }

                        startActivity(intent)
                    }
                }
            } else {
                showLoginSignUpDialog()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val applId = sharedPreferences.getString("applId", "")
        val jobId = intent.getStringExtra("jobId").toString()

        if (applId != null) {
            viewModel.checkApplyJobStatus(applId, jobId)
            viewModel.checkSaveJobStatus(applId, jobId)
        }

        viewModel.applyJobStatus.observe(this, Observer { isApplied ->
            if (isApplied) {
                binding.applyNowBtn.visibility = View.GONE
                binding.appliedBtn.visibility = View.VISIBLE
            } else {
                binding.applyNowBtn.visibility = View.VISIBLE
                binding.appliedBtn.visibility = View.GONE
            }
        })

        viewModel.saveJobStatus.observe(this, Observer { isSaved ->
            if (isSaved) {
                binding.saveBtn.visibility = View.GONE
                binding.savedBtn.visibility = View.VISIBLE
            } else {
                binding.saveBtn.visibility = View.VISIBLE
                binding.savedBtn.visibility = View.GONE
            }
        })
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    private fun showCompleteProfileDialog() {
        val completeProfileDialog = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val completeProfileDialogView =
            inflater.inflate(R.layout.appl_complete_profile_dialog, null)
        val completeProfileBtn =
            completeProfileDialogView.findViewById<Button>(R.id.completeProfileBtn)
        val maybeLaterBtn = completeProfileDialogView.findViewById<TextView>(R.id.maybeLaterBtn)

        completeProfileDialog.setView(completeProfileDialogView)

        val alertDialog = completeProfileDialog.create()
        alertDialog.show()

        completeProfileBtn.setOnClickListener {
            alertDialog.dismiss()
            replaceFragment(ApplCompleteCurrentStatusFragment())
        }

        maybeLaterBtn.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun showLoginSignUpDialog() {
        val loginSignUpDialog = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val loginSignUpDialogView = inflater.inflate(R.layout.appl_login_signup_dialog, null)
        val loginBtn = loginSignUpDialogView.findViewById<Button>(R.id.loginBtn)
        val signUpBtn = loginSignUpDialogView.findViewById<Button>(R.id.signUpBtn)
        val maybeLaterBtn = loginSignUpDialogView.findViewById<TextView>(R.id.maybeLaterBtn)

        loginSignUpDialog.setView(loginSignUpDialogView)

        val alertDialog = loginSignUpDialog.create()
        alertDialog.show()

        loginBtn.setOnClickListener {
            val intent = Intent(this, ApplLoginActivity::class.java)
            startActivity(intent)
        }


        signUpBtn.setOnClickListener {
            val intent = Intent(this, ApplSignUpActivity::class.java)
            startActivity(intent)
        }

        maybeLaterBtn.setOnClickListener {
            alertDialog.dismiss()
        }
    }
}

