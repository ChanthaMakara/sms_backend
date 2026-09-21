package com.onemore.mission.settlement.mapper

import com.onemore.mission.settlement.domain.Settlement
import com.onemore.mission.settlement.dto.response.SettlementResponse
import org.springframework.stereotype.Component

@Component
class SettlementMapper {

    fun toResponse(entity: Settlement): SettlementResponse {
        return SettlementResponse(
            id = entity.id!!,
            missionId = entity.missionId,
            totalAllowance = entity.totalAllowance,
            totalMileageClaim = entity.totalMileageClaim,
            grandTotal = entity.grandTotal,
            status = entity.status,
            settledAt = entity.settledAt,
            settledBy = entity.settledBy,
            notes = entity.notes,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}