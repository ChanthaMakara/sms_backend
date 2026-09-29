package com.onemore.mission.report.service

import com.onemore.mission.report.domain.ActivityReport
import com.onemore.mission.report.domain.ActivityReportStatus
import com.onemore.mission.report.dto.request.ActivityReportCommentRequest
import com.onemore.mission.report.dto.request.CreateActivityReportRequest
import com.onemore.mission.report.dto.response.ActivityReportResponse
import com.onemore.mission.report.mapper.ActivityReportMapper
import com.onemore.mission.report.repository.ActivityReportRepository
import com.onemore.mission.security.CustomUserDetails
import com.onemore.mission.user.domain.UserRole
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.LocalDate

@Service
class ActivityReportService(
    private val activityReportRepository: ActivityReportRepository,
    private val activityReportMapper: ActivityReportMapper
) {

    @Transactional
    fun createReport(
        missionId: Long,
        request: CreateActivityReportRequest
    ): ActivityReportResponse {

        if (missionId <= 0) {
            throw IllegalArgumentException("Invalid mission ID")
        }

        if (request.travelStartDate.isAfter(request.travelEndDate)) {
            throw IllegalArgumentException(
                "Travel start date cannot be after travel end date"
            )
        }

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

        return activityReportMapper.toResponse(
            activityReportRepository.save(report)
        )
    }

    @Transactional(readOnly = true)
    fun getReportsByMissionId(
        missionId: Long
    ): List<ActivityReportResponse> {

        return activityReportRepository
            .findByMissionId(missionId)
            .sortedByDescending { it.createdAt }
            .map(activityReportMapper::toResponse)
    }

    @Transactional(readOnly = true)
    fun getAllReports(): List<ActivityReportResponse> {

        return activityReportRepository
            .findAll()
            .sortedByDescending { it.createdAt }
            .map(activityReportMapper::toResponse)
    }

    @Transactional
    fun updateReport(
        missionId: Long,
        reportId: Long,
        request: CreateActivityReportRequest
    ): ActivityReportResponse {

        val report = activityReportRepository
            .findById(reportId)
            .orElseThrow {
                IllegalArgumentException(
                    "Activity report not found: $reportId"
                )
            }

        if (report.missionId != missionId) {
            throw IllegalArgumentException(
                "Activity report does not belong to mission: $missionId"
            )
        }

        if (report.status != ActivityReportStatus.DRAFT) {
            throw IllegalStateException(
                "Only draft reports can be updated"
            )
        }

        if (request.travelStartDate.isAfter(request.travelEndDate)) {
            throw IllegalArgumentException(
                "Travel start date cannot be after travel end date"
            )
        }

        report.requesterName = request.requesterName
        report.requesterId = request.requesterId
        report.position = request.position
        report.function = request.function
        report.business = request.business
        report.basedLocation = request.basedLocation
        report.destinationLocation = request.destinationLocation
        report.travelStartDate = request.travelStartDate
        report.travelEndDate = request.travelEndDate
        report.travelObjectives = request.travelObjectives
        report.achievedResults = request.achievedResults
        report.nextPlan = request.nextPlan
        report.attachedDocuments = request.attachedDocuments
        report.requesterSignatureDate = request.requesterSignatureDate
        report.updatedAt = Instant.now()

        return activityReportMapper.toResponse(
            activityReportRepository.save(report)
        )
    }

    @Transactional
    fun addComment(
        reportId: Long,
        request: ActivityReportCommentRequest,
        userDetails: CustomUserDetails
    ): ActivityReportResponse {

        val report = activityReportRepository
            .findById(reportId)
            .orElseThrow {
                IllegalArgumentException(
                    "Activity report not found: $reportId"
                )
            }

        val roles = userDetails.authorities
            .map { it.authority }

        when {
            roles.contains(UserRole.ROLE_FUNCTION_MANAGER.name) ||
            roles.contains(UserRole.ROLE_ADMIN.name) -> {

                report.functionManagerComment = request.comment
                report.functionManagerSignatureDate = LocalDate.now()
            }

            roles.contains(UserRole.ROLE_BIZOPS.name) -> {

                report.bizOpsComment = request.comment
                report.bizOpsSignatureDate = LocalDate.now()
            }

            else -> {
                throw IllegalStateException(
                    "User is not authorized to comment on activity reports"
                )
            }
        }

        report.updatedAt = Instant.now()

        return activityReportMapper.toResponse(
            activityReportRepository.save(report)
        )
    }

    @Transactional
    fun updateStatus(
        reportId: Long,
        status: String
    ): ActivityReportResponse {

        val report = activityReportRepository
            .findById(reportId)
            .orElseThrow {
                IllegalArgumentException(
                    "Activity report not found: $reportId"
                )
            }

        val newStatus = try {
            ActivityReportStatus.valueOf(
                status.uppercase()
            )
        } catch (ex: IllegalArgumentException) {
            throw IllegalArgumentException(
                "Invalid status: $status"
            )
        }

        report.status = newStatus
        report.updatedAt = Instant.now()

        return activityReportMapper.toResponse(
            activityReportRepository.save(report)
        )
    }

    @Transactional
    fun deleteReport(
        missionId: Long,
        reportId: Long
    ) {

        val report = activityReportRepository
            .findById(reportId)
            .orElseThrow {
                IllegalArgumentException(
                    "Activity report not found: $reportId"
                )
            }

        if (report.missionId != missionId) {
            throw IllegalArgumentException(
                "Activity report does not belong to mission: $missionId"
            )
        }

        if (report.status != ActivityReportStatus.DRAFT) {
            throw IllegalStateException(
                "Only draft reports can be deleted"
            )
        }

        activityReportRepository.delete(report)
    }
}
