package com.example.jobguru.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
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
import com.example.jobguru.databinding.FragmentEmpAddNewJobBinding
import com.example.jobguru.databinding.FragmentEmpSetInterviewBinding
import com.example.jobguru.view.EmpInterviewActivity
import com.example.jobguru.view.EmpJobsActivity
import com.example.jobguru.viewmodel.EmpSetInterviewViewModel
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EmpSetInterviewFragment : Fragment() {
    private lateinit var binding: FragmentEmpSetInterviewBinding
    private lateinit var viewModel: EmpSetInterviewViewModel
    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_emp_set_interview, container, false)

        binding = FragmentEmpSetInterviewBinding.bind(view)
        viewModel = ViewModelProvider(this).get(EmpSetInterviewViewModel::class.java)

        binding.upButton.setOnClickListener {
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack()
        }

        binding.interviewDate.setOnClickListener {
            showDatePickerDialog()
        }

        binding.interviewTime.setOnClickListener {
            showTimePickerDialog()
        }

        binding.sendInvitationBtn.setOnClickListener {
            val interviewerName = binding.interviewerNameField.text.toString()

            //val arguments = arguments
            val intvwId = arguments?.getString("intvwId")?:""
            val applName = arguments?.getString("applName")?:""
            val applId = arguments?.getString("applId")?:""
            val delimitedJobIds = arguments?.getString("delimitedJobIds")?:""

            val selectedDate = viewModel.selectedDate
            val selectedTime = viewModel.selectedTime


            val jobTitle = viewModel.getJobId(applId, delimitedJobIds)

            viewModel.saveInterviewData(
                intvwId,
                applName,
                jobTitle,
                interviewerName,
                selectedDate, selectedTime,
                onSuccess = {
                    Toast.makeText(
                        requireContext(),
                        "Data inserted successfully",
                        Toast.LENGTH_LONG
                    ).show()
                    binding.interviewerNameField.text.clear()
                    requireActivity().finish()
                    val intent = Intent(requireContext(), EmpInterviewActivity::class.java)
                    startActivity(intent)
                },
                onError = { errorMessage ->
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                }
            )
        }

        interviewPlatformSpinner()

        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        return view
    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                // Handle the selected date
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                // Format the selected date as needed
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val selectedDate = dateFormat.format(calendar.time)

                viewModel.selectedDate = selectedDate
                binding.interviewDate.text = selectedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        // Show the date picker dialog
        datePicker.show()
    }

    private fun showTimePickerDialog() {
        val timePicker = TimePickerDialog(
            requireContext(),
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                // Handle the selected time
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

                // Format the selected time as needed
                val timeFormat = SimpleDateFormat("HH:mm", Locale.US)
                val selectedTime = timeFormat.format(calendar.time)

                viewModel.selectedTime = selectedTime
                binding.interviewTime.text = selectedTime

            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true // 24-hour format
        )

        // Show the time picker dialog
        timePicker.show()
    }

    private fun interviewPlatformSpinner() {
        val platformAdapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                viewModel.platforms
            )
        platformAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.interviewPlatformSpinner.adapter = platformAdapter

        binding.interviewPlatformSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>,
                    selectedItemView: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.selectedPlatform = viewModel.platforms[position]
                }

                override fun onNothingSelected(parentView: AdapterView<*>) {
                    viewModel.selectedPlatform = ""
                }
            }
    }
}