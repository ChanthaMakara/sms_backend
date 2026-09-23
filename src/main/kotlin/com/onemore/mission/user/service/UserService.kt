package com.onemore.mission.user.service

import com.onemore.mission.common.exception.BusinessException
import com.onemore.mission.common.exception.ResourceNotFoundException
import com.onemore.mission.common.response.PageResponse
import com.onemore.mission.user.dto.request.CreateUserRequest
import com.onemore.mission.user.dto.request.UpdateUserRequest
import com.onemore.mission.user.dto.response.UserResponse
import com.onemore.mission.user.mapper.UserMapper
import com.onemore.mission.user.repository.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userMapper: UserMapper
) {

    @Transactional
    fun createUser(request: CreateUserRequest): UserResponse {
        if (userRepository.existsByEmail(request.email)) {
            throw BusinessException("Email already in use: ${request.email}")
        }
        if (userRepository.existsByEmployeeCode(request.employeeCode)) {
            throw BusinessException("Employee code already in use: ${request.employeeCode}")
        }

        val entity = userMapper.toEntity(request)
        val saved = userRepository.save(entity)
        return userMapper.toResponse(saved)
    }

    @Transactional(readOnly = true)
    fun getAllUsers(page: Int, pageSize: Int, search: String?): PageResponse<UserResponse> {
        val safePage = if (page < 1) 1 else page
        val safeSize = if (pageSize < 1) 10 else pageSize.coerceAtMost(100)
        val pageable = PageRequest.of(safePage - 1, safeSize, Sort.by(Sort.Direction.DESC, "createdAt"))

        val normalizedSearch = search?.trim()?.ifBlank { null }
        val result = userRepository.search(normalizedSearch, pageable)
            .map { userMapper.toResponse(it) }

        return PageResponse.from(result, safePage)
    }

    @Transactional(readOnly = true)
    fun getUserById(id: Long): UserResponse {
        val entity = userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("User not found: $id") }
        return userMapper.toResponse(entity)
    }

    @Transactional
    fun updateUser(id: Long, request: UpdateUserRequest): UserResponse {
        val entity = userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("User not found: $id") }

        val updated = userMapper.applyUpdate(entity, request)
        val saved = userRepository.save(updated)
        return userMapper.toResponse(saved)
    }

    @Transactional
    fun deactivateUser(id: Long): UserResponse {
        val entity = userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("User not found: $id") }

        entity.isActive = false
        entity.updatedAt = Instant.now()

        val saved = userRepository.save(entity)
        return userMapper.toResponse(saved)
    }
}
