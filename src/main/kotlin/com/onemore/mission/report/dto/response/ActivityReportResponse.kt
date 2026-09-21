package com.onemore.mission.report.dto.response

import java.time.Instant
import java.time.LocalDate

data class ActivityReportResponse(

    val id: Long,

    val missionId: Long,

    val requesterName: String,

    val requesterId: String?,

    val position: String?,

    val function: String?,

    val business: String?,

    val basedLocation: String?,

    val destinationLocation: String?,

    val travelStartDate: LocalDate,

    val travelEndDate: LocalDate,

    val travelObjectives: String,

    val achievedResults: String,

    val nextPlan: String?,

    val attachedDocuments: String?,

    val requesterSignatureDate: LocalDate?,

    val functionManagerComment: String?,

    val functionManagerSignatureDate: LocalDate?,

    val bizOpsComment: String?,

    val bizOpsSignatureDate: LocalDate?,

    val createdAt: Instant,

    val updatedAt: Instant
)