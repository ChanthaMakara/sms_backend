package com.onemore.mission.allowance.controller

import com.onemore.mission.allowance.service.AllowanceService
import com.onemore.mission.mission.dto.response.MissionResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/missions")
@SecurityRequirement(name = "bearerAuth")
class AllowanceController(
    private val allowanceService: AllowanceService
) {

    @GetMapping("/{id}/allowances")
    fun getAllowances(
        @PathVariable id: Long
    ): ResponseEntity<MissionResponse> {

        return ResponseEntity.ok(
            allowanceService.calculateAndSave(id)
        )
    }
}