package com.example.jobguru.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jobguru.model.ApplicantModel
import com.example.jobguru.model.ApplyModel
import com.example.jobguru.model.JobModel
import com.example.jobguru.model.SaveJobModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class ApplJobDetailsViewModel : ViewModel() {
    private val _jobCompanyLogo = MutableLiveData<Bitmap>()
    val jobCompanyLogo: LiveData<Bitmap>
        get() = _jobCompanyLogo

    private val _jobCompanyName = MutableLiveData<String>()
    val jobCompanyName: LiveData<String>
        get() = _jobCompanyName

    private val _jobCompanyEmail = MutableLiveData<String>()
    val jobCompanyEmail: LiveData<String>
        get() = _jobCompanyEmail

    private val _jobTitle = MutableLiveData<String>()
    val jobTitle: LiveData<String>
        get() = _jobTitle

    private val _jobRole = MutableLiveData<String>()
    val jobRole: LiveData<String>
        get() = _jobRole

    private val _jobSpecialization = MutableLiveData<String>()
    val jobSpecialization: LiveData<String>
        get() = _jobSpecialization

    private val _jobYearOfExp = MutableLiveData<String>()
    val jobYearOfExp: LiveData<String>
        get() = _jobYearOfExp

    private val _jobDesc = MutableLiveData<String>()
    val jobDesc: LiveData<String>
        get() = _jobDesc

    private val _jobWorkState = MutableLiveData<String>()
    val jobWorkState: LiveData<String>
        get() = _jobWorkState

    private val _jobMinSalary = MutableLiveData<Double>()
    val jobMinSalary: LiveData<Double>
        get() = _jobMinSalary

    private val _jobMaxSalary = MutableLiveData<Double>()
    val jobMaxSalary: LiveData<Double>
        get() = _jobMaxSalary

    private val _applProfileStatus = MutableLiveData<Boolean>()
    val applProfileStatus: LiveData<Boolean>
        get() = _applProfileStatus

    private val _applyJobStatus = MutableLiveData<Boolean>()
    val applyJobStatus: LiveData<Boolean>
        get() = _applyJobStatus

    private val _saveJobStatus = MutableLiveData<Boolean>()
    val saveJobStatus: LiveData<Boolean>
        get() = _saveJobStatus

    private val _saveJobId = MutableLiveData<String>()
    val saveJobId: LiveData<String>
        get() = _saveJobId

    private val _applName = MutableLiveData<String>()
    val applName: LiveData<String>
        get() = _applName

    private val _applEmail = MutableLiveData<String>()
    val applEmail: LiveData<String>
        get() = _applEmail

    private val _applEducationLevel = MutableLiveData<String>()
    val applEducationLevel: LiveData<String>
        get() = _applEducationLevel

    private val _applMinimumMonthlySalary = MutableLiveData<Double>()
    val applMinimumMonthlySalary: LiveData<Double>
        get() = _applMinimumMonthlySalary

    private val _applLiveIn = MutableLiveData<String>()
    val applLiveIn: LiveData<String>
        get() = _applLiveIn

    private val _applGender = MutableLiveData<String>()
    val applGender: LiveData<String>
        get() = _applGender

    private val _applPhoneNum = MutableLiveData<String>()
    val applPhoneNum: LiveData<String>
        get() = _applPhoneNum

    fun getJobData(jobId: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Jobs")

        val query = dbRef.orderByChild("jobId").equalTo(jobId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val jobData = snapshot.children.first().getValue(JobModel::class.java)
                    _jobCompanyName.value = jobData?.empName
                    _jobCompanyEmail.value = jobData?.personInChargeEmail
                    _jobTitle.value = jobData?.jobTitle
                    _jobRole.value = jobData?.jobRole
                    _jobSpecialization.value = jobData?.jobSpecialization
                    _jobYearOfExp.value = jobData?.jobYearOfExp
                    _jobWorkState.value = jobData?.jobWorkState
                    _jobMinSalary.value = jobData?.jobMinSalary
                    _jobMaxSalary.value = jobData?.jobMaxSalary
                    _jobDesc.value = jobData?.jobDesc

                    val localFile = File.createTempFile("tempImage", "jpeg")
                    var storageRef: StorageReference = FirebaseStorage.getInstance().reference
                    val imageRef = storageRef.child("employersLogo/${jobData?.personInChargeEmail}")

                    imageRef.getFile(localFile).addOnSuccessListener {
                        val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                        _jobCompanyLogo.value = bitmap
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle the error if needed
            }
        })
    }


    fun getApplicantData(applId: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Applicants")

        val query = dbRef.orderByChild("applId").equalTo(applId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val applData = snapshot.children.first().getValue(ApplicantModel::class.java)
                    _applGender.value = applData?.applGender
                    _applName.value = applData?.applFirstName + " " + applData?.applLastName
                    _applEmail.value = applData?.applEmail
                    _applPhoneNum.value = "+${applData?.applAreaCode?.replace(Regex("[^\\d]"), "")}"
                    _applPhoneNum.value = _applPhoneNum.value + " " + applData?.applPhoneNumber
                    _applEducationLevel.value = applData?.applEducationLevel
                    _applLiveIn.value = applData?.applLiveIn
                    _applMinimumMonthlySalary.value = applData?.applMinimumMonthlySalary
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error if needed
            }
        })
    }

    fun checkApplProfileStatus(applId: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Applicants")

        val query = dbRef.orderByChild("applId").equalTo(applId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (applSnap in snapshot.children) {
                        val applData = applSnap.getValue(ApplicantModel::class.java)
                        if (applData != null && applData.applLiveIn.isNullOrEmpty()) {
                            _applProfileStatus.postValue(true)
                        }else {
                            _applProfileStatus.postValue(false) // Profile status is not empty
                        }
                    }
                } else {
                    // No data found for the specified applId
                    // Handle this case as needed
                    _applProfileStatus.postValue(false)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error if needed
                _applProfileStatus.postValue(false)
            }
        })
    }

    fun checkApplyJobStatus(applId: String, jobId: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Apply")

        val query = dbRef.orderByChild("jobId").equalTo(jobId)

        _applyJobStatus.postValue(false)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (applySnap in snapshot.children) {
                        val applyData = applySnap.getValue(ApplyModel::class.java)
                        if (applyData != null && applyData.applId.equals(applId)) {
                            _applyJobStatus.postValue(true)
                        }
                    }
                } else {
                    _applyJobStatus.postValue(false)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error if needed
            }
        })
    }

    fun checkSaveJobStatus(applId: String, jobId: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Saved Jobs")

        val query = dbRef.orderByChild("jobId").equalTo(jobId)

        _saveJobStatus.postValue(false)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (saveJobSnap in snapshot.children) {
                        val saveJobData = saveJobSnap.getValue(SaveJobModel::class.java)
                        if (saveJobData != null && saveJobData.applId.equals(applId)) {
                            _saveJobStatus.postValue(true)
                            _saveJobId.value = saveJobData?.saveJobId
                        }
                    }
                } else {
                    _saveJobStatus.postValue(false)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error if needed
            }
        })
    }

    fun removeRecord(
        saveJobId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Saved Jobs").child(saveJobId)
        val task = dbRef.removeValue()

        task.addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener { error ->
            onError("Removing Err ${error.message}")
        }
    }

    private val dbRef = FirebaseDatabase.getInstance().getReference("Saved Jobs")

    fun saveJob(
        jobId: String,
        empName: String,
        personInChargeEmail: String,
        jobTitle: String,
        jobWorkState: String,
        jobMinSalary: Double,
        jobMaxSalary:Double,
        applId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        Log.d("Test", applId)
        generateNextSaveJobId { nextSaveJobId ->
            val saveJob = SaveJobModel(
                nextSaveJobId,
                jobId,
                empName,
                personInChargeEmail,
                jobTitle,
                jobWorkState,
                jobMinSalary,
                jobMaxSalary,
                applId
            )

            dbRef.child(nextSaveJobId).setValue(saveJob)
                .addOnCompleteListener {
                    onSuccess()
                }.addOnFailureListener { err ->
                    onError("Error inserting the data")
                }
        }
    }

    // Function to generate the next apply ID
    private fun generateNextSaveJobId(callback: (String) -> Unit) {
        // Query the database to find the current maximum save job ID
        val query = dbRef.orderByKey().limitToLast(1)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (childSnapshot in dataSnapshot.children) {
                        val lastSaveJobId = childSnapshot.key
                        if (lastSaveJobId != null) {
                            // Extract the numeric part of the ID and increment it
                            val numericPart = lastSaveJobId.substring(2).toInt()
                            val nextNumericPart = numericPart + 1
                            val nextSaveJobId = "SJ$nextNumericPart"

                            callback(nextSaveJobId)
                        }
                    }
                } else {
                    callback("SJ1")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback("SJ1")
            }
        })
    }
}