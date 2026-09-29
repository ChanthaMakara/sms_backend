package com.onemore.mission.vehicle.service

import com.onemore.mission.common.exception.ResourceNotFoundException
import com.onemore.mission.vehicle.dto.request.CreateVehicleRequestRequest
import com.onemore.mission.vehicle.dto.response.VehicleRequestResponse
import com.onemore.mission.vehicle.mapper.VehicleRequestMapper
import com.onemore.mission.vehicle.repository.VehicleRequestRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

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

        val entity =
            vehicleRequestMapper.toEntity(
                missionId,
                request
            )

        val saved =
            vehicleRequestRepository.save(entity)

        return vehicleRequestMapper.toResponse(saved)
    }

    // GET ALL
    @Transactional(readOnly = true)
    fun getVehicleRequestsByMission(
        missionId: Long
    ): List<VehicleRequestResponse> {

        return vehicleRequestRepository
            .findByMissionId(missionId)
            .map { vehicleRequestMapper.toResponse(it) }
    }

    // GET BY ID
    @Transactional(readOnly = true)
    fun getVehicleRequestById(
        id: Long
    ): VehicleRequestResponse {

        val entity =
            vehicleRequestRepository.findById(id)
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

        val entity =
            vehicleRequestRepository.findById(vehicleRequestId)
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

        vehicleRequestMapper.updateEntity(
            entity,
            request
        )

        val saved =
            vehicleRequestRepository.save(entity)

        return vehicleRequestMapper.toResponse(saved)
    }

    // DELETE
    @Transactional
    fun deleteVehicleRequest(
        missionId: Long,
        vehicleRequestId: Long
    ) {

        val entity =
            vehicleRequestRepository.findById(vehicleRequestId)
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

        vehicleRequestRepository.delete(entity)
    }
}
