package com.onemore.mission.settlement.repository

import com.onemore.mission.settlement.domain.Settlement
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface SettlementRepository : JpaRepository<Settlement, Long> {

    fun findByMissionId(missionId: Long): Optional<Settlement>

    fun existsByMissionId(missionId: Long): Boolean
}