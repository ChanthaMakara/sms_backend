package com.onemore.mission.user.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class UpdateUserRequest(

    @field:NotBlank
    val fullName: String,

    @field:NotBlank
    val jobLevel: String,

    val functionName: String? = null,

    val business: String? = null,

    @field:NotEmpty
    val roles: List<String>,

    @field:NotNull
    val isActive: Boolean
)