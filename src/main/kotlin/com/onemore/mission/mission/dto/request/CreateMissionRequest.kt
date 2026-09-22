package com.onemore.mission.mission.dto.request

import com.onemore.mission.mission.domain.LocationTier
import com.onemore.mission.user.domain.JobLevel
import jakarta.validation.constraints.*
import java.time.LocalDate
import java.time.LocalTime

data class CreateMissionRequest(
    @field:NotBlank
    val position: String,

    @field:NotBlank
    val functionName: String,

    @field:NotBlank
    val business: String,

    @field:NotNull
    val jobLevel: JobLevel,

    @field:NotBlank
    val basedLocation: String,

    @field:NotBlank
    val destinationLocation: String,

    @field:NotNull
    val locationTier: LocationTier,

    @field:NotBlank
    val travelObjectives: String,

    @field:NotNull
    val departureDate: LocalDate,

    val departureTime: LocalTime? = null,

    @field:NotNull
    val arrivalDate: LocalDate,

    val arrivalTime: LocalTime? = null,

    @field:Min(1)
    val numberOfTravelDays: Int,

    val description: String? = null,

    val onBehalfOfUserId: Long? = null
)
