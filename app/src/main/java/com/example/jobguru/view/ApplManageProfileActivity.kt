package com.example.jobguru.view

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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
//TO Add
//            finish()
//            val intent = Intent(this, ApplProfileActivity::class.java)
//            startActivity(intent)
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

        viewModel.applFirstName.observe(this) { applFirstName ->
            viewModel.applLastName.observe(this) { applLastName ->
                binding.applName.text = "$applFirstName $applLastName"
            }
        }

        viewModel.applEmail.observe(this) { applEmail ->
            binding.applEmail.text = applEmail
        }

        viewModel.applAreaCode.observe(this) { applAreaCode ->
            viewModel.applPhoneNum.observe(this) { applPhoneNum ->
                val stringBuilder = StringBuilder()
                stringBuilder.append("+")
                stringBuilder.append(applAreaCode?.replace(Regex("[^\\d]"), ""))
                stringBuilder.append(" ")
                stringBuilder.append(applPhoneNum)
                binding.applPhoneNum.text = stringBuilder.toString()
            }
        }


        viewModel.applCompanyName.observe(this) { applCompanyName ->
            if (applCompanyName.isEmpty()) {
                binding.applExpCompany.text = "Fresh graduate"
            } else {
                binding.applExpCompany.text = applCompanyName
            }
        }

        viewModel.applStartDate.observe(this) { applStartDate ->
            viewModel.applEndDate.observe(this) { applEndDate ->
                if (!applStartDate.isEmpty() && !applEndDate.isEmpty()) {
                    binding.applExpDate.visibility = View.VISIBLE
                    binding.applExpDate.text = "$applStartDate - $applEndDate"
                }
            }
        }

        viewModel.applInstitute.observe(this) { applInstitute ->
            binding.applInstitute.text = applInstitute
        }

        viewModel.applYearOfGraduation.observe(this) { applYear ->
            binding.applGraduateYear.text = applYear
        }

        viewModel.applEducationLevel.observe(this) { applEduLvl ->
            viewModel.applFieldOfStudies.observe(this) { applFieldOfStudies ->
                binding.applEducationLvl.text = "$applEduLvl in $applFieldOfStudies"
            }
        }

        viewModel.applLiveIn.observe(this) { applLiveIn ->
            binding.applLiveIn.text = applLiveIn
        }

        viewModel.applNationality.observe(this) { applNation ->
            binding.applNationality.text = applNation
        }

        viewModel.applExpectedSalary.observe(this) { applSalary ->
            binding.applExpectedSalary.text = applSalary

        }

        binding.editProfileBtn.setOnClickListener {
            viewModel.applFirstName.observe(this) { applFirstName ->
                viewModel.applLastName.observe(this) { applLastName ->
                    viewModel.applEmail.observe(this) { applEmail ->
                        viewModel.applAreaCode.observe(this) { applAreaCode ->
                            viewModel.applPhoneNum.observe(this) { applPhoneNum ->
                                viewModel.applGender.observe(this) { applGender ->
                                    if (applId != null) {
                                        openEditProfileFragment(
                                            applId,
                                            applFirstName,
                                            applLastName,
                                            applEmail,
                                            applAreaCode,
                                            applPhoneNum,
                                            applGender
                                        )
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }

        binding.editExperienceBtn.setOnClickListener {
            viewModel.applJobTitle.observe(this) { applJobTitle ->
                viewModel.applCompanyName.observe(this) { applCompanyName ->
                    viewModel.applStartDate.observe(this) { applStartDate ->
                        viewModel.applEndDate.observe(this) { applEndDate ->
                            viewModel.applCompanyIndustry.observe(this) { applCompanyIndustry ->
                                if (applId != null) {
                                    openEditExperienceFragment(
                                        applId,
                                        applJobTitle,
                                        applCompanyName,
                                        applStartDate,
                                        applEndDate,
                                        applCompanyIndustry
                                    )
                                }
                            }
                        }
                    }

                }
            }
        }

        binding.editEducationBtn.setOnClickListener {
            viewModel.applEducationLevel.observe(this) { applEducationLevel ->
                viewModel.applInstitute.observe(this) { applInstitute ->
                    viewModel.applFieldOfStudies.observe(this) { applFieldOfStudies ->
                        viewModel.applLocation.observe(this) { applLocation ->
                            viewModel.applYearOfGraduation.observe(this) { applYearOfGraduation ->
                                viewModel.applMonthOfGraduation.observe(this) { applMonthOfGraduation ->
                                    if (applId != null) {
                                        openEditEducationFragment(
                                            applId,
                                            applEducationLevel,
                                            applInstitute,
                                            applFieldOfStudies,
                                            applLocation,
                                            applYearOfGraduation,
                                            applMonthOfGraduation
                                        )
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }

        binding.editCurrentStatusBtn.setOnClickListener {
            viewModel.applLiveIn.observe(this) { applLiveIn ->
                viewModel.applNationality.observe(this) { applNationality ->
                    viewModel.applExpectedSalary.observe(this) { applExpectedSalary ->
                        if (applId != null) {
                            openEditCurrentStatusFragment(
                                applId,
                                applLiveIn,
                                applNationality,
                                applExpectedSalary
                            )
                        }
                    }

                }
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun openEditProfileFragment(
        applId: String,
        firstName: String,
        lastName: String,
        email: String,
        areaCode: String,
        phoneNum: String,
        gender: String
    ) {
        val applEditProfileFragment = ApplEditProfileFragment()

        val bundle = Bundle()
        bundle.putString("applId", applId)
        bundle.putString("firstName", firstName)
        bundle.putString("lastName", lastName)
        bundle.putString("email", email)
        bundle.putString("areaCode", areaCode)
        bundle.putString("phoneNum", phoneNum)
        bundle.putString("gender", gender)

        applEditProfileFragment.arguments = bundle
        replaceFragment(applEditProfileFragment)
    }

    private fun openEditExperienceFragment(
        applId: String,
        jobTitle: String,
        companyName: String,
        startDate: String,
        endDate: String,
        companyIndustry: String
    ) {
        val applEditExperienceFragment = ApplEditExperienceFragment()

        val bundle = Bundle()
        bundle.putString("applId", applId)
        bundle.putString("jobTitle", jobTitle)
        bundle.putString("companyName", companyName)
        bundle.putString("startDate", startDate)
        bundle.putString("endDate", endDate)
        bundle.putString("companyIndustry", companyIndustry)

        applEditExperienceFragment.arguments = bundle
        replaceFragment(applEditExperienceFragment)
    }

    private fun openEditEducationFragment(
        applId: String,
        educationLvl: String,
        institute: String,
        fieldOfStudies: String,
        location: String,
        yearOfGraduation: String,
        monthOfGraduation: String
    ) {
        val applEditEducationFragment = ApplEditEducationFragment()

        val bundle = Bundle()
        bundle.putString("applId", applId)
        bundle.putString("educationLvl", educationLvl)
        bundle.putString("institute", institute)
        bundle.putString("fieldOfStudies", fieldOfStudies)
        bundle.putString("location", location)
        bundle.putString("yearOfGraduation", yearOfGraduation)
        bundle.putString("monthOfGraduation", monthOfGraduation)

        applEditEducationFragment.arguments = bundle
        replaceFragment(applEditEducationFragment)
    }

    private fun openEditCurrentStatusFragment(
        applId: String,
        liveIn: String,
        nationality: String,
        salary: String,
    ) {
        val applEditCurrentStatusFragment = ApplEditCurrentStatusFragment()

        val bundle = Bundle()
        bundle.putString("applId", applId)
        bundle.putString("liveIn", liveIn)
        bundle.putString("nationality", nationality)
        bundle.putString("salary", salary)

        applEditCurrentStatusFragment.arguments = bundle
        replaceFragment(applEditCurrentStatusFragment)
    }

}