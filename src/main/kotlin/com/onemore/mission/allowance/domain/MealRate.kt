package com.onemore.mission.allowance.domain

import com.onemore.mission.mission.domain.LocationTier
import com.onemore.mission.user.domain.JobLevel
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(
    name = "meal_rates",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["location_tier", "job_level"])
    ]
)
class MealRate(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Enumerated(EnumType.STRING)
    @Column(name = "location_tier", nullable = false, length = 20)
    var locationTier: LocationTier,

    @Enumerated(EnumType.STRING)
    @Column(name = "job_level", nullable = false, length = 30)
    var jobLevel: JobLevel,

    @Column(name = "breakfast_amount", nullable = false)
    var breakfastAmount: BigDecimal,

    @Column(name = "lunch_amount", nullable = false)
    var lunchAmount: BigDecimal,

    @Column(name = "dinner_amount", nullable = false)
    var dinnerAmount: BigDecimal,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
)