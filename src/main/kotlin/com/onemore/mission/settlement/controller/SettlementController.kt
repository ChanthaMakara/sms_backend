package com.onemore.mission.settlement.controller

import com.onemore.mission.common.response.ApiResponse
import com.onemore.mission.settlement.dto.request.SettleMissionRequest
import com.onemore.mission.settlement.dto.response.SettlementResponse
import com.onemore.mission.settlement.service.SettlementService
import com.onemore.mission.security.CustomUserDetails   // adjust package if different
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/missions/{missionId}")
class SettlementController(
    private val settlementService: SettlementService
) {

    @PostMapping("/settle")
    @ResponseStatus(HttpStatus.CREATED)
    fun settleMission(
        @PathVariable missionId: Long,
        @RequestBody(required = false) request: SettleMissionRequest?,
        @AuthenticationPrincipal user: CustomUserDetails
    ): ApiResponse<SettlementResponse> {
        val settleRequest = request ?: SettleMissionRequest()
        val result = settlementService.settleMission(missionId, settleRequest, user.id)
        return ApiResponse.success(result, "Mission settled successfully")
    }

    @GetMapping("/settlement")
    fun getSettlement(
        @PathVariable missionId: Long
    ): ApiResponse<SettlementResponse> {
        val result = settlementService.getSettlementByMissionId(missionId)
        return ApiResponse.success(result)
    }
}