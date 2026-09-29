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

    // CREATE
    @PostMapping
    fun createMileageClaim(
        @PathVariable missionId: Long,
        @Valid @RequestBody request: CreateMileageClaimRequest
    ): ResponseEntity<ApiResponse<MileageClaimResponse>> {

        val result = mileageClaimService.createMileageClaim(
            missionId,
            request
        )

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success(result))
    }

    // GET ALL
    @GetMapping
    fun getMileageClaims(
        @PathVariable missionId: Long
    ): ResponseEntity<ApiResponse<List<MileageClaimResponse>>> {

        val result = mileageClaimService.getMileageClaimsByMission(
            missionId
        )

        return ResponseEntity.ok(
            ApiResponse.success(result)
        )
    }

    // UPDATE
    @PutMapping("/{claimId}")
    fun updateMileageClaim(
        @PathVariable missionId: Long,
        @PathVariable claimId: Long,
        @Valid @RequestBody request: CreateMileageClaimRequest
    ): ResponseEntity<ApiResponse<MileageClaimResponse>> {

        val result = mileageClaimService.updateMileageClaim(
            missionId,
            claimId,
            request
        )

        return ResponseEntity.ok(
            ApiResponse.success(result)
        )
    }

    // DELETE
    @DeleteMapping("/{claimId}")
    fun deleteMileageClaim(
        @PathVariable missionId: Long,
        @PathVariable claimId: Long
    ): ResponseEntity<Void> {

        mileageClaimService.deleteMileageClaim(
            missionId,
            claimId
        )

        return ResponseEntity
            .noContent()
            .build()
    }
}
