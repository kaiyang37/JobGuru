package com.example.jobguru.view

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import com.example.jobguru.R
import com.example.jobguru.databinding.FragmentApplSpecBinding

class ApplSpecFragment : Fragment() {

    private lateinit var binding: FragmentApplSpecBinding
    private var checkedCount = 0
    private val maxCheckboxes = 5
    private val selectedSpec = mutableSetOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentApplSpecBinding.inflate(inflater, container, false)

        binding.upBtn.setOnClickListener { view ->
            requireActivity().supportFragmentManager.popBackStack()
        }

        updateContinueButtonState()

        // Define a single listener that can be reused for all checkboxes
        val checkboxListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            val checkbox = buttonView as CheckBox
            val specName = checkbox.text.toString()

            if (isChecked) {
                checkbox.setTypeface(null, Typeface.BOLD)
                checkedCount++
                if (checkedCount >= maxCheckboxes) {
                    disableUncheckedCheckboxes()
                }
                selectedSpec.add(specName)
            } else {
                checkbox.setTypeface(null, Typeface.NORMAL)
                checkedCount--
                if (checkedCount < maxCheckboxes) {
                    enableAllCheckboxes()
                }
                selectedSpec.remove(specName)
            }

            updateContinueButtonState()
        }

// Assign the listener to each checkbox
        binding.accFinCheckbox.setOnCheckedChangeListener(checkboxListener)
        binding.adminHRCheckbox.setOnCheckedChangeListener(checkboxListener)
        binding.artsMediaCommsCheckbox.setOnCheckedChangeListener(checkboxListener)
        binding.bldgConstrCheckbox.setOnCheckedChangeListener(checkboxListener)
        binding.compITCheckbox.setOnCheckedChangeListener(checkboxListener)
        binding.eduTrnCheckbox.setOnCheckedChangeListener(checkboxListener)
        binding.engCheckbox.setOnCheckedChangeListener(checkboxListener)
        binding.hltcareCheckbox.setOnCheckedChangeListener(checkboxListener)
        binding.hotelDormCheckbox.setOnCheckedChangeListener(checkboxListener)
        binding.mfgCheckbox.setOnCheckedChangeListener(checkboxListener)
        binding.restCheckbox.setOnCheckedChangeListener(checkboxListener)
        binding.salesMktgCheckbox.setOnCheckedChangeListener(checkboxListener)
        binding.sciCheckbox.setOnCheckedChangeListener(checkboxListener)
        binding.servCheckbox.setOnCheckedChangeListener(checkboxListener)

        binding.continueBtn.setOnClickListener {
            val selectedSpecString = selectedSpec.joinToString(",")
            saveSelectedSpec(selectedSpecString)

            val nextFragment = ApplSalaryFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, nextFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return binding.root
    }

    private fun updateContinueButtonState() {
        // Check if at least one checkbox is checked and the maximum limit is not exceeded
        if (checkedCount > 0) {
            binding.continueBtn.isEnabled = true
            binding.continueBtn.setBackgroundResource(R.drawable.filled_button)
        } else {
            binding.continueBtn.isEnabled = false
            binding.continueBtn.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    android.R.color.white
                )
            )
            binding.continueBtn.setBackgroundResource(R.drawable.grayed_button)
        }
    }

    // Function to disable unchecked checkboxes
    private fun disableUncheckedCheckboxes() {
        val checkboxesToDisable = listOf(
            binding.accFinCheckbox,
            binding.adminHRCheckbox,
            binding.artsMediaCommsCheckbox,
            binding.bldgConstrCheckbox,
            binding.compITCheckbox,
            binding.eduTrnCheckbox,
            binding.engCheckbox,
            binding.hltcareCheckbox,
            binding.hotelDormCheckbox,
            binding.mfgCheckbox,
            binding.restCheckbox,
            binding.salesMktgCheckbox,
            binding.sciCheckbox,
            binding.servCheckbox
        )

        for (checkbox in checkboxesToDisable) {
            if (!checkbox.isChecked) {
                checkbox.isEnabled = false
            }
        }
    }

    private fun enableAllCheckboxes() {
        val checkboxesToEnable = listOf(
            binding.accFinCheckbox,
            binding.adminHRCheckbox,
            binding.artsMediaCommsCheckbox,
            binding.bldgConstrCheckbox,
            binding.compITCheckbox,
            binding.eduTrnCheckbox,
            binding.engCheckbox,
            binding.hltcareCheckbox,
            binding.hotelDormCheckbox,
            binding.mfgCheckbox,
            binding.restCheckbox,
            binding.salesMktgCheckbox,
            binding.sciCheckbox,
            binding.servCheckbox
        )

        for (checkbox in checkboxesToEnable) {
            checkbox.isEnabled = true
        }
    }

    private fun saveSelectedSpec(selectedSpecString: String) {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("selectedSpec", selectedSpecString)
        editor.apply()
    }


}