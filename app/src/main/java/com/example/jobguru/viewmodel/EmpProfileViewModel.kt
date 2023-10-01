package com.example.jobguru.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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

class EmpProfileViewModel : ViewModel() {
    private val _empLogo = MutableLiveData<Bitmap>()
    val empLogo: LiveData<Bitmap>
        get() = _empLogo

    private val _empName = MutableLiveData<String>()
    val empName: LiveData<String>
        get() = _empName

    private val _empIndustry = MutableLiveData<String>()
    val empIndustry: LiveData<String>
        get() = _empIndustry

    private val _empAddress = MutableLiveData<String>()
    val empAddress: LiveData<String>
        get() = _empAddress

    private val _empPostcode = MutableLiveData<String>()
    val empPostcode: LiveData<String>
        get() = _empPostcode

    private val _empState = MutableLiveData<String>()
    val empState: LiveData<String>
        get() = _empState

    private val _personInChargeName = MutableLiveData<String>()
    val personInChargeName: LiveData<String>
        get() = _personInChargeName

    private val _personInChargeContact = MutableLiveData<String>()
    val personInChargeContact: LiveData<String>
        get() = _personInChargeContact

    private val _personInChargeDesignation = MutableLiveData<String>()
    val personInChargeDesignation: LiveData<String>
        get() = _personInChargeDesignation

    private val _personInChargeGender = MutableLiveData<String>()
    val personInChargeGender: LiveData<String>
        get() = _personInChargeGender

    private val _personInChargeEmail = MutableLiveData<String>()
    val personInChargeEmail: LiveData<String>
        get() = _personInChargeEmail

    fun getProfileData(empEmail: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Employers")
        val query = dbRef.orderByChild("personInChargeEmail").equalTo(empEmail)
        var storageRef: StorageReference

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val profileData = snapshot.children.first().getValue(EmployerModel::class.java)
                    _empName.value = profileData?.empName
                    _empIndustry.value = profileData?.empIndustry
                    _empAddress.value = profileData?.empAddress
                    _empPostcode.value = profileData?.empPostcode
                    _empState.value = profileData?.empState
                    _personInChargeName.value = profileData?.personInChargeName
                    _personInChargeContact.value = profileData?.personInChargeContact
                    _personInChargeDesignation.value = profileData?.personInChargeDesignation
                    _personInChargeGender.value = profileData?.personInChargeGender
                    _personInChargeEmail.value = profileData?.personInChargeEmail

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
    }