package com.onemore.mission.mileage.controller

import com.onemore.mission.common.response.ApiResponse
import com.onemore.mission.mileage.dto.request.CreateMileageClaimRequest
import com.onemore.mission.mileage.dto.response.MileageClaimResponse
import com.onemore.mission.mileage.service.MileageClaimService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/missions/{missionId}/mileage-claims")
class MileageClaimController(
    private val mileageClaimService: MileageClaimService
) {

    @PostMapping
    fun createMileageClaim(
        @PathVariable missionId: Long,
        @Valid @RequestBody request: CreateMileageClaimRequest
    ): ResponseEntity<ApiResponse<MileageClaimResponse>> {
        val result = mileageClaimService.createMileageClaim(missionId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result))
    }

    @GetMapping
    fun getMileageClaims(
        @PathVariable missionId: Long
    ): ResponseEntity<ApiResponse<List<MileageClaimResponse>>> {
        val result = mileageClaimService.getMileageClaimsByMission(missionId)
        return ResponseEntity.ok(ApiResponse.success(result))
    }
}