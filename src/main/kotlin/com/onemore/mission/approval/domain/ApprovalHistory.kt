package com.onemore.mission.approval.domain

import com.onemore.mission.mission.domain.ApprovalStep
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "approval_history")
class ApprovalHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "mission_id", nullable = false)
    var missionId: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "step", nullable = false, length = 30)
    var step: ApprovalStep,

    @Enumerated(EnumType.STRING)
    @Column(name = "decision", nullable = false, length = 20)
    var decision: ApprovalDecision,

    @Column(name = "comment", length = 1000)
    var comment: String? = null,

    @Column(name = "decided_by", nullable = false)
    var decidedBy: Long,

    @Column(name = "decided_at", nullable = false)
    var decidedAt: Instant = Instant.now()
)