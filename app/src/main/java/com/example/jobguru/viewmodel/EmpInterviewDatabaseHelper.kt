package com.example.jobguru.viewmodel

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class EmpInterviewDatabaseHelper (context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "empInterview_db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Interviews(intvwId TEXT PRIMARY KEY, jobId TEXT, applId TEXT, applName TEXT, empName TEXT, jobTitle TEXT, intvwrName TEXT, intvwDate TEXT, intvwTime TEXT, intvwPlatform TEXT, intvwStatus TEXT, intvwReason TEXT, resend INTEGER)")

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database upgrades if needed
    }
}