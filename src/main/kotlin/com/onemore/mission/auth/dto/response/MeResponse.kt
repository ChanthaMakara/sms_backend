package com.onemore.mission.auth.dto.response

data class MeResponse(
    val id: Long,
    val email: String,
    val fullName: String,
    val jobLevel: String,
    val roles: List<String>
)