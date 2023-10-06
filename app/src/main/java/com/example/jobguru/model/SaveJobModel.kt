package com.example.jobguru.model

data class SaveJobModel(
    var saveJobId: String? = null,
    var jobId: String? = null,
    var empName: String? = null,
    var personInChargeEmail: String? = null,
    var jobTitle: String? = null,
    var jobWorkState: String? = null,
    var jobMinSalary: Double? = null,
    var jobMaxSalary: Double? = null,
    var applId: String? = null
)