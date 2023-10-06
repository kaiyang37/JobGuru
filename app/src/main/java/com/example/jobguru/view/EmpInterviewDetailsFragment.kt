package com.example.jobguru.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.jobguru.R
import com.example.jobguru.databinding.FragmentEmpInterviewDetailsBinding
import com.example.jobguru.viewmodel.EmpInterviewDetailsViewModel

class EmpInterviewDetailsFragment : Fragment() {
    private lateinit var binding: FragmentEmpInterviewDetailsBinding
    private lateinit var viewModel: EmpInterviewDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmpInterviewDetailsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(EmpInterviewDetailsViewModel::class.java)

        binding.upButton.setOnClickListener {
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack()
        }

        val intvwId = arguments?.getString("intvwId") ?: ""
        val jobId = arguments?.getString("jobId") ?: ""
        val applId = arguments?.getString("applId") ?: ""
        val applName = arguments?.getString("applName") ?: ""
        val empName = arguments?.getString("empName") ?: ""
        val jobTitle = arguments?.getString("jobTitle") ?: ""

        viewModel.getInterviewData(
            intvwId
        )

        viewModel.jobTitle.observe(requireActivity(), { jobTitle ->
            binding.tvJobTitle.text = jobTitle
        })
        viewModel.applName.observe(requireActivity(), { applName ->
            binding.tvApplicantName.text = applName
        })
        viewModel.intvwrName.observe(requireActivity(), { intvwrName ->
            binding.tvInterviewerName.text = intvwrName
        })
        viewModel.intvwDate.observe(requireActivity(), { intvwDate ->
            binding.tvInterviewDate.text = intvwDate
        })
        viewModel.intvwTime.observe(requireActivity(), { intvwTime ->
            binding.tvInterviewTime.text = intvwTime
        })
        viewModel.intvwPlatform.observe(requireActivity(), { intvwPlatform ->
            if (intvwPlatform == "Company Office" || intvwPlatform == "Conference Room") {
                binding.interviewVenueTitle.visibility = View.VISIBLE
            } else {
                binding.interviewPlatformTitle.visibility = View.VISIBLE
            }
            binding.tvInterviewPlatform.text = intvwPlatform
        })
        viewModel.intvwReason.observe(requireActivity(), { intvwReason ->
            binding.tvInterviewRejectedReason.text = intvwReason
        })
        viewModel.intvwStatus.observe(requireActivity(), { intvwStatus ->
            viewModel.intvwIsResend.observe(requireActivity(), { intvwIsResend ->
                binding.tvInterviewStatus.text = intvwStatus

                if (intvwStatus.equals("Rejected", ignoreCase = true)) {
                    binding.rejectedReasonTitle.visibility = View.VISIBLE
                    binding.tvInterviewRejectedReason.visibility = View.VISIBLE
                    if(!intvwIsResend) {
                        binding.resendView.visibility = View.VISIBLE
                        binding.resendInvitationBtn.visibility = View.VISIBLE
                    }
                }
            })
        })

        binding.resendInvitationBtn.setOnClickListener {

            openSetInterviewFragment(intvwId, jobId, applId, applName, empName, jobTitle)
        }

        return binding.root
    }

    private fun openSetInterviewFragment(
        intvwId: String,
        jobId: String,
        applId: String,
        applName: String,
        empName: String,
        jobTitle: String
    ) {
        val empSetInterviewFragment = EmpSetInterviewFragment()

        val bundle = Bundle()
        bundle.putString("intvwId", intvwId)
        bundle.putString("jobId", jobId)
        bundle.putString("applId", applId)
        bundle.putString("applName", applName)
        bundle.putString("empName", empName)
        bundle.putString("jobTitle", jobTitle)
        bundle.putBoolean("isResend", true)

        empSetInterviewFragment.arguments = bundle

        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, empSetInterviewFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}