package com.onemore.mission.mission.dto.request

data class UpdateMissionParticipantsRequest(
    val participantIds: List<Long> = emptyList()
)
