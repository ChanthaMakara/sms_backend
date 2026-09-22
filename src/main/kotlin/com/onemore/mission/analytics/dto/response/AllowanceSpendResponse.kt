package com.onemore.mission.analytics.dto.response

import java.math.BigDecimal

data class AllowanceSpendResponse(
    val currency: String,
    val totalSpend: BigDecimal,
    val budget: BigDecimal,
    val pendingSettlement: BigDecimal,
    val changeVsLastMonthPct: Double,
    val monthly: List<MonthlySpend>
)

data class MonthlySpend(
    val month: String,
    val planned: BigDecimal,
    val actual: BigDecimal
)