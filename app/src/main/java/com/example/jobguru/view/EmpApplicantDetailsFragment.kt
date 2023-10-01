package com.example.jobguru.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        //viewModel = ViewModelProvider(this).get(EmpApplicantDetailsViewModel::class.java)

        val sharedPreferences =
            requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val delimitedJobIds = sharedPreferences.getString("myJobIdList", "") ?: ""
        viewModel = ViewModelProvider(this).get(EmpApplicantDetailsViewModel::class.java)

        binding.upButton.setOnClickListener {
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack()
        }


        val applId = arguments?.getString("applId") ?: ""
        var applName = ""
        viewModel.getApplicantData(
            applId
        )

        viewModel.applFirstName.observe(requireActivity(), { applFirstName ->
            viewModel.applLastName.observe(requireActivity(), { applLastName ->
                binding.tvApplicantName.text = applFirstName + " " + applLastName
                applName = applFirstName + " " + applLastName
                //binding.tvJobTitle.text = applFirstName
                //jTitle = applFirstName
            })

            viewModel.applGender.observe(requireActivity(), { applGender ->
                binding.tvApplicantGender.text = applGender
                //jSpecialization = jobSpecialization
            })
            viewModel.applEmail.observe(requireActivity(), { applEmail ->
                binding.tvApplicantEmail.text = applEmail
                //jYearOfExp = jobYearOfExp
            })
            viewModel.applLiveIn.observe(requireActivity(), { applLiveIn ->
                binding.tvApplicantLiveIn.text = applLiveIn
                //jDesc = jobDesc
            })
            viewModel.applAreaCode.observe(requireActivity(), { applAreaCode ->
                viewModel.applPhoneNumber.observe(requireActivity(), { applPhoneNumber ->
                    binding.tvApplicantPhone.text = applAreaCode + " " + applPhoneNumber
                    //jWorkState = jobWorkState
                })

                viewModel.applNationality.observe(requireActivity(), { applNationality ->
                    binding.tvApplicantNationality.text = applNationality
                    // jMaxSalary = jobMaxSalary
                })


                viewModel.applMinimumMonthlySalary.observe(
                    requireActivity(),
                    { applMinimumMonthlySalary ->
                        binding.tvApplicantExpectedSalary.text =
                            String.format("%.2f", applMinimumMonthlySalary)
                        //jYearOfExp = jobYearOfExp
                    })
                viewModel.applEducationLevel.observe(requireActivity(), { applEducationLevel ->
                    binding.tvApplicantEducationLvl.text = applEducationLevel
                    //jDesc = jobDesc
                })
                viewModel.applInstitute.observe(requireActivity(), { applInstitute ->
                    binding.tvApplicantInstitute.text = applInstitute
                    //jWorkState = jobWorkState
                })
                viewModel.applFieldOfStudies.observe(requireActivity(), { applFieldOfStudies ->
                    binding.tvApplicantFieldOfStudies.text = applFieldOfStudies
                    // jMinSalary = jobMinSalary
                })
                viewModel.applLocation.observe(requireActivity(), { applLocation ->
                    binding.tvApplicantInstituteLocation.text = applLocation
                    // jMaxSalary = jobMaxSalary
                })

                viewModel.applYearOfGraduation.observe(requireActivity(), { applYearOfGraduation ->
                    binding.tvApplicantYearOfGraduation.text = applYearOfGraduation
                    //jYearOfExp = jobYearOfExp
                })
                viewModel.applMonthOfGraduation.observe(
                    requireActivity(),
                    { applMonthOfGraduation ->
                        binding.tvApplicantMonthOfGraduation.text = applMonthOfGraduation
                        //jDesc = jobDesc
                    })
                viewModel.applInstitute.observe(requireActivity(), { applInstitute ->
                    binding.tvApplicantInstitute.text = applInstitute
                    //jWorkState = jobWorkState
                })
                viewModel.applFieldOfStudies.observe(requireActivity(), { applFieldOfStudies ->
                    binding.tvApplicantFieldOfStudies.text = applFieldOfStudies
                    // jMinSalary = jobMinSalary
                })
                viewModel.applLocation.observe(requireActivity(), { applLocation ->
                    binding.tvApplicantInstituteLocation.text = applLocation
                    // jMaxSalary = jobMaxSalary
                })

            })
        })

        binding.rejectBtn.setOnClickListener {
            viewModel.rejectApplicant(applId, delimitedJobIds)
            requireActivity().finish()
            val intent = Intent(requireContext(), EmpJobsActivity::class.java)
            startActivity(intent)
        }

        binding.acceptBtn.setOnClickListener {
            val setInterviewFragment = EmpSetInterviewFragment()

            val bundle = Bundle()
            bundle.putString("applId", applId)
            bundle.putString("applName", applName)
            bundle.putString("delimitedJobIds", delimitedJobIds)

            setInterviewFragment.arguments = bundle

            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, setInterviewFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return binding.root
    }
}