package com.example.jobguru.view

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.jobguru.databinding.FragmentEmpSignUpBinding
import com.example.jobguru.viewmodel.EmpSignUpViewModel

class EmpSignUpFragment : Fragment() {
    private lateinit var binding: FragmentEmpSignUpBinding
    private lateinit var viewModel: EmpSignUpViewModel
    private var imageUri: Uri? = null
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEmpSignUpBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this).get(EmpSignUpViewModel::class.java)

        binding.upButton.setOnClickListener {
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack()
        }
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Signing up")

        binding.signUpBtn.setOnClickListener {
            val empName = binding.empNameField.text.toString()
            val empIndustry = binding.empIndustryField.text.toString()
            val empAddress =
                binding.empAdd1Field.text.toString() + ", " + binding.empAdd2Field.text.toString()
            val empPostcode = binding.empPostcodeField.text.toString()
            val personInChargeName = binding.personInChargeNameField.text.toString()
            val personInChargeContact = binding.personInChargeContactField.text.toString()
            val personInChargeDesignation = binding.personInChargeDesignationField.text.toString()
            val personInChargeEmail = binding.personInChargeEmailField.text.toString()
            val personInChargePassword = binding.personInChargePasswordField.text.toString()

            if (viewModel.validateData(
                    imageUri,
                    empName,
                    empIndustry,
                    empAddress,
                    empPostcode,
                    personInChargeName,
                    personInChargeContact,
                    personInChargeDesignation,
                    personInChargeEmail,
                    personInChargePassword
                )
            ) {
                progressDialog.show()
                viewModel.empSignUp(personInChargeEmail, personInChargePassword)

                // Observe registration success
                viewModel.registrationSuccess.observe(requireActivity()) { isSuccess ->
                    if (isSuccess) {
                        viewModel.saveEmployerData(empName,
                            empIndustry,
                            empAddress,
                            empPostcode,
                            personInChargeName,
                            personInChargeContact,
                            personInChargeDesignation,
                            personInChargeEmail, onSuccess =
                            {
                                viewModel.uploadImage(personInChargeEmail, imageUri)
                                Toast.makeText(
                                    requireContext(),
                                    "Sign Up Successfully",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                                val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                editor.putString("personInChargeEmail", binding.personInChargeEmailField.text.toString())
                                editor.apply()
                                progressDialog.dismiss()
                                requireActivity().finish()
                                val intent = Intent(requireContext(), EmpJobsActivity::class.java)
                                startActivity(intent)
                            }, onError =
                            { errorMessage ->
                                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG)
                                    .show()
                            }
                        )

                    }
                }

                // Observe registration error
                viewModel.dbErrorText.observe(requireActivity()) { errorMessage ->
                    if (errorMessage.contains("An error occurred:")) {
                        progressDialog.dismiss()
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    } else {
                        binding.personInChargeEmailErrorMessage.text = errorMessage
                        binding.personInChargeEmailErrorMessage.visibility = View.VISIBLE
                        progressDialog.dismiss()
                    }
                }
            }
        }

        // Observe error messages and update UI accordingly
        viewModel.imageError.observe(requireActivity()) { errorMessage ->
            binding.empLogoErrorMessage.text = errorMessage
            binding.empLogoErrorMessage.visibility = View.VISIBLE
            progressDialog.dismiss()
        }

        viewModel.empNameError.observe(requireActivity()) { errorMessage ->
            binding.empNameErrorMessage.text = errorMessage
            binding.empNameErrorMessage.visibility = View.VISIBLE
            progressDialog.dismiss()
        }

        viewModel.empIndustryError.observe(requireActivity()) { errorMessage ->
            binding.empIndustryErrorMessage.text = errorMessage
            binding.empIndustryErrorMessage.visibility = View.VISIBLE
            progressDialog.dismiss()
        }
        viewModel.empAddressError.observe(requireActivity()) { errorMessage ->
            binding.empAdd1ErrorMessage.text = errorMessage
            binding.empAdd1ErrorMessage.visibility = View.VISIBLE
            binding.empAdd2ErrorMessage.text = errorMessage
            binding.empAdd2ErrorMessage.visibility = View.VISIBLE
            progressDialog.dismiss()
        }

        viewModel.empPostcodeError.observe(requireActivity()) { errorMessage ->
            binding.empPostcodeErrorMessage.text = errorMessage
            binding.empPostcodeErrorMessage.visibility = View.VISIBLE
            progressDialog.dismiss()
        }

        viewModel.personInChargeNameError.observe(requireActivity()) { errorMessage ->
            binding.personInChargeNameErrorMessage.text = errorMessage
            binding.personInChargeNameErrorMessage.visibility = View.VISIBLE
            progressDialog.dismiss()
        }
        viewModel.personInChargeContactError.observe(requireActivity()) { errorMessage ->
            binding.personInChargeContactErrorMessage.text = errorMessage
            binding.personInChargeContactErrorMessage.visibility = View.VISIBLE
            progressDialog.dismiss()
        }

        viewModel.personInChargeDesignationError.observe(requireActivity()) { errorMessage ->
            binding.personInChargeDesignationErrorMessage.text = errorMessage
            binding.personInChargeDesignationErrorMessage.visibility = View.VISIBLE
            progressDialog.dismiss()
        }

        viewModel.personInChargeEmailError.observe(requireActivity()) { errorMessage ->
            binding.personInChargeEmailErrorMessage.text = errorMessage
            binding.personInChargeEmailErrorMessage.visibility = View.VISIBLE
            progressDialog.dismiss()
        }

        viewModel.personInChargePasswordError.observe(requireActivity()) { errorMessage ->
            binding.personInChargePasswordErrorMessage.text = errorMessage
            binding.personInChargePasswordErrorMessage.visibility = View.VISIBLE
            progressDialog.dismiss()
        }

        binding.uploadLogoBtn.setOnClickListener {
            selectImage()
        }

        genderSpinner()
        stateSpinner()

        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        return binding.root
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == AppCompatActivity.RESULT_OK) {
            imageUri = data?.data!!
            val imageFileName = getImageFileName(imageUri!!, requireContext()) ?: "Default Text"
            binding.empLogoField.text =
                Editable.Factory.getInstance().newEditable(imageFileName)
        }
    }

    @SuppressLint("Range")
    fun getImageFileName(uri: Uri, context: Context): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (displayNameIndex != -1) {
                        val displayName = it.getString(displayNameIndex)
                        result = displayName
                    } else {
                        result = "default_image_logo.jpg" // Example: Return a default name
                    }
                } else {
                    result = " "
                }
            }
        }
        return result
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