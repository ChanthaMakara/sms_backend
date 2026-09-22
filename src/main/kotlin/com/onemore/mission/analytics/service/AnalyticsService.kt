package com.onemore.mission.analytics.service

import com.onemore.mission.analytics.dto.response.AllowanceSpendResponse
import com.onemore.mission.analytics.dto.response.ApprovalTurnaroundResponse
import com.onemore.mission.analytics.dto.response.DepartmentMissionCount
import com.onemore.mission.analytics.dto.response.ExceptionItem
import com.onemore.mission.analytics.dto.response.ExceptionsResponse
import com.onemore.mission.analytics.dto.response.MissionsSummaryResponse
import com.onemore.mission.analytics.dto.response.MonthlyMissionTrend
import com.onemore.mission.analytics.dto.response.MonthlySpend
import com.onemore.mission.analytics.dto.response.StageTurnaround
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
        private val PENDING_STATUSES = setOf(
            MissionStatus.SUBMITTED,
            MissionStatus.FM_REVIEW,
            MissionStatus.HRBP_REVIEW,
            MissionStatus.FINANCE_REVIEW,
            MissionStatus.BIZOPS_REVIEW,
            MissionStatus.EXECUTIVE_REVIEW
        )
        private const val STUCK_THRESHOLD_DAYS = 5L
        private const val SLA_HOURS_THRESHOLD = 24.0
    }

    fun getMissionsSummary(): MissionsSummaryResponse {
        val counts = missionRepository.countByStatusGrouped()
        val byStatus = counts.associate { it.status to it.count }

        val total = counts.sumOf { it.count }
        val pending = PENDING_STATUSES.sumOf { byStatus[it] ?: 0L }
        val approved = byStatus[MissionStatus.APPROVED] ?: 0L
        val completed = (byStatus[MissionStatus.SETTLED] ?: 0L) + (byStatus[MissionStatus.REPORT_SUBMITTED] ?: 0L)
        val rejected = byStatus[MissionStatus.REJECTED] ?: 0L

        return MissionsSummaryResponse(
            total = total,
            pending = pending,
            approved = approved,
            inProgress = pending, // TODO: decide how "in progress" should differ from "pending"
            completed = completed,
            rejected = rejected,
            changeVsLastMonthPct = 0.0, // TODO: needs a month-over-month comparison query
            trend = emptyList(), // TODO: needs a query grouping missions by month
            byDepartment = emptyList() // TODO: no "department" field exists yet
        )
    }

    fun getAllowanceSpend(): AllowanceSpendResponse {
        val settledStatuses = listOf(MissionStatus.APPROVED, MissionStatus.SETTLED)
        val settledRows = missionRepository.sumAllowanceByBusiness(settledStatuses)
        val totalSpend = settledRows.fold(BigDecimal.ZERO) { acc, row -> acc + row.total }

        val pendingRows = missionRepository.sumAllowanceByBusiness(listOf(MissionStatus.REPORT_SUBMITTED))
        val pendingSettlement = pendingRows.fold(BigDecimal.ZERO) { acc, row -> acc + row.total }

        return AllowanceSpendResponse(
            currency = "USD", // TODO: pull from config once multi-currency support exists
            totalSpend = totalSpend,
            budget = BigDecimal.ZERO, // TODO: no budget figure exists in the data model yet
            pendingSettlement = pendingSettlement,
            changeVsLastMonthPct = 0.0, // TODO: needs a month-over-month comparison query
            monthly = emptyList() // TODO: needs a query grouping allowance totals by month
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

        val byStage = hoursByStep.map { (step, durations) ->
            StageTurnaround(stage = step, hours = durations.average())
        }

        val averageHours = if (allDurations.isNotEmpty()) allDurations.average() else 0.0
        val medianHours = if (allDurations.isNotEmpty()) median(allDurations) else 0.0
        val withinSlaPct = if (allDurations.isNotEmpty())
            allDurations.count { it <= SLA_HOURS_THRESHOLD } * 100.0 / allDurations.size
        else 0.0

        // TODO: requires comparing against last month's approval history once that query exists
        val changeVsLastMonthPct = 0.0

        return ApprovalTurnaroundResponse(
            averageHours = averageHours,
            medianHours = medianHours,
            withinSlaPct = withinSlaPct,
            changeVsLastMonthPct = changeVsLastMonthPct,
            byStage = byStage
        )
    }

    private fun median(values: List<Double>): Double {
        val sorted = values.sorted()
        val mid = sorted.size / 2
        return if (sorted.size % 2 == 0) (sorted[mid - 1] + sorted[mid]) / 2.0 else sorted[mid]
    }

    fun getExceptions(): ExceptionsResponse {
        val allMissions = missionRepository.findAll()
        val allHistory = approvalHistoryRepository.findAllByOrderByMissionIdAscDecidedAtAsc()
        val historyByMission = allHistory.groupBy { it.missionId }
        val now = Instant.now()

        val stuckItems = allMissions
            .filter { it.status !in FINAL_STATUSES }
            .mapNotNull { mission ->
                val missionHistory = historyByMission[mission.id]
                val lastActivityAt = missionHistory?.maxByOrNull { it.decidedAt }?.decidedAt
                    ?: mission.createdAt
                val daysSince = Duration.between(lastActivityAt, now).toDays()

                if (daysSince >= STUCK_THRESHOLD_DAYS) {
                    ExceptionItem(
                        id = "stuck-${mission.id}",
                        missionReference = mission.missionCode ?: "MSN-${mission.id}",
                        type = "Stuck in review",
                        severity = if (daysSince >= STUCK_THRESHOLD_DAYS * 2) "HIGH" else "MEDIUM",
                        message = "No activity for $daysSince days while in ${mission.status.name}.",
                        raisedAt = lastActivityAt
                    )
                } else null
            }

        val rejectedItems = allMissions
            .filter { it.status == MissionStatus.REJECTED }
            .map { mission ->
                val rejectionDecision = historyByMission[mission.id]
                    ?.lastOrNull { it.decision == ApprovalDecision.REJECTED }
                val rejectedAt = rejectionDecision?.decidedAt ?: mission.updatedAt

                ExceptionItem(
                    id = "rejected-${mission.id}",
                    missionReference = mission.missionCode ?: "MSN-${mission.id}",
                    type = "Rejected",
                    severity = "LOW",
                    message = "Mission was rejected during the approval chain.",
                    raisedAt = rejectedAt
                )
            }

        val items = (stuckItems + rejectedItems).sortedByDescending { it.raisedAt }

        return ExceptionsResponse(
            open = stuckItems.size.toLong(),
            changeVsLastMonthPct = 0.0, // TODO: needs a month-over-month comparison query
            items = items
        )
    }
}