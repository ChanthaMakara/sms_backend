package com.onemore.mission.analytics.service

import com.onemore.mission.analytics.dto.response.AllowanceSpendResponse
import com.onemore.mission.analytics.dto.response.ApprovalTurnaroundResponse
import com.onemore.mission.analytics.dto.response.ExceptionsResponse
import com.onemore.mission.analytics.dto.response.MissionsSummaryResponse
import com.onemore.mission.analytics.dto.response.RejectedMission
import com.onemore.mission.analytics.dto.response.StuckMission
import com.onemore.mission.approval.domain.ApprovalDecision
import com.onemore.mission.approval.repository.ApprovalHistoryRepository
import com.onemore.mission.mission.domain.MissionStatus
import com.onemore.mission.mission.repository.MissionRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant

@Service
class AnalyticsService(
    private val missionRepository: MissionRepository,
    private val approvalHistoryRepository: ApprovalHistoryRepository
) {

    companion object {
        private val FINAL_STATUSES = setOf(
            MissionStatus.APPROVED,
            MissionStatus.REJECTED,
            MissionStatus.CANCELLED,
            MissionStatus.SETTLED
        )
        private const val STUCK_THRESHOLD_DAYS = 5L
    }

    fun getMissionsSummary(): MissionsSummaryResponse {
        val counts = missionRepository.countByStatusGrouped()

        val byStatus = counts.associate { it.status.name to it.count }
        val totalMissions = counts.sumOf { it.count }

        return MissionsSummaryResponse(
            totalMissions = totalMissions,
            byStatus = byStatus
        )
    }

    fun getAllowanceSpend(): AllowanceSpendResponse {
        val statuses = listOf(MissionStatus.APPROVED, MissionStatus.SETTLED)
        val rows = missionRepository.sumAllowanceByBusiness(statuses)

        val byBusiness = rows.associate { it.business to it.total }
        val totalSpend = rows.fold(BigDecimal.ZERO) { acc, row -> acc + row.total }

        return AllowanceSpendResponse(
            totalSpend = totalSpend,
            byBusiness = byBusiness
        )
    }

    fun getApprovalTurnaround(): ApprovalTurnaroundResponse {
        val allHistory = approvalHistoryRepository.findAllByOrderByMissionIdAscDecidedAtAsc()

        val byMission = allHistory.groupBy { it.missionId }

        val hoursByStep = mutableMapOf<String, MutableList<Double>>()
        val allDurations = mutableListOf<Double>()

        for ((_, historyForMission) in byMission) {
            for (i in 1 until historyForMission.size) {
                val previous = historyForMission[i - 1]
                val current = historyForMission[i]

                val hours = Duration.between(previous.decidedAt, current.decidedAt)
                    .toMinutes() / 60.0

                val stepName = current.step.name
                hoursByStep.getOrPut(stepName) { mutableListOf() }.add(hours)
                allDurations.add(hours)
            }
        }

        val averageHoursByStep = hoursByStep.mapValues { (_, durations) ->
            durations.average()
        }

        val overallAverageHours = if (allDurations.isNotEmpty()) allDurations.average() else 0.0

        return ApprovalTurnaroundResponse(
            averageHoursByStep = averageHoursByStep,
            overallAverageHours = overallAverageHours
        )
    }

    fun getExceptions(): ExceptionsResponse {
        val allMissions = missionRepository.findAll()
        val allHistory = approvalHistoryRepository.findAllByOrderByMissionIdAscDecidedAtAsc()
        val historyByMission = allHistory.groupBy { it.missionId }

        val now = Instant.now()

        val stuckMissions = allMissions
            .filter { it.status !in FINAL_STATUSES }
            .mapNotNull { mission ->
                val missionHistory = historyByMission[mission.id]
                val lastActivityAt = missionHistory?.maxByOrNull { it.decidedAt }?.decidedAt
                    ?: mission.createdAt

                val daysSince = Duration.between(lastActivityAt, now).toDays()

                if (daysSince >= STUCK_THRESHOLD_DAYS) {
                    StuckMission(
                        missionId = mission.id,
                        missionCode = mission.missionCode,
                        status = mission.status.name,
                        lastActivityAt = lastActivityAt,
                        daysSinceLastActivity = daysSince
                    )
                } else null
            }

        val rejectedMissions = allMissions
            .filter { it.status == MissionStatus.REJECTED }
            .map { mission ->
                val rejectionDecision = historyByMission[mission.id]
                    ?.lastOrNull { it.decision == ApprovalDecision.REJECTED }

                RejectedMission(
                    missionId = mission.id,
                    missionCode = mission.missionCode,
                    rejectedAt = rejectionDecision?.decidedAt ?: mission.updatedAt
                )
            }

        return ExceptionsResponse(
            stuckMissions = stuckMissions,
            rejectedMissions = rejectedMissions
        )
    }
}