package com.onemore.mission.mission.mapper

import com.onemore.mission.mission.domain.Mission
import com.onemore.mission.mission.dto.response.MissionResponse
import org.springframework.stereotype.Component

@Component
class MissionMapper {

    fun toResponse(mission: Mission): MissionResponse {
        return MissionResponse(
            id = mission.id,
            missionCode = mission.missionCode,
            requesterId = mission.requesterId,
            requesterName = mission.requesterName,
            position = mission.position,
            functionName = mission.functionName,
            business = mission.business,
            jobLevel = mission.jobLevel,
            basedLocation = mission.basedLocation,
            destinationLocation = mission.destinationLocation,
            locationTier = mission.locationTier,
            travelObjectives = mission.travelObjectives,

            departureDate = mission.departureDate,
            departureTime = mission.departureTime,
            arrivalDate = mission.arrivalDate,
            arrivalTime = mission.arrivalTime,
            numberOfTravelDays = mission.numberOfTravelDays,

            breakfastAmount = mission.breakfastAmount,
            breakfastQuantity = mission.breakfastQuantity,
            breakfastTotal = mission.breakfastTotal,

            lunchAmount = mission.lunchAmount,
            lunchQuantity = mission.lunchQuantity,
            lunchTotal = mission.lunchTotal,

            dinnerAmount = mission.dinnerAmount,
            dinnerQuantity = mission.dinnerQuantity,
            dinnerTotal = mission.dinnerTotal,

            accommodationAmountPerNight = mission.accommodationAmountPerNight,
            numberOfNightStay = mission.numberOfNightStay,
            accommodationTotal = mission.accommodationTotal,

            totalExpense = mission.totalExpense,
            description = mission.description,

            status = mission.status,
            currentApprovalStep = mission.currentApprovalStep,

            createdAt = mission.createdAt,
            updatedAt = mission.updatedAt
        )
    }
}