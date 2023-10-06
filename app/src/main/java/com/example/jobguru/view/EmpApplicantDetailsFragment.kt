package com.example.jobguru.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.jobguru.R
import com.example.jobguru.databinding.FragmentEmpApplicantDetailsBinding
import com.example.jobguru.databinding.FragmentEmpInterviewDetailsBinding
import com.example.jobguru.databinding.FragmentEmpSignUpBinding
import com.example.jobguru.model.ApplyModel
import com.example.jobguru.view.EmpJobDetailsActivity
import com.example.jobguru.view.EmpJobsActivity
import com.example.jobguru.viewmodel.EmpApplicantAdapter
import com.example.jobguru.viewmodel.EmpApplicantDetailsViewModel
import com.example.jobguru.viewmodel.EmpApplicantsViewModel
import com.example.jobguru.viewmodel.EmpApplicantsViewModelFactory
import com.example.jobguru.viewmodel.EmpSignUpViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EmpApplicantDetailsFragment : Fragment() {
    private lateinit var binding: FragmentEmpApplicantDetailsBinding
    private lateinit var viewModel: EmpApplicantDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmpApplicantDetailsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(EmpApplicantDetailsViewModel::class.java)

        binding.upButton.setOnClickListener {
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack()
        }


        val applId = arguments?.getString("applId") ?: ""
        val jobId = arguments?.getString("jobId") ?: ""
        val appId = arguments?.getString("appId") ?: ""
        val jobTitle = arguments?.getString("jobTitle") ?: ""
        val empName = arguments?.getString("empName") ?: ""
        var applName = ""
        var phoneNum = ""
        viewModel.getApplicantData(
            applId
        )

        binding.tvJobTitle.text = jobTitle
        viewModel.applFirstName.observe(requireActivity(), { applFirstName ->
            viewModel.applLastName.observe(requireActivity(), { applLastName ->
                binding.tvApplicantName.text = applFirstName + " " + applLastName
                applName = applFirstName + " " + applLastName
            })
        })

        viewModel.applGender.observe(requireActivity(), { applGender ->
            binding.tvApplicantGender.text = applGender
        })
        viewModel.applEmail.observe(requireActivity(), { applEmail ->
            binding.tvApplicantEmail.text = applEmail
        })
        viewModel.applLiveIn.observe(requireActivity(), { applLiveIn ->
            binding.tvApplicantLiveIn.text = applLiveIn
        })
        viewModel.applAreaCode.observe(requireActivity(), { applAreaCode ->
            viewModel.applPhoneNumber.observe(requireActivity(), { applPhoneNumber ->
                phoneNum = "+${applAreaCode?.replace(Regex("[^\\d]"), "")}" + " " + applPhoneNumber
                binding.tvApplicantPhone.text = phoneNum
            })
        })
        viewModel.applNationality.observe(requireActivity(), { applNationality ->
            binding.tvApplicantNationality.text = applNationality
        })
        viewModel.applMinimumMonthlySalary.observe(
            requireActivity(),
            { applMinimumMonthlySalary ->
                binding.tvApplicantExpectedSalary.text =
                    String.format("%.2f", applMinimumMonthlySalary)
            })
        viewModel.applEducationLevel.observe(requireActivity(), { applEducationLevel ->
            binding.tvApplicantEducationLvl.text = applEducationLevel
        })
        viewModel.applInstitute.observe(requireActivity(), { applInstitute ->
            binding.tvApplicantInstitute.text = applInstitute
        })
        viewModel.applFieldOfStudies.observe(requireActivity(), { applFieldOfStudies ->
            binding.tvApplicantFieldOfStudies.text = applFieldOfStudies
        })
        viewModel.applLocation.observe(requireActivity(), { applLocation ->
            binding.tvApplicantInstituteLocation.text = applLocation
        })

        viewModel.applYearOfGraduation.observe(requireActivity(), { applYearOfGraduation ->
            binding.tvApplicantYearOfGraduation.text = applYearOfGraduation
        })
        viewModel.applMonthOfGraduation.observe(
            requireActivity(),
            { applMonthOfGraduation ->
                binding.tvApplicantMonthOfGraduation.text = applMonthOfGraduation
            })
        viewModel.applInstitute.observe(requireActivity(), { applInstitute ->
            binding.tvApplicantInstitute.text = applInstitute
        })
        viewModel.applFieldOfStudies.observe(requireActivity(), { applFieldOfStudies ->
            binding.tvApplicantFieldOfStudies.text = applFieldOfStudies
        })
        viewModel.applLocation.observe(requireActivity(), { applLocation ->
            binding.tvApplicantInstituteLocation.text = applLocation
        })
        viewModel.applJobTitle.observe(requireActivity(), { applJobTitle ->
            viewModel.applCompanyName.observe(requireActivity(), { applCompanyName ->
                viewModel.applStartDate.observe(requireActivity(), { applStartDate ->
                    viewModel.applEndDate.observe(requireActivity(), { applEndDate ->
                        viewModel.applCompanyIndustry.observe(
                            requireActivity(),
                            { applCompanyIndustry ->
                                val areAllFieldsNull =
                                    applJobTitle.isEmpty() && applCompanyName.isEmpty() &&
                                            applStartDate.isEmpty() && applEndDate.isEmpty() &&
                                            applCompanyIndustry.isEmpty()
                                Log.e("My Tag", areAllFieldsNull.toString())
                                if (areAllFieldsNull) {
                                    binding.recentExpTitle.visibility = View.GONE
                                    binding.recentExpLine1.visibility = View.GONE
                                    binding.recentExpLine2.visibility = View.GONE
                                    binding.recentExpLine3.visibility = View.GONE
                                    binding.recentExpLine4.visibility = View.GONE
                                    binding.recentExpLine5.visibility = View.GONE
                                    binding.recentExpLine6.visibility = View.GONE
                                    binding.recentExpLine7.visibility = View.GONE
                                    binding.recentExpLine8.visibility = View.GONE
                                    binding.tvApplicantPreviousCompanyIndustry.visibility =
                                        View.GONE
                                } else {
                                    binding.tvApplicantPreviousJobTitle.text = applJobTitle
                                    binding.tvApplicantPreviousCompanyName.text = applCompanyName
                                    binding.tvApplicantPreviousJobStartDate.text = applStartDate
                                    binding.tvApplicantPreviousJobEndDate.text = applEndDate
                                    binding.tvApplicantPreviousCompanyIndustry.text =
                                        applCompanyIndustry
                                }
                            })
                    })
                })
            })
        })

        binding.rejectBtn.setOnClickListener {
            viewModel.rejectApplicant(appId, onSuccess = {
                Toast.makeText(
                    requireContext(),
                    "$applName is rejected successfully",
                    Toast.LENGTH_LONG
                ).show()
                requireActivity().finish()
                val intent = Intent(requireContext(), EmpApplicantsActivity::class.java)
                startActivity(intent)
            },
                onError = { errorMessage ->
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                })
        }

        binding.acceptBtn.setOnClickListener {
            val setInterviewFragment = EmpSetInterviewFragment()

            val bundle = Bundle()
            bundle.putString("jobId", jobId)
            bundle.putString("applId", applId)
            bundle.putString("appId", appId)
            bundle.putString("applName", applName)
            bundle.putString("empName", empName)
            bundle.putString("jobTitle", jobTitle)

            setInterviewFragment.arguments = bundle

            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, setInterviewFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return binding.root
    }
}