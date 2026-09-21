package com.onemore.mission.approval.repository

import com.onemore.mission.approval.domain.ApprovalHistory
import org.springframework.data.jpa.repository.JpaRepository

interface ApprovalHistoryRepository : JpaRepository<ApprovalHistory, Long> {

    fun findByMissionId(missionId: Long): List<ApprovalHistory>

    fun findByMissionIdOrderByDecidedAtAsc(missionId: Long): List<ApprovalHistory>

    fun findAllByOrderByMissionIdAscDecidedAtAsc(): List<ApprovalHistory>
}