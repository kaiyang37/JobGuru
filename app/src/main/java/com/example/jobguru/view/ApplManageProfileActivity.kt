package com.example.jobguru.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.jobguru.R
import com.example.jobguru.databinding.ActivityApplManageProfileBinding
import com.example.jobguru.viewmodel.ApplManageProfileViewModel


class ApplManageProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityApplManageProfileBinding
    private lateinit var viewModel: ApplManageProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApplManageProfileBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(ApplManageProfileViewModel::class.java)
        setContentView(binding.root)

        binding.upBtn.setOnClickListener {
            finish()
        }

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val applId = sharedPreferences.getString("applId", "")

        if (applId != null) {
            viewModel.getApplicantData(applId)
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

        viewModel.applEmail.observe(this) { applEmail ->
            binding.applEmail.text = applEmail
        }

        viewModel.applPhoneNum.observe(this) { applPhoneNum ->
            binding.applPhoneNum.text = applPhoneNum
        }

        viewModel.applCompanyName.observe(this) { applCompanyName ->
            if (applCompanyName.isEmpty()) {
                binding.applExpCompany.text = "Fresh graduate"
            } else {
                binding.applExpCompany.text = applCompanyName
            }
        }

        viewModel.applStartDate.observe(this){applStartDate ->
            if(!applStartDate.isEmpty()){
                binding.applExpDate.visibility = View.VISIBLE
                binding.applExpDate.text = applStartDate
            }
        }

        viewModel.applInstitute.observe(this){applInstitute ->
            binding.applInstitute.text = applInstitute
        }

        viewModel.applYearOfGraduation.observe(this){applYear ->
            binding.applGraduateYear.text = applYear
        }

        viewModel.applEducationLevel.observe(this){applEduLvl ->
            binding.applEducationLvl.text = applEduLvl
        }

        viewModel.applLiveIn.observe(this){applLiveIn ->
            binding.applLiveIn.text = applLiveIn
        }

        viewModel.applNationality.observe(this){applNation ->
            binding.applNationality.text = applNation
        }

        viewModel.applExpectedSalary.observe(this){applSalary ->
            binding.applExpectedSalary.text = applSalary
        }
    }
}