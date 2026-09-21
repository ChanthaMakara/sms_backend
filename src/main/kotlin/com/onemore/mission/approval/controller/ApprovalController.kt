package com.onemore.mission.approval.controller

import com.onemore.mission.approval.dto.response.PendingApprovalResponse
import com.onemore.mission.approval.dto.request.ApprovalDecisionRequest
import com.onemore.mission.approval.dto.response.ApprovalHistoryResponse
import com.onemore.mission.approval.service.ApprovalService
import com.onemore.mission.mission.dto.response.MissionResponse
import com.onemore.mission.security.CustomUserDetails
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/missions")
@SecurityRequirement(name = "bearerAuth")
class ApprovalController(
    private val approvalService: ApprovalService
) {

    @PostMapping("/{id}/submit")
    fun submit(
        @PathVariable id: Long,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<MissionResponse> {

        return ResponseEntity.ok(
            approvalService.submit(id, userDetails)
        )
    }

    @PostMapping("/{id}/approve")
    fun approve(
        @PathVariable id: Long,
        @Valid @RequestBody(required = false) request: ApprovalDecisionRequest?,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<MissionResponse> {

        return ResponseEntity.ok(
            approvalService.approve(id, userDetails, request?.comment)
        )
    }

    @PostMapping("/{id}/reject")
    fun reject(
        @PathVariable id: Long,
        @Valid @RequestBody(required = false) request: ApprovalDecisionRequest?,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<MissionResponse> {

        return ResponseEntity.ok(
            approvalService.reject(id, userDetails, request?.comment)
        )
    }

    @PostMapping("/{id}/cancel")
    fun cancel(
        @PathVariable id: Long,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<MissionResponse> {

        return ResponseEntity.ok(
            approvalService.cancel(id, userDetails)
        )
    }

    @GetMapping("/{id}/approvals")
    fun getApprovalHistory(
        @PathVariable id: Long
    ): ResponseEntity<List<ApprovalHistoryResponse>> {

        return ResponseEntity.ok(
            approvalService.getApprovalHistory(id)
        )
    }

    @GetMapping("/approvals/pending")
    fun getPendingApprovals(
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<List<PendingApprovalResponse>> {

        return ResponseEntity.ok(
            approvalService.getPendingApprovals(userDetails)
        )
    }
}