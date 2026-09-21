package com.onemore.mission.vehicle.dto.request

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDate

data class CreateVehicleRequestRequest(

    @field:NotBlank
    val requesterName: String,

    val requesterId: String? = null,

    val position: String? = null,

    val function: String? = null,

    val business: String? = null,

    val jobLevel: String? = null,

    val basedLocation: String? = null,

    val destinationLocation: String? = null,

    @field:NotNull
    val travelStartDate: LocalDate,

    @field:NotNull
    val travelEndDate: LocalDate,

    @field:NotBlank
    val travelObjectives: String,

    @field:NotEmpty
    @field:Valid
    val travelDetails: List<VehicleTravelDetailRequest>
)

data class VehicleTravelDetailRequest(

    @field:NotNull
    val date: LocalDate,

    @field:NotBlank
    val origin: String,

    @field:NotBlank
    val destination: String,

    val purposeOfTravel: String? = null,

    @field:NotNull
    val distanceKm: BigDecimal,

    val remarks: String? = null
)