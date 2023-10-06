package com.example.jobguru.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.jobguru.databinding.FragmentApplEditProfileBinding
import com.example.jobguru.viewmodel.ApplEditProfileViewModel

class ApplEditProfileFragment : Fragment() {
    private lateinit var binding: FragmentApplEditProfileBinding
    private lateinit var viewModel: ApplEditProfileViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentApplEditProfileBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(ApplEditProfileViewModel::class.java)
        areaCodeSpinner()
        genderSpinner()

        binding.cancelBtn.setOnClickListener {
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack()
        }

        val applId = arguments?.getString("applId").toString()
        binding.firstNameTextBox.setText(arguments?.getString("firstName").toString())
        binding.lastNameTextBox.setText(arguments?.getString("lastName").toString())
        binding.emailTextBox.setText(arguments?.getString("email").toString())
        val areaCodePosition = viewModel.areaCodes.indexOf(arguments?.getString("areaCode"))
        if (areaCodePosition != -1) {
            binding.areaCodeSpinner.setSelection(areaCodePosition)
        }
        binding.phoneNumTextBox.setText(arguments?.getString("phoneNum").toString())
        val genderPosition = viewModel.gender.indexOf(arguments?.getString("gender"))
        if (genderPosition != -1) {
            binding.genderSpinner.setSelection(genderPosition)
        }

        binding.saveBtn.setOnClickListener {
            val firstName = binding.firstNameTextBox.text.toString()
            val lastName = binding.lastNameTextBox.text.toString()
            val phoneNum = binding.phoneNumTextBox.text.toString()

            if (viewModel.validateData(firstName, lastName, phoneNum)) {
                viewModel.updateProfileData(applId, firstName, lastName, phoneNum,
                    onSuccess = {
                        Toast.makeText(
                            requireContext(),
                            "Profile is updated successfully",
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

        viewModel.firstNameError.observe(requireActivity()) { errorMessage ->
            binding.textInputLayoutFirstName.error = errorMessage
        }

        viewModel.lastNameError.observe(requireActivity()) { errorMessage ->
            binding.textInputLayoutLastName.error = errorMessage
        }

        viewModel.phoneNumError.observe(requireActivity()) { errorMessage ->
            binding.textInputLayoutPhoneNum.error = errorMessage
        }

        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        return binding.root
    }

    private fun areaCodeSpinner() {
        val areaCodeAdapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                viewModel.areaCodes
            )
        areaCodeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.areaCodeSpinner.adapter = areaCodeAdapter

        binding.areaCodeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>,
                    selectedItemView: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.selectedAreaCode = viewModel.areaCodes[position]
                }

                override fun onNothingSelected(parentView: AdapterView<*>) {
                    viewModel.selectedAreaCode = ""
                }
            }
    }

    private fun genderSpinner() {
        val genderAdapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                viewModel.gender
            )
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.genderSpinner.adapter = genderAdapter

        binding.genderSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>,
                    selectedItemView: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.selectedGender = viewModel.gender[position]
                }

                override fun onNothingSelected(parentView: AdapterView<*>) {
                    viewModel.selectedGender = ""
                }
            }
    }

}