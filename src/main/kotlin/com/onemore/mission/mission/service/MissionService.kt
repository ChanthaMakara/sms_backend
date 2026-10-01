package com.onemore.mission.mission.service

import com.onemore.mission.allowance.service.AllowanceService
import com.onemore.mission.mission.domain.Mission
import com.onemore.mission.mission.domain.MissionStatus
import com.onemore.mission.mission.domain.MissionType
import com.onemore.mission.mission.dto.request.CreateMissionRequest
import com.onemore.mission.mission.dto.request.UpdateMissionParticipantsRequest
import com.onemore.mission.mission.dto.request.UpdateMissionRequest
import com.onemore.mission.mission.dto.response.MissionParticipantResponse
import com.onemore.mission.mission.dto.response.MissionResponse
import com.onemore.mission.mission.mapper.MissionMapper
import com.onemore.mission.mission.repository.MissionRepository
import com.onemore.mission.user.repository.UserRepository
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MissionService(
    private val missionRepository: MissionRepository,
    private val missionMapper: MissionMapper,
    private val userRepository: UserRepository,
    private val participantService: MissionParticipantService,
    private val allowanceService: AllowanceService
) {

    companion object {
        private val APPROVER_ROLES = setOf(
            "ROLE_FUNCTION_MANAGER", "ROLE_HRBP", "ROLE_FINANCE",
            "ROLE_BIZOPS", "ROLE_EXECUTIVE", "ROLE_ADMIN"
        )
    }

    @Transactional
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
            missionType = request.missionType,
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
        participantService.rebuild(savedMission, request.participantIds)

        return toResponse(savedMission)
    }

    fun getById(id: Long): MissionResponse {
        val mission = findMission(id)
        return toResponse(mission)
    }

    fun getAll(): List<MissionResponse> {
        val missions = missionRepository.findAll()
        val byMission = participantService.listForMissions(missions.map { it.id }).groupBy { it.missionId }
        return missions.map { m ->
            missionMapper.toResponse(
                m,
                (byMission[m.id] ?: emptyList()).map(MissionParticipantResponse::from)
            )
        }
    }

    fun getParticipants(id: Long): List<MissionParticipantResponse> {
        findMission(id)
        return participantService.list(id).map(MissionParticipantResponse::from)
    }

    @Transactional
    fun updateParticipants(
        id: Long,
        request: UpdateMissionParticipantsRequest,
        callerId: Long,
        callerRoles: List<String>
    ): List<MissionParticipantResponse> {
        val mission = findMission(id)

        checkOwnerOrAdminWhileDraft(mission, callerId, callerRoles, action = "change participants of")

        if (mission.missionType != MissionType.GROUP) {
            throw IllegalArgumentException("Only GROUP missions can have extra participants.")
        }

        participantService.rebuild(mission, request.participantIds)
        allowanceService.calculateAndSave(id)

        return participantService.list(id).map(MissionParticipantResponse::from)
    }

    fun update(id: Long, request: UpdateMissionRequest, callerId: Long, callerRoles: List<String>): MissionResponse {
        val mission = findMission(id)

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

        return toResponse(savedMission)
    }

    fun delete(id: Long, callerId: Long, callerRoles: List<String>) {
        val mission = findMission(id)

        checkOwnerOrAdminWhileDraft(mission, callerId, callerRoles, action = "delete")

        missionRepository.delete(mission)
    }

    private fun findMission(id: Long): Mission =
        missionRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Mission not found with id: $id") }

    private fun toResponse(mission: Mission): MissionResponse =
        missionMapper.toResponse(
            mission,
            participantService.list(mission.id).map(MissionParticipantResponse::from)
        )

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
