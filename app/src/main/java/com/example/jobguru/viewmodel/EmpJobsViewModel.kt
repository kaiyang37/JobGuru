package com.example.jobguru

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.jobguru.model.JobModel
import com.example.jobguru.viewmodel.EmpJobsDatabaseHelper
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class EmpJobsViewModel(private val empEmail: String, private val context: Context) : ViewModel() {
    private val _jobList = MutableLiveData<List<JobModel>>()
    val jobList: LiveData<List<JobModel>> = _jobList

    private val _searchedJobList = MutableLiveData<List<JobModel>>()
    val searchedJobList: LiveData<List<JobModel>> = _searchedJobList

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
        val employersRef = FirebaseDatabase.getInstance().getReference("Employers")
        val dbRef = FirebaseDatabase.getInstance().getReference("Jobs")

        val employerQuery = employersRef.orderByChild("personInChargeEmail").equalTo(empEmail)

        employerQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(employerDataSnapshot: DataSnapshot) {
                if (employerDataSnapshot.exists()) {
                    val employerSnapshot = employerDataSnapshot.children.first()
                    val empId = employerSnapshot.key

                    val jobQuery = dbRef.orderByChild("empId").equalTo(empId)

                    jobQuery.addValueEventListener(object : ValueEventListener {
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
                            // Handle the error
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })
    }

    private fun insertOrUpdateDataInSQLite(jobList: List<JobModel>) {
        val dbHelper = EmpJobsDatabaseHelper(context)
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
        val dbHelper = EmpJobsDatabaseHelper(context)
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


    fun searchJobs(query: String) {
        val searchedList = _jobList.value?.filter { job ->
            (job.jobTitle?.contains(query, ignoreCase = true) == true) ||
                    (job.jobRole?.contains(query, ignoreCase = true) == true) ||
                    (job.jobWorkState?.contains(query, ignoreCase = true) == true) ||
                    (job.jobMinSalary.toString()?.contains(query, ignoreCase = true) == true) ||
                    (job.jobMaxSalary.toString()?.contains(query, ignoreCase = true) == true)

        } ?: emptyList()
        _searchedJobList.postValue(searchedList)
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return networkInfo?.isConnectedOrConnecting == true
    }
}