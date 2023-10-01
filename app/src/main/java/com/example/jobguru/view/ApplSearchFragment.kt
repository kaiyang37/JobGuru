package com.example.jobguru.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jobguru.databinding.FragmentApplSearchBinding
import com.example.jobguru.viewmodel.ApplSearchViewModel

class ApplSearchFragment : Fragment() {
    private lateinit var binding: FragmentApplSearchBinding
    private lateinit var viewModel: ApplSearchViewModel
    private lateinit var jAdapter: ApplJobAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentApplSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(ApplSearchViewModel::class.java)

        binding.searchJobsField.setOnClickListener { // Make the EditText focusable when clicked
            binding.searchJobsField.isFocusableInTouchMode = true
            binding.searchJobsField.requestFocus()

            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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

        binding.rvJob.layoutManager = LinearLayoutManager(requireContext())
        binding.rvJob.setHasFixedSize(true)
        jAdapter = ApplJobAdapter(ArrayList())
        binding.rvJob.adapter = jAdapter


        viewModel.searchedJobList.observe(viewLifecycleOwner) { jobList ->
            jAdapter.setData(jobList)
            if (binding.searchJobsField.equals(null)) {
                binding.rvJob.visibility = View.GONE
                binding.specializationsBtn.visibility = View.VISIBLE
            } else {
                binding.rvJob.visibility = View.VISIBLE
                binding.specializationsBtn.visibility = View.GONE
            }
        }

        // Filter by Specializations
        binding.accFinBtn.setOnClickListener{
            viewModel.filterJobsBasedOnSpec("Accounting/Finance")
        }
        binding.adminHRBtn.setOnClickListener{
            viewModel.filterJobsBasedOnSpec("Admin/HR")
        }
        binding.artsMediaBtn.setOnClickListener{
            viewModel.filterJobsBasedOnSpec("Arts/Media")
        }
        binding.bldgConstrBtn.setOnClickListener{
            viewModel.filterJobsBasedOnSpec("Building/Construction")
        }
        binding.compITBtn.setOnClickListener{
            viewModel.filterJobsBasedOnSpec("Computer/IT")
        }
        binding.eduTrnBtn.setOnClickListener{
            viewModel.filterJobsBasedOnSpec("Education/Training")
        }
        binding.engBtn.setOnClickListener{
            viewModel.filterJobsBasedOnSpec("Engineering")
        }
        binding.hltcareBtn.setOnClickListener{
            viewModel.filterJobsBasedOnSpec("Healthcare")
        }
        binding.hotelBtn.setOnClickListener{
            viewModel.filterJobsBasedOnSpec("Hotel/Dormitory")
        }
        binding.mfgBtn.setOnClickListener{
            viewModel.filterJobsBasedOnSpec("Manufacturing")
        }
        binding.restBtn.setOnClickListener{
            viewModel.filterJobsBasedOnSpec("Restaurant")
        }
        binding.salesMktgBtn.setOnClickListener{
            viewModel.filterJobsBasedOnSpec("Sales/Marketing")
        }
        binding.sciBtn.setOnClickListener{
            viewModel.filterJobsBasedOnSpec("Sciences")
        }
        binding.servBtn.setOnClickListener{
            viewModel.filterJobsBasedOnSpec("Services")
        }

        // Observe the jobList LiveData and update the RecyclerView when it changes
//        viewModel.jobList.observe(viewLifecycleOwner, { jobList ->
//            jAdapter.setData(jobList)
//            binding.rvJob.visibility = if (jobList.isEmpty()) View.GONE else View.VISIBLE
//        })

        jAdapter.setOnItemClickListener(object : ApplJobAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                val jobListToUse =
                    if (viewModel.searchedJobList.value != null && position < viewModel.searchedJobList.value!!.size) {
                        viewModel.searchedJobList.value!!
                    } else {
                        viewModel.jobList.value ?: emptyList()
                    }

                if (position < jobListToUse.size) {
                    val intent = Intent(requireContext(), ApplJobDetailsActivity::class.java)
                    val jobItem = jobListToUse[position]

                    intent.apply {
                        putExtra("jobId", jobItem.jobId)
                        putExtra("empId", jobItem.empId)
                        putExtra("jobTitle", jobItem.jobTitle)
                        putExtra("jobRole", jobItem.jobRole)
                        putExtra("jobSpecialization", jobItem.jobSpecialization)
                        putExtra("jobYearOfExp", jobItem.jobYearOfExp)
                        putExtra("jobWorkState", jobItem.jobWorkState)
                        putExtra("jobMinSalary", jobItem.jobMinSalary.toString())
                        putExtra("jobMaxSalary", jobItem.jobMaxSalary.toString())
                        putExtra("jobDesc", jobItem.jobDesc)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Job not found or list is empty.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }


}
