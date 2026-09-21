package com.onemore.mission.analytics.controller

import com.onemore.mission.analytics.dto.response.AllowanceSpendResponse
import com.onemore.mission.analytics.dto.response.ApprovalTurnaroundResponse
import com.onemore.mission.analytics.dto.response.ExceptionsResponse
import com.onemore.mission.analytics.dto.response.MissionsSummaryResponse
import com.onemore.mission.analytics.service.AnalyticsService
import com.onemore.mission.common.response.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/analytics")
@PreAuthorize("hasAnyRole('ADMIN', 'EXECUTIVE', 'FINANCE', 'BIZOPS', 'FUNCTION_MANAGER', 'HRBP')")
class AnalyticsController(
    private val analyticsService: AnalyticsService
) {
    @GetMapping("/missions/summary")
    fun getMissionsSummary(): ResponseEntity<ApiResponse<MissionsSummaryResponse>> {
        val result = analyticsService.getMissionsSummary()
        return ResponseEntity.ok(ApiResponse.success(result))
    }

    @GetMapping("/allowances/spend")
    fun getAllowanceSpend(): ResponseEntity<ApiResponse<AllowanceSpendResponse>> {
        val result = analyticsService.getAllowanceSpend()
        return ResponseEntity.ok(ApiResponse.success(result))
    }

    @GetMapping("/approvals/turnaround")
    fun getApprovalTurnaround(): ResponseEntity<ApiResponse<ApprovalTurnaroundResponse>> {
        val result = analyticsService.getApprovalTurnaround()
        return ResponseEntity.ok(ApiResponse.success(result))
    }

    @GetMapping("/exceptions")
    fun getExceptions(): ResponseEntity<ApiResponse<ExceptionsResponse>> {
        val result = analyticsService.getExceptions()
        return ResponseEntity.ok(ApiResponse.success(result))
    }
}