package com.onemore.mission.mission.controller

import com.onemore.mission.common.response.ApiResponse
import com.onemore.mission.mission.dto.request.CreateMissionRequest
import com.onemore.mission.mission.dto.request.UpdateMissionRequest
import com.onemore.mission.mission.dto.response.MissionResponse
import com.onemore.mission.mission.service.MissionService
import com.onemore.mission.security.CustomUserDetails
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/missions")
@SecurityRequirement(name = "bearerAuth")
class MissionController(
    private val missionService: MissionService
) {

    @PostMapping
    fun create(
        @Valid @RequestBody request: CreateMissionRequest,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<MissionResponse> {

        val response = missionService.create(
            request = request,
            requesterId = userDetails.id,
            requesterName = userDetails.fullName
        )

        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: Long
    ): ResponseEntity<MissionResponse> {

        return ResponseEntity.ok(
            missionService.getById(id)
        )
    }

    @GetMapping
    fun getAll(): ResponseEntity<List<MissionResponse>> {

        return ResponseEntity.ok(
            missionService.getAll()
        )
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateMissionRequest
    ): ResponseEntity<MissionResponse> {

        return ResponseEntity.ok(
            missionService.update(id, request)
        )
    }
}