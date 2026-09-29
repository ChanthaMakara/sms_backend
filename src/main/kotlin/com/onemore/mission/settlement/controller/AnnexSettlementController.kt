package com.onemore.mission.settlement.controller

import com.onemore.mission.common.response.ApiResponse
import com.onemore.mission.settlement.dto.request.UpdateSettlementStatusRequest
import com.onemore.mission.settlement.dto.response.SettlementResponse
import com.onemore.mission.settlement.service.SettlementService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/settlements")
class AnnexSettlementController(
    private val settlementService: SettlementService
) {

    @GetMapping
    fun getAllSettlements(): ApiResponse<List<SettlementResponse>> {
        return ApiResponse.success(settlementService.getAllSettlements())
    }

    @PatchMapping("/{id}")
    fun updateStatus(
        @PathVariable id: Long,
        @RequestBody request: UpdateSettlementStatusRequest
    ): ApiResponse<SettlementResponse> {
        return ApiResponse.success(settlementService.updateStatus(id, request.status))
    }
}
