package com.onemore.mission.report.controller

import com.onemore.mission.report.dto.request.ActivityReportCommentRequest
import com.onemore.mission.security.CustomUserDetails
import org.springframework.security.core.annotation.AuthenticationPrincipal
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import com.onemore.mission.report.dto.request.CreateActivityReportRequest
import com.onemore.mission.report.dto.response.ActivityReportResponse
import com.onemore.mission.report.service.ActivityReportService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/missions/{missionId}/reports")
@SecurityRequirement(name = "bearerAuth")
class ActivityReportController(
    private val activityReportService: ActivityReportService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createReport(
        @PathVariable missionId: Long,
        @RequestBody request: CreateActivityReportRequest
    ): ActivityReportResponse {
        return activityReportService.createReport(missionId, request)
    }

    @GetMapping
    fun getReports(
        @PathVariable missionId: Long
    ): List<ActivityReportResponse> {
        return activityReportService.getReportsByMissionId(missionId)
    }

    @PostMapping("/{reportId}/comment")
    fun addComment(
        @PathVariable reportId: Long,
        @RequestBody request: ActivityReportCommentRequest,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ActivityReportResponse {
        return activityReportService.addComment(
            reportId,
            request,
            userDetails
        )
    }
}