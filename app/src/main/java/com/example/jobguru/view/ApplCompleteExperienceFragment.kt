package com.example.jobguru.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.jobguru.R
import com.example.jobguru.databinding.FragmentApplCompleteExperienceBinding
import com.example.jobguru.viewmodel.ApplCompleteExperienceViewModel
import com.example.jobguru.viewmodel.CombinedSpinnerAdapter

class ApplCompleteExperienceFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: FragmentApplCompleteExperienceBinding
    private lateinit var viewModel: ApplCompleteExperienceViewModel
    private lateinit var monthsArray: Array<String>
    private lateinit var yearsArray: Array<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentApplCompleteExperienceBinding.inflate(inflater, container, false)

        binding.upBtn.setOnClickListener { view ->
            requireActivity().onBackPressed()
        }

        binding.switchBtn.setOnClickListener {
            if (checkSwitchBtnStatus()) {
                binding.fillInExperience.visibility = View.GONE
                binding.completeBtn.isEnabled = true
                binding.completeBtn.setBackgroundResource(R.drawable.filled_button)
            } else {
                binding.completeBtn.isEnabled = false
                binding.completeBtn.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        android.R.color.white
                    )
                )
                binding.completeBtn.setBackgroundResource(R.drawable.grayed_button)
            }
        }

        binding.jobTitleTextBox.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkTextFields()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used
            }
        })

        binding.companyNameTextBox.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkTextFields()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used
            }
        })

        binding.companyIndustrySpinner.onItemSelectedListener = this
        ArrayAdapter.createFromResource(
            requireContext(), R.array.companyIndustry_spinner, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.companyIndustrySpinner.adapter = adapter
        }

        monthsArray = resources.getStringArray(R.array.month_spinner)
        yearsArray = resources.getStringArray(R.array.year_spinner)

        val combinedAdapter = CombinedSpinnerAdapter(requireActivity(), yearsArray, monthsArray)
        binding.startDateSpinner.adapter = combinedAdapter
        binding.endDateSpinner.adapter = combinedAdapter

        viewModel = ViewModelProvider(this).get(ApplCompleteExperienceViewModel::class.java)

        binding.completeBtn.setOnClickListener {
            if (viewModel.validateDate(
                    binding.startDateSpinner.selectedItem.toString(),
                    binding.endDateSpinner.selectedItem.toString()
                )
            ) {
                saveExperience(
                    binding.jobTitleTextBox.text.toString(),
                    binding.companyNameTextBox.text.toString(),
                    binding.startDateSpinner.selectedItem.toString(),
                    binding.endDateSpinner.selectedItem.toString(),
                    binding.companyIndustrySpinner.selectedItem.toString()
                )
                val sharedPreferences =
                    requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val applId = sharedPreferences.getString("applId", "")
                val selectedLiveInString = sharedPreferences.getString("selectedLiveInString", "")
                val selectedAreaCodeString =
                    sharedPreferences.getString("selectedAreaCodeString", "")
                val phoneNum = sharedPreferences.getString("phoneNum", "")
                val selectedNationalityString =
                    sharedPreferences.getString("selectedNationalityString", "")
                val selectedMinMonthlySalaryString =
                    sharedPreferences.getString("selectedMinMonthlySalaryString", "")
                val minMonthlySalary =
                    selectedMinMonthlySalaryString?.replace(Regex("[^0-9.]"), "")?.trim()
                val selectedEducationLvlString =
                    sharedPreferences.getString("selectedEducationLvlString", "")
                val institute = sharedPreferences.getString("institute", "")
                val selectedFieldOfStudies =
                    sharedPreferences.getString("selectedFieldOfStudies", "")
                val selectedLocationString =
                    sharedPreferences.getString("selectedLocationString", "")
                val selectedYearOfGraduationSpinnerString =
                    sharedPreferences.getString("selectedYearOfGraduationSpinnerString", "")
                val selectedMonthOfGraduationSpinnerString =
                    sharedPreferences.getString("selectedMonthOfGraduationSpinnerString", "")
                val expJobTitle = sharedPreferences.getString("expJobTitle", "")
                val expCompanyName = sharedPreferences.getString("expCompanyName", "")
                val expStartDate = sharedPreferences.getString("expStartDate", "")
                val expEndDate = sharedPreferences.getString("expEndDate", "")
                val expCompanyIndustry = sharedPreferences.getString("expCompanyIndustry", "")
                if (applId != null) {
                    if (selectedLiveInString != null) {
                        if (selectedAreaCodeString != null) {
                            if (phoneNum != null) {
                                if (selectedNationalityString != null) {
                                    if (minMonthlySalary != null) {
                                        if (selectedEducationLvlString != null) {
                                            if (institute != null) {
                                                if (selectedFieldOfStudies != null) {
                                                    if (selectedLocationString != null) {
                                                        if (selectedYearOfGraduationSpinnerString != null) {
                                                            if (selectedMonthOfGraduationSpinnerString != null) {
                                                                if (expJobTitle != null) {
                                                                    if (expCompanyName != null) {
                                                                        if (expStartDate != null) {
                                                                            if (expEndDate != null) {
                                                                                if (expCompanyIndustry != null) {
                                                                                    viewModel.updateApplicantProfile(
                                                                                        applId,
                                                                                        selectedLiveInString,
                                                                                        selectedAreaCodeString,
                                                                                        phoneNum,
                                                                                        selectedNationalityString,
                                                                                        minMonthlySalary.toDouble(),
                                                                                        selectedEducationLvlString,
                                                                                        institute,
                                                                                        selectedFieldOfStudies,
                                                                                        selectedLocationString,
                                                                                        selectedYearOfGraduationSpinnerString,
                                                                                        selectedMonthOfGraduationSpinnerString,
                                                                                        expJobTitle,
                                                                                        expCompanyName,
                                                                                        expStartDate,
                                                                                        expEndDate,
                                                                                        expCompanyIndustry, onSuccess = {
                                                                                            Toast.makeText(requireContext(), "Profile Updated", Toast.LENGTH_LONG).show()
                                                                                            requireActivity().finish()
                                                                                            val intent = Intent(requireContext(), ApplSubmitApplicationActivity::class.java)
//                                                                                            intent.putExtras(requireActivity().intent) // Pass any existing extras if needed
                                                                                            startActivity(intent)
                                                                                        },
                                                                                        onError = { errorMessage ->
                                                                                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                                                                                        })
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }


                val intent = Intent(requireContext(), ApplSubmitApplicationActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }

        viewModel.startDateError.observe(requireActivity()) { errorMessage ->
            binding.textInputLayoutStartDate.error = errorMessage
        }

        viewModel.endDateError.observe(requireActivity()) { errorMessage ->
            binding.textInputLayoutEndDate.error = errorMessage
        }

        return binding.root
    }

    private fun checkTextFields() {
        val jobTitleText = binding.jobTitleTextBox.text.toString()
        val companyNameText = binding.companyNameTextBox.text.toString()

        if (jobTitleText.isNotBlank() && companyNameText.isNotBlank()) {
            binding.completeBtn.isEnabled = true
            binding.completeBtn.setBackgroundResource(R.drawable.filled_button)
        } else {
            binding.completeBtn.isEnabled = false
            binding.completeBtn.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    android.R.color.white
                )
            )
            binding.completeBtn.setBackgroundResource(R.drawable.grayed_button)
        }
    }

    private fun checkSwitchBtnStatus(): Boolean {
        return binding.switchBtn.isChecked
    }

    private fun saveExperience(
        expJobTitle: String,
        expCompanyName: String,
        expStartDate: String,
        expEndDate: String,
        expCompanyIndustry: String
    ) {
        val sharedPreferences =
            requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        if (!checkSwitchBtnStatus()) {
            editor.putString("expJobTitle", expJobTitle)
            editor.putString("expCompanyName", expCompanyName)
            editor.putString("expStartDate", expStartDate)
            editor.putString("expEndDate", expEndDate)
            editor.putString("expCompanyIndustry", expCompanyIndustry)
            editor.apply()
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedValue = parent?.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}