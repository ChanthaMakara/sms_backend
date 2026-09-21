package com.onemore.mission.mileage.dto.response

import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

data class MileageClaimResponse(

    val id: Long,
    val missionId: Long,
    val vehicleRequestId: Long?,

    val requesterName: String,
    val requesterId: String?,
    val position: String?,
    val function: String?,
    val business: String?,
    val jobLevel: String?,

    val basedLocation: String?,
    val destinationLocation: String?,
    val travelStartDate: LocalDate,
    val travelEndDate: LocalDate,
    val travelObjectives: String,

    val travelDetails: List<MileageTravelDetailResponse>,

    val totalDistanceKm: BigDecimal,
    val totalClaimAmount: BigDecimal,

    val status: String,

    val createdAt: Instant,
    val updatedAt: Instant
)

data class MileageTravelDetailResponse(

    val id: Long,
    val date: LocalDate,
    val origin: String,
    val destination: String,
    val purposeOfTravel: String?,
    val distanceKm: BigDecimal,
    val remarks: String?
)