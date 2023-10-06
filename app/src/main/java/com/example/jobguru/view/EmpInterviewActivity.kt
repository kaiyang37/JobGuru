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
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jobguru.view.EmpApplicantsActivity
import com.example.jobguru.viewmodel.EmpInterviewAdapter
import com.example.jobguru.view.EmpInterviewDetailsFragment
import com.example.jobguru.viewmodel.EmpInterviewViewModel
import com.example.jobguru.view.EmpProfileActivity
import com.example.jobguru.R
import com.example.jobguru.databinding.ActivityEmpApplicantsBinding
import com.example.jobguru.databinding.ActivityEmpInterviewsBinding
import com.example.jobguru.viewmodel.EmpInterviewViewModelFactory

class EmpInterviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmpInterviewsBinding
    private lateinit var viewModel: EmpInterviewViewModel
    private lateinit var iAdapter: EmpInterviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmpInterviewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bottomNavigationBar()

        val sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val delimitedJobIds = sharedPreferences.getString("myJobIdList", "") ?: ""
        viewModel =
            ViewModelProvider(this, EmpInterviewViewModelFactory(delimitedJobIds, this)).get(
                EmpInterviewViewModel::class.java
            )

        binding.searchInterviewsField.setOnClickListener {
            binding.searchInterviewsField.isFocusableInTouchMode = true
            binding.searchInterviewsField.requestFocus()

            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(
                binding.searchInterviewsField,
                InputMethodManager.SHOW_IMPLICIT
            )
        }

        binding.searchInterviewsField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchInterviews(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.rvInterview.layoutManager = LinearLayoutManager(this)
        binding.rvInterview.setHasFixedSize(true)
        iAdapter = EmpInterviewAdapter(ArrayList())
        binding.rvInterview.adapter = iAdapter

        viewModel = ViewModelProvider(this).get(EmpInterviewViewModel::class.java)

        viewModel.searchedInterviewList.observe(this, { interviewList ->
            iAdapter.setData(interviewList)
            binding.rvInterview.visibility =
                if (interviewList.isEmpty()) View.GONE else View.VISIBLE
        })

        viewModel.interviewList.observe(this, { interviewList ->
            iAdapter.setData(interviewList)
            binding.rvInterview.visibility =
                if (interviewList.isEmpty()) View.GONE else View.VISIBLE
        })

        iAdapter.setOnItemClickListener(object : EmpInterviewAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                if (viewModel.isNetworkAvailable()) {
                    val interviewListToUse =
                        if (viewModel.searchedInterviewList.value != null && position < viewModel.searchedInterviewList.value!!.size) {
                            viewModel.searchedInterviewList.value!!
                        } else {
                            viewModel.interviewList.value ?: emptyList()
                        }

                    if (position < interviewListToUse.size) {

                        val interviewDetailsFragment = EmpInterviewDetailsFragment()
                        val interviewItem = interviewListToUse[position]

                        val bundle = Bundle()
                        bundle.putString("intvwId", interviewItem.intvwId)
                        bundle.putString("jobId", interviewItem.jobId)
                        bundle.putString("applId", interviewItem.applId)
                        bundle.putString("applName", interviewItem.applName)
                        bundle.putString("empName", interviewItem.empName)
                        bundle.putString("jobTitle", interviewItem.jobTitle)

                        interviewDetailsFragment.arguments = bundle

                        val transaction = supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.fragmentContainer, interviewDetailsFragment)
                        transaction.addToBackStack(null)
                        transaction.commit()
                    } else {
                        Toast.makeText(
                            this@EmpInterviewActivity,
                            "Selected interview details is not found.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@EmpInterviewActivity,
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
        binding.bottomNavigationView.setSelectedItemId(R.id.interviews)
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.jobs -> {
                    val intent = Intent(applicationContext, EmpJobsActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.applicants -> {
                    val intent = Intent(applicationContext, EmpApplicantsActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.interviews -> {
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