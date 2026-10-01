package com.onemore.mission.mission.controller

import com.onemore.mission.mission.dto.request.CreateMissionRequest
import com.onemore.mission.mission.dto.request.UpdateMissionParticipantsRequest
import com.onemore.mission.mission.dto.request.UpdateMissionRequest
import com.onemore.mission.mission.dto.response.MissionParticipantResponse
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
            requesterName = userDetails.fullName,
            callerRoles = userDetails.authorities.map { it.authority }
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
        @Valid @RequestBody request: UpdateMissionRequest,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<MissionResponse> {

        return ResponseEntity.ok(
            missionService.update(
                id = id,
                request = request,
                callerId = userDetails.id,
                callerRoles = userDetails.authorities.map { it.authority }
            )
        )
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: Long,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<Void> {

        missionService.delete(
            id = id,
            callerId = userDetails.id,
            callerRoles = userDetails.authorities.map { it.authority }
        )

        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{id}/participants")
    fun getParticipants(
        @PathVariable id: Long
    ): ResponseEntity<List<MissionParticipantResponse>> {

        return ResponseEntity.ok(
            missionService.getParticipants(id)
        )
    }

    @PutMapping("/{id}/participants")
    fun updateParticipants(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateMissionParticipantsRequest,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<List<MissionParticipantResponse>> {

        return ResponseEntity.ok(
            missionService.updateParticipants(
                id = id,
                request = request,
                callerId = userDetails.id,
                callerRoles = userDetails.authorities.map { it.authority }
            )
        )
    }
}
