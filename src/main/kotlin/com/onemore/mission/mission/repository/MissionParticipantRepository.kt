package com.onemore.mission.mission.repository

import com.onemore.mission.mission.domain.MissionParticipant
import org.springframework.data.jpa.repository.JpaRepository

interface MissionParticipantRepository : JpaRepository<MissionParticipant, Long> {
    fun findAllByMissionIdIn(missionIds: Collection<Long>): List<MissionParticipant>
    fun deleteAllByMissionId(missionId: Long)
}
