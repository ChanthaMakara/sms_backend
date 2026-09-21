package com.onemore.mission.mission.service

import com.onemore.mission.mission.domain.Mission
import com.onemore.mission.mission.dto.request.CreateMissionRequest
import com.onemore.mission.mission.dto.request.UpdateMissionRequest
import com.onemore.mission.mission.dto.response.MissionResponse
import com.onemore.mission.mission.mapper.MissionMapper
import com.onemore.mission.mission.repository.MissionRepository
import org.springframework.stereotype.Service

@Service
class MissionService(
    private val missionRepository: MissionRepository,
    private val missionMapper: MissionMapper
) {

    fun create(
        request: CreateMissionRequest,
        requesterId: Long,
        requesterName: String
    ): MissionResponse {

        val mission = Mission(
            missionCode = "MIS-${System.currentTimeMillis()}",
            requesterId = requesterId,
            requesterName = requesterName,
            position = request.position,
            functionName = request.functionName,
            business = request.business,
            jobLevel = request.jobLevel,
            basedLocation = request.basedLocation,
            destinationLocation = request.destinationLocation,
            locationTier = request.locationTier,
            travelObjectives = request.travelObjectives,
            departureDate = request.departureDate,
            departureTime = request.departureTime,
            arrivalDate = request.arrivalDate,
            arrivalTime = request.arrivalTime,
            numberOfTravelDays = request.numberOfTravelDays,
            description = request.description
        )

        val savedMission = missionRepository.save(mission)

        return missionMapper.toResponse(savedMission)
    }

    fun getById(id: Long): MissionResponse {
        val mission = missionRepository.findById(id)
            .orElseThrow {
                IllegalArgumentException("Mission not found with id: $id")
            }

        return missionMapper.toResponse(mission)
    }

    fun getAll(): List<MissionResponse> {
        return missionRepository.findAll()
            .map(missionMapper::toResponse)
    }

    fun update(id: Long, request: UpdateMissionRequest): MissionResponse {
        val mission = missionRepository.findById(id)
            .orElseThrow {
                IllegalArgumentException("Mission not found with id: $id")
            }

        mission.position = request.position
        mission.functionName = request.functionName
        mission.business = request.business
        mission.jobLevel = request.jobLevel
        mission.basedLocation = request.basedLocation
        mission.destinationLocation = request.destinationLocation
        mission.locationTier = request.locationTier
        mission.travelObjectives = request.travelObjectives
        mission.departureDate = request.departureDate
        mission.departureTime = request.departureTime
        mission.arrivalDate = request.arrivalDate
        mission.arrivalTime = request.arrivalTime
        mission.numberOfTravelDays = request.numberOfTravelDays
        mission.description = request.description

        val savedMission = missionRepository.save(mission)

        return missionMapper.toResponse(savedMission)
    }
}