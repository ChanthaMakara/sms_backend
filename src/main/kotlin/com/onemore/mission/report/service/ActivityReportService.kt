package com.onemore.mission.report.service

import com.onemore.mission.mission.domain.MissionStatus
import com.onemore.mission.mission.repository.MissionRepository
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
    private val activityReportMapper: ActivityReportMapper,
    private val missionRepository: MissionRepository
) {

    companion object {
        // Mission status once FM and BizOps have both approved the report.
        // Change to MissionStatus.SETTLED if you want the mission closed here.
        private val MISSION_STATUS_AFTER_REPORT_APPROVED = MissionStatus.REPORT_SUBMITTED
    }

    @Transactional
    fun createReport(
        missionId: Long,
        request: CreateActivityReportRequest
    ): ActivityReportResponse {

        if (missionId <= 0) {
            throw IllegalArgumentException("Invalid mission ID")
        }

        if (!missionRepository.existsById(missionId)) {
            throw IllegalArgumentException("Mission not found: $missionId")
        }

        if (request.travelStartDate.isAfter(request.travelEndDate)) {
            throw IllegalArgumentException("Travel start date cannot be after travel end date")
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
            // A written report goes straight to FM review
            status = ActivityReportStatus.SUBMITTED,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        return activityReportMapper.toResponse(activityReportRepository.save(report))
    }

    @Transactional(readOnly = true)
    fun getReportsByMissionId(missionId: Long): List<ActivityReportResponse> {
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

        val report = findReport(reportId)

        if (report.missionId != missionId) {
            throw IllegalArgumentException("Activity report does not belong to mission: $missionId")
        }

        requireEditable(report)

        if (request.travelStartDate.isAfter(request.travelEndDate)) {
            throw IllegalArgumentException("Travel start date cannot be after travel end date")
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

        return activityReportMapper.toResponse(activityReportRepository.save(report))
    }

    /**
     * Review order: Function Manager comments first, then BizOps.
     * Admin may act at whichever step is next.
     */
    @Transactional
    fun addComment(
        reportId: Long,
        request: ActivityReportCommentRequest,
        userDetails: CustomUserDetails
    ): ActivityReportResponse {

        if (request.comment.isBlank()) {
            throw IllegalArgumentException("Comment cannot be empty")
        }

        val report = findReport(reportId)
        val roles = rolesOf(userDetails)
        val isAdmin = UserRole.ROLE_ADMIN.name in roles
        val isFm = isAdmin || UserRole.ROLE_FUNCTION_MANAGER.name in roles
        val isBizOps = isAdmin || UserRole.ROLE_BIZOPS.name in roles

        when {
            // Step 1: Function Manager
            report.status == ActivityReportStatus.SUBMITTED && report.functionManagerComment == null -> {
                if (!isFm) throw IllegalStateException("Only the Function Manager can review this report now")
                report.functionManagerComment = request.comment.trim()
                report.functionManagerSignatureDate = LocalDate.now()
            }

            // Step 2: BizOps (after FM)
            report.status == ActivityReportStatus.UNDER_REVIEW &&
                report.functionManagerComment != null && report.bizOpsComment == null -> {
                if (!isBizOps) throw IllegalStateException("Only Business Operations can review this report now")
                report.bizOpsComment = request.comment.trim()
                report.bizOpsSignatureDate = LocalDate.now()
            }

            else -> throw IllegalStateException(
                "This report is not waiting for a comment (status: ${report.status})"
            )
        }

        report.updatedAt = Instant.now()
        return activityReportMapper.toResponse(activityReportRepository.save(report))
    }

    /**
     * SUBMITTED -> (FM comment) UNDER_REVIEW -> (BizOps comment) APPROVED
     * Either reviewer may REJECT at their own step.
     * APPROVED also moves the mission, in the same transaction.
     */
    @Transactional
    fun updateStatus(
        reportId: Long,
        status: String,
        userDetails: CustomUserDetails
    ): ActivityReportResponse {

        val report = findReport(reportId)

        val newStatus = try {
            ActivityReportStatus.valueOf(status.uppercase())
        } catch (ex: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid status: $status")
        }

        val roles = rolesOf(userDetails)
        val isAdmin = UserRole.ROLE_ADMIN.name in roles
        val isFm = isAdmin || UserRole.ROLE_FUNCTION_MANAGER.name in roles
        val isBizOps = isAdmin || UserRole.ROLE_BIZOPS.name in roles

        when (newStatus) {
            ActivityReportStatus.SUBMITTED -> {
                if (report.status != ActivityReportStatus.DRAFT) {
                    throw IllegalStateException("Only draft reports can be submitted")
                }
            }

            ActivityReportStatus.UNDER_REVIEW -> {
                if (!isFm) throw IllegalStateException("Only the Function Manager can move a report to review")
                if (report.status != ActivityReportStatus.SUBMITTED || report.functionManagerComment == null) {
                    throw IllegalStateException("Function Manager comment is required first")
                }
            }

            ActivityReportStatus.APPROVED -> {
                if (!isBizOps) throw IllegalStateException("Only Business Operations can approve this report")
                if (report.status != ActivityReportStatus.UNDER_REVIEW ||
                    report.functionManagerComment == null || report.bizOpsComment == null
                ) {
                    throw IllegalStateException(
                        "Both Function Manager and BizOps must review before approval"
                    )
                }
                completeMission(report.missionId)
            }

            ActivityReportStatus.REJECTED -> {
                val allowed =
                    (report.status == ActivityReportStatus.SUBMITTED && isFm) ||
                    (report.status == ActivityReportStatus.UNDER_REVIEW && isBizOps)
                if (!allowed) throw IllegalStateException("You cannot reject this report at its current step")
            }

            ActivityReportStatus.DRAFT -> throw IllegalStateException("A report cannot go back to draft")
        }

        report.status = newStatus
        report.updatedAt = Instant.now()
        return activityReportMapper.toResponse(activityReportRepository.save(report))
    }

    @Transactional
    fun deleteReport(missionId: Long, reportId: Long) {

        val report = findReport(reportId)

        if (report.missionId != missionId) {
            throw IllegalArgumentException("Activity report does not belong to mission: $missionId")
        }

        requireEditable(report)
        activityReportRepository.delete(report)
    }

    // ---------- helpers ----------

    private fun findReport(reportId: Long): ActivityReport =
        activityReportRepository.findById(reportId).orElseThrow {
            IllegalArgumentException("Activity report not found: $reportId")
        }

    private fun rolesOf(userDetails: CustomUserDetails): Set<String> =
        userDetails.authorities.map { it.authority }.toSet()

    /** Editable/deletable until a reviewer has commented. */
    private fun requireEditable(report: ActivityReport) {
        val untouched = report.functionManagerComment == null && report.bizOpsComment == null
        val ok = report.status == ActivityReportStatus.DRAFT ||
            (report.status == ActivityReportStatus.SUBMITTED && untouched)
        if (!ok) throw IllegalStateException("This report is already under review and cannot be changed")
    }

    private fun completeMission(missionId: Long) {
        val mission = missionRepository.findById(missionId).orElseThrow {
            IllegalArgumentException("Mission not found: $missionId")
        }
        // Only move an approved mission forward; never overwrite SETTLED, CANCELLED, etc.
        if (mission.status == MissionStatus.APPROVED) {
            mission.status = MISSION_STATUS_AFTER_REPORT_APPROVED
            missionRepository.save(mission)
        }
    }
}
