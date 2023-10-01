package com.example.jobguru.view

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.jobguru.view.EmpEditJobFragment
import com.example.jobguru.viewmodel.EmpJobDetailsViewModel
import com.example.jobguru.R
import com.example.jobguru.databinding.ActivityEmpJobDetailsBinding

class EmpJobDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmpJobDetailsBinding
    private lateinit var viewModel: EmpJobDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmpJobDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(EmpJobDetailsViewModel::class.java)

        binding.upButton.setOnClickListener {
            finish()
        }

        val jobId = intent.getStringExtra("jobId").toString()
        var jTitle = ""
        var jRole = ""
        var jSpecialization = ""
        var jYearOfExp = ""
        var jDesc = ""
        var jWorkState = ""
        var jMinSalary = 0.0
        var jMaxSalary = 0.0

        viewModel.getJobData(
            jobId
        )

        viewModel.jobTitle.observe(this, { jobTitle ->
            binding.tvMainJobTitle.text = jobTitle
            binding.tvJobTitle.text = jobTitle
            jTitle = jobTitle
        })

        viewModel.jobRole.observe(this, { jobRole ->
            binding.tvJobRole.text = jobRole
            jRole = jobRole
        })
        viewModel.jobSpecialization.observe(this, { jobSpecialization ->
            binding.tvJobSpecialization.text = jobSpecialization
            jSpecialization = jobSpecialization
        })
        viewModel.jobYearOfExp.observe(this, { jobYearOfExp ->
            binding.tvJobYearOfExp.text = jobYearOfExp
            jYearOfExp = jobYearOfExp
        })
        viewModel.jobDesc.observe(this, { jobDesc ->
            binding.tvJobDesc.text = jobDesc
            jDesc = jobDesc
        })
        viewModel.jobWorkState.observe(this, { jobWorkState ->
            binding.tvJobWorkState.text = jobWorkState
            jWorkState = jobWorkState
        })
        viewModel.jobMinSalary.observe(this, { jobMinSalary ->
            binding.tvJobMinSalary.text =
                String.format("%.2f", jobMinSalary)
            jMinSalary = jobMinSalary
        })
        viewModel.jobMaxSalary.observe(this, { jobMaxSalary ->
            binding.tvJobMaxSalary.text =
                String.format("%.2f", jobMaxSalary)
            jMaxSalary = jobMaxSalary
        })


        binding.editBtn.setOnClickListener {
            openEditJobFragment(
                jobId,
                jTitle,
                jRole,
                jSpecialization,
                jYearOfExp,
                jDesc,
                jWorkState,
                jMinSalary,
                jMaxSalary
            )

        }

        binding.removeBtn.setOnClickListener {

            val removeDialog = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val removeDialogView = inflater.inflate(R.layout.emp_job_remove_dialog, null)
            val removeBtn = removeDialogView.findViewById<Button>(R.id.remove_btn)
            val cancelBtn = removeDialogView.findViewById<TextView>(R.id.cancel_btn)
            var jTitle = ""

            viewModel.jobTitle.observe(this, { jobTitle ->
                val jobTitleTextView = removeDialogView.findViewById<TextView>(R.id.job_title)
                jobTitleTextView.text = jobTitle
                jTitle = jobTitle

            })

            removeDialog.setView(removeDialogView)

            val alertDialog = removeDialog.create()
            alertDialog.show()

            removeBtn.setOnClickListener {
                viewModel.removeRecord(
                    jobId,
                    onSuccess = {
                        Toast.makeText(this, "$jTitle has been removed", Toast.LENGTH_LONG).show()
                        finish()
                    },
                    onError = { errorMessage ->
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                    })
            }

            cancelBtn.setOnClickListener {
                alertDialog.dismiss()
            }
        }
    }

    private fun openEditJobFragment(
        jobId: String,
        jTitle: String,
        jRole: String,
        jSpecialization: String,
        jYearOfExp: String,
        jDesc: String,
        jWorkState: String,
        jMinSalary: Double,
        jMaxSalary: Double
    ) {
        val empEditJobFragment = EmpEditJobFragment()

        val bundle = Bundle()
        bundle.putString("jobId", jobId)
        bundle.putString("jobTitle", jTitle)
        bundle.putString("jobRole", jRole)
        bundle.putString("jobSpecialization", jSpecialization)
        bundle.putString("jobYearOfExp", jYearOfExp)
        bundle.putString("jobDesc", jDesc)
        bundle.putString("jobWorkState", jWorkState)
        bundle.putDouble("jobMinSalary", jMinSalary)
        bundle.putDouble("jobMaxSalary", jMaxSalary)


        empEditJobFragment.arguments = bundle

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, empEditJobFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}