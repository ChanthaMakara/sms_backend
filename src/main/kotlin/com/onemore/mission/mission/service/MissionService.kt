package com.onemore.mission.mission.service

import com.onemore.mission.mission.domain.Mission
import com.onemore.mission.mission.domain.MissionStatus
import com.onemore.mission.mission.dto.request.CreateMissionRequest
import com.onemore.mission.mission.dto.request.UpdateMissionRequest
import com.onemore.mission.mission.dto.response.MissionResponse
import com.onemore.mission.mission.mapper.MissionMapper
import com.onemore.mission.mission.repository.MissionRepository
import com.onemore.mission.user.repository.UserRepository
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service

@Service
class MissionService(
    private val missionRepository: MissionRepository,
    private val missionMapper: MissionMapper,
    private val userRepository: UserRepository
) {

    companion object {
        private val APPROVER_ROLES = setOf(
            "ROLE_FUNCTION_MANAGER", "ROLE_HRBP", "ROLE_FINANCE",
            "ROLE_BIZOPS", "ROLE_EXECUTIVE", "ROLE_ADMIN"
        )
    }

    fun create(
        request: CreateMissionRequest,
        requesterId: Long,
        requesterName: String,
        callerRoles: List<String>
    ): MissionResponse {

        var finalRequesterId = requesterId
        var finalRequesterName = requesterName

        if (request.onBehalfOfUserId != null && request.onBehalfOfUserId != requesterId) {
            val isApprover = callerRoles.any { it in APPROVER_ROLES }
            if (!isApprover) {
                throw AccessDeniedException("Only approvers can create a mission on behalf of another staff member.")
            }

            val targetUser = userRepository.findById(request.onBehalfOfUserId)
                .orElseThrow { IllegalArgumentException("Selected staff member not found.") }

            finalRequesterId = targetUser.id
            finalRequesterName = targetUser.fullName
        }

        val mission = Mission(
            missionCode = "MIS-${System.currentTimeMillis()}",
            requesterId = finalRequesterId,
            requesterName = finalRequesterName,
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

    fun update(id: Long, request: UpdateMissionRequest, callerId: Long, callerRoles: List<String>): MissionResponse {
        val mission = missionRepository.findById(id)
            .orElseThrow {
                IllegalArgumentException("Mission not found with id: $id")
            }

        checkOwnerOrAdminWhileDraft(mission, callerId, callerRoles, action = "update")

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

    fun delete(id: Long, callerId: Long, callerRoles: List<String>) {
        val mission = missionRepository.findById(id)
            .orElseThrow {
                IllegalArgumentException("Mission not found with id: $id")
            }

        checkOwnerOrAdminWhileDraft(mission, callerId, callerRoles, action = "delete")

        missionRepository.delete(mission)
    }

    private fun checkOwnerOrAdminWhileDraft(
        mission: Mission,
        callerId: Long,
        callerRoles: List<String>,
        action: String
    ) {
        val isAdmin = callerRoles.contains("ROLE_ADMIN")
        val isOwner = mission.requesterId == callerId
        val isDraft = mission.status == MissionStatus.DRAFT

        if (!isAdmin && !(isOwner && isDraft)) {
            throw AccessDeniedException("You can only $action your own mission while it is still in draft.")
        }
    }
}
