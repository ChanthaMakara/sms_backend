package com.onemore.mission.settlement.domain

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "settlements")
class Settlement(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "mission_id", nullable = false, unique = true)
    var missionId: Long,

    @Column(name = "total_allowance", nullable = false, precision = 18, scale = 2)
    var totalAllowance: BigDecimal,

    @Column(name = "total_mileage_claim", nullable = false, precision = 18, scale = 2)
    var totalMileageClaim: BigDecimal,

    @Column(name = "grand_total", nullable = false, precision = 18, scale = 2)
    var grandTotal: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    var status: SettlementStatus = SettlementStatus.DRAFT,

    @Column(name = "settled_at")
    var settledAt: Instant? = null,

    @Column(name = "settled_by")
    var settledBy: Long? = null,

    @Column(columnDefinition = "NVARCHAR(MAX)")
    var notes: String? = null,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
)