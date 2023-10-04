package com.example.jobguru.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.jobguru.R
import com.example.jobguru.databinding.FragmentApplSalaryBinding

class ApplSalaryFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: FragmentApplSalaryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentApplSalaryBinding.inflate(inflater, container, false)


        binding.upBtn.setOnClickListener { view ->
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.salarySpinner.onItemSelectedListener = this
        ArrayAdapter.createFromResource(
            requireContext(), R.array.salary_spinner, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.salarySpinner.adapter = adapter
        }

        binding.completeBtn.setOnClickListener {
            saveSelectedSalary(binding.salarySpinner.selectedItem.toString())
            val intent = Intent(requireActivity(), ApplHomeActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedValue = parent?.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    private fun saveSelectedSalary(selectedSalaryString: String) {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("selectedSalary", selectedSalaryString)
        editor.apply()
    }
}