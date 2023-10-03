package com.example.jobguru.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jobguru.R
import com.example.jobguru.databinding.ActivityApplHomeBinding
import com.example.jobguru.viewmodel.ApplFilterViewModel
import com.example.jobguru.viewmodel.ApplMainViewModel

class ApplHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityApplHomeBinding
    private lateinit var filterViewModel: ApplFilterViewModel
    private lateinit var viewModel: ApplMainViewModel
    private lateinit var jAdapter: ApplJobAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApplHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bottomNavigationBar()

        viewModel = ViewModelProvider(this).get(ApplMainViewModel::class.java)

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val loginEmail = sharedPreferences.getString("loginEmail", "")

        var observedApplId: String = ""

        if (loginEmail.isNullOrEmpty()) {
            binding.homeLogout.visibility = View.VISIBLE
            binding.homeLogin.visibility = View.GONE

            binding.loginBtn.setOnClickListener {
                val intent = Intent(this, ApplLoginActivity::class.java)
                startActivity(intent)
            }

            binding.signUpBtn.setOnClickListener {
                val intent = Intent(this, ApplSignUpActivity::class.java)
                startActivity(intent)
            }
        } else {
            binding.homeLogin.visibility = View.VISIBLE
            binding.homeLogout.visibility = View.GONE

            viewModel.getApplId(loginEmail)
            viewModel.applId.observe(this) { applId ->
                Log.d("MyApp", "Applicant ID: $applId")
                observedApplId = applId
                val editor = sharedPreferences.edit()
                editor.putString("applId", observedApplId)
                editor.apply()
                Log.d("MyApp", "Applicant ID: $observedApplId")
            }

            filterViewModel = ViewModelProvider(this).get(ApplFilterViewModel::class.java)

            binding.rvJob.layoutManager = LinearLayoutManager(this)
            binding.rvJob.setHasFixedSize(true)
            jAdapter = ApplJobAdapter(ArrayList())
            binding.rvJob.adapter = jAdapter

            filterViewModel.jobList.observeForever {
                if (it != null) {
                    // Observe changes in jobList
                    val selectedLocation = sharedPreferences.getString("selectedLocation", "")
                    val selectedSpec = sharedPreferences.getString("selectedSpec", "")
                    val selectedSalary = sharedPreferences.getString("selectedSalary", "")

                    if (!selectedLocation.isNullOrEmpty() || !selectedSpec.isNullOrEmpty() || !selectedSalary.isNullOrEmpty()) {
                        filterViewModel.filterJobsBasedOnPreferences(
                            selectedLocation,
                            selectedSpec,
                            selectedSalary
                        )
                    } else{
                        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.clear()
                        editor.apply()

                        val intent = Intent(this, RoleActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                }
            }

            filterViewModel.filteredJobList.observe(this) { jobList ->
                jAdapter.setData(jobList)
                binding.rvJob.visibility = if (jobList.isEmpty()) View.GONE else View.VISIBLE
            }

            jAdapter.setOnItemClickListener(object : ApplJobAdapter.onItemClickListener {
                override fun onItemClick(position: Int) {
                    val jobListToUse =
                        if (filterViewModel.filteredJobList.value != null && position < filterViewModel.filteredJobList.value!!.size) {
                            filterViewModel.filteredJobList.value!!
                        } else {
                            filterViewModel.jobList.value ?: emptyList()
                        }

                    if (position < jobListToUse.size) {
                        val intent =
                            Intent(this@ApplHomeActivity, ApplJobDetailsActivity::class.java)
                        val jobItem = jobListToUse[position]

                        intent.apply {
                            putExtra("jobId", jobItem.jobId)
                        }
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this@ApplHomeActivity,
                            "Selected job details is not found.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }


            })
        }
    }

    private fun bottomNavigationBar() {
        binding.bottomNavigationView.setSelectedItemId(R.id.home)
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    true
                }

                R.id.search -> {
                    val intent = Intent(applicationContext, ApplSearchActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.saved_jobs -> {
                    val intent = Intent(applicationContext, ApplSavedJobsActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.profile -> {
                    val intent = Intent(applicationContext, ApplProfileActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }

                else -> false
            }
        }
    }

}