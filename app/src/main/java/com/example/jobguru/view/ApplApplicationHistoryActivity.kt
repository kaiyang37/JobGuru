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
import com.example.jobguru.databinding.ActivityApplApplicationHistoryBinding
import com.example.jobguru.viewmodel.ApplApplicationHistoryAdapter
import com.example.jobguru.viewmodel.ApplApplicationHistoryViewModel
import com.example.jobguru.viewmodel.ApplInterviewViewModel

class ApplApplicationHistoryActivity : AppCompatActivity() {

    private lateinit var viewModel: ApplApplicationHistoryViewModel
    private lateinit var intViewModel: ApplInterviewViewModel
    private lateinit var jAdapter: ApplApplicationHistoryAdapter
    private lateinit var binding: ActivityApplApplicationHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApplApplicationHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(ApplApplicationHistoryViewModel::class.java)
        intViewModel = ViewModelProvider(this).get(ApplInterviewViewModel::class.java)
        binding.upBtn.setOnClickListener{
            val intent = Intent(this, ApplProfileActivity::class.java)
            startActivity(intent)
        }

        binding.rvJob.layoutManager = LinearLayoutManager(this)
        binding.rvJob.setHasFixedSize(true)
        jAdapter = ApplApplicationHistoryAdapter(ArrayList(), intViewModel, this)
        binding.rvJob.adapter = jAdapter

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val applId = sharedPreferences.getString("applId", "")
        if (applId != null) {
            viewModel.getApplyData(applId)
        }

        viewModel.searchApplyList.observe(this) { applyList ->
            jAdapter.setData(applyList)
            Log.d("MyTag", "Apply List Size: ${applyList.size}")
            Log.d("MyTag", "Apply List: $applyList")
            binding.rvJob.visibility = if (applyList.isEmpty()) View.GONE else View.VISIBLE
            binding.emptyAppHistory.visibility = if (applyList.isEmpty()) View.VISIBLE else View.GONE
        }

        jAdapter.setOnItemClickListener(object : ApplApplicationHistoryAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                val applyListToUse =
                    if (viewModel.searchApplyList.value != null && position < viewModel.searchApplyList.value!!.size) {
                        viewModel.searchApplyList.value!!
                    } else {
                        viewModel.jobList.value ?: emptyList()
                    }

                if (position < applyListToUse.size) {
                    val intent = Intent(this@ApplApplicationHistoryActivity, ApplJobDetailsActivity::class.java)
                    val jobItem = applyListToUse[position]

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