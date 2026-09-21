package com.onemore.mission.analytics.dto.response

data class ApprovalTurnaroundResponse(
    val averageHoursByStep: Map<String, Double>,
    val overallAverageHours: Double
)