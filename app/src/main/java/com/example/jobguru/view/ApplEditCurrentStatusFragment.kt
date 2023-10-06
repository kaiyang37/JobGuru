package com.example.jobguru.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.example.jobguru.R
import com.example.jobguru.databinding.FragmentApplEditCurrentStatusBinding
import com.example.jobguru.viewmodel.ApplEditCurrentStatusViewModel

class ApplEditCurrentStatusFragment : Fragment() {
    private lateinit var binding: FragmentApplEditCurrentStatusBinding
    private lateinit var viewModel: ApplEditCurrentStatusViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentApplEditCurrentStatusBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(ApplEditCurrentStatusViewModel::class.java)
        liveInSpinner()
        nationalitySpinner()
        salarySpinner()

        binding.cancelBtn.setOnClickListener {
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack()
        }

        val applId = arguments?.getString("applId").toString()

        val liveInPosition = viewModel.liveIns.indexOf(arguments?.getString("liveIn"))
        if (liveInPosition != -1) {
            binding.liveInSpinner.setSelection(liveInPosition)
        }

        val nationalityPosition = viewModel.nationalities.indexOf(arguments?.getString("nationality"))
        if (nationalityPosition != -1) {
            binding.nationalitySpinner.setSelection(nationalityPosition)
        }

        val salaryPosition = viewModel.salaries.indexOf(arguments?.getString("salary"))
        if (salaryPosition != -1) {
            binding.salarySpinner.setSelection(salaryPosition)
        }

        binding.saveBtn.setOnClickListener {

            viewModel.updateCurrentStatusData(applId,
                onSuccess = {
                    Toast.makeText(
                        requireContext(),
                        "Current Status is updated successfully",
                        Toast.LENGTH_LONG
                    ).show()
                    val intent = Intent(requireContext(), ApplManageProfileActivity::class.java)
                    startActivity(intent)
                },
                onError = { errorMessage ->
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                })

        }

        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        return binding.root
    }

    private fun liveInSpinner() {
        val liveInAdapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                viewModel.liveIns
            )
        liveInAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.liveInSpinner.adapter = liveInAdapter

        binding.liveInSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>,
                    selectedItemView: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.selectedLiveIn = viewModel.liveIns[position]
                }

                override fun onNothingSelected(parentView: AdapterView<*>) {
                    viewModel.selectedLiveIn = ""
                }
            }
    }

    private fun nationalitySpinner() {
        val nationalityAdapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                viewModel.nationalities
            )
        nationalityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.nationalitySpinner.adapter = nationalityAdapter

        binding.nationalitySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>,
                    selectedItemView: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.selectedNationality = viewModel.nationalities[position]
                }

                override fun onNothingSelected(parentView: AdapterView<*>) {
                    viewModel.selectedNationality = ""
                }
            }
    }

    private fun salarySpinner() {
        val salaryAdapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                viewModel.salaries
            )
        salaryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.salarySpinner.adapter = salaryAdapter

        binding.salarySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>,
                    selectedItemView: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.selectedSalaries = viewModel.salaries[position]
                }

                override fun onNothingSelected(parentView: AdapterView<*>) {
                    viewModel.selectedSalaries = ""
                }
            }
    }
}