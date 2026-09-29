package com.onemore.mission.report.controller

import com.onemore.mission.report.dto.request.ActivityReportCommentRequest
import com.onemore.mission.report.dto.request.CreateActivityReportRequest
import com.onemore.mission.report.dto.response.ActivityReportResponse
import com.onemore.mission.report.service.ActivityReportService
import com.onemore.mission.security.CustomUserDetails
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/missions/{missionId}/reports")
@SecurityRequirement(name = "bearerAuth")
class ActivityReportController(
    private val activityReportService: ActivityReportService
) {

    // CREATE
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createReport(
        @PathVariable missionId: Long,
        @RequestBody request: CreateActivityReportRequest
    ): ActivityReportResponse {
        return activityReportService.createReport(
            missionId,
            request
        )
    }

    // GET ALL
    @GetMapping
    fun getReports(
        @PathVariable missionId: Long
    ): List<ActivityReportResponse> {
        return activityReportService.getReportsByMissionId(
            missionId
        )
    }

    // ADD COMMENT
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

    // UPDATE
    @PutMapping("/{reportId}")
    fun updateReport(
        @PathVariable missionId: Long,
        @PathVariable reportId: Long,
        @RequestBody request: CreateActivityReportRequest
    ): ActivityReportResponse {
        return activityReportService.updateReport(
            missionId,
            reportId,
            request
        )
    }

    // DELETE
    @DeleteMapping("/{reportId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteReport(
        @PathVariable missionId: Long,
        @PathVariable reportId: Long
    ) {
        activityReportService.deleteReport(
            missionId,
            reportId
        )
    }
}