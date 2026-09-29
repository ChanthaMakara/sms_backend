package com.onemore.mission.mileage.service

import com.onemore.mission.common.exception.ResourceNotFoundException
import com.onemore.mission.mileage.domain.MileageClaimStatus
import com.onemore.mission.mileage.dto.request.CreateMileageClaimRequest
import com.onemore.mission.mileage.dto.response.MileageClaimResponse
import com.onemore.mission.mileage.mapper.MileageClaimMapper
import com.onemore.mission.mileage.repository.MileageClaimRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class MileageClaimService(
    private val mileageClaimRepository: MileageClaimRepository,
    private val mileageClaimMapper: MileageClaimMapper
) {

    @Transactional
    fun createMileageClaim(missionId: Long, request: CreateMileageClaimRequest): MileageClaimResponse {
        val entity = mileageClaimMapper.toEntity(missionId, request)
        val saved = mileageClaimRepository.save(entity)
        return mileageClaimMapper.toResponse(saved)
    }

    @Transactional(readOnly = true)
    fun getMileageClaimsByMission(missionId: Long): List<MileageClaimResponse> {
        return mileageClaimRepository.findByMissionId(missionId)
            .map { mileageClaimMapper.toResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getAllMileageClaims(): List<MileageClaimResponse> {
        return mileageClaimRepository.findAll()
            .sortedByDescending { it.createdAt }
            .map { mileageClaimMapper.toResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getMileageClaimById(id: Long): MileageClaimResponse {
        val entity = mileageClaimRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Mileage claim not found: $id") }
        return mileageClaimMapper.toResponse(entity)
    }

    @Transactional
    fun updateStatus(id: Long, status: String): MileageClaimResponse {
        val entity = mileageClaimRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Mileage claim not found: $id") }

        entity.status = parseStatus(status)
        entity.updatedAt = Instant.now()

        return mileageClaimMapper.toResponse(mileageClaimRepository.save(entity))
    }

    private fun parseStatus(value: String): MileageClaimStatus {
        return try {
            MileageClaimStatus.valueOf(value.uppercase())
        } catch (ex: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid status: $value")
        }
    }
}
