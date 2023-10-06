package com.example.jobguru.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.example.jobguru.databinding.FragmentApplEditEducationBinding
import com.example.jobguru.viewmodel.ApplEditEducationViewModel
import kotlin.math.log

class ApplEditEducationFragment : Fragment() {
    private lateinit var binding: FragmentApplEditEducationBinding
    private lateinit var viewModel: ApplEditEducationViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentApplEditEducationBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(ApplEditEducationViewModel::class.java)
        educationLvlSpinner()
        locationSpinner()
        yearOfGraduationSpinner()
        monthOfGraduationSpinner()

        binding.cancelBtn.setOnClickListener {
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack()
        }

        val applId = arguments?.getString("applId").toString()
        val educationLvlPosition =
            viewModel.educationLvls.indexOf(arguments?.getString("educationLvl"))
        if (educationLvlPosition != -1) {
            binding.educationLvlSpinner.setSelection(educationLvlPosition)
        }

        binding.instituteTextBox.setText(arguments?.getString("institute").toString())

        val selectedItem = binding.educationLvlSpinner.selectedItem.toString()
        var fieldOfStudiesPosition = 0
        fieldOfStudiesPosition = if (selectedItem == "SPM" || selectedItem == "STPM") {
            fieldOfStudiesSpinner(2)
            viewModel.fieldOfStudies2.indexOf(arguments?.getString("fieldOfStudies"))
        } else {
            fieldOfStudiesSpinner(1)
            viewModel.fieldOfStudies1.indexOf(arguments?.getString("fieldOfStudies"))
        }
        if (fieldOfStudiesPosition != -1) {
            binding.fieldOfStudiesSpinner.setSelection(fieldOfStudiesPosition)
        }

        val locationPosition = viewModel.locations.indexOf(arguments?.getString("location"))
        if (locationPosition != -1) {
            binding.locationSpinner.setSelection(locationPosition)
        }

        val yearPosition = viewModel.years.indexOf(arguments?.getString("yearOfGraduation"))
        if (yearPosition != -1) {
            binding.yearOfGraduationSpinner.setSelection(yearPosition)
        }

        val monthPosition = viewModel.months.indexOf(arguments?.getString("monthOfGraduation"))
        if (monthPosition != -1) {
            binding.monthOfGraduationSpinner.setSelection(monthPosition)
        }

        binding.saveBtn.setOnClickListener {
            val institute = binding.instituteTextBox.text.toString()

            if (viewModel.validateData(institute)) {
                viewModel.updateEducationData(applId, institute,
                    onSuccess = {
                        Toast.makeText(
                            requireContext(),
                            "Education is updated successfully",
                            Toast.LENGTH_LONG
                        ).show()
                        val intent = Intent(requireContext(), ApplManageProfileActivity::class.java)
                        startActivity(intent)
                    },
                    onError = { errorMessage ->
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                    })
            }
        }

        viewModel.instituteError.observe(requireActivity()) { errorMessage ->
            binding.textInputLayoutInstitute.error = errorMessage
        }

        Log.d("Test1", binding.fieldOfStudiesSpinner.selectedItem.toString())

        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        return binding.root
    }

    private fun educationLvlSpinner() {
        val educationLvlAdapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                viewModel.educationLvls
            )
        educationLvlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.educationLvlSpinner.adapter = educationLvlAdapter

        binding.educationLvlSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>,
                    selectedItemView: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.selectedEducationLvl = viewModel.educationLvls[position]
                }

                override fun onNothingSelected(parentView: AdapterView<*>) {
                    viewModel.selectedEducationLvl = ""
                }
            }
    }

    private fun fieldOfStudiesSpinner(arrayId: Int) {
        var fieldOfStudiesAdapter: ArrayAdapter<String>
        if (arrayId == 1) {
            fieldOfStudiesAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                viewModel.fieldOfStudies1
            )
        } else {
            fieldOfStudiesAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                viewModel.fieldOfStudies2
            )
        }

        fieldOfStudiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.fieldOfStudiesSpinner.adapter = fieldOfStudiesAdapter

        binding.fieldOfStudiesSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>,
                    selectedItemView: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.selectedFieldOfStudies =
                        fieldOfStudiesAdapter.getItem(position).toString()
                }

                override fun onNothingSelected(parentView: AdapterView<*>) {
                    viewModel.selectedFieldOfStudies = ""
                }
            }
    }

    private fun locationSpinner() {
        val locationAdapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                viewModel.locations
            )
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.locationSpinner.adapter = locationAdapter

        binding.locationSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>,
                    selectedItemView: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.selectedLocation = viewModel.locations[position]
                }

                override fun onNothingSelected(parentView: AdapterView<*>) {
                    viewModel.selectedLocation = ""
                }
            }
    }

    private fun yearOfGraduationSpinner() {
        val yearAdapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                viewModel.years
            )
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.yearOfGraduationSpinner.adapter = yearAdapter

        binding.yearOfGraduationSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>,
                    selectedItemView: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.selectedYearOfGraduation = viewModel.years[position]
                }

                override fun onNothingSelected(parentView: AdapterView<*>) {
                    viewModel.selectedYearOfGraduation = ""
                }
            }
    }

    private fun monthOfGraduationSpinner() {
        val monthAdapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                viewModel.months
            )
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.monthOfGraduationSpinner.adapter = monthAdapter

        binding.monthOfGraduationSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>,
                    selectedItemView: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.selectedMonthOfGraduation = viewModel.months[position]
                }

                override fun onNothingSelected(parentView: AdapterView<*>) {
                    viewModel.selectedMonthOfGraduation = ""
                }
            }
    }
}