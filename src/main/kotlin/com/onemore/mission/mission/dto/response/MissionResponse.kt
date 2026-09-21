package com.onemore.mission.mission.dto.response

import com.onemore.mission.mission.domain.ApprovalStep
import com.onemore.mission.mission.domain.LocationTier
import com.onemore.mission.mission.domain.MissionStatus
import com.onemore.mission.user.domain.JobLevel
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

data class MissionResponse(
    val id: Long,
    val missionCode: String?,
    val requesterId: Long,
    val requesterName: String,
    val position: String,
    val functionName: String,
    val business: String,
    val jobLevel: JobLevel,
    val basedLocation: String,
    val destinationLocation: String,
    val locationTier: LocationTier,
    val travelObjectives: String,

    val departureDate: LocalDate,
    val departureTime: LocalTime?,
    val arrivalDate: LocalDate,
    val arrivalTime: LocalTime?,
    val numberOfTravelDays: Int,

    val breakfastAmount: BigDecimal?,
    val breakfastQuantity: Int?,
    val breakfastTotal: BigDecimal?,

    val lunchAmount: BigDecimal?,
    val lunchQuantity: Int?,
    val lunchTotal: BigDecimal?,

    val dinnerAmount: BigDecimal?,
    val dinnerQuantity: Int?,
    val dinnerTotal: BigDecimal?,

    val accommodationAmountPerNight: BigDecimal?,
    val numberOfNightStay: Int?,
    val accommodationTotal: BigDecimal?,

    val totalExpense: BigDecimal?,
    val description: String?,

    val status: MissionStatus,
    val currentApprovalStep: ApprovalStep?,

    val createdAt: Instant,
    val updatedAt: Instant
)