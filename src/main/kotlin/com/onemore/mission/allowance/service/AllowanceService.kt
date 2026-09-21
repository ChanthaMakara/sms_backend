package com.onemore.mission.allowance.service

import com.onemore.mission.allowance.repository.AccommodationRateRepository
import com.onemore.mission.allowance.repository.MealRateRepository
import com.onemore.mission.mission.dto.response.MissionResponse
import com.onemore.mission.mission.mapper.MissionMapper
import com.onemore.mission.mission.repository.MissionRepository
import org.springframework.stereotype.Service

@Service
class AllowanceService(
    private val missionRepository: MissionRepository,
    private val mealRateRepository: MealRateRepository,
    private val accommodationRateRepository: AccommodationRateRepository,
    private val missionMapper: MissionMapper
) {

    fun calculateAndSave(missionId: Long): MissionResponse {
        val mission = missionRepository.findById(missionId)
            .orElseThrow {
                IllegalArgumentException("Mission not found with id: $missionId")
            }

        val mealRate = mealRateRepository.findByLocationTierAndJobLevel(
            mission.locationTier,
            mission.jobLevel
        ) ?: throw IllegalStateException(
            "No meal rate configured for locationTier=${mission.locationTier}, jobLevel=${mission.jobLevel}"
        )

        val accommodationRate = accommodationRateRepository.findByLocationTierAndJobLevel(
            mission.locationTier,
            mission.jobLevel
        ) ?: throw IllegalStateException(
            "No accommodation rate configured for locationTier=${mission.locationTier}, jobLevel=${mission.jobLevel}"
        )

        val mealQuantity = mission.numberOfTravelDays
        val nights = (mission.numberOfTravelDays - 1).coerceAtLeast(0)

        mission.breakfastAmount = mealRate.breakfastAmount
        mission.breakfastQuantity = mealQuantity
        mission.breakfastTotal = mealRate.breakfastAmount.multiply(mealQuantity.toBigDecimal())

        mission.lunchAmount = mealRate.lunchAmount
        mission.lunchQuantity = mealQuantity
        mission.lunchTotal = mealRate.lunchAmount.multiply(mealQuantity.toBigDecimal())

        mission.dinnerAmount = mealRate.dinnerAmount
        mission.dinnerQuantity = mealQuantity
        mission.dinnerTotal = mealRate.dinnerAmount.multiply(mealQuantity.toBigDecimal())

        mission.accommodationAmountPerNight = accommodationRate.amountPerNight
        mission.numberOfNightStay = nights
        mission.accommodationTotal = accommodationRate.amountPerNight.multiply(nights.toBigDecimal())

        mission.totalExpense = mission.breakfastTotal!!
            .add(mission.lunchTotal!!)
            .add(mission.dinnerTotal!!)
            .add(mission.accommodationTotal!!)

        val savedMission = missionRepository.save(mission)

        return missionMapper.toResponse(savedMission)
    }
}