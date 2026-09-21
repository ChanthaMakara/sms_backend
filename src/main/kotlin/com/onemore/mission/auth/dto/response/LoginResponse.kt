package com.onemore.mission.auth.dto.response

data class LoginResponse(
    val token: String,
    val type: String = "Bearer",
    val id: Long,
    val email: String,
    val fullName: String,
    val roles: List<String>
)