package com.onemore.mission.vehicle.repository

import com.onemore.mission.vehicle.domain.VehicleRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VehicleRequestRepository : JpaRepository<VehicleRequest, Long> {

    fun findByMissionId(missionId: Long): List<VehicleRequest>
}