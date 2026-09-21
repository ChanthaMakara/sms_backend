package com.onemore.mission.auth.controller

import com.onemore.mission.auth.dto.request.LoginRequest
import com.onemore.mission.auth.dto.response.LoginResponse
import com.onemore.mission.auth.dto.response.LogoutResponse
import com.onemore.mission.auth.dto.response.MeResponse
import com.onemore.mission.auth.service.AuthService
import com.onemore.mission.security.CustomUserDetails
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequest
    ): ResponseEntity<LoginResponse> {
        val response = authService.login(request)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    fun me(
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<MeResponse> {

        val response = MeResponse(
            id = userDetails.id,
            email = userDetails.username,
            fullName = userDetails.fullName,
            jobLevel = userDetails.jobLevel.name,
            roles = userDetails.authorities.map { it.authority }
        )

        return ResponseEntity.ok(response)
    }

    @PostMapping("/logout")
    @SecurityRequirement(name = "bearerAuth")
    fun logout(): ResponseEntity<LogoutResponse> {
        return ResponseEntity.ok(
            LogoutResponse(
                message = "Logout successful. Please remove the JWT token from the client."
            )
        )
    }
}
