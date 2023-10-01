package com.example.jobguru.model

data class EmployerModel(
    var empId: String? = null,
    var empName: String? = null,
    var empIndustry: String? = null,
    var empAddress: String? = null,
    var empPostcode: String? = null,
    var empState: String? = null,
    var personInChargeName: String? = null,
    var personInChargeContact: String? = null,
    var personInChargeDesignation: String? = null,
    var personInChargeGender: String? = null,
    var personInChargeEmail: String? = null
)