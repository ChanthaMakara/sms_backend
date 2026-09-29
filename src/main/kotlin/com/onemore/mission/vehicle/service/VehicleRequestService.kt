package com.onemore.mission.vehicle.service

import com.onemore.mission.common.exception.ResourceNotFoundException
import com.onemore.mission.vehicle.dto.request.CreateVehicleRequestRequest
import com.onemore.mission.vehicle.dto.response.VehicleRequestResponse
import com.onemore.mission.vehicle.mapper.VehicleRequestMapper
import com.onemore.mission.vehicle.repository.VehicleRequestRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class VehicleRequestService(
    private val vehicleRequestRepository: VehicleRequestRepository,
    private val vehicleRequestMapper: VehicleRequestMapper
) {

    // CREATE
    @Transactional
    fun createVehicleRequest(
        missionId: Long,
        request: CreateVehicleRequestRequest
    ): VehicleRequestResponse {

        validateMissionId(missionId)
        validateRequest(request)

        val entity = vehicleRequestMapper.toEntity(
            missionId = missionId,
            request = request
        )

        val saved = vehicleRequestRepository.save(entity)

        return vehicleRequestMapper.toResponse(saved)
    }

    // GET ALL BY MISSION
    @Transactional(readOnly = true)
    fun getVehicleRequestsByMission(
        missionId: Long
    ): List<VehicleRequestResponse> {

        validateMissionId(missionId)

        return vehicleRequestRepository
            .findByMissionId(missionId)
            .map { vehicleRequestMapper.toResponse(it) }
    }

    // GET ALL VEHICLE REQUESTS
    @Transactional(readOnly = true)
    fun getAllVehicleRequests(): List<VehicleRequestResponse> {

        return vehicleRequestRepository
            .findAll()
            .map { vehicleRequestMapper.toResponse(it) }
    }

    // GET BY ID
    @Transactional(readOnly = true)
    fun getVehicleRequestById(
        id: Long
    ): VehicleRequestResponse {

        val entity = vehicleRequestRepository
            .findById(id)
            .orElseThrow {
                ResourceNotFoundException(
                    "Vehicle request not found: $id"
                )
            }

        return vehicleRequestMapper.toResponse(entity)
    }

    // UPDATE
    @Transactional
    fun updateVehicleRequest(
        missionId: Long,
        vehicleRequestId: Long,
        request: CreateVehicleRequestRequest
    ): VehicleRequestResponse {

        validateMissionId(missionId)
        validateRequest(request)

        val entity = vehicleRequestRepository
            .findById(vehicleRequestId)
            .orElseThrow {
                ResourceNotFoundException(
                    "Vehicle request not found: $vehicleRequestId"
                )
            }

        if (entity.missionId != missionId) {
            throw ResourceNotFoundException(
                "Vehicle request not found for mission: $missionId"
            )
        }

        if (entity.status.name == "APPROVED") {
            throw IllegalStateException(
                "Approved vehicle request cannot be updated"
            )
        }

        if (entity.status.name == "COMPLETED") {
            throw IllegalStateException(
                "Completed vehicle request cannot be updated"
            )
        }

        vehicleRequestMapper.updateEntity(
            entity = entity,
            request = request
        )

        val saved = vehicleRequestRepository.save(entity)

        return vehicleRequestMapper.toResponse(saved)
    }

    // UPDATE STATUS
    @Transactional
    fun updateStatus(
        id: Long,
        status: String
    ): VehicleRequestResponse {

        val entity = vehicleRequestRepository
            .findById(id)
            .orElseThrow {
                ResourceNotFoundException(
                    "Vehicle request not found: $id"
                )
            }

        val newStatus = try {
            com.onemore.mission.vehicle.domain.VehicleRequestStatus
                .valueOf(status.uppercase())
        } catch (ex: IllegalArgumentException) {
            throw IllegalArgumentException(
                "Invalid vehicle request status: $status"
            )
        }

        entity.status = newStatus

        val saved = vehicleRequestRepository.save(entity)

        return vehicleRequestMapper.toResponse(saved)
    }

    // DELETE
    @Transactional
    fun deleteVehicleRequest(
        missionId: Long,
        vehicleRequestId: Long
    ) {

        validateMissionId(missionId)

        val entity = vehicleRequestRepository
            .findById(vehicleRequestId)
            .orElseThrow {
                ResourceNotFoundException(
                    "Vehicle request not found: $vehicleRequestId"
                )
            }

        if (entity.missionId != missionId) {
            throw ResourceNotFoundException(
                "Vehicle request not found for mission: $missionId"
            )
        }

        if (entity.status.name == "APPROVED") {
            throw IllegalStateException(
                "Approved vehicle request cannot be deleted"
            )
        }

        if (entity.status.name == "COMPLETED") {
            throw IllegalStateException(
                "Completed vehicle request cannot be deleted"
            )
        }

        vehicleRequestRepository.delete(entity)
    }

    // VALIDATE MISSION ID
    private fun validateMissionId(
        missionId: Long
    ) {

        if (missionId <= 0) {
            throw IllegalArgumentException(
                "Mission ID must be greater than 0"
            )
        }
    }

    // VALIDATE REQUEST
    private fun validateRequest(
        request: CreateVehicleRequestRequest
    ) {

        val requesterName = request.requesterName

        if (requesterName == null || requesterName.trim().isEmpty()) {
            throw IllegalArgumentException(
                "Requester name is required"
            )
        }

        val travelObjectives = request.travelObjectives

        if (
            travelObjectives == null ||
            travelObjectives.trim().isEmpty()
        ) {
            throw IllegalArgumentException(
                "Travel objective is required"
            )
        }

        if (request.travelStartDate.isAfter(request.travelEndDate)) {
            throw IllegalArgumentException(
                "Travel start date cannot be after travel end date"
            )
        }

        if (request.travelDetails.isEmpty()) {
            throw IllegalArgumentException(
                "At least one travel detail is required"
            )
        }

        request.travelDetails.forEachIndexed { index, detail ->

            if (
                detail.date.isBefore(request.travelStartDate) ||
                detail.date.isAfter(request.travelEndDate)
            ) {
                throw IllegalArgumentException(
                    "Travel detail #${index + 1} date must be within the travel period"
                )
            }

            val origin = detail.origin

            if (origin == null || origin.trim().isEmpty()) {
                throw IllegalArgumentException(
                    "Travel detail #${index + 1} origin is required"
                )
            }

            val destination = detail.destination

            if (
                destination == null ||
                destination.trim().isEmpty()
            ) {
                throw IllegalArgumentException(
                    "Travel detail #${index + 1} destination is required"
                )
            }

            val purpose = detail.purposeOfTravel

            if (purpose == null || purpose.trim().isEmpty()) {
                throw IllegalArgumentException(
                    "Travel detail #${index + 1} purpose of travel is required"
                )
            }

            val distance = detail.distanceKm

            if (distance < BigDecimal.ZERO) {
                throw IllegalArgumentException(
                    "Travel detail #${index + 1} distance cannot be negative"
                )
            }
        }
    }
}