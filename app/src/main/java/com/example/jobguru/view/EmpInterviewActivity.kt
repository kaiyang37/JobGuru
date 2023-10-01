package com.example.jobguru.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
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

class EmpInterviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmpInterviewsBinding
    private lateinit var viewModel: EmpInterviewViewModel
    private lateinit var iAdapter: EmpInterviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmpInterviewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bottomNavigationBar()
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_emp_interviews)
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
                    bundle.putString("applName", interviewItem.applName)
                    bundle.putString("jobTitle", interviewItem.jobTitle)
                    bundle.putString("intvwrName", interviewItem.intvwrName)
                    bundle.putString("intvwDate", interviewItem.intvwDate)
                    bundle.putString("intvwTime", interviewItem.intvwTime)
                    bundle.putString("intvwPlatform", interviewItem.intvwPlatform)
                    bundle.putString("intvwReason", interviewItem.intvwReason)
                    bundle.putString("intvwStatus", interviewItem.intvwStatus)

                    interviewDetailsFragment.arguments = bundle

                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragmentContainer, interviewDetailsFragment)
                    transaction.addToBackStack(null) // Optional: Add the transaction to the back stack
                    transaction.commit()
                } else {
                    Toast.makeText(
                        this@EmpInterviewActivity,
                        "Interview not found or list is empty.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }


        })

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