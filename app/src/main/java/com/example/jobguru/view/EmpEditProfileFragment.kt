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
import com.example.jobguru.viewmodel.EmpEditJobViewModel
import com.example.jobguru.view.EmpProfileActivity
import com.example.jobguru.R
import com.example.jobguru.databinding.FragmentEmpAddNewJobBinding
import com.example.jobguru.databinding.FragmentEmpEditJobBinding
import com.example.jobguru.databinding.FragmentEmpEditProfileBinding
import com.example.jobguru.viewmodel.EmpEditProfileViewModel

class EmpEditProfileFragment : Fragment() {
    private lateinit var binding: FragmentEmpEditProfileBinding
    private lateinit var viewModel: EmpEditProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmpEditProfileBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(EmpEditProfileViewModel::class.java)
        stateSpinner()
        genderSpinner()

        binding.upButton.setOnClickListener {
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack()
        }

        val empEmail = arguments?.getString("personInChargeEmail").toString()
        binding.empNameField.setText(arguments?.getString("empName").toString())
        binding.empIndustryField.setText(arguments?.getString("empIndustry").toString())
        binding.empAdd1Field.setText(arguments?.getString("empAddress").toString())
        binding.empPostcodeField.setText(arguments?.getString("empPostcode").toString())
        val statePosition = viewModel.states.indexOf(arguments?.getString("empState"))
        if (statePosition != -1) {
            binding.stateSpinner.setSelection(statePosition)
        }
        binding.personInChargeNameField.setText(
            arguments?.getString("personInChargeName").toString()
        )
        binding.personInChargeContactField.setText(
            arguments?.getString("personInChargeContact").toString()
        )
        binding.personInChargeDesignationField.setText(
            arguments?.getString("personInChargeDesignation").toString()
        )
        val genderPosition = viewModel.genders.indexOf(arguments?.getString("personInChargeGender"))
        if (genderPosition != -1) {
            binding.personInChargeGenderSpinner.setSelection(genderPosition)
        }

        binding.editProfileBtn.setOnClickListener {
            val empName = binding.empNameField.text.toString()
            val empIndustry = binding.empIndustryField.text.toString()
            val empAddress =
                binding.empAdd1Field.text.toString() + " " + binding.empAdd2Field.text.toString()
            val empPostcode = binding.empPostcodeField.text.toString()
            val personInChargeName = binding.personInChargeNameField.text.toString()
            val personInChargeContact = binding.personInChargeContactField.text.toString()
            val personInChargeDesignation = binding.personInChargeDesignationField.text.toString()

            if (viewModel.validateData(
                    empName,
                    empIndustry,
                    empAddress,
                    empPostcode,
                    personInChargeName,
                    personInChargeContact,
                    personInChargeDesignation
                )
            ) {
                viewModel.updateProfileData(
                    empEmail,
                    empName,
                    empIndustry,
                    empAddress,
                    empPostcode,
                    personInChargeName,
                    personInChargeContact,
                    personInChargeDesignation,
                    onSuccess = {
                        Toast.makeText(requireContext(), "Profile Data Updated", Toast.LENGTH_LONG)
                            .show()
                        requireActivity().finish()
                        val intent = Intent(requireContext(), EmpProfileActivity::class.java)
                        intent.putExtras(requireActivity().intent) // Pass any existing extras if needed
                        startActivity(intent)
                        //requireActivity().onBackPressed()
                    },
                    onError = { errorMessage ->
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                    })
            }

        }

        // Observe error messages and update UI accordingly
        viewModel.empNameError.observe(requireActivity()) { errorMessage ->
            binding.empNameErrorMessage.text = errorMessage
            binding.empNameErrorMessage.visibility = View.VISIBLE
        }

        viewModel.empIndustryError.observe(requireActivity()) { errorMessage ->
            binding.empIndustryErrorMessage.text = errorMessage
            binding.empIndustryErrorMessage.visibility = View.VISIBLE
        }
        viewModel.empAddressError.observe(requireActivity()) { errorMessage ->
            binding.empAdd1ErrorMessage.text = errorMessage
            binding.empAdd1ErrorMessage.visibility = View.VISIBLE
            binding.empAdd2ErrorMessage.text = errorMessage
            binding.empAdd2ErrorMessage.visibility = View.VISIBLE
        }

        viewModel.empPostcodeError.observe(requireActivity()) { errorMessage ->
            binding.empPostcodeErrorMessage.text = errorMessage
            binding.empPostcodeErrorMessage.visibility = View.VISIBLE
        }

        viewModel.personInChargeNameError.observe(requireActivity()) { errorMessage ->
            binding.personInChargeNameErrorMessage.text = errorMessage
            binding.personInChargeNameErrorMessage.visibility = View.VISIBLE
        }
        viewModel.personInChargeContactError.observe(requireActivity()) { errorMessage ->
            binding.personInChargeContactErrorMessage.text = errorMessage
            binding.personInChargeContactErrorMessage.visibility = View.VISIBLE
        }

        viewModel.personInChargeDesignationError.observe(requireActivity()) { errorMessage ->
            binding.personInChargeDesignationErrorMessage.text = errorMessage
            binding.personInChargeDesignationErrorMessage.visibility = View.VISIBLE
        }

        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        return binding.root
    }

    private fun genderSpinner() {
        val genderAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, viewModel.genders)
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.personInChargeGenderSpinner.adapter = genderAdapter

        binding.personInChargeGenderSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>,
                    selectedItemView: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.selectedGender = viewModel.genders[position]
                }

                override fun onNothingSelected(parentView: AdapterView<*>) {
                    viewModel.selectedGender = ""
                }
            }
    }

    private fun stateSpinner() {
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