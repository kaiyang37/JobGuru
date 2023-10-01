package com.example.jobguru.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.jobguru.view.EmpApplicantsActivity
import com.example.jobguru.view.EmpProfileActivity
import com.example.jobguru.R
import com.example.jobguru.databinding.FragmentEmpNavigationBinding
import com.example.jobguru.databinding.FragmentEmpSignUpBinding

class EmpNavigationFragment : Fragment() {
    private lateinit var binding: FragmentEmpNavigationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmpNavigationBinding.inflate(inflater, container, false)

        binding.bottomNavigationView.setOnNavigationItemSelectedListener {item->
            when (item.itemId) {
                R.id.jobs -> {
                    //requireActivity().finish()
                    val intent = Intent(requireContext(), EmpJobsActivity::class.java)
                    startActivity(intent)
                    requireActivity().overridePendingTransition(0,0)
                    true
                }
                R.id.applicants -> {
                    //requireActivity().finish()
                    val intent = Intent(requireContext(), EmpApplicantsActivity::class.java)
                    startActivity(intent)
                    requireActivity().overridePendingTransition(0,0)
                   binding.bottomNavigationView.selectedItemId = R.id.applicants
                    true
                }
                R.id.interviews -> {
                   // requireActivity().finish()
                    val intent = Intent(requireContext(), EmpInterviewActivity::class.java)
                    startActivity(intent)
                    requireActivity().overridePendingTransition(0,0)
                    binding.bottomNavigationView.selectedItemId = R.id.interviews
                    true
                }
                R.id.profile -> {
                    //requireActivity().finish()
                    val intent = Intent(requireContext(), EmpProfileActivity::class.java)
                    startActivity(intent)
                    requireActivity().overridePendingTransition(0,0)
                   binding.bottomNavigationView.selectedItemId = R.id.profile
                    true
                }
                else -> false
            }
        }

        return binding.root
    }

}