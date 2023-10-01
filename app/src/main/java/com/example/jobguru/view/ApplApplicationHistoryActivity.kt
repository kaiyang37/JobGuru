package com.example.jobguru.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jobguru.databinding.ActivityApplApplicationHistoryBinding
import com.example.jobguru.databinding.FragmentApplHomeBinding
import com.example.jobguru.viewmodel.ApplApplicationHistoryAdapter
import com.example.jobguru.viewmodel.ApplApplicationHistoryViewModel
import com.example.jobguru.viewmodel.ApplFilterViewModel

class ApplApplicationHistoryActivity : AppCompatActivity() {

    private lateinit var viewModel: ApplApplicationHistoryViewModel
    private lateinit var jAdapter: ApplApplicationHistoryAdapter
    private lateinit var binding: ActivityApplApplicationHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApplApplicationHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(ApplApplicationHistoryViewModel::class.java)

        binding.upBtn.setOnClickListener{
            finish()
        }

        binding.rvJob.layoutManager = LinearLayoutManager(this)
        binding.rvJob.setHasFixedSize(true)
        jAdapter = ApplApplicationHistoryAdapter(ArrayList())
        binding.rvJob.adapter = jAdapter

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val applId = sharedPreferences.getString("applId", "")
        if (applId != null) {
            viewModel.getApplyData(applId)
        }

        viewModel.searchJobList.observe(this) { jobList ->
            jAdapter.setData(jobList)
            Log.d("MyTag", "Job List Size: ${jobList.size}")
            Log.d("MyTag", "Job List Size: $jobList")
            binding.rvJob.visibility = if (jobList.isEmpty()) View.GONE else View.VISIBLE
        }

        jAdapter.setOnItemClickListener(object : ApplApplicationHistoryAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                val jobListToUse =
                    if (viewModel.searchJobList.value != null && position < viewModel.searchJobList.value!!.size) {
                        viewModel.searchJobList.value!!
                    } else {
                        viewModel.jobList.value ?: emptyList()
                    }

                if (position < jobListToUse.size) {
                    val intent = Intent(this@ApplApplicationHistoryActivity, ApplJobDetailsActivity::class.java)
                    val jobItem = jobListToUse[position]

                    intent.putExtra("jobId", jobItem.jobId)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this@ApplApplicationHistoryActivity,
                        "Selected job details is not found.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        })

    }
}