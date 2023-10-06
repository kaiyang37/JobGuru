package com.example.jobguru.viewmodel

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jobguru.viewmodel.ApplJobsDatabaseHelper
import com.example.jobguru.model.JobModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ApplFilterViewModel(private val context: Context) : ViewModel() {
    private val _jobList = MutableLiveData<List<JobModel>>()
    val jobList: LiveData<List<JobModel>> = _jobList

    private val _filteredJobList = MutableLiveData<List<JobModel>>()
    val filteredJobList: LiveData<List<JobModel>> = _filteredJobList

    init {
        getJobsData()
    }

    private fun getJobsData() {
        if (isNetworkAvailable()) {
            syncDataWithFirebase()
        } else {
            loadJobsDataFromSQLite()
        }
    }

    private fun syncDataWithFirebase() {
        val dbRef = FirebaseDatabase.getInstance().getReference("Jobs")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val jobList = mutableListOf<JobModel>()
                if (snapshot.exists()) {
                    for (jobSnap in snapshot.children) {
                        val jobData = jobSnap.getValue(JobModel::class.java)
                        jobData?.let {
                            jobList.add(it)
                        }
                    }
                }
                _jobList.postValue(jobList)

                insertOrUpdateDataInSQLite(jobList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error if needed
            }
        })
    }

    private fun insertOrUpdateDataInSQLite(jobList: List<JobModel>) {
        val dbHelper = ApplJobsDatabaseHelper(context)
        val database = dbHelper.writableDatabase

        val firebaseJobIds = jobList.map { it.jobId }

        val cursor = database.query("Jobs", arrayOf("jobId"), null, null, null, null, null)
        val sqliteJobIds = mutableListOf<String>()

        @SuppressLint("Range")
        while (cursor.moveToNext()) {
            val jobId = cursor.getString(cursor.getColumnIndex("jobId"))
            sqliteJobIds.add(jobId)
        }
        cursor.close()

        // Identify and delete records in SQLite that don't exist in Firebase
        val recordsToDelete = sqliteJobIds.filter { it !in firebaseJobIds }

        for (jobIdToDelete in recordsToDelete) {
            val whereClause = "jobId = ?"
            val whereArgs = arrayOf(jobIdToDelete)
            database.delete("Jobs", whereClause, whereArgs)
        }

        for (job in jobList) {
            val contentValues = ContentValues()
            contentValues.put("jobId", job.jobId)
            contentValues.put("empId", job.empId)
            contentValues.put("empName", job.empName)
            contentValues.put("personInChargeEmail", job.personInChargeEmail)
            contentValues.put("jobTitle", job.jobTitle)
            contentValues.put("jobRole", job.jobRole)
            contentValues.put("jobSpecialization", job.jobSpecialization)
            contentValues.put("jobYearOfExp", job.jobYearOfExp)
            contentValues.put("jobWorkState", job.jobWorkState)
            contentValues.put("jobMinSalary", job.jobMinSalary)
            contentValues.put("jobMaxSalary", job.jobMaxSalary)
            contentValues.put("jobDesc", job.jobDesc)

            val whereClause = "jobId = ?"
            val whereArgs = arrayOf(job.jobId)

            val rowsAffected = database.update("Jobs", contentValues, whereClause, whereArgs)

            if (rowsAffected == 0) {
                database.insert("Jobs", null, contentValues)
            }
        }

        database.close()
    }

    @SuppressLint("Range")
    private fun loadJobsDataFromSQLite() {
        val dbHelper = ApplJobsDatabaseHelper(context)
        val database = dbHelper.readableDatabase
        val jobList = mutableListOf<JobModel>()

        val cursor = database.query("Jobs", null, null, null, null, null, null)

        while (cursor.moveToNext()) {
            val job = JobModel(
                jobId = cursor.getString(cursor.getColumnIndex("jobId")),
                empId = cursor.getString(cursor.getColumnIndex("empId")),
                empName = cursor.getString(cursor.getColumnIndex("empName")),
                personInChargeEmail = cursor.getString(cursor.getColumnIndex("personInChargeEmail")),
                jobTitle = cursor.getString(cursor.getColumnIndex("jobTitle")),
                jobRole = cursor.getString(cursor.getColumnIndex("jobRole")),
                jobSpecialization = cursor.getString(cursor.getColumnIndex("jobSpecialization")),
                jobYearOfExp = cursor.getString(cursor.getColumnIndex("jobYearOfExp")),
                jobWorkState = cursor.getString(cursor.getColumnIndex("jobWorkState")),
                jobMinSalary = cursor.getDouble(cursor.getColumnIndex("jobMinSalary")),
                jobMaxSalary = cursor.getDouble(cursor.getColumnIndex("jobMaxSalary")),
                jobDesc = cursor.getString(cursor.getColumnIndex("jobDesc"))
            )
            Log.d("LoadJobsData", "Data loaded from SQLite successfully")
            jobList.add(job)
        }

        cursor.close()
        database.close()

        _jobList.postValue(jobList)
    }


    fun filterJobsBasedOnPreferences(locationQuery: String?, specQuery: String?, salaryQuery: String?) {

        val filteredList = _jobList.value?.filter{ job ->

            val locationValues = locationQuery?.split(",")?.map { it.trim() }
            val specValues = specQuery?.split(",")?.map { it.trim() }
            val numSalaryQuery = salaryQuery?.replace(Regex("[^0-9.]"), "")?.trim()

            val locationMatched = locationValues?.any { job.jobWorkState?.contains(it, ignoreCase = true) == true } ?: false
            val specMatched = specValues?.any { job.jobSpecialization?.contains(it, ignoreCase = true) == true } ?: false

            (locationMatched && specMatched) || numSalaryQuery?.let { job.jobMinSalary!! >= it.toDouble() } == true

        } ?: emptyList()

        val sortedJobList = filteredList.sortedBy { job ->
            job.jobWorkState
        }

        _filteredJobList.postValue(sortedJobList)
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return networkInfo?.isConnectedOrConnecting == true
    }
}