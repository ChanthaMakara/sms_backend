package com.onemore.mission.report.mapper

import com.onemore.mission.report.domain.ActivityReport
import com.onemore.mission.report.dto.response.ActivityReportResponse
import org.springframework.stereotype.Component

@Component
class ActivityReportMapper {

    fun toResponse(report: ActivityReport): ActivityReportResponse {
        return ActivityReportResponse(
            id = report.id!!,
            missionId = report.missionId,
            requesterName = report.requesterName,
            requesterId = report.requesterId,
            position = report.position,
            function = report.function,
            business = report.business,
            basedLocation = report.basedLocation,
            destinationLocation = report.destinationLocation,
            travelStartDate = report.travelStartDate,
            travelEndDate = report.travelEndDate,
            travelObjectives = report.travelObjectives,
            achievedResults = report.achievedResults,
            nextPlan = report.nextPlan,
            attachedDocuments = report.attachedDocuments,
            requesterSignatureDate = report.requesterSignatureDate,
            functionManagerComment = report.functionManagerComment,
            functionManagerSignatureDate = report.functionManagerSignatureDate,
            bizOpsComment = report.bizOpsComment,
            bizOpsSignatureDate = report.bizOpsSignatureDate,
            createdAt = report.createdAt,
            updatedAt = report.updatedAt
        )
    }
}