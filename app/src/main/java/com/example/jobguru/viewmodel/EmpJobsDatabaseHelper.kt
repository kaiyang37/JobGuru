package com.example.jobguru.viewmodel

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class EmpJobsDatabaseHelper (context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "empJobs_db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Jobs (jobId TEXT PRIMARY KEY, empId TEXT, empName TEXT, personInChargeEmail TEXT, jobTitle TEXT, jobRole TEXT, jobSpecialization TEXT, jobYearOfExp TEXT, jobWorkState TEXT, jobMinSalary REAL, jobMaxSalary REAL, jobDesc TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database upgrades if needed
    }
}