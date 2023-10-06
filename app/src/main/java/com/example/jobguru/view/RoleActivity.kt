package com.example.jobguru.view

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.jobguru.R
import com.example.jobguru.databinding.ActivityRoleBinding

class RoleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRoleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoleBinding.inflate(layoutInflater)

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val exploreJobsSelected = sharedPreferences.getBoolean("exploreJobsSelected", false)
        val loginEmail = sharedPreferences.getString("loginEmail", "")
        val empEmail = sharedPreferences.getString("personInChargeEmail", "")

        if (exploreJobsSelected || !loginEmail.isNullOrEmpty()) {
            startActivity(Intent(this, ApplHomeActivity::class.java))
            finish()
        }

        if(!empEmail.isNullOrEmpty()){
            startActivity(Intent(this, EmpJobsActivity::class.java))
            finish()
        }

        setContentView(binding.root)

        // Set the initial text style of applRadioBtn
        binding.applRadioBtn.setTypeface(null, Typeface.BOLD)

        binding.roleRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.applRadioBtn) {
                // Show applicant related buttons
                binding.exploreJobsBtn.visibility = View.VISIBLE
                binding.applLoginBtn.visibility = View.VISIBLE
                binding.applSignUpBtn.visibility = View.VISIBLE

                // Hide employer related buttons
                binding.empLoginBtn.visibility = View.GONE
                binding.empSignUpBtn.visibility = View.GONE

                // Set the text style of applRadioButton to bold
                binding.applRadioBtn.setTypeface(null, Typeface.BOLD)
                // Set the text style of empRadioButton to normal
                binding.empRadioBtn.setTypeface(null, Typeface.NORMAL)

            } else if (checkedId == R.id.empRadioBtn) {
                // Show employer related buttons
                binding.empLoginBtn.visibility = View.VISIBLE
                binding.empSignUpBtn.visibility = View.VISIBLE

                // Hide applicant related buttons
                binding.exploreJobsBtn.visibility = View.GONE
                binding.applLoginBtn.visibility = View.GONE
                binding.applSignUpBtn.visibility = View.GONE

                // Set the text style of empRadioButton to bold
                binding.empRadioBtn.setTypeface(null, Typeface.BOLD)
                // Set the text style of applRadioButton to normal
                binding.applRadioBtn.setTypeface(null, Typeface.NORMAL)
            }
        }

        binding.exploreJobsBtn.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.putBoolean("exploreJobsSelected", true)
            editor.apply()
            startActivity(Intent(this, ApplHomeActivity::class.java))
            finish()
        }

        binding.applLoginBtn.setOnClickListener {
            startActivity(Intent(this, ApplLoginActivity::class.java))
        }

        binding.applSignUpBtn.setOnClickListener {
            startActivity(Intent(this, ApplSignUpActivity::class.java))
        }


        binding.empLoginBtn.setOnClickListener {
            startActivity(Intent(this, EmpLoginActivity::class.java))
        }

        binding.empSignUpBtn.setOnClickListener {
            val EmpSignUpFragment = EmpSignUpFragment()

            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, EmpSignUpFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
}