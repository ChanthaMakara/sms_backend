package com.onemore.mission.analytics.dto.response

data class MissionsSummaryResponse(
    val total: Long,
    val pending: Long,
    val approved: Long,
    val inProgress: Long,
    val completed: Long,
    val rejected: Long,
    val changeVsLastMonthPct: Double,
    val trend: List<MonthlyMissionTrend>,
    val byDepartment: List<DepartmentMissionCount>
)

data class MonthlyMissionTrend(
    val month: String,
    val submitted: Long,
    val completed: Long
)

data class DepartmentMissionCount(
    val department: String,
    val missions: Long
)