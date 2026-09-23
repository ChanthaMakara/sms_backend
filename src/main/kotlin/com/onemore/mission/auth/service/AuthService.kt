package com.onemore.mission.auth.service

import com.onemore.mission.auth.dto.request.LoginRequest
import com.onemore.mission.auth.dto.response.LoginResponse
import com.onemore.mission.security.CustomUserDetails
import com.onemore.mission.security.JwtService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService
) {

    fun login(request: LoginRequest): LoginResponse {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.email, request.password)
        )

        val userDetails = authentication.principal as CustomUserDetails

        // Extra claims we want inside the JWT
        val extraClaims = mapOf(
            "userId" to userDetails.id,
            "fullName" to userDetails.fullName,
            "jobLevel" to userDetails.jobLevel.name,   // or .toString() if it's an enum
            "roles" to userDetails.authorities.map { it.authority }
        )

        val token = jwtService.generateToken(extraClaims, userDetails)

        return LoginResponse(
            token = token,
            id = userDetails.id,
            email = userDetails.username,
            fullName = userDetails.fullName,
            roles = userDetails.authorities.map { it.authority }
        )
    }
}
