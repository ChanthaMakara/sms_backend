package com.onemore.mission.report.dto.request

import java.time.LocalDate

data class CreateActivityReportRequest(

    val requesterName: String,

    val requesterId: String? = null,

    val position: String? = null,

    val function: String? = null,

    val business: String? = null,

    val basedLocation: String? = null,

    val destinationLocation: String? = null,

    val travelStartDate: LocalDate,

    val travelEndDate: LocalDate,

    val travelObjectives: String,

    val achievedResults: String,

    val nextPlan: String? = null,

    val attachedDocuments: String? = null,

    val requesterSignatureDate: LocalDate? = null
)