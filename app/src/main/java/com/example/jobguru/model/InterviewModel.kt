package com.example.jobguru.model

data class InterviewModel(
    var intvwId: String? = null,
    var jobId: String? = null,
    var applId: String? = null,
    var applName: String? = null,
    var empName: String? = null,
    var jobTitle: String? = null,
    var intvwrName: String? = null,
    var intvwDate: String? = null,
    var intvwTime: String? = null,
    var intvwPlatform: String? = null,
    var intvwStatus: String? = null,
    var intvwReason: String? = null,
    var resend: Boolean? = false
)
