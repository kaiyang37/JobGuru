package com.example.jobguru.model

data class JobModel (
    var jobId: String? = null,
    var empId: String? = null,
    var empName: String? = null,
    var personInChargeEmail: String? = null,
    var jobTitle: String? = null,
    var jobRole: String? = null,
    var jobSpecialization: String? = null,
    var jobYearOfExp: String? = null,
    var jobWorkState: String? = null,
    var jobMinSalary: Double? = null,
    var jobMaxSalary: Double? = null,
    var jobDesc: String? = null
)