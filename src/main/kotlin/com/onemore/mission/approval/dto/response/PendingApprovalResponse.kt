package com.onemore.mission.approval.dto.response

import com.onemore.mission.mission.domain.ApprovalStep
import com.onemore.mission.mission.domain.MissionStatus
import java.time.LocalDate

data class PendingApprovalResponse(
    val missionId: Long,
    val missionCode: String?,
    val requesterName: String,
    val destinationLocation: String,
    val departureDate: LocalDate,
    val arrivalDate: LocalDate,
    val status: MissionStatus,
    val currentApprovalStep: ApprovalStep?
)