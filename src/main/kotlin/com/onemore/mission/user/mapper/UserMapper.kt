package com.onemore.mission.user.mapper

import com.onemore.mission.common.exception.BusinessException
import com.onemore.mission.user.domain.JobLevel
import com.onemore.mission.user.domain.User
import com.onemore.mission.user.domain.UserRole
import com.onemore.mission.user.dto.request.CreateUserRequest
import com.onemore.mission.user.dto.request.UpdateUserRequest
import com.onemore.mission.user.dto.response.UserResponse
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class UserMapper(
    private val passwordEncoder: PasswordEncoder
) {

    fun toEntity(request: CreateUserRequest): User {
        val jobLevel = parseJobLevel(request.jobLevel)
        val roles = request.roles.map { parseRole(it) }.toMutableSet()

        return User(
            employeeCode = request.employeeCode,
            fullName = request.fullName,
            email = request.email,
            passwordHash = passwordEncoder.encode(request.password),
            jobLevel = jobLevel,
            functionName = request.functionName,
            business = request.business,
            roles = roles
        )
    }

    fun applyUpdate(entity: User, request: UpdateUserRequest): User {
        entity.fullName = request.fullName
        entity.jobLevel = parseJobLevel(request.jobLevel)
        entity.functionName = request.functionName
        entity.business = request.business
        entity.roles = request.roles.map { parseRole(it) }.toMutableSet()
        entity.isActive = request.isActive
        entity.updatedAt = Instant.now()
        return entity
    }

    fun toResponse(entity: User): UserResponse {
        return UserResponse(
            id = entity.id,
            employeeCode = entity.employeeCode,
            fullName = entity.fullName,
            email = entity.email,
            jobLevel = entity.jobLevel.name,
            functionName = entity.functionName,
            business = entity.business,
            isActive = entity.isActive,
            roles = entity.roles.map { it.name },
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    private fun parseJobLevel(value: String): JobLevel {
        return try {
            JobLevel.valueOf(value.uppercase())
        } catch (ex: IllegalArgumentException) {
            throw BusinessException("Invalid jobLevel: $value")
        }
    }

    private fun parseRole(value: String): UserRole {
        return try {
            UserRole.valueOf(value.uppercase())
        } catch (ex: IllegalArgumentException) {
            throw BusinessException("Invalid role: $value")
        }
    }
}