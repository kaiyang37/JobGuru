package com.example.jobguru.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.jobguru.R
import com.example.jobguru.databinding.ActivityApplMainBinding
import com.example.jobguru.viewmodel.ApplMainViewModel

private lateinit var binding: ActivityApplMainBinding
private lateinit var viewModel: ApplMainViewModel

class ApplMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityApplMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(ApplMainViewModel::class.java)

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val loginEmail = sharedPreferences.getString("loginEmail", "")

        var observedApplId: String = ""
        viewModel.getApplId(loginEmail)
        viewModel.applId.observe(this) { applId ->
            observedApplId = applId
            val editor = sharedPreferences.edit()
            editor.putString("applId", observedApplId)
            editor.apply()
            Log.d("MyApp", "Email: $observedApplId")
        }

        replaceFragment(ApplHomeFragment())

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(ApplHomeFragment())
                R.id.search -> replaceFragment(ApplSearchFragment())
                R.id.saved_jobs -> replaceFragment(ApplSavedJobsFragment())
                R.id.profile -> replaceFragment(ApplProfileFragment())
                else -> {

                }
            }
            true
        }

//        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
//
//        val selectedState = sharedPreferences.getString("selectedLocation", "")
//        val selectedSpec = sharedPreferences.getString("selectedSpec", "")
//        val selectedSalary = sharedPreferences.getString("selectedSalary", "")
//        val loginEmail = sharedPreferences.getString("loginEmail", "")
//
//        val displayText = "Location: $selectedState\nSpecialization: $selectedSpec\nSalary: $selectedSalary\n" +
//                "Email: $loginEmail"
//        binding.textViewDisplayData.text = displayText
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}