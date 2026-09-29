package com.onemore.mission.report.controller

import com.onemore.mission.common.response.ApiResponse
import com.onemore.mission.report.dto.request.UpdateActivityReportStatusRequest
import com.onemore.mission.report.dto.response.ActivityReportResponse
import com.onemore.mission.report.service.ActivityReportService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/annexes/activity-reports")
class AnnexActivityReportController(
    private val activityReportService: ActivityReportService
) {

    @GetMapping
    fun getAllReports(): ApiResponse<List<ActivityReportResponse>> {
        return ApiResponse.success(activityReportService.getAllReports())
    }

    @PatchMapping("/{id}")
    fun updateStatus(
        @PathVariable id: Long,
        @RequestBody request: UpdateActivityReportStatusRequest
    ): ApiResponse<ActivityReportResponse> {
        return ApiResponse.success(activityReportService.updateStatus(id, request.status))
    }
}
