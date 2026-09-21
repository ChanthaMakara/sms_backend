package com.onemore.mission.user.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class CreateUserRequest(

    @field:NotBlank
    val employeeCode: String,

    @field:NotBlank
    val fullName: String,

    @field:NotBlank
    @field:Email
    val email: String,

    @field:NotBlank
    @field:Size(min = 6, message = "Password must be at least 6 characters")
    val password: String,

    @field:NotBlank
    val jobLevel: String,

    val functionName: String? = null,

    val business: String? = null,

    @field:NotEmpty
    val roles: List<String>
)