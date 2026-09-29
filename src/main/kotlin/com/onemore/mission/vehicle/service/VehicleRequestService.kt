package com.onemore.mission.vehicle.service

import com.onemore.mission.common.exception.ResourceNotFoundException
import com.onemore.mission.vehicle.domain.VehicleRequestStatus
import com.onemore.mission.vehicle.dto.request.CreateVehicleRequestRequest
import com.onemore.mission.vehicle.dto.response.VehicleRequestResponse
import com.onemore.mission.vehicle.mapper.VehicleRequestMapper
import com.onemore.mission.vehicle.repository.VehicleRequestRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class VehicleRequestService(
    private val vehicleRequestRepository: VehicleRequestRepository,
    private val vehicleRequestMapper: VehicleRequestMapper
) {

    @Transactional
    fun createVehicleRequest(
        missionId: Long,
        request: CreateVehicleRequestRequest
    ): VehicleRequestResponse {
        val entity = vehicleRequestMapper.toEntity(missionId, request)
        val saved = vehicleRequestRepository.save(entity)
        return vehicleRequestMapper.toResponse(saved)
    }

    @Transactional(readOnly = true)
    fun getVehicleRequestsByMission(missionId: Long): List<VehicleRequestResponse> {
        return vehicleRequestRepository
            .findByMissionId(missionId)
            .map { vehicleRequestMapper.toResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getAllVehicleRequests(): List<VehicleRequestResponse> {
        return vehicleRequestRepository.findAll()
            .sortedByDescending { it.createdAt }
            .map { vehicleRequestMapper.toResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getVehicleRequestById(id: Long): VehicleRequestResponse {
        val entity = vehicleRequestRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Vehicle request not found: $id") }
        return vehicleRequestMapper.toResponse(entity)
    }

    @Transactional
    fun updateVehicleRequest(
        missionId: Long,
        vehicleRequestId: Long,
        request: CreateVehicleRequestRequest
    ): VehicleRequestResponse {
        val entity = vehicleRequestRepository.findById(vehicleRequestId)
            .orElseThrow { ResourceNotFoundException("Vehicle request not found: $vehicleRequestId") }

        if (entity.missionId != missionId) {
            throw ResourceNotFoundException("Vehicle request not found for mission: $missionId")
        }

        vehicleRequestMapper.updateEntity(entity, request)
        val saved = vehicleRequestRepository.save(entity)
        return vehicleRequestMapper.toResponse(saved)
    }

    @Transactional
    fun deleteVehicleRequest(missionId: Long, vehicleRequestId: Long) {
        val entity = vehicleRequestRepository.findById(vehicleRequestId)
            .orElseThrow { ResourceNotFoundException("Vehicle request not found: $vehicleRequestId") }

        if (entity.missionId != missionId) {
            throw ResourceNotFoundException("Vehicle request not found for mission: $missionId")
        }

        vehicleRequestRepository.delete(entity)
    }

    @Transactional
    fun updateStatus(id: Long, status: String): VehicleRequestResponse {
        val entity = vehicleRequestRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Vehicle request not found: $id") }

        entity.status = parseStatus(status)
        entity.updatedAt = Instant.now()

        return vehicleRequestMapper.toResponse(vehicleRequestRepository.save(entity))
    }

    private fun parseStatus(value: String): VehicleRequestStatus {
        return try {
            VehicleRequestStatus.valueOf(value.uppercase())
        } catch (ex: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid status: $value")
        }
    }
}
