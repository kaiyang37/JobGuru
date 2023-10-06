package com.example.jobguru.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jobguru.R
import com.example.jobguru.databinding.ActivityApplSavedJobsBinding
import com.example.jobguru.viewmodel.ApplSavedJobsAdapter
import com.example.jobguru.viewmodel.ApplSavedJobsViewModel

class ApplSavedJobsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityApplSavedJobsBinding
    private lateinit var viewModel: ApplSavedJobsViewModel
    private lateinit var jAdapter: ApplSavedJobsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val applId = sharedPreferences.getString("applId", "")

        binding = ActivityApplSavedJobsBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(ApplSavedJobsViewModel::class.java)
        binding.rvJob.layoutManager = LinearLayoutManager(this)
        binding.rvJob.setHasFixedSize(true)
        jAdapter = ApplSavedJobsAdapter(ArrayList())
        binding.rvJob.adapter = jAdapter

        viewModel.saveJobList.observe(this) { jobList ->
            jAdapter.setData(jobList)
            if (jobList.isEmpty()) {
                binding.emptySavedJobsScrollView.visibility = View.VISIBLE
                binding.savedJobsScrollView.visibility = View.GONE
                val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val loginEmail = sharedPreferences.getString("loginEmail", "")

                if (loginEmail.isNullOrEmpty()) {
                    binding.actionButton.visibility = View.VISIBLE
                }

                binding.loginBtn.setOnClickListener {
                    val intent = Intent(this, ApplLoginActivity::class.java)
                    startActivity(intent)
                }

                binding.signUpBtn.setOnClickListener {
                    val intent = Intent(this, ApplSignUpActivity::class.java)
                    startActivity(intent)
                }
                binding.rvJob.visibility = View.GONE
            } else {
                binding.emptySavedJobsScrollView.visibility = View.GONE
                binding.savedJobsScrollView.visibility = View.VISIBLE
                binding.rvJob.visibility = View.VISIBLE
            }
        }

        viewModel.filteredJobList.observe(this) { jobList ->
            jAdapter.setData(jobList)
            if (jobList.isEmpty()) {
                binding.emptySavedJobsScrollView.visibility = View.VISIBLE
                binding.savedJobsScrollView.visibility = View.GONE
                val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val loginEmail = sharedPreferences.getString("loginEmail", "")

                if (loginEmail.isNullOrEmpty()) {
                    binding.actionButton.visibility = View.VISIBLE
                }

                binding.loginBtn.setOnClickListener {
                    val intent = Intent(this, ApplLoginActivity::class.java)
                    startActivity(intent)
                }

                binding.signUpBtn.setOnClickListener {
                    val intent = Intent(this, ApplSignUpActivity::class.java)
                    startActivity(intent)
                }
                binding.rvJob.visibility = View.GONE
            } else {
                binding.emptySavedJobsScrollView.visibility = View.GONE
                binding.savedJobsScrollView.visibility = View.VISIBLE
                binding.rvJob.visibility = View.VISIBLE
            }
        }
        if (applId.isNullOrEmpty()) {
            binding.emptySavedJobsScrollView.visibility = View.VISIBLE
            binding.savedJobsScrollView.visibility = View.GONE
            val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val loginEmail = sharedPreferences.getString("loginEmail", "")

            if (loginEmail.isNullOrEmpty()) {
                binding.actionButton.visibility = View.VISIBLE
            }

            binding.loginBtn.setOnClickListener {
                val intent = Intent(this, ApplLoginActivity::class.java)
                startActivity(intent)
            }

            binding.signUpBtn.setOnClickListener {
                val intent = Intent(this, ApplSignUpActivity::class.java)
                startActivity(intent)
            }
        }
        jAdapter.setOnItemClickListener(object : ApplSavedJobsAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                val saveJobListToUse =
                    if (viewModel.saveJobList.value != null && position < viewModel.saveJobList.value!!.size) {
                        viewModel.saveJobList.value!!
                    } else {
                        viewModel.saveJobList.value ?: emptyList()
                    }

                if (position < saveJobListToUse.size) {
                    val intent =
                        Intent(
                            this@ApplSavedJobsActivity,
                            ApplJobDetailsActivity::class.java
                        )
                    val saveJobItem = saveJobListToUse[position]

                    intent.apply {
                        putExtra("jobId", saveJobItem.jobId)
                        putExtra("jobTitle", saveJobItem.jobTitle)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this@ApplSavedJobsActivity,
                        "Selected job details is not found.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
        setContentView(binding.root)
        bottomNavigationBar()
    }

    private fun bottomNavigationBar() {
        binding.bottomNavigationView.setSelectedItemId(R.id.saved_jobs)
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val intent = Intent(applicationContext, ApplHomeActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.search -> {
                    val intent = Intent(applicationContext, ApplSearchActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.saved_jobs -> {
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