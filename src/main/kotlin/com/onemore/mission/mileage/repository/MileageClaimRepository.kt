package com.onemore.mission.mileage.repository

import com.onemore.mission.mileage.domain.MileageClaim
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MileageClaimRepository : JpaRepository<MileageClaim, Long> {

    fun findByMissionId(missionId: Long): List<MileageClaim>
}