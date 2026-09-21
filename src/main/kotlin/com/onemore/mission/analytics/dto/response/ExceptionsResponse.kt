package com.onemore.mission.analytics.dto.response

import java.time.Instant

data class ExceptionsResponse(
    val stuckMissions: List<StuckMission>,
    val rejectedMissions: List<RejectedMission>
)

data class StuckMission(
    val missionId: Long,
    val missionCode: String?,
    val status: String,
    val lastActivityAt: Instant,
    val daysSinceLastActivity: Long
)

data class RejectedMission(
    val missionId: Long,
    val missionCode: String?,
    val rejectedAt: Instant
)