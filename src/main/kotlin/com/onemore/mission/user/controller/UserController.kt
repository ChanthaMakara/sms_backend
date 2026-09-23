package com.onemore.mission.user.controller

import com.onemore.mission.common.response.ApiResponse
import com.onemore.mission.common.response.PageResponse
import com.onemore.mission.user.dto.request.CreateUserRequest
import com.onemore.mission.user.dto.request.UpdateUserRequest
import com.onemore.mission.user.dto.response.UserResponse
import com.onemore.mission.user.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
class UserController(
    private val userService: UserService
) {

    @PostMapping
    fun createUser(@Valid @RequestBody request: CreateUserRequest): ResponseEntity<ApiResponse<UserResponse>> {
        val result = userService.createUser(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result))
    }

    @GetMapping
    fun getAllUsers(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
        @RequestParam(required = false) search: String?
    ): ResponseEntity<ApiResponse<PageResponse<UserResponse>>> {
        val result = userService.getAllUsers(page, pageSize, search)
        return ResponseEntity.ok(ApiResponse.success(result))
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<ApiResponse<UserResponse>> {
        val result = userService.getUserById(id)
        return ResponseEntity.ok(ApiResponse.success(result))
    }

    @PutMapping("/{id}")
    fun updateUser(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateUserRequest
    ): ResponseEntity<ApiResponse<UserResponse>> {
        val result = userService.updateUser(id, request)
        return ResponseEntity.ok(ApiResponse.success(result))
    }

    @DeleteMapping("/{id}")
    fun deactivateUser(@PathVariable id: Long): ResponseEntity<ApiResponse<UserResponse>> {
        val result = userService.deactivateUser(id)
        return ResponseEntity.ok(ApiResponse.success(result))
    }
}
