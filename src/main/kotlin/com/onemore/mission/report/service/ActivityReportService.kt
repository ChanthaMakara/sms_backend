package com.onemore.mission.report.service

import com.onemore.mission.report.dto.request.ActivityReportCommentRequest
import com.onemore.mission.security.CustomUserDetails
import com.onemore.mission.user.domain.UserRole
import java.time.LocalDate
import com.onemore.mission.report.domain.ActivityReport
import com.onemore.mission.report.dto.request.CreateActivityReportRequest
import com.onemore.mission.report.dto.response.ActivityReportResponse
import com.onemore.mission.report.mapper.ActivityReportMapper
import com.onemore.mission.report.repository.ActivityReportRepository
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class ActivityReportService(
    private val activityReportRepository: ActivityReportRepository,
    private val activityReportMapper: ActivityReportMapper
) {

    fun createReport(
        missionId: Long,
        request: CreateActivityReportRequest
    ): ActivityReportResponse {

        val report = ActivityReport(
            missionId = missionId,
            requesterName = request.requesterName,
            requesterId = request.requesterId,
            position = request.position,
            function = request.function,
            business = request.business,
            basedLocation = request.basedLocation,
            destinationLocation = request.destinationLocation,
            travelStartDate = request.travelStartDate,
            travelEndDate = request.travelEndDate,
            travelObjectives = request.travelObjectives,
            achievedResults = request.achievedResults,
            nextPlan = request.nextPlan,
            attachedDocuments = request.attachedDocuments,
            requesterSignatureDate = request.requesterSignatureDate,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val savedReport = activityReportRepository.save(report)

        return activityReportMapper.toResponse(savedReport)
    }

    fun getReportsByMissionId(missionId: Long): List<ActivityReportResponse> {
        return activityReportRepository
            .findByMissionId(missionId)
            .map(activityReportMapper::toResponse)
    }

    fun addComment(
        reportId: Long,
        request: ActivityReportCommentRequest,
        userDetails: CustomUserDetails
    ): ActivityReportResponse {
        val report = activityReportRepository.findById(reportId)
            .orElseThrow { IllegalArgumentException("Activity report not found: $reportId") }

        val roles = userDetails.authorities.map { it.authority }

        when {
            roles.contains(UserRole.ROLE_FUNCTION_MANAGER.name) -> {
                report.functionManagerComment = request.comment
                report.functionManagerSignatureDate = LocalDate.now()
            }

            roles.contains(UserRole.ROLE_BIZOPS.name) -> {
                report.bizOpsComment = request.comment
                report.bizOpsSignatureDate = LocalDate.now()
            }

            else -> {
                throw IllegalStateException("User is not authorized to comment on activity reports")
            }
        }

        report.updatedAt = java.time.Instant.now()

        return activityReportMapper.toResponse(
            activityReportRepository.save(report)
        )
    }
}