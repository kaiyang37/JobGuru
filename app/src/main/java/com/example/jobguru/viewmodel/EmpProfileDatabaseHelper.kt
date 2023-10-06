package com.example.jobguru.viewmodel

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class EmpProfileDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "empProfile_db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Employers (empId TEXT PRIMARY KEY, empName TEXT, empIndustry TEXT, empAddress TEXT, empPostcode TEXT, empState TEXT, personInChargeName TEXT, personInChargeContact TEXT, personInChargeDesignation TEXT, personInChargeGender TEXT, personInChargeEmail TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database upgrades if needed
    }
}