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
import com.example.jobguru.model.ApplicantModel
import com.example.jobguru.model.ApplyModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EmpApplicantsViewModel(private val delimitedJobIds: String, private val context: Context) :
    ViewModel() {
    private val _applList = MutableLiveData<List<ApplyModel>>()
    val applList: LiveData<List<ApplyModel>> = _applList

    private val _searchedApplList = MutableLiveData<List<ApplyModel>>()
    val searchedApplList: LiveData<List<ApplyModel>> = _searchedApplList

    init {
        getApplicantsData(delimitedJobIds)
    }

    private fun getApplicantsData(delimitedJobIds: String) {
        if (isNetworkAvailable()) {
            syncDataWithFirebase(delimitedJobIds)
        } else {
            loadApplicantsDataFromSQLite()
        }
    }

    private fun syncDataWithFirebase(delimitedJobIds: String) {
        val jobIdList = delimitedJobIds.split(",")
        val applyRef = FirebaseDatabase.getInstance().getReference("Apply")

        applyRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val applList = mutableListOf<ApplyModel>()
                if (snapshot.exists()) {
                    for (applSnap in snapshot.children) {
                        val jobId = applSnap.child("jobId").getValue(String::class.java)
                        if (jobId != null && jobId in jobIdList) {
                            val applData = applSnap.getValue(ApplyModel::class.java)
                            applData?.let {
                                if (applSnap.child("appStatus")
                                        .getValue(String::class.java) == "Pending"
                                ) {
                                    applList.add(it)
                                }
                            }
                        }
                    }
                }
                _applList.postValue(applList)

                insertOrUpdateDataInSQLite(applList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })
    }

    private fun insertOrUpdateDataInSQLite(applList: List<ApplyModel>) {
        val dbHelper = EmpApplicantsDatabaseHelper(context)
        val database = dbHelper.writableDatabase

        val firebaseApplyIds = applList.map { it.appId }

        val cursor = database.query("Applicants", arrayOf("appId"), null, null, null, null, null)
        val sqliteApplicantsIds = mutableListOf<String>()

        @SuppressLint("Range")
        while (cursor.moveToNext()) {
            val appId = cursor.getString(cursor.getColumnIndex("appId"))
            sqliteApplicantsIds.add(appId)
        }
        cursor.close()

        // Identify and delete records in SQLite that don't exist in Firebase
        val recordsToDelete = sqliteApplicantsIds.filter { it !in firebaseApplyIds }

        for (applyIdToDelete in recordsToDelete) {
            val whereClause = "appId = ?"
            val whereArgs = arrayOf(applyIdToDelete)
            database.delete("Applicants", whereClause, whereArgs)
        }

        for (apply in applList) {
            val contentValues = ContentValues()
            contentValues.put("appId", apply.appId)
            contentValues.put("applId", apply.applId)
            contentValues.put("jobId", apply.jobId)
            contentValues.put("jobTitle", apply.jobTitle)
            contentValues.put("empName", apply.empName)
            contentValues.put("jobCompanyEmail", apply.jobCompanyEmail)
            contentValues.put("jobWorkState", apply.jobWorkState)
            contentValues.put("applName", apply.applName)
            contentValues.put("applEducationLevel", apply.applEducationLevel)
            contentValues.put("applMinimumMonthlySalary", apply.applMinimumMonthlySalary)
            contentValues.put("applLiveIn", apply.applLiveIn)
            contentValues.put("appStatus", apply.appStatus)

            val whereClause = "appId = ?"
            val whereArgs = arrayOf(apply.appId)

            val rowsAffected = database.update("Applicants", contentValues, whereClause, whereArgs)

            if (rowsAffected == 0) {
                database.insert("Applicants", null, contentValues)
            }
        }

        database.close()
    }

    @SuppressLint("Range")
    private fun loadApplicantsDataFromSQLite() {
        val dbHelper = EmpApplicantsDatabaseHelper(context)
        val database = dbHelper.readableDatabase
        val applList = mutableListOf<ApplyModel>()

        val cursor = database.query("Applicants", null, null, null, null, null, null)

        while (cursor.moveToNext()) {
            val apply = ApplyModel(
                appId = cursor.getString(cursor.getColumnIndex("appId")),
                applId = cursor.getString(cursor.getColumnIndex("applId")),
                jobId = cursor.getString(cursor.getColumnIndex("jobId")),
                jobTitle = cursor.getString(cursor.getColumnIndex("jobTitle")),
                empName = cursor.getString(cursor.getColumnIndex("empName")),
                jobCompanyEmail = cursor.getString(cursor.getColumnIndex("jobCompanyEmail")),
                jobWorkState = cursor.getString(cursor.getColumnIndex("jobWorkState")),
                applName = cursor.getString(cursor.getColumnIndex("applName")),
                applEducationLevel = cursor.getString(cursor.getColumnIndex("applEducationLevel")),
                applMinimumMonthlySalary = cursor.getDouble(cursor.getColumnIndex("applMinimumMonthlySalary")),
                applLiveIn = cursor.getString(cursor.getColumnIndex("applLiveIn")),
                appStatus = cursor.getString(cursor.getColumnIndex("appStatus"))
            )
            Log.d("LoadApplicantsData", "Data loaded from SQLite successfully")
            applList.add(apply)
        }

        cursor.close()
        database.close()

        _applList.postValue(applList)
    }

    fun searchApplicants(query: String) {
        val searchedList = _applList.value?.filter { appl ->
            (appl.jobTitle?.contains(
                query,
                ignoreCase = true
            ) == true) || (appl.applName?.contains(query, ignoreCase = true) == true) ||
                    (appl.applEducationLevel?.contains(
                        query,
                        ignoreCase = true
                    ) == true) || (appl.applMinimumMonthlySalary.toString()
                ?.contains(query, ignoreCase = true) == true) ||
                    (appl.applLiveIn?.contains(query, ignoreCase = true) == true)
        } ?: emptyList()
        _searchedApplList.postValue(searchedList)
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return networkInfo?.isConnectedOrConnecting == true
    }
}