package com.onemore.mission.mission.domain

import com.onemore.mission.user.domain.JobLevel
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

@Entity
@Table(name = "missions")
class Mission(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "mission_code", unique = true, length = 50)
    var missionCode: String? = null,

    @Column(name = "requester_id", nullable = false)
    var requesterId: Long,

    @Column(name = "requester_name", nullable = false, length = 150)
    var requesterName: String,

    @Column(nullable = false, length = 100)
    var position: String,

    @Column(name = "function_name", nullable = false, length = 100)
    var functionName: String,

    @Column(nullable = false, length = 100)
    var business: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "job_level", nullable = false, length = 50)
    var jobLevel: JobLevel,

    @Column(name = "based_location", nullable = false, length = 150)
    var basedLocation: String,

    @Column(name = "destination_location", nullable = false, length = 150)
    var destinationLocation: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "location_tier", nullable = false, length = 20)
    var locationTier: LocationTier,

    @Column(name = "travel_objectives", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    var travelObjectives: String,

    @Column(name = "departure_date", nullable = false)
    var departureDate: LocalDate,

    @Column(name = "departure_time")
    var departureTime: LocalTime? = null,

    @Column(name = "arrival_date", nullable = false)
    var arrivalDate: LocalDate,

    @Column(name = "arrival_time")
    var arrivalTime: LocalTime? = null,

    @Column(name = "number_of_travel_days", nullable = false)
    var numberOfTravelDays: Int,

    @Column(name = "breakfast_amount", precision = 18, scale = 2)
    var breakfastAmount: BigDecimal? = null,

    @Column(name = "breakfast_quantity")
    var breakfastQuantity: Int? = null,

    @Column(name = "breakfast_total", precision = 18, scale = 2)
    var breakfastTotal: BigDecimal? = null,

    @Column(name = "lunch_amount", precision = 18, scale = 2)
    var lunchAmount: BigDecimal? = null,

    @Column(name = "lunch_quantity")
    var lunchQuantity: Int? = null,

    @Column(name = "lunch_total", precision = 18, scale = 2)
    var lunchTotal: BigDecimal? = null,

    @Column(name = "dinner_amount", precision = 18, scale = 2)
    var dinnerAmount: BigDecimal? = null,

    @Column(name = "dinner_quantity")
    var dinnerQuantity: Int? = null,

    @Column(name = "dinner_total", precision = 18, scale = 2)
    var dinnerTotal: BigDecimal? = null,

    @Column(name = "accommodation_amount_per_night", precision = 18, scale = 2)
    var accommodationAmountPerNight: BigDecimal? = null,

    @Column(name = "number_of_night_stay")
    var numberOfNightStay: Int? = null,

    @Column(name = "accommodation_total", precision = 18, scale = 2)
    var accommodationTotal: BigDecimal? = null,

    @Column(name = "total_expense", precision = 18, scale = 2)
    var totalExpense: BigDecimal? = null,

    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
    var description: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    var status: MissionStatus = MissionStatus.DRAFT,

    @Enumerated(EnumType.STRING)
    @Column(name = "current_approval_step", length = 30)
    var currentApprovalStep: ApprovalStep? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),

    @Column(name = "created_by")
    var createdBy: Long? = null,

    @Column(name = "updated_by")
    var updatedBy: Long? = null
)