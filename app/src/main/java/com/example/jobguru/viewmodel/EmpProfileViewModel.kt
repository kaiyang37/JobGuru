package com.example.jobguru.viewmodel

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jobguru.model.JobModel
import com.example.jobguru.model.EmployerModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class EmpProfileViewModel (private val context: Context): ViewModel() {
    private val _empLogo = MutableLiveData<Bitmap>()
    val empLogo: LiveData<Bitmap> = _empLogo

    private val _empName = MutableLiveData<String>()
    val empName: LiveData<String> = _empName

    private val _empIndustry = MutableLiveData<String>()
    val empIndustry: LiveData<String> = _empIndustry

    private val _empAddress = MutableLiveData<String>()
    val empAddress: LiveData<String> = _empAddress

    private val _empPostcode = MutableLiveData<String>()
    val empPostcode: LiveData<String> = _empPostcode

    private val _empState = MutableLiveData<String>()
    val empState: LiveData<String> = _empState

    private val _personInChargeName = MutableLiveData<String>()
    val personInChargeName: LiveData<String> = _personInChargeName

    private val _personInChargeContact = MutableLiveData<String>()
    val personInChargeContact: LiveData<String> = _personInChargeContact

    private val _personInChargeDesignation = MutableLiveData<String>()
    val personInChargeDesignation: LiveData<String> = _personInChargeDesignation

    private val _personInChargeGender = MutableLiveData<String>()
    val personInChargeGender: LiveData<String> = _personInChargeGender

    private val _personInChargeEmail = MutableLiveData<String>()
    val personInChargeEmail: LiveData<String> = _personInChargeEmail

    fun getProfileData(empEmail: String, context: Context) {
        if (isNetworkAvailable()) {
            // If there's an internet connection, fetch data from Firebase and update LiveData
            syncDataWithFirebase(empEmail)
        } else {
            // If no internet connection, load data from SQLite and update LiveData
            Log.e("My Tag", "No internet connection")
            loadProfileDataFromSQLite(empEmail, context)
        }
    }

    private fun syncDataWithFirebase(empEmail: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Employers")
        val query = dbRef.orderByChild("personInChargeEmail").equalTo(empEmail)
        var storageRef: StorageReference

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val profileData = snapshot.children.first().getValue(EmployerModel::class.java)
                    updateLiveDataWithProfileData(profileData)

                    if (profileData != null) {
                        insertOrUpdateDataInSQLite(profileData)
                    }

                    val localFile = File.createTempFile("tempImage", "jpeg")
                    storageRef = FirebaseStorage.getInstance().reference
                    val imageRef = storageRef.child("employersLogo/$empEmail")

                    imageRef.getFile(localFile).addOnSuccessListener {
                        val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                        _empLogo.value = bitmap
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error if needed
            }
        })
    }

    private fun insertOrUpdateDataInSQLite(profileData: EmployerModel) {
        val dbHelper = EmpProfileDatabaseHelper(context)
        val database = dbHelper.writableDatabase

        val contentValues = ContentValues()
        contentValues.put("empId", profileData.empId)
        contentValues.put("empName", profileData.empName)
        contentValues.put("empIndustry", profileData.empIndustry)
        contentValues.put("empAddress", profileData.empAddress)
        contentValues.put("empPostcode", profileData.empPostcode)
        contentValues.put("empState", profileData.empState)
        contentValues.put("personInChargeName", profileData.personInChargeName)
        contentValues.put("personInChargeContact", profileData.personInChargeContact)
        contentValues.put("personInChargeDesignation", profileData.personInChargeDesignation)
        contentValues.put("personInChargeGender", profileData.personInChargeGender)
        contentValues.put("personInChargeEmail", profileData.personInChargeEmail)

        val whereClause = "empId = ?"
        val whereArgs = arrayOf(profileData.empId)

        val rowsAffected = database.update("Employers", contentValues, whereClause, whereArgs)

        if (rowsAffected == 0) {
            // If no rows were updated, it means the data doesn't exist in SQLite, so insert it.
            database.insert("Employers", null, contentValues)
        }

        database.close()
    }

    @SuppressLint("Range")
    private fun loadProfileDataFromSQLite(empEmail: String, context: Context) {
        val dbHelper = EmpProfileDatabaseHelper(context)
        val database = dbHelper.readableDatabase
        val query = "SELECT * FROM Employers WHERE personInChargeEmail = ?"
        val cursor = database.rawQuery(query, arrayOf(empEmail))

        if (cursor.moveToFirst()) {
            val profileData = EmployerModel(
                cursor.getString(cursor.getColumnIndex("empId")),
                cursor.getString(cursor.getColumnIndex("empName")),
                cursor.getString(cursor.getColumnIndex("empIndustry")),
                cursor.getString(cursor.getColumnIndex("empAddress")),
                cursor.getString(cursor.getColumnIndex("empPostcode")),
                cursor.getString(cursor.getColumnIndex("empState")),
                cursor.getString(cursor.getColumnIndex("personInChargeName")),
                cursor.getString(cursor.getColumnIndex("personInChargeContact")),
                cursor.getString(cursor.getColumnIndex("personInChargeDesignation")),
                cursor.getString(cursor.getColumnIndex("personInChargeGender")),
                cursor.getString(cursor.getColumnIndex("personInChargeEmail"))
            )
            Log.d("LoadProfileData", "Data loaded from SQLite successfully")
            updateLiveDataWithProfileData(profileData)
        }else {
            Log.d("LoadProfileData", "No data found in SQLite")
        }

        cursor.close()
        database.close()
    }

    private fun updateLiveDataWithProfileData(profileData: EmployerModel?) {
        if (profileData != null) {
            _empName.value = profileData.empName
            _empIndustry.value = profileData.empIndustry
            _empAddress.value = profileData.empAddress
            _empPostcode.value = profileData.empPostcode
            _empState.value = profileData.empState
            _personInChargeName.value = profileData.personInChargeName
            _personInChargeContact.value = profileData.personInChargeContact
            _personInChargeDesignation.value = profileData.personInChargeDesignation
            _personInChargeGender.value = profileData.personInChargeGender
            _personInChargeEmail.value = profileData.personInChargeEmail
        }
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return networkInfo?.isConnectedOrConnecting == true
    }
}