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

        val view = inflater.inflate(R.layout.fragment_emp_interview_details, container, false)

        binding = FragmentEmpInterviewDetailsBinding.bind(view)
        viewModel =
            ViewModelProvider(requireActivity()).get(EmpInterviewDetailsViewModel::class.java)

        binding.upButton.setOnClickListener {
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack()
        }

        val arguments = arguments
        viewModel.initializeWithData(
            arguments?.getString("intvwId") ?: "",
            arguments?.getString("applName") ?: "",
            arguments?.getString("jobTitle") ?: "",
            arguments?.getString("intvwrName") ?: "",
            arguments?.getString("intvwDate") ?: "",
            arguments?.getString("intvwTime") ?: "",
            arguments?.getString("intvwPlatform") ?: "",
            arguments?.getString("intvwReason") ?: "",
            arguments?.getString("intvwStatus") ?: ""
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
            binding.tvInterviewPlatform.text = intvwPlatform
        })
        viewModel.intvwReason.observe(requireActivity(), { intvwReason ->
            binding.tvInterviewRejectedReason.text = intvwReason
        })
        viewModel.intvwStatus.observe(requireActivity(), { intvwStatus ->
            binding.tvInterviewStatus.text = intvwStatus

            if (!intvwStatus.equals("Rejected", ignoreCase = true)) {
                binding.rejectedReasonTitle.visibility = View.GONE
                binding.tvInterviewRejectedReason.visibility = View.GONE
                binding.resendView.visibility = View.GONE
                binding.resendInvitationBtn.visibility = View.GONE
            }
        })

        binding.resendInvitationBtn.setOnClickListener {
            val intvwId = viewModel.intvwId.value ?: ""
            val applicantName = viewModel.applName.value ?: ""
            val jobTitle = viewModel.jobTitle.value ?: ""

            openSetInterviewFragment(intvwId, applicantName, jobTitle)
        }

        return view
    }

    private fun openSetInterviewFragment(
        intvwId: String,
        applicantName: String,
        jobTitle: String,
    ) {
        val empSetInterviewFragment = EmpSetInterviewFragment()

        val bundle = Bundle()
        bundle.putString("intvwId", intvwId)
        bundle.putString("applName", applicantName)
        bundle.putString("jobTitle", jobTitle)

        empSetInterviewFragment.arguments = bundle

        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, empSetInterviewFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}