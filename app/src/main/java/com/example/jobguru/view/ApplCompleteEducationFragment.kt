package com.example.jobguru.view

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.jobguru.R
import com.example.jobguru.databinding.FragmentApplCompleteEducationBinding
import com.example.jobguru.viewmodel.ApplCompleteCurrentStatusViewModel

class ApplCompleteEducationFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: FragmentApplCompleteEducationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentApplCompleteEducationBinding.inflate(inflater, container, false)

        binding.upBtn.setOnClickListener { view ->
            requireActivity().onBackPressed()
        }

        binding.educationLvlSpinner.onItemSelectedListener = this
        ArrayAdapter.createFromResource(
            requireContext(), R.array.educationLvl_spinner, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.educationLvlSpinner.adapter = adapter
        }

        binding.educationLvlSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = binding.educationLvlSpinner.selectedItem.toString()

                    // Check the selected item in the educationLvlSpinner
                    val fieldOfStudiesArrayId = when (selectedItem) {
                        "SPM", "STPM" -> R.array.fieldOfStudies2_spinner
                        else -> R.array.fieldOfStudies1_spinner
                    }

                    // Create the ArrayAdapter based on the selected item
                    val adapter = ArrayAdapter.createFromResource(
                        requireContext(),
                        fieldOfStudiesArrayId,
                        android.R.layout.simple_spinner_item
                    )

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.fieldOfStudiesSpinner.adapter = adapter
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Handle the case where nothing is selected (if needed)
                }
            }


        binding.instituteTextBox.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotBlank()) {
                    binding.continueBtn.isEnabled = true
                    binding.continueBtn.setBackgroundResource(R.drawable.filled_button)
                } else {
                    binding.continueBtn.isEnabled = false
                    binding.continueBtn.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            android.R.color.white
                        )
                    )
                    binding.continueBtn.setBackgroundResource(R.drawable.grayed_button)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used
            }
        })

        binding.fieldOfStudiesSpinner.onItemSelectedListener = this
        ArrayAdapter.createFromResource(
            requireContext(), R.array.fieldOfStudies1_spinner, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.fieldOfStudiesSpinner.adapter = adapter
        }

        binding.locationSpinner.onItemSelectedListener = this
        ArrayAdapter.createFromResource(
            requireContext(), R.array.nationality_spinner, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.locationSpinner.adapter = adapter
        }

        binding.yearOfGraduationSpinner.onItemSelectedListener = this
        ArrayAdapter.createFromResource(
            requireContext(), R.array.year_spinner, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.yearOfGraduationSpinner.adapter = adapter
        }

        binding.monthOfGraduationSpinner.onItemSelectedListener = this
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.month_spinner,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.monthOfGraduationSpinner.adapter = adapter
        }

        binding.continueBtn.setOnClickListener {

            val institute = binding.instituteTextBox.text.toString()
            saveEducation(
                binding.educationLvlSpinner
                    .selectedItem.toString(),
                institute,
                binding.fieldOfStudiesSpinner.selectedItem.toString(),
                binding.locationSpinner.selectedItem.toString(),
                binding.yearOfGraduationSpinner.selectedItem.toString(),
                binding.monthOfGraduationSpinner.selectedItem.toString()
            )

            val nextFragment = ApplCompleteExperienceFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, nextFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return binding.root
    }

    private fun saveEducation(
        selectedEducationLvlString: String,
        institute: String,
        selectedFieldOfStudies: String,
        selectedLocationString: String,
        selectedYearOfGraduationSpinnerString: String,
        selectedMonthOfGraduationSpinnerString: String
    ) {
        val sharedPreferences =
            requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("selectedEducationLvlString", selectedEducationLvlString)
        editor.putString("institute", institute)
        editor.putString("selectedFieldOfStudies", selectedFieldOfStudies)
        editor.putString("selectedLocationString", selectedLocationString)
        editor.putString(
            "selectedYearOfGraduationSpinnerString",
            selectedYearOfGraduationSpinnerString
        )
        editor.putString(
            "selectedMonthOfGraduationSpinnerString",
            selectedMonthOfGraduationSpinnerString
        )
        editor.apply()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedValue = parent?.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}