package com.onemore.mission.user.dto.response

import java.time.Instant

data class UserResponse(
    val id: Long,
    val employeeCode: String,
    val fullName: String,
    val email: String,
    val jobLevel: String,
    val functionName: String?,
    val business: String?,
    val isActive: Boolean,
    val roles: List<String>,
    val createdAt: Instant,
    val updatedAt: Instant
)