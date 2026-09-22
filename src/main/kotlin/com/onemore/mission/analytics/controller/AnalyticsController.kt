package com.onemore.mission.analytics.controller

import com.onemore.mission.analytics.dto.response.AllowanceSpendResponse
import com.onemore.mission.analytics.dto.response.ApprovalTurnaroundResponse
import com.onemore.mission.analytics.dto.response.ExceptionsResponse
import com.onemore.mission.analytics.dto.response.MissionsSummaryResponse
import com.onemore.mission.analytics.service.AnalyticsService
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
    fun getMissionsSummary(): ResponseEntity<MissionsSummaryResponse> {
        return ResponseEntity.ok(analyticsService.getMissionsSummary())
    }

    @GetMapping("/allowances/spend")
    fun getAllowanceSpend(): ResponseEntity<AllowanceSpendResponse> {
        return ResponseEntity.ok(analyticsService.getAllowanceSpend())
    }

    @GetMapping("/approvals/turnaround")
    fun getApprovalTurnaround(): ResponseEntity<ApprovalTurnaroundResponse> {
        return ResponseEntity.ok(analyticsService.getApprovalTurnaround())
    }

    @GetMapping("/exceptions")
    fun getExceptions(): ResponseEntity<ExceptionsResponse> {
        return ResponseEntity.ok(analyticsService.getExceptions())
    }
}