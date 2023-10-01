package com.example.jobguru.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.jobguru.databinding.FragmentApplProfileBinding
import com.example.jobguru.viewmodel.ApplSubmitApplicationViewModel

class ApplProfileFragment : Fragment() {

    private lateinit var binding: FragmentApplProfileBinding
    private lateinit var viewModel: ApplSubmitApplicationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentApplProfileBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(ApplSubmitApplicationViewModel::class.java)

        binding.applicationHistoryBtn.setOnClickListener {
            val intent = Intent(requireContext(), ApplApplicationHistoryActivity::class.java)
            startActivity(intent)
        }

        binding.logOutBtn.setOnClickListener{
            val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()

            val intent = Intent(requireContext(), RoleActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        val sharedPreferences =
            requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val applId = sharedPreferences.getString("applId", "")
        val jobId = sharedPreferences.getString("applyJobId", "")

        if (applId != null) {
            viewModel.getApplicantData(applId)
        }

        if (jobId != null) {
            viewModel.getJobTitle(jobId)
        }


        viewModel.applGender.observe(viewLifecycleOwner) { applGender ->
            if (applGender.equals("Male")) {
                binding.maleAvatar.visibility = View.VISIBLE
            } else {
                binding.femaleAvatar.visibility = View.VISIBLE
            }
        }

        viewModel.applName.observe(viewLifecycleOwner) { applName ->
            binding.applName.text = applName
        }

        binding.manageProfileBtn.setOnClickListener {
            val intent = Intent(requireContext(), ApplManageProfileActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }


}