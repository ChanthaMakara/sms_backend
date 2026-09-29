package com.onemore.mission.mileage.controller

import com.onemore.mission.common.response.ApiResponse
import com.onemore.mission.mileage.dto.request.UpdateMileageClaimStatusRequest
import com.onemore.mission.mileage.dto.response.MileageClaimResponse
import com.onemore.mission.mileage.service.MileageClaimService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/annexes/mileage-claims")
class AnnexMileageClaimController(
    private val mileageClaimService: MileageClaimService
) {

    @GetMapping
    fun getAllMileageClaims(): ApiResponse<List<MileageClaimResponse>> {
        return ApiResponse.success(mileageClaimService.getAllMileageClaims())
    }

    @PatchMapping("/{id}")
    fun updateStatus(
        @PathVariable id: Long,
        @RequestBody request: UpdateMileageClaimStatusRequest
    ): ApiResponse<MileageClaimResponse> {
        return ApiResponse.success(mileageClaimService.updateStatus(id, request.status))
    }
}
