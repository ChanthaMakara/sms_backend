package com.onemore.mission.analytics.dto.response

import java.math.BigDecimal

data class AllowanceSpendResponse(
    val totalSpend: BigDecimal,
    val byBusiness: Map<String, BigDecimal>
)