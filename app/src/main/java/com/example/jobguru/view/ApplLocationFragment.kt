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
import com.example.jobguru.databinding.FragmentApplLocationBinding

class ApplLocationFragment : Fragment() {

    private lateinit var binding: FragmentApplLocationBinding
    private var checkedCount = 0
    private val maxCheckboxes = 5
    private val selectedLocation = mutableSetOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentApplLocationBinding.inflate(inflater, container, false)

        updateContinueButtonState()

        // Define a single listener that can be reused for all checkboxes
        val checkboxListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            val checkbox = buttonView as CheckBox
            val stateName = checkbox.text.toString()

            if (isChecked) {
                checkbox.setTypeface(null, Typeface.BOLD)
                checkedCount++
                if (checkedCount >= maxCheckboxes) {
                    disableUncheckedCheckboxes()
                }
                selectedLocation.add(stateName)
            } else {
                checkbox.setTypeface(null, Typeface.NORMAL)
                checkedCount--
                if (checkedCount < maxCheckboxes) {
                    enableAllCheckboxes()
                }
                selectedLocation.remove(stateName)
            }

            updateContinueButtonState()
        }

// Assign the listener to each checkbox
        binding.jhrCheckbox.setOnCheckedChangeListener(checkboxListener)
        binding.kdhCheckbox.setOnCheckedChangeListener(checkboxListener)
        binding.ktnCheckbox.setOnCheckedChangeListener(checkboxListener)
        binding.kulCheckbox.setOnCheckedChangeListener(checkboxListener)
        binding.mlkCheckbox.setOnCheckedChangeListener(checkboxListener)
        binding.nsnCheckbox.setOnCheckedChangeListener(checkboxListener)
        binding.phgCheckbox.setOnCheckedChangeListener(checkboxListener)
        binding.pngCheckbox.setOnCheckedChangeListener(checkboxListener)
        binding.prkCheckbox.setOnCheckedChangeListener(checkboxListener)
        binding.plsCheckbox.setOnCheckedChangeListener(checkboxListener)
        binding.sbhCheckbox.setOnCheckedChangeListener(checkboxListener)
        binding.swkCheckbox.setOnCheckedChangeListener(checkboxListener)
        binding.sgrCheckbox.setOnCheckedChangeListener(checkboxListener)
        binding.trgCheckbox.setOnCheckedChangeListener(checkboxListener)

        binding.continueBtn.setOnClickListener {
            val selectedLocationString = selectedLocation.joinToString(",")
            saveSelectedLocation(selectedLocationString)

            val nextFragment = ApplSpecFragment()
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
            binding.continueBtn.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            binding.continueBtn.setBackgroundResource(R.drawable.grayed_button)
        }
    }

    // Function to disable unchecked checkboxes
    private fun disableUncheckedCheckboxes() {
        val checkboxesToDisable = listOf(
            binding.jhrCheckbox,
            binding.kdhCheckbox,
            binding.ktnCheckbox,
            binding.kulCheckbox,
            binding.mlkCheckbox,
            binding.nsnCheckbox,
            binding.phgCheckbox,
            binding.pngCheckbox,
            binding.prkCheckbox,
            binding.plsCheckbox,
            binding.sbhCheckbox,
            binding.swkCheckbox,
            binding.sgrCheckbox,
            binding.trgCheckbox
        )

        for (checkbox in checkboxesToDisable) {
            if (!checkbox.isChecked) {
                checkbox.isEnabled = false
            }
        }
    }

    private fun enableAllCheckboxes() {
        val checkboxesToEnable = listOf(
            binding.jhrCheckbox,
            binding.kdhCheckbox,
            binding.ktnCheckbox,
            binding.kulCheckbox,
            binding.mlkCheckbox,
            binding.nsnCheckbox,
            binding.phgCheckbox,
            binding.pngCheckbox,
            binding.prkCheckbox,
            binding.plsCheckbox,
            binding.sbhCheckbox,
            binding.swkCheckbox,
            binding.sgrCheckbox,
            binding.trgCheckbox
        )

        for (checkbox in checkboxesToEnable) {
            checkbox.isEnabled = true
        }
    }

    private fun saveSelectedLocation(selectedLocationString: String) {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("selectedLocation", selectedLocationString)
        editor.apply()
    }
}