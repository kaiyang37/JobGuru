package com.example.jobguru.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jobguru.view.EmpAddNewJobFragment
import com.example.jobguru.view.EmpApplicantsActivity
import com.example.jobguru.viewmodel.EmpJobAdapter
import com.example.jobguru.EmpJobsViewModel
import com.example.jobguru.view.EmpProfileActivity
import com.example.jobguru.R
import com.example.jobguru.databinding.ActivityEmpJobsBinding
import com.example.jobguru.viewmodel.EmpJobsViewModelFactory

class EmpJobsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmpJobsBinding
    private lateinit var viewModel: EmpJobsViewModel
    private lateinit var jAdapter: EmpJobAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmpJobsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val empEmail = sharedPreferences.getString("personInChargeEmail", "") ?: ""
        viewModel = ViewModelProvider(
            this,
            EmpJobsViewModelFactory(empEmail)
        ).get(EmpJobsViewModel::class.java)

        bottomNavigationBar()

        binding.searchJobsField.setOnClickListener { // Make the EditText focusable when clicked
            binding.searchJobsField.isFocusableInTouchMode = true
            binding.searchJobsField.requestFocus()

            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(
                binding.searchJobsField,
                InputMethodManager.SHOW_IMPLICIT
            )
        }

        binding.searchJobsField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Call the filterJobs function in the ViewModel
                viewModel.searchJobs(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.rvJob.layoutManager = LinearLayoutManager(this)
        binding.rvJob.setHasFixedSize(true)
        jAdapter = EmpJobAdapter(ArrayList())
        binding.rvJob.adapter = jAdapter

        viewModel.searchedJobList.observe(this, { jobList ->
            jAdapter.setData(jobList)
            binding.rvJob.visibility = if (jobList.isEmpty()) View.GONE else View.VISIBLE
        })

        // Observe the jobList LiveData and update the RecyclerView when it changes
        viewModel.jobList.observe(this, { jobList ->
            jAdapter.setData(jobList)
            binding.rvJob.visibility = if (jobList.isEmpty()) View.GONE else View.VISIBLE

            val jobIdList = jobList.map { it.jobId }

            // Convert the jobIdList to a delimited string
            val delimitedJobIds = jobIdList.joinToString(",")

            val sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("myJobIdList", delimitedJobIds)
            editor.apply()
        })

        jAdapter.setOnItemClickListener(object : EmpJobAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                val jobListToUse =
                    if (viewModel.searchedJobList.value != null && position < viewModel.searchedJobList.value!!.size) {
                        viewModel.searchedJobList.value!!
                    } else {
                        viewModel.jobList.value ?: emptyList()
                    }

                if (position < jobListToUse.size) {
                    val intent = Intent(this@EmpJobsActivity, EmpJobDetailsActivity::class.java)
                    val jobItem = jobListToUse[position]

                    intent.apply {
                        putExtra("jobId", jobItem.jobId)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this@EmpJobsActivity,
                        "Selected job details is not found.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }


        })

        binding.addNewJobBtn.setOnClickListener {
            callAddJobForm()
        }


    }

    private fun bottomNavigationBar() {
        binding.bottomNavigationView.setSelectedItemId(R.id.jobs)
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.jobs -> {
                    true
                }

                R.id.applicants -> {
                    val intent = Intent(applicationContext, EmpApplicantsActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.interviews -> {
                    val intent = Intent(applicationContext, EmpInterviewActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.profile -> {
                    val intent = Intent(applicationContext, EmpProfileActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }

                else -> false
            }
        }
    }

    private fun callAddJobForm() {
        val addNewJobFragment = EmpAddNewJobFragment()

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, addNewJobFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}