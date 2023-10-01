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
import com.example.jobguru.databinding.FragmentApplCompleteCurrentStatusBinding
import com.example.jobguru.viewmodel.ApplCompleteCurrentStatusViewModel
import com.example.jobguru.viewmodel.ApplSignUpViewModel

class ApplCompleteCurrentStatusFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: FragmentApplCompleteCurrentStatusBinding
    private lateinit var viewModel: ApplCompleteCurrentStatusViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentApplCompleteCurrentStatusBinding.inflate(inflater, container, false)

        binding.upBtn.setOnClickListener { view ->
            requireActivity().onBackPressed()
        }

        binding.liveInSpinner.onItemSelectedListener = this
        ArrayAdapter.createFromResource(
            requireContext(), R.array.liveIn_spinner, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.liveInSpinner.adapter = adapter
        }

        binding.areaCodeSpinner.onItemSelectedListener = this
        ArrayAdapter.createFromResource(
            requireContext(), R.array.areaCode_spinner, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.areaCodeSpinner.adapter = adapter
        }

        binding.phoneNumTextBox.addTextChangedListener(object : TextWatcher {
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

        binding.nationalitySpinner.onItemSelectedListener = this
        ArrayAdapter.createFromResource(
            requireContext(), R.array.nationality_spinner, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.nationalitySpinner.adapter = adapter
        }

        binding.salarySpinner.onItemSelectedListener = this
        ArrayAdapter.createFromResource(
            requireContext(), R.array.salary_spinner, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.salarySpinner.adapter = adapter
        }

        viewModel = ViewModelProvider(this).get(ApplCompleteCurrentStatusViewModel::class.java)

        binding.continueBtn.setOnClickListener {
            val phoneNum = binding.phoneNumTextBox.text.toString().trim()
            if(viewModel.validatePhoneNum(phoneNum)){
                saveCurrentStatus(binding.liveInSpinner.selectedItem.toString(), binding.areaCodeSpinner.selectedItem.toString(), phoneNum, binding.nationalitySpinner.selectedItem.toString(), binding.salarySpinner.selectedItem.toString())

                val nextFragment = ApplCompleteEducationFragment()
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frame_layout, nextFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }

        viewModel.phoneNumError.observe(requireActivity()) { errorMessage ->
            binding.textInputLayoutPhoneNum.error = errorMessage
        }
        return binding.root
    }

    private fun saveCurrentStatus(selectedLiveInString: String, selectedAreaCodeString: String, phoneNumber:String, selectedNationalityString: String, selectedMinMonthlySalaryString: String) {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("selectedLiveInString", selectedLiveInString)
        editor.putString("selectedAreaCodeString", selectedAreaCodeString)
        editor.putString("phoneNum", phoneNumber)
        editor.putString("selectedNationalityString", selectedNationalityString)
        editor.putString("selectedMinMonthlySalaryString", selectedMinMonthlySalaryString)
        editor.apply()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedValue = parent?.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }



}