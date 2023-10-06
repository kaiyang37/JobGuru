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
import com.example.jobguru.model.InterviewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EmpInterviewViewModel(private val delimitedJobIds: String, private val context: Context) :
    ViewModel() {
    private val _interviewList = MutableLiveData<List<InterviewModel>>()
    val interviewList: LiveData<List<InterviewModel>> = _interviewList

    private val _searchedInterviewList = MutableLiveData<List<InterviewModel>>()
    val searchedInterviewList: LiveData<List<InterviewModel>> = _searchedInterviewList

    init {
        getInterviewsData(delimitedJobIds)
    }

    private fun getInterviewsData(delimitedJobIds: String) {
        if (isNetworkAvailable()) {
            syncDataWithFirebase(delimitedJobIds)
        } else {
            loadInterviewsDataFromSQLite()
        }
    }

    private fun syncDataWithFirebase(delimitedJobIds: String) {
        val jobIdList = delimitedJobIds.split(",")
        val dbRef = FirebaseDatabase.getInstance().getReference("Interviews")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val interviewList = mutableListOf<InterviewModel>()
                if (snapshot.exists()) {
                    for (interviewSnap in snapshot.children) {
                        val jobId = interviewSnap.child("jobId").getValue(String::class.java)
                        if (jobId != null && jobId in jobIdList) {
                            val interviewData = interviewSnap.getValue(InterviewModel::class.java)
                            interviewData?.let {
                                interviewList.add(it)
                            }
                        }
                    }
                }
                _interviewList.postValue(interviewList)
                insertOrUpdateDataInSQLite(interviewList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error: ${error.message}")
            }
        })
    }

    private fun insertOrUpdateDataInSQLite(interviewList: List<InterviewModel>) {
        val dbHelper = EmpInterviewDatabaseHelper(context)
        val database = dbHelper.writableDatabase

        val firebaseInterviewIds = interviewList.map { it.intvwId }

        val cursor = database.query("Interviews", arrayOf("intvwId"), null, null, null, null, null)
        val sqliteInterviewsIds = mutableListOf<String>()

        @SuppressLint("Range")
        while (cursor.moveToNext()) {
            val intvwId = cursor.getString(cursor.getColumnIndex("intvwId"))
            sqliteInterviewsIds.add(intvwId)
        }
        cursor.close()

        // Identify and delete records in SQLite that don't exist in Firebase
        val recordsToDelete = sqliteInterviewsIds.filter { it !in firebaseInterviewIds }

        for (interviewIdToDelete in recordsToDelete) {
            val whereClause = "intvwId = ?"
            val whereArgs = arrayOf(interviewIdToDelete)
            database.delete("Interviews", whereClause, whereArgs)
        }

        for (interview in interviewList) {
            val contentValues = ContentValues()
            contentValues.put("intvwId", interview.intvwId)
            contentValues.put("jobId", interview.jobId)
            contentValues.put("applId", interview.applId)
            contentValues.put("applName", interview.applName)
            contentValues.put("empName", interview.empName)
            contentValues.put("jobTitle", interview.jobTitle)
            contentValues.put("intvwrName", interview.intvwrName)
            contentValues.put("intvwDate", interview.intvwDate)
            contentValues.put("intvwTime", interview.intvwTime)
            contentValues.put("intvwPlatform", interview.intvwPlatform)
            contentValues.put("intvwStatus", interview.intvwStatus)
            contentValues.put("intvwReason", interview.intvwReason)
            contentValues.put("resend", interview.resend)

            val whereClause = "intvwId = ?"
            val whereArgs = arrayOf(interview.intvwId)

            val rowsAffected = database.update("Interviews", contentValues, whereClause, whereArgs)

            if (rowsAffected == 0) {
                database.insert("Interviews", null, contentValues)
            }
        }

        database.close()
    }

    @SuppressLint("Range")
    private fun loadInterviewsDataFromSQLite() {
        val dbHelper = EmpInterviewDatabaseHelper(context)
        val database = dbHelper.readableDatabase
        val interviewList = mutableListOf<InterviewModel>()

        val cursor = database.query("Interviews", null, null, null, null, null, null)

        while (cursor.moveToNext()) {
            val interview = InterviewModel(
                intvwId = cursor.getString(cursor.getColumnIndex("intvwId")),
                jobId = cursor.getString(cursor.getColumnIndex("jobId")),
                applId = cursor.getString(cursor.getColumnIndex("applId")),
                applName = cursor.getString(cursor.getColumnIndex("applName")),
                empName = cursor.getString(cursor.getColumnIndex("empName")),
                jobTitle = cursor.getString(cursor.getColumnIndex("jobTitle")),
                intvwrName = cursor.getString(cursor.getColumnIndex("intvwrName")),
                intvwDate = cursor.getString(cursor.getColumnIndex("intvwDate")),
                intvwTime = cursor.getString(cursor.getColumnIndex("intvwTime")),
                intvwPlatform = cursor.getString(cursor.getColumnIndex("intvwPlatform")),
                intvwStatus = cursor.getString(cursor.getColumnIndex("intvwStatus")),
                intvwReason = cursor.getString(cursor.getColumnIndex("intvwReason")),
                false
            )
            Log.d("LoadInterviewsData", "Data loaded from SQLite successfully")
            interviewList.add(interview)
        }

        cursor.close()
        database.close()

        _interviewList.postValue(interviewList)
    }

    fun searchInterviews(query: String) {
        val searchedList = _interviewList.value?.filter { interview ->
            (interview.jobTitle?.contains(query, ignoreCase = true) == true) ||
                    (interview.applName?.contains(query, ignoreCase = true) == true) ||
                    (interview.intvwDate?.contains(query, ignoreCase = true) == true) ||
                    (interview.intvwTime?.contains(query, ignoreCase = true) == true) ||
                    (interview.intvwStatus?.contains(query, ignoreCase = true) == true)

            // Add more fields to search if needed
        } ?: emptyList()
        _searchedInterviewList.postValue(searchedList)
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return networkInfo?.isConnectedOrConnecting == true
    }
}