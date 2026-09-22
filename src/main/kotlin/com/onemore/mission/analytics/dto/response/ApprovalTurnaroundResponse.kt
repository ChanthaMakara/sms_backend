package com.onemore.mission.analytics.dto.response

data class ApprovalTurnaroundResponse(
    val averageHours: Double,
    val medianHours: Double,
    val withinSlaPct: Double,
    val changeVsLastMonthPct: Double,
    val byStage: List<StageTurnaround>
)

data class StageTurnaround(
    val stage: String,
    val hours: Double
)