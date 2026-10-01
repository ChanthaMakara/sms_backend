package com.onemore.mission.mission.service

import com.onemore.mission.mission.domain.Mission
import com.onemore.mission.mission.domain.MissionParticipant
import com.onemore.mission.mission.domain.MissionStatus
import com.onemore.mission.mission.domain.MissionType
import com.onemore.mission.mission.repository.MissionParticipantRepository
import com.onemore.mission.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MissionParticipantService(
    private val participantRepository: MissionParticipantRepository,
    private val userRepository: UserRepository
) {

    @Transactional
    fun rebuild(mission: Mission, extraIds: List<Long>) {
        if (mission.status != MissionStatus.DRAFT) {
            throw IllegalArgumentException("Participants can only be changed while the mission is in DRAFT.")
        }

        val others: List<Long> = if (mission.missionType == MissionType.GROUP) {
            extraIds.filter { it != mission.requesterId }.distinct()
        } else emptyList()

        if (mission.missionType == MissionType.GROUP && others.isEmpty()) {
            throw IllegalArgumentException("A GROUP mission needs at least one participant besides the requester.")
        }

        val users = userRepository.findAllById(others)
        val missing = others - users.map { it.id }.toSet()
        if (missing.isNotEmpty()) throw IllegalArgumentException("Staff not found: $missing")
        val inactive = users.filter { !it.isActive }.map { it.fullName }
        if (inactive.isNotEmpty()) throw IllegalArgumentException("Inactive staff cannot be added: $inactive")

        val requester = userRepository.findById(mission.requesterId)
            .orElseThrow { IllegalArgumentException("Requester not found.") }

        participantRepository.deleteAllByMissionId(mission.id)
        participantRepository.flush()

        val rows = mutableListOf(
            MissionParticipant(
                missionId = mission.id,
                employeeId = requester.id,
                employeeCode = requester.employeeCode,
                fullName = mission.requesterName,
                jobLevel = mission.jobLevel,
                functionName = mission.functionName,
                business = mission.business,
                requester = true
            )
        )
        users.forEach { u ->
            rows.add(
                MissionParticipant(
                    missionId = mission.id,
                    employeeId = u.id,
                    employeeCode = u.employeeCode,
                    fullName = u.fullName,
                    jobLevel = u.jobLevel,
                    functionName = u.functionName,
                    business = u.business,
                    requester = false
                )
            )
        }
        participantRepository.saveAll(rows)
    }

    @Transactional(readOnly = true)
    fun listForMissions(missionIds: Collection<Long>): List<MissionParticipant> =
        if (missionIds.isEmpty()) emptyList()
        else participantRepository.findAllByMissionIdIn(missionIds)
            .sortedWith(compareByDescending<MissionParticipant> { it.requester }.thenBy { it.fullName })

    @Transactional(readOnly = true)
    fun list(missionId: Long): List<MissionParticipant> = listForMissions(listOf(missionId))
}
