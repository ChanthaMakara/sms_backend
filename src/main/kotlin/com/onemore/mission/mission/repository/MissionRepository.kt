package com.onemore.mission.mission.repository

import com.onemore.mission.mission.domain.ApprovalStep
import com.onemore.mission.mission.domain.Mission
import com.onemore.mission.mission.domain.MissionStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface MissionRepository : JpaRepository<Mission, Long> {

    fun findByRequesterId(requesterId: Long): List<Mission>

    fun findByStatus(status: MissionStatus): List<Mission>

    fun findByCurrentApprovalStep(currentApprovalStep: ApprovalStep): List<Mission>

    @Query("SELECT m.status AS status, COUNT(m) AS count FROM Mission m GROUP BY m.status")
    fun countByStatusGrouped(): List<MissionStatusCount>

    @Query(
        "SELECT m.business AS business, COALESCE(SUM(m.totalExpense), 0) AS total " +
        "FROM Mission m " +
        "WHERE m.status IN :statuses " +
        "GROUP BY m.business"
    )
    fun sumAllowanceByBusiness(statuses: List<MissionStatus>): List<AllowanceSpendByBusiness>
}

interface MissionStatusCount {
    val status: MissionStatus
    val count: Long
}

interface AllowanceSpendByBusiness {
    val business: String
    val total: java.math.BigDecimal
}