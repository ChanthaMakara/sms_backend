package com.onemore.mission.analytics.dto.response

import java.time.Instant

data class ExceptionsResponse(
    val open: Long,
    val changeVsLastMonthPct: Double,
    val items: List<ExceptionItem>
)

data class ExceptionItem(
    val id: String,
    val missionReference: String,
    val type: String,
    val severity: String,
    val message: String,
    val raisedAt: Instant
)