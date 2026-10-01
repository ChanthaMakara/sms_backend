package com.onemore.mission.mission.domain

import com.onemore.mission.user.domain.JobLevel
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(
    name = "mission_participants",
    uniqueConstraints = [UniqueConstraint(columnNames = ["mission_id", "employee_id"])]
)
class MissionParticipant(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "mission_id", nullable = false)
    var missionId: Long,

    @Column(name = "employee_id", nullable = false)
    var employeeId: Long,

    @Column(name = "employee_code", length = 50)
    var employeeCode: String? = null,

    @Column(name = "full_name", nullable = false, length = 150)
    var fullName: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "job_level", nullable = false, length = 50)
    var jobLevel: JobLevel,

    @Column(name = "function_name", length = 100)
    var functionName: String? = null,

    @Column(length = 100)
    var business: String? = null,

    @Column(name = "is_requester", nullable = false)
    var requester: Boolean = false,

    @Column(name = "breakfast_total", nullable = false, precision = 18, scale = 2)
    var breakfastTotal: BigDecimal = BigDecimal.ZERO,

    @Column(name = "lunch_total", nullable = false, precision = 18, scale = 2)
    var lunchTotal: BigDecimal = BigDecimal.ZERO,

    @Column(name = "dinner_total", nullable = false, precision = 18, scale = 2)
    var dinnerTotal: BigDecimal = BigDecimal.ZERO,

    @Column(name = "accommodation_total", nullable = false, precision = 18, scale = 2)
    var accommodationTotal: BigDecimal = BigDecimal.ZERO,

    @Column(name = "total_expense", nullable = false, precision = 18, scale = 2)
    var totalExpense: BigDecimal = BigDecimal.ZERO
)
