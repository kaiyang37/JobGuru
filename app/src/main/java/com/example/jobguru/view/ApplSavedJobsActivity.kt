package com.example.jobguru.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.jobguru.R
import com.example.jobguru.databinding.ActivityApplSavedJobsBinding

class ApplSavedJobsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityApplSavedJobsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApplSavedJobsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bottomNavigationBar()

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val loginEmail = sharedPreferences.getString("loginEmail", "")

        if(loginEmail.isNullOrEmpty()){
            binding.actionButton.visibility = View.VISIBLE
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

    private fun bottomNavigationBar() {
        binding.bottomNavigationView.setSelectedItemId(R.id.saved_jobs)
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
                    true
                }

                R.id.profile -> {
                    val intent = Intent(applicationContext, ApplProfileActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }

                else -> false
            }
        }
    }
}