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
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.jobguru.R
import com.example.jobguru.databinding.FragmentApplEditExperienceBinding
import com.example.jobguru.viewmodel.ApplEditExperienceViewModel
import com.example.jobguru.viewmodel.CombinedSpinnerAdapter

class ApplEditExperienceFragment : Fragment() {
    private lateinit var binding: FragmentApplEditExperienceBinding
    private lateinit var viewModel: ApplEditExperienceViewModel
    private lateinit var monthsArray: Array<String>
    private lateinit var yearsArray: Array<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentApplEditExperienceBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(ApplEditExperienceViewModel::class.java)

        companyIndustrySpinner()

        binding.cancelBtn.setOnClickListener {
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack()
        }

        val applId = arguments?.getString("applId").toString()
        if (arguments?.getString("jobTitle").toString().isBlank()) {
            binding.switchBtn.isChecked = true
            binding.fillInExperience.visibility = View.GONE
        } else {
            binding.switchBtn.isChecked = false
            binding.fillInExperience.visibility = View.VISIBLE
        }
        binding.jobTitleTextBox.setText(arguments?.getString("jobTitle").toString())
        binding.companyNameTextBox.setText(arguments?.getString("companyName").toString())

        monthsArray = resources.getStringArray(R.array.month_spinner)
        yearsArray = resources.getStringArray(R.array.year_spinner)

        val combinedAdapter = CombinedSpinnerAdapter(requireActivity(), yearsArray, monthsArray)
        binding.startDateSpinner.adapter = combinedAdapter
        binding.endDateSpinner.adapter = combinedAdapter

        val applStartDate = arguments?.getString("startDate")
        var startDatePosition = -1
        for (i in 0 until combinedAdapter.count) {
            if (applStartDate == combinedAdapter.getItem(i)) {
                startDatePosition = i
                break  // Stop searching once found
            }
        }
        if (startDatePosition != -1) {
            binding.startDateSpinner.setSelection(startDatePosition)
        }
        val applEndDate = arguments?.getString("endDate")
        var endDatePosition = -1
        for (i in 0 until combinedAdapter.count) {
            if (applEndDate == combinedAdapter.getItem(i)) {
                endDatePosition = i
                break  // Stop searching once found
            }
        }
        if (endDatePosition != -1) {
            binding.endDateSpinner.setSelection(endDatePosition)
        }
        val companyIndustryPosition =
            viewModel.companyIndustry.indexOf(arguments?.getString("companyIndustry"))
        if (companyIndustryPosition != -1) {
            binding.companyIndustrySpinner.setSelection(companyIndustryPosition)
        }

        binding.switchBtn.setOnClickListener {
            if (checkSwitchBtnStatus()) {
                binding.fillInExperience.visibility = View.GONE
                binding.jobTitleTextBox.text.clear()
                binding.companyNameTextBox.text.clear()
                viewModel.selectedCompanyIndustry = ""
                binding.startDateSpinner.setSelection(-1)
                binding.endDateSpinner.setSelection(-1)
                binding.companyIndustrySpinner.setSelection(-1)
            } else {
                binding.fillInExperience.visibility = View.VISIBLE
                binding.startDateSpinner.setSelection(0)
                binding.endDateSpinner.setSelection(0)
                binding.companyIndustrySpinner.setSelection(0)
                binding.textInputLayoutJobTitle.error = null
                binding.textInputLayoutCompanyName.error = null
                binding.textInputLayoutStartDate.error = null
                binding.textInputLayoutEndDate.error = null
            }
        }

        binding.saveBtn.setOnClickListener {
            val jobTitle = binding.jobTitleTextBox.text.toString()
            val companyName = binding.companyNameTextBox.text.toString()
            if (checkSwitchBtnStatus()) {
                viewModel.updateExperienceData(applId,
                    jobTitle,
                    companyName, "", "",
                    onSuccess = {
                        Toast.makeText(
                            requireContext(),
                            "Experience is updated successfully",
                            Toast.LENGTH_LONG
                        ).show()
                        val intent = Intent(requireContext(), ApplManageProfileActivity::class.java)
                        startActivity(intent)
                    },
                    onError = { errorMessage ->
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                    })
            } else {
                if (viewModel.validateData(
                        jobTitle,
                        companyName
                    ) && viewModel.validateDate(
                        binding.startDateSpinner.selectedItem.toString(),
                        binding.endDateSpinner.selectedItem.toString()
                    )
                ) {
                    viewModel.updateExperienceData(applId,
                        jobTitle,
                        companyName,
                        binding.startDateSpinner.selectedItem.toString(),
                        binding.endDateSpinner.selectedItem.toString(),
                        onSuccess = {
                            Toast.makeText(
                                requireContext(),
                                "Experience is updated successfully",
                                Toast.LENGTH_LONG
                            ).show()
                            val intent =
                                Intent(requireContext(), ApplManageProfileActivity::class.java)
                            startActivity(intent)
                        },
                        onError = { errorMessage ->
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                        })
                }
            }

        }

        viewModel.jobTitleError.observe(requireActivity()) { errorMessage ->
            binding.textInputLayoutJobTitle.error = errorMessage
        }

        viewModel.companyNameError.observe(requireActivity()) { errorMessage ->
            binding.textInputLayoutCompanyName.error = errorMessage
        }

        viewModel.startDateError.observe(requireActivity()) { errorMessage ->
            binding.textInputLayoutStartDate.error = errorMessage
        }

        viewModel.endDateError.observe(requireActivity()) { errorMessage ->
            binding.textInputLayoutEndDate.error = errorMessage
        }

        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        return binding.root
    }

    private fun companyIndustrySpinner() {
        val companyIndustryAdapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                viewModel.companyIndustry
            )
        companyIndustryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.companyIndustrySpinner.adapter = companyIndustryAdapter

        binding.companyIndustrySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>,
                    selectedItemView: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.selectedCompanyIndustry = viewModel.companyIndustry[position]
                }

                override fun onNothingSelected(parentView: AdapterView<*>) {
                    viewModel.selectedCompanyIndustry = ""
                }
            }
    }

    private fun checkSwitchBtnStatus(): Boolean {
        return binding.switchBtn.isChecked
    }

}