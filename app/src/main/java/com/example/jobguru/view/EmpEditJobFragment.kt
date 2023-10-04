package com.example.jobguru.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.jobguru.R
import com.example.jobguru.databinding.FragmentEmpEditJobBinding
import com.example.jobguru.view.EmpJobDetailsActivity
import com.example.jobguru.view.EmpJobsActivity
import com.example.jobguru.viewmodel.EmpEditJobViewModel

class EmpEditJobFragment : Fragment() {
    private lateinit var binding: FragmentEmpEditJobBinding
    private lateinit var viewModel: EmpEditJobViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmpEditJobBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(EmpEditJobViewModel::class.java)
        roleSpinner()
        specializationSpinner()
        yearOfExpSpinner()
        stateSpinner()

        binding.upButton.setOnClickListener{
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack()
        }

        val jobId = arguments?.getString("jobId").toString()
        binding.jobTitle.setText(arguments?.getString("jobTitle").toString())
        val rolePosition = viewModel.roles.indexOf(arguments?.getString("jobRole"))
        if (rolePosition != -1) {
            binding.roleSpinner.setSelection(rolePosition)
        }
        val specializationPosition =
            viewModel.specializations.indexOf(arguments?.getString("jobSpecialization"))
        if (specializationPosition != -1) {
            binding.specializationSpinner.setSelection(specializationPosition)
        }

        val yearOfExpPosition =
            viewModel.yearsOfExperience.indexOf(arguments?.getString("jobYearOfExp"))
        if (yearOfExpPosition != -1) {
            binding.yearOfExpSpinner.setSelection(yearOfExpPosition)
        }
        binding.jobDesc.setText(arguments?.getString("jobDesc").toString())
        val statePosition = viewModel.states.indexOf(arguments?.getString("jobWorkState"))
        if (statePosition != -1) {
            binding.stateSpinner.setSelection(statePosition)
        }
        binding.minSalary.setText(arguments?.getDouble("jobMinSalary").toString())
        binding.maxSalary.setText(arguments?.getDouble("jobMaxSalary").toString())

        binding.editJobBtn.setOnClickListener {
            val jobTitle = binding.jobTitle.text.toString()
            val jobDesc = binding.jobDesc.text.toString()
            val jobMinSalary = binding.minSalary.text.toString().toDoubleOrNull() ?: 0.0
            val jobMaxSalary = binding.maxSalary.text.toString().toDoubleOrNull() ?: 0.0
            if (viewModel.validateData(
                    jobTitle,
                    jobDesc,
                    jobMinSalary,
                    jobMaxSalary
                )
            ) {
                viewModel.updateJobData(
                    jobId,
                    jobTitle,
                    jobDesc,
                    jobMinSalary,
                    jobMaxSalary,
                    onSuccess = {
                        Toast.makeText(requireContext(), "$jobTitle is updated successfully", Toast.LENGTH_LONG)
                            .show()
                        requireActivity().finish()
                        val intent = Intent(requireContext(), EmpJobDetailsActivity::class.java)
                        intent.apply {
                            putExtra("jobId", jobId)
                        }
                        startActivity(intent)
                    },
                    onError = { errorMessage ->
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                    }

                )
            }
        }

        // Observe error messages and update UI accordingly
        viewModel.jobTitleError.observe(requireActivity()) { errorMessage ->
            binding.jobTitleErrorMessage.text = errorMessage
            binding.jobTitleErrorMessage.visibility = View.VISIBLE
        }

        viewModel.jobDescError.observe(requireActivity()) { errorMessage ->
            binding.jobDescErrorMessage.text = errorMessage
            binding.jobDescErrorMessage.visibility = View.VISIBLE
        }

        viewModel.minSalaryError.observe(requireActivity()) { errorMessage ->
            binding.minSalaryErrorMessage.text = errorMessage
            binding.minSalaryErrorMessage.visibility = View.VISIBLE
        }
        viewModel.maxSalaryError.observe(requireActivity()) { errorMessage ->
            binding.maxSalaryErrorMessage.text = errorMessage
            binding.maxSalaryErrorMessage.visibility = View.VISIBLE
        }

        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        return binding.root
    }

    private fun roleSpinner() {
        // Initialize and populate the role_spinner
        val roleAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, viewModel.roles)
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.roleSpinner.adapter = roleAdapter

        binding.roleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                viewModel.selectedRole = viewModel.roles[position]
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                viewModel.selectedRole = ""
            }
        }
    }

    private fun specializationSpinner() {
        // Initialize and populate the specialization_spinner
        val specializationAdapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                viewModel.specializations
            )
        specializationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.specializationSpinner.adapter = specializationAdapter

        binding.specializationSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>,
                    selectedItemView: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.selectedSpecialization = viewModel.specializations[position]
                }

                override fun onNothingSelected(parentView: AdapterView<*>) {
                    viewModel.selectedSpecialization = ""
                }
            }
    }

    private fun yearOfExpSpinner() {
        // Initialize and populate the yearOfExp_spinner
        val yearOfExpAdapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                viewModel.yearsOfExperience
            )
        yearOfExpAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.yearOfExpSpinner.adapter = yearOfExpAdapter

        binding.yearOfExpSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>,
                    selectedItemView: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.selectedYearOfExp = viewModel.yearsOfExperience[position]
                }

                override fun onNothingSelected(parentView: AdapterView<*>) {
                    viewModel.selectedYearOfExp = ""
                }
            }
    }

    private fun stateSpinner() {

        // Initialize and populate the state_spinner
        val stateAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, viewModel.states)
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.stateSpinner.adapter = stateAdapter

        binding.stateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                viewModel.selectedState = viewModel.states[position]
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                viewModel.selectedState = ""
            }
        }
    }
}