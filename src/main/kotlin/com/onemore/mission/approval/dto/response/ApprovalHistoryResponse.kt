package com.onemore.mission.approval.dto.response

import com.onemore.mission.approval.domain.ApprovalDecision
import com.onemore.mission.mission.domain.ApprovalStep
import java.time.Instant

data class ApprovalHistoryResponse(
    val id: Long,
    val missionId: Long,
    val step: ApprovalStep,
    val decision: ApprovalDecision,
    val comment: String?,
    val decidedBy: Long,
    val decidedAt: Instant
)