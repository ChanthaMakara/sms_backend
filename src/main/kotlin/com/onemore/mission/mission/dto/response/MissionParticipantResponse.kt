package com.onemore.mission.mission.dto.response

import com.onemore.mission.mission.domain.MissionParticipant
import com.onemore.mission.user.domain.JobLevel
import java.math.BigDecimal

data class MissionParticipantResponse(
    val id: Long,
    val employeeId: Long,
    val employeeCode: String?,
    val fullName: String,
    val jobLevel: JobLevel,
    val functionName: String?,
    val business: String?,
    val requester: Boolean,
    val breakfastTotal: BigDecimal,
    val lunchTotal: BigDecimal,
    val dinnerTotal: BigDecimal,
    val accommodationTotal: BigDecimal,
    val totalExpense: BigDecimal
) {
    companion object {
        fun from(p: MissionParticipant) = MissionParticipantResponse(
            id = p.id,
            employeeId = p.employeeId,
            employeeCode = p.employeeCode,
            fullName = p.fullName,
            jobLevel = p.jobLevel,
            functionName = p.functionName,
            business = p.business,
            requester = p.requester,
            breakfastTotal = p.breakfastTotal,
            lunchTotal = p.lunchTotal,
            dinnerTotal = p.dinnerTotal,
            accommodationTotal = p.accommodationTotal,
            totalExpense = p.totalExpense
        )
    }
}
