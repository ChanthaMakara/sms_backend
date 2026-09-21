package com.onemore.mission.settlement.service

import com.onemore.mission.common.exception.BusinessException
import com.onemore.mission.common.exception.ResourceNotFoundException
import com.onemore.mission.mileage.repository.MileageClaimRepository
import com.onemore.mission.mission.domain.MissionStatus
import com.onemore.mission.mission.repository.MissionRepository
import com.onemore.mission.settlement.domain.Settlement
import com.onemore.mission.settlement.domain.SettlementStatus
import com.onemore.mission.settlement.dto.request.SettleMissionRequest
import com.onemore.mission.settlement.dto.response.SettlementResponse
import com.onemore.mission.settlement.mapper.SettlementMapper
import com.onemore.mission.settlement.repository.SettlementRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.Instant

@Service
class SettlementService(
    private val settlementRepository: SettlementRepository,
    private val missionRepository: MissionRepository,
    private val mileageClaimRepository: MileageClaimRepository,
    private val settlementMapper: SettlementMapper
) {

    @Transactional
    fun settleMission(missionId: Long, request: SettleMissionRequest, settledBy: Long?): SettlementResponse {
        // 1. Check mission exists
        val mission = missionRepository.findById(missionId)
            .orElseThrow { ResourceNotFoundException("Mission not found with id: $missionId") }

        // 2. Prevent double settlement
        if (settlementRepository.existsByMissionId(missionId)) {
            throw BusinessException("Mission $missionId is already settled")
        }

        // 3. Calculate total allowance from Mission DSA fields
        val totalAllowance = listOfNotNull(
            mission.breakfastTotal,
            mission.lunchTotal,
            mission.dinnerTotal,
            mission.accommodationTotal
        ).fold(BigDecimal.ZERO) { acc, value -> acc.add(value) }

        // 4. Calculate total mileage claim
        val mileageClaims = mileageClaimRepository.findByMissionId(missionId)
        val totalMileageClaim = mileageClaims
            .map { it.totalClaimAmount }
            .fold(BigDecimal.ZERO) { acc, value -> acc.add(value) }

        // 5. Grand total
        val grandTotal = totalAllowance.add(totalMileageClaim)

        // 6. Create Settlement
        val now = Instant.now()
        val settlement = Settlement(
            missionId = missionId,
            totalAllowance = totalAllowance,
            totalMileageClaim = totalMileageClaim,
            grandTotal = grandTotal,
            status = SettlementStatus.SETTLED,
            settledAt = now,
            settledBy = settledBy,
            notes = request.notes,
            createdAt = now,
            updatedAt = now
        )

        val saved = settlementRepository.save(settlement)

        // 7. Update Mission status to SETTLED
        mission.status = MissionStatus.SETTLED
        mission.updatedAt = now
        missionRepository.save(mission)

        return settlementMapper.toResponse(saved)
    }

    @Transactional(readOnly = true)
    fun getSettlementByMissionId(missionId: Long): SettlementResponse {
        // Optional: check mission exists first
        if (!missionRepository.existsById(missionId)) {
            throw ResourceNotFoundException("Mission not found with id: $missionId")
        }

        val settlement = settlementRepository.findByMissionId(missionId)
            .orElseThrow { ResourceNotFoundException("Settlement not found for mission id: $missionId") }

        return settlementMapper.toResponse(settlement)
    }
}