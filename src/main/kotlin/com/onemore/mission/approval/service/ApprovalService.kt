package com.onemore.mission.approval.service

import com.onemore.mission.approval.dto.response.PendingApprovalResponse
import com.onemore.mission.approval.domain.ApprovalDecision
import com.onemore.mission.approval.domain.ApprovalHistory
import com.onemore.mission.approval.dto.response.ApprovalHistoryResponse
import com.onemore.mission.approval.repository.ApprovalHistoryRepository
import com.onemore.mission.mission.domain.ApprovalStep
import com.onemore.mission.mission.domain.MissionStatus
import com.onemore.mission.mission.dto.response.MissionResponse
import com.onemore.mission.mission.mapper.MissionMapper
import com.onemore.mission.mission.repository.MissionRepository
import com.onemore.mission.security.CustomUserDetails
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service

@Service
class ApprovalService(
    private val missionRepository: MissionRepository,
    private val approvalHistoryRepository: ApprovalHistoryRepository,
    private val missionMapper: MissionMapper
) {

    // Which role is required to approve/reject at each step
    private val stepRoleMap = mapOf(
        ApprovalStep.FUNCTION_MANAGER to "ROLE_FUNCTION_MANAGER",
        ApprovalStep.HRBP to "ROLE_HRBP",
        ApprovalStep.FINANCE to "ROLE_FINANCE",
        ApprovalStep.BIZOPS to "ROLE_BIZOPS",
        ApprovalStep.EXECUTIVE to "ROLE_EXECUTIVE"
    )

    // Order of steps, FM -> HRBP -> FINANCE -> BIZOPS (Executive not wired in yet)
    private val stepOrder = listOf(
        ApprovalStep.FUNCTION_MANAGER,
        ApprovalStep.HRBP,
        ApprovalStep.FINANCE,
        ApprovalStep.BIZOPS
    )

    private val stepToStatus = mapOf(
        ApprovalStep.FUNCTION_MANAGER to MissionStatus.FM_REVIEW,
        ApprovalStep.HRBP to MissionStatus.HRBP_REVIEW,
        ApprovalStep.FINANCE to MissionStatus.FINANCE_REVIEW,
        ApprovalStep.BIZOPS to MissionStatus.BIZOPS_REVIEW
    )

    fun submit(missionId: Long, userDetails: CustomUserDetails): MissionResponse {
        val mission = getMissionOrThrow(missionId)

        if (mission.requesterId != userDetails.id) {
            throw IllegalStateException("Only the requester can submit this mission")
        }

        if (mission.status != MissionStatus.DRAFT) {
            throw IllegalStateException("Only DRAFT missions can be submitted, current status: ${mission.status}")
        }

        val firstStep = stepOrder.first()
        mission.status = stepToStatus[firstStep]!!
        mission.currentApprovalStep = firstStep

        val saved = missionRepository.save(mission)
        return missionMapper.toResponse(saved)
    }

    fun approve(missionId: Long, userDetails: CustomUserDetails, comment: String?): MissionResponse {
        val mission = getMissionOrThrow(missionId)
        val currentStep = mission.currentApprovalStep
            ?: throw IllegalStateException("Mission is not currently in an approval step")

        requireRoleForStep(currentStep, userDetails)

        approvalHistoryRepository.save(
            ApprovalHistory(
                missionId = mission.id,
                step = currentStep,
                decision = ApprovalDecision.APPROVED,
                comment = comment,
                decidedBy = userDetails.id
            )
        )

        val currentIndex = stepOrder.indexOf(currentStep)
        val isLastStep = currentIndex == stepOrder.lastIndex

        if (isLastStep) {
            mission.status = MissionStatus.APPROVED
            mission.currentApprovalStep = null
        } else {
            val nextStep = stepOrder[currentIndex + 1]
            mission.status = stepToStatus[nextStep]!!
            mission.currentApprovalStep = nextStep
        }

        val saved = missionRepository.save(mission)
        return missionMapper.toResponse(saved)
    }

    fun reject(missionId: Long, userDetails: CustomUserDetails, comment: String?): MissionResponse {
        val mission = getMissionOrThrow(missionId)
        val currentStep = mission.currentApprovalStep
            ?: throw IllegalStateException("Mission is not currently in an approval step")

        requireRoleForStep(currentStep, userDetails)

        approvalHistoryRepository.save(
            ApprovalHistory(
                missionId = mission.id,
                step = currentStep,
                decision = ApprovalDecision.REJECTED,
                comment = comment,
                decidedBy = userDetails.id
            )
        )

        mission.status = MissionStatus.DRAFT
        mission.currentApprovalStep = null

        val saved = missionRepository.save(mission)
        return missionMapper.toResponse(saved)
    }

    fun cancel(missionId: Long, userDetails: CustomUserDetails): MissionResponse {
        val mission = getMissionOrThrow(missionId)

        if (mission.requesterId != userDetails.id) {
            throw IllegalStateException("Only the requester can cancel this mission")
        }

        val nonCancellableStatuses = setOf(
            MissionStatus.APPROVED,
            MissionStatus.REJECTED,
            MissionStatus.CANCELLED,
            MissionStatus.SETTLED
        )

        if (mission.status in nonCancellableStatuses) {
            throw IllegalStateException("Cannot cancel a mission with status: ${mission.status}")
        }

        mission.status = MissionStatus.CANCELLED
        mission.currentApprovalStep = null

        val saved = missionRepository.save(mission)
        return missionMapper.toResponse(saved)
    }

    fun getApprovalHistory(missionId: Long): List<ApprovalHistoryResponse> {
        // Make sure the mission exists
        getMissionOrThrow(missionId)

        return approvalHistoryRepository.findByMissionIdOrderByDecidedAtAsc(missionId)
            .map { history ->
                ApprovalHistoryResponse(
                    id = history.id!!,
                    missionId = history.missionId,
                    step = history.step,
                    decision = history.decision,
                    comment = history.comment,
                    decidedBy = history.decidedBy,
                    decidedAt = history.decidedAt
                )
            }
    }

        fun getPendingApprovals(userDetails: CustomUserDetails): List<PendingApprovalResponse> {
        // Find which step(s) this user's roles qualify them to approve
        val matchingSteps = stepOrder.filter { step ->
            val requiredRole = stepRoleMap[step]
            requiredRole != null && userDetails.authorities.contains(SimpleGrantedAuthority(requiredRole))
        }

        return matchingSteps
            .flatMap { step -> missionRepository.findByCurrentApprovalStep(step) }
            .map { mission ->
                PendingApprovalResponse(
                    missionId = mission.id,
                    missionCode = mission.missionCode,
                    requesterName = mission.requesterName,
                    destinationLocation = mission.destinationLocation,
                    departureDate = mission.departureDate,
                    arrivalDate = mission.arrivalDate,
                    status = mission.status,
                    currentApprovalStep = mission.currentApprovalStep
                )
            }
    }

    private fun requireRoleForStep(step: ApprovalStep, userDetails: CustomUserDetails) {
        val requiredRole = stepRoleMap[step]
            ?: throw IllegalStateException("No role configured for step: $step")

        val hasRole = userDetails.authorities.contains(SimpleGrantedAuthority(requiredRole))

        if (!hasRole) {
            throw IllegalStateException("User does not have the required role ($requiredRole) for step: $step")
        }
    }

    private fun getMissionOrThrow(missionId: Long) =
        missionRepository.findById(missionId)
            .orElseThrow { IllegalArgumentException("Mission not found with id: $missionId") }
}