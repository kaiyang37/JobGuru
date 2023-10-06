package com.example.jobguru.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jobguru.R
import com.example.jobguru.databinding.ActivityEmpApplicantsBinding
import com.example.jobguru.view.EmpInterviewActivity
import com.example.jobguru.view.EmpJobsActivity
import com.example.jobguru.viewmodel.EmpApplicantAdapter
import com.example.jobguru.viewmodel.EmpApplicantsViewModel
import com.example.jobguru.viewmodel.EmpApplicantsViewModelFactory
import com.example.jobguru.viewmodel.EmpJobsViewModelFactory

class EmpApplicantsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmpApplicantsBinding
    private lateinit var viewModel: EmpApplicantsViewModel
    private lateinit var aAdapter: EmpApplicantAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmpApplicantsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bottomNavigationBar()

        val sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val delimitedJobIds = sharedPreferences.getString("myJobIdList", "") ?: ""
        viewModel = ViewModelProvider(this, EmpApplicantsViewModelFactory(delimitedJobIds, this)).get(
            EmpApplicantsViewModel::class.java
        )

        binding.searchApplicantsField.setOnClickListener { // Make the EditText focusable when clicked
            binding.searchApplicantsField.isFocusableInTouchMode = true
            binding.searchApplicantsField.requestFocus()

            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(
                binding.searchApplicantsField,
                InputMethodManager.SHOW_IMPLICIT
            )
        }

        binding.searchApplicantsField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchApplicants(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.rvApplicants.layoutManager = LinearLayoutManager(this)
        binding.rvApplicants.setHasFixedSize(true)
        aAdapter = EmpApplicantAdapter(ArrayList())
        binding.rvApplicants.adapter = aAdapter

        viewModel.searchedApplList.observe(this, { applList ->
            aAdapter.setData(applList)
            binding.rvApplicants.visibility = if (applList.isEmpty()) View.GONE else View.VISIBLE
        })

        viewModel.applList.observe(this, { applList ->
            aAdapter.setData(applList)
            binding.rvApplicants.visibility = if (applList.isEmpty()) View.GONE else View.VISIBLE
        })

        aAdapter.setOnItemClickListener(object : EmpApplicantAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                if (viewModel.isNetworkAvailable()) {
                    val applListToUse =
                        if (viewModel.searchedApplList.value != null && position < viewModel.searchedApplList.value!!.size) {
                            viewModel.searchedApplList.value!!
                        } else {
                            viewModel.applList.value ?: emptyList()
                        }

                    if (position < applListToUse.size) {
                        val applicantDetailsFragment = EmpApplicantDetailsFragment()
                        val applItem = applListToUse[position]

                        val bundle = Bundle()
                        bundle.putString("applId", applItem.applId)
                        bundle.putString("jobId", applItem.jobId)
                        bundle.putString("appId", applItem.appId)
                        bundle.putString("jobTitle", applItem.jobTitle)
                        bundle.putString("empName", applItem.empName)

                        applicantDetailsFragment.arguments = bundle

                        val transaction = supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.fragmentContainer, applicantDetailsFragment)
                        transaction.addToBackStack(null)
                        transaction.commit()

                    } else {
                        Toast.makeText(
                            this@EmpApplicantsActivity,
                            "Selected applicant details is not found.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@EmpApplicantsActivity,
                        "Unable to perform this action. Please check your network connection and try again.",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
        })
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    private fun bottomNavigationBar() {
        binding.bottomNavigationView.setSelectedItemId(R.id.applicants)
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.jobs -> {
                    val intent = Intent(applicationContext, EmpJobsActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.applicants -> {
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
}