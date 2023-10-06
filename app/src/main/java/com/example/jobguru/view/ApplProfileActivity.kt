package com.example.jobguru.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.jobguru.R
import com.example.jobguru.databinding.ActivityApplProfileBinding
import com.example.jobguru.viewmodel.ApplProfileViewModel

class ApplProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityApplProfileBinding
    private lateinit var viewModel: ApplProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApplProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bottomNavigationBar()

        viewModel = ViewModelProvider(this).get(ApplProfileViewModel::class.java)

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val loginEmail = sharedPreferences.getString("loginEmail", "")
        if (loginEmail.isNullOrEmpty()) {
            binding.profileLogin.visibility = View.GONE
            binding.profileLogout.visibility = View.VISIBLE
        } else {
            binding.profileLogin.visibility = View.VISIBLE
            binding.profileLogout.visibility = View.GONE
        }

        binding.applicationHistoryBtn.setOnClickListener {
            val intent = Intent(this, ApplApplicationHistoryActivity::class.java)
            startActivity(intent)
        }

        binding.logOutBtn.setOnClickListener {
            val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()

            val intent = Intent(this, RoleActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        val applId = sharedPreferences.getString("applId", "")
        val jobId = sharedPreferences.getString("applyJobId", "")

        if (applId != null) {
            viewModel.getApplicantData(applId)
        }

        if (jobId != null) {
            viewModel.getJobTitle(jobId)
        }


        viewModel.applGender.observe(this) { applGender ->
            if (applGender.equals("Male")) {
                binding.maleAvatar.visibility = View.VISIBLE
            } else {
                binding.femaleAvatar.visibility = View.VISIBLE
            }
        }

        viewModel.applName.observe(this) { applName ->
            binding.applName.text = applName
        }

        binding.manageProfileBtn.setOnClickListener {
            if (applId != null) {
                viewModel.checkApplProfileStatus(applId)
            }

            viewModel.applProfileStatus.observe(this) { isProfileEmpty ->
                if (isProfileEmpty) {
                    showCompleteProfileDialog()
                } else {
                    val intent = Intent(this, ApplManageProfileActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        binding.loginBtn.setOnClickListener {
            val intent = Intent(this, ApplLoginActivity::class.java)
            startActivity(intent)
        }

        binding.signUpBtn.setOnClickListener {
            val intent = Intent(this, ApplSignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    private fun showCompleteProfileDialog() {
        val completeProfileDialog = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val completeProfileDialogView =
            inflater.inflate(R.layout.appl_complete_profile_dialog, null)
        val completeProfileBtn =
            completeProfileDialogView.findViewById<Button>(R.id.completeProfileBtn)
        val maybeLaterBtn = completeProfileDialogView.findViewById<TextView>(R.id.maybeLaterBtn)

        completeProfileDialog.setView(completeProfileDialogView)

        val alertDialog = completeProfileDialog.create()
        alertDialog.show()

        completeProfileBtn.setOnClickListener {
            alertDialog.dismiss()
            replaceFragment(ApplCompleteCurrentStatusFragment())
        }

        maybeLaterBtn.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun bottomNavigationBar() {
        binding.bottomNavigationView.setSelectedItemId(R.id.profile)
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val intent = Intent(applicationContext, ApplHomeActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.search -> {
                    val intent = Intent(applicationContext, ApplSearchActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.saved_jobs -> {
                    val intent = Intent(applicationContext, ApplSavedJobsActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.profile -> {
                    true
                }

                else -> false
            }
        }
    }
}