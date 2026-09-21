package com.onemore.mission.settlement.dto.response

import com.onemore.mission.settlement.domain.SettlementStatus
import java.math.BigDecimal
import java.time.Instant

data class SettlementResponse(
    val id: Long,
    val missionId: Long,
    val totalAllowance: BigDecimal,
    val totalMileageClaim: BigDecimal,
    val grandTotal: BigDecimal,
    val status: SettlementStatus,
    val settledAt: Instant?,
    val settledBy: Long?,
    val notes: String?,
    val createdAt: Instant,
    val updatedAt: Instant
)