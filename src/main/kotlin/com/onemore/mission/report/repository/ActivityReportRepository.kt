package com.onemore.mission.report.repository

import com.onemore.mission.report.domain.ActivityReport
import org.springframework.data.jpa.repository.JpaRepository

interface ActivityReportRepository : JpaRepository<ActivityReport, Long> {

    fun findByMissionId(missionId: Long): List<ActivityReport>
}