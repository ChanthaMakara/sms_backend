package com.onemore.mission.analytics.dto.response

data class MissionsSummaryResponse(
    val totalMissions: Long,
    val byStatus: Map<String, Long>
)