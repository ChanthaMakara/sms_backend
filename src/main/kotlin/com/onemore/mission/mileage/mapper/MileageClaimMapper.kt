package com.onemore.mission.mileage.mapper

import com.onemore.mission.common.exception.BusinessException
import com.onemore.mission.mileage.domain.MileageClaim
import com.onemore.mission.mileage.domain.MileageClaimStatus
import com.onemore.mission.mileage.domain.MileageTravelDetail
import com.onemore.mission.mileage.dto.request.CreateMileageClaimRequest
import com.onemore.mission.mileage.dto.response.MileageClaimResponse
import com.onemore.mission.mileage.dto.response.MileageTravelDetailResponse
import com.onemore.mission.user.domain.JobLevel
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode

@Component
class MileageClaimMapper {

    companion object {
        val RATE_PER_KM: BigDecimal = BigDecimal("0.20")
    }

    fun toEntity(missionId: Long, request: CreateMileageClaimRequest): MileageClaim {
        val jobLevel = request.jobLevel?.let { parseJobLevel(it) }

        val totalDistanceKm = request.travelDetails
            .map { it.distanceKm }
            .fold(BigDecimal.ZERO) { acc, distance -> acc + distance }

        val totalClaimAmount = totalDistanceKm.multiply(RATE_PER_KM).setScale(2, RoundingMode.HALF_UP)

        val mileageClaim = MileageClaim(
            missionId = missionId,
            vehicleRequestId = request.vehicleRequestId,
            requesterName = request.requesterName,
            requesterId = request.requesterId,
            position = request.position,
            function = request.function,
            business = request.business,
            jobLevel = jobLevel,
            basedLocation = request.basedLocation,
            destinationLocation = request.destinationLocation,
            travelStartDate = request.travelStartDate,
            travelEndDate = request.travelEndDate,
            travelObjectives = request.travelObjectives,
            totalDistanceKm = totalDistanceKm,
            totalClaimAmount = totalClaimAmount,
            status = MileageClaimStatus.DRAFT
        )

        val details = request.travelDetails.map { detail ->
            MileageTravelDetail(
                mileageClaim = mileageClaim,
                travelDate = detail.date,
                origin = detail.origin,
                destination = detail.destination,
                purposeOfTravel = detail.purposeOfTravel,
                distanceKm = detail.distanceKm,
                remarks = detail.remarks
            )
        }

        mileageClaim.travelDetails.addAll(details)

        return mileageClaim
    }

    fun toResponse(entity: MileageClaim): MileageClaimResponse {
        return MileageClaimResponse(
            id = requireNotNull(entity.id),
            missionId = entity.missionId,
            vehicleRequestId = entity.vehicleRequestId,
            requesterName = entity.requesterName,
            requesterId = entity.requesterId,
            position = entity.position,
            function = entity.function,
            business = entity.business,
            jobLevel = entity.jobLevel?.name,
            basedLocation = entity.basedLocation,
            destinationLocation = entity.destinationLocation,
            travelStartDate = entity.travelStartDate,
            travelEndDate = entity.travelEndDate,
            travelObjectives = entity.travelObjectives,
            travelDetails = entity.travelDetails.map { detail ->
                MileageTravelDetailResponse(
                    id = requireNotNull(detail.id),
                    date = detail.travelDate,
                    origin = detail.origin,
                    destination = detail.destination,
                    purposeOfTravel = detail.purposeOfTravel,
                    distanceKm = detail.distanceKm,
                    remarks = detail.remarks
                )
            },
            totalDistanceKm = entity.totalDistanceKm,
            totalClaimAmount = entity.totalClaimAmount,
            status = entity.status.name,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    private fun parseJobLevel(value: String): JobLevel {
        return try {
            JobLevel.valueOf(value.uppercase())
        } catch (ex: IllegalArgumentException) {
            throw BusinessException("Invalid jobLevel: $value")
        }
    }
}