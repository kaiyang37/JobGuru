package com.example.jobguru.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jobguru.databinding.FragmentApplHomeBinding
import com.example.jobguru.viewmodel.ApplFilterViewModel

class ApplHomeFragment : Fragment() {

    private lateinit var binding: FragmentApplHomeBinding
    private lateinit var viewModel: ApplFilterViewModel
    private lateinit var jAdapter: ApplJobAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentApplHomeBinding.inflate(inflater, container, false)

        val sharedPreferences =
            requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val loginEmail = sharedPreferences.getString("loginEmail", "")
        if (loginEmail.isNullOrEmpty()) {
            binding.homeLogout.visibility = View.VISIBLE
            binding.homeLogin.visibility = View.GONE

            binding.loginBtn.setOnClickListener {
                val intent = Intent(requireActivity(), ApplLoginActivity::class.java)
                startActivity(intent)
            }

            binding.signUpBtn.setOnClickListener {
                val intent = Intent(requireActivity(), ApplSignUpActivity::class.java)
                startActivity(intent)
            }
        } else {
            binding.homeLogin.visibility = View.VISIBLE
            binding.homeLogout.visibility = View.GONE

            viewModel = ViewModelProvider(this).get(ApplFilterViewModel::class.java)

            binding.rvJob.layoutManager = LinearLayoutManager(requireContext())
            binding.rvJob.setHasFixedSize(true)
            jAdapter = ApplJobAdapter(ArrayList())
            binding.rvJob.adapter = jAdapter

            viewModel.jobList.observeForever {
                if (it != null) {
                    // Observe changes in jobList
                    val selectedLocation = sharedPreferences.getString("selectedLocation", "")
                    val selectedSpec = sharedPreferences.getString("selectedSpec", "")
                    val selectedSalary = sharedPreferences.getString("selectedSalary", "")

                    if (!selectedLocation.isNullOrEmpty() || !selectedSpec.isNullOrEmpty() || !selectedSalary.isNullOrEmpty()) {
                        viewModel.filterJobsBasedOnPreferences(selectedLocation, selectedSpec, selectedSalary)
                    }
                }
            }

            viewModel.filteredJobList.observe(requireActivity()) { jobList ->
                jAdapter.setData(jobList)
                binding.rvJob.visibility = if (jobList.isEmpty()) View.GONE else View.VISIBLE
            }

            jAdapter.setOnItemClickListener(object : ApplJobAdapter.onItemClickListener {
                override fun onItemClick(position: Int) {
                    val jobListToUse =
                        if (viewModel.filteredJobList.value != null && position < viewModel.filteredJobList.value!!.size) {
                            viewModel.filteredJobList.value!!
                        } else {
                            viewModel.jobList.value ?: emptyList()
                        }

                    if (position < jobListToUse.size) {
                        val intent = Intent(requireContext(), ApplJobDetailsActivity::class.java)
                        val jobItem = jobListToUse[position]

                        intent.apply {
                            putExtra("jobId", jobItem.jobId)
                        }
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Selected job details is not found.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }


            })


//
//            viewModel = ViewModelProvider(this).get(ApplSearchViewModel::class.java)
//

//
//            viewModel.filterJobs(selectedLocation?:"", selectedSpec?:"", selectedSalary?:"")
//
//            viewModel.searchedJobList.observe(requireActivity(), { jobList ->
//                jAdapter.setData(jobList)
//                Log.d("MyTag", "Job List Size: ${jobList.size}")
//                binding.rvJob.visibility = if (jobList.isEmpty()) View.GONE else View.VISIBLE
//            })

//            viewModel.searchedJobList.observe(requireActivity(), { jobList ->
//                jAdapter.setData(jobList)
//                Log.d("MyTag", "Job List Size: ${jobList.size}")
//                binding.rvJob.visibility = if (jobList.isEmpty()) View.GONE else View.VISIBLE
//            })


//            viewModel.searchJobs("J")


//            if (!selectedLocation.isNullOrEmpty() || !selectedSpec.isNullOrEmpty() || !selectedSalary.isNullOrEmpty()) {
//                val query = "$selectedLocation".trim()
//
//            }


            // Observe the jobList LiveData and update the RecyclerView when it changes
//            viewModel.jobList.observe(viewLifecycleOwner, { jobList ->
//                jAdapter.setData(jobList)
//                binding.rvJob.visibility = if (jobList.isEmpty()) View.GONE else View.VISIBLE
//            })

//            jAdapter.setOnItemClickListener(object : ApplSearchAdapter.onItemClickListener {
//                override fun onItemClick(position: Int) {
//                    val jobListToUse =
//                        if (viewModel.searchedJobList.value != null && position < viewModel.searchedJobList.value!!.size) {
//                            viewModel.searchedJobList.value!!
//                        } else {
//                            viewModel.jobList.value ?: emptyList()
//                        }
//
//                    if (position < jobListToUse.size) {
//                        val intent = Intent(, EmpJobDetailsActivity::class.java)
//                        val jobItem = jobListToUse[position]
//
//                        intent.apply {
//                            putExtra("jobId", jobItem.jobId)
//                            putExtra("jobTitle", jobItem.jobTitle)
//                            putExtra("jobRole", jobItem.jobRole)
//                            putExtra("jobSpecialization", jobItem.jobSpecialization)
//                            putExtra("jobYearOfExp", jobItem.jobYearOfExp)
//                            putExtra("jobWorkState", jobItem.jobWorkState)
//                            putExtra("jobMinSalary", jobItem.jobMinSalary.toString())
//                            putExtra("jobMaxSalary", jobItem.jobMaxSalary.toString())
//                            putExtra("jobDesc", jobItem.jobDesc)
//                        }
//                        startActivity(intent)
//                    } else {
//                        Toast.makeText(
//                            requireContext(),
//                            "Job not found or list is empty.",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//
//
//            })
        }

        return binding.root
    }

}