package com.example.jobguru.viewmodel

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class EmpApplicantsDatabaseHelper (context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "empApplicants_db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Applicants(appId TEXT PRIMARY KEY, applId TEXT, jobId TEXT, jobTitle TEXT, empName TEXT, jobCompanyEmail TEXT, jobWorkState TEXT, applName TEXT, applEducationLevel TEXT, applMinimumMonthlySalary REAL, applLiveIn TEXT, appStatus TEXT)")

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database upgrades if needed
    }
}