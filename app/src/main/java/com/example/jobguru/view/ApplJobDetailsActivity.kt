package com.example.jobguru.view

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.jobguru.R
import com.example.jobguru.databinding.ActivityApplJobDetailsBinding
import com.example.jobguru.viewmodel.ApplJobDetailsViewModel
import com.example.jobguru.viewmodel.SaveJobInsertionViewModel

class ApplJobDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityApplJobDetailsBinding
    private lateinit var viewModel: ApplJobDetailsViewModel
    private lateinit var saveJobInsertionViewModel: SaveJobInsertionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApplJobDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        viewModel = ViewModelProvider(this).get(ApplJobDetailsViewModel::class.java)

        binding.upButton.setOnClickListener {
            this.onBackPressed()
        }

        val jobId = intent.getStringExtra("jobId").toString()

        viewModel.getJobData(jobId)

        viewModel.jobTitle.observe(this) { jobTitle ->
            binding.tvMainJobTitle.text = jobTitle
            binding.tvJobTitle.text = jobTitle
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
        }

        viewModel.jobMinSalary.observe(this) { jobMinSalary ->
            binding.tvJobMinSalary.text =
                String.format("%.2f", jobMinSalary)
        }

        viewModel.jobMaxSalary.observe(this) { jobMaxSalary ->
            binding.tvJobMaxSalary.text =
                String.format("%.2f", jobMaxSalary)
        }

        viewModel.jobCompanyName.observe(this) { jobCompanyName ->
            binding.tvMainJobCompanyName.text = jobCompanyName
            binding.tvJobCompanyName.text = jobCompanyName
        }

        val applId = sharedPreferences.getString("applId", "")

        if (applId != null) {
            viewModel.checkApplyJobStatus(applId, jobId)
        }

        viewModel.applyJobStatus.observe(this){isApplied ->
            if (isApplied) {
                binding.applyNowBtn.visibility = View.GONE
                binding.appliedBtn.visibility = View.VISIBLE
            } else {
                binding.applyNowBtn.visibility = View.VISIBLE
                binding.appliedBtn.visibility = View.GONE
            }

        }
//        binding.saveBtn.setOnClickListener {
//            saveJobInsertionViewModel = ViewModelProvider(this).get(SaveJobInsertionViewModel::class.java)
//            saveJobData()
//        }

        binding.applyNowBtn.setOnClickListener {
            if (!applId.isNullOrEmpty()) {
                val editor = sharedPreferences.edit()
                editor.putString("applyJobId", jobId)
                editor.apply()

                viewModel.checkApplProfileStatus(applId)

                viewModel.applProfileStatus.observe(this) { isProfileEmpty ->
                    if (isProfileEmpty) {
                        showCompleteProfileDialog()
                    } else {
                        val intent = Intent(this, ApplSubmitApplicationActivity::class.java)
                        startActivity(intent)
                    }
                }
            } else{
                showLoginSignUpDialog()
            }
        }
    }
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    private fun showCompleteProfileDialog(){
        val completeProfileDialog = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val completeProfileDialogView = inflater.inflate(R.layout.appl_complete_profile_dialog, null)
        val completeProfileBtn = completeProfileDialogView.findViewById<Button>(R.id.completeProfileBtn)
        val maybeLaterBtn = completeProfileDialogView.findViewById<TextView>(R.id.maybeLaterBtn)

        completeProfileDialog.setView(completeProfileDialogView)

        val alertDialog = completeProfileDialog.create()
        alertDialog.show()

        completeProfileBtn.setOnClickListener {
            replaceFragment(ApplCompleteCurrentStatusFragment())
        }

        maybeLaterBtn.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun showLoginSignUpDialog(){
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


//    private fun saveJobData() {
//        val jobId = viewModel.jobId.value ?: ""
//
//        val databaseReference = FirebaseDatabase.getInstance().getReference("Applicants")
//
//        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
//        val loginEmail = sharedPreferences.getString("loginEmail", "")
//
//        val query: Query = databaseReference.orderByChild("applEmail").equalTo(loginEmail)
//
//        query.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    // Iterate through the results (usually, there should be only one result)
//                    for (userSnapshot in dataSnapshot.children) {
//                        val applId = userSnapshot.key // This is the parent ID
//                        if (applId != null) {
//                            saveJobInsertionViewModel.saveJob(jobId, applId,
//                                onSuccess = {
//                                    binding.saveBtn.visibility = View.GONE
//                                    binding.savedBtn.visibility = View.VISIBLE
//                                },
//                                onError = { errorMessage ->
//                                    // Handle error here
//                                }
//                            )
//                        }
//                    }
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                println("Error: ${databaseError.message}")
//            }
//        })
//    }
}

