package com.onemore.mission.mileage.service

import com.onemore.mission.common.exception.ResourceNotFoundException
import com.onemore.mission.mileage.domain.MileageClaimStatus
import com.onemore.mission.mileage.dto.request.CreateMileageClaimRequest
import com.onemore.mission.mileage.dto.response.MileageClaimResponse
import com.onemore.mission.mileage.mapper.MileageClaimMapper
import com.onemore.mission.mileage.repository.MileageClaimRepository
import com.onemore.mission.user.domain.JobLevel
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.Instant

@Service
class MileageClaimService(
    private val mileageClaimRepository: MileageClaimRepository,
    private val mileageClaimMapper: MileageClaimMapper
) {

    // =========================
    // CREATE
    // =========================
    @Transactional
    fun createMileageClaim(
        missionId: Long,
        request: CreateMileageClaimRequest
    ): MileageClaimResponse {

        validateClaim(missionId, request)

        val entity = mileageClaimMapper.toEntity(
            missionId,
            request
        )

        val saved = mileageClaimRepository.save(entity)

        return mileageClaimMapper.toResponse(saved)
    }

    // =========================
    // GET BY MISSION
    // =========================
    @Transactional(readOnly = true)
    fun getMileageClaimsByMission(
        missionId: Long
    ): List<MileageClaimResponse> {

        return mileageClaimRepository
            .findByMissionId(missionId)
            .map { mileageClaimMapper.toResponse(it) }
    }

    // =========================
    // GET ALL
    // =========================
    @Transactional(readOnly = true)
    fun getAllMileageClaims(): List<MileageClaimResponse> {

        return mileageClaimRepository
            .findAll()
            .sortedByDescending { it.createdAt }
            .map { mileageClaimMapper.toResponse(it) }
    }

    // =========================
    // GET BY ID
    // =========================
    @Transactional(readOnly = true)
    fun getMileageClaimById(
        id: Long
    ): MileageClaimResponse {

        val entity = mileageClaimRepository
            .findById(id)
            .orElseThrow {
                ResourceNotFoundException(
                    "Mileage claim not found: $id"
                )
            }

        return mileageClaimMapper.toResponse(entity)
    }

    // =========================
    // UPDATE
    // =========================
    @Transactional
    fun updateMileageClaim(
        missionId: Long,
        claimId: Long,
        request: CreateMileageClaimRequest
    ): MileageClaimResponse {

        val entity = mileageClaimRepository
            .findById(claimId)
            .orElseThrow {
                ResourceNotFoundException(
                    "Mileage claim not found: $claimId"
                )
            }

        // Check mission
        if (entity.missionId != missionId) {
            throw IllegalArgumentException(
                "Mileage claim does not belong to mission: $missionId"
            )
        }

        // Only DRAFT can be updated
        if (entity.status != MileageClaimStatus.DRAFT) {
            throw IllegalStateException(
                "Only draft mileage claims can be updated"
            )
        }

        // Validate request
        validateClaim(missionId, request)

        // Basic information
        entity.requesterName = request.requesterName
        entity.requesterId = request.requesterId
        entity.position = request.position
        entity.function = request.function
        entity.business = request.business

        // Convert String? to JobLevel?
        entity.jobLevel = parseJobLevel(request.jobLevel)

        entity.basedLocation = request.basedLocation
        entity.destinationLocation = request.destinationLocation

        // Travel information
        entity.travelStartDate = request.travelStartDate
        entity.travelEndDate = request.travelEndDate
        entity.travelObjectives = request.travelObjectives

        /*
         * totalDistanceKm and totalClaimAmount are NOT updated here.
         *
         * These fields are calculated from mileage/travel details
         * and are not present in CreateMileageClaimRequest.
         */

        entity.updatedAt = Instant.now()

        val saved = mileageClaimRepository.save(entity)

        return mileageClaimMapper.toResponse(saved)
    }

    // =========================
    // UPDATE STATUS
    // =========================
    @Transactional
    fun updateStatus(
        id: Long,
        status: String
    ): MileageClaimResponse {

        val entity = mileageClaimRepository
            .findById(id)
            .orElseThrow {
                ResourceNotFoundException(
                    "Mileage claim not found: $id"
                )
            }

        entity.status = parseStatus(status)
        entity.updatedAt = Instant.now()

        return mileageClaimMapper.toResponse(
            mileageClaimRepository.save(entity)
        )
    }

    // =========================
    // DELETE
    // =========================
    @Transactional
    fun deleteMileageClaim(
        missionId: Long,
        claimId: Long
    ) {

        val entity = mileageClaimRepository
            .findById(claimId)
            .orElseThrow {
                ResourceNotFoundException(
                    "Mileage claim not found: $claimId"
                )
            }

        // Check mission
        if (entity.missionId != missionId) {
            throw IllegalArgumentException(
                "Mileage claim does not belong to mission: $missionId"
            )
        }

        // Only DRAFT can be deleted
        if (entity.status != MileageClaimStatus.DRAFT) {
            throw IllegalStateException(
                "Only draft mileage claims can be deleted"
            )
        }

        mileageClaimRepository.delete(entity)
    }

    // =========================
    // VALIDATION
    // =========================
    private fun validateClaim(
        missionId: Long,
        request: CreateMileageClaimRequest
    ) {

        if (missionId <= 0) {
            throw IllegalArgumentException(
                "Invalid mission ID"
            )
        }

        if (request.requesterName.isBlank()) {
            throw IllegalArgumentException(
                "Requester name is required"
            )
        }

        if (request.travelObjectives.isBlank()) {
            throw IllegalArgumentException(
                "Travel objectives are required"
            )
        }

        if (
            request.travelStartDate.isAfter(
                request.travelEndDate
            )
        ) {
            throw IllegalArgumentException(
                "Travel start date cannot be after travel end date"
            )
        }
    }

    // =========================
    // JOB LEVEL PARSER
    // =========================
    private fun parseJobLevel(
        value: String?
    ): JobLevel? {

        if (value.isNullOrBlank()) {
            return null
        }

        return try {
            JobLevel.valueOf(
                value.trim().uppercase()
            )
        } catch (ex: IllegalArgumentException) {
            throw IllegalArgumentException(
                "Invalid job level: $value"
            )
        }
    }

    // =========================
    // STATUS PARSER
    // =========================
    private fun parseStatus(
        value: String
    ): MileageClaimStatus {

        return try {
            MileageClaimStatus.valueOf(
                value.trim().uppercase()
            )
        } catch (ex: IllegalArgumentException) {
            throw IllegalArgumentException(
                "Invalid status: $value"
            )
        }
    }
}