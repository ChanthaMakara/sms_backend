package com.onemore.mission.vehicle.mapper

import com.onemore.mission.common.exception.BusinessException
import com.onemore.mission.user.domain.JobLevel
import com.onemore.mission.vehicle.domain.VehicleRequest
import com.onemore.mission.vehicle.domain.VehicleRequestStatus
import com.onemore.mission.vehicle.domain.VehicleTravelDetail
import com.onemore.mission.vehicle.dto.request.CreateVehicleRequestRequest
import com.onemore.mission.vehicle.dto.response.VehicleRequestResponse
import com.onemore.mission.vehicle.dto.response.VehicleTravelDetailResponse
import org.springframework.stereotype.Component

@Component
class VehicleRequestMapper {

    fun toEntity(missionId: Long, request: CreateVehicleRequestRequest): VehicleRequest {
        val jobLevel = request.jobLevel?.let { parseJobLevel(it) }

        val vehicleRequest = VehicleRequest(
            missionId = missionId,
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
            status = VehicleRequestStatus.DRAFT
        )

        val details = request.travelDetails.map { detail ->
            VehicleTravelDetail(
                vehicleRequest = vehicleRequest,
                travelDate = detail.date,
                origin = detail.origin,
                destination = detail.destination,
                purposeOfTravel = detail.purposeOfTravel,
                distanceKm = detail.distanceKm,
                remarks = detail.remarks
            )
        }

        vehicleRequest.travelDetails.addAll(details)

        return vehicleRequest
    }

    fun toResponse(entity: VehicleRequest): VehicleRequestResponse {
        return VehicleRequestResponse(
            id = requireNotNull(entity.id),
            missionId = entity.missionId,
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
                VehicleTravelDetailResponse(
                    id = requireNotNull(detail.id),
                    date = detail.travelDate,
                    origin = detail.origin,
                    destination = detail.destination,
                    purposeOfTravel = detail.purposeOfTravel,
                    distanceKm = detail.distanceKm,
                    remarks = detail.remarks
                )
            },
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